package gui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class MyPopUp {

	private MainWindow w;
	private float opacity = 0.9f;
	private int time = 5000;

	private ArrayList<Integer> list;
	
	private StringBuilder sb = new StringBuilder();

	private static MyPopUp instance;

	private MyPopUp() {
		w = MainWindow.getInstance();
		list = new ArrayList<Integer>();
		for (int i = 0; i < 100; i++) {
			list.add(0);
		}
	}

	public static synchronized MyPopUp getInstance() {
		if (MyPopUp.instance == null) {
			MyPopUp.instance = new MyPopUp();
		}
		return MyPopUp.instance;
	}

	public void show(String title, String message) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String timeStemp = sdf.format(new Date());
		sb.append(timeStemp+" "+title+"\r\n"+message+"\r\n\r\n");

		JOptionPane pane = new JOptionPane(message, JOptionPane.NO_OPTION);
		JDialog dialog = pane.createDialog(null, title);
		dialog.setModal(false);

		dialog.setAlwaysOnTop(true);
		dialog.setOpacity(opacity);

		int x = w.getFrame().getBounds().x + w.getFrame().getBounds().width;
		int y = w.getFrame().getBounds().y + w.getFrame().getBounds().height;
		int pos = this.requestPosition();
		dialog.setLocation(x - dialog.getWidth() - 10, y - ((pos + 1) * dialog.getHeight()) - 10);
		dialog.setVisible(true);
		dialog.setFocusableWindowState(false);
		dialog.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// stop count down if pop up is clicked
				if (!dialog.getTitle().startsWith("Stopped...")) {
					dialog.setTitle("Stopped... " + dialog.getTitle());
				}
			}
		});

		dialog.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			public void componentHidden(ComponentEvent e) {
				// free stack position after closing pop up window
				freePosition(pos);
			}
		});

		Thread t = new Thread() {
			public void run() {
				long totalTime = time;
				try {
					for (long t = 0; t < totalTime; t += 1000) {
						if (dialog.getTitle().startsWith("Stopped...")) {
							break;
						}
						dialog.setTitle(title + " " + (time - t) / 1000 + "s");
						sleep(1000);
					}
					if (!dialog.getTitle().startsWith("Stopped...")) {
						dialog.setVisible(false);
						dialog.dispose();
					}
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
	
	public String getAll(){
		return sb.toString();
	}
}
