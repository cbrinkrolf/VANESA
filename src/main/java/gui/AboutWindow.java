package gui;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

public class AboutWindow {
	public AboutWindow() {
		// Get current size of heap in bytes.
		long heapSize = Runtime.getRuntime().totalMemory();
		// Get maximum size of heap in bytes. The heap cannot grow beyond this size.
		// Any attempt will result in an OutOfMemoryException.
		long heapMaxSize = Runtime.getRuntime().maxMemory();
		// Get amount of free memory within the heap in bytes. This size will
		// increase after garbage collection and decrease as new objects are created.
		long heapFreeSize = Runtime.getRuntime().freeMemory();

		final StringBuilder text = new StringBuilder();
		text.append("<html>");
		text.append("<h3>About</h3>");
		text.append(
				"VANESA is a network editor software to search, create and examine biological pathways,<br>as well as to model and simulate Petri nets.<p>");
		text.append("It is developed at Bielefeld University (Germany).<p>");
		text.append("For further details, please visit the VANESA GitHub website:<br>");
		text.append(
				"<a href=\"https://github.com/cbrinkrolf/VANESA/\">https://github.com/cbrinkrolf/VANESA/</a><p><p>");
		text.append("Memory overview:<br>");
		text.append("Size of current memory usage: ").append(FileUtils.byteCountToDisplaySize(heapSize)).append("<br>");
		text.append("Size of maximum memory: ").append(FileUtils.byteCountToDisplaySize(heapMaxSize)).append("<br>");
		text.append("Size of free memory: ").append(FileUtils.byteCountToDisplaySize(heapFreeSize)).append("<p><p>");
		text.append("Contact Details:<br>");
		text.append("Christoph Brinkrolf <a href=\"mailto:cbrinkro@gmail.com\">cbrinkro@gmail.com</a><br>");
		text.append("Marcel Friedrichs<br>");
		text.append("Benjamin Kormeier<p><p>");
		text.append("</html>");
		MainWindow w = MainWindow.getInstance();
		JOptionPane.showMessageDialog(w.getFrame(), text.toString(), "About", JOptionPane.INFORMATION_MESSAGE);
	}
}
