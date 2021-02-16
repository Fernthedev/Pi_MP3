package com.github.fernthedev.pi_mp3;

import com.github.fernthedev.modules.Module;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.core.MP3Server;
import com.github.fernthedev.pi_mp3.ui.PiButton;
import com.github.fernthedev.pi_mp3.ui.UIFactory;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class StartScene extends Scene {
    private final Pane stackPane;

    /**
     * Creates a Scene for a specific root Node with a specific size.
     *
     * @param width  The width of the scene
     * @param height The height of the scene
     * @throws NullPointerException if root is null
     */
    public StartScene(Pane stackPane, double width, double height) {
        super(stackPane, width, height);

        this.stackPane = stackPane;

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

        PiButton button = new PiButton(UIFactory.getUiInterface());

        button.setTranslateX(l.getTranslateX());
        button.setTranslateY(l.getTranslateY() - 80);


        String defaultText = "Some text";
        button.setText(defaultText);

        button.addButtonListener((ui, event) -> ui.runOnUIThread((Callable<Void>) () -> {
            if (event == PiButton.ButtonEvent.HOVER) button.setText("Some pretty cool text");
            else if (event == PiButton.ButtonEvent.SELECT) button.setText("Easter egg?");
            else if (event == PiButton.ButtonEvent.CLICK) button.setText("Fern is god");
            else button.setText(defaultText);
            return null;
        }));


        UIFactory.getUiInterface().runOnUIThread(() -> {
            stackPane.getChildren().add(l);

            stackPane.getChildren().add(button);
        });

        // Wait for the server to finish startup
        new Thread(() -> {
            try {
                while (MP3Server.getServer() == null || MP3Pi.getInstance() == null || !MP3Server.getInstance().isStarted()) Thread.sleep(1); // Wait for server to init
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }

            showModules(l);
        }).start();
    }


    private void showModules(Label l) {
        Label moduleListLabel = new Label("Server loaded modules: ");

        double y = l.getTranslateY() + 20;
        moduleListLabel.setTranslateY(y);

        List<Label> labels = new ArrayList<>();

        for (Module module : MP3Pi.getInstance().getModuleHandler().getModuleList()) {
            y += 20;

            Label modLabel = new Label(module.getName() + " " + module.getDescription().toString());
            modLabel.setTranslateY(y);
            labels.add(modLabel);
        }


        HelloFX.getInstance().runOnUIThread(() -> {
            l.setText(l.getText() + " Server running status: " + MP3Server.getServer().isRunning());

            stackPane.getChildren().add(moduleListLabel);
            stackPane.getChildren().addAll(labels);
        });
    }


    public String getName() {
        return "StartScreen";
    }

}
