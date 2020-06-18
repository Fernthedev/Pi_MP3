package com.github.fernthedev.pi_mp3.api;

import java.util.Queue;

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


    /**
     * Returns the queue of songs
     *
     * @deprecated This will be replaced with it's own class
     *
     * @return
     */
    @Deprecated
    Queue<String> getSongsQueue();

}
