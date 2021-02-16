package com.github.fernthedev.pi_mp3.api.songs


import java.io.File
import java.nio.IntBuffer

interface Song {
    fun read(buffer: ByteArray): Int
    fun reset()

    fun getName() : String

    fun getFile() : File?
}