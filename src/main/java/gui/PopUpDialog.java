package gui;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PopUpDialog {
    private static PopUpDialog instance;

    private final MainWindow w = MainWindow.getInstance();
    private final int time = 5000;
    private final int[] list = new int[100];
    private final StringBuilder sb = new StringBuilder();

    private PopUpDialog() {
    }

    public static synchronized PopUpDialog getInstance() {
        if (instance == null) {
            instance = new PopUpDialog();
        }
        return instance;
    }

    public void show(String title, String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());
        sb.append(timestamp).append(" ").append(title).append("\n").append(message).append("\n\n");
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(null, title);
        dialog.setModal(false);
        dialog.setAlwaysOnTop(true);
        dialog.setOpacity(0.9f);
        //int x = w.getFrame().getBounds().x + w.getFrame().getBounds().width / 2;
        //int y = w.getFrame().getBounds().y + w.getFrame().getBounds().height / 2;
        int x = w.getFrame().getBounds().x + w.getFrame().getBounds().width;
		int y = w.getFrame().getBounds().y + w.getFrame().getBounds().height;
		int pos = this.requestPosition();
		dialog.setLocation(x - dialog.getWidth() - 10, y - ((pos + 1) * dialog.getHeight()) - 10);
		dialog.setVisible(true);
        //dialog.setLocation(x - dialog.getWidth() / 2, y - dialog.getHeight() / 2 + pos * dialog.getHeight());
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

        Thread t = new Thread(() -> {
            try {
                for (long t1 = 0; t1 < (long) time; t1 += 1000) {
                    if (dialog.getTitle().startsWith("Stopped...")) {
                        break;
                    }
                    dialog.setTitle(title + " " + (time - t1) / 1000 + "s");
                    Thread.sleep(1000);
                }
                if (!dialog.getTitle().startsWith("Stopped...")) {
                    dialog.setVisible(false);
                    dialog.dispose();
                }
            } catch (Exception ignored) {
            }
        });
        t.start();
    }

    private int requestPosition() {
        for (int i = 0; i < 100; i++) {
            if (list[i] == 0) {
                list[i] = 1;
                return i;
            }
        }
        return 0;
    }

    private void freePosition(int pos) {
        list[pos] = 0;
    }

    public String getAll() {
        return sb.toString();
    }
}
