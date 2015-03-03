package io;

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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JProgressBar;

public class VantedFileProgressInputStream extends FilterInputStream {

	private int nread = 0;
	private int size = 0;
	private long steps = 0;

	/**
	 * Constructs an object to monitor the progress of an input stream.
	 * 
	 * @param inputStream
	 *            The input stream to be monitored.
	 */
	public VantedFileProgressInputStream(InputStream inputStream) {
		super(inputStream);
		try {
			size = inputStream.available();
			System.out.println(size);
		} catch (IOException ioe) {
			size = 0;
		}
	}

	/**
	 * Get the ProgressMonitor object being used by this stream. Normally this
	 * isn't needed unless you want to do something like change the descriptive
	 * text partway through reading the file.
	 * 
	 * @return the ProgressMonitor object used by this object
	 */

	/**
	 * Overrides <code>FilterInputStream.read</code> to update the progress
	 * monitor after the read.
	 */
	public int read() throws IOException {
		int c = in.read();
		if (c >= 0)
			++nread;

		return c;
	}

	/**
	 * Overrides <code>FilterInputStream.read</code> to update the progress
	 * monitor after the read.
	 */
	public int read(byte b[]) throws IOException {
		int nr = in.read(b);
		System.out.println("readprogress "+nr);

		if (nr > 0)
			nread += nr;
		return nr;
	}

	/**
	 * Overrides <code>FilterInputStream.read</code> to update the progress
	 * monitor after the read.
	 */
	public int read(byte b[], int off, int len) throws IOException {

		// if (nread < 100000) {
		// System.out.println("IN: " + in.available() + "\t NV: " + nread);
		// }

		String prepareCopy = "Prepare Copying : ";

		if (in.available() == size) {



		} else {

		}

		int nr = in.read(b, off, len);
		// if (nr > 0) {
		// monitor.setProgress(nread += nr);
		// }
		steps++;
		return nr;
	}

	/**
	 * Overrides <code>FilterInputStream.skip</code> to update the progress
	 * monitor after the skip.
	 */
	public long skip(long n) throws IOException {
		long nr = in.skip(n);
		if (nr > 0)
			nread += nr;
		return nr;
	}

	/**
	 * Overrides <code>FilterInputStream.close</code> to close the progress
	 * monitor as well as the stream.
	 */
	public void close() throws IOException {
		in.close();
	}

	/**
	 * Overrides <code>FilterInputStream.reset</code> to reset the progress
	 * monitor as well as the stream.
	 */
	public synchronized void reset() throws IOException {
		in.reset();
		nread = size - in.available();
	}
}