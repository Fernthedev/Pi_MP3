package com.github.fernthedev.pi_mp3.api.songs;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public enum SongAction {

    SET_SONG,
    GET_SONG,
    REPLAY,
    SET_POSITION,
    PLAY,
    PAUSE,
    RESUME,
    ADD_TO_PLAY_NEXT,
    ADD_TO_QUEUE,
    PREVIOUS_SONG,
    SKIP,
    MOVE_SONG,
    REMOVE_SONG,
    CLEAR_QUEUE,
    SHUFFLE,
    SET_LOOP;

    List<Object> parameters = null;

    /**
     * Add parameters to actions that provide them
     */
    public SongAction addParameters(@Nullable Object o,@Nullable Object... ob) {
        if (parameters == null) parameters = new ArrayList<>();

        parameters.add(o);
        if (ob != null && ob.length > 0) parameters.addAll(Arrays.asList(ob));

        return this;
    }

    /**
     * Get parameters of action
     * This list is a copy, no modifications are reflected else where.
     * Store the return value as a variable for proper usage.
     *
     * @return parameters, null if no parameters were provided.
     */
    @Nullable
    public List<Object> getParameters() {
        if (parameters == null) return null;

        return new ArrayList<>(parameters);
    }

    /**
     * Get parameters of action in the form of a queue
     * This list is a copy, no modifications are reflected else where.
     * Store the return value as a variable for proper usage.
     *
     * @return parameters, null if no parameters were provided.
     */
    @Nullable
    public LinkedList<Object> getParametersQueue() {
        if (parameters == null) return null;

        return new LinkedList<>(parameters);
    }





}
