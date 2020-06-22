package com.github.fernthedev.pi_mp3.core;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALAudio;
import com.badlogic.gdx.backends.lwjgl3.audio.mock.MockAudio;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.fernthedev.lightchat.core.ColorCode;
import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import kotlin.Pair;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;


public class LibGDXHackApp implements Application {

    private Audio audio;
    private final Files files;
    private final Lwjgl3Clipboard clipboard;
    private int logLevel = LOG_INFO;
    private ApplicationLogger applicationLogger;

    private Thread thread;

    private final BlockingDeque<Pair<Callable<Object>, CompletableFuture<Object>>> queue = new LinkedBlockingDeque<>();

    LibGDXHackApp(Lwjgl3ApplicationConfiguration config, String title) {
        Lwjgl3NativesLoader.load();
        setApplicationLogger(new Lwjgl3ApplicationLogger());

        if (title == null) title = getClass().getSimpleName();

        config.setTitle(title);

        Gdx.app = this;
//        if (!MP3Pi.isTestMode()) {
            try {
                this.audio = Gdx.audio = new OpenALAudio(16,
                        9, 512);
            } catch (Throwable t) {
                log(getClass().getSimpleName(), "Couldn't initialize audio, disabling audio", t);
                this.audio = Gdx.audio = new MockAudio();
            }
//        } else {
//            this.audio = Gdx.audio = new MockAudio();
//        }

        this.files = Gdx.files = new Lwjgl3Files();
        this.clipboard = new Lwjgl3Clipboard();


        new Thread(() -> {
            try {
                thread = Thread.currentThread();
                loop();
            } catch(Throwable t) {
                if (t instanceof RuntimeException)
                    throw (RuntimeException) t;
                else
                    throw new GdxRuntimeException(t);
            } finally {
                cleanup();
            }
        }).start();
    }

    private void loop() {
        while (MP3Server.getServer().isRunning()) {
            // OLDFIXME put it on a separate thread
            if (audio instanceof OpenALAudio) {
                try {
                    ((OpenALAudio) audio).update();

                    BlockingDeque<Pair<Callable<Object>, CompletableFuture<Object>>> queueCopy;

                    synchronized (queue) {
                        queueCopy = new LinkedBlockingDeque<>(queue);
                        queue.clear();
                    }

                    while (!queueCopy.isEmpty()) {
                        Pair<Callable<Object>, CompletableFuture<Object>> pair = queueCopy.take();

                        Object o = pair.getFirst().call();

                        pair.getSecond().complete(o);
                        StaticHandler.getCore().getLogger().debug("Completed " + pair.getSecond());
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cleanup() {
        StaticHandler.getCore().getLogger().warn("Shutting down Audio Thread");
        if (audio instanceof OpenALAudio) {
            try {
                ((OpenALAudio) audio).dispose();
            } catch (IllegalStateException ignored) {}
        }

        MP3Pi.getInstance().getLogger().info(ColorCode.GREEN + "Shut down audio thread");
    }

    /**
     *
     * Use the audio thread to avoid issues
     *
     * @param runnable
     */
    public <T> CompletableFuture<T> runOnAudioThread(Callable<T> runnable) throws Exception {
        CompletableFuture<T> future = new CompletableFuture<>();
        if (Thread.currentThread() == thread) {
            future.complete(runnable.call());
        } else {
            Callable<Object> callable = (Callable<Object>) runnable;
            CompletableFuture<Object> objectCompletableFuture = (CompletableFuture<Object>) future;
            synchronized (queue) {
                queue.add(new Pair<>(callable, objectCompletableFuture));
            }
        }
        return future;
    }


    @Override
    public ApplicationListener getApplicationListener() {
        return null;
    }

    @Override
    public Graphics getGraphics() {
        return null;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Files getFiles() {
        return files;
    }

    @Override
    public Net getNet() {
        return null;
    }

    @Override
    public void debug (String tag, String message) {
        if (logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message);
    }

    @Override
    public void debug (String tag, String message, Throwable exception) {
        if (logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message, exception);
    }

    @Override
    public void log (String tag, String message) {
        if (logLevel >= LOG_INFO) getApplicationLogger().log(tag, message);
    }

    @Override
    public void log (String tag, String message, Throwable exception) {
        if (logLevel >= LOG_INFO) getApplicationLogger().log(tag, message, exception);
    }

    @Override
    public void error (String tag, String message) {
        if (logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message);
    }

    @Override
    public void error (String tag, String message, Throwable exception) {
        if (logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message, exception);
    }

    @Override
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public void setApplicationLogger (ApplicationLogger applicationLogger) {
        this.applicationLogger = applicationLogger;
    }

    @Override
    public ApplicationLogger getApplicationLogger () {
        return applicationLogger;
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.Desktop;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public long getJavaHeap() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    @Override
    public long getNativeHeap() {
        return getJavaHeap();
    }

    @Override
    public Preferences getPreferences(String name) {

        return null;

//        if (preferences.containsKey(name)) {
//            return preferences.get(name);
//        } else {
//            Preferences prefs = new Lwjgl3Preferences(
//                    new Lwjgl3FileHandle(new File(config.preferencesDirectory, name), config.preferencesFileType));
//            preferences.put(name, prefs);
//            return prefs;
//        }
    }

    @Override
    public Clipboard getClipboard() {
        return clipboard;
    }

    @Override
    public void postRunnable(Runnable runnable) { }

    @Override
    public void exit() {
        new IllegalAccessError("Exit was called on " + getClass().getSimpleName() + ". Was this supposed to happen?").printStackTrace();
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
    }
}
