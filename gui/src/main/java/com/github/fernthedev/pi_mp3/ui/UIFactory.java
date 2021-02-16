package com.github.fernthedev.pi_mp3.ui;

public class UIFactory {

    private static UIInterface uiInterface;

    public static void setUiInterface(UIInterface uiInterface) {
        if (UIFactory.uiInterface != null) throw new IllegalStateException("UI Interface is already registered");

        UIFactory.uiInterface = uiInterface;
    }

    public static UIInterface getUiInterface() {
        return uiInterface;
    }
}
