package gui;

import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class MyPopUp {

	private MainWindow w;
	private float opacity = 0.9f;
	private int time = 5000;

	ArrayList<Integer> list;

	public MyPopUp() {
		w = MainWindowSingleton.getInstance();
		list = new ArrayList<Integer>();
		for (int i = 0; i < 100; i++) {
			list.add(0);
		}
	}

	public void show(String title, String message) {
		JOptionPane pane = new JOptionPane(message, JOptionPane.NO_OPTION);
		JDialog dialog = pane.createDialog(null, title);
		dialog.setModal(false);

		dialog.setAlwaysOnTop(true);
		dialog.setOpacity(opacity);

		int x = w.getBounds().x + w.getBounds().width;
		int y = w.getBounds().y + w.getBounds().height;
		int pos = this.requestPosition();
		dialog.setLocation(x - dialog.getWidth() - 10, y - ((pos + 1) * dialog.getHeight()) - 10);
		dialog.setVisible(true);
		dialog.setFocusableWindowState(false);

		Thread t = new Thread() {
			public void run() {
				long totalTime = time;
				try {
					for (long t = 0; t < totalTime; t += 1000) {
						dialog.setTitle(title + " " + (time - t) / 1000 + "s");
						sleep(1000);
					}
					dialog.setVisible(false);
					dialog.dispose();
					freePosition(pos);
				} catch (Exception e) {
				}
			}
		};
		t.start();
	}

	private int requestPosition() {
		for (int i = 0; i < 100; i++) {
			if (list.get(i) == 0) {
				list.set(i, 1);
				return i;
			}
		}
		return 0;
	}

	private void freePosition(int pos) {
		list.set(pos, 0);
	}
}
