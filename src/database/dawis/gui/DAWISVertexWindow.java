package database.dawis.gui;

import graph.GraphInstance;

import gui.MainWindow;
import gui.MainWindowSingelton;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import miscalleanous.internet.FollowLink;
import miscalleanous.tables.NodePropertyTable;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.hyperlink.LinkAction;

import database.dawis.*;

import biologicalElements.Pathway;
import biologicalObjects.nodes.*;

/**
 * 
 * @author Olga
 *
 */

/**
 * create dawis vertex window
 */
public class DAWISVertexWindow implements ActionListener {

	JPanel p = new JPanel();
	boolean emptyPane = true;
	boolean loaded = false;
	Object[][] values = null;
	JButton loadElement = new JButton("Load the element(s) down");
	JButton loadElementInformation = new JButton("Update element information");

	BiologicalNodeAbstract ab;
	GraphInstance graphInstance = new GraphInstance();
	Object selectedObject;
	NodePropertyTable table;
	int[] selectedRows;
	Vector<String[]> selectedElements;
	Vector<String> loadedElements = new Vector<String>();
	Pathway path;
	int elementCountInCollector, rowCount;

	// empty constructor
	public DAWISVertexWindow() {

	}

	/**
	 * get panel
	 * 
	 * @return jpanel
	 */
	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	/**
	 * refresh the window
	 */
	public void revalidateView() {

		selectedObject = graphInstance.getSelectedObject();

		if (emptyPane) {
			updateWindow(graphInstance.getSelectedObject());
			p.setVisible(true);
			p.repaint();
			p.revalidate();
			emptyPane = false;
		} else {
			p.removeAll();
			updateWindow(graphInstance.getSelectedObject());
			p.setVisible(true);
			p.repaint();
			p.revalidate();
		}
	}

	public JButton getLoadButton() {
		return loadElement;
	}

	/**
	 * update data for
	 * 
	 * @param element
	 *            vertex
	 */
	@SuppressWarnings( { "serial", "unchecked" })
	private void updateWindow(Object element) {

		this.ab = (BiologicalNodeAbstract) graphInstance
				.getPathwayElement(element);

		DAWISNode node = ab.getDAWISNode();

		final String link = node.getLink(ab);
		JXHyperlink hyperlink = null;
		JXHyperlink hyperlink2 = null;

		if (!(ab instanceof CollectorNode)) {
			LinkAction linkAction = new LinkAction("Original Database Link") {
				public void actionPerformed(ActionEvent e) {
					setVisited(true);
					FollowLink.openURL(link);
				}
			};

			hyperlink = new JXHyperlink(linkAction);

			final String link2 = node.getDAWISLink(ab);
			LinkAction linkAction2 = new LinkAction("DAWIS-M.D. Link") {
				public void actionPerformed(ActionEvent e) {
					setVisited(true);
					FollowLink.openURL(link2);
				}
			};

			hyperlink2 = new JXHyperlink(linkAction2);
		}

		String[] header;

		values = node.getDAWISDetailsFor(node.getObject());

		int countColumns = values[0].length;

		if (!node.getObject().equals("Collector")) {
			if (countColumns == 1) {
				header = new String[1];
			} else {
				header = new String[2];
				header[1] = "Value";
			}
			header[0] = "Attribute";

		} else {
			if (countColumns == 1) {
				header = new String[1];
			} else {
				header = new String[2];
				header[1] = "Element-Name";
			}
			header[0] = "Element-ID";
		}

		table = new NodePropertyTable(values, header);
		MigLayout layout = new MigLayout("fillx", "[right]rel[grow,fill]", "");

		JPanel headerPanel = new JPanel(layout);
		headerPanel.setBackground(new Color(192, 215, 227));
		if (!(ab instanceof CollectorNode)) {
			if (link != null) {
				if (!link.equals("")) {
					headerPanel.add(hyperlink, "");
				}
			}
			headerPanel.add(hyperlink2, "dock east");
		}

		MigLayout layout2 = new MigLayout("fillx", "[grow,fill]",
				"[]5[fill]5[]");

		p.setLayout(layout2);
		p.add(headerPanel, "wrap");
		p.add(table.getTable(), "");
		if (!node.getDataLoaded()) {
			if (node.getObject().equals("Collector")) {
				loadElement.addActionListener(this);
				loadElement.setActionCommand("load");
				p.add(loadElement, "gapright 10, gapleft 10, dock south");
			} else {
				loadElementInformation.addActionListener(this);
				loadElementInformation.setActionCommand("loadInformation");
				p.add(loadElementInformation,
						"gapright 10, gapleft 10, dock south");
			}
		} else {
			p.removeAll();
			updateInformation(element);
			p.setVisible(true);
			p.repaint();
			p.validate();

		}

	}

