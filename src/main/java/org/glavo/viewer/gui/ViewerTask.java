package org.glavo.viewer.gui;

import javafx.concurrent.Task;

import java.util.function.Consumer;

public abstract class ViewerTask<T> extends Task<T> {
    @SuppressWarnings("unchecked")
    public void setOnSucceeded(Consumer<T> callback) {
        super.setOnSucceeded(
                e -> callback.accept((T) e.getSource().getValue()));
    }

    public void setOnFailed(Consumer<Throwable> callback) {
        super.setOnFailed(event -> {
            Throwable err = event.getSource().getException();
            callback.accept(err);
        });
    }

    public void startInNewThread() {
        new Thread(this).start();
    }
}
