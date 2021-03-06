package io;

import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingleton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

import javax.security.auth.Subject;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import xmlOutput.sbml.JSBMLoutput;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalFileChooser;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalAbstractFileFilter;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalFileNameExtensionFilter;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class EdalSaveDialog {

	/*
	 * Copyright (c) 2014 Leibniz Institute of Plant Genetics and Crop Plant
	 * Research (IPK), Gatersleben, Germany. All rights reserved. This program
	 * and the accompanying materials are made available under the terms of the
	 * Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND
	 * 4.0) which accompanies this distribution, and is available at
	 * http://creativecommons.org/licenses/by-nd/4.0/
	 * 
	 * Contributors: Leibniz Institute of Plant Genetics and Crop Plant Research
	 * (IPK), Gatersleben, Germany - RMI Client, FileChooser and WebDAV
	 */

	private static final int SERVER_PORT = 2000;
	private static final String SERVER_ADDRESS = "bit-249.ipk-gatersleben.de";

	public EdalSaveDialog() {

		try {
			this.openWindow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void openWindow() throws Exception {
		/** use windows or unix login **/
		Subject subject = EdalHelpers.authenticateSampleUser();

		/** alternatively use Google+ login **/
		// Subject subject = EdalHelpers.authenticateGoogleUser("", 3128);

		/** connect to running EDAL server on "bit-249" **/
		ClientDataManager dataManagerClient = new ClientDataManager(
				SERVER_ADDRESS, SERVER_PORT, new Authentication(subject));

		// JFrame jf = new JFrame();

		EdalFileChooser dialog = new EdalFileChooser(
				MainWindowSingleton.getInstance(), dataManagerClient);
		dialog.setLocationRelativeTo(MainWindowSingleton.getInstance());
		dialog.setFileSelectionMode(EdalFileChooser.FILES_AND_DIRECTORIES);
		dialog.showConnectionButton(false);

		// dialog.setFileFilter(new EdalFileNameExtensionFilter("sbml",
		// "sbml"));

		int result = dialog.showSaveDialog();
		// dialog.setVisible(true);

		// System.out.println(System.getProperty("user.home"));
		// System.out.println(dialog.getSelectedFile().getName());
		if (result == EdalFileChooser.APPROVE_OPTION) {
			// clicked ok
			if (dialog.getSelectedFile() != null) {
				// System.out.println(dialog.g);
				ClientPrimaryDataEntity de = dialog.getSelectedFile();
				ClientPrimaryDataFile df = null;

				if (de.isDirectory()) {
					// System.out.println("dir chosen");
					ClientPrimaryDataDirectory dd = (ClientPrimaryDataDirectory) de;

					// ClientPrimaryDataFile f = new ClientPrimaryDataFile(de,
					// subject);
					String name = this.dirChosen();
					// System.out.println(name);
					if (name != null) {
						if (dd.exist(name)) {
							// System.out.println("file exists");

							// override?
							int opt = JOptionPane.showConfirmDialog(
									MainWindowSingleton.getInstance(),
									"Override file and create new version?",
									"alert", JOptionPane.OK_CANCEL_OPTION);
							if (opt == JOptionPane.OK_OPTION) {
								if (!dd.getPrimaryDataEntity(name)
										.isDirectory()) {
									df = (ClientPrimaryDataFile) dd
											.getPrimaryDataEntity(name);
								}
							}
						} else {

							df = dd.createPrimaryDataFile(name);
						}
					}
				} else {
					df = (ClientPrimaryDataFile) de;
				}

				// System.out.println(dialog.getSelectedFile().getName());

				// JSBMLoutput jsbmlOutput = new JSBMLoutput(file, new
				// GraphInstance().getPathway());

				// read and stores data to harddisk
				// ((ClientPrimaryDataFile) dialog.getSelectedFile())
				// .read(new FileOutputStream(Paths.get(
				// System.getProperty("user.home"), "tex.exe").toFile()));

				// String test = "testtest";
				// PipedInputStream pin = new PipedInputStream();
				// PipedOutputStream pout = new PipedOutputStream(pin);
				if (df != null) {
					// System.out.println("1");

					// PrimaryDataDirectory rootDirectory =
					// DataManager.getRootDirectory(dialog.get subject);
					try {
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						JSBMLoutput jsbmlOutput = new JSBMLoutput(os,
								new GraphInstance().getPathway());
						// System.out.println("######################"
						// + jsbmlOutput.generateSBMLDocument());
						jsbmlOutput.generateSBMLDocument();
						os.flush();
						// System.out.println(os.size());
						os.close();
						byte[] b = os.toByteArray();
						// System.out.println(b.length);
						ByteArrayInputStream is = new ByteArrayInputStream(b);
						// System.out.println("av: " + is.available());
						df.store(is);
						// throw new Exception("bla");
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(
								MainWindowSingleton.getInstance(),
								"Saving was not successful. An error occured!");
					}
				}

				// VantedFileStoreSwingWorker v = new
				// VantedFileStoreSwingWorker(df);
				// v.execute();
				// FileOutputStream fop = new FileOutputStream(new
				// File("test.txt"));
				// in.r
				// fop.write(in.);
				// ClientPrimaryDataFile datafile = new ClientPrimaryDataFile(,
				// dataManagerClient);
				/*
				 * new Thread( new Runnable(){ public void run(){ try {
				 * 
				 * df.store(in); in.close(); } catch (PrimaryDataFileException |
				 * PrimaryDataEntityVersionException | IOException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); } }
				 * }).start();
				 * 
				 * new Thread( new Runnable(){ public void run(){ JSBMLoutput
				 * jsbmlOutput = new JSBMLoutput(out, new
				 * GraphInstance().getPathway());
				 * System.out.println("######################"
				 * +jsbmlOutput.generateSBMLDocument()); } } ).start();
				 */

				// System.out.println("2");
				// in.close();
				// out.close();
				// final PipedInputStream pin = getInputStream();
				// final PipedOutputStream pout = getOutputStream();

				/*
				 * final CountDownLatch latch = new CountDownLatch(1);
				 * 
				 * InputStream in = new InputStream() {
				 * 
				 * @Override public int read() throws IOException { return
				 * pin.read(); }
				 * 
				 * @Override public void close() throws IOException {
				 * super.close(); latch.countDown(); } };
				 * 
				 * 
				 * OutputStream out = new OutputStream(){
				 * 
				 * @Override public void write(int b) throws IOException {
				 * pout.write(b); }
				 * 
				 * @Override public void close() throws IOException {
				 * while(latch.getCount()!=0) { try { latch.await(); } catch
				 * (InterruptedException e) { //too bad } } super.close(); } };
				 */

				// give the streams to your threads, they don't know a latch
				// ever
				// existed
				// threadOne.feed(in);
				// threadTwo.feed(out);

				/*
				 * new Thread( new Runnable(){ public void run(){ try {
				 * df.store(in); } catch (PrimaryDataFileException |
				 * PrimaryDataEntityVersionException | IOException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); } }
				 * }).start();
				 * 
				 * new Thread( new Runnable(){ public void run(){
				 * 
				 * JSBMLoutput jsbmlOutput = new JSBMLoutput(out, new
				 * GraphInstance().getPathway());
				 * System.out.println("######################"
				 * +jsbmlOutput.generateSBMLDocument()); } } ).start();
				 */
				// new ByteArrayInputStream()
				// ClientPrimaryDataFile
			}
		}

	}

	private String dirChosen() {
		JPanel panel = new JPanel();
		JTextField tf = new JTextField(new GraphInstance().getPathway()
				.getName());
		tf.setColumns(20);
		panel.add(tf);

		panel.add(new JLabel(".sbml"));

		// System.out.println(new GraphInstance().getPathway().getName());
		JOptionPane pane = new JOptionPane(panel);// ,
													// JOptionPane.PLAIN_MESSAGE,
		// JOptionPane.OK_CANCEL_OPTION);
		pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = pane.createDialog(null, "Choose filename");

		dialog.setLocationRelativeTo(MainWindowSingleton.getInstance());
		dialog.setVisible(true);
		// System.out.println(pane.getValue());
		if ((int) pane.getValue() == JOptionPane.OK_OPTION) {
			return tf.getText() + ".sbml";
		} else {
			// System.out.println("null string");
			return null;
		}

	}
}
