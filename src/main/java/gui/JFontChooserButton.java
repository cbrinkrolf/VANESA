package gui;

import org.drjekyll.fontchooser.FontDialog;

import javax.swing.JButton;
import javax.swing.WindowConstants;
import java.awt.Font;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

public class JFontChooserButton extends JButton {
	private final List<FontSelectedListener> listeners = new ArrayList<>();
	private final String dialogTitle;
	private Font selectedFont;
	private Font defaultFont;

	public JFontChooserButton(final String label, final String dialogTitle) {
		super(label);
		this.dialogTitle = dialogTitle;
		addActionListener(e -> onShowDialog());
	}

	private void onShowDialog() {
		final FontDialog dialog = new FontDialog((Frame) null, dialogTitle, true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setSelectedFont(selectedFont != null ? selectedFont : defaultFont);
		dialog.setLocationRelativeTo(this);
		dialog.setAlwaysOnTop(true);
		dialog.requestFocus();
		dialog.setVisible(true);
		if (!dialog.isCancelSelected()) {
			selectedFont = dialog.getSelectedFont();
			for (final FontSelectedListener listener : listeners) {
				listener.onFontSelected(selectedFont);
			}
		}
	}

	public Font getSelectedFont() {
		return selectedFont;
	}

	public void setSelectedFont(Font selectedFont) {
		this.selectedFont = selectedFont;
	}

	public Font getDefaultFont() {
		return defaultFont;
	}

	public void setDefaultFont(Font defaultFont) {
		this.defaultFont = defaultFont;
	}

	public void addFontSelectedListener(final FontSelectedListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeFontSelectedListener(final FontSelectedListener listener) {
		listeners.remove(listener);
	}

	public interface FontSelectedListener {
		void onFontSelected(final Font font);
	}
}
