package com.github.fernthedev.pi_mp3.api.ui;

import lombok.Setter;

public interface UIButton extends UIElement {

    /**
     * Gets the button label text
     *
     * @return
     */
    String getText();

    /**
     * Sets the button label text
     *
     * @param text
     */
    void setText(String text);

    /**
     * Get the textbox hint label
     *
     * @return
     */
    String getHintLabel();

    /**
     * Set the textbox hint label
     *
     * @param label
     */
    void setHintLabel(String label);

    /**
     * Adds a button event.
     * This button event may be called on different
     * threads depending on the implementation
     * <p>
     * Attempt to use thread-safe code.
     *
     * @param buttonListener
     */
    void addButtonListener(ButtonListener buttonListener);

    @FunctionalInterface
    interface ButtonListener {

        /**
         * Adds a button event.
         * This button event may be called on different
         * threads depending on the implementation
         * <p>
         * Attempt to use thread-safe code.
         *
         * @param event
         */
        void onEvent(UIInterface ui, ButtonEvent event);
    }

    enum ButtonEvent {

        /**
         * When the mouse hovers over button
         */
        HOVER,

        /**
         * When the mouse clicks the button. Is true only when clicked
         */
        CLICK,

        /**
         * When the mouse clicks the button. Is true only while clicked
         */
        SELECT,

        /**
         * Called when mouse is no longer hovering
         */
        UNHOVER,

        /**
         * Called when mouse is no longer being held
         */
        UNSELECT;


        /**
         * Whether or not the Shift modifier is down on this event.
         */
        @Setter
        private boolean shiftDown;

        /**
         * Whether or not the Shift modifier is down on this event.
         *
         * @return true if the Shift modifier is down on this event
         */
        public boolean isShiftDown() {
            return shiftDown;
        }

        /**
         * Whether or not the Control modifier is down on this event.
         */
        @Setter
        private boolean controlDown;

        /**
         * Whether or not the Control modifier is down on this event.
         *
         * @return true if the Control modifier is down on this event
         */
        public boolean isControlDown() {
            return controlDown;
        }

        /**
         * Whether or not the Alt modifier is down on this event.
         */
        @Setter
        private boolean altDown;

        /**
         * Whether or not the Alt modifier is down on this event.
         *
         * @return true if the Alt modifier is down on this event
         */
        public boolean isAltDown() {
            return altDown;
        }
    }

}
