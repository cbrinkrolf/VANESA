package util;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JDialog;

public class MyColorChooser implements ActionListener {

	private Color color = null;
	private JColorChooser cc;
	private boolean isOk = false;

	public MyColorChooser(Component parent, String title, boolean modal, Color color) {
		this.isOk = false;
		this.color = color;
		this.cc = new JColorChooser(color);
		JDialog dialog = JColorChooser.createDialog(parent, title, modal, cc, this, this);
		dialog.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println(e.getActionCommand());
		switch (e.getActionCommand()) {
		case "OK":
			isOk = true;
			break;
		case "cancel":
			isOk = false;
			break;
		}
	}

	public boolean isOkAction() {
		color = cc.getColor();
		return this.isOk;
	}

	public Color getColor() {
		return color;
	}
}
