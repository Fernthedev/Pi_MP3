package com.github.fernthedev.pi_mp3.core;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.fernthedev.config.common.exceptions.ConfigLoadException;
import com.github.fernthedev.config.gson.GsonConfig;
import com.github.fernthedev.lightchat.core.ColorCode;
import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.lightchat.core.api.plugin.PluginManager;
import com.github.fernthedev.lightchat.server.Server;
import com.github.fernthedev.lightchat.server.terminal.ServerTerminal;
import com.github.fernthedev.lightchat.server.terminal.ServerTerminalSettings;
import com.github.fernthedev.modules.Module;
import com.github.fernthedev.modules.ModuleHandler;
import com.github.fernthedev.modules.ModuleLoadingHandler;
import com.github.fernthedev.pi_mp3.api.ICore;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.events.ModuleLoadedEvent;
import com.github.fernthedev.pi_mp3.api.events.ModulesInitializedEvent;
import com.github.fernthedev.pi_mp3.api.songs.SongManager;
import com.github.fernthedev.pi_mp3.api.ui.UIInterface;
import com.github.fernthedev.pi_mp3.core.command.MusicCommand;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

import java.io.File;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.libc.LibCStdlib.free;

/**
 * Contains and manages the core of the application.
 */
public class MP3Server extends ServerTerminal implements ICore, ModuleHandler {

    @Getter
    private static Injector instanceInjector;

    private static MP3Server instance;

    @Getter
    private final ModuleLoadingHandler moduleHandler;
    private volatile boolean started;

    @Getter
    private LibGDXHackApp audioHandler;

    private SongManagerImpl songManager;

    private final List<UIInterface> uiInterfaces = new ArrayList<>();

    public static void main(String[] args) {
        start(args);
    }


    public static void start(String[] args, Module... modules) {
        MP3Server mp3Server = new MP3Server();
        mp3Server.init(args, modules);

        mp3Server.initAudio();
        mp3Server.started = true;
    }

    public static void testModules(String[] args, Module... modules) {
        MP3Server mp3Server = new MP3Server();
        mp3Server.init(args, modules);

        mp3Server.started = true;
    }



    private MP3Server() {
        instance = this;
        moduleHandler = new ModuleLoadingHandler(this);
    }

    private void init(String[] args, Module[] modules) {
        started = false;
        try {
            ServerTerminal.init(args,
                    ServerTerminalSettings.builder()
                            .allowChangePassword(false)
                            .allowTermPackets(false)
                            .serverSettings(new GsonConfig<>(new MP3ServerSettings(), new File("config.json")))
                            .launchConsoleInCMDWhenNone(false)
                            .build()
            );
        } catch (ConfigLoadException e) {
            e.printStackTrace();
        }

        server.addShutdownListener(() -> getExecutorService().shutdownNow());

        new Thread(server).start();
//        server.start();

        server.getStartupLock().join();

        MP3Pi.setCore(this);
        instanceInjector = Guice.createInjector(new ServerGuiceModule());
        MP3Pi.setInjector(instanceInjector);



        if (modules.length > 0) {
            for (Module module : modules) {
                moduleHandler.registerModule(module);
            }
        }

        File moduleFolder = new File("./modules");

        if (!moduleFolder.exists())
            moduleFolder.mkdir();

        moduleHandler.scanDirectory(moduleFolder, getClass().getClassLoader());

        moduleHandler.initializeModules().awaitFinish(1);
        MP3Pi.getInstance().getPluginManager().callEvent(new ModulesInitializedEvent());
    }

    private void initAudio() {

//        ALC.create();
        server.addShutdownListener(ALC::destroy);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        audioHandler = new LibGDXHackApp(config, "PiMP3");

//        Audio audio = Gdx.audio;

//        ALC.create();
        songManager = new SongManagerImpl(audioHandler, this);
        if (!MP3Pi.isTestMode()) {
            Thread t = new Thread(songManager);
            t.setDaemon(true);
            t.start();
        }


//        Song musicTest = new Song(Gdx.files.local("sound.ogg"));

        if (StaticHandler.isDebug())
            songManager.play(Constants.getDebugSong());


//        musicTest.setLooping(true);

        registerCommand(new MusicCommand());
        StaticHandler.getCore().getLogger().info(ColorCode.GREEN + "Initialized audio");
    }



