package com.github.fernthedev.pi_mp3.core;

import com.github.fernthedev.pi_mp3.api.exceptions.song.NoSongPlayingException;
import com.github.fernthedev.pi_mp3.api.exceptions.song.SongNotFoundException;
import com.github.fernthedev.pi_mp3.api.songs.AbstractSongChild;
import com.github.fernthedev.pi_mp3.api.songs.MainSongManager;
import com.github.fernthedev.pi_mp3.api.songs.Song;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("JavaDoc")
public class OpenALSongManager extends AbstractSongChild {
    private final LibGDXHackApp audioHandler;

    public OpenALSongManager(MainSongManager mainSongManager, LibGDXHackApp audioHandler) {
        super(mainSongManager, "OpenAL");
        this.audioHandler = audioHandler;
    }

    /**
     * @deprecated Internal use
     */
    @Deprecated
    public void update() {
        if (currentSong == null && !getSongQueue().isEmpty()) {
            skip();
        }
    }

    /**
     * This initializes the audio and/or connection to the music service.
     */
    @Override
    public void initialize() {

    }


    /**
     * Handles the end of the music
     */
    @Override
    public void dispose() {
        if (currentSong != null) {
            currentSong.getMusic().stop();
            currentSong.getMusic().dispose();
        }
    }


    /**
     * Returns the playing song
     *
     * @return
     */
    @Override
    @Nullable
    public Song getCurrentSong() {
        return currentSong;
    }

    /**
     * Restarts the song
     */
    @Override
    public void replay() {
        currentSong.getMusic().setPosition(0);
    }

    /**
     * @param volume volume of current song
     */
    @Override
    public CompletableFuture<Song> setVolume(float volume) {
        if (currentSong == null)
            throw new NoSongPlayingException("A song must be playing to set volume");

        try {
            return audioHandler.runOnAudioThread(() -> {
                currentSong.getMusic().setVolume(volume);
                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return volume of current song
     */
    @Override
    public float getVolume() {
        if (currentSong == null)
            throw new NoSongPlayingException("A song must be playing to get volume");

        return currentSong.getMusic().getVolume();
    }

    /**
     * Sets song position to position
     *
     * @param position
     * @return
     */
    @Override
    public CompletableFuture<Song> setPosition(float position) {
        if (currentSong == null)
            throw new NoSongPlayingException("A song must be playing to set position");


        try {
            return audioHandler.runOnAudioThread(() -> {
                currentSong.getMusic().setPosition(position);
                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get position of song
     */
    @Override
    public float getPosition() {
        if (currentSong == null)
            throw new NoSongPlayingException("A song must be playing to get volume");

        return currentSong.getMusic().getPosition();
    }

    /**
     * if playing, returns true
     *
     * @return
     */
    @Override
    public boolean isPlaying() {
        return currentSong == null || currentSong.getMusic().isPlaying();
    }

    /**
     * Plays the song instantly
     *
     * @param song
     * @return
     */
    @Override
    public CompletableFuture<Song> play(Song song) {
        try {
            return audioHandler.runOnAudioThread(() -> {
                setCurrentSong(song);
    //            playNext(song);
    //            skip();
                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Pauses the song
     * @return
     */
    @Override
    public CompletableFuture<Song> pause() {
        try {
            return audioHandler.runOnAudioThread(() -> {
                currentSong.getMusic().pause();
                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resumes the song
     * @return
     */
    @Override
    public CompletableFuture<Song> resume() {

        try {
            return audioHandler.runOnAudioThread(() -> {
                currentSong.getMusic().play();
                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds the song to the first queue
     *
     * @param song
     */
    @Override
    public void playNext(@NonNull Song song) {

        super.playNext(song);
    }

    /**
     * Adds the song to the queue
     *
     * @param song
     */
    @Override
    public void addSongToQueue(@NonNull Song song) {

        super.playNext(song);
    }

    /**
     * Adds the songs to the queue
     *
     * @param songs
     */
    @Override
    public void addSongToQueue(Collection<? extends Song> songs) {

        super.addSongToQueue(songs);
    }

    /**
     * Adds the songs to the queue
     *
     * @param songs
     */
    @Override
    public void addSongToQueue(@NonNull Song... songs) {

        super.addSongToQueue(songs);
    }

    /**
     * Rewinds the song to the previous
     * @return
     */
    @Override
    public CompletableFuture<Song> previousSong() {
        return previousSong(1);
    }

    /**
     * Goes back to the index of the previous song. The index is from {@link #getSongHistory()}
     *
     * @param index
     * @return
     */
    @Override
    public CompletableFuture<Song> previousSong(int index) {


        if (index < 1) throw new IllegalArgumentException("Index cannot be less than 1");
        if (getSongHistory().size() < index) throw new IndexOutOfBoundsException("Index " + index + " is larger than song history " + getSongHistory().size());

        try {
            return audioHandler.runOnAudioThread(() -> {
                if (currentSong != null) {
                    playNext(currentSong);
                    currentSong.getMusic().stop();
                }


                Song song;

                for (int i = 0; i < index; i++) {
                    song = getSongHistory().pop();

                    playNext(song);
                }

                skip().get(); // Since play next adds to the top queue, skip to it.

//                setCurrentSong(song);

                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Goes back to the index of the previous song. The song is from {@link #getSongHistory()}
     *
     * @param song
     */
    @Override
    public CompletableFuture<Song> previousSong(@NonNull Song song) {

        int index = getSongHistory().indexOf(song);

        if (index < 1) throw new SongNotFoundException("Cannot find song in history");

        return previousSong(index);
    }

    /**
     * Skips to the next song
     */
    @Override
    public CompletableFuture<Song> skip() {
        return skip(1);
//        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.SKIP));
//        if (getSongQueue().isEmpty()) throw new NoSongsException("No songs in queue");
//
//        try {
//            return audioHandler.runOnAudioThread(() -> {
//                Song song = currentSong;
//                setCurrentSong(getSongQueue().pop());
//                getSongHistory().push(song);
//
//                return song;
//            });
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * Skips to the index of the next song
     * The index is from {@link #getSongQueue()}
     *
     * @param index
     * @return
     */
    @Override
    public CompletableFuture<Song> skip(int index) {

        if (index < 1) throw new IllegalArgumentException("Index cannot be less than 1");
        if (getSongQueue().size() < index) throw new IndexOutOfBoundsException("Index " + index + " is larger than song queue " + getSongQueue().size());

        try {
            return audioHandler.runOnAudioThread(() -> {
                if (currentSong != null)
                    currentSong.getMusic().stop();

                for (int i = 0; i < index; i++) {
                    Song oldSong = currentSong; // Get current song playing
                    currentSong = getSongQueue().pop(); // Get next song to play and set to play

                    if (oldSong != null)
                        getSongHistory().push(oldSong); // Put old song in history
                }

                setCurrentSong(currentSong);

                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Skips to the index of the next song
     * The song is from {@link #getSongQueue()}
     *
     * @param song
     * @return
     */
    @Override
    public CompletableFuture<Song> skip(@NonNull Song song) {
        int index = getSongQueue().indexOf(song);
        if (index < 1) throw new SongNotFoundException("Cannot find song in history");
        return skip(song);
    }

    /**
     * Moves the song from the queue to the index in queue
     *
     * @param song
     * @param index
     */
    @Override
    public void moveSong(@NonNull Song song, int index) {
        int songIndex = getSongQueue().indexOf(song);
        if (songIndex < 1) throw new SongNotFoundException("Cannot find song in history");

        moveSong(songIndex, index);
    }

    /**
     * Moves the song from the index in the queue to the new index in queue
     *
     * @param song
     * @param index
     */
    @Override
    public void moveSong(@NonNull int song, int index) {
        if (getSongQueue().size() < index) throw new IndexOutOfBoundsException("Index " + index + " is larger than song queue " + getSongQueue().size());
        if (getSongQueue().size() < song) throw new IndexOutOfBoundsException("Index " + song + " is larger than song queue " + getSongQueue().size());

        Song song1 = getSongQueue().remove(song);

        getSongQueue().add(index, song1);
    }



    @Override
    public void loop(LoopMode loopMode) {

        switch (loopMode) {
            case SONG:
                currentSong.getMusic().setLooping(true);
                break;
            case NONE:
                currentSong.getMusic().setLooping(false);
                break;
        }

        super.loop(loopMode);
    }


    public void setCurrentSong(@NonNull Song song) {
        if (currentSong != null)
            currentSong.getMusic().dispose();

        currentSong = song;
        currentSong.getMusic().setOnCompletionListener(music -> {
            getSongHistory().push(currentSong);
            if (loopMode == LoopMode.SONG_ONCE) {
                loop(LoopMode.NONE);
            }

            if (!getSongQueue().isEmpty())
                skip();
            else
                currentSong = null;

            music.stop();
            music.dispose();
        });

        try {
            audioHandler.runOnAudioThread(() -> {
                currentSong.getMusic().play();
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
