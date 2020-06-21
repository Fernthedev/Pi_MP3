package com.github.fernthedev.pi_mp3.api.events;

import com.github.fernthedev.lightchat.core.api.event.api.Event;
import com.github.fernthedev.lightchat.core.api.event.api.HandlerList;
import com.github.fernthedev.pi_mp3.api.songs.Song;
import com.github.fernthedev.pi_mp3.api.songs.SongAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * An event that fires on a
 * song action.
 *
 * Note, some events may trigger others
 */
@RequiredArgsConstructor
@Getter
public class SongActionEvent extends Event {

    @Nullable
    private final Song song;

    private final SongAction action;

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
