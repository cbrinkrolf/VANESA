/**
 * 
 */
package gui;

import graph.GraphInstance;
import gui.algorithms.ScreenSize;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.CollectorNode;
import biologicalObjects.nodes.KEGGNode;
import database.dawis.GetDAWISNode;
import database.kegg.GetKEGGNode;

/**
 * @author Sebastian
 * 
 */
public class GetDBinformation extends JFrame implements ActionListener {

	JPanel panel;

	JOptionPane pane;

	JCheckBox brendaSearch = new JCheckBox();

	JCheckBox keggSearch = new JCheckBox();

	JCheckBox dawisSearch = new JCheckBox();

	JCheckBox replace = new JCheckBox();

	JButton newButton = new JButton("ok");

	JButton cancelButton = new JButton("cancel");

	JButton[] buttons = { newButton, cancelButton };

	JOptionPane optionPane;

	JDialog dialog;

	JProgressBar bar = new JProgressBar();

	int progressBarCounter = 0;

	private Pathway pw;

	/**
	 * 
	 */
	public GetDBinformation() {

		run();

	}

	public void run() {

		this.pw = new GraphInstance().getPathway();
		Container contentPane = getContentPane();
		MigLayout layout = new MigLayout("", "[left][left][left][left]");

		brendaSearch.setSelected(true);
		keggSearch.setSelected(true);

		panel = new JPanel(layout);

		panel.add(new JLabel(
				"Which data sources would you like to search through?"),
				"span, growx,wrap 5");
		panel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");

		panel.add(new JLabel("BRENDA"), "gap 5, gaptop 2 ");
		panel.add(brendaSearch, "gaptop 2");

		panel.add(new JLabel("KEGG"), "gap 5, gaptop 2 ");
		panel.add(keggSearch, "gaptop 2");

		panel.add(new JLabel("DAWIS"), "gap 5, gaptop 2 ");
		panel.add(dawisSearch, "wrap ,gaptop 2");

		panel.add(new JLabel("Replace existing database information"),
				"span 6, gap 5, gaptop 2 ");
		panel.add(replace, "wrap ,gaptop 2");

		panel.add(bar, "span, growx, wrap 5, gaptop 7 ");
		panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");

		optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JDialog(this, "Search data sources through information",
				true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		ScreenSize screen = new ScreenSize();
		int screenHeight = (int) screen.getheight();
		int screenWidth = (int) screen.getwidth();

		newButton.addActionListener(this);
		newButton.setActionCommand("new");

		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancel");
		bar.setStringPainted(true);
		dialog.setModal(false);
		dialog.pack();
		dialog.setLocation((screenWidth / 2) - dialog.getSize().width / 2,
				(screenHeight / 2) - dialog.getSize().height / 2);
		dialog.setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
		UpdateThread t = new UpdateThread();
		if ("new".equals(event)) {

			if (!keggSearch.isSelected() && !brendaSearch.isSelected()
					&& !dawisSearch.isSelected()) {
				JOptionPane.showMessageDialog(null,
						"Please choose a data source.", "Message", 1);
			} else {
				// t.start();
				t.execute();
			}

		} else if ("cancel".equals(event)) {
			// if (t.isAlive()) {
			// t.interrupt();
			// t.stop();
			// }
			t.cancel(true);
			dialog.setVisible(false);
		}

	}

	class UpdateThread extends SwingWorker {

		Runnable update;
		Vector updates = new Vector();

		@Override
		public Void doInBackground() {

			HashSet set = pw.getAllNodes();

			bar.setMaximum(set.size());
			bar.setValue(30);
			Runnable run = new Runnable() {
				public void run() {
					bar.setEnabled(true);
					progressBarCounter = 0;
				}
			};
			SwingUtilities.invokeLater(run);

			GetKEGGNode getKeggNode = new GetKEGGNode();

			Iterator it = set.iterator();
			while (it.hasNext()) {

				String[] elementUpdates = new String[4];
				boolean updateBoolean = false;

				progressBarCounter++;
				bar.setValue(progressBarCounter);

				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) it.next();
				elementUpdates[0] = bna.getLabel();
				elementUpdates[1] = "-";
				elementUpdates[2] = "-";
				elementUpdates[3] = "-";

				KEGGNode universalKEGGNode = bna.getKEGGnode();

				if (replace.isSelected()) {
					bna.hasBrendaNode(false);
					bna.hasKEGGNode(false);
					bna.hasDAWISNode(false);
				}

				// String oldName = bna.getName();
				// String oldLabel = bna.getLabel();

				// if (brendaSearch.isSelected()) {
				// if (!bna.hasBrendaNode()
				// && bna.getBiologicalElement().equals(
				// Elementdeclerations.enzyme)) {
				// BRENDANode node = null;
				// try {
				// node = new GetBrendaNode().getElementDetails(bna
				// .getLabel());
				// } catch (SQLException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
				//
				// if (node != null) {
				// updateBoolean = true;
				// elementUpdates[1] = "updated";
				// bna.setBrendaNode(node);
				// bna.hasBrendaNode(true);
				// bna.setLabel(node.getEc_number());
				// bna.setName(node.getName());
				// pw.getGraph().updateElementLabel(bna.getVertex());
				// } else {
				// if (universalBRENDANode != null) {
				// bna.hasBrendaNode(true);
				// bna.setBrendaNode(universalBRENDANode);
				// }
				// }
				// }
				// }

				if (keggSearch.isSelected()) {

					if (!bna.hasKEGGNode()) {
						KEGGNode node = null;

						if (bna.getBiologicalElement().equals(
								Elementdeclerations.enzyme)) {
							node = getKeggNode
									.getNode(bna.getLabel(), "enzyme");
						} else if (bna.getBiologicalElement().equals(
								Elementdeclerations.dna)) {
							node = getKeggNode.getNode(bna.getLabel(), "gene");
						} else if (bna.getBiologicalElement().equals(
								Elementdeclerations.orthologGroup)) {
							node = getKeggNode.getNode(bna.getLabel(),
									"ortholog");
						} else {
							node = getKeggNode.getNode(bna.getLabel(),
									"compound");
						}

						if (node != null) {
							elementUpdates[2] = "updated";
							updateBoolean = true;
							bna.setKEGGnode(node);
							bna.hasKEGGNode(true);
							bna.setLabel(node.getKEGGentryName());
							bna.setName(node.getKEGGentryName());
							pw.getGraph().updateElementLabel(bna.getVertex());

						} else {
							if (universalKEGGNode != null) {
								bna.setKEGGnode(universalKEGGNode);
								bna.hasKEGGNode(true);
							}
						}
					}
				}
				if (dawisSearch.isSelected()) {

					if (bna.hasDAWISNode()) {

						if (!(bna instanceof CollectorNode)) {
							new GetDAWISNode().getElementDetails(bna);
							elementUpdates[3] = "updated";
							updateBoolean = true;
						}
					}
				}

				if (updateBoolean) {
					updates.add(elementUpdates);
				}

			}

			// Runnable run2=new Runnable(){
			// public void run(){
			//					
			// }
			// };
			// SwingUtilities.invokeLater(run2);

			return null;
		}

		@Override
		public void done() {

			dialog.setVisible(false);
			pw.getGraph().updateGraph();

			if (updates.size() > 0) {
				new IntegrationResults(updates);
			} else {
				JOptionPane
						.showMessageDialog(
								null,
								"Sorry, no matches have been found. Please characterise the elements in more detail.",
								"Message", 1);
			}

		}
	}
}
