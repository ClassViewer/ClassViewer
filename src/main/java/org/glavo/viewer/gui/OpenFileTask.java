package org.glavo.viewer.gui;

import org.glavo.viewer.gui.filetypes.FileType;

import java.net.URL;

public class OpenFileTask extends ViewerTask<ViewerTab> {
    private Viewer viewer;
    private FileType type;
    private URL url;

    public OpenFileTask(Viewer viewer, FileType type, URL url) {
        this.viewer = viewer;
        this.type = type;
        this.url = url;

        this.setOnFailed(ViewerAlert::logAndShowExceptionAlert);
    }

    @Override
    protected ViewerTab call() throws Exception {
        if (type != null) {
            return type.open(viewer, url);
        } else {
            for (FileType type : FileType.fileTypes) {
                if (type.accept(url)) {
                    ViewerTab ans = type.open(viewer, url);
                    RecentFiles.Instance.add(type, url);
                    return ans;
                }
            }
        }
        return null;
    }
}
