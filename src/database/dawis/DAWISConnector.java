package database.dawis;

import java.sql.SQLException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import biologicalElements.Pathway;

/**
 * 
 * @author Olga
 *
 */

/**
 * sets pathway parameter creates vector for related objects
 */
@SuppressWarnings("unchecked")
public class DAWISConnector extends SwingWorker {

	private MyGraph myGraph;

	private Pathway pw = null;

	private String title = "";
	private String organism = "";
	private String pathwayLink = "";
	private String pathwayImage = "";
	private String pathwayNumber = "";
	private String object = "";

	String[] elem = null;

	private int searchDepth = 0;

	public boolean organismSpecific;
	public boolean toShow[] = new boolean[11];

	private ProgressBar bar;

	private ElementLoader loader;

	/**
	 * construct DAWISConnector for searching results of the object(basic mode)
	 * 
	 * @param progressBar
	 * @throws SQLException
	 */
	public DAWISConnector(ProgressBar b) {
		this.bar = b;
		for (int i = 0; i < toShow.length; i++) {
			toShow[i] = true;
		}
		this.organismSpecific = false;
	}

	/**
	 * construct DAWISConnector for searching results of the object(expert mode)
	 * 
	 * @param results
	 * @param object
	 * @throws SQLException
	 */
	public DAWISConnector(ProgressBar b, boolean[] settings) {

		this.bar = b;
		this.toShow = settings;
		this.organismSpecific = false;

	}

	/**
	 * array for showing objects
	 * 
	 * @return toShow
	 */
	public boolean[] getSettings() {
		return toShow;
	}

	/**
	 * set the depth of the search
	 * 
	 * @param searchDepth
	 */
	public void setSearchDepth(int searchDepth) {
		this.searchDepth = searchDepth;
	}

	/**
	 * set organism of interest
	 * 
	 * @param organism
	 */
	public void setOrganism(String organism) {
		this.organism = organism;
	}

	/**
	 * get organism of interest
	 * 
	 * @param acc
	 */
	public String getOrganism() {
		return this.organism;
	}

	/**
	 * set organism specification
	 * 
	 * @param specification
	 */
	public void setOrganismSpecification(boolean specification) {
		this.organismSpecific = specification;
	}

	/**
	 * set start object
	 * 
	 * @param object
	 */
	public void setObject(String object) {
		this.object = object;
	}

	/**
	 * get start object
	 * 
	 * @param object
	 */
	public String getObject() {
		return this.object;
	}

	/**
	 * create pathway of the element
	 * 
	 * @throws SQLException
	 * 
	 * @throws SQLException
	 */
	public void createPathway(String[] element) throws SQLException {

		elem = element;

	}

	/**
	 * create empty fields for pathway headings
	 */
	public void getPathwayHeadings() {

		title = "";
		pathwayLink = "";
		pathwayImage = "";
		pathwayNumber = "";

	}

	/**
	 * stop visualization
	 */
	private void stopVisualizationModel() {
		myGraph.lockVertices();
		myGraph.stopVisualizationModel();
	}

	/**
	 * start visualization
	 */
	private void startVisualizationModel() {
		bar.closeWindow();
		myGraph.unlockVertices();
		myGraph.restartVisualizationModel();
	}

	/**
	 * get the graph
	 * 
	 * @return MyGraph
	 */
	public MyGraph getGraph() {
		return myGraph;
	}

	@Override
	protected Object doInBackground() throws Exception {

		Runnable run = new Runnable() {
			public void run() {
				bar = new ProgressBar();
				bar.init(100, "   Loading Data for DAWIS-Function ", true);
				bar.setProgressBarString("Querying Database");
			}
		};
		SwingUtilities.invokeLater(run);

		// pathway headings
		getPathwayHeadings();

		// set title
		if (!elem[1].equals("")) {
			title = elem[1];
		} else {
			title = elem[0];
		}

		if (!this.organism.equals("")) {

			this.organism = this.organism.toLowerCase();
			int stringLength = this.organism.length();

			// needed because some organisms have a point at the end
			if (this.organism.endsWith(".")) {
				this.organism = this.organism.substring(0, stringLength - 1);
			}

		}

		bar.setProgressBarString("Loading elements");

		// create loader
		loader = new ElementLoader(this, toShow);

		// set element data
		String id = elem[0];
		String name = elem[1];
		String db = "";

		// diseases are only for h.sapiens known
		if (object.equals("Disease")) {
			this.organism = "h.sapiens";
		} else {
			this.organism = elem[2];
		}
		db = elem[3];

		// set relevant data in loader
		loader.setSearchDepth(searchDepth);
		loader.setOrganismSpecification(organismSpecific);
		loader.setOrganism(this.organism);

		// put known element data into the array for further search
		String[] elementData = { this.object, id, name, "", db };
		loader.getDetails(elementData, null);

		return null;
	}

	@Override
	public void done() {

		// set pathway data
		pw = new CreatePathway(title).getPathway();
		pw.setLink(pathwayLink);
		pw.setImagePath(pathwayImage);
		pw.setNumber(pathwayNumber);
		pw.setDAWISProject();
		pw.setSettings(toShow);
		pw.setOrganism(organism);
		pw.setLink(pathwayLink);
		pw.setImagePath(pathwayImage);
		pw.setNumber(pathwayNumber);
		pw.setSpecification(organismSpecific);
		myGraph = pw.getGraph();

		// create pathway
		stopVisualizationModel();
		loader.drawNodes(pw);
		loader.drawEdges(pw);
		startVisualizationModel();

		// center graph
		// change layout
		myGraph.normalCentering();
		bar.closeWindow();

		// update window
		MainWindow window = MainWindowSingelton.getInstance();
		window.updateOptionPanel();
		window.setEnable(true);
		pw.getGraph().changeToGEMLayout();
		
	}

}
