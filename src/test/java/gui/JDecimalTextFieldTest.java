package gui;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

class JDecimalTextFieldTest {
	@Test
	@Disabled
	public void test() throws InterruptedException {
		final JFrame frame = new JFrame();
		frame.setSize(500, 100);
		final JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.add(new JDecimalTextField());
		panel.add(new JDecimalTextField(true));
		frame.setContentPane(panel);
		frame.setVisible(true);
		while (frame.isVisible()) {
			Thread.sleep(1);
		}
	}
}