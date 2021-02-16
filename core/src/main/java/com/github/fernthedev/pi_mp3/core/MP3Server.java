package com.github.fernthedev.pi_mp3.core;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.fernthedev.config.common.exceptions.ConfigLoadException;
import com.github.fernthedev.config.gson.GsonConfig;
import com.github.fernthedev.fernutils.console.ArgumentArrayUtils;
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
import com.github.fernthedev.pi_mp3.api.songs.MainSongManager;
import com.github.fernthedev.pi_mp3.core.audio.OpenALSongManager;
import com.github.fernthedev.pi_mp3.core.audio.SongManagerImpl;
import com.github.fernthedev.pi_mp3.core.audio.VLCSongManager;
import com.github.fernthedev.pi_mp3.core.command.MusicCommand;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import org.lwjgl.openal.ALC;
import org.slf4j.Logger;
import uk.co.caprica.vlcj.factory.NativeLibraryMappingException;

import java.io.File;
import java.util.concurrent.ExecutorService;

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

    public static void testModules(String[] args, Module... modules) {
        MP3Server mp3Server = new MP3Server();
        mp3Server.init(args, modules);

        mp3Server.started = true;
    }

    public static void main(String[] args) {
        start(args);
    }


    public static void start(String[] args, Module... modules) {
        MP3Server mp3Server = new MP3Server();
        mp3Server.init(args, modules);

        mp3Server.initAudio();
        mp3Server.started = true;
    }


    private MP3Server() {
        instance = this;
        moduleHandler = new ModuleLoadingHandler(this);
    }



    private void init(String[] args, Module[] modules) {
        started = false;
        ArgumentArrayUtils.parseArguments(args)
                .handle("-debug", s -> StaticHandler.setDebug(true))
        .apply();

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
        initiateInjector();



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

    public static void initiateInjector() {
        if (instanceInjector == null) {
            instanceInjector = Guice.createInjector(new ServerGuiceModule());
            MP3Pi.setInjector(instanceInjector);
        }
    }

    private void initAudio() {

//        ALC.create();
        if (!MP3Pi.isTestMode())
            server.addShutdownListener(ALC::destroy);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        audioHandler = new LibGDXHackApp(config, "PiMP3");

//        Audio audio = Gdx.audio;

//        ALC.create();
        songManager = new SongManagerImpl(abstractMainSongManager -> new OpenALSongManager(abstractMainSongManager, audioHandler), "PiMP3 Song Manager", this);

        if (!MP3Pi.isTestMode()) {
            try {
                songManager.registerSongManager(new VLCSongManager(songManager));
            } catch (NativeLibraryMappingException e) {
                logger.error("Unable to load VLC libraries, is it installed?", e);
            }

            Thread t = new Thread(songManager);
            t.setDaemon(true);
            t.start();
        }


//        Song musicTest = new Song(Gdx.files.local("sound.ogg"));

        if (StaticHandler.isDebug())
            songManager.play(Constants.getDebugSong());


//        musicTest.setLooping(true);

        MusicCommand musicCommand = instanceInjector.getInstance(MusicCommand.class);
        registerCommand(musicCommand);
        StaticHandler.getCore().getLogger().info(ColorCode.GREEN + "Initialized audio");
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
    public MainSongManager getSongManager() {
        return songManager;
    }

    @Override
    public Logger getLogger() {
        return StaticHandler.getCore().getLogger();
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
