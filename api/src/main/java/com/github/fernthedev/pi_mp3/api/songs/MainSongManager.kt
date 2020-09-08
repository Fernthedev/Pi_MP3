package com.github.fernthedev.pi_mp3.api.songs

import com.github.fernthedev.pi_mp3.api.exceptions.song.NoSongManagerSelectedException
import com.github.fernthedev.pi_mp3.api.songs.SongManager.LoopMode
import java.util.*
import java.util.concurrent.CompletableFuture

interface MainSongManager : SongManager {
    /**
     * Returns a unmodifiable list of registered song managers
     * @return List of song managers.
     */
    val songManagers: Map<String, SongManager>

    /**
     * Registers a song manager to be selected
     */
    fun registerSongManager(songManager: SongManager)

    /**
     * Gets the song manager
     */
    fun getSongManager(s: String): SongManager?

    /**
     * Selects the song manager
     */
    fun selectSongManager(s: String): SongManager

    /**
     * The selected song manager
     * @return selected song manager, null if none are selected.
     */
    val selectedSongManager: SongManager?

    /**
     * Gets the parent song manager
     *
     * @return parent song manager
     */
    override fun getParent(): SongManager? {
        return null
    }

    /**
     * Validates if the song manager is selected.
     * If not, throws an exception
     */
    fun validateSongManagerSelected() {
        if (selectedSongManager == null)
            throw NoSongManagerSelectedException()
    }

