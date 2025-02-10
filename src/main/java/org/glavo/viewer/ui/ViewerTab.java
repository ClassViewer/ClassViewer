package org.glavo.viewer.ui;

import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import org.glavo.viewer.util.UrlUtils;

import java.net.URL;

public class ViewerTab extends Tab {
    public URL url = null;
    public Runnable showOrHideSearchBar = null;

    public static ViewerTab create(String text) {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxWidth(75);
        progressIndicator.setMaxHeight(75);
        return new ViewerTab(text, new BorderPane(progressIndicator));
    }

    public static ViewerTab create(URL url) {
        ViewerTab tab = create(UrlUtils.getFileName(url));
        tab.url = url;
        return tab;
    }

    public ViewerTab() {
        this(null, null);
    }

    public ViewerTab(String text) {
        this(text, null);
    }

    public ViewerTab(String text, Node content) {
        super(text, content);
    }

    public void showSearchBar() {
        if(showOrHideSearchBar != null) {
            showOrHideSearchBar.run();
        }
    }

    public URL getUrl() {
        return url;
    }
}
