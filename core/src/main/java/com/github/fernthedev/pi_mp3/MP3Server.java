package com.github.fernthedev.pi_mp3;

import com.github.fernthedev.lightchat.server.terminal.ServerTerminal;
import com.github.fernthedev.lightchat.server.terminal.ServerTerminalSettings;

public class MP3Server extends ServerTerminal {

    public static void main(String[] args) {
        ServerTerminal.init(args,
                ServerTerminalSettings.builder()
                        .allowChangePassword(false)
                        .allowTermPackets(false)
                        .serverSettings(new MP3ServerSettings())
                .build()
        );

        server.start();
    }

}
