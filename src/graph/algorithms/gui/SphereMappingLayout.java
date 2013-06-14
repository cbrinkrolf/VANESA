package graph.algorithms.gui;

import graph.algorithms.BiologicalISOMNode;
import graph.algorithms.UniqueRandom;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.vecmath.Vector3f;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * Maps two dimensional coordinates onto a surface of a sphere. A sphere with
 * constant radius r can be described by two angles in polar coordinates, say
 * phi and theta. These polar coordinates can be translated into the cartesian
 * coordinate system in the following way:
 * 
 * x = r * cos(phi) * cos(theta) y = r * sin(phi) * cos(theta) z = r *
 * sin(theta)
 * 
 * Now only the two dimensional coordinates that should be mapped, say X and Y
 * have to be translated into angles like:
 * 
 * for a sphere: 0 <= X <= 2*Pi = theta 0 <= Y <= 2*Pi = phi for hemisphere: 0
 * <= X <= 2*Pi = theta 0 <= Y <= Pi = phi and so on.
 * 
 * 
 * To do this, the maximum value of the X and Y coordinates is calculated. Each
 * X and Y coordinate value is then normalized by the corresponding maximum
 * value and multiplied with the interval of the corresponding angle. For
 * example: (X coordinate / maximum of the X coordinates) * 2*Pi translates a X
 * coordinate into an angle between zero and 2*Pi.
 * 
 * @author Pascal (pwitthus@techfak.uni-bielefeld.de)
 * 
 */
public class SphereMappingLayout {

	private double xPos;
	private double yPos;
	private double phi;
	private double theta;

	private int numberOfPathways;
	private int pathwayCount;

	private Pathway pw;

	private Vector<BiologicalISOMNode> nodes;
	// private Vector<CM4Edge> edges;
	// private Vector<CM4Pathway> pathways;
	private Vector<BiologicalISOMNode> nodesOfCurrentPathway;

	private Vector<UniqueRandom> uRandomVector;
	private UniqueRandom uRandom;
	private long uRandomSeed;

	private static double enzymeRadius = 1;
	private static double compoundRadius = 1;
	private static double geneRadius = 1;
	private static double mapRadius = 1;
	private static double otherRadius = 1;
	private static double layerDist = 0.0;
	private static boolean sameEnzymeSamePosition = true;

	private static String hemiOrShpere = "Hemisphere";

	private static boolean flattened = false;

	public SphereMappingLayout(Pathway pw) {

		// uRandomVector = new Vector<UniqueRandom>();
		// uRandom = new UniqueRandom(this.uRandomSeed);
		//		

		// put the nodes of the current pathway into a vector
		// nodesOfCurrentPathway = new Vector<BiologicalISOMNode>();

		this.nodes = new Vector<BiologicalISOMNode>();

		for (BiologicalNodeAbstract node : (Vector<BiologicalNodeAbstract>) pw
				.getAllNodesAsVector()) {
			// for (Enumeration<BiologicalISOMNode> e = pw.getAllNodes(); e
			// .hasMoreElements();) {
			// BiologicalISOMNode node = e.nextElement();
			nodes.addElement(new BiologicalISOMNode(node));
		}

		this.pw = pw;

	}

	public SphereMappingLayout(Vector<BiologicalISOMNode> nodes,
	/** Vector<CM4Edge> edges, */
	long uRandomSeed, double enzymeRadius_get, double compoundRadius_get,
			double geneRadius_get, double mapRadius_get,
			double otherRadius_get, String hemiOrShpere_get,
			double layerDist_get, boolean flatt) {
		layerDist = layerDist_get;
		enzymeRadius = enzymeRadius_get;
		compoundRadius = compoundRadius_get;
		geneRadius = geneRadius_get;
		mapRadius = mapRadius_get;
		otherRadius = otherRadius_get;
		hemiOrShpere = hemiOrShpere_get;
		flattened = flatt;
		this.nodes = nodes;
		// this.edges = edges;
		this.uRandomSeed = uRandomSeed;
		uRandomVector = new Vector<UniqueRandom>();
		uRandom = new UniqueRandom(this.uRandomSeed);

	}

