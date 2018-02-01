package org.glavo.viewer.gui.filetypes.classfile;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.ImageUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SearchBar extends ToolBar {
    private static final Pattern textFill = Pattern.compile("-fx-text-fill:[^;]*;");

    private ParsedViewerPane pane;

    private TextField textField = new TextField();
    private Button searchButton = new Button(null, new ImageView(ImageUtils.searchImage));
    private ComboBox searchInBox;
    private Button previousButton = new Button(null, new ImageView(ImageUtils.previousOccurenceImage));
    private Button nextButton = new Button(null, new ImageView(ImageUtils.nextOccurenceImage));;

    public SearchBar(ParsedViewerPane pane) {
        this.pane = pane;
        this.searchInBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        "in constant pool"
                ));

        this.setFont();

        this.getItems().addAll(
                textField,
                searchButton,
                searchInBox,
                previousButton,
                nextButton
        );
    }

    public void setColor(String color) {
        String style = textField.getStyle() == null ? "" : textField.getStyle();
        Matcher m = textFill.matcher(style);
        if (!m.find()) {
            style += "-fx-text-fill: " + color + ";";
        } else {
            style = m.replaceAll("-fx-text-fill: " + color + ";");
        }
        textField.setStyle(style);
    }

    public void setFont() {
        textField.setFont(FontUtils.textFont);
        searchButton.setFont(FontUtils.uiFont);
        FontUtils.setUIFont(searchInBox);
        previousButton.setFont(FontUtils.uiFont);
        nextButton.setFont(FontUtils.uiFont);
    }
}
