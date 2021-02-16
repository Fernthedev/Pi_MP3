package com.github.fernthedev.pi_mp3.core;

import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import org.slf4j.Logger;

public class ServerGuiceModule extends AbstractModule {
    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        if (MP3Pi.getInstance() == null) throw new IllegalStateException("MP3Pi instance must not be null. Call after it has been initialized");
        bind(MP3Pi.class).toInstance(MP3Pi.getInstance());
        bind(Logger.class).toInstance(MP3Pi.getInstance().getLogger());
    }
}
