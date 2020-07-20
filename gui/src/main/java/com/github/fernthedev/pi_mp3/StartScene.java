package com.github.fernthedev.pi_mp3;

import com.github.fernthedev.pi_mp3.api.JavaFXButton;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.UIJavaFXScene;
import com.github.fernthedev.modules.Module;
import com.github.fernthedev.pi_mp3.api.ui.Position;
import com.github.fernthedev.pi_mp3.api.ui.UIButton;
import com.github.fernthedev.pi_mp3.api.ui.UIElement;
import com.github.fernthedev.pi_mp3.api.ui.UIInterface;
import com.github.fernthedev.pi_mp3.core.MP3Server;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class StartScene extends UIJavaFXScene {


    /**
     * Creates a Scene for a specific root Node with a specific size.
     *
     * @param width  The width of the scene
     * @param height The height of the scene
     * @throws NullPointerException if root is null
     */
    public StartScene(UIInterface uiInterface, StackPane stackPane, double width, double height) {
        super(stackPane, width, height, uiInterface);

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

        JavaFXButton button = (JavaFXButton) getUIFactory().createButton(new Position(l.getTranslateX(), l.getTranslateY() - 80));


        String defaultText = "Some text";
        button.setText(defaultText);

        button.addButtonListener((ui, event) -> ui.runOnUIThread((Callable<Void>) () -> {
            System.out.println("SOME CLICK " + event.name());
            if (event == UIButton.ButtonEvent.HOVER) button.setText("Some pretty cool text");
            else if (event == UIButton.ButtonEvent.SELECT) button.setText("Easter egg?");
            else if (event == UIButton.ButtonEvent.CLICK) button.setText("Fern is god");
            else button.setText(defaultText);
            return null;
        }));


        addElement(button);

        stackPane.getChildren().add(l);
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


        HelloFX.getInstance().runOnUIThread(() -> {
            double y = l.getTranslateY() + 20;
            moduleListLabel.setTranslateY(y);
            addElement(moduleListLabel);


            for (Module module : MP3Pi.getInstance().getModuleHandler().getModuleList()) {
                y += 20;

                Label modLabel = new Label(module.getName() + " " + module.getDescription().toString());
                modLabel.setTranslateY(y);
                addElement(modLabel);
            }
            l.setText(l.getText() + " Server running status: " + MP3Server.getServer().isRunning());
//            Scene scene = new Scene(new StackPane(l, pane), 640, 480);
//
//            stage.setScene(scene);
            return null;
        });
    }


    @Override
    public String getName() {
        return "StartScreen";
    }

    /**
     * Return a list of UI
     * elements in the
     * current UI
     *
     * @return a copy of the list
     */
    @Override
    public List<UIElement> getUIObjects() {
        return getPane().getChildren().parallelStream()
                .filter(node -> node instanceof UIElement)
                .map(node -> (UIElement) node)
                .collect(Collectors.toList());
    }

}
