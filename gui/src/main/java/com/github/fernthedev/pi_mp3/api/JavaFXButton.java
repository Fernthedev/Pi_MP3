package com.github.fernthedev.pi_mp3.api;

import com.github.fernthedev.fernutils.thread.ThreadUtils;
import com.github.fernthedev.pi_mp3.api.ui.Position;
import com.github.fernthedev.pi_mp3.api.ui.UIButton;
import com.github.fernthedev.pi_mp3.api.ui.UIInterface;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class JavaFXButton extends JavaFXElement<Button> implements UIButton {

    private final List<ButtonListener> buttonListeners = new ArrayList<>();

    public JavaFXButton(UIInterface uiInterface, @NonNull Position position) {
        this(uiInterface, position, new Button().widthProperty().get(), new Button().heightProperty().get());
    }

    public JavaFXButton(UIInterface uiInterface, @NonNull Position position, double width, double height) {
        super(position, new Button());

        node.setTranslateX(position.getX());
        node.setTranslateY(position.getY());

        node.resize(width, height);

        node.setOnMouseClicked(event -> runEvents(uiInterface, parseClickEvent(event, ButtonEvent.CLICK)));

        node.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> runEvents(uiInterface, parseClickEvent(event, ButtonEvent.SELECT)));

        node.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> runEvents(uiInterface, parseClickEvent(event, ButtonEvent.SELECT)));

        node.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> runEvents(uiInterface, parseClickEvent(event, ButtonEvent.HOVER)));

        node.addEventFilter(MouseEvent.MOUSE_EXITED, event -> runEvents(uiInterface, parseClickEvent(event, ButtonEvent.UNHOVER)));
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
     * Gets the button label text
     *
     * @return
     */
    @Override
    public String getText() {
        return node.getText();
    }

    /**
     * Sets the button label text
     *
     * @param text
     */
    @Override
    public void setText(String text) {
        node.setText(text);
    }

    /**
     * Get the textbox hint label
     *
     * @return
     */
    @Override
    public String getHintLabel() {
        return node.tooltipProperty().get().getText();
    }

    /**
     * Set the textbox hint label
     *
     * @param label
     */
    @Override
    public void setHintLabel(String label) {
        node.tooltipProperty().get().setText(label);
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
    @Override
    public void addButtonListener(ButtonListener buttonListener) {
        buttonListeners.add(buttonListener);
    }
}
