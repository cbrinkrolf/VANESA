package cluster;

import gui.MainWindow;
import gui.MainWindowSingelton;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import dataMapping.DataMappingModelController;

public class MappingCallback extends UnicastRemoteObject implements
		IMappingCallback, Serializable {

	private static final long serialVersionUID = -4445566937473044196L;


	public MappingCallback() throws RemoteException {
		super();
	}

	@Override
	public void progressNotify(String message) throws RemoteException {
		DataMappingModelController.progressBarExport.setProgressBarString(message);

	}

	@Override
	public void setMappingProgress(int mappedNodes, String experiment)
			throws RemoteException {	
		final String message = mappedNodes + " successfull mapped.";
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(
						MainWindowSingelton.getInstance(),
						message,
						"Mapping done", JOptionPane.INFORMATION_MESSAGE);
				DataMappingModelController.reactivateUI();
			}
		});
		
	}
}
