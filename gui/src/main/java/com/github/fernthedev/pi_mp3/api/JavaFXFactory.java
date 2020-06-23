package com.github.fernthedev.pi_mp3.api;

import com.github.fernthedev.pi_mp3.api.ui.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class JavaFXFactory implements UIFactory {

    @Getter
    @NonNull
    private final UIInterface uiInterface;

    @NotNull
    @Override
    public UIButton createButton(@NotNull Position position, double width, double height) {
        return new JavaFXButton(uiInterface, position, width, height);
    }

    @NotNull
    @Override
    public UIButton createButton(@NotNull Position position) {
        return new JavaFXButton(uiInterface, position);
    }

    @NotNull
    @Override
    public UITextbox createTextbox(@Nullable Position position) {
        // TODO: IMPLEMENT
        return null;
    }

    @NotNull
    @Override
    public UITextbox createTextbox(@Nullable Position position, double width, double height) {
        // TODO: IMPLEMENT
        return null;
    }
}
