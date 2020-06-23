package com.github.fernthedev.pi_mp3.api;

import com.github.fernthedev.pi_mp3.api.ui.*;
import javafx.application.ConditionalFeature;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;

import java.util.List;
import java.util.stream.Collectors;

public abstract class UIJavaFXScene extends Scene implements UIScreen {

    private final UIInterface uiInterface;
    private JavaFXFactory javaFXFactory;

    /**
     * Creates a Scene for a specific root Node.
     *
     * @param root The root node of the scene graph
     * @throws NullPointerException if root is null
     */
    public UIJavaFXScene(Parent root, UIInterface uiInterface) {
        super(root);
        this.uiInterface = uiInterface;
        init();
    }

    /**
     * Creates a Scene for a specific root Node with a specific size.
     *
     * @param root   The root node of the scene graph
     * @param width  The width of the scene
     * @param height The height of the scene
     * @throws NullPointerException if root is null
     */
    public UIJavaFXScene(Parent root, double width, double height, UIInterface uiInterface) {
        super(root, width, height);
        this.uiInterface = uiInterface;
        init();
    }

    /**
     * Creates a Scene for a specific root Node with a fill.
     *
     * @param root The parent
     * @param fill The fill
     * @throws NullPointerException if root is null
     */
    public UIJavaFXScene(Parent root, Paint fill, UIInterface uiInterface) {
        super(root, fill);
        this.uiInterface = uiInterface;
        init();
    }

    /**
     * Creates a Scene for a specific root Node with a specific size and fill.
     *
     * @param root   The root node of the scene graph
     * @param width  The width of the scene
     * @param height The height of the scene
     * @param fill   The fill
     * @throws NullPointerException if root is null
     */
    public UIJavaFXScene(Parent root, double width, double height, Paint fill, UIInterface uiInterface) {
        super(root, width, height, fill);
        this.uiInterface = uiInterface;
        init();
    }

    /**
     * Constructs a scene consisting of a root, with a dimension of width and
     * height, and specifies whether a depth buffer is created for this scene.
     * <p>
     * A scene with only 2D shapes and without any 3D transforms does not need a
     * depth buffer. A scene containing 3D shapes or 2D shapes with 3D
     * transforms may use depth buffer support for proper depth sorted
     * rendering; to avoid depth fighting (also known as Z fighting), disable
     * depth testing on 2D shapes that have no 3D transforms. See
     * {@link Node#depthTestProperty() depthTest} for more information.
     *
     * @param root        The root node of the scene graph
     * @param width       The width of the scene
     * @param height      The height of the scene
     * @param depthBuffer The depth buffer flag
     *                    <p>
     *                    The depthBuffer flag is a conditional feature and its default value is
     *                    false. See
     *                    {@link ConditionalFeature#SCENE3D ConditionalFeature.SCENE3D}
     *                    for more information.
     * @throws NullPointerException if root is null
     * @see Node#setDepthTest(DepthTest)
     */
    public UIJavaFXScene(Parent root, double width, double height, boolean depthBuffer, UIInterface uiInterface) {
        super(root, width, height, depthBuffer);
        this.uiInterface = uiInterface;
        init();
    }

    /**
     * Constructs a scene consisting of a root, with a dimension of width and
     * height, specifies whether a depth buffer is created for this scene and
     * specifies whether scene anti-aliasing is requested.
     * <p>
     * A scene with only 2D shapes and without any 3D transforms does not need a
     * depth buffer nor scene anti-aliasing support. A scene containing 3D
     * shapes or 2D shapes with 3D transforms may use depth buffer support for
     * proper depth sorted rendering; to avoid depth fighting (also known as Z
     * fighting), disable depth testing on 2D shapes that have no 3D transforms.
     * See {@link Node#depthTestProperty() depthTest} for more information. A
     * scene with 3D shapes may enable scene anti-aliasing to improve its
     * rendering quality.
     *
     * @param root         The root node of the scene graph
     * @param width        The width of the scene
     * @param height       The height of the scene
     * @param depthBuffer  The depth buffer flag
     * @param antiAliasing The scene anti-aliasing attribute. A value of
     *                     {@code null} is treated as DISABLED.
     *                     <p>
     *                     The depthBuffer and antiAliasing are conditional features. With the
     *                     respective default values of: false and {@code SceneAntialiasing.DISABLED}. See
     *                     {@link ConditionalFeature#SCENE3D ConditionalFeature.SCENE3D}
     *                     for more information.
     * @throws NullPointerException if root is null
     * @see Node#setDepthTest(DepthTest)
     * @since JavaFX 8.0
     */
    public UIJavaFXScene(Parent root, double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing, UIInterface uiInterface) {
        super(root, width, height, depthBuffer, antiAliasing);
        this.uiInterface = uiInterface;
        init();
    }

    protected void init() {
       javaFXFactory = new JavaFXFactory(uiInterface);
    }

    public Pane getPane() {
        return (Pane) rootProperty().get();
    }

    /**
     * {@link #getUIObjects()} but scoped to UIButtons
     *
     * @return a copy of the list
     */
    @Override
    public List<UIButton> getUIButtons() {
        return getUIObjects().parallelStream().filter(e -> e instanceof UIButton).map(e -> (UIButton) e).collect(Collectors.toList());
    }


    @Override
    public UIFactory getUIFactory() {
        return javaFXFactory;
    }

    protected Node validateIsNode(UIElement element) {
        if ((element instanceof JavaFXElement<?>)) return ((JavaFXElement<?>) element).getNode();
        if (!(element instanceof Node)) throw new IllegalArgumentException("UIElement " + element + " must be instance of Node. You may use getUIFactory() to create the element");

        return (Node) element;
    }


    /**
     * Adds an element to
     * the screen
     *
     * @param uiElement
     */
    @Override
    public void addElement(UIElement uiElement) {
        getPane().getChildren().add(validateIsNode(uiElement));
    }


    /**
     * Adds an element to
     * the screen
     *
     * @param node node
     */
    public void addElement(Node node) {
        getPane().getChildren().add(node);
    }

    /**
     * Adds an element to
     * the screen
     *
     * @param uiElement
     */
    @Override
    public void removeElement(UIElement uiElement) {
        getPane().getChildren().remove(validateIsNode(uiElement));
    }
}
