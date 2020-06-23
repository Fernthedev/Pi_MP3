package com.github.fernthedev.pi_mp3.api;

import com.github.fernthedev.pi_mp3.api.ui.Position;
import com.github.fernthedev.pi_mp3.api.ui.UIElement;
import javafx.scene.Node;
import lombok.Getter;
import lombok.NonNull;

public class JavaFXElement<N extends Node> implements UIElement {
    @NonNull
    private Position position;

    @Getter
    protected N node;

    public JavaFXElement(@NonNull Position position, N node) {
        this.position = position;
        this.node = node;
    }

    public JavaFXElement(N node) {
        this.node = node;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean isHidden() {
        return !node.isVisible();
    }

    @Override
    public void setHidden(boolean hidden) {
        node.setVisible(!hidden);
    }
}
