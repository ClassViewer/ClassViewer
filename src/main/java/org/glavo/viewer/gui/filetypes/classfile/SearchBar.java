package org.glavo.viewer.gui.filetypes.classfile;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import org.glavo.viewer.classfile.ClassFile;
import org.glavo.viewer.classfile.ClassFileComponent;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SearchBar extends ToolBar {
    public enum SearchRange {
        ALL {
            @Override
            public ClassFileComponent getRange(ClassFile file) {
                return file;
            }

            @Override
            public String toString() {
                return "ClassFile";
            }
        }, InConstantPool {
            @Override
            public ClassFileComponent getRange(ClassFile file) {
                return file.getConstantPool();
            }

            @Override
            public String toString() {
                return "ConstantPool";
            }
        },
        InInterfaces {
            @Override
            public ClassFileComponent getRange(ClassFile file) {
                return (ClassFileComponent) file.get("interfaces");
            }

            @Override
            public String toString() {
                return "Interfaces";
            }
        },
        InFields {
            @Override
            public ClassFileComponent getRange(ClassFile file) {
                return (ClassFileComponent) file.get("fields");
            }

            @Override
            public String toString() {
                return "Fields";
            }
        },
        InMethods {
            @Override
            public ClassFileComponent getRange(ClassFile file) {
                return (ClassFileComponent) file.get("methods");
            }

            @Override
            public String toString() {
                return "Methods";
            }
        };

        public abstract ClassFileComponent getRange(ClassFile file);
    }

    public enum Searcher {
        UInt("UInt", new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object == null ? "0" : Integer.toUnsignedString(object);
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseUnsignedInt(string.trim());
                } catch (Exception e) {
                    return null;
                }
            }
        }) {
            @Override
            public List<ClassFileComponent> search(SearchBar bar) {
                ArrayList<ClassFileComponent> ans = new ArrayList<>();
                Integer value = (Integer) converter.fromString(bar.textField.getText());
                if (value != null) {
                    ClassFileComponent component = bar.searchRangeBox.getSelectionModel().getSelectedItem().getRange(
                            (ClassFile) bar.pane.getTree().getRoot()
                    );
                    component.walkComponentTree(c -> {
                        if (c instanceof org.glavo.viewer.classfile.datatype.UInt
                                && ((org.glavo.viewer.classfile.datatype.UInt) c).getIntValue() == value) {
                            ans.add(c);
                        }
                    });
                    return ans;
                } else {
                    return null;
                }
            }
        };
        StringConverter<?> converter;
        String name;

        Searcher(String name, StringConverter<?> converter) {
            this.name = name;
            this.converter = converter;
        }

        public abstract List<ClassFileComponent> search(SearchBar bar);

        @Override
        public String toString() {
            return name;
        }
    }

    private static final Pattern textFill = Pattern.compile("-fx-text-fill:[^;]*;");

    private ParsedViewerPane pane;

    private ListIterator<ClassFileComponent> iterator = null;
    private ClassFileComponent selected = null;

    private ComboBox<Searcher> searcherBox = new ComboBox<>(FXCollections.observableArrayList(Searcher.values()));
    private TextField textField = new TextField();
    private Button searchButton = new Button(null, new ImageView(ImageUtils.searchImage));
    private ComboBox<SearchRange> searchRangeBox = new ComboBox<>(FXCollections.observableArrayList(SearchRange.values()));
    private Button previousButton = new Button(null, new ImageView(ImageUtils.previousOccurenceImage));
    private Button nextButton = new Button(null, new ImageView(ImageUtils.nextOccurenceImage));
    private Label count = new Label("");

    public SearchBar(ParsedViewerPane pane) {
        this.pane = pane;

        this.setStyle(this.getStyle() == null
                ? "-fx-background-color: transparent;" : this.getStyle() + "-fx-background-color: transparent;");

        this.searcherBox.getSelectionModel().selectedItemProperty().addListener((list, oldValue, newValue) -> {
            textField.setTextFormatter(new TextFormatter<>(newValue.converter));
        });

        this.searchRangeBox.getSelectionModel().select(0);
        this.searcherBox.getSelectionModel().select(0);

        this.setFont();

        this.textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.ENTER).match(event)) {
                search(null);
            }
        });
        this.searchButton.setOnAction(this::search);
        this.previousButton.setOnAction(event -> {
            if (iterator == null) {
                return;
            }
            ClassFileComponent component;
            if (iterator.hasPrevious()) {
                component = iterator.previous();
                if (component == selected) {
                    if (iterator.hasPrevious()) {
                        component = iterator.previous();
                    }
                }
                this.selected = component;
            }
            pane.getTree().getSelectionModel().select(selected);
        });
        this.nextButton.setOnAction(event -> {
            if (iterator == null) {
                return;
            }
            ClassFileComponent component;
            if (iterator.hasNext()) {
                component = iterator.next();
                if (component == selected) {
                    if (iterator.hasNext()) {
                        component = iterator.next();
                    }
                }
                this.selected = component;
            }
            pane.getTree().getSelectionModel().select(selected);
        });

        this.getItems().addAll(
                new Label("type:"),
                searcherBox,
                textField,
                searchButton,
                new Label("in"),
                searchRangeBox,
                previousButton,
                nextButton,
                count
        );
    }

    public void search(ActionEvent event) {
        List<ClassFileComponent> ans = searcherBox.getSelectionModel().getSelectedItem().search(this);
        if (ans == null) {
            count.setText("No matches");
            return;
        }

        switch (ans.size()) {
            case 0:
                count.setText("No matches");
                this.iterator = null;
                return;
            case 1:
                count.setText("One match");
                break;
            case 2:
                count.setText("Two matches");
            default:
                count.setText(ans.size() + " matches");
        }

        this.iterator = ans.listIterator();
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
        FontUtils.setUIFont(searchRangeBox);
        previousButton.setFont(FontUtils.uiFont);
        nextButton.setFont(FontUtils.uiFont);
    }
}
