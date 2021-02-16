package com.github.fernthedev.pi_mp3.ui;

import javafx.scene.Scene;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public interface UIInterface {
    <V> CompletableFuture<V> runOnUIThread(Callable<V> callable);

    String getName();

    Scene getCurrentScreen();

    CompletableFuture<Scene> setCurrentScreen(Scene uiScreen);

    boolean isUIThread();
}
