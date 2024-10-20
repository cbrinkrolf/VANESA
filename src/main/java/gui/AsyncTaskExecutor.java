package gui;

import java.util.concurrent.CompletableFuture;

public final class AsyncTaskExecutor {
	public static void runUIBlocking(String label, Runnable body) {
		MainWindow.getInstance().showProgressBar(label);
		CompletableFuture.runAsync(() -> {
			body.run();
		}).exceptionally(t -> {
			t.printStackTrace();
			PopUpDialog.getInstance().show("Task Executor error.",
					"Failed to run task: " + label + "\r\n" + t.getMessage());
			return null;
		});
		MainWindow.getInstance().closeProgressBar();
	}
}