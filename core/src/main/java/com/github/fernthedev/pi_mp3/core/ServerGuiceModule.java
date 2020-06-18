package com.github.fernthedev.pi_mp3.core;

import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;

public class ServerGuiceModule extends AbstractModule {
    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(MP3Pi.class).toInstance(MP3Pi.getInstance());
    }
}
