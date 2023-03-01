package gui;

import java.util.concurrent.CompletableFuture;

public final class AsyncTaskExecutor {
    public static void runUIBlocking(String label, Runnable body) {
        MainWindow.getInstance().showProgressBar(label);
        CompletableFuture.runAsync(() -> {
            body.run();
            MainWindow.getInstance().closeProgressBar();
        });
    }
}
