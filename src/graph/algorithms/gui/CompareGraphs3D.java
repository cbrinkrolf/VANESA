package graph.algorithms.gui;

import graph.algorithms.BiologicalISOMNode;
import graph.algorithms.CompareGraphs;
import gui.images.ImagePath;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Map.Entry;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.CapabilityNotSetException;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.RestrictedAccessException;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import save.graphPicture.WriteGraphPicture;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class CompareGraphs3D extends JFrame implements ItemListener,
		MouseListener, ActionListener, KeyListener, WindowListener {

	private static final long serialVersionUID = 1L;

	// Das Orbit-Behaviour fuer die Interaktivitaet im Universum
	private final OrbitBehavior orbit;
	private final OrbitBehavior orbit_orientation;

	// Wird genutzt für Aktualisierung des OrientationFrame bei Benutzung des
	// OrbitBehaviour
	private Task task;
	private Timer timer;

	// Die Transformation fuer die Starteinstellung
	private final Transform3D home;

	// Die TransformGroup regelt die Transformationen aller Graphen
	private final TransformGroup objTransform;
	private final TransformGroup objTransform_orientation;
	private final BranchGroup worldTransform;
	private final BranchGroup worldTransform_orientation;

	// Rotationszenturm der TransformGroup objTransform
	private Point3d rotCenter = new Point3d(0, 0, 0);

	// aktuelle Translationsrichtung zum vertauschen der Achsen speichern
	// Zoom Richtung ebenfalls speichern
	Transformation transformation;

	// Legt ein Graph betrachtet wird
	public boolean singleGraph;

	// Das java3D universum
	private final SimpleUniverse uni;
	private final SimpleUniverse uni_orientation;

	// Das 3d canvas welches fuer die darstellung des universums zust�ndig ist
	private final Canvas3D can;
	private Canvas3D can_orientation;
	private final JFrame orientationFrame;

	// Diese Gruppe regelt die Sichtbarkeit der original nodes
	// private Switch switchGroup;
	// private java.util.BitSet visibleNodes;
	private Switch[] switches;

	// Die checkboxes fuer die sichtbarkeit
	private final Checkbox[] checkboxes;
	private final Checkbox[] plane_checkboxes;

	// Dieses canvas wird fuer das mouse picking benoetigt
	private final PickCanvas pickCanvas;

	// Diese farbe bekommt ein ausgewaehlter node
	private final Color3f selectedColor = new Color3f(.8f, .8f, .8f);

	// Diese beiden felder werden benoetigt um den ausgangszustand eines
	// gepickten nodes wieder herzustellen
	private Primitive oldSelected = null;
	private Color3f oldColor = null;

	private Shape3D[] planes;

	public CompareGraphs3D(Collection<Pathway> pathways) {
		this(pathways.toArray(new Pathway[0]));
	}

	public CompareGraphs3D(Pathway[] pathways) {
		this.setVisible(false);
		if (pathways.length < 2) {
			this.singleGraph = true;
		} else {
			this.singleGraph = false;
		}

		// Fenster erstellen und eigenschaften setzen
		if (this.singleGraph) {
			this.setTitle("3D View");
		} else {
			this.setTitle("Compare 3D");
		}

		// Fenster erstellen und eigenschaften setzen
		this.setPreferredSize(new Dimension(800, 600));
		this.setLayout(new BorderLayout());
		this.addWindowListener(this);

		// Das canvas erstellen
		can = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		can.setPreferredSize(new Dimension(800, 600));

		// KeyListener initialisieren
		can.addKeyListener(this);

		// Das universum erstellen
		uni = new SimpleUniverse(can);
		uni.getViewingPlatform().setNominalViewingTransform();

		// Den "rootnode" erstellen planes fuer die ebenen und die lichter
		BranchGroup world = this.getInitialWorld(pathways, false);
		world.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		world.compile();

		// Initialisiere TransformGroup für Transformationen des Graphen
		objTransform = new TransformGroup();
		objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objTransform.addChild(world);

		// neuer "rootnode" mit Transformationsmöglichkeiten und das Behavior
		worldTransform = new BranchGroup();
		worldTransform.addChild(objTransform);
		worldTransform.compile();

		uni.addBranchGraph(worldTransform);

		// Das pick canvas erstellen
		pickCanvas = new PickCanvas(can, world);
		pickCanvas.setMode(PickCanvas.GEOMETRY);
		can.addMouseListener(this);

		// OrbitBehavior fuer die interaktivitaet
		orbit = new OrbitBehavior(can, OrbitBehavior.REVERSE_ALL
				| OrbitBehavior.STOP_ZOOM);
		BoundingSphere bound = new BoundingSphere(new Point3d(0, 0, 0), 15);
		orbit.setSchedulingBounds(bound);
		orbit.setMinRadius(1.5);
		home = new Transform3D();
		home.setTranslation(new Vector3f(0, 0, 15f));
		orbit.setHomeTransform(home);
		uni.getViewingPlatform().setViewPlatformBehavior(orbit);
		Transform3D transform = new Transform3D();
		transform.setTranslation(new Vector3f(0, 0, 15f));
		uni.getViewingPlatform().getViewPlatformTransform().setTransform(
				transform);
		uni.getViewer().getView().setBackClipDistance(500);
		this
				.add(
						new JLabel(
								"<html>Use left mouse button to rotate, right mouse button to drag."
										+ "<br/>Use cursors, Q and A to navigate through the View"
										+ "<br/>Toolbar: click to execute shown action. Click again to stop. "
										+ "</html>"), BorderLayout.NORTH);

		// ////////////////////////////////////////////////
		// Zusätzliches Canvas für Orientierung der Achsen
		can_orientation = new Canvas3D(SimpleUniverse
				.getPreferredConfiguration());
		can_orientation.setPreferredSize(new Dimension(100, 100));
		can_orientation.setDoubleBufferEnable(true);

		uni_orientation = new SimpleUniverse(can_orientation);
		uni_orientation.getViewingPlatform().setNominalViewingTransform();

		BranchGroup world_orientation = this.getInitialWorld(pathways, true);
		world_orientation.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		world_orientation.compile();

		// Initialisiere TransformGroup für Transformationen des Graphen
		objTransform_orientation = new TransformGroup();
		objTransform_orientation
				.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTransform_orientation
				.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objTransform_orientation.addChild(world_orientation);

		// neuer "rootnode" mit Transformationsmöglichkeiten und das Behavior
		worldTransform_orientation = new BranchGroup();
		worldTransform_orientation.addChild(objTransform_orientation);
		worldTransform_orientation.compile();

		uni_orientation.addBranchGraph(worldTransform_orientation);

		orbit_orientation = new OrbitBehavior(can_orientation,
				OrbitBehavior.REVERSE_ALL | OrbitBehavior.STOP_ZOOM);
		bound = new BoundingSphere(new Point3d(0, 0, 0), 15);
		orbit_orientation.setSchedulingBounds(bound);
		orbit_orientation.setMinRadius(1.5);
		orbit_orientation.setRotateEnable(false);
		orbit_orientation.setTranslateEnable(false);
		orbit_orientation.setZoomEnable(false);
		// uni_orientation.getViewingPlatform().setViewPlatformBehavior(orbit_orientation);
		transform = new Transform3D();
		transform.setTranslation(new Vector3f(0, 0, 3f));
		uni_orientation.getViewingPlatform().getViewPlatformTransform()
				.setTransform(transform);
		uni_orientation.getViewer().getView().setBackClipDistance(500);
		// /////////////////////////////////////////////////

		// Transformations Klasse initieren
		this.transformation = new Transformation(this, singleGraph);
		transformation.start();

		// Das canvas ins fenster einfuegen
		this.add(can, BorderLayout.CENTER);

		// Die Checkboxes fuer die visibility einbauen
		JPanel vpanel = new JPanel();
		JPanel upper_panel = new JPanel();
		JPanel middle_panel = new JPanel();
		JPanel down_panel = new JPanel();
		vpanel.setLayout(new BorderLayout());

		JLabel vlabel = new JLabel("<html><b>Visibility:</b> </html>");
		vpanel.add(vlabel, BorderLayout.WEST);
		this.checkboxes = new Checkbox[pathways.length];
		this.plane_checkboxes = new Checkbox[pathways.length + 1];
		for (int i = 0; i < pathways.length; i++) {
			checkboxes[i] = new Checkbox(pathways[i].getName());
			checkboxes[i].addItemListener(this);
			// Checkboxen f�r die Graphen kommen nach oben
			upper_panel.add(checkboxes[i]);
		}

		String name;
		for (int i = 0; i <= pathways.length; i++) {
			if (i == 0)
				name = "Layer for common elements";
			else
				name = "Layer for " + pathways[i - 1].getName();
			plane_checkboxes[i] = new Checkbox(name);
			plane_checkboxes[i].addItemListener(this);
			plane_checkboxes[i].setState(true);
			// die Checkbox f�r die zentrale Ebene kommt in die Mitte
			if (i == 0)
				middle_panel.add(plane_checkboxes[i]);
			// Checkboxen f�r die Ebenen kommen nach unten
			else
				down_panel.add(plane_checkboxes[i], BorderLayout.SOUTH);
		}

		vpanel.add(upper_panel, BorderLayout.NORTH);
		vpanel.add(middle_panel, BorderLayout.CENTER);
		vpanel.add(down_panel, BorderLayout.SOUTH);

		if (!this.singleGraph) {
			this.add(vpanel, BorderLayout.SOUTH);
		}

		// Die Buttons für Zoom, Drehung etc.
		JPanel opanel = new JPanel(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();

		ImagePath imagePath = ImagePath.getInstance();

		// Button für Koordinatenkreuz
		JButton orientationButton = new JButton(new ImageIcon(imagePath
				.getPath("move.png")));
		orientationButton.setToolTipText("Show Axis Rotation");
		orientationButton.setActionCommand("axis_rotation");
		orientationButton.addActionListener(this);
		orientationButton.setMinimumSize(new Dimension(40, 40));

		// Zoom Funktionen
		JButton zoomOut = new JButton(new ImageIcon(imagePath
				.getPath("move_left.png")));
		zoomOut.setToolTipText("Move blue Axis forward");
		zoomOut.setBackground(new Color(100, 100, 255));
		zoomOut.setActionCommand("zoomOut");
		zoomOut.addActionListener(this);
		zoomOut.setMinimumSize(new Dimension(40, 40));

		JButton zoomIn = new JButton(new ImageIcon(imagePath
				.getPath("move_left.png")));
		zoomIn.setToolTipText("Move blue Axis backward");
		zoomIn.setBackground(new Color(100, 100, 255));
		zoomIn.setActionCommand("zoomIn");
		zoomIn.addActionListener(this);
		zoomIn.setMinimumSize(new Dimension(40, 40));

		// Über diesen Button wird die Kamera nach links bewegt
		JButton move_left = new JButton(new ImageIcon(imagePath
				.getPath("move_left.png")));
		move_left.setToolTipText("Move green Axis forward");
		move_left.setBackground(new Color(100, 255, 100));
		move_left.setActionCommand("move_left");
		move_left.addActionListener(this);
		move_left.setMinimumSize(new Dimension(40, 40));

		// Über diesen Button wird die Kamera nach rechts bewegt
		JButton move_right = new JButton(new ImageIcon(imagePath
				.getPath("move_left.png")));
		move_right.setToolTipText("Move green Axis backward");
		move_right.setBackground(new Color(100, 255, 100));
		move_right.setActionCommand("move_right");
		move_right.addActionListener(this);
		move_right.setMinimumSize(new Dimension(40, 40));

		// Über diesen Button wird die Kamera nach oben bewegt
		JButton move_forward = new JButton(new ImageIcon(imagePath
				.getPath("move_left.png")));
		move_forward.setToolTipText("Move red Axis forward");
		move_forward.setBackground(new Color(255, 100, 100));
		move_forward.setActionCommand("move_up");
		move_forward.addActionListener(this);
		move_forward.setMinimumSize(new Dimension(40, 40));

		// Über diesen Button wird die Kamera nach unten bewegt
		JButton move_back = new JButton(new ImageIcon(imagePath
				.getPath("move_left.png")));
		move_back.setToolTipText("Move red Axis backward");
		move_back.setBackground(new Color(255, 100, 100));
		move_back.setActionCommand("move_down");
		move_back.addActionListener(this);
		move_back.setMinimumSize(new Dimension(40, 40));

		// Über diesen Button wird die Kamera nach unten bewegt
		JButton rotate_left = new JButton(new ImageIcon(imagePath
				.getPath("rot_right.png")));
		rotate_left.setToolTipText("Right Rotation about red axis");
		rotate_left.setBackground(new Color(255, 100, 100));
		rotate_left.setActionCommand("rotate_left");
		rotate_left.addActionListener(this);
		rotate_left.setMinimumSize(new Dimension(40, 40));

		// Über diesen Button wird die Kamera nach unten bewegt
		JButton rotate_right = new JButton(new ImageIcon(imagePath
				.getPath("rot_left.png")));
		rotate_right.setToolTipText("Left Rotation about red axis");
		rotate_right.setBackground(new Color(255, 100, 100));
		rotate_right.setActionCommand("rotate_right");
		rotate_right.addActionListener(this);
		rotate_right.setMinimumSize(new Dimension(40, 40));

		// Über diesen Button wird die Kamera nach unten bewegt
		JButton rotate_up = new JButton(new ImageIcon(imagePath
				.getPath("rot_right.png")));
		rotate_up.setToolTipText("Right Rotation about green axis");
		rotate_up.setBackground(new Color(100, 255, 100));
		rotate_up.setActionCommand("rotate_up");
		rotate_up.addActionListener(this);
		rotate_up.setMinimumSize(new Dimension(40, 40));

		// Über diesen Button wird die Kamera nach unten bewegt
		JButton rotate_down = new JButton(new ImageIcon(imagePath
				.getPath("rot_left.png")));
		rotate_down.setToolTipText("Left Rotation about green axis");
		rotate_down.setBackground(new Color(100, 255, 100));
		rotate_down.setActionCommand("rotate_down");
		rotate_down.addActionListener(this);
		rotate_down.setMinimumSize(new Dimension(40, 40));

		// Über diesen Button wird die Kamera nach unten bewegt
		JButton rotate_forward = new JButton(new ImageIcon(imagePath
				.getPath("rot_right.png")));
		rotate_forward.setToolTipText("Right Rotation about blue axis");
		rotate_forward.setBackground(new Color(100, 100, 255));
		rotate_forward.setActionCommand("rotate_forward");
		rotate_forward.addActionListener(this);
		rotate_forward.setMinimumSize(new Dimension(40, 40));

		// Über diesen Button wird die Kamera nach unten bewegt
		JButton rotate_backward = new JButton(new ImageIcon(imagePath
				.getPath("rot_left.png")));
		rotate_backward.setToolTipText("Left Rotation about blue axis");
		rotate_backward.setBackground(new Color(100, 100, 255));
		rotate_backward.setActionCommand("rotate_backward");
		rotate_backward.addActionListener(this);
		rotate_backward.setMinimumSize(new Dimension(40, 40));

		// Bewegung zum Ursprungspunkt
		JButton moveHome = new JButton(new ImageIcon(imagePath
				.getPath("centerGraph.png")));
		moveHome.setToolTipText("Move Graph to home position");
		moveHome.setActionCommand("move_home");
		moveHome.addActionListener(this);
		moveHome.setMinimumSize(new Dimension(40, 40));

		// Screenshot aufnehmen
		JButton screenCap = new JButton(new ImageIcon(imagePath
				.getPath("picture.png")));
		screenCap.setToolTipText("Make a screenshot");
		screenCap.setActionCommand("screen_cap");
		screenCap.addActionListener(this);
		screenCap.setMinimumSize(new Dimension(40, 40));

		// Platzhalter für ein schönes Layout
		JLabel placeholderZoom = new JLabel();
		placeholderZoom.setPreferredSize(new Dimension(40, 20));
		placeholderZoom.setMinimumSize(new Dimension(40, 20));

		// Platzhalter für ein schönes Layout
		JLabel placeholderMove = new JLabel();
		placeholderMove.setPreferredSize(new Dimension(40, 20));
		placeholderMove.setMinimumSize(new Dimension(40, 20));

		// Platzhalter für ein schönes Layout
		JLabel placeholderRotate = new JLabel();
		placeholderRotate.setPreferredSize(new Dimension(40, 20));
		placeholderRotate.setMinimumSize(new Dimension(40, 20));

		orientationFrame = new JFrame();
		// Image image = can_orientation.createImage(100, 100);
		//
		// can_orientation.setOffScreenBuffer(buffer);
		// can_orientation.renderOffScreenBuffer();
		orientationFrame.add(can_orientation);

		// Layouten, zentrieren und sichtbar machen
		orientationFrame.pack();
		orientationFrame.setResizable(false);
		orientationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		cons.fill = GridBagConstraints.BOTH;
		cons.gridwidth = 1;
		cons.gridx = 0;
		cons.gridy = 0;
		opanel.add(orientationButton, cons);
		cons.gridx = 0;
		cons.gridy = 1;
		opanel.add(placeholderZoom, cons);
		cons.gridwidth = 1;
		cons.gridx = 0;
		cons.gridy = 2;
		opanel.add(zoomOut, cons);
		cons.gridx = 1;
		cons.gridy = 2;
		opanel.add(zoomIn, cons);
		cons.gridx = 0;
		cons.gridy = 3;
		opanel.add(move_left, cons);
		cons.gridx = 1;
		cons.gridy = 3;
		opanel.add(move_right, cons);
		cons.gridx = 0;
		cons.gridy = 4;
		opanel.add(move_forward, cons);
		cons.gridx = 1;
		cons.gridy = 4;
		opanel.add(move_back, cons);
		cons.gridwidth = 2;
		cons.gridx = 0;
		cons.gridy = 5;
		opanel.add(placeholderMove, cons);
		cons.gridwidth = 1;
		cons.gridx = 0;
		cons.gridy = 6;
		opanel.add(rotate_left, cons);
		cons.gridx = 1;
		cons.gridy = 6;
		opanel.add(rotate_right, cons);
		cons.gridx = 0;
		cons.gridy = 7;
		opanel.add(rotate_up, cons);
		cons.gridx = 1;
		cons.gridy = 7;
		opanel.add(rotate_down, cons);
		cons.gridx = 0;
		cons.gridy = 8;
		opanel.add(rotate_forward, cons);
		cons.gridx = 1;
		cons.gridy = 8;
		opanel.add(rotate_backward, cons);
		cons.gridwidth = 2;
		cons.gridx = 0;
		cons.gridy = 9;
		opanel.add(placeholderRotate, cons);
		cons.gridwidth = 1;
		cons.gridx = 0;
		cons.gridy = 10;
		opanel.add(moveHome, cons);
		cons.gridx = 1;
		cons.gridy = 10;
		opanel.add(screenCap, cons);

		this.add(opanel, BorderLayout.EAST);

		// Layouten, zentrieren und sichtbar machen
		this.pack();
		this.centerSelf();
		// this.setVisible(true);
		this.setAlwaysOnTop(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Diese branchGroup enthaelt die eigentlichen daten
		if (this.singleGraph) {
			addSingleData(world, pathways[0]);
		} else {
			// die Pathways zu einem gemeinsamen Pathway zusammenf�hren
			Pathway merge = CompareGraphs.compare3D(pathways);

			addData(world, merge, pathways);
		}

		// world.addChild(bg);
		can_orientation.setDoubleBufferEnable(true);

		orientationFrame.setVisible(true);
		orientationFrame.setAlwaysOnTop(true);
		this.setVisible(true);
	}

	/**
	 * Nimmt einen Pathway und stellt ihn in 3d dar
	 * 
	 * @param pathway
	 * @param bg
	 *            die Branchgroup zu welcher die Geometrie hinzugefügt wird
	 */
	private void addSingleData(BranchGroup world, Pathway pathway) {
		BranchGroup bg = new BranchGroup();
		Appearance app = getAppearence(new Color3f(0, 0, 1f)); // Blau

		// Eine Branchgroup für den Pathway
		BranchGroup p = new BranchGroup();

		// Das ISOMLayout erstellen
		// ISOMLayout layout = new ISOMLayout(pathway);
		SphereMappingLayout layout = new SphereMappingLayout(pathway);
		layout.doLayout();

		// Die erzeugten ISOMNodes aus dem Layout holen
		Vector<BiologicalISOMNode> nodes = layout.getNodes();

		// Zunaechst wollen wir die knoten hinzufuegen
		for (BiologicalISOMNode node : nodes) {
			if (node.getNode().isVertex()) {

				Vector3f vec = node.getPos();
				// Korrektur der Node Positionen um die Sphere zu vergrößen
				vec.scale(4.0f);
				node.setPos(vec);

				// Jetzt erstellen wir die eigentliche geometrie
				Primitive object = null;
				Primitive o1 = null, o2 = null;
				// Enzymes als boxes, der rest spheres
				if (node.getNode().getBiologicalElement().equals("Enzyme")) {
					app = getAppearence(new Color3f(1f, 0, 1f));
					object = new Box(.07f, .07f, .07f, app);
					o1 = new Box(.07f, .07f, .07f, app);
					o2 = new Box(.07f, .07f, .07f, app);
				} else {
					object = new Sphere(.07f, Sphere.GENERATE_NORMALS, app);
					o1 = new Sphere(.07f, Sphere.GENERATE_NORMALS, app);
					o2 = new Sphere(.07f, Sphere.GENERATE_NORMALS, app);
				}
				// Da wir spaeter das aussehen aendern wollen muessen wir die
				// capabilities entsprechend setzen
				object.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
				object.setCapability(Primitive.ENABLE_GEOMETRY_PICKING);
				object.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
				object.setCapability(Primitive.ALLOW_LOCAL_TO_VWORLD_READ);
				o1.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
				o1.setCapability(Primitive.ENABLE_GEOMETRY_PICKING);
				o2.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
				o2.setCapability(Primitive.ENABLE_GEOMETRY_PICKING);

				// System.out.println("Test: "+vec);
				// Jetzt noch eine transformgroup erstellen um den node an die
				// richtige position zu schieben
				Transform3D dtransform = new Transform3D();
				dtransform.setTranslation(vec);
				TransformGroup trans_group1 = new TransformGroup(dtransform);
				trans_group1
						.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
				trans_group1.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
				trans_group1
						.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

				trans_group1.addChild(object);
				// Das label einfuegen
				// System.out.println("aktuelles Label : " +
				// node.getNode().getLabel());
				trans_group1.addChild(get3dText(new Vector3f(0, 0, 1f), node
						.getNode().getLabel()));
				bg.addChild(trans_group1);

			}
		}

		// Jetzt muessen wir noch die kanten einfuegen
		for (BiologicalEdgeAbstract edge : (Vector<BiologicalEdgeAbstract>) pathway
				.getAllEdgesAsVector()) {
			BiologicalNodeAbstract node1 = (BiologicalNodeAbstract) pathway
					.getNodeByVertexID(edge.getEdge().getEndpoints().getFirst()
							.toString());
			BiologicalNodeAbstract node2 = (BiologicalNodeAbstract) pathway
					.getNodeByVertexID(edge.getEdge().getEndpoints()
							.getSecond().toString());

			// Positionen der Nodes sind bekannt aus der ISOMLayout
			// Erster Node
			Vector3f vec1 = null;
			for (int i = 0; i < nodes.size(); i++) {
				if (node1.equals(nodes.get(i).getNode())) {
					vec1 = nodes.get(i).getPos();
					break;
				}
			}

			// Zweiter Node
			Vector3f vec2 = null;
			for (int i = 0; i < nodes.size(); i++) {
				if (node2.equals(nodes.get(i).getNode())) {
					vec2 = nodes.get(i).getPos();
					break;
				}
			}

			// nur wenn beide positionen gefunden wurden
			if ((vec1 != null) && (vec2 != null)) {
				// Wir erstellen ein LineArray fuer uneser kante... dieses
				// enthaelt die koordinaten und die farbe
				LineArray a = new LineArray(2, LineArray.COORDINATES
						| LineArray.COLOR_3);
				// Koordinaten und farben setzen und ein Shape3D fuer die line
				// erstellen
				float[] v1 = { vec1.x, vec1.y, vec1.z };
				a.setCoordinate(0, v1);
				a.setColor(0, new Color3f(.5f, .5f, .5f));
				float[] v2 = { vec2.x, vec2.y, vec2.z };
				a.setCoordinate(1, v2);
				a.setColor(1, new Color3f(.5f, .5f, .5f));
				bg.addChild(new Shape3D(a));
			}

		}

		bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

		enablePicking(bg);

		world.addChild(bg);
	}

	/**
	 * Setzt die ViewPlatform des gesamten Universums
	 * 
	 * @param transform
	 */
	public void setViewTransform(Transform3D transform) {
		uni.getViewingPlatform().getViewPlatformTransform().setTransform(
				transform);
	}

	/**
	 * Gibt die aktuelle Transformation der ViewPlatform zurück
	 * 
	 * @return Transform3D
	 */
	public Transform3D getViewTransformTransformation() {
		Transform3D transform = new Transform3D();
		TransformGroup transformGroup = uni.getViewingPlatform()
				.getViewPlatformTransform();
		transformGroup.getTransform(transform);
		return transform;
	}

	public Canvas3D getOrientationCanvas() {
		return this.can_orientation;
	}

	public void setOrientationCanvas(Canvas3D can) {
		this.can_orientation = can;
	}

	/**
	 * Setzt Rotationszenturm der TransformGroup
	 * 
	 * @param point
	 */
	public void setRotationCenter() {
		Point3d center = new Point3d();
		orbit.getRotationCenter(center);
		this.rotCenter = center;
	}

	public Point3d getRotationCenter() {
		return this.rotCenter;
	}

	/**
	 * Setzt die TransformGroup des gesamten Universums
	 * 
	 * @param transform
	 */
	public void setObjTransform(Transform3D transform) {
		objTransform.setTransform(transform);
	}

	/**
	 * Setzt die TransformGroup für die Orientierung der Achsen
	 * 
	 * @param transform
	 */
	public void setObjTransformOrientation(Transform3D transform) {
		objTransform_orientation.setTransform(transform);
	}

	/**
	 * Gibt die aktuelle Transformation der TransformGroup zurück
	 * 
	 * @return Transform3D
	 */
	public Transform3D getObjTransformTransformation() {
		Transform3D transform = new Transform3D();
		objTransform.getTransform(transform);
		// objTransform_orientation.getTransform(transform);
		return transform;
	}

	/**
	 * Gibt die aktuelle Transformation der TransformGroup zur
	 * AchsenOrientierung zurück
	 * 
	 * @return Transform3D
	 */
	public Transform3D getObjTransformOrientationTransformation() {
		Transform3D transform = new Transform3D();
		objTransform_orientation.getTransform(transform);
		return transform;
	}

	public OrbitBehavior getOrbit() {
		return this.orbit;
	}

	public SimpleUniverse getUniverse() {
		return this.uni;
	}

	public SimpleUniverse getOrientationUniverse() {
		return this.uni_orientation;
	}

	private void setTransform(int transformationVariant) {
		if (transformation.getTransform() == transformationVariant) {
			transformation.setTransform(-1);
		} else {
			transformation.setTransform(transformationVariant);
		}
	}

	public static String outputVector(Vector3f vec) {
		return vec.x + " " + vec.y + " " + vec.z;
	}

	public static Vector3f transformVector(Vector3f vector,
			Transform3D transform) {
		Vector3f v = new Vector3f();
		if (transform != null) {
			transform.get(v);
			v.add(vector);
		}
		// transform.transform(vec);
		return v;
	}

	public static Vector3f getDefaultNodePosition(Pathway merge,
			BiologicalNodeAbstract node) {
		Point2D pos = merge.getGraph().getVertexLocation(node.getVertex());
		return new Vector3f((float) pos.getX() / 150, (float) pos.getY() / 150,
				0f);
	}

	public static Transform3D[] getNodePositionsAccordingToGraph(Pathway merge,
			BiologicalNodeAbstract node, Pathway[] pathways,
			HashMap<BiologicalNodeAbstract, Node>[] graph_node_to_primitive) {
		ArrayList<Transform3D> vectors = new ArrayList<Transform3D>();
		Vector3f vec = getDefaultNodePosition(merge, node);

		if (node.containedInAllOriginalGraphs(pathways)) {
			Transform3D t = new Transform3D();
			t.set(vec);
			vectors.add(t);
		} else {
			// suche die passende Verschiebung f�r den Punkt
			for (int i = 1; i <= pathways.length; i++) {
				if (node.getOriginalGraphs().contains(i)) {
					for (Entry<BiologicalNodeAbstract, Node> entry : graph_node_to_primitive[i - 1]
							.entrySet()) {
						if (entry.getKey().equals(node)) {
							Transform3D transform = new Transform3D();
							entry.getValue().getLocalToVworld(transform);
							vectors.add(transform);
						}
					}
				}
			}
		}
		return vectors.toArray(new Transform3D[vectors.size()]);
	}

	public static Primitive getPrimitiveFor(BiologicalNodeAbstract node,
			Pathway[] pathways) {

		Appearance app = null;
		Primitive object = null;
		if (node.containedInAllOriginalGraphs(pathways)) {
			// Kommt der node aus beiden graphen dann kommt er in die mitte
			// vec = new Vector3d(pos.getX()/100, pos.getY()/100, 0f);
			app = getAppearence(new Color3f(0, 0, 1f)); // Blau
		} else {
			// bestimme eine andere Farbe je nach Graph
			for (int i = 1; i <= pathways.length; i++) {
				if (node.getOriginalGraphs().contains(i)) {
					Color3f c;
					if (i == 1)
						c = new Color3f(0, 1f, 0);
					else if (i == 2)
						c = new Color3f(1f, 0, 0);
					else if (i == 3)
						c = new Color3f(1f, 1f, 0);
					else if (i == 4)
						c = new Color3f(1f, 0, 1f);
					else if (i == 5)
						c = new Color3f(0, 1f, 1f);
					else
						c = new Color3f(1f, 1f, 1f);
					app = getAppearence(c);
				}
			}
		}

		// Enzymes als boxes, der rest spheres
		if (node.getBiologicalElement().equals("Enzyme")) {
			app = getAppearence(new Color3f(1f, 0, 1f));
			object = new Box(.07f, .07f, .07f, app);
		} else {
			object = new Sphere(.07f, Sphere.GENERATE_NORMALS, app);
		}
		object.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
		object.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
		// eventuell einbinden: object.setCapability(PickTool.INTERSECT_COORD);
		return object;
	}

	private void addLine(BranchGroup bg, Transform3D start_node_transform,
			Transform3D end_node_transform) {
		// beide Knoten wirklich gefunden?
		if ((start_node_transform != null) && (end_node_transform != null)) {
			Vector3f vec1 = new Vector3f();
			start_node_transform.get(vec1);
			Vector3f vec2 = new Vector3f();
			end_node_transform.get(vec2);
			bg.addChild(this.createLine(vec1, vec2));
		}
	}

	/**
	 * Nimmt einen Pathway und stellt diesen in 3d dar.
	 * 
	 * @param world
	 * @param merge
	 *            Der Pathway der dargestellt werden soll.
	 * @param bg
	 *            Die BranchGroup zu welcher die Geometrie hinzugefuegt wird.
	 */
	private BranchGroup addData(BranchGroup world, Pathway merge,
			Pathway[] pathways) {
		// wie gro� sollen die Ebenen sein?
		int planesize = 12;

		BranchGroup bg = new BranchGroup();

		this.planes = new Shape3D[pathways.length + 1];
		// Zentralebene f�r gemeinsame Elemente

		this.planes[0] = getPlane(planesize, 0, new Color4f(1, 0, 0, 1));

		bg.addChild(this.planes[0]);

		// zusaetzliche BranchGroups fuer die original Pathways erstellen
		switches = new Switch[pathways.length];
		for (int i = 0; i < switches.length; i++) {
			switches[i] = new Switch(Switch.CHILD_NONE);
			switches[i].setCapability(Switch.ALLOW_SWITCH_WRITE);
		}
		TransformGroup[] graphs = new TransformGroup[pathways.length];
		for (int i = 0; i < graphs.length; i++) {

			graphs[i] = new TransformGroup();
			graphs[i].setTransform(getTransformForGraphNumber(i + 1,
					pathways.length));

			// Ebene zeichnen
			this.planes[i + 1] = getPlane(planesize, 0, new Color4f(0, 1, 0, 1));
			graphs[i].addChild(this.planes[i + 1]);

			// graphs enth�lt
			// - die Ebenen (immer sichtbar)
			// - die BranchGroups (Graphen, sichtbar auf Wunsch)
			graphs[i].addChild(switches[i]);

			bg.addChild(graphs[i]);
			// bg.getlo
		}
		HashMap<BiologicalNodeAbstract, Node>[] graph_node_to_primitive = new HashMap[pathways.length];
		for (int i = 0; i < graph_node_to_primitive.length; i++) {
			graph_node_to_primitive[i] = new HashMap<BiologicalNodeAbstract, Node>();
		}

		for (int i = 0; i < pathways.length; i++) {
			Pathway pathway = pathways[i];

			// Knoten hinzuf�gen
			for (BiologicalNodeAbstract node : (Vector<BiologicalNodeAbstract>) pathway
					.getAllNodesAsVector()) {
				if (node.isVertex()) {
					Vector3f vec = getDefaultNodePosition(pathway, node);
					Primitive object = getPrimitiveFor(node, pathways);

					Transform3D dtransform = new Transform3D();
					dtransform.setTranslation(vec);
					TransformGroup trans_group1 = new TransformGroup(dtransform);
					trans_group1.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
					trans_group1.addChild(object);
					// Das label einfuegen
					trans_group1.addChild(get3dText(new Vector3f(0, 0, 1f),
							node.getLabel()));
					graph_node_to_primitive[i].put(node, object);
					switches[i].addChild(trans_group1);
				}
			}

			// Edges hinzuf�gen
			for (BiologicalEdgeAbstract edge : (Vector<BiologicalEdgeAbstract>) pathway
					.getAllEdgesAsVector()) {
				BiologicalNodeAbstract start_node = (BiologicalNodeAbstract) pathway
						.getNodeByVertexID(edge.getEdge().getEndpoints()
								.getFirst().toString());
				BiologicalNodeAbstract end_node = (BiologicalNodeAbstract) pathway
						.getNodeByVertexID(edge.getEdge().getEndpoints()
								.getSecond().toString());

				Vector3f vec1 = getDefaultNodePosition(pathway, start_node);
				Vector3f vec2 = getDefaultNodePosition(pathway, end_node);
				switches[i].addChild(this.createLine(vec1, vec2));
			}
		}
		// enablePicking(bg);
		bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		world.addChild(bg);
		bg = new BranchGroup();

		// ----------------------------------
		// ---------KNOTEN HINZUFUEGEN------
		for (BiologicalNodeAbstract node : (Vector<BiologicalNodeAbstract>) merge
				.getAllNodesAsVector()) {
			if (node.isVertex()) {
				Vector3f vec = getDefaultNodePosition(merge, node); // new
				// Vector3d(pos.getX()/100,
				// pos.getY()/100,
				// 0f);

				// Jetzt noch eine transformgroup erstellen um den node an die
				// richtige position zu schieben
				Transform3D dtransform = new Transform3D();
				dtransform.setTranslation(vec);
				if (!node.containedInAllOriginalGraphs(pathways)) {

					Transform3D[] positions = getNodePositionsAccordingToGraph(
							merge, node, pathways, graph_node_to_primitive);
					for (Transform3D transform : positions) {
						TransformGroup trans_group1 = new TransformGroup(
								transform);
						trans_group1.addChild(getPrimitiveFor(node, pathways));
						// Das label einfuegen
						trans_group1.addChild(get3dText(new Vector3f(0, 0, 1f),
								node.getLabel()));
						bg.addChild(trans_group1);
					}
				} else {
					TransformGroup trans_group1 = new TransformGroup(dtransform);
					trans_group1.addChild(getPrimitiveFor(node, pathways));
					// Das label einfuegen
					trans_group1.addChild(get3dText(new Vector3f(0, 0, 1f),
							node.getLabel()));
					bg.addChild(trans_group1);
				}

			}
		}

		// -----------------------------------------
		// -----------KANTEN HINZUFUEGEN------------
		for (BiologicalEdgeAbstract edge : (Vector<BiologicalEdgeAbstract>) merge
				.getAllEdgesAsVector()) {
			BiologicalNodeAbstract start_node = (BiologicalNodeAbstract) merge
					.getNodeByVertexID(edge.getEdge().getEndpoints().getFirst()
							.toString());
			BiologicalNodeAbstract end_node = (BiologicalNodeAbstract) merge
					.getNodeByVertexID(edge.getEdge().getEndpoints()
							.getSecond().toString());

			// suche die passende Verschiebung f�r den Start- und Endpunkt
			boolean center_start_node = start_node
					.containedInAllOriginalGraphs(pathways);
			boolean center_end_node = end_node
					.containedInAllOriginalGraphs(pathways);
			if (!center_start_node || !center_end_node) {
				Collection originals = edge.getOriginalGraphs();
				for (int i : edge.getOriginalGraphs()) {
					if ((start_node.getOriginalGraphs().contains(i))
							&& (start_node.getOriginalGraphs().contains(i))) {
						Transform3D start_node_transform = null;
						Transform3D end_node_transform = null;

						for (Entry<BiologicalNodeAbstract, Node> entry : graph_node_to_primitive[i - 1]
								.entrySet()) {
							if (entry.getKey().equals(start_node)) {
								start_node_transform = new Transform3D();
								entry.getValue().getLocalToVworld(
										start_node_transform);
							} else if (entry.getKey().equals(end_node)) {
								end_node_transform = new Transform3D();
								entry.getValue().getLocalToVworld(
										end_node_transform);
							}
						}

						if (center_start_node) {
							Vector3f vec = getDefaultNodePosition(merge,
									start_node);
							start_node_transform = new Transform3D();
							start_node_transform.set(vec);
						}

						if (center_end_node) {
							Vector3f vec = getDefaultNodePosition(merge,
									end_node);
							end_node_transform = new Transform3D();
							end_node_transform.set(vec);
						}

						this.addLine(bg, start_node_transform,
								end_node_transform);
					}
				}
			}
			// sonst erstelle die Linie in der Zentralebene
			else {
				Vector3f start_vec = getDefaultNodePosition(merge, start_node);
				Transform3D start_node_transform = new Transform3D();
				start_node_transform.set(start_vec);

				Vector3f end_vec = getDefaultNodePosition(merge, end_node);
				Transform3D end_node_transform = new Transform3D();
				end_node_transform.set(end_vec);

				this.addLine(bg, start_node_transform, end_node_transform);
			}
		}

		// Picking ermoeglichen
		bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		enablePicking(bg);
		world.addChild(bg);
		return bg;
	}

	/** erstellt eine Linie, die zwei Punkte im Raum verbindet */
	private Shape3D createLine(Vector3f vec1, Vector3f vec2) {
		// Wir erstellen ein LineArray fuer unsere Kante... dieses enthaelt die
		// Koordinaten und die Farbe
		LineArray linearray = new LineArray(2, LineArray.COORDINATES
				| LineArray.COLOR_3);
		// Koordinaten und Farben setzen und ein Shape3D fuer die Line erstellen
		float[] v1 = { vec1.x, vec1.y, vec1.z };
		linearray.setCoordinate(0, v1);
		linearray.setColor(0, new Color3f(.5f, .5f, .5f));
		float[] v2 = { vec2.x, vec2.y, vec2.z };
		linearray.setCoordinate(1, v2);
		linearray.setColor(1, new Color3f(.5f, .5f, .5f));
		return new Shape3D(linearray);
	}

	private void centerSelf() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int x = (screenSize.width - this.getSize().width) / 2;
		int y = (screenSize.height - this.getSize().height) / 2;
		this.setLocation(x, y);
	}

	private TransformGroup get3dText(Vector3f rotationPoint, String text) {
		Font3D font3D = new Font3D(new Font("Helvetica", Font.PLAIN, 1),
				new FontExtrusion());
		Text3D textGeom = new Text3D(font3D, new String(text));
		textGeom.setAlignment(Text3D.ALIGN_CENTER);

		TransformGroup tg = new TransformGroup();
		Transform3D t = new Transform3D();
		t.setScale(.15f);
		tg.setTransform(t);

		OrientedShape3D osd = new OrientedShape3D();
		osd.setAlignmentMode(osd.ROTATE_ABOUT_POINT);
		osd.setRotationPoint(rotationPoint.x, rotationPoint.y + 1f,
				rotationPoint.z + 0.5f);
		osd.addGeometry(textGeom);
		tg.addChild(osd);
		return tg;
	}

	/**
	 * Die einzelnen Graphen werden grunds�tzlich so aufgebaut, als w�ren sie in
	 * der Mitte der Bildfl�che. Die Ebene wird anschlie�end verschoben und
	 * rotiert. Diese funktion liefert zu einem Graphen geh�rende
	 * Transformation.
	 */
	public static Transform3D getTransformForGraphNumber(int number,
			int maxNumber) {
		/*
		 * Die Berechnung der Anordnung der Ebenen passiert wie folgt. Es gibt
		 * insgesamt maxNumber+1 Ebenen, die angeordnet werden m�ssen: maxNumber
		 * Graphenebenen und 1 Zentralebene f�r gemeinsame Elemente. Die
		 * Graphenebenen sollen im Kreis um die Zentralebene angeordnet werden.
		 * Dazu werden sie gedreht und vom Zentrum aus nach Au�en verschoben.
		 * 
		 * Wir teilen dazu den gedachten Kreis um die Zentralebene in maxNumber
		 * Teile auf. Der so bestimmte Winkel f�r die Verschiebung ergibt mit
		 * Sinus und Cosinus die Verschiebungswerte. �ber Winkelbeziehungen kann
		 * man dann den zugeh�rigen Drehungswinkel der Tangentenebene bestimmen.
		 */

		Transform3D move = new Transform3D();
		double transform_angle;
		if (maxNumber == 2) { // f�r 2 Ebenen mache Spezialwerte
			if (number == 1)
				transform_angle = Math.PI / 2; // 90 grad
			else
				transform_angle = Math.PI / 2 * 3; // 270 grad
		} else {
			transform_angle = ((double) number / ((double) maxNumber))
					* Math.PI * 2;
		}
		double rotate_angle = -1 * (Math.PI / 2 - transform_angle);
		move.set(new Vector3d(0, 15 * Math.cos(transform_angle), 12 * Math
				.sin(transform_angle)));
		Transform3D rotate = new Transform3D();
		rotate.rotX(rotate_angle);

		// combine rotating and moving in one transformation
		move.mul(rotate);
		return move;
	}

	private BranchGroup getInitialWorld(Pathway[] pathways, boolean orientation) {
		BranchGroup g = new BranchGroup();

		if (orientation) {
			LineArray a = new LineArray(30, LineArray.COORDINATES
					| LineArray.COLOR_3);
			// x achse
			a.setCoordinate(0, new Point3f(-1.0f, 0, 0));
			a.setColor(0, new Color3f(0, 1, 0));
			a.setCoordinate(1, new Point3f(1.0f, 0, 0));
			a.setColor(1, new Color3f(0, 1, 0));
			// y achse
			a.setCoordinate(2, new Point3f(0, -1.0f, 0));
			a.setColor(2, new Color3f(1, 0, 0));
			a.setCoordinate(3, new Point3f(0, 1.0f, 0));
			a.setColor(3, new Color3f(1, 0, 0));
			// z achse
			a.setCoordinate(4, new Point3f(0, 0, -1.0f));
			a.setColor(4, new Color3f(0, 0, 1));
			a.setCoordinate(5, new Point3f(0, 0, 1.0f));
			a.setColor(5, new Color3f(0, 0, 1));
			// pfeil x achse
			a.setCoordinate(6, new Point3f(0.95f, .05f, 0));
			a.setColor(6, new Color3f(0, 1, 0));
			a.setCoordinate(7, new Point3f(1, 0, 0));
			a.setColor(7, new Color3f(0, 1, 0));
			a.setCoordinate(8, new Point3f(0.95f, -.05f, 0));
			a.setColor(8, new Color3f(0, 1, 0));
			a.setCoordinate(9, new Point3f(1, 0, 0));
			a.setColor(9, new Color3f(0, 1, 0));

			a.setCoordinate(18, new Point3f(0.95f, 0, .05f));
			a.setColor(18, new Color3f(0, 1, 0));
			a.setCoordinate(19, new Point3f(1, 0, 0));
			a.setColor(19, new Color3f(0, 1, 0));
			a.setCoordinate(20, new Point3f(0.95f, 0, -.05f));
			a.setColor(20, new Color3f(0, 1, 0));
			a.setCoordinate(21, new Point3f(1, 0, 0));
			a.setColor(21, new Color3f(0, 1, 0));
			// pfeil y achse
			a.setCoordinate(10, new Point3f(.05f, .95f, 0));
			a.setColor(10, new Color3f(1, 0, 0));
			a.setCoordinate(11, new Point3f(0, 1, 0));
			a.setColor(11, new Color3f(1, 0, 0));
			a.setCoordinate(12, new Point3f(-.05f, .95f, 0));
			a.setColor(12, new Color3f(1, 0, 0));
			a.setCoordinate(13, new Point3f(0, 1, 0));
			a.setColor(13, new Color3f(1, 0, 0));

			a.setCoordinate(22, new Point3f(0, .95f, .05f));
			a.setColor(22, new Color3f(1, 0, 0));
			a.setCoordinate(23, new Point3f(0, 1, 0));
			a.setColor(23, new Color3f(1, 0, 0));
			a.setCoordinate(24, new Point3f(0, .95f, -.05f));
			a.setColor(24, new Color3f(1, 0, 0));
			a.setCoordinate(25, new Point3f(0, 1, 0));
			a.setColor(25, new Color3f(1, 0, 0));

			// pfeil z achse
			a.setCoordinate(14, new Point3f(0, .05f, .95f));
			a.setColor(14, new Color3f(0, 0, 1));
			a.setCoordinate(15, new Point3f(0, 0, 1));
			a.setColor(15, new Color3f(0, 0, 1));
			a.setCoordinate(16, new Point3f(0, -.05f, .95f));
			a.setColor(16, new Color3f(0, 0, 1));
			a.setCoordinate(17, new Point3f(0, 0, 1));
			a.setColor(17, new Color3f(0, 0, 1));

			a.setCoordinate(26, new Point3f(.05f, 0, .95f));
			a.setColor(26, new Color3f(0, 0, 1));
			a.setCoordinate(27, new Point3f(0, 0, 1));
			a.setColor(27, new Color3f(0, 0, 1));
			a.setCoordinate(28, new Point3f(-.05f, 0, .95f));
			a.setColor(28, new Color3f(0, 0, 1));
			a.setCoordinate(29, new Point3f(0, 0, 1));
			a.setColor(29, new Color3f(0, 0, 1));
			// hinzufuegen
			g.addChild(new Shape3D(a));

		}

		Background b = new Background(new Color3f(.20f, .20f, .20f));
		b.setApplicationBounds(new BoundingSphere(new Point3d(), 1000.0));
		b.setCapability(Background.ALLOW_COLOR_WRITE);
		g.addChild(b);

		AmbientLight lightA = new AmbientLight();
		lightA
				.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0),
						15));
		g.addChild(lightA);

		DirectionalLight lightD1 = new DirectionalLight();
		lightD1.setDirection(new Vector3f(-1, -1, -1));
		lightD1.setInfluencingBounds(new BoundingSphere(new Point3d(1, 1, 1),
				15));
		g.addChild(lightD1);

		// g.compile();
		return g;
	}

	/**
	 * Erstellt ein Plane mit gegebener katenlaenge im ursprung und verschiebt
	 * es um height auf der z-achse
	 * 
	 * @param size
	 *            Die Kantenlaenge
	 * @param height
	 *            Die z koordinate fuer das erstellte plane
	 * @return Ein Plane mit den spezifizierten Parametern
	 */
	private Shape3D getPlane(float size, float height, Color4f color) {
		QuadArray e1 = new QuadArray(4, QuadArray.COORDINATES
				| QuadArray.COLOR_4);
		e1.setCoordinate(0, new Point3f(-size, -size, height));
		e1.setColor(0, color);
		e1.setCoordinate(1, new Point3f(size, -size, height));
		e1.setColor(1, color);
		e1.setCoordinate(2, new Point3f(size, size, height));
		e1.setColor(2, color);
		e1.setCoordinate(3, new Point3f(-size, size, height));
		e1.setColor(3, color);
		e1.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		e1.setCapability(GeometryArray.ALLOW_COLOR_READ);
		Appearance app = new Appearance();
		TransparencyAttributes ta = new TransparencyAttributes();
		ta.setTransparencyMode(ta.NICEST);
		ta.setTransparency(.9f);
		app.setTransparencyAttributes(ta);
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(pa.CULL_NONE);
		app.setPolygonAttributes(pa);
		Shape3D s = new Shape3D(e1);
		s.setAppearance(app);
		s.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		s.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		return s;
	}

	/**
	 * Erstellt eine neue Appearence mit gegebner farbe
	 * 
	 * @param c
	 *            Die Farbe mit der die Appearence erstellt wird
	 * @return Eine Appearence
	 */
	public static Appearance getAppearence(Color3f c) {
		Appearance app = new Appearance();
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		ColoringAttributes ca = new ColoringAttributes(0, 0, 0, 0);
		ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
		app.setColoringAttributes(ca);

		Material mat = new Material();
		mat.setCapability(Material.ALLOW_COMPONENT_WRITE);
		mat.setCapability(Material.ALLOW_COMPONENT_READ);
		mat.setDiffuseColor(c);
		// System.out.println(c);
		Color3f ambient = new Color3f(c);
		ambient.scale(.7f);
		// System.out.println(ambient);
		mat.setAmbientColor(ambient);
		app.setMaterial(mat);

		return app;
	}

	/*
	 * Capabilities fuers picking setzen
	 */
	public void enablePicking(Node node) {
		node.setPickable(true);
		node.setCapability(Node.ENABLE_PICK_REPORTING);
		try {
			Group group = (Group) node;
			for (Enumeration e = group.getAllChildren(); e.hasMoreElements();) {
				Object o = e.nextElement();
				if (o instanceof Node)
					enablePicking((Node) o);
			}
		} catch (ClassCastException e) {
		}
		try {
			Shape3D shape = (Shape3D) node;
			PickTool.setCapabilities(node, PickTool.INTERSECT_FULL);
			for (Enumeration e = shape.getAllGeometries(); e.hasMoreElements();) {
				Object o = e.nextElement();
				if (o instanceof Geometry) {
					Geometry g = (Geometry) o;
					g.setCapability(g.ALLOW_INTERSECT);
				}
			}
		} catch (ClassCastException e) {
		} catch (RestrictedAccessException e) {
		}
	}

	private void setPlaneColor(Shape3D plane, Color4f color) {
		QuadArray planecolors = (QuadArray) plane.getGeometry(0);
		planecolors.setColor(0, color);
		planecolors.setColor(1, color);
		planecolors.setColor(2, color);
		planecolors.setColor(3, color);
		plane.setGeometry(planecolors);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		/*
		 * Hier wird auf die Checkboxen reagiert, die die Sichtbarkeit der
		 * Graphen regeln
		 */
		for (int i = 0; i < this.checkboxes.length; i++) {
			if (e.getSource() == this.checkboxes[i]) {
				if (this.checkboxes[i].getState())
					this.switches[i].setWhichChild(Switch.CHILD_ALL); // visibleNodes.set(i);
				else
					this.switches[i].setWhichChild(Switch.CHILD_NONE); // this.visibleNodes.clear(i);
			}
		}

		/*
		 * Hier wird auf die Checkboxen reagiert, die die Sichtbarkeit der
		 * Ebenen regeln
		 */
		for (int i = 0; i < this.plane_checkboxes.length; i++) {
			if (e.getSource() == this.plane_checkboxes[i]) {
				// setzt den Alpha-Wert der jeweiligen Ebene auf transparent
				if (!this.plane_checkboxes[i].getState()) {
					this.setPlaneColor(planes[i], new Color4f(1.0f, 1.0f, 1.0f,
							0.0f));
				} else {
					if (i == 0)
						this.setPlaneColor(planes[i], new Color4f(1.0f, 0.0f,
								0.0f, 1.0f)); // rot f�r die zentrale Ebene
					else
						this.setPlaneColor(planes[i], new Color4f(0.0f, 1.0f,
								0.0f, 1.0f)); // sonst gr�n
				}
			}

		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		/*
		 * Hier wird das picking ausgefuehrt und die farbe des gepickten nodes
		 * veraendert. Der zuvor gepickte node wird in seinen ausgangszustand
		 * zurueckversetzt.
		 */
		pickCanvas.setShapeLocation(e);
		PickResult result = pickCanvas.pickClosest();
		if (result == null) {
			if (oldSelected != null) {
				oldSelected.getAppearance().getMaterial().setAmbientColor(
						oldColor);
				oldSelected.getAppearance().getMaterial().setDiffuseColor(
						oldColor);
			}
			// System.out.println("Nothing picked");
			// Transform3D transform = new Transform3D();
			// orbit.getLocalToVworld(transform);
			// setObjTransformOrientation(transform);
		} else {
			Primitive p = (Primitive) result.getNode(PickResult.PRIMITIVE);
			if (p != null) {
				try {
					// System.out.println(p.getClass().getName());
					if (oldSelected != null) {
						oldSelected.getAppearance().getMaterial()
								.setAmbientColor(oldColor);
						oldSelected.getAppearance().getMaterial()
								.setDiffuseColor(oldColor);
					}
					oldColor = new Color3f();
					p.getAppearance().getMaterial().getAmbientColor(oldColor);
					oldSelected = p;
					p.getAppearance().getMaterial().setAmbientColor(
							selectedColor);
					p.getAppearance().getMaterial().setDiffuseColor(
							selectedColor);

				} catch (CapabilityNotSetException cE) {
					// Keine Änderung der Farbe
				}

				Transform3D translation = new Transform3D();
				translation = getViewTransformTransformation();

				// Reset um neuen Knoten anzusteuern
				orbit.goHome();
				objTransform.setTransform(new Transform3D());
				objTransform_orientation.setTransform(new Transform3D());
				orbit.setRotationCenter(new Point3d(0, 0, 0));
				setRotationCenter();
				centerSelf();

				// Rotationszentrum auf Node setzen
				Transform3D transform = new Transform3D();
				p.getLocalToVworld(transform);
				Matrix4d matrix = new Matrix4d();
				transform.get(matrix);
				orbit.setRotationCenter(new Point3d(matrix.m03, matrix.m13,
						matrix.m23));
				setRotationCenter();

				setViewTransform(translation);

				// den Graphen an dem Knoten ausrichten
				transformation.setInterpolation(true);
				setTransform(transformation.CENTER);

				this.repaint();

			} else {
				// System.out.println("Nix");
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

		timer = new Timer();
		task = new Task();
		timer.schedule(task, 100, 100);

	}

	class Task extends TimerTask {
		// Orbit Rotation an OrientationFrame anpassen
		@Override
		public void run() {
			Transform3D transform = new Transform3D();
			transform = getViewTransformTransformation();
			Matrix3d matrix = new Matrix3d();
			transform.getRotationScale(matrix);
			matrix.invert();
			Transform3D orientationTransform = new Transform3D();
			orientationTransform.setIdentity();
			orientationTransform.setRotation(matrix);
			setObjTransformOrientation(orientationTransform);
		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (timer != null) {
			timer.cancel();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String event = e.getActionCommand();

		if ("axis_rotation".equals(event)) {
			orientationFrame.dispose();

			orientationFrame.add(can_orientation);

			// Layouten, zentrieren und sichtbar machen
			orientationFrame.pack();
			orientationFrame.setVisible(true);
			orientationFrame.setAlwaysOnTop(true);
			orientationFrame.setResizable(false);
			orientationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		if ("zoomIn".equals(event)) {
			transformation.setInterpolation(true);
			setTransform(transformation.ZOOM_IN);

		}
		if ("zoomOut".equals(event)) {
			transformation.setInterpolation(true);
			setTransform(transformation.ZOOM_OUT);

		}
		if ("move_left".equals(event)) {
			transformation.setInterpolation(true);
			setTransform(transformation.MOVE_LEFT);

		}
		if ("move_right".equals(event)) {
			transformation.setInterpolation(true);
			setTransform(transformation.MOVE_RIGHT);

		}
		if ("move_up".equals(event)) {
			transformation.setInterpolation(true);
			setTransform(transformation.MOVE_UP);

		}
		if ("move_down".equals(event)) {
			transformation.setInterpolation(true);
			setTransform(transformation.MOVE_DOWN);

		}
		if ("rotate_left".equals(event)) {
			transformation.setInterpolation(true);
			if (transformation.getTransform() > 5) {
				Transform3D translation = new Transform3D();
				translation.setIdentity();
				Vector3d trans = new Vector3d();
				getObjTransformTransformation().get(trans);
				trans.negate();
				translation.setTranslation(trans);
				translation.mul(getObjTransformTransformation());
				setObjTransform(translation);
			}
			setTransform(transformation.ROT_LEFT);

		}
		if ("rotate_right".equals(event)) {
			transformation.setInterpolation(true);
			if (transformation.getTransform() > 5) {
				Transform3D translation = new Transform3D();
				translation.setIdentity();
				Vector3d trans = new Vector3d();
				getObjTransformTransformation().get(trans);
				trans.negate();
				translation.setTranslation(trans);
				translation.mul(getObjTransformTransformation());
				setObjTransform(translation);
			}
			setTransform(transformation.ROT_RIGHT);

		}
		if ("rotate_up".equals(event)) {
			transformation.setInterpolation(true);
			if (transformation.getTransform() > 5) {
				Transform3D translation = new Transform3D();
				translation.setIdentity();
				Vector3d trans = new Vector3d();
				getObjTransformTransformation().get(trans);
				trans.negate();
				translation.setTranslation(trans);
				translation.mul(getObjTransformTransformation());
				setObjTransform(translation);
			}
			setTransform(transformation.ROT_UP);

		}
		if ("rotate_down".equals(event)) {
			transformation.setInterpolation(true);
			if (transformation.getTransform() > 5) {
				Transform3D translation = new Transform3D();
				translation.setIdentity();
				Vector3d trans = new Vector3d();
				getObjTransformTransformation().get(trans);
				trans.negate();
				translation.setTranslation(trans);
				translation.mul(getObjTransformTransformation());
				setObjTransform(translation);
			}
			setTransform(transformation.ROT_DOWN);
		}
		if ("rotate_forward".equals(event)) {
			transformation.setInterpolation(true);
			if (transformation.getTransform() > 5) {
				Transform3D translation = new Transform3D();
				translation.setIdentity();
				Vector3d trans = new Vector3d();
				getObjTransformTransformation().get(trans);
				trans.negate();
				translation.setTranslation(trans);
				translation.mul(getObjTransformTransformation());
				setObjTransform(translation);
			}
			setTransform(transformation.ROT_FORWARD);

		}
		if ("rotate_backward".equals(event)) {
			transformation.setInterpolation(true);
			if (transformation.getTransform() > 5) {
				Transform3D translation = new Transform3D();
				translation.setIdentity();
				Vector3d trans = new Vector3d();
				getObjTransformTransformation().get(trans);
				trans.negate();
				translation.setTranslation(trans);
				translation.mul(getObjTransformTransformation());
				setObjTransform(translation);
			}
			setTransform(transformation.ROT_BACKWARD);
		}
		if ("move_home".equals(event)) {
			// deaktiviert die aktuelle Transformation und
			// springt zum Ursprung zurück
			transformation.setTransform(-1);
			orbit.goHome();
			Transform3D transform = new Transform3D();
			objTransform.setTransform(transform);
			objTransform_orientation.setTransform(transform);
			orbit.setRotationCenter(new Point3d(0, 0, 0));
			setRotationCenter();
			centerSelf();
		}
		if ("screen_cap".equals(event)) {

			// Mit dem Robotor Objekt wird ein Screenshot erstellt
			// Diese Screenshot wird in einem BufferedImage gespeichert und
			// über
			// writeFile() in die gewünschte Datei geschrieben
			Robot robot;
			try {
				robot = new Robot();
				Point point = this.getLocationOnScreen();
				Rectangle rect = new Rectangle();
				Dimension dim = can.getSize();
				if (this.MAXIMIZED_BOTH != 0) {
					rect.setBounds((int) point.getX() + 4,
							(int) point.getY() + 79, (int) dim.getWidth(),
							(int) dim.getHeight());
				} else {
					rect.setBounds((int) point.getX() + 4,
							(int) point.getY() + 87, (int) dim.getWidth(),
							(int) dim.getHeight());
				}
				BufferedImage bufferedImage = robot.createScreenCapture(rect);
				new WriteGraphPicture().writeFile(bufferedImage);

			} catch (AWTException e1) {
				e1.printStackTrace();
			}

		}

	}

	@Override
	public void keyPressed(KeyEvent e) {

		transformation.setInterpolation(false);
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			setTransform(transformation.ZOOM_IN);
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			setTransform(transformation.ZOOM_OUT);
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			setTransform(transformation.ROT_LEFT);
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			setTransform(transformation.ROT_RIGHT);
		}
		if ((e.getKeyCode() == KeyEvent.VK_PAGE_UP)
				|| (e.getKeyCode() == KeyEvent.VK_Q)) {
			setTransform(transformation.ROT_UP);
		}
		if ((e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
				|| (e.getKeyCode() == KeyEvent.VK_A)) {
			setTransform(transformation.ROT_DOWN);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	@Override
	public void windowActivated(WindowEvent arg0) {

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		orientationFrame.dispose();

	}

	@Override
	public void windowClosing(WindowEvent arg0) {

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {

	}

	@Override
	public void windowIconified(WindowEvent arg0) {

	}

	@Override
	public void windowOpened(WindowEvent arg0) {

	}
}

/**
 * Transformations Klasse: Berechnet verschiedene Transformationen des Compare3D
 * Universums zur Laufzeit
 * 
 * @author srubert
 * 
 */
class Transformation extends Thread {

	private final CompareGraphs3D compare;
	private final boolean singleGraph;

	// Variablen für die verschiedenen Transformationen
	public final int ROT_LEFT = 0;
	public final int ROT_RIGHT = 1;
	public final int ROT_UP = 2;
	public final int ROT_DOWN = 3;
	public final int ROT_FORWARD = 4;
	public final int ROT_BACKWARD = 5;
	public final int MOVE_LEFT = 6;
	public final int MOVE_RIGHT = 7;
	public final int MOVE_UP = 8;
	public final int MOVE_DOWN = 9;
	public final int ZOOM_IN = 10;
	public final int ZOOM_OUT = 11;
	public final int CENTER = 12;

	// Es findet keine Transformation statt
	private int transformVariant = -1;

	// Legt fest ob die Bewegung kontinuierlich läuft
	boolean interpolated = false;

	@Override
	public void run() {
		transform();
	}

	public void setInterpolation(boolean interpolated) {
		this.interpolated = interpolated;
	}

	/**
	 * Führt die notwendigen Transformationen durch, welche zur Laufzeit über
	 * transformVariant spezifiziert werden
	 * 
	 * @param none
	 */
	private void transform() {
		Transform3D tempTransform = new Transform3D();
		Transform3D tempTransformOrientation = new Transform3D();
		while (true) {

			if (interpolated) {
				tempTransform = this.compare.getObjTransformTransformation();
				tempTransformOrientation = this.compare
						.getObjTransformOrientationTransformation();
			} else {
				tempTransform = this.compare.getViewTransformTransformation();
			}

			// aktuelle Translation aus der TransformGroup auslesen
			Vector3d currentTranslation = new Vector3d();
			tempTransform.get(currentTranslation);
			Vector3d translate = new Vector3d();

			// aktuelle Skalierung aus der TransformGroup auslesen
			Vector3d currentScale = new Vector3d();
			tempTransform.getScale(currentScale);

			// aktueller Mittelpunkt der TransformGroup
			Transform3D rotCenter = new Transform3D();
			Transform3D rotCenterOrientation = new Transform3D();
			rotCenter.setIdentity();
			rotCenterOrientation.setIdentity();
			Point3d center = compare.getRotationCenter();
			if (interpolated) {
				rotCenter.setTranslation(new Vector3d(-center.x, -center.y,
						-center.z));
			} else {
				rotCenter.setTranslation(new Vector3d(0, 0, 0));
			}

			// nächste Transformationsart auslesen und Transform3D Objekt
			// passend manipulieren
			int action = getTransform();

			switch (action) {
			case -1:
				break;
			case ROT_LEFT:
				tempTransform = rotate(tempTransform, ROT_LEFT, 0.01,
						rotCenter, false);
				break;
			case ROT_RIGHT:
				tempTransform = rotate(tempTransform, ROT_RIGHT, 0.01,
						rotCenter, false);
				break;
			case ROT_UP:
				tempTransform = rotate(tempTransform, ROT_UP, 0.01, rotCenter,
						false);
				break;
			case ROT_DOWN:
				tempTransform = rotate(tempTransform, ROT_DOWN, 0.01,
						rotCenter, false);
				break;
			case ROT_FORWARD:
				tempTransform = rotate(tempTransform, ROT_FORWARD, 0.01,
						rotCenter, false);
				break;
			case ROT_BACKWARD:
				tempTransform = rotate(tempTransform, ROT_BACKWARD, 0.01,
						rotCenter, false);
				break;
			case MOVE_LEFT:
				translate.set(0.08, 0, 0);
				break;
			case MOVE_RIGHT:
				translate.set(-0.08, 0, 0);
				break;
			case MOVE_UP:
				translate.set(0, 0.08, 0);
				break;
			case MOVE_DOWN:
				translate.set(0, -0.08, 0);
				break;
			case ZOOM_IN:
				translate.set(0, 0, -0.08);
				break;
			case ZOOM_OUT:
				translate.set(0, 0, 0.08);
				break;
			case CENTER:
				tempTransform = rotate(tempTransform, CENTER, 0.01, rotCenter,
						false);
				break;
			}
			switch (action) {
			case -1:
				break;
			case ROT_LEFT:
				tempTransformOrientation = rotate(tempTransformOrientation,
						ROT_LEFT, 0.01, rotCenterOrientation, true);
				break;
			case ROT_RIGHT:
				tempTransformOrientation = rotate(tempTransformOrientation,
						ROT_RIGHT, 0.01, rotCenterOrientation, true);
				break;
			case ROT_UP:
				tempTransformOrientation = rotate(tempTransformOrientation,
						ROT_UP, 0.01, rotCenterOrientation, true);
				break;
			case ROT_DOWN:
				tempTransformOrientation = rotate(tempTransformOrientation,
						ROT_DOWN, 0.01, rotCenterOrientation, true);
				break;
			case ROT_FORWARD:
				tempTransformOrientation = rotate(tempTransformOrientation,
						ROT_FORWARD, 0.01, rotCenterOrientation, true);
				break;
			case ROT_BACKWARD:
				tempTransformOrientation = rotate(tempTransformOrientation,
						ROT_BACKWARD, 0.01, rotCenterOrientation, true);
				break;
			}
			// Führt unterschiedliche Transformationen aus, je nach gewählter
			// "action"
			if (action == -1) {
				if (singleGraph) {
					compare.setTitle("3D View");
				} else {
					compare.setTitle("Compare 3D");
				}

			} else if ((action >= 0) && (action <= 5)) {
				if (singleGraph) {
					compare.setTitle("3D View [Rotating ...]");
				} else {
					compare.setTitle("Compare 3D [Rotating ...]");
				}

				if (interpolated) {
					this.compare.setObjTransform(tempTransform);
					this.compare
							.setObjTransformOrientation(tempTransformOrientation);
				} else {
					this.compare.setViewTransform(tempTransform);
					setTransform(-1);
				}

			} else if ((action > 5) && (action <= 9)) {
				if (singleGraph) {
					compare.setTitle("3D View [Translating ...]");
				} else {
					compare.setTitle("Compare 3D [Translating ...]");
				}

				Transform3D translation = new Transform3D();
				translation.setIdentity();
				translation.setTranslation(translate);
				tempTransform.mul(translation);
				if (interpolated) {
					this.compare.setObjTransform(tempTransform);
					// Point3d rotPoint = new Point3d();
					// double[] pointArray = new double[3];
					// translate.get(pointArray);
					// rotPoint.add(new Point3d(pointArray));
					// this.compare.getOrbit().setRotationCenter(rotPoint);
					// this.compare.setRotationCenter();
				} else {
					this.compare.setViewTransform(tempTransform);
					setTransform(-1);
				}
			} else {
				if (singleGraph) {
					compare.setTitle("3D View [Scaling ...]");
				} else {
					compare.setTitle("Compare 3D [Scaling ...]");
				}
				Transform3D translation = new Transform3D();
				translation.setIdentity();
				translation.setTranslation(translate);
				tempTransform.mul(translation);

				if (interpolated) {
					this.compare.setObjTransform(tempTransform);
				} else {
					this.compare.setViewTransform(tempTransform);
					setTransform(-1);
				}

			}

			if (action == CENTER) {
				setTransform(-1);
			}

			// this.compare.setObjTransformOrientation(tempTransformOrientation);

			// Canvas3D can = this.compare.getOrientationCanvas();
			// Image image = can.createImage(100, 100);
			//
			// this.compare.setOrientationCanvas(can);

			try {
				sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Erzeugt ein neues Transformation Objekt
	 * 
	 * @param compare
	 *            : aktuelles Compare3D Objekt
	 */
	public Transformation(CompareGraphs3D compare, boolean singleGraph) {
		this.compare = compare;
		this.singleGraph = singleGraph;
	}

	/**
	 * Setzt die aktuell benötigte Transformationsart
	 * 
	 * @param transformVariant
	 */
	public void setTransform(int transformVariant) {
		this.transformVariant = transformVariant;
	}

	/**
	 * Gibt die aktuelle Transformationsart zurück
	 * 
	 * @return transformVariant
	 */
	public int getTransform() {
		return transformVariant;
	}

	private Transform3D rotate(Transform3D tempTransform, int Axis,
			double angle, Transform3D rotCenter, boolean orientation) {

		Vector3d vec = new Vector3d();
		Transform3D transform = new Transform3D();

		Matrix3d rotMatrix = new Matrix3d();
		rotMatrix.setIdentity();

		if (Axis == ROT_LEFT) {

			vec.set(0, 1, 0);

		} else if (Axis == ROT_RIGHT) {

			vec.set(0, -1, 0);

		} else if (Axis == ROT_UP) {

			vec.set(1, 0, 0);

		} else if (Axis == ROT_DOWN) {

			vec.set(-1, 0, 0);

		} else if (Axis == ROT_FORWARD) {

			vec.set(0, 0, 1);

		} else if (Axis == ROT_BACKWARD) {

			vec.set(0, 0, -1);

		} else {

			// nur zum aktuellen Node zentrieren
			// Vektor willkürlich gewählt

		}

		// Rotation um den Einheitsvektor
		rotMatrix.setElement(0, 0, Math.cos(angle) + Math.pow(vec.x, 2)
				* (1 - Math.cos(angle)));
		rotMatrix.setElement(0, 1, vec.x * vec.y * (1 - Math.cos(angle))
				- vec.z * Math.sin(angle));
		rotMatrix.setElement(0, 2, vec.x * vec.z * (1 - Math.cos(angle))
				+ vec.y * Math.sin(angle));

		rotMatrix.setElement(1, 0, vec.y * vec.x * (1 - Math.cos(angle))
				+ vec.z * Math.sin(angle));
		rotMatrix.setElement(1, 1, Math.cos(angle) + Math.pow(vec.y, 2)
				* (1 - Math.cos(angle)));
		rotMatrix.setElement(1, 2, vec.y * vec.z * (1 - Math.cos(angle))
				- vec.x * Math.sin(angle));

		rotMatrix.setElement(2, 0, vec.z * vec.x * (1 - Math.cos(angle))
				- vec.y * Math.sin(angle));
		rotMatrix.setElement(2, 1, vec.z * vec.y * (1 - Math.cos(angle))
				+ vec.x * Math.sin(angle));
		rotMatrix.setElement(2, 2, Math.cos(angle) + Math.pow(vec.z, 2)
				* (1 - Math.cos(angle)));

		// Translation zum Ursprung
		Matrix4d matrix = new Matrix4d();
		tempTransform.get(matrix);

		Vector3d trans = new Vector3d();
		tempTransform.get(trans);

		if (!orientation) {
			if (interpolated)
				tempTransform.setTranslation(new Vector3d(0, 0, 0));
		}

		// System.out.println("TempTransform Translations Komponente : " +
		// matrix.m03 + " " + matrix.m13 + " " + matrix.m23 );

		// Rotation im Ursprung
		transform.set(rotMatrix);
		tempTransform.mul(transform);

		// Translation zum aktuellen Rotationszentrum
		if (!orientation) {
			if (interpolated) {
				tempTransform.mul(rotCenter);
				// tempTransform.setTranslation(trans);
			}
		}

		if (Axis == CENTER) {
			this.compare.getOrbit().setRotationCenter(new Point3d(0, 0, 0));
		}

		return tempTransform;
	}

}