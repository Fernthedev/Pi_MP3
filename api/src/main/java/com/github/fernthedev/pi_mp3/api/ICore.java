package com.github.fernthedev.pi_mp3.api;

import com.github.fernthedev.pi_mp3.api.module.ModuleHandler;

import java.util.concurrent.ExecutorService;

/**
 * The API for any modules should default to this.
 *
 * If the API cannot accomplish said task, the developer should
 * use the core in {@link com.github.fernthedev.pi_mp3.core} and
 * when required reflection though it is not officially supported.
 *
 * All methods here are
 *
 */
public interface ICore {

    /**
     *
     * @return The actual core implementing the code.
     */
    ICore getCore();




    ExecutorService getExecutorService();

    ModuleHandler getModuleHandler();

    boolean isStarted();

    SongManager getSongManager();
}
