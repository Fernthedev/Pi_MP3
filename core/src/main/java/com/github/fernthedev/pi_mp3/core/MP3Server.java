package com.github.fernthedev.pi_mp3.core;

import com.github.fernthedev.lightchat.server.terminal.ServerTerminal;
import com.github.fernthedev.lightchat.server.terminal.ServerTerminalSettings;
import com.github.fernthedev.pi_mp3.api.ICore;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.module.Module;
import com.github.fernthedev.pi_mp3.api.module.ModuleHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Contains and manages the core of the application.
 */
public class MP3Server extends ServerTerminal implements ICore {

    @Getter
    private static Injector injector;


    private MP3Server(String[] args, Module... modules) {
        ServerTerminal.init(args,
                ServerTerminalSettings.builder()
                        .allowChangePassword(false)
                        .allowTermPackets(false)
                        .serverSettings(new MP3ServerSettings())
                        .build()
        );

        server.start();

        MP3Pi.setCore(this);
        injector = Guice.createInjector(new ServerGuiceModule());
        MP3Pi.setInjector(injector);

        if (modules.length > 0) {
            for (Module module : modules) {
                ModuleHandler.registerModule(module);
            }
        }

        ModuleHandler.initializeModules();
    }

    public static void debug(String[] args, Module... modules) {
        new MP3Server(args, modules);
    }

    public static void main(String[] args) {
        new MP3Server(args);
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
    @Override
    public Queue<String> getSongsQueue() {
        return new LinkedList<>(List.of("Some song1", "Some other song", "The best song ever"));
    }
}
