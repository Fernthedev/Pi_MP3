package com.github.fernthedev.pi_mp3.api;

import com.github.fernthedev.lightchat.core.api.plugin.PluginManager;
import com.github.fernthedev.pi_mp3.api.module.ModuleHandler;
import com.github.fernthedev.pi_mp3.api.songs.SongManager;
import com.github.fernthedev.pi_mp3.api.ui.UIInterface;
import com.google.inject.Injector;
import lombok.*;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MP3Pi implements ICore {

    private static MP3Pi mp3Pi;

    @Getter
    private static Injector injector;

    private static ICore core;

    /**
     * Internal use only
     */
    @Deprecated
    @Getter
    @Setter
    private static boolean testMode = false;

    public static void setCore(@NonNull ICore core) {
        if (MP3Pi.core != null && !MP3Pi.isTestMode()) throw new IllegalStateException("Core is already initialized in API");

        MP3Pi.core = core;

        mp3Pi = new MP3Pi();
    }

    public static void setInjector(Injector injector) {
        if (MP3Pi.injector != null) {
            if (MP3Pi.isTestMode()) return;

            throw new IllegalStateException("Injector is already initialized in API");
        }
        MP3Pi.injector = injector;
    }

    public static MP3Pi getInstance() {
        return mp3Pi;
    }

    /**
     * @return The actual core implementing the code.
     */
    @Override
    public ICore getCore() {
        return core;
    }

    @Override
    public PluginManager getPluginManager() {
        return core.getPluginManager();
    }

    @Override
    public ExecutorService getExecutorService() {
        return core.getExecutorService();
    }

    @Override
    public ModuleHandler getModuleHandler() {
        return core.getModuleHandler();
    }

    @Override
    public boolean isStarted() {
        return core.isStarted();
    }

    @Override
    public SongManager getSongManager() {
        return core.getSongManager();
    }

    @Override
    public Logger getLogger() {
        return core.getLogger();
    }

    /**
     * Checks if the server is running GUI
     *
     * @return Returns true if {@link #getUIPlatforms()} is empty
     */
    @Override
    public boolean isGUI() {
        return core.isGUI();
    }

    /**
     * Get loaded GUIs
     *
     * @return registered UIs
     */
    @Override
    public List<UIInterface> getUIPlatforms() {
        return core.getUIPlatforms();
    }

    /**
     * Adds to {@link #getUIPlatforms()} for Modules to validate what UIs are usage such as JavaFX GUI or WebGUI
     *
     * @param uiInterface
     */
    @Override
    public void registerUIPlatform(UIInterface uiInterface) {
        core.registerUIPlatform(uiInterface);
    }


}
