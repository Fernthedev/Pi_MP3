package com.github.fernthedev.pi_mp3.core.audio.songs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALMusic;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.songs.Song;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class FileSong implements Song {

    @Getter
    private final OpenALMusic music;

    private final File file;
    private final FileHandle fileHandle;

    public FileSong(File file) {
        this.file = file;
        this.fileHandle = new FileHandle(file);

        OpenALMusic tempMusic = null;
        try {
            tempMusic = (OpenALMusic) Gdx.audio.newMusic(fileHandle);
        } catch (GdxRuntimeException e) {
            MP3Pi.getInstance().getLogger().warn("Could not load {} due to {}", file.getName(), e.getLocalizedMessage());

            if (StaticHandler.isDebug())
                e.printStackTrace();
        }
        this.music = tempMusic;
    }

    public FileSong(FileHandle fileHandle) {
        this.file = fileHandle.file();
        this.fileHandle = fileHandle;

        OpenALMusic tempMusic = null;
        try {
            tempMusic = (OpenALMusic) Gdx.audio.newMusic(fileHandle);
        } catch (GdxRuntimeException e) {
            MP3Pi.getInstance().getLogger().warn("Could not load {} due to {}", file.getName(), e.getLocalizedMessage());

            if (StaticHandler.isDebug())
                e.printStackTrace();
        }
        this.music = tempMusic;
    }

    @Override
    public int read(@NotNull byte[] buffer) {
        if (music == null)
            throw new IllegalStateException("Unable to read music bytes for " + file.getName() + " as it's not supported by LibGDX");

        return music.read(buffer);
    }

    @Override
    public void reset() {
        if (music == null)
            throw new IllegalStateException("Unable to read music bytes for " + file.getName() + " as it's not supported by LibGDX");

        music.reset();
    }

    @NotNull
    @Override
    public String getName() {
        return file.getName();
    }

    @Nullable
    @Override
    public File getFile() {
        return file;
    }
}
