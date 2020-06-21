package com.github.fernthedev.pi_mp3.core.command;

import com.github.fernthedev.lightchat.server.SenderInterface;
import com.github.fernthedev.lightchat.server.terminal.command.Command;
import lombok.NonNull;

public class MusicCommand extends Command {
    public MusicCommand() {
        super("music");
    }

    @Override
    public void onCommand(SenderInterface sender, String[] args) {

    }
}
