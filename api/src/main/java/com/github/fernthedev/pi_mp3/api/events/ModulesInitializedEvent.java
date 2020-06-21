package com.github.fernthedev.pi_mp3.api.events;

import com.github.fernthedev.lightchat.core.api.event.api.Event;
import com.github.fernthedev.lightchat.core.api.event.api.HandlerList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This event is called when all modules
 * that are loaded in the registry have been
 * initialized and ready.
 */
@RequiredArgsConstructor
@Getter
public class ModulesInitializedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
