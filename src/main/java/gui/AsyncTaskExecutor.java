package gui;

import java.util.concurrent.CompletableFuture;

public final class AsyncTaskExecutor {
	public static void runUIBlocking(String label, Runnable body) {
		MainWindow.getInstance().showProgressBar(label);
		CompletableFuture.runAsync(() -> {
			body.run();
			MainWindow.getInstance().closeProgressBar();
		}).exceptionally(t -> {
			t.printStackTrace();
			MainWindow.getInstance().closeProgressBar();
			PopUpDialog.getInstance().show("Task Executor error.",
					"Failed to run task: " + label + "\r\n" + t.getMessage());
			return null;
		});
	}

	public static void runUIBlocking(String label, Runnable body, Runnable complete) {
		MainWindow.getInstance().showProgressBar(label);
		CompletableFuture.runAsync(() -> {
			body.run();
			MainWindow.getInstance().closeProgressBar();
		}).exceptionally(t -> {
			t.printStackTrace();
			MainWindow.getInstance().closeProgressBar();
			PopUpDialog.getInstance().show("Task Executor error.",
					"Failed to run task: " + label + "\r\n" + t.getMessage());
			return null;
		}).thenRun((() -> {
			complete.run();
			MainWindow.getInstance().closeProgressBar();
		}));
	}
}