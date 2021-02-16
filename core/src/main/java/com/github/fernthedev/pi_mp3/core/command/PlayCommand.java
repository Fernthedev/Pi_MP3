package com.github.fernthedev.pi_mp3.core.command;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.fernthedev.lightchat.core.ColorCode;
import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.lightchat.server.SenderInterface;
import com.github.fernthedev.lightchat.server.terminal.ServerTerminal;
import com.github.fernthedev.lightchat.server.terminal.command.Command;
import com.github.fernthedev.lightchat.server.terminal.command.FileNameTabExecutor;
import com.github.fernthedev.lightchat.server.terminal.command.TabExecutor;
import com.github.fernthedev.lightchat.server.terminal.exception.InvalidCommandArgumentException;
import com.github.fernthedev.pi_mp3.api.songs.MainSongManager;
import com.github.fernthedev.pi_mp3.core.Constants;
import com.github.fernthedev.pi_mp3.core.audio.factory.FileSongFactory;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class PlayCommand extends MusicCommand {

    @Inject
    protected PlayCommand(Injector injector, MainSongManager songManager, FileSongFactory fileSongFactory) {
        super("play");
        commandMap.put("test", new Command("test") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                songManager.play(Constants.getDebugSong());
            }
        });


        Command fileCommand = new FileCommand("file");
        Command folderCommand = new FileCommand("folder") {

            @Override
            public void onCommand(SenderInterface sender, String[] args) {

                Queue<String> argQueue = new LinkedList<>(Arrays.asList(args));

                while (!argQueue.isEmpty()) {
                    FileHandle fileHandle = Gdx.files.absolute(argQueue.remove());

                    if (!fileHandle.exists()) throw new InvalidCommandArgumentException("Unable to find file " + fileHandle.name());

                    if (!fileHandle.isDirectory()) throw new InvalidCommandArgumentException(fileHandle.name() + "Must be a directory, not a file");

                    ServerTerminal.sendMessage(sender, "Playing music " + fileHandle.name());

                    for (FileHandle file : fileHandle.list()) {
                        try {
                            songManager.playNext(fileSongFactory.getSong(file));
                        } catch (GdxRuntimeException | FileNotFoundException e) {
                            if (StaticHandler.isDebug()) e.printStackTrace();

                            ServerTerminal.sendMessage(sender, "Unable to load file " + file.name() + " for: " + e.getMessage());
                        }
                    }


                }

            }
        };

        injector.injectMembers(fileCommand);
        injector.injectMembers(folderCommand);

        commandMap.put("file", fileCommand);

        commandMap.put("folder", folderCommand);
    }


    private static class FileCommand extends Command implements TabExecutor {

        private static final FileNameTabExecutor fileNameTabExecutor = new FileNameTabExecutor();

        @Inject
        private FileSongFactory fileSongFactory;

        @Inject
        private MainSongManager songManager;

        public FileCommand(@NonNull String name) {
            super(name);
        }

        @Override
        public void onCommand(SenderInterface sender, String[] args) {
            if (args.length == 0) {
                ServerTerminal.sendMessage(sender, ColorCode.RED + "Please provide files.");
                return;
            }

            Queue<String> argQueue = new LinkedList<>(Arrays.asList(args));

            while (!argQueue.isEmpty()) {
                FileHandle fileHandle = Gdx.files.absolute(argQueue.remove());

                if (!fileHandle.exists()) throw new InvalidCommandArgumentException("Unable to find file " + fileHandle.name());

                if (fileHandle.isDirectory()) throw new InvalidCommandArgumentException(fileHandle.name() + "File must be a file, not a directory");
                
                ServerTerminal.sendMessage(sender, "Playing music " + fileHandle.name());

                try {
                    songManager.playNext(fileSongFactory.getSong(fileHandle));
                } catch (FileNotFoundException e) {
                    if (StaticHandler.isDebug()) e.printStackTrace();

                    ServerTerminal.sendMessage(sender, "Unable to load file " + fileHandle.name() + " for: " + e.getMessage());
                }
            }

        }

        /**
         * Returns a list of completions based on the arguments given
         *
         * @param sender
         * @param args
         * @return
         */
        @Override
        public List<String> getCompletions(SenderInterface sender, LinkedList<String> args) {
            return fileNameTabExecutor.getCompletions(sender, args);
        }

        private List<String> getFiles(File folder, String arg) {


            File[] files;

            if (arg == null || arg.isEmpty()) {
                files = folder.listFiles();
            } else files = folder.listFiles(
                    (dir, name) ->
                            dir.getName().startsWith(arg) ||
                                    dir.getName().contains(arg));

            if (files == null) return Collections.emptyList();

            return Arrays.stream(files).map(file1 -> file1.toPath().toString().replace("\\","/")).collect(Collectors.toList());
        }
    }
}
