package database.dawis;

import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import database.dawis.gui.DAWISVertexWindow;

@SuppressWarnings("unchecked")
public class InformationUpdate extends SwingWorker{
	
	ProgressBar bar;
	Object selectedObject;
	JPanel p;
	DAWISVertexWindow w;
	
	public InformationUpdate(DAWISVertexWindow win, JPanel panel, Object object){
		p = panel;
		selectedObject = object;
		w = win;
	}

	@Override
	public void done(){
		
		MainWindow elinf = MainWindowSingelton.getInstance();
		elinf.setEnable(false);
		p.removeAll();
		w.updateInformation(selectedObject);
		
		p.setVisible(true);
		p.repaint();
		p.validate();
		updateWindow(elinf, bar);
	}
	
	private void updateWindow(MainWindow w, ProgressBar bar) {

		w.updateElementTree();
		w.updateSatelliteView();
		w.updateFilterView();
		// w.updateTheoryProperties();
		bar.closeWindow();
		w.setEnable(true);

	}


	@Override
	protected Object doInBackground() throws Exception {
		Runnable run = new Runnable() {
			public void run() {
				bar = new ProgressBar();
				bar.init(100, "   Loading Data for DAWIS-Function ", true);
				bar.setProgressBarString("Loading element information");
			}
		};
		SwingUtilities.invokeLater(run);
		return null;
	}
}
