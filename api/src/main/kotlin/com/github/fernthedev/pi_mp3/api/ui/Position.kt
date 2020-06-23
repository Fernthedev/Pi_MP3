package com.github.fernthedev.pi_mp3.api.ui

data class Position(
        val x: Double = 0.0,
        val y: Double = 0.0
) {


    fun copy(x: Double?, y: Double?): Position {

        val xPar = x ?: this.x
        val yPar = y ?: this.y

        return Position(x = xPar, y = yPar)
    }


}