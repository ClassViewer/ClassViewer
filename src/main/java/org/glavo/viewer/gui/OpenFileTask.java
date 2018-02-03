package org.glavo.viewer.gui;

import javafx.scene.control.Tab;
import org.glavo.viewer.gui.filetypes.FileType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpenFileTask extends ViewerTask<List<Tab>> {
    private Viewer viewer;
    private URL[] urls;

    public OpenFileTask(Viewer viewer, URL... urls) {
        this.viewer = viewer;
        this.urls = urls;

        this.setOnFailed(ViewerAlert::logAndShowExceptionAlert);
    }

    public void runInNewThread() {
        new Thread(this).start();
    }

    @Override
    protected List<Tab> call() throws Exception {
        ArrayList<Tab> ans = new ArrayList<>();

        for (URL url : urls) {
            for (FileType type : FileType.fileTypes) {
                if (type.accept(url)) {
                    ans.add(type.open(viewer, url));
                    RecentFiles.Instance.add(type, url);
                }
            }
        }

        return ans;
    }
}
