package com.github.fernthedev.pi_mp3.core.audio.songs;

import com.badlogic.gdx.files.FileHandle;
import com.github.fernthedev.pi_mp3.api.songs.Song;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class FileSong implements Song {

    private final File file;

    @Getter
    private final FileHandle fileHandle;

    public FileSong(File file) {
        this.file = file;
        this.fileHandle = new FileHandle(file);
    }

    public FileSong(FileHandle fileHandle) {
        this.file = fileHandle.file();
        this.fileHandle = fileHandle;
    }

    @Override
    public int read(@NotNull byte[] buffer) {
        return 0;
    }

    @Override
    public void reset() {

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