    private static void openAlAudioTest() {

        //Initialization
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        long   device            = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        long  context    = alcCreateContext(device, attributes);
        alcMakeContextCurrent(context);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        ALCapabilities alCapabilities  = AL.createCapabilities(alcCapabilities);



        if (StaticHandler.isDebug()) {


            ShortBuffer rawAudioBuffer;

            int channels;
            int sampleRate;

            try (MemoryStack stack = stackPush()) {
                //Allocate space to store return information from the function
                IntBuffer channelsBuffer   = stack.mallocInt(1);
                IntBuffer sampleRateBuffer = stack.mallocInt(1);

//                try {
//                    Resources.getResource("sound.ogg");
//                } catch (IllegalArgumentException e) {
//                    throw new RuntimeException(new FileNotFoundException("Cannot find sound.ogg"));
//                }

                StaticHandler.getCore().getLogger().debug(new File(".").getAbsolutePath() + " is path");

                rawAudioBuffer = stb_vorbis_decode_filename("sound.ogg", channelsBuffer, sampleRateBuffer);

                //Retreive the extra information that was stored in the buffers by the function
                channels = channelsBuffer.get(0);
                sampleRate = sampleRateBuffer.get(0);
            }

//Find the correct OpenAL format
            int format = -1;
            if (channels == 1) {
                format = AL_FORMAT_MONO16;
            } else if (channels == 2) {
                format = AL_FORMAT_STEREO16;
            }

//Request space for the buffer
            int bufferPointer = alGenBuffers();

//Send the data to OpenAL

            if (rawAudioBuffer == null) throw new NullPointerException("Audio is null for some reason");

            alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);

//Free the memory allocated by STB
            free(rawAudioBuffer);

//Request a source
            int sourcePointer = alGenSources();

//Assign the sound we just loaded to the source
            alSourcei(sourcePointer, AL_BUFFER, bufferPointer);

//Play the sound
            alSourcePlay(sourcePointer);

            try {


                int bits = alGetBufferi(bufferPointer, AL_BITS);
                int sizeInBytes = alGetBufferi(bufferPointer, AL_SIZE);


                int lengthInSamples = sizeInBytes * 8 / (channels * bits);
                float samplesInMillis = (lengthInSamples / (float) sampleRate) * 1000;

                StaticHandler.getCore().getLogger().debug("Duration of sound: " + samplesInMillis);

                //Wait for a second
                Thread.sleep((long) samplesInMillis);
            } catch (InterruptedException ignored) {
            }



            //Terminate OpenAL
            alDeleteSources(sourcePointer);
            alDeleteBuffers(bufferPointer);
            alcDestroyContext(context);
            alcCloseDevice(device);
        }

        server.addShutdownListener(ALC::destroy);
    }






    public static Server getServer() {
        return server;
    }


    /**
     * @return The actual core implementing the code.
     */
    @Override
    public ICore getCore() {
        return this;
    }

    @Override
    public PluginManager getPluginManager() {
        return MP3Server.getServer().getPluginManager();
    }

    @Override
    public ExecutorService getExecutorService() {
        return server.getExecutorService();
    }

    @Override
    public Injector getInjector() {
        return instanceInjector;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public SongManager getSongManager() {
        return songManager;
    }

    @Override
    public Logger getLogger() {
        return StaticHandler.getCore().getLogger();
    }

    /**
     * Checks if the server is running GUI
     *
     * @return Returns true if {@link #getUIPlatforms()} is empty
     */
    @Override
    public boolean isGUI() {
        return !getUIPlatforms().isEmpty();
    }

    /**
     * Get loaded GUIs
     *
     * @return registered UIs
     */
    @Override
    public List<UIInterface> getUIPlatforms() {
        return new ArrayList<>(uiInterfaces);
    }

    /**
     * Adds to {@link #getUIPlatforms()} for Modules to validate what UIs are usage such as JavaFX GUI or WebGUI
     *
     * @param uiInterface
     */
    @Override
    public void registerUIPlatform(UIInterface uiInterface) {
        uiInterfaces.add(uiInterface);
    }

    @Override
    public boolean isDebug() {
        return StaticHandler.isDebug();
    }

    @Override
    public void loadedModule(Module module) {
        getPluginManager().callEvent(new ModuleLoadedEvent(module));
    }

    @Override
    public void setDebug(boolean debug) {
        StaticHandler.setDebug(debug);
    }

    public static MP3Server getInstance() {
        return instance;
    }
}
