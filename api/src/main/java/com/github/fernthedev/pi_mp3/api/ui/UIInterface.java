package com.github.fernthedev.pi_mp3.api.ui;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public interface UIInterface {

    /**
     * Gets the name of the UI interface
     * @return the name
     */
    String getName();

    /**
     * Gets the current screen
     * @return screen
     */
    UIScreen getCurrentScreen();

    /**
     * Sets the current screen
     *
     *
     * @param uiScreen
     * @return
     */
    CompletableFuture<UIScreen> setCurrentScreen(UIScreen uiScreen);

    /**
     * Returns true if currently running on the UI thread
     * @return
     */
    boolean isUIThread();

    /**
     *
     * @param runnable
     * @return
     */
    <V> CompletableFuture<V> runOnUIThread(Callable<V> runnable);

    /**
     * Returns the UI factory of UI
     * It should contain the default
     * UI element styles and settings
     * used throughout the interface.
     *
     * @return ui factory
     */
    UIFactory getUIFactory();

}
