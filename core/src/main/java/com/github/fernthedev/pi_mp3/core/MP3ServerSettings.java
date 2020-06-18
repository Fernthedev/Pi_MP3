package com.github.fernthedev.pi_mp3.core;

import com.github.fernthedev.lightchat.server.settings.ServerSettings;

/**
 * Manages the setting values for the server and their defaults
 */
public class MP3ServerSettings extends ServerSettings {

    public MP3ServerSettings() {
        setPort(Constants.PORT);
    }
}
