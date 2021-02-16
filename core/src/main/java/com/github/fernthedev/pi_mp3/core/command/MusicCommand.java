package com.github.fernthedev.pi_mp3.core.command;

import com.github.fernthedev.lightchat.server.SenderInterface;
import com.github.fernthedev.lightchat.server.terminal.ServerTerminal;
import com.github.fernthedev.lightchat.server.terminal.command.Command;
import com.github.fernthedev.lightchat.server.terminal.command.TabExecutor;
import com.github.fernthedev.lightchat.server.terminal.exception.InvalidCommandArgumentException;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.exceptions.song.NoSongsException;
import com.github.fernthedev.pi_mp3.api.songs.Song;

import java.util.*;

public class MusicCommand extends Command implements TabExecutor {
    protected Map<String, Command> commandMap = new HashMap<>();

    public MusicCommand() {
        this("music");
        commandMap.put("play", new PlayCommand());

        Command pause = new Command("stop") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (MP3Pi.getInstance().getSongManager().getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                MP3Pi.getInstance().getSongManager().pause();
            }
        };

        commandMap.put("stop", pause);

        commandMap.put("pause", pause);

        commandMap.put("resume", new Command("resume") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (MP3Pi.getInstance().getSongManager().getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                MP3Pi.getInstance().getSongManager().resume();
            }
        });

        commandMap.put("queue", new Command("queue") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (MP3Pi.getInstance().getSongManager().getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                LinkedList<Song> queue = MP3Pi.getInstance().getSongManager().getSongQueue();

                if (queue.isEmpty()) {
                    ServerTerminal.sendMessage(sender, "Queue is empty");
                }

                for (Song song : queue) {
                    ServerTerminal.sendMessage(sender, queue.indexOf(song) + ": " + song.getFileHandle().name());
                }
            }
        });

        commandMap.put("history", new Command("history") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (MP3Pi.getInstance().getSongManager().getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                LinkedList<Song> history = MP3Pi.getInstance().getSongManager().getSongHistory();

                if (history.isEmpty()) {
                    ServerTerminal.sendMessage(sender, "History is empty");
                }

                for (Song song : history) {
                    ServerTerminal.sendMessage(sender, history.indexOf(song) + ": " + song.getFileHandle().name());
                }
            }
        });

        commandMap.put("previous", new Command("previous") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                try {
                    if (args.length == 0) {
                        MP3Pi.getInstance().getSongManager().previousSong();
                    } else {
                        if (args[0].replace(".", "").matches("[0-9]+")) {
                            int amount = Integer.parseInt(args[0]);
                            MP3Pi.getInstance().getSongManager().previousSong(amount);
                        }
                    }
                } catch (NoSongsException | IndexOutOfBoundsException e) {
                    ServerTerminal.sendMessage(sender, "There are either no songs or you're skipping more songs than in queue.");
                }
            }
        });

        commandMap.put("skip", new Command("skip") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (MP3Pi.getInstance().getSongManager().getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                try {
                    if (args.length == 0) {
                        MP3Pi.getInstance().getSongManager().skip();
                    } else {
                        if (args[0].replace(".", "").matches("[0-9]+")) {
                            int amount = Integer.parseInt(args[0]);
                            MP3Pi.getInstance().getSongManager().skip(amount);
                        }
                    }
                } catch (NoSongsException | IndexOutOfBoundsException e) {
                    ServerTerminal.sendMessage(sender, "There are either no songs or you're skipping more songs than in queue.");
                }
            }
        });

        commandMap.put("volume", new Command("volume") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (MP3Pi.getInstance().getSongManager().getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                if (args.length == 0) {
                    ServerTerminal.sendMessage(sender, "Current Volume: " + MP3Pi.getInstance().getSongManager().getVolume());
                } else {
                    if (args[0].replace(".","").matches("[0-9]+")) {
                        float volume = Float.parseFloat(args[0]);
                        MP3Pi.getInstance().getSongManager().setVolume(volume);
                        ServerTerminal.sendMessage(sender, "Current Volume: " + volume);
                    }
                }
            }
        });

        commandMap.put("position", new Command("position") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (MP3Pi.getInstance().getSongManager().getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                if (args.length == 0) {
                    ServerTerminal.sendMessage(sender, "Current position: " + MP3Pi.getInstance().getSongManager().getPosition());
                } else {
                    if (args[0].replace(".","").matches("[0-9]+")) {
                        float position = Float.parseFloat(args[0]);
                        MP3Pi.getInstance().getSongManager().setPosition(position);
                        ServerTerminal.sendMessage(sender, "Current Position: " + position);
                    }
                }
            }
        });
    }

    protected MusicCommand(String s) {
        super(s);
    }


    @Override
    public void onCommand(SenderInterface sender, String[] args) {
        if (args.length == 0) {
            doHelp(sender);
            return;
        }
        Queue<String> argQueue = new LinkedList<>(Arrays.asList(args));

        String rootArg = argQueue.remove();

        Command c = commandMap.get(rootArg);

        if (c == null) throw new InvalidCommandArgumentException("No arg " + rootArg + " found");

        c.onCommand(sender, argQueue.toArray(new String[0]));
    }

    protected void doHelp(SenderInterface senderInterface) {
        ServerTerminal.sendMessage(senderInterface, getName() + "'s help");
        for (String s : commandMap.keySet()) {
            ServerTerminal.sendMessage(senderInterface, "--" + s);
        }
    }

    @Override
    public List<String> getCompletions(SenderInterface sender, LinkedList<String> args) {
        if (args.size() <= 1) {
            return new ArrayList<>(commandMap.keySet());
        }

        LinkedList<String> argQueue = new LinkedList<>(args);

        String rootArg = argQueue.remove();

        Command c = commandMap.get(rootArg);

        if (!(c instanceof TabExecutor)) return Collections.emptyList();

        TabExecutor tabExecutor = (TabExecutor) c;

        return tabExecutor.getCompletions(sender, argQueue);
    }
}