	/**
	 * Compute the layout.
	 * 
	 * @param nodes
	 *            the BiologicalISOMNodes to be layouted
	 */
	// public void doLayout(Vector<BiologicalISOMNode> nodes) {
	public void doLayout() {

		// this.nodes = nodes;

		uRandomVector = new Vector<UniqueRandom>();
		uRandom = new UniqueRandom(this.uRandomSeed);

		if (!nodes.isEmpty()) {
			// initial positioning of all nodes
			int countElements = 0;
			for (BiologicalISOMNode currNode : nodes) {

				uRandomVector.add(new UniqueRandom(currNode));
				currNode
						.setPos(flattened ? randomVectorFlattened(countElements)
								: randomVector(countElements));

				countElements++;

			}
			// the pathways that should be mapped on the sphere
			// pathways = ObjectPool.getNodeController().getPathways();
			// numberOfPathways = pathways.size();

			mapTo3D();

		}
	}

	/**
	 * This method gets 2-dimensional coordinates from the KEGG-Layout and maps
	 * them onto a spherical surface.
	 */
	private void mapTo3D() {
		// getting maximum of Kegg X- and Y-coordinates
		double xmax = getMaxX();
		double ymax = getMaxY();

		// pathwayCount = 1;
		// for (CM4Pathway currendPathway : pathways) {

		// put the nodes of the current pathway into a vector
		nodesOfCurrentPathway = new Vector<BiologicalISOMNode>();

		for (BiologicalNodeAbstract node : (Vector<BiologicalNodeAbstract>) pw
				.getAllNodesAsVector()) {
			// for (Enumeration<BiologicalISOMNode> e = pw.getAllNodes(); e
			// .hasMoreElements();) {
			// BiologicalISOMNode node = e.nextElement();
			nodesOfCurrentPathway.addElement(new BiologicalISOMNode(node));
		}

		for (BiologicalISOMNode currNode : nodesOfCurrentPathway) {
			// 2d Kegg-Coordinate

			Point2D p = pw.getGraph().getClusteringLayout().getLocation(
					currNode.getNode().getVertex());

			xPos = p.getX();
			yPos = p.getY();

			phi = calcAnglePhi(xPos, xmax);
			// decide whether they were mapped onto a shpere or a hemisphere
			if (hemiOrShpere.equals("Hemisphere")) {
				theta = calcAngleTheta(yPos, ymax);
			} else {
				theta = calcAngleThetaforSphere(yPos, ymax);
			}

			if (layerDist > 0) {
				currNode.setPos(flattened ? compute3dPointFlattened(
						pathwayCount + layerDist - 1, phi, theta)
						: compute3dPoint(pathwayCount + layerDist - 1, phi,
								theta));

				// if (sameEnzymeSamePosition) {
				// setPosForSameEnzymesOrMaps(currNode);
				// }
			} else {

				if (currNode.getNode().getDescription().equals(
						Elementdeclerations.enzyme)) {

					currNode.setPos(flattened ? compute3dPointFlattened(
							enzymeRadius, phi, theta) : compute3dPoint(
							enzymeRadius, phi, theta));

				} else if (currNode.getNode().getDescription().equals(
						Elementdeclerations.compound)) {

					currNode.setPos(flattened ? compute3dPointFlattened(
							compoundRadius, phi, theta) : compute3dPoint(
							compoundRadius, phi, theta));

				} else if (currNode.getNode().getDescription().equals(
						Elementdeclerations.gene)) {

					currNode.setPos(flattened ? compute3dPointFlattened(
							geneRadius, phi, theta) : compute3dPoint(
							geneRadius, phi, theta));

					// } else if (currNode.getType().equals("map")) {
					//
					// currNode.setPos(flattened ? compute3dPointFlattened(
					// mapRadius, phi, theta) : compute3dPoint(mapRadius,
					// phi, theta));

				} else {

					currNode.setPos(flattened ? compute3dPointFlattened(
							otherRadius, phi, theta) : compute3dPoint(
							otherRadius, phi, theta));

				}
			}
		}
		// pathwayCount++;
		// }
	}

	/**
	 * Gets the maximum value of the Kegg X-Coordinate
	 * 
	 * @return maximum value of all Kegg X-Coordinates
	 */
	private double getMaxX() {

		double xmax = // nodes.firstElement().getKgmlX();
		pw.getGraph().getClusteringLayout().getLocation(
				nodes.firstElement().getNode().getVertex()).getX();

		for (BiologicalISOMNode currNode : nodes) {

			double temp = // currNode.getKgmlX();
			pw.getGraph().getClusteringLayout().getLocation(
					currNode.getNode().getVertex()).getX();

			if (temp >= xmax) {
				xmax = temp;
			}

		}

		return xmax;
	}

	/**
	 * Gets the maximum value of the Kegg Y-Coordinate
	 * 
	 * @return maximum value of all Kegg Y-Coordinates
	 */
	private double getMaxY() {

		double ymax = // nodes.firstElement().getKgmly();
		pw.getGraph().getClusteringLayout().getLocation(
				nodes.firstElement().getNode().getVertex()).getY();

		for (BiologicalISOMNode currNode : nodes) {

			double temp = // currNode.getKgmly();
			pw.getGraph().getClusteringLayout().getLocation(
					currNode.getNode().getVertex()).getY();

			if (temp >= ymax) {
				ymax = temp;
			}

		}

		return ymax;
	}

	/**
	 * Translates a x-value into an angle of interval zero to 2*Pi.
	 * 
	 * @param x
	 *            value to be translated
	 * @return phi angle between zero and 2*Pi
	 */
	private double calcAnglePhi(double x, double xmax) {
		// double xmax = getMaxX();
		double phi = (x / xmax) * 2 * Math.PI;

		return phi;

	}

	/**
	 * Translates a y-value into an angle of interval zero to 2*Pi.
	 * 
	 * @param y
	 *            value to be translated
	 * @return theta angle between zero and 2*Pi
	 */
	private double calcAngleThetaforSphere(double y, double ymax) {

		// double ymax = getMaxY();

		return theta = (y / ymax) * 2 * Math.PI;

	}

	/**
	 * Translates a y-value into an angle of interval zero to Pi.
	 * 
	 * @param y
	 *            value to be translated
	 * @return theta angle between zero and Pi
	 */
	private double calcAngleTheta(double y, double ymax) {

		// double ymax = getMaxY();
		double theta = (y / ymax) * Math.PI;

		return theta;

	}

	/**
	 * Computes a 3D coordinate from a given 2D coordinate with the help of
	 * polar coordinate transformation.
	 * 
	 * @param radius
	 *            the radius of the sphere the nodes are placed on.
	 * @param phi
	 *            angle between x- and y axis
	 * @param theta
	 *            angle between y- and z axis
	 * @return 3D cartesian coordinate
	 */
	public Vector3f compute3dPoint(double radius, double phi, double theta) {

		double newXPos = radius * Math.cos(phi) * Math.cos(theta);
		double newYPos = radius * Math.sin(phi) * Math.cos(theta);
		double newZPos = radius * Math.sin(theta);

		return new Vector3f((float) newXPos, (float) newYPos, (float) newZPos);

	}

	/**
	 * Computes a 3D coordinate from a given 2D coordinate with the help polar
	 * coordinate transformation. z coordinate is zero here.
	 * 
	 * @param radius
	 *            the radius of the sphere the nodes are placed on.
	 * @param phi
	 *            angle between x- and y axis
	 * @param theta
	 *            angle between y- and z axis
	 * @return 3D cartesian coordinate with z=0
	 */
	public Vector3f compute3dPointFlattened(double radius, double phi,
			double theta) {

		double newXPos = radius * Math.cos(phi) * Math.cos(theta);
		double newYPos = radius * Math.sin(phi) * Math.cos(theta);
		double newZPos = 0;

		return new Vector3f((float) newXPos, (float) newYPos, (float) newZPos);

	}

	/**
	 * Creating a 3f vector with z = 0 by using the Unique Random Numbers of the
	 * overall uRandom values for creating the random points which are used for
	 * narrowing the nodes.
	 * 
	 * @return Vector3f
	 */
	private Vector3f randomVectorFlattened(int internalNodeID) {
		double x = uRandom.getNextDouble(true);
		double y = Math.sqrt(1 - Math.pow(x, 2));
		double z = 0.0d;
		int quadrant = (int) Math.round(3 * uRandom.getNextDouble(true));
		switch (quadrant) {
		case 1:
			y = -y;
			break;
		case 2:
			x = -x;
			break;
		case 3:
			x = -x;
			y = -y;
		default:
			// +X, +Y
		}
		return new Vector3f((float) x, (float) y, (float) z);
	}

