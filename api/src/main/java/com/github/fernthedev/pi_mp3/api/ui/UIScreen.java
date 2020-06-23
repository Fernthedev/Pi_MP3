package com.github.fernthedev.pi_mp3.api.ui;

import java.util.List;

/**
 *
 */
public interface UIScreen {


    String getName();

    /**
     * {@link #getUIObjects()} but scoped to UIButtons
     * @return a copy of the list
     */
    List<UIButton> getUIButtons();

    /**
     * Return a list of UI
     * elements in the
     * current UI
     *
     * @return a copy of the list
     */
    List<UIElement> getUIObjects();

    /**
     * Adds an element to
     * the screen
     * @param uiElement
     */
    void addElement(UIElement uiElement);

    /**
     * Adds an element to
     * the screen
     * @param uiElement
     */
    void removeElement(UIElement uiElement);

    UIFactory getUIFactory();

}
