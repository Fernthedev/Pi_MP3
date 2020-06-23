package com.github.fernthedev.pi_mp3;

import com.github.fernthedev.lightchat.core.ColorCode;
import com.github.fernthedev.pi_mp3.api.JavaFXFactory;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.UIJavaFXScene;
import com.github.fernthedev.pi_mp3.api.ui.UIFactory;
import com.github.fernthedev.pi_mp3.api.ui.UIInterface;
import com.github.fernthedev.pi_mp3.api.ui.UIScreen;
import com.github.fernthedev.pi_mp3.core.MP3Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class HelloFX extends Application implements UIInterface {

    private Thread uiThread;


    private UIJavaFXScene uiJavaFXScene;

    @Getter
    private Stage stage;

    private final JavaFXFactory javaFXFactory = new JavaFXFactory(this);

    @Getter
    private static HelloFX instance;

    /**
     * The application initialization method. This method is called immediately
     * after the Application class is loaded and constructed. An application may
     * override this method to perform initialization prior to the actual starting
     * of the application.
     *
     * <p>
     * The implementation of this method provided by the Application class does nothing.
     * </p>
     *
     * <p>
     * NOTE: This method is not called on the JavaFX Application Thread. An
     * application must not construct a Scene or a Stage in this
     * method.
     * An application may construct other JavaFX objects in this method.
     * </p>
     *
     * @throws Exception if something goes wrong
     */
    @Override
    public void init() throws Exception {
        uiThread = Thread.currentThread();

        HelloFX.instance = this;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        stage.setTitle("Pi MP3 JavaFX UI");

//        GUIModule guiModule = new GUIModule();
//        ModuleHandler.registerModule(guiModule);
        System.out.println(ColorCode.BLUE + "Starting GUI");


        instance.runOnUIThread(() -> {
            instance.setCurrentScreen(new StartScene(this, new StackPane(), 640, 480));
            return null;
        });

        stage.show();
        stage.setOnCloseRequest(event -> {
            if (event.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
                close();
            }
        });
    }


    /**
     * MUST USE FOR UPDATING THE UI
     *
     * JAVAFX CONTAINS A UI THREAD WHICH
     * MUST BE USED FOR MANIPULATING THE UI
     * @param callable
     */
    public <V> CompletableFuture<V> runOnUIThread(Callable<V> callable) {
        CompletableFuture<V> completableFuture = new CompletableFuture<>();

        if (Platform.isFxApplicationThread()) {
            try {
                completableFuture.complete(callable.call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    completableFuture.complete(callable.call());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        return completableFuture;
    }

    /**
     * Returns the UI factory of UI
     * It should contain the default
     * UI element styles and settings
     * used throughout the interface.
     *
     * @return ui factory
     */
    @Override
    public UIFactory getUIFactory() {

        return javaFXFactory;
    }

    private static void close() {
        try {
            Thread shutdownJavaFX = new Thread(Platform::exit);

            shutdownJavaFX.setDaemon(true);
            shutdownJavaFX.start();

            MP3Server.getServer().shutdownServer();

            shutdownJavaFX.join();

            System.exit(0);
        } catch (IllegalStateException e) {
            MP3Pi.getInstance().getLogger().warn("Message when shutting down: " + e.getMessage());

            if (MP3Pi.getInstance().isDebug()) e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {

        // Checks if the server is shutdown
        Thread serverWatchThread = new Thread(() -> {
            while (MP3Server.getServer().isRunning()) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }

            close();
        }, "ServerWatchThread");

        Thread thread = new Thread(() -> {
            try {
                System.out.println("Starting MP3 server");
                MP3Server.start(args, new TestModule());
                Validate.notNull(instance);
                MP3Pi.getInstance().registerUIPlatform(instance);
                serverWatchThread.setDaemon(true);
                serverWatchThread.start();

            } catch (Exception e) {
                e.printStackTrace();
                close();
            }
        }, "ServerThread");

        thread.start();
        launch();
    }


    @Override
    public String getName() {
        return "JavaFX UI";
    }

    /**
     * Gets the current screen
     *
     * @return screen
     */
    @Override
    public UIScreen getCurrentScreen() {
        return uiJavaFXScene;
    }

    /**
     * Sets the current screen
     *
     * @param uiScreen
     * @return
     */
    @Override
    public CompletableFuture<UIScreen> setCurrentScreen(UIScreen uiScreen) {
        this.uiJavaFXScene = (UIJavaFXScene) uiScreen;
        
        return runOnUIThread(() -> {
            stage.setScene(uiJavaFXScene);
            return uiJavaFXScene;
        });
    }

    /**
     * Returns true if currently running on the UI thread
     *
     * @return
     */
    @Override
    public boolean isUIThread() {
        return Platform.isFxApplicationThread();
    }
}