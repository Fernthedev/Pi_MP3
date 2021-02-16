package com.github.fernthedev.pi_mp3.core;

import com.github.fernthedev.lightchat.server.Server;
import com.github.fernthedev.pi_mp3.api.ICore;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.songs.MainSongManager;
import com.github.fernthedev.pi_mp3.core.audio.OpenALSongManager;
import com.github.fernthedev.pi_mp3.core.audio.factory.FileSongFactory;
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
        bind(MP3Server.class).toInstance(MP3Server.getInstance());
        bind(Server.class).toInstance(MP3Server.getServer());

        bind(Logger.class).toInstance(MP3Pi.getInstance().getLogger());

        bind(MainSongManager.class).toProvider(() -> MP3Pi.getInstance().getSongManager());
        bind(ICore.class).toInstance(MP3Pi.getInstance().getCore());
        bind(FileSongFactory.class).toProvider(() -> (FileSongFactory) MP3Pi.getInstance().getSongManager().getSongFactory(FileSongFactory.NAME));

        bind(OpenALSongManager.class).toProvider(() -> (OpenALSongManager) MP3Pi.getInstance().getSongManager().selectSongManager(OpenALSongManager.NAME));
    }
}
