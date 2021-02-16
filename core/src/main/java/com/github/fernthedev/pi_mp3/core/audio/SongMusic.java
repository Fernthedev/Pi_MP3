package com.github.fernthedev.pi_mp3.core.audio;

import com.badlogic.gdx.backends.lwjgl3.audio.OpenALAudio;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALMusic;
import com.badlogic.gdx.files.FileHandle;
import com.github.fernthedev.pi_mp3.api.songs.Song;
import com.github.fernthedev.pi_mp3.core.audio.songs.FileSong;

/**
 * TODO: Clean and do code instead of wrap around a wrapper
 */
public class SongMusic extends OpenALMusic {

    private final Song song;
    private final OpenALMusic music;

    public SongMusic(OpenALAudio audio, FileHandle file, Song song) {
        super(audio, file);
        this.song = song;

        if (song instanceof FileSong) {
            this.music = ((FileSong) song).getMusic();
        } else music = null;
    }

    private void validateMusicNull() {
        if (music == null)
            throw new NullPointerException("The method was called on music but it is null");
    }


    @Override
    public void play() {
        validateMusicNull();

        music.play();
    }

    @Override
    public void stop() {
        validateMusicNull();

        music.stop();
    }

    @Override
    public void pause() {
        validateMusicNull();

        music.pause();
    }

    @Override
    public boolean isPlaying() {
        validateMusicNull();

        return music.isPlaying();
    }

    @Override
    public void setLooping(boolean isLooping) {
        validateMusicNull();

        music.setLooping(isLooping);
    }

    @Override
    public boolean isLooping() {
        validateMusicNull();

        return music.isLooping();
    }

    @Override
    public void setVolume(float volume) {
        validateMusicNull();

        music.setVolume(volume);
    }

    @Override
    public float getVolume() {
        validateMusicNull();

        return music.getVolume();
    }

    @Override
    public void setPan(float pan, float volume) {
        validateMusicNull();

        music.setPan(pan, volume);
    }

    @Override
    public void setPosition(float position) {
        validateMusicNull();

        music.setPosition(position);
    }

    @Override
    public float getPosition() {
        validateMusicNull();

        return music.getPosition();
    }


    @Override
    public int getChannels() {
        validateMusicNull();

        return music.getChannels();
    }

    @Override
    public int getRate() {
        validateMusicNull();

        return music.getRate();
    }

    @Override
    public void update() {
        validateMusicNull();

        music.update();
    }

    @Override
    public void dispose() {
        validateMusicNull();

        music.dispose();
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        validateMusicNull();

        music.setOnCompletionListener(listener);
    }

    @Override
    public int getSourceId() {
        validateMusicNull();

        return music.getSourceId();
    }

    /**
     * Fills as much of the buffer as possible and returns the number of bytes filled. Returns <= 0 to indicate the end of the
     * stream.
     *
     * @param buffer
     */
    @Override
    public int read(byte[] buffer) {
        return song.read(buffer);
    }

    /**
     * Resets the stream to the beginning.
     */
    @Override
    public void reset() {
        song.reset();
    }
}
