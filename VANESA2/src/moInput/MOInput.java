package moInput;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.StringTokenizer;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.SmallMolecule;
import edu.uci.ics.jung.graph.Vertex;
import graph.CreatePathway;
import gui.MainWindowSingelton;

public class MOInput {

	private static final boolean debug = false;
	private File file = null;
	private int scale = 2;
	private int shiftx = 0, shifty = 0;
	private Hashtable<String, Vertex> mapping = new Hashtable<String, Vertex>();

	public MOInput(File file) throws IOException {
		this.file = file;
		getData();
	}

	private void addProjectDetails(Pathway pw, String title) {
		pw.setTitle(title);
		if (debug)
			System.out.println("Title: " + title);
		pw.setAuthor("OpenModelica");
	}

	private void addEdge(Pathway pw, String from, String to) {
		boolean isDirected = true;

		BiologicalEdgeAbstract graphElement = new BiologicalEdgeAbstract(pw
				.getGraph().createEdge(mapping.get(from), mapping.get(to),
						isDirected), "", "");

		// for appearing as original
		(graphElement).setReference(false);

		(graphElement).setDirected(isDirected);
		pw.addElement(graphElement);
	}

	private void addNetworkNode(Pathway pw, String name, String type, double x,
			double y, String bioName, String ec) {
		Object obj = null;

		// if (debug) System.out.println(ec+" "+brendaNode+" "+type);

		if (type.equals("Enzyme")) {// \ec/
			Enzyme e = new Enzyme(bioName, name, pw.getGraph()
					.createNewVertex());
			obj = e;
		} else if (type.equals("Reaction")) {
			// Reaction e = new
			// Reaction(bioName,name,pw.getGraph().createNewVertex());
			Enzyme e = new Enzyme(bioName, name, pw.getGraph()
					.createNewVertex());
			obj = e;
		} else {
			// Other e = new
			// Other(bioName,name,pw.getGraph().createNewVertex());
			SmallMolecule e = new SmallMolecule(bioName, name, pw.getGraph()
					.createNewVertex());
			obj = e;
		}// */

		BiologicalNodeAbstract bna = (BiologicalNodeAbstract) obj;

		bna.setIsVertex(true);
		// for appearing as original
		if (!type.equals("Enzyme"))
			bna.setReference(false);

		pw.addElement(obj);
		pw.getGraph().moveVertex(bna.getVertex(), x / scale, y / scale);
		// if (debug)
		// System.out.println("mapping: "+name+" -> "+bna.getVertex());
		mapping.put(name, bna.getVertex());
		// if (debug)
		// System.out.println("mapping : "+name+" -> "+mapping.get(name));
	}

	private void getData() throws IOException {

		if (debug)
			System.out.println("Reading mo file " + file.getName());

		RandomAccessFile reader = new RandomAccessFile(file, "r");
		Pathway pw = new CreatePathway().getPathway();

		pw.getGraph().lockVertices();
		pw.getGraph().stopVisualizationModel();

		// net name
		String line = reader.readLine();
		StringTokenizer tok = new StringTokenizer(line);
		addProjectDetails(pw, line.split(" ")[1]);

		// annotation for shift
		line = reader.readLine(); // skip annotation
		tok = new StringTokenizer(line, "{,}");
		tok.nextToken();
		shiftx = -Integer.parseInt(tok.nextToken().trim());
		shifty = -Integer.parseInt(tok.nextToken().trim());

		while (!(line = reader.readLine()).startsWith("equation")) {

			// PetriNetsBioChem.Continuous.Re_MM_11 V0(kcat = 39.9, Km = 0.1073,
			// ec_number = "1.1.1.49") annotation(Placement...
			// PetriNetsBioChem.Continuous.PC01 V1
			// annotation(Placement(transformation(x = 675.0, y =
			// 389.71143341064453, scale = 0.84), iconTransformation(x = 675.0,
			// y = 389.71143341064453, scale = 0.84)));
			// PetriNetsBioChem.Continuous.PC01 V1(biologicalName = "schnurz")
			// annotation(Placement(transformation(x = 675.0, y =
			// 389.71143341064453, scale = 0.84), iconTransformation(x = 675.0,
			// y = 389.71143341064453, scale = 0.84)));
			if (debug)
				System.out.println(line);
			if (line.length() > 1) {
				tok = new StringTokenizer(line);
				String type = tok.nextToken();
				// for parsing discretes ans stochastic, too.
				type = type.replaceAll("Discrete", "Continuous");
				type = type.replaceAll("Stochastic", "Continuous");
				String name = tok.nextToken().trim();
				String bioName = "", ec = "", kcat = "", km = "";
				if (name.contains("(")) {
					String nameWithParameters = line.split("annotation")[0];
					StringTokenizer paramTok = new StringTokenizer(
							nameWithParameters);
					paramTok.nextToken();
					name = paramTok.nextToken("(=,").trim();
					while (paramTok.hasMoreTokens()) {
						String variable = paramTok.nextToken();
						String value = "";
						// strings can contain commas
						if (variable.contains("ec")
								|| variable.contains("biologicalName")) {
							value = paramTok.nextToken("\"");
							// if (debug) System.out.println("*"+value);
							value = paramTok.nextToken("\"");
							if (debug)
								System.out.println("bio " + value);
							paramTok.nextToken(" "); // to set delimiter
						} else {
							// System.out.println(variable);
							value = paramTok.nextToken().trim();
						}
						if (value.endsWith("\""))
							value = value.substring(0, value.length() - 1);
						if (value.startsWith("\""))
							value = value.substring(1, value.length());
						if (value.length() < 2)
							value = "";
						value = value.trim();
						if (variable.contains("kcat"))
							kcat = value;
						if (variable.contains("Km"))
							km = value;
						if (variable.contains("ec"))
							ec = value;
						if (variable.contains("biologicalName"))
							bioName = value;
					}
				}
				tok = new StringTokenizer(line.split("iconTransformation")[1]);
				tok.nextToken("=,");
				double x = shiftx + Double.parseDouble(tok.nextToken().trim());
				tok.nextToken();
				double y = shifty - Double.parseDouble(tok.nextToken().trim());
				// if (type.startsWith("PetriNetsBioChem.Continuous.Re")){
				if (ec.length() > 1) {
					addNetworkNode(pw, name, "Enzyme", x, y, bioName, ec);

				} else if (type.startsWith("PetriNetsBioChem.Continuous.")) {
					String biologicalType = (type
							.startsWith("PetriNetsBioChem.Continuous.T") ? "Reaction"
							: "Other");
					addNetworkNode(pw, name, biologicalType, x, y, bioName, ec);
				} else
				// TODO mm and so
				if (debug)
					System.err.println("Found ugly node");
			}
		}
		while (!(line = reader.readLine()).startsWith("end")) {
			if (debug)
				System.out.println(line);
			tok = new StringTokenizer(line, "(.,");
			tok.nextToken();
			String from = tok.nextToken().trim();
			tok.nextToken();
			String to = tok.nextToken().trim();
			// if (debug) System.out.println("Von "+from+" nach "+to);
			addEdge(pw, from, to);
		}
		pw.getGraph().unlockVertices();
		pw.getGraph().restartVisualizationModel();
		MainWindowSingelton.getInstance().updateProjectProperties();
		MainWindowSingelton.getInstance().updateOptionPanel();

	}
}
