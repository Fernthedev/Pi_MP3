package com.github.fernthedev.pi_mp3.core.test;

import com.github.fernthedev.lightchat.core.StaticHandler;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.exceptions.song.NoSongPlayingException;
import com.github.fernthedev.pi_mp3.api.songs.Song;
import com.github.fernthedev.pi_mp3.core.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SongTest {



    @DisplayName("Song playing")
    @Test
    public void testSongAdd() {
        ServerTest.testStartServer();


//        Assertions.assertTimeout(Duration.ofSeconds(2), () -> {
//            while (MP3Pi.getInstance().getCore() != null &&
//                    MP3Pi.getInstance() == null || MP3Pi.getInstance().getSongManager() == null) {
//                try {
//                    Thread.sleep(40);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


        StaticHandler.getCore().getLogger().info("Adding debug song to play queue");
        Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
            try {
                MP3Pi.getInstance().getSongManager().play(Constants.getDebugSong()).get(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        Assertions.assertEquals(Constants.getDebugSong(), MP3Pi.getInstance().getSongManager().getCurrentSong());
//        Assertions.assertDoesNotThrow(() -> );
//
//        Assertions.assertTimeout(Duration.ofSeconds(180), () -> MP3Server.testModules(new String[0],
//                new TestModuleClass1(),
//                new TestModuleClass2(),
//                new TestModuleClass3(),
//                new TestModuleClass4(),
//                new TestModuleClass5(),
//                new TestModuleClass6()
//        ));


    }

    @DisplayName("Song queue add")
    @Test
    public void testSongQueueAdd() {
        ServerTest.testStartServer();

        StaticHandler.getCore().getLogger().info("Adding debug song to play queue");
        MP3Pi.getInstance().getSongManager().addSongToQueue(Constants.getDebugSong());
        Assertions.assertTrue(MP3Pi.getInstance().getSongManager().getSongQueue().contains(Constants.getDebugSong()));

    }


    @DisplayName("Song queue add")
    @Test
    public void testSongListQueueAdd() {
        ServerTest.testStartServer();

        StaticHandler.getCore().getLogger().info("Adding debug song list to play queue");

        List<Song> songList = new ArrayList<>();
        songList.add(Constants.getDebugSong());
        songList.add(Constants.getDebugSong());
        songList.add(Constants.getDebugSong());

        MP3Pi.getInstance().getSongManager().clear();
        MP3Pi.getInstance().getSongManager().addSongToQueue(songList);
        Assertions.assertTrue(MP3Pi.getInstance().getSongManager().getSongQueue().containsAll(songList));

    }


    @DisplayName("Song play next add")
    @Test
    public void testPlayNextSong() {
        ServerTest.testStartServer();

        StaticHandler.getCore().getLogger().info("Adding debug song to play next");


        MP3Pi.getInstance().getSongManager().clear();
        MP3Pi.getInstance().getSongManager().playNext(Constants.getDebugSong());
        Assertions.assertEquals(0, MP3Pi.getInstance().getSongManager().getSongQueue().indexOf(Constants.getDebugSong()));

    }

    @DisplayName("Set and get volume")
    @Test
    public void testVolumeSong() {
        ServerTest.testStartServer();

        float volume = 0.5f;

        StaticHandler.getCore().getLogger().info("Adding debug song to play next");

        MP3Pi.getInstance().getSongManager().setNull();
        // Check if exception is thrown when no song is playing.
        Assertions.assertThrows(NoSongPlayingException.class, () -> MP3Pi.getInstance().getSongManager().setVolume(volume));
        Assertions.assertThrows(NoSongPlayingException.class, () -> MP3Pi.getInstance().getSongManager().getVolume());


        MP3Pi.getInstance().getSongManager().clear();
        try {
            MP3Pi.getInstance().getSongManager().play(Constants.getDebugSong()).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            throw new AssertionError("Took more than 10 seconds", e);
        }

        Assertions.assertDoesNotThrow(() -> {
            MP3Pi.getInstance().getSongManager().setVolume(volume).get(10, TimeUnit.SECONDS);
        });

        Assertions.assertEquals(volume, MP3Pi.getInstance().getSongManager().getVolume());
    }

    @DisplayName("Set and get position")
    @Test
    public void testPositionSong() {
        ServerTest.testStartServer();

        float position = 0.5f;

        StaticHandler.getCore().getLogger().info("Adding debug song to play next");

        MP3Pi.getInstance().getSongManager().setNull();
        // Check if exception is thrown when no song is playing.

        Assertions.assertThrows(NoSongPlayingException.class, () -> MP3Pi.getInstance().getSongManager().setPosition(position).get());
        Assertions.assertThrows(NoSongPlayingException.class, () -> MP3Pi.getInstance().getSongManager().getPosition());


        MP3Pi.getInstance().getSongManager().clear();
        try {
            MP3Pi.getInstance().getSongManager().play(Constants.getDebugSong()).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            throw new AssertionError("Took more than 10 seconds", e);
        }

        Assertions.assertDoesNotThrow(() -> {
            MP3Pi.getInstance().getSongManager().setPosition(position).get(10, TimeUnit.SECONDS);
        });

        Assertions.assertEquals(position, MP3Pi.getInstance().getSongManager().getPosition());
    }

    @DisplayName("Song skip 1")
    @Test
    public void testSongSkipQueue() {
        ServerTest.testStartServer();

        StaticHandler.getCore().getLogger().info("Adding debug song list to play queue");

        List<Song> songList = new ArrayList<>();
        songList.add(Constants.getDebugSong());
        songList.add(Constants.getDebugSong());
        songList.add(Constants.getDebugSong());

        MP3Pi.getInstance().getSongManager().clear();
        MP3Pi.getInstance().getSongManager().addSongToQueue(songList);
        try {
            MP3Pi.getInstance().getSongManager().skip().get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            throw new AssertionError("Took more than 10 seconds", e);
        }
        Assertions.assertEquals(songList.size() - 1,MP3Pi.getInstance().getSongManager().getSongQueue().size());

    }


    private static int repetitions = 0;

    @DisplayName("Song skip specific amount")
    @Test
    @RepeatedTest(2)
    public void testSongSkipRepeatQueue() {
        repetitions++;
        ServerTest.testStartServer();

        StaticHandler.getCore().getLogger().info("Skipping " + repetitions + " times");

        List<Song> songList = new ArrayList<>();
        songList.add(Constants.getDebugSong());
        songList.add(Constants.getDebugSong());
        songList.add(Constants.getDebugSong());
        
        int skipAmount = repetitions;

        MP3Pi.getInstance().getSongManager().clear();
        MP3Pi.getInstance().getSongManager().addSongToQueue(songList);

        try {
            MP3Pi.getInstance().getSongManager().skip(skipAmount).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            throw new AssertionError("Took more than 10 seconds", e);
        }
        Assertions.assertEquals(songList.size() - skipAmount,MP3Pi.getInstance().getSongManager().getSongQueue().size());

    }


    @DisplayName("Song skip then rewind once")
    @Test
    public void testSongPreviousQueue() {
        if (MP3Pi.getInstance().getSongManager().isPlaying() && repetitions == 0)
            MP3Pi.getInstance().getSongManager().skip();

        ServerTest.testStartServer();

        StaticHandler.getCore().getLogger().info("Adding debug song list to play queue");

        List<Song> songList = new ArrayList<>();
        songList.add(Constants.getDebugSong());
        songList.add(Constants.getDebugSong());
        songList.add(Constants.getDebugSong());

        MP3Pi.getInstance().getSongManager().getSongHistory().clear();
        MP3Pi.getInstance().getSongManager().clear();
        MP3Pi.getInstance().getSongManager().addSongToQueue(songList);
        try {
            StaticHandler.getCore().getLogger().info("Song queue: " + MP3Pi.getInstance().getSongManager().getSongQueue());
            MP3Pi.getInstance().getSongManager().skip().get(10, TimeUnit.SECONDS);
            StaticHandler.getCore().getLogger().info("Song history: " + MP3Pi.getInstance().getSongManager().getSongHistory());
            MP3Pi.getInstance().getSongManager().previousSong().get(10, TimeUnit.SECONDS);
            MP3Pi.getInstance().getSongManager().pause().get(10, TimeUnit.SECONDS);

            StaticHandler.getCore().getLogger().info("Song queue finished: " + MP3Pi.getInstance().getSongManager().getSongQueue());
            StaticHandler.getCore().getLogger().info("Song history finished: " + MP3Pi.getInstance().getSongManager().getSongHistory());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            throw new AssertionError("Took more than 10 seconds", e);
        }
        // TODO: FIGURE OUT AND FIX INCONSISTENCY
        // - 1 since the song is currently playing when calling previousSong();
        Assertions.assertEquals(songList.size() - 1, MP3Pi.getInstance().getSongManager().getSongQueue().size());

    }


    private static int repetitions2 = 0;

    @DisplayName("Song skip then rewind specific amount")
    @Test
    @RepeatedTest(2)
    public void testSongSkipPreviousQueue() {

        if (MP3Pi.getInstance().getSongManager().isPlaying() && repetitions2 == 0)
            MP3Pi.getInstance().getSongManager().skip();

        repetitions2++;
        ServerTest.testStartServer();

        StaticHandler.getCore().getLogger().info("Skipping and rewinding " + repetitions2 + " times");

        List<Song> songList = new ArrayList<>();
        songList.add(Constants.getDebugSong());
        songList.add(Constants.getDebugSong());
        songList.add(Constants.getDebugSong());
        songList.add(Constants.getDebugSong());

        int skipAmount = repetitions2;



        MP3Pi.getInstance().getSongManager().clear();
        MP3Pi.getInstance().getSongManager().getSongHistory().clear();
        MP3Pi.getInstance().getSongManager().addSongToQueue(songList);

        try {
            MP3Pi.getInstance().getSongManager().skip(skipAmount).get(10, TimeUnit.SECONDS);
            StaticHandler.getCore().getLogger().info("Song history: " + MP3Pi.getInstance().getSongManager().getSongHistory());
            MP3Pi.getInstance().getSongManager().previousSong(skipAmount).get(10, TimeUnit.SECONDS);

            StaticHandler.getCore().getLogger().info("Song queue: " + MP3Pi.getInstance().getSongManager().getSongQueue());
            StaticHandler.getCore().getLogger().info("Song history finished: " + MP3Pi.getInstance().getSongManager().getSongHistory());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            throw new AssertionError("Took more than 10 seconds", e);
        }
        Assertions.assertTrue(MP3Pi.getInstance().getSongManager().getSongQueue().containsAll(songList));

    }
}