	/**
	 * load chosen elements remove them from the table
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		final MainWindow elinf = MainWindowSingelton.getInstance();

		String event = e.getActionCommand();

		if ("load".equals(event)) {

			loaded = true;

			elinf.setEnable(false);

			path = graphInstance.getPathway();

			SingleElementLoader sel = new SingleElementLoader(this, path, p,
					table);
			sel.execute();

		} else if ("loadInformation".equals(event)) {

			InformationUpdate iu = new InformationUpdate(this, p,
					selectedObject);
			iu.execute();

		}

	}

	@SuppressWarnings( { "unchecked", "serial" })
	public void updateInformation(Object element) {

		this.ab = (BiologicalNodeAbstract) graphInstance
				.getPathwayElement(element);

		DAWISNode node = ab.getDAWISNode();

		if (this.ab instanceof PathwayMap) {
			if (!node.getDataLoaded()) {
				PathwayMap p = (PathwayMap) this.ab;
				new GetPathwayDetails(p);
			}
		} else if (this.ab instanceof Enzyme) {
			if (!node.getDataLoaded()) {
				Enzyme e = (Enzyme) this.ab;
				new GetEnzymeDetails(e);
			}
		} else if (this.ab instanceof Protein) {
			if (!node.getDataLoaded()) {
				Protein p = (Protein) this.ab;
				new GetProteinDetails(p);
			}
		} else if (this.ab instanceof Disease) {
			if (!node.getDataLoaded()) {
				Disease d = (Disease) this.ab;
				new GetDiseaseDetails(d);
			}
		} else if (this.ab instanceof Drug) {
			if (!node.getDataLoaded()) {
				Drug d = (Drug) this.ab;
				new GetDrugDetails(d);
			}
		} else if (this.ab instanceof GeneOntology) {
			if (!node.getDataLoaded()) {
				GeneOntology go = (GeneOntology) this.ab;
				new GetGODetails(go);
			}
		} else if (this.ab instanceof Reaction) {
			if (!node.getDataLoaded()) {
				Reaction r = (Reaction) this.ab;
				new GetReactionDetails(r);
			}
		} else if (this.ab instanceof Glycan) {
			if (!node.getDataLoaded()) {
				Glycan gl = (Glycan) this.ab;
				new GetGlycanDetails(gl);
			}
		} else if (this.ab instanceof CompoundNode) {
			if (!node.getDataLoaded()) {
				CompoundNode c = (CompoundNode) this.ab;
				new GetCompoundDetails(c);
			}
		} else if (this.ab instanceof Gene) {
			if (!node.getDataLoaded()) {
				Gene g = (Gene) this.ab;
				new GetGeneDetails(g);
			}
		} else if (this.ab instanceof Factor) {
			if (!node.getDataLoaded()) {
				Factor g = (Factor) this.ab;
				new GetTransfacFactorDetails(g);
			}
		} else if (this.ab instanceof Fragment) {
			if (!node.getDataLoaded()) {
				Fragment g = (Fragment) this.ab;
				new GetTransfacFragmentDetails(g);
			}
		} else if (this.ab instanceof Site) {
			if (!node.getDataLoaded()) {
				Site g = (Site) this.ab;
				new GetTransfacSiteDetails(g);
			}
		}

		final String link = node.getLink(ab);
		LinkAction linkAction = new LinkAction("Original Database Link") {
			public void actionPerformed(ActionEvent e) {
				setVisited(true);
				FollowLink.openURL(link);
			}
		};

		JXHyperlink hyperlink = new JXHyperlink(linkAction);

		final String link2 = node.getDAWISLink(ab);
		LinkAction linkAction2 = new LinkAction("DAWIS-M.D. Link") {
			public void actionPerformed(ActionEvent e) {
				setVisited(true);
				FollowLink.openURL(link2);
			}
		};

		JXHyperlink hyperlink2 = new JXHyperlink(linkAction2);

		String[] header;

		values = node.getDAWISDetailsFor(node.getObject());

		int countColumns = values[0].length;

		if (!node.getObject().equals("Collector")) {
			if (countColumns == 1) {
				header = new String[1];
			} else {
				header = new String[2];
				header[1] = "Value";
			}
			header[0] = "Attribute";
		} else {
			if (countColumns == 1) {
				header = new String[1];
			} else {
				header = new String[2];
				header[1] = "Element-Name";
			}
			header[0] = "Element-ID";
		}

		if (loaded) {
			node.removeElementsFromTable(selectedRows);
			loaded = false;
		}

		table = new NodePropertyTable(values, header);
		MigLayout layout = new MigLayout("fillx", "[right]rel[grow,fill]", "");

		JPanel headerPanel = new JPanel(layout);
		headerPanel.setBackground(new Color(192, 215, 227));
		headerPanel.add(hyperlink, "");
		headerPanel.add(hyperlink2, "dock east");
		MigLayout layout2 = new MigLayout("fillx", "[grow,fill]",
				"[]5[fill]5[]");

		p.setLayout(layout2);
		p.add(headerPanel, "wrap");
		p.add(table.getTable(), "");
		if (ab.getBiologicalElement().equals("Collector")) {
			loadElement.addActionListener(this);
			loadElement.setActionCommand("load");
			p.add(loadElement, "gapright 10, gapleft 10, dock south");
		}
	}

}
