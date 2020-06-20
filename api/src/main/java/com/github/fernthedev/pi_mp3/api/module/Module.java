package com.github.fernthedev.pi_mp3.api.module;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public abstract class Module {

    /**
     * Injected after registered
     */
    @Setter(value = AccessLevel.MODULE)
    @Getter
    private String name;

    public void onEnable() {}

    public void onDisable() {}
}
