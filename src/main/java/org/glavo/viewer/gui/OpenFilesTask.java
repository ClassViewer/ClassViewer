package org.glavo.viewer.gui;

import org.glavo.viewer.gui.filetypes.FileType;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenFilesTask extends ViewerTask<List<ViewerTab>> {
    private Viewer viewer;
    private List<URL> urls;

    public OpenFilesTask(Viewer viewer, URL... urls) {
        this(viewer, Arrays.asList(urls));
    }

    public OpenFilesTask(Viewer viewer, List<URL> urls) {
        this.viewer = viewer;
        this.urls = urls;

        this.setOnFailed(ViewerAlert::logAndShowExceptionAlert);
    }

    @Override
    protected List<ViewerTab> call() throws Exception {
        ArrayList<ViewerTab> ans = new ArrayList<>();

        tag:
        for (URL url : urls) {
            for (FileType type : FileType.fileTypes) {
                try {
                    if (type.accept(url)) {
                        ans.add(type.open(viewer, url));
                        RecentFiles.Instance.add(type, url);
                        continue tag;
                    }
                } catch (Exception ex) {
                    ViewerAlert.logAndShowExceptionAlert(ex);
                }
            }
        }
        return ans;
    }
}
