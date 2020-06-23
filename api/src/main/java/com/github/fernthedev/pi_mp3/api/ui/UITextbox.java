package com.github.fernthedev.pi_mp3.api.ui;

public interface UITextbox extends UIElement {

    /**
     * Get the textbox hint label
     * @return
     */
    String getHintLabel();

    /**
     * Set the textbox hint label
     * @param label
     */
    void setHintLabel(String label);

    /**
     * Get the input of the text box
     * @return input
     */
    String getInput();

    /**
     * Set the text box input
     * @param s string
     */
    void setInput(String s);

    /**
     * Sets the text validator for
     * @param textValidator validator
     */
    void setValidator(TextValidator textValidator);

    /**
     * Shortcut to isValid(getInput());
     * @return {@link #isValid(String)}
     */
    boolean isValid();

    /**
     *
     * @param s The string to check
     * @return true if the {@link TextValidator} returns true
     */
    boolean isValid(String s);

    /**
     * Gets the maximum input length
     * The default length is to be defined
     * by the implementation
     * @return length
     */
    int getInputLength();

    /**
     * Sets the maximum input length
     */
    void setInputLength(int length);


    @FunctionalInterface
    interface TextValidator {
        boolean isValid(UITextbox textbox, String s);
    }
}
