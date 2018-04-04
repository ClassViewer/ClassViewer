package org.glavo.viewer.gui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.ImageUtils;

public class ViewerTitleBar extends ToolBar {

    public static Image logo = ImageUtils.loadImage("/icons/spy24.png");

    private Viewer viewer;

    private ImageView logoView = new ImageView(logo);
    private Label titleLabel = new Label(Viewer.TITLE);
    private Button minimizeButton = new Button();
    private Button maximizeButton = new Button();
    private Button restoreButton = new Button();
    private Button closeButton = new Button();


    private double xOffset = 0;
    private double yOffset = 0;

    public ViewerTitleBar(Viewer viewer) {
        this.viewer = viewer;

        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                viewer.getStage().setMaximized(!viewer.getStage().isMaximized());
            }
        });

        this.setOnMousePressed((event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        this.setOnMouseDragged((event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                viewer.getStage().setX(event.getScreenX() - xOffset);
                viewer.getStage().setY(event.getScreenY() - yOffset);
            }
        });

        titleLabel.textProperty().bind(viewer.getStage().titleProperty());
        viewer.getStage().maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.getItems().set(5, restoreButton);
            } else {
                this.getItems().set(5, maximizeButton);
            }
        });

        minimizeButton.setGraphic(new ImageView(ImageUtils.minimizeImage));
        maximizeButton.setGraphic(new ImageView(ImageUtils.maximizeImage));
        restoreButton.setGraphic(new ImageView(ImageUtils.restoreImage));
        closeButton.setGraphic(new ImageView(ImageUtils.closeImage));

        minimizeButton.setOnAction(event -> viewer.getStage().setIconified(true));
        closeButton.setOnAction(event -> viewer.getStage().close());

        maximizeButton.setOnAction(event -> viewer.getStage().setMaximized(true));
        restoreButton.setOnAction(event -> viewer.getStage().setMaximized(false));

        titleLabel.setFont(FontUtils.uiFont);

        Text noop1 = new Text(" ");
        HBox noop2 = new HBox();

        HBox.setHgrow(noop1, Priority.NEVER);
        HBox.setHgrow(noop2, Priority.ALWAYS);

        HBox.setHgrow(logoView, Priority.NEVER);
        HBox.setHgrow(minimizeButton, Priority.NEVER);
        HBox.setHgrow(maximizeButton, Priority.NEVER);
        HBox.setHgrow(restoreButton, Priority.NEVER);
        HBox.setHgrow(closeButton, Priority.NEVER);

        this.getItems().addAll(logoView, noop1, titleLabel, noop2, minimizeButton, maximizeButton, closeButton);
    }
}