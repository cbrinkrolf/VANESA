package database.dawis;

import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import pojos.DBColumn;
import biologicalElements.InternalGraphRepresentation;
import biologicalElements.Pathway;
import database.dawis.webstart.DAWISWebstartCombiner;
import database.dawis.webstart.RemoteData;

@SuppressWarnings("unchecked")
public class CombinerSwingWorker extends SwingWorker
{

	private MyGraph myGraph;
	private ProgressBar bar;
	private Pathway pw;
	private DAWISWebstartCombiner combiner;
	private EdgeController edgeController;
	private String pathwayLink="";
	private String pathwayImage="";
	private String pathwayNumber="";
	private InternalGraphRepresentation adjazenzList;

	public CombinerSwingWorker(Pathway pw, ArrayList<DBColumn> remotecontroldata, RemoteData remoteData)
	{
		combiner=new DAWISWebstartCombiner(pw, remotecontroldata, remoteData);
		
		this.pw=pw;
	}

	@Override
	protected Object doInBackground() throws Exception
	{
		Runnable run=new Runnable()
		{
			public void run()
			{
				bar=new ProgressBar();
				bar.init(100, "   Loading Data from DAWIS ", true);
				bar.setProgressBarString("Testing relations of incoming elements");
			}
		};
		SwingUtilities.invokeLater(run);

		combiner.addElements();

		return null;
	}

	@Override
	public void done()
	{

		edgeController=new EdgeController(pw);

		pw.setLink(pathwayLink);
		pw.setImagePath(pathwayImage);
		pw.setNumber(pathwayNumber);
		myGraph=pw.getGraph();

		adjazenzList=pw.getGraphRepresentation();
		combiner.setAdjazenzList(adjazenzList);

		stopVisualizationModel();
		combiner.drawNodes();
		combiner.drawEdges(edgeController);
		startVisualizationModel();

		myGraph.changeToKKLayout();
		myGraph.normalCentering();

		MainWindow window=MainWindowSingelton.getInstance();
		updateWindow(window, bar);
		window.updateOptionPanel();
		window.setEnable(true);
	}

	private void updateWindow(MainWindow w, ProgressBar bar)
	{

		w.updateElementTree();
		w.updateSatelliteView();
		w.updateFilterView();
		// w.updateTheoryProperties();
		bar.closeWindow();
		w.setEnable(true);

	}

	/**
	 * prevents from repainting the graph in the time as the graph is beeing
	 * generated to get better performance
	 */
	private void stopVisualizationModel()
	{
		// MainWindow window = MainWindowSingelton.getInstance();
		// window.setEnable(false);
		bar.closeWindow();
		myGraph.lockVertices();
		myGraph.stopVisualizationModel();
	}

	/**
	 * start painting after generating the graph is ready
	 */
	private void startVisualizationModel()
	{
		myGraph.restartVisualizationModel();
		myGraph.unlockVertices();
	}

}
