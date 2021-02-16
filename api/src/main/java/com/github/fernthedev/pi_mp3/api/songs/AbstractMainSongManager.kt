package com.github.fernthedev.pi_mp3.api.songs

import com.github.fernthedev.pi_mp3.api.songs.MainSongManager
import java.lang.Runnable
import com.github.fernthedev.pi_mp3.api.songs.SongManager
import com.github.fernthedev.pi_mp3.api.songs.AbstractMainSongManager
import java.lang.IllegalArgumentException
import java.lang.InterruptedException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.function.Function
import javax.annotation.OverridingMethodsMustInvokeSuper

abstract class AbstractMainSongManager : MainSongManager, Runnable {
    override val songManagers: MutableMap<String, SongManager> = HashMap()
    protected var selectedManager: SongManager?
    private var name: String
    private val songFactories: MutableMap<String, SongFactory> = HashMap()

    override fun getName(): String {
        return name;
    }

    fun setName(s: String) {
        this.name = s
    }


    /**
     * Gets a copy of the song factories
     * @return song factories
     */
    override fun getSongFactories(): MutableMap<String, SongFactory> {
        return HashMap(songFactories)
    }

    /**
     *
     * @param name The name of the song factory
     * @return the song factory
     *
     * @throws IllegalArgumentException is thrown if the song manager does not exist
     */
    override fun getSongFactory(name: String): SongFactory {
        return songFactories[name] ?: throw IllegalArgumentException("Could not find song factory $name")
    }

    /**
     * Avoid registering the same song factory with multiple names
     *
     * @param name The name of the song factory
     * @param songFactory The song factory instance
     *
     * @throws IllegalArgumentException is thrown if the song manager with the name already exists
     */
    override fun registerSongFactory(name: String, songFactory: SongFactory) {
        require(!songFactories.containsKey(name)) {"Song factory $name already exists"}

        songFactories[name] = songFactory
    }

    constructor(selectedManager: SongManager, name: String) {
        this.selectedManager = selectedManager
        this.name = name
    }

    constructor(selectedManager: Function<AbstractMainSongManager, SongManager>, name: String) {
        this.selectedManager = selectedManager.apply(this)
        this.name = name

        if (selectedManager != null)
            registerSongManager(selectedSongManager!!)
    }

    override fun getParent(): SongManager? {
        return null
    }

    override fun registerSongManager(songManager: SongManager) {
        require(!(songManager === this)) { "You cannot register itself" }
        songManagers[songManager.uniqueId] = songManager
    }

    override fun getSongManager(s: String): SongManager? {
        return songManagers[s]
    }

    override fun selectSongManager(s: String): SongManager {
        require(songManagers.containsKey(s)) { "Could not find the song manager $s" }
        selectedManager = songManagers[s]!!
        return selectedManager!!
    }

    override val selectedSongManager: SongManager?
        get() = selectedManager

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
        initialize()
    }

    /**
     * This initializes the audio and/or connection to the music service.
     */
    override fun initialize() {
        while (isRunning) {
            update()
            try {
                Thread.sleep(30) // AVOID USING UNNECESSARY CYCLES ON UPDATE
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
        dispose()
    }

    /**
     * Handles updates in audio thread.
     */
    override fun update() {
        if (selectedManager != null)
            selectedManager!!.update()
    }

    /**
     * Handles the end of the music
     */
    @OverridingMethodsMustInvokeSuper
    override fun dispose() {
        val completableFutures: MutableList<CompletableFuture<*>> = ArrayList()
        songManagers.forEach { (_: String, songManager: SongManager) -> completableFutures.add(CompletableFuture.runAsync { songManager.dispose() }) }
        try {
            CompletableFuture.allOf(*completableFutures.toTypedArray())[30, TimeUnit.SECONDS]
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            e.printStackTrace()
        }
    }

    /**
     * Returns true if the server handling the audio
     * is running
     *
     * @return true if main thread running
     */
    abstract val isRunning: Boolean
}