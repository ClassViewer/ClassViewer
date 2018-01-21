package org.glavo.viewer.gui;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.Log;

import java.awt.*;
import java.net.URI;

public class ViewerAboutDialog extends BorderPane {
    public static final String homeUrl = "https://github.com/Glavo/ClassViewer";

    public static void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        ViewerAboutDialog dialog = new ViewerAboutDialog(stage);
        Scene scene = new Scene(dialog, 300, 180);

        stage.setScene(scene);
        stage.setTitle("About");
        stage.show();
    }

    public ViewerAboutDialog(Stage stage) {
        ImageView image = ImageUtils.createImageView("/icons/spy128.png");
        this.setCenter(image);
        this.setOnMouseClicked(e -> stage.close());

        image.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Desktop.getDesktop().browse(URI.create(homeUrl));
                    return null;
                }
            };
            task.setOnFailed(e -> {
                Throwable exception = e.getSource().getException();
                if (exception != null) {
                    Log.error(exception);
                    ViewerAlert.showExceptionAlert(exception);
                }
            });

            new Thread(task).start();
        });

    }
}
