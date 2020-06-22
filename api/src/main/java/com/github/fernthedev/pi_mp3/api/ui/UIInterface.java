package com.github.fernthedev.pi_mp3.api.ui;

import java.util.List;

public interface UIInterface {

    String getName();

    List<UIButton> addUIButton();

    UIButtonFactory getUIButtonFactory();

}
