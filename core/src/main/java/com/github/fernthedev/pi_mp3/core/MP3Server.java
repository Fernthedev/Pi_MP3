package com.github.fernthedev.pi_mp3.core;

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

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

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
