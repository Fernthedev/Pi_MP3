package com.github.fernthedev.pi_mp3;

import com.github.fernthedev.pi_mp3.core.MP3Server;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {
//        GUIModule guiModule = new GUIModule();
//        ModuleHandler.registerModule(guiModule);

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

        Scene scene = new Scene(new StackPane(l), 640, 480);

        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            if (event.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
                MP3Server.getServer().shutdownServer();
                System.exit(0);
            }
        });
    }

    public static void launchWindow() {
        launch();
    }

    public static void main(String[] args) {
        MP3Server.start(args, new GUIModule());
    }


}