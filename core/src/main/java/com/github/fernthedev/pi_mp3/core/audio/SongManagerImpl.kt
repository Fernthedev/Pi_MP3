package com.github.fernthedev.pi_mp3.core.audio

import com.github.fernthedev.fernutils.thread.ThreadUtils
import com.github.fernthedev.pi_mp3.api.events.SongActionEvent
import com.github.fernthedev.pi_mp3.api.songs.AbstractMainSongManager
import com.github.fernthedev.pi_mp3.api.songs.Song
import com.github.fernthedev.pi_mp3.api.songs.SongAction
import com.github.fernthedev.pi_mp3.api.songs.SongManager
import com.github.fernthedev.pi_mp3.api.songs.SongManager.LoopMode
import com.github.fernthedev.pi_mp3.core.MP3Server
import com.github.fernthedev.pi_mp3.core.audio.factory.FileSongFactory
import java.util.concurrent.CompletableFuture
import java.util.function.Function

class SongManagerImpl(
    selectedManager: Function<AbstractMainSongManager, SongManager>,
    name: String,
    private val server: MP3Server
) : AbstractMainSongManager(selectedManager, name) {

    init {
        registerSongFactory(FileSongFactory.NAME, FileSongFactory())
    }

    /**
     * Returns true if the server handling the audio
     * is running
     *
     * @return true if main thread running
     */
    override val isRunning: Boolean
        get() = MP3Server.getServer().isRunning

    /**
     * When an object implementing interface `Runnable` is used
     * to create a thread, starting the thread causes the object's
     * `run` method to be called in that separately executing
     * thread.
     *
     *
     * The general contract of the method `run` is that it may
     * take any action whatsoever.
     *
     * @see Thread.run
     */
    override fun run() {
        super.run()
    }

    /**
     * This initializes the audio and/or connection to the music service.
     */
    override fun initialize() {
        super.initialize()
    }

    /**
     * Handles updates in audio thread.
     */
    override fun update() {
        super.update()
    }

    /**
     * Handles the end of the music
     */
    override fun dispose() {
        super.dispose()
    }

    /**
     * Name of the song manager e.g OpenAL or Online
     *
     * @return name
     */
    override fun getName(): String {
        return super.getName()
    }


    override fun getCurrentSong(): Song? {
        val song = super.getCurrentSong()
        MP3Server.getServer().pluginManager.callEvent(SongActionEvent(song, SongAction.GET_SONG))
        return song
    }

    override fun replay() {
        MP3Server.getServer().pluginManager.callEvent(SongActionEvent(super.getCurrentSong(), SongAction.REPLAY))
        super.replay()
    }

    override fun setPosition(position: Float): CompletableFuture<Song> {
        MP3Server.getServer().pluginManager.callEvent(SongActionEvent(super.getCurrentSong(), SongAction.SET_POSITION))
        return super.setPosition(position)
    }

    override fun play(song: Song): CompletableFuture<Song> {
        MP3Server.getServer().pluginManager.callEvent(SongActionEvent(super.getCurrentSong(), SongAction.PLAY))
        return super.play(song)
    }

    override fun pause(): CompletableFuture<Song> {
        MP3Server.getServer().pluginManager.callEvent(SongActionEvent(super.getCurrentSong(), SongAction.PAUSE))
        return super.pause()
    }

    override fun resume(): CompletableFuture<Song> {
        MP3Server.getServer().pluginManager.callEvent(SongActionEvent(super.getCurrentSong(), SongAction.RESUME))
        return super.resume()
    }

    override fun playNext(song: Song) {
        MP3Server.getServer().pluginManager.callEvent(
            SongActionEvent(
                super.getCurrentSong(),
                SongAction.ADD_TO_PLAY_NEXT.addParameters(song)
            )
        )
        super.playNext(song)
    }

    override fun addSongToQueue(song: Song) {
        MP3Server.getServer().pluginManager.callEvent(
            SongActionEvent(
                super.getCurrentSong(),
                SongAction.ADD_TO_QUEUE.addParameters(song)
            )
        )
        super.addSongToQueue(song)
    }

    override fun addSongToQueue(songs: Collection<Song>) {
        MP3Server.getServer().pluginManager.callEvent(
            SongActionEvent(
                super.getCurrentSong(),
                SongAction.ADD_TO_QUEUE.addParameters(songs)
            )
        )
        super.addSongToQueue(songs)
    }

    override fun addSongToQueue(vararg songs: Song) {
        MP3Server.getServer().pluginManager.callEvent(
            SongActionEvent(
                super.getCurrentSong(),
                SongAction.ADD_TO_QUEUE.addParameters(songs)
            )
        )
        super.addSongToQueue(*songs)
    }

    override fun previousSong(index: Int): CompletableFuture<Song> {
        MP3Server.getServer().pluginManager.callEvent(
            SongActionEvent(
                super.getCurrentSong(),
                SongAction.PREVIOUS_SONG.addParameters(index)
            )
        )
        return super.previousSong(index)
    }

    override fun previousSong(song: Song): CompletableFuture<Song> {
        MP3Server.getServer().pluginManager.callEvent(
            SongActionEvent(
                super.getCurrentSong(),
                SongAction.PREVIOUS_SONG.addParameters(song)
            )
        )
        return super.previousSong(song)
    }

    override fun skip(index: Int): CompletableFuture<Song> {
        MP3Server.getServer().pluginManager.callEvent(
            SongActionEvent(
                super.getCurrentSong(),
                SongAction.SKIP.addParameters(index)
            )
        )
        return super.skip(index)
    }

    override fun moveSong(song: Int, index: Int) {
        MP3Server.getServer().pluginManager.callEvent(
            SongActionEvent(
                super.getCurrentSong(),
                SongAction.MOVE_SONG.addParameters(song, index)
            )
        )
        super.moveSong(song, index)
    }

    override fun removeFromQueue(song: Int) {
        MP3Server.getServer().pluginManager.callEvent(
            SongActionEvent(
                super.getCurrentSong(),
                SongAction.REMOVE_SONG.addParameters(song)
            )
        )
        super.removeFromQueue(song)
    }

    override fun removeFromQueue(song: Song): Boolean {
        MP3Server.getServer().pluginManager.callEvent(
            SongActionEvent(
                super.getCurrentSong(),
                SongAction.REMOVE_SONG.addParameters(song)
            )
        )
        return super.removeFromQueue(song)
    }

    override fun clear() {
        MP3Server.getServer().pluginManager.callEvent(SongActionEvent(super.getCurrentSong(), SongAction.CLEAR_QUEUE))
        super.clear()
    }

    override fun shuffle() {
        MP3Server.getServer().pluginManager.callEvent(SongActionEvent(super.getCurrentSong(), SongAction.SHUFFLE))
        super.shuffle()
    }

    override fun loop(loopMode: LoopMode) {
        MP3Server.getServer().pluginManager.callEvent(
            SongActionEvent(
                super.getCurrentSong(),
                SongAction.SET_LOOP.addParameters(loopMode)
            )
        )
        super.loop(loopMode)
    }

    override fun setCurrentSong(song: Song) {
        ThreadUtils.runAsync(
            {
                MP3Server.getServer().pluginManager.callEvent(
                    SongActionEvent(
                        super.getCurrentSong(),
                        SongAction.SET_SONG.addParameters(song)
                    )
                )
            },
            server.executorService
        )
        super.setCurrentSong(song)
    }
}