/**
 * 
 */
package graph.algorithms;

import javax.swing.SwingUtilities;

import gui.ProgressBar;

/**
 * @author Britta Niemann
 *
 */
public class DCBProgressRunnable implements Runnable {

	public ProgressBar progressBar;
	private int max;
	private String info;
	private boolean indeterminate;
	
	public DCBProgressRunnable(int max, String info, boolean indeterminate){
		
		this.max = max;
		this.info = info;
		this.indeterminate = indeterminate;



	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar = new ProgressBar();
				progressBar.init(max, info, indeterminate);
				progressBar.setProgressBarString("started");
		
			}
		});
	}
	
	public void close(){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.closeWindow();
			}
		});
	}

}
