package org.glavo.viewer.gui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.glavo.viewer.util.FontUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ViewerAlert extends Alert {
    public static ViewerAlert exceptionAlert(Throwable ex) {
        ViewerAlert alert = new ViewerAlert(AlertType.ERROR);

        alert.setHeaderText(ex.toString());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");
        FontUtils.setUIFont(label);

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setFont(FontUtils.textFont);

        //textArea.setMaxWidth(Double.MAX_VALUE);
        //textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(alert.getWidth());
        expContent.setMinHeight(300);
        expContent.setPrefSize(alert.getWidth(), 200);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().expandedProperty().addListener( (obs, oldValue, newValue) -> {
            Platform.runLater( () -> {
                alert.getDialogPane().requestLayout();
                alert.getDialogPane().getScene().getWindow().sizeToScene();
            } );
        } );
        alert.getDialogPane().setExpandableContent(expContent);
        return alert;
    }

    public static void showExceptionAlert(Throwable ex) {
        exceptionAlert(ex).show();
    }

    public ViewerAlert(AlertType alertType) {
        super(alertType);
        FontUtils.setUIFont(this.getDialogPane());
    }

    public ViewerAlert(AlertType alertType, String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
        FontUtils.setUIFont(this.getDialogPane());
    }
}
