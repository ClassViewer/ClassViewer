package org.glavo.viewer.gui;

import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Tab;
import org.glavo.viewer.gui.filetypes.FileType;
import org.glavo.viewer.util.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpenFileTask extends ViewerTask<List<Tab>> {
    private Viewer viewer;
    private URL[] urls;

    public OpenFileTask(Viewer viewer, URL... urls) {
        this.viewer = viewer;
        this.urls = urls;

        this.setOnFailed(this::onFailed);
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

    private void onFailed(WorkerStateEvent event) {
        Throwable e = event.getSource().getException();
        Log.error(e);
        ViewerAlert.showExceptionAlert(e);
    }
}
