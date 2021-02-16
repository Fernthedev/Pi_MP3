package com.github.fernthedev.pi_mp3.ui;

import com.github.fernthedev.fernutils.thread.ThreadUtils;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A button with some utilities
 */
public class PiButton extends Button {

    private final List<ButtonListener> buttonListeners = new ArrayList<>();

    /**
     * Creates a button with an empty string for its label.
     */
    public PiButton(UIInterface uiInterface) {
        this(uiInterface, "");
    }

    /**
     * Constructs a button with the specified label.
     *
     * @param label a string label for the button, or
     *              {@code null} for no label
     * @throws HeadlessException if GraphicsEnvironment.isHeadless()
     *                           returns true
     * @see GraphicsEnvironment#isHeadless
     */
    public PiButton(UIInterface uiInterface, String label) throws HeadlessException {
        super(label);

        setOnMouseClicked(event -> runEvents(uiInterface, parseClickEvent(event, ButtonEvent.CLICK)));
        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> runEvents(uiInterface, parseClickEvent(event, ButtonEvent.SELECT)));
        addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> runEvents(uiInterface, parseClickEvent(event, ButtonEvent.SELECT)));
        addEventFilter(MouseEvent.MOUSE_ENTERED, event -> runEvents(uiInterface, parseClickEvent(event, ButtonEvent.HOVER)));
        addEventFilter(MouseEvent.MOUSE_EXITED, event -> runEvents(uiInterface, parseClickEvent(event, ButtonEvent.UNHOVER)));
    }

    private void runEvents(UIInterface uiInterface, ButtonEvent event) {
        if (MP3Pi.getInstance() == null) return;

        ThreadUtils.runAsync(() -> {
            ThreadUtils.runForLoopAsync(buttonListeners, buttonListener -> {
                        buttonListener.onEvent(uiInterface, event);
                    }
            ).runThreads(MP3Pi.getInstance().getExecutorService());
        }, MP3Pi.getInstance().getExecutorService());
    }

    private ButtonEvent parseClickEvent(MouseEvent mouseEvent, ButtonEvent buttonEvent) {
        if (mouseEvent.isPrimaryButtonDown()) buttonEvent = ButtonEvent.SELECT;

        buttonEvent.setAltDown(buttonEvent.isAltDown());
        buttonEvent.setControlDown(buttonEvent.isControlDown());
        buttonEvent.setShiftDown(buttonEvent.isShiftDown());


        return buttonEvent;
    }

    /**
     * Adds a button event.
     * This button event may be called on different
     * threads depending on the implementation
     * <p>
     * Attempt to use thread-safe code.
     *
     * @param buttonListener
     */
    public void addButtonListener(ButtonListener buttonListener) {
        buttonListeners.add(buttonListener);
    }

    @FunctionalInterface
    public interface ButtonListener {

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

    public enum ButtonEvent {

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
