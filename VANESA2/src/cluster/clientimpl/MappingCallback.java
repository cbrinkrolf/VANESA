package cluster.clientimpl;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import cluster.slave.IMappingCallback;
import gui.MainWindow;

public class MappingCallback extends UnicastRemoteObject implements
		IMappingCallback, Serializable {

	private static final long serialVersionUID = -4445566937473044196L;


	public MappingCallback() throws RemoteException {
		super();
	}

	@Override
	public void progressNotify(String message) throws RemoteException {
		MainWindow.progressbar.setProgressBarString(message);

	}

	@Override
	public void setMappingProgress(int mappedNodes, String experiment)
			throws RemoteException {	
		final String message = mappedNodes + " successfull mapped.";
		MainWindow.getInstance().closeProgressBar();
		
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(
						MainWindow.getInstance(),
						message,
						"Mapping done", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
	}
}
