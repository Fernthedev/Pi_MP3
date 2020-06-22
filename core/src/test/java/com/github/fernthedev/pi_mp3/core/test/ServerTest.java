package com.github.fernthedev.pi_mp3.core.test;

import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.core.MP3Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServerTest {


    private static boolean serverStarted = false;

    @DisplayName("Start server")
    @Test
    public static void testStartServer() {
        MP3Pi.setTestMode(true);
//        StaticHandler.setDebug(true);
        if (!serverStarted) {
            Assertions.assertDoesNotThrow(() -> MP3Server.start(new String[0]));
        }
        serverStarted = true;
    }

}
