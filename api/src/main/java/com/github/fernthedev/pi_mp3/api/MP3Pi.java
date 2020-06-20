package com.github.fernthedev.pi_mp3.api;

import com.google.inject.Injector;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Queue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MP3Pi implements ICore {

    private static MP3Pi mp3Pi;

    @Getter
    private static Injector injector;

    private static ICore core;

    public static void setCore(@NonNull ICore core) {
        if (MP3Pi.core != null) throw new IllegalStateException("Core is already initialized in API");

        MP3Pi.core = core;

        mp3Pi = new MP3Pi();
    }

    public static void setInjector(Injector injector) {
        if (MP3Pi.injector != null) throw new IllegalStateException("Injector is already initialized in API");
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

    /**
     * Returns the queue of songs
     *
     * @return
     * @deprecated This will be replaced with it's own class
     */
    @Deprecated
    @Override
    public Queue<String> getSongsQueue() {
        return core.getSongsQueue();
    }
}
