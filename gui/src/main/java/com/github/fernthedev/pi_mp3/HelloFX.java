package com.github.fernthedev.pi_mp3;

import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.module.Module;
import com.github.fernthedev.pi_mp3.core.MP3Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class HelloFX extends Application {

    private Thread uiThread;
    private static String[] args;
    private StackPane moduleList;


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

    }

    @Override
    public void start(Stage stage) {

//        GUIModule guiModule = new GUIModule();
//        ModuleHandler.registerModule(guiModule);


        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

        StackPane stackPane = new StackPane(l);
        // Wait for the server to finish startup
        new Thread(() -> {
            try {
                while (MP3Server.getServer() == null || MP3Pi.getInstance() == null || !MP3Server.getInstance().isStarted()) Thread.sleep(1); // Wait for server to init
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            showModules(stage, l, stackPane);
        }).start();

        Scene scene = new Scene(stackPane, 640, 480);

        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            if (event.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
                MP3Server.getServer().shutdownServer();
                System.exit(0);
            }
        });
    }

    private void showModules(Stage stage, Label l, StackPane pane) {
        Label moduleListLabel = new Label("Server loaded modules: ");
        double y = l.getTranslateY() + 20;
        moduleListLabel.setTranslateY(y);
        moduleList = new StackPane(moduleListLabel);


        for (Module module : MP3Pi.getInstance().getModuleHandler().getModuleList()) {
            y += 20;

            Label modLabel = new Label(module.getName() + " " + module.getDescription().toString());
            modLabel.setTranslateY(y);
            moduleList.getChildren().add(modLabel);
        }



        runOnUIThread(() -> {
            pane.getChildren().add(moduleList);
            l.setText(l.getText() + " Server running status: " + MP3Server.getServer().isRunning());
//            Scene scene = new Scene(new StackPane(l, pane), 640, 480);
//
//            stage.setScene(scene);
            stage.show();
        });
    }

    /**
     * MUST USE FOR UPDATING THE UI
     *
     * JAVAFX CONTAINS A UI THREAD WHICH
     * MUST BE USED FOR MANIPULATING THE UI
     * @param runnable
     */
    public static void runOnUIThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }


    public static void main(String[] args) {
        HelloFX.args = args;
        Thread thread = new Thread(() -> MP3Server.start(args, new TestModule()), "ServerThread");

        thread.start();
        launch();
    }


}