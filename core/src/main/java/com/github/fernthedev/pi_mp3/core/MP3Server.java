package com.github.fernthedev.pi_mp3.core;

import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.lightchat.server.Server;
import com.github.fernthedev.lightchat.server.terminal.ServerTerminal;
import com.github.fernthedev.lightchat.server.terminal.ServerTerminalSettings;
import com.github.fernthedev.pi_mp3.api.ICore;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.module.Module;
import com.github.fernthedev.pi_mp3.api.module.ModuleHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.libc.LibCStdlib.free;

/**
 * Contains and manages the core of the application.
 */
public class MP3Server extends ServerTerminal implements ICore {

    @Getter
    private static Injector injector;

    private static MP3Server instance;

    @Getter
    private final ModuleHandler moduleHandler;
    private volatile boolean started;


    private MP3Server(String[] args, Module... modules) {
        instance = this;
        started = false;
        ServerTerminal.init(args,
                ServerTerminalSettings.builder()
                        .allowChangePassword(false)
                        .allowTermPackets(false)
                        .serverSettings(new MP3ServerSettings())
                        .launchConsoleWhenNull(false)
                        .build()
        );

        new Thread(server).start();
//        server.start();

        try {
            server.getStartupLock().waitOnLock();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        MP3Pi.setCore(this);
        injector = Guice.createInjector(new ServerGuiceModule());
        MP3Pi.setInjector(injector);

        moduleHandler = new ModuleHandler();

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

//        ALC.create();
        server.addShutdownListener(ALC::destroy);

        //Initialization
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        long   device            = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        long  context    = alcCreateContext(device, attributes);
        alcMakeContextCurrent(context);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        ALCapabilities  alCapabilities  = AL.createCapabilities(alcCapabilities);



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

        started = true;
    }

    public static void start(String[] args, Module... modules) {
        new MP3Server(args, modules);
    }

    public static void main(String[] args) {
        new MP3Server(args);
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

    /**
     * Returns the queue of songs
     *
     * @return
     * @deprecated This will be replaced with it's own class
     */
    @Deprecated
    @Override
    public Queue<String> getSongsQueue() {
        return new LinkedList<>(List.of("Some song1", "Some other song", "The best song ever"));
    }

    @Override
    public ExecutorService getExecutorService() {
        return server.getExecutorService();
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    public static MP3Server getInstance() {
        return instance;
    }
}