	/**
	 * Creating a 3f vector by using the Unique Random Numbers of the node name
	 * for initializing the position of the nodes.
	 * 
	 * @param internalNodeID
	 * @return Vector3f
	 */
	private Vector3f randomVector(int internalNodeID) {
		double x = uRandomVector.get(internalNodeID).getNextDouble(true);
		double y = uRandomVector.get(internalNodeID).getNextDouble(true);
		if ((Math.pow(x, 2) + Math.pow(y, 2)) > 1) {
			x = x / 2;
			y = y / 2;
		}
		double z = Math.sqrt(1 - Math.pow(x, 2) - Math.pow(y, 2));
		// try to make it uniform by preventing z from being always positive
		// here
		if (x - y < 0)
			z = -z;
		int quadrant = (int) Math.round(7 * uRandomVector.get(internalNodeID)
				.getNextDouble(true));
		switch (quadrant) {
		case 1:
			y = -y;
			break;
		case 2:
			x = -x;
			y = -y;
			break;
		case 3:
			x = -x;
			break;
		case 4:
			z = -z;
			break;
		case 5:
			y = -y;
			z = -z;
			break;
		case 6:
			x = -x;
			y = -y;
			z = -z;
		case 7:
			x = -x;
			z = -z;
		default:
			// +X, +Y, +Z
		}
		return new Vector3f((float) x, (float) y, (float) z);
	}

	public Vector<BiologicalISOMNode> getNodes() {
		return this.nodes;
	}

	// /**
	// * Set the position of all internal enzyme nodes of the same name like the
	// * given node.
	// *
	// * @param nodeName
	// * @param position
	// */
	// private void setPosForSameEnzymesOrMaps(BiologicalISOMNode node) {
	// BiologicalISOMNode temp;
	// if (node.getType().equals("enzyme")) {
	// for (Enumeration<BiologicalISOMNode> e = nodes.elements(); e
	// .hasMoreElements();) {
	// temp = e.nextElement();
	// // take this to place nodes with same names in the same pathway
	// // on different positions, but there will be no connection to
	// // nodes with the same name in other pathways
	// // if (temp.getName().equals(node.getName()))
	// if (temp.getShortName().equals(node.getShortName()))
	// temp.setPos(node.getPos());
	// }
	// } else if (node.getType().equals("map")) {
	// for (Enumeration<BiologicalISOMNode> e = nodes.elements(); e
	// .hasMoreElements();) {
	// temp = e.nextElement();
	// if (temp.getName().equals(node.getName()))
	// temp.setPos(node.getPos());
	// }
	// }
	// }

