package com.github.fernthedev.pi_mp3.api.events;

import com.github.fernthedev.lightchat.core.api.event.api.Event;
import com.github.fernthedev.lightchat.core.api.event.api.HandlerList;
import com.github.fernthedev.pi_mp3.api.module.Module;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Called when a module is loaded
 */
@RequiredArgsConstructor
@Getter
public class ModuleLoadedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @NonNull
    private final Module module;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
