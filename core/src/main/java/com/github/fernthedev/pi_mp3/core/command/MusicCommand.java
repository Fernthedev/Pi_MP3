package com.github.fernthedev.pi_mp3.core.command;

import com.github.fernthedev.lightchat.core.ColorCode;
import com.github.fernthedev.lightchat.server.SenderInterface;
import com.github.fernthedev.lightchat.server.terminal.ServerTerminal;
import com.github.fernthedev.lightchat.server.terminal.command.Command;
import com.github.fernthedev.lightchat.server.terminal.command.TabExecutor;
import com.github.fernthedev.lightchat.server.terminal.exception.InvalidCommandArgumentException;
import com.github.fernthedev.pi_mp3.api.exceptions.song.NoSongsException;
import com.github.fernthedev.pi_mp3.api.songs.MainSongManager;
import com.github.fernthedev.pi_mp3.api.songs.Song;
import com.github.fernthedev.pi_mp3.api.songs.SongManager;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.util.*;

public class MusicCommand extends Command implements TabExecutor {
    protected Map<String, Command> commandMap = new HashMap<>();

    @Inject
    public MusicCommand(Injector injector, MainSongManager songManager) {
        this("music");

        commandMap.put("play", injector.getInstance(PlayCommand.class));

        Command pause = new Command("stop") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (songManager.getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                songManager.pause();
            }
        };

        commandMap.put("stop", pause);

        commandMap.put("pause", pause);

        commandMap.put("resume", new Command("resume") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (songManager.getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                songManager.resume();
            }
        });

        commandMap.put("backend", new Command("backend") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (args.length == 0) {
                    SongManager selected = songManager;

                    if (selected != null && ((MainSongManager) selected).getSelectedSongManager() != null) selected = ((MainSongManager) selected).getSelectedSongManager();

                    ServerTerminal.sendMessage(sender, "Currently using backend " + selected.getName() + ". Other choices are");

                    for (String songManager : songManager.getSongManagers().keySet()) {
                        ServerTerminal.sendMessage(sender, songManager);
                    }
                } else {
                    String s = args[0];

                    SongManager songManagerChild = songManager.getSongManager(s);

                    if (songManagerChild == null)
                        ServerTerminal.sendMessage(sender, ColorCode.RED + "Could not find song manager " + s);
                    else {
                        songManager.selectSongManager(s);
                        ServerTerminal.sendMessage(sender, ColorCode.GREEN + "Found and selected song manager " + s);
                    }
                }
            }
        });

        commandMap.put("queue", new Command("queue") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (songManager.getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                LinkedList<Song> queue = songManager.getSongQueue();

                if (queue.isEmpty()) {
                    ServerTerminal.sendMessage(sender, "Queue is empty");
                }

                for (Song song : queue) {
                    ServerTerminal.sendMessage(sender, queue.indexOf(song) + ": " + song.getName());
                }
            }
        });

        commandMap.put("history", new Command("history") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (songManager.getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                LinkedList<Song> history = songManager.getSongHistory();

                if (history.isEmpty()) {
                    ServerTerminal.sendMessage(sender, "History is empty");
                }

                for (Song song : history) {
                    ServerTerminal.sendMessage(sender, history.indexOf(song) + ": " + song.getName());
                }
            }
        });

        commandMap.put("previous", new Command("previous") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                try {
                    if (args.length == 0) {
                        songManager.previousSong();
                    } else {
                        if (args[0].replace(".", "").matches("[0-9]+")) {
                            int amount = Integer.parseInt(args[0]);
                            songManager.previousSong(amount);
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
                if (songManager.getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                try {
                    if (args.length == 0) {
                        songManager.skip();
                    } else {
                        if (args[0].replace(".", "").matches("[0-9]+")) {
                            int amount = Integer.parseInt(args[0]);
                            songManager.skip(amount);
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
                if (songManager.getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                if (args.length == 0) {
                    ServerTerminal.sendMessage(sender, "Current Volume: " + songManager.getVolume());
                } else {
                    if (args[0].replace(".","").matches("[0-9]+")) {
                        float volume = Float.parseFloat(args[0]);
                        songManager.setVolume(volume);
                        ServerTerminal.sendMessage(sender, "Current Volume: " + volume);
                    }
                }
            }
        });

        commandMap.put("position", new Command("position") {
            @Override
            public void onCommand(SenderInterface sender, String[] args) {
                if (songManager.getCurrentSong() == null) {
                    ServerTerminal.sendMessage(sender, "Please play a song before continuing.");
                    return;
                }

                if (args.length == 0) {
                    ServerTerminal.sendMessage(sender, "Current position: " + songManager.getPosition());
                } else {
                    if (args[0].replace(".","").matches("[0-9]+")) {
                        float position = Float.parseFloat(args[0]);
                        songManager.setPosition(position);
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