	/**
	 * This method constructs the GUI where the user can change the settings of
	 * the algorithm.
	 */
	public static JPanel buildAndShowGUI() {
		JPanel guiPanel = new JPanel();

		JPanel spherePanel = new JPanel();

		spherePanel.setLayout(new GridLayout(10, 2));

		final JSpinner enzymeSpinner;
		final JSpinner compoundSpinner;
		final JSpinner geneSpinner;
		final JSpinner mapSpinner;
		final JSpinner otherSpinner;
		final JSpinner layerDistance;

		final JComboBox switchTohemiSphere;
		final JComboBox flattOrNot;
		final JComboBox sameEnzyme;

		SpinnerNumberModel enzyme_model = new SpinnerNumberModel(enzymeRadius,
				0.5, 5, 0.1);
		SpinnerNumberModel compound_model = new SpinnerNumberModel(
				compoundRadius, 0.5, 5, 0.1);
		SpinnerNumberModel gene_model = new SpinnerNumberModel(geneRadius, 0.5,
				5, 0.1);
		SpinnerNumberModel map_model = new SpinnerNumberModel(mapRadius, 0.5,
				5, 0.1);
		SpinnerNumberModel other_model = new SpinnerNumberModel(otherRadius,
				0.5, 5, 0.1);
		SpinnerNumberModel layerDistance_model = new SpinnerNumberModel(
				layerDist, 0.0, 3, 0.5);

		Boolean[] bool = { false, true };
		String whichSphere[] = { "Hemisphere", "Sphere" };

		enzymeSpinner = new JSpinner(enzyme_model);
		compoundSpinner = new JSpinner(compound_model);
		geneSpinner = new JSpinner(gene_model);
		mapSpinner = new JSpinner(map_model);
		otherSpinner = new JSpinner(other_model);
		layerDistance = new JSpinner(layerDistance_model);

		switchTohemiSphere = new JComboBox(whichSphere);
		flattOrNot = new JComboBox(bool);
		sameEnzyme = new JComboBox(bool);

		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				enzymeRadius = new Double(enzymeSpinner.getValue().toString());
				compoundRadius = new Double(compoundSpinner.getValue()
						.toString());
				geneRadius = new Double(geneSpinner.getValue().toString());
				mapRadius = new Double(mapSpinner.getValue().toString());
				otherRadius = new Double(otherSpinner.getValue().toString());
				layerDist = new Double(geneSpinner.getValue().toString());
				hemiOrShpere = switchTohemiSphere.getSelectedItem().toString();
				flattened = flattOrNot.getSelectedIndex() == 1 ? true : false;
				sameEnzymeSamePosition = sameEnzyme.getSelectedIndex() == 1 ? true
						: false;

			}
		});

		JLabel enzymeRadiusLabel = new JLabel("Radius of Enzyme");
		enzymeRadiusLabel
				.setToolTipText("Sets the Radius of Enzymes in spherical Coordinates");
		JLabel compoundRadiusLabel = new JLabel("Radius of Compound");
		compoundRadiusLabel
				.setToolTipText("Sets the Radius of Compounds in spherical Coordinates");
		JLabel geneRadiusLabel = new JLabel("Radius of Gene");
		geneRadiusLabel
				.setToolTipText("Sets the Radius of Genes in spherical Coordinates");
		JLabel mapRadiusLabel = new JLabel("Radius of Map");
		mapRadiusLabel
				.setToolTipText("Sets the Radius of Maps in spherical Coordinates");
		JLabel otherRadiusLabel = new JLabel("Radius of Other");
		otherRadiusLabel
				.setToolTipText("Sets the Radius of Others in spherical Coordinates");
		JLabel applyLabel = new JLabel("Apply");
		applyLabel.setToolTipText("Applies Settings");

		JLabel hemisphereOrNot = new JLabel("Switch to hemi-/Sphere");
		hemisphereOrNot
				.setToolTipText("Places the Nodes on the surface of a hemisphere or a sphere  ");

		JLabel flatt = new JLabel("Flattened");
		flatt
				.setToolTipText("If this is true, the nodes are placed in a 2D Area in 3D Space (X/Y-Area with Z=0)");

		JLabel layerLabel = new JLabel("layer distance");
		layerLabel.setToolTipText("Sets the distance between the pathways");
		JLabel sameEnzymeLabel = new JLabel("SameEnzymeSamePosition");
		sameEnzymeLabel
				.setToolTipText("if this is true, enzymes of the same type are placed at same position");

		spherePanel.add(enzymeRadiusLabel);
		spherePanel.add(enzymeSpinner);
		spherePanel.add(compoundRadiusLabel);
		spherePanel.add(compoundSpinner);
		spherePanel.add(geneRadiusLabel);
		spherePanel.add(geneSpinner);
		spherePanel.add(mapRadiusLabel);
		spherePanel.add(mapSpinner);
		spherePanel.add(otherRadiusLabel);
		spherePanel.add(otherSpinner);
		spherePanel.add(hemisphereOrNot);
		spherePanel.add(switchTohemiSphere);
		spherePanel.add(sameEnzymeLabel);
		spherePanel.add(sameEnzyme);
		spherePanel.add(flatt);
		spherePanel.add(flattOrNot);
		spherePanel.add(layerLabel);
		spherePanel.add(layerDistance);
		spherePanel.add(applyLabel);
		spherePanel.add(apply);

		guiPanel.add(new JLabel());

		guiPanel.add(spherePanel, BorderLayout.NORTH);

		return guiPanel;
	}

}