    /**
     * Gets the song history list
     *
     * @return history
     */
    override fun getSongHistory(): LinkedList<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.songHistory
    }

    /**
     * Returns a modifiable list of songs in the queue.
     *
     * @return queue
     */
    override fun getSongQueue(): LinkedList<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.songQueue
    }

    /**
     * Returns the song queue length
     *
     * @return queue length
     */
    override fun getSongQueueLength(): Int {
        return songQueue.size
    }

    /**
     * Returns the song history length
     *
     * @return history length
     */
    override fun getSongHistoryLength(): Int {
        return songHistory.size
    }

    /**
     * Gets the song from the queue
     *
     * @param index index
     * @return song
     */
    override fun getSongInQueue(index: Int): Song {
        validateSongManagerSelected()

        return selectedSongManager!!.getSongInQueue(index)
    }

    /**
     * Gets the song from the history
     *
     * @param index index
     * @return song
     */
    override fun getSongInHistory(index: Int): Song {
        validateSongManagerSelected()

        return selectedSongManager!!.getSongInHistory(index)
    }

    /**
     * Checks if the song is in the queue
     *
     * @param song song
     * @return true if in queue
     */
    override fun isSongInQueue(song: Song): Boolean {
        return songQueue.contains(song)
    }

    /**
     * Checks whether the song has played before
     *
     * @param song song
     * @return if song is in history
     */
    override fun hasPlayedBefore(song: Song): Boolean {
        return songHistory.contains(song)
    }

    /**
     * Returns the playing song
     *
     * @return current song
     */
    override fun getCurrentSong(): Song? {
        validateSongManagerSelected()

        return selectedSongManager!!.currentSong
    }

    /**
     * Restarts the song
     */
    override fun replay() {
        validateSongManagerSelected()

        selectedSongManager!!.replay()
    }

    /**
     * @param volume volume of current song
     */
    override fun setVolume(volume: Float): CompletableFuture<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.setVolume(volume)
    }

    /**
     * @return volume of current song
     */
    override fun getVolume(): Float {
        validateSongManagerSelected()

        return selectedSongManager!!.volume
    }

    /**
     * Sets song position to position
     *
     * @param position position
     */
    override fun setPosition(position: Float): CompletableFuture<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.setPosition(position)
    }

    /**
     * Get position of song
     */
    override fun getPosition(): Float {
        validateSongManagerSelected()

        return selectedSongManager!!.position
    }

    /**
     * if playing, returns true
     *
     * @return if playing
     */
    override fun isPlaying(): Boolean {
        validateSongManagerSelected()

        return selectedSongManager!!.isPlaying
    }

    /**
     * Plays the song instantly
     * Runs on Audio Thread
     *
     * @param song
     */
    override fun play(song: Song): CompletableFuture<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.play(song)
    }

    /**
     * Pauses the song
     * Runs on audio thread
     */
    override fun pause(): CompletableFuture<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.pause()
    }

    /**
     * Resumes the song
     */
    override fun resume(): CompletableFuture<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.resume()
    }

    /**
     * Adds the song to the first queue
     *
     * @param song
     */
    override fun playNext(song: Song) {
        validateSongManagerSelected()

        return selectedSongManager!!.playNext(song)
    }

    /**
     * Adds the song to the queue
     *
     * @param song
     */
    override fun addSongToQueue(song: Song) {
        validateSongManagerSelected()
        
        return selectedSongManager!!.addSongToQueue(song)
    }

    /**
     * Adds the songs to the queue
     *
     * @param songs
     */
    override fun addSongToQueue(songs: Collection<Song>) {
        validateSongManagerSelected()
        
        return selectedSongManager!!.addSongToQueue(songs)
    }

    /**
     * Adds the songs to the queue
     *
     * @param songs
     */
    override fun addSongToQueue(vararg songs: Song) {
        validateSongManagerSelected()

        selectedSongManager!!.addSongToQueue(*songs)
    }

    override fun getPositionInQueue(song: Song): Int {
        validateSongManagerSelected()

        return selectedSongManager!!.getPositionInQueue(song)
    }


    override fun getPositionInHistory(song: Song): Int {
        validateSongManagerSelected()

        return selectedSongManager!!.getPositionInHistory(song)
    }

    /**
     * Rewinds the song to the previous
     */
    override fun previousSong(): CompletableFuture<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.previousSong()
    }

    /**
     * Goes back to the index of the previous song. The index is from [.getSongHistory]
     *
     * @param index
     */
    override fun previousSong(index: Int): CompletableFuture<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.previousSong(index)
    }

    /**
     * Goes back to the index of the previous song. The song is from [.getSongHistory]
     *
     * @param song
     */
    override fun previousSong(song: Song): CompletableFuture<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.previousSong(song)
    }

    /**
     * Skips to the next song
     */
    override fun skip(): CompletableFuture<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.skip()
    }

    /**
     * Skips to the index of the next song
     * The index is from [.getSongQueue]
     *
     * @param index
     */
    override fun skip(index: Int): CompletableFuture<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.skip(index)
    }

    /**
     * Skips to the index of the next song
     * The song is from [.getSongQueue]
     *
     * @param song
     */
    override fun skip(song: Song): CompletableFuture<Song> {
        validateSongManagerSelected()

        return selectedSongManager!!.skip(song)
    }

    /**
     * Moves the song from the queue to the index in queue
     *
     * @param song
     * @param index
     */
    override fun moveSong(song: Song, index: Int) {
        validateSongManagerSelected()

        return selectedSongManager!!.moveSong(song, index)
    }

    /**
     * Moves the song from the index in the queue to the new index in queue
     *
     * @param song
     * @param index
     */
    override fun moveSong(song: Int, index: Int) {
        validateSongManagerSelected()

        return selectedSongManager!!.moveSong(song, index)
    }

    /**
     * Removes the song from queue
     *
     * @param song
     */
    override fun removeFromQueue(song: Int) {
        validateSongManagerSelected()

        return selectedSongManager!!.removeFromQueue(song)
    }

    /**
     * Removes the song from queue
     *
     * @param song
     * @return true if song removed, false if no song in queue
     */
    override fun removeFromQueue(song: Song): Boolean {
        validateSongManagerSelected()

        return selectedSongManager!!.removeFromQueue(song)
    }

    /**
     * Removes all songs from queue
     */
    override fun clear() {
        validateSongManagerSelected()

        return selectedSongManager!!.clear()
    }

    /**
     * Shuffles all songs in queue in random order.
     */
    override fun shuffle() {
        validateSongManagerSelected()

        return selectedSongManager!!.shuffle()
    }

    /**
     * Set loop mode
     *
     * @param loopMode
     */
    override fun loop(loopMode: LoopMode) {
        validateSongManagerSelected()

        return selectedSongManager!!.loop(loopMode)
    }

    /**
     * Used internally
     * @param song
     */
    override fun setCurrentSong(song: Song) {
        validateSongManagerSelected()

        selectedSongManager!!.setCurrentSong(song)
    }

    @Deprecated("""USED FOR TESTING ONLY
      DO NOT USE""")
    override fun setNull() {
        validateSongManagerSelected()

        return selectedSongManager!!.setNull()
    }
}