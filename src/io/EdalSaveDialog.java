package io;

import graph.GraphInstance;
import gui.MainWindowSingleton;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.nio.file.Paths;

import javax.security.auth.Subject;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import xmlOutput.sbml.JSBMLoutput;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
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
	
	public void openWindow() throws Exception{
		/** use windows or unix login **/
		Subject subject = EdalHelpers.authenticateSampleUser();

		/** alternatively use Google+ login **/
		// Subject subject = EdalHelpers.authenticateGoogleUser("", 3128);

		/** connect to running EDAL server on "bit-249" **/
		ClientDataManager dataManagerClient = new ClientDataManager(
				SERVER_ADDRESS, SERVER_PORT, new Authentication(subject));

		JFrame jf = new JFrame();
		
		EdalFileChooser dialog = new EdalFileChooser(jf,
				dataManagerClient);

		dialog.setFileSelectionMode(EdalFileChooser.FILES_AND_DIRECTORIES);
		dialog.showConnectionButton(false);

		//dialog.setFileFilter(new EdalFileNameExtensionFilter("sbml", "sbml"));

		dialog.showSaveDialog();
		//dialog.setVisible(true);

		System.out.println(System.getProperty("user.home"));
		System.out.println(dialog.getSelectedFile().getName());
		//System.out.println(dialog.getSelectedFile().getName());

		//JSBMLoutput jsbmlOutput = new JSBMLoutput(file, new GraphInstance().getPathway());
		
		
		// read and stores data to harddisk
		//((ClientPrimaryDataFile) dialog.getSelectedFile())
		//		.read(new FileOutputStream(Paths.get(
		//				System.getProperty("user.home"), "tex.exe").toFile()));

		String test = "testtest";
		
		((ClientPrimaryDataFile)dialog.getSelectedFile()).store(new
		ByteArrayInputStream(test.getBytes("UTF-8")));

	}

}
