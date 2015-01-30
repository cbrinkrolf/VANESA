package database.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.infonode.tabbedpanel.titledtab.TitledTab;

public class QueryInfoWindow {

	private JPanel p = new JPanel();
	private final TitledTab tab;

	public QueryInfoWindow() {

		String instructions =

		"<html>"
				+ "To search for one attribute simply type<p>"
				+ "in the attribute of interest.  <font color=\"#000099\">Example: Homosapien </font><p><p>"
				+ "To search for two attributes in one field use the ' & ' char<p>"
				+ "to connect them.  <font color=\"#000099\">Example: 5.4.2.1 & 5.3.2.1 </font><p><p>"
				+ "To search for either one or another attribute in one field<p>"
				+ "use the ' | ' char to connect them. <font color=\"#000099\">Example: 5.4.2.1 | 5.3.2.1 </font><p><p>"
				+ "To search for data where given attributes should not appear <p>"
				+ "put an exclamation mark before that attribute. <font color=\"#000099\">Example: !homo </font><p><p>"
				+ "" + "</html>";
		p = new JPanel();
		p.add(new JLabel(instructions), BorderLayout.WEST);

		tab = new TitledTab("Instructions", null, p, null);
		tab.getProperties().setHighlightedRaised(2);
		tab.getProperties().getHighlightedProperties().getComponentProperties()
				.setBackgroundColor(Color.WHITE);
		tab.getProperties().getNormalProperties().getComponentProperties()
				.setBackgroundColor(Color.LIGHT_GRAY);
	}

	public TitledTab getTab() {
		return tab;
	}
}
