package org.glavo.viewer.gui;

import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.UrlUtils;

import java.net.URL;

public class ViewerTab extends Tab {
    public class UserData {
        public URL url = null;
        public Runnable showOrHideSearchBar = null;


        public ViewerTab getTab() {
            return ViewerTab.this;
        }
    }

    public static ViewerTab create(String text) {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxWidth(75);
        progressIndicator.setMaxHeight(75);
        return new ViewerTab(text, new BorderPane(progressIndicator));
    }

    public static ViewerTab create(URL url) {
        ViewerTab tab = create(UrlUtils.getFileName(url));
        tab.getUserData().url = url;
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
        this.setStyle(FontUtils.setUIFont(this.getStyle()));
        this.setUserData(new UserData());
    }

    public void showSearchBar() {
        UserData data = getUserData();
        if(data.showOrHideSearchBar != null) {
            data.showOrHideSearchBar.run();
        }
    }

    @Override
    public UserData getUserData() {
        return (UserData) super.getUserData();
    }

    public URL getUrl() {
        return getUserData().url;
    }
}
