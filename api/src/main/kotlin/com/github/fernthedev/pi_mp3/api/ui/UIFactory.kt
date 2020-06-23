package com.github.fernthedev.pi_mp3.api.ui

interface UIFactory {
    /**
     * Creates the button
     * @param width
     * @param height
     * @return The button
     */
    fun createButton(position: Position, width: Double, height: Double): UIButton

    /**
     * Creates the button
     * The width and height are defined
     * by the implementation of the
     * [UIFactory]
     *
     * @return The button
     */
    fun createButton(position: Position): UIButton

    /**
     * Creates the button
     * @param width
     * @param height
     * @return The button
     */
    fun createTextbox(position: Position?, width: Double, height: Double): UITextbox

    /**
     * Creates the button
     * The width and height are defined
     * by the implementation of the
     * [UIFactory]
     *
     * @return The button
     */
    fun createTextbox(position: Position?): UITextbox
}