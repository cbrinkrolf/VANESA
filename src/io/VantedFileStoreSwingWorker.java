/*
 *  Copyright (c) 2011 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the GNU Lesser Public License v2.1
 *  which accompanies this distribution, and is available at
 *  http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 *  Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser and WebDAV
 */
package io;

import graph.GraphInstance;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.CountDownLatch;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import xmlOutput.sbml.JSBMLoutput;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.ErrorDialog;

public class VantedFileStoreSwingWorker extends SwingWorker<Object, Object> {

	private ClientPrimaryDataFile edalFile;
	//private JProgressBar fileProgressBar;
	private CountDownLatch latch;

	public VantedFileStoreSwingWorker(ClientPrimaryDataFile edalFile) {

		this.edalFile = edalFile;
		//this.fileProgressBar = fileProgressbar;
		this.latch = new CountDownLatch(1);
	}

	@Override
	protected Object doInBackground() throws Exception {
		System.out.println("do in background");

		try {
			PipedInputStream pin = new PipedInputStream();
			PipedOutputStream pout = new PipedOutputStream(pin);

			//CountDownLatch newLatch = new CountDownLatch(1);

			StreamOutputToInputThreadProgress thread = new StreamOutputToInputThreadProgress(this.edalFile, pin, latch);

			thread.start();
			//EdalAddon.executor.execute(thread);

			 JSBMLoutput jsbmlOutput = new JSBMLoutput(pout, new GraphInstance().getPathway());
			 System.out.println("######################"+jsbmlOutput.generateSBMLDocument());

			/** wait till writing is finished **/
			 System.out.println("vor latch");
			latch.await();
			System.out.println("nach latch");

		} catch (IOException | InterruptedException e) {
			ErrorDialog.showError(e);
		}
		System.out.println("do in background end");
		return null;
	}

	@Override
	protected void done() {
		System.out.println("done");
		this.latch.countDown();
		System.out.println("done end");
	}

	private class StreamOutputToInputThreadProgress extends Thread {
		ClientPrimaryDataFile fileToUpload = null;
		PipedInputStream pipedIn = null;
		CountDownLatch latch = null;
		//JProgressBar fileProgressBar;

		public StreamOutputToInputThreadProgress(ClientPrimaryDataFile fileToUpload, PipedInputStream pipedIn, CountDownLatch latch) {
			this.fileToUpload = fileToUpload;
			this.pipedIn = pipedIn;
			this.latch = latch;
		}

		@Override
		public void run() {
			try {
				System.out.println("run1");
				//this.fileProgressBar.setIndeterminate(true);
				//this.fileProgressBar.setString(this.vantedOutput);

				//VantedFileProgressInputStream progressInputStream = new VantedFileProgressInputStream(this.fileProgressBar, this.pipedIn);

				//VantedFileProgressInputStream progressInputStream = new VantedFileProgressInputStream(this.pipedIn);
				
				try{
					System.out.println("run2");
					System.out.println(pipedIn.available());
				this.fileToUpload.store(this.pipedIn);
				System.out.println("run3");
				}catch(Exception e){
					
					e.printStackTrace();
				}
				
				this.pipedIn.close();
				System.out.println("run4");
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.latch.countDown();
			System.out.println("run end");
		}

	}
}