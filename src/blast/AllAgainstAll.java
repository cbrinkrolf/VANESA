package blast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

public class AllAgainstAll implements Runnable {

	private String referenceTmpFile = "tmpFileReference.fasta";
	private String queryTmpFile = "tmpFileQuery.fasta";
	private double eValueThreshold = 1;
	private double maxSimValue = -Math.log10(Double.MIN_VALUE);

	private HashMap<String, Integer> referenceID2Position;
	private HashMap<String, Integer> queryID2Position;
	private HashMap<Integer, String> position2ReferenceID, position2QueryID;
	private DoubleMatrix2D simMatrix;
	private HashMap<String, String> mapReference, mapQuery;
	private String mode;

	public AllAgainstAll(HashMap<String, String> mapReference,
			HashMap<String, String> mapQuery, String mode) {
		this.mapQuery = mapQuery;
		this.mapReference = mapReference;
		this.mode = mode;
	}

	public void run() {

		referenceID2Position = new HashMap<String, Integer>();
		queryID2Position = new HashMap<String, Integer>();
		position2ReferenceID = new HashMap<Integer, String>();
		position2QueryID = new HashMap<Integer, String>();
		int n = mapReference.keySet().size();
		int m = mapQuery.keySet().size();
		simMatrix = new DenseDoubleMatrix2D(n, m);
		simMatrix.assign(0.0);

		Writer fw = null;
		try {
			fw = new FileWriter(queryTmpFile);
			int position = 0;
			String sequence, identifier;
			for (Iterator<String> iterator = mapQuery.keySet().iterator(); iterator
					.hasNext();) {
				
				identifier = iterator.next();
				sequence = mapQuery.get(identifier);
				
				if (!sequence.equals("")) {
					queryID2Position.put(identifier, new Integer(position));
					position2QueryID.put(new Integer(position), identifier);
					fw.write(">" + identifier + "\n");
					fw.write(sequence + "\n");

					position++;
				}
				
			}
			fw.close();

			fw = new FileWriter(referenceTmpFile);
			position = 0;
			for (Iterator<String> iterator = mapReference.keySet().iterator(); iterator
					.hasNext();) {
					
				identifier = iterator.next();
				sequence = mapReference.get(identifier);
				
				if (!sequence.equals("")) {
					referenceID2Position.put(identifier, new Integer(position));
					position2ReferenceID.put(new Integer(position), identifier);
					fw.write(">" + identifier + "\n");
					fw.write(sequence + "\n");
					
					position++;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String blastallArgs = " -p " + mode + " -d " + referenceTmpFile
				+ " -i " + queryTmpFile + " -e " + eValueThreshold + " -m8";

		String formatdbArgs = " -i " + referenceTmpFile;
		if (mode.equalsIgnoreCase("blastp")) {
			formatdbArgs += " -p T -o T";
		} else if (mode.equalsIgnoreCase("blastn")) {
			formatdbArgs += " -p F -o T";
		} else {
			System.err.print("Unknown mode!");
			return;
		}

		try {
			Runtime rt = Runtime.getRuntime();
			Process fomatdbProcess = rt.exec("formatdb" + formatdbArgs);
//			BufferedReader resultReader2 = new BufferedReader(
//					new InputStreamReader(fomatdbProcess.getInputStream()));
			// String line2 = resultReader2.readLine();
			// while (line2 != null) {
			// System.out.println(line2);
			// line2 = resultReader2.readLine();
			// }
			// System.out.println("formatdb exit code: "
			// + fomatdbProcess.waitFor());
			fomatdbProcess.waitFor();
			fomatdbProcess.destroy();

			Process blastallProcess = rt.exec("blastall" + blastallArgs);
			// System.out.println("blastall exit code: " +
			// blastallProcess.waitFor());
			// System.out.println(blastallProcess.exitValue());

			/*
			 * Read BLAST result and build similarity matrix.
			 */
			BufferedReader resultReader = new BufferedReader(
					new InputStreamReader(blastallProcess.getInputStream()));
			String line = resultReader.readLine();
			while (line != null) {

				String[] splitLine = line.split("\t");
				String queryID = splitLine[0];
				String refID = splitLine[1];

				double eValue = Double.parseDouble(splitLine[10]);
				// double similarityScore = Double.parseDouble(splitLine[11]);

				double similarity = 0;
				if (eValue <= 1) {
					if (eValue == 0)
						similarity = maxSimValue;
					else
						similarity = -Math.log10(eValue);
				}

				int i = (referenceID2Position.get(refID)).intValue();
				int j = (queryID2Position.get(queryID)).intValue();
				double oldSimilarity = simMatrix.get(i, j);
				// System.out.println(i + "-" + j + "=" + similarity);
				// System.out.println(oldSimilarity);
				if (oldSimilarity == 0) {
					simMatrix.set(i, j, similarity);
				} else if (similarity > oldSimilarity) {
					simMatrix.set(i, j, similarity);
				}

				line = resultReader.readLine();
			}
			resultReader.close();
			blastallProcess.destroy();

			/*
			 * Delete all temporary Files.
			 */
			File currentPath = new File(".");
			File[] allFiles = currentPath.listFiles();
			for (int i = 0; i < allFiles.length; i++) {
				if (allFiles[i].getName().startsWith("tmpFile")) {
					allFiles[i].delete();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, Integer> getReferenceID2Position() {
		return referenceID2Position;
	}

	public HashMap<String, Integer> getQueryID2Position() {
		return queryID2Position;
	}

	public DoubleMatrix2D getSimMatrix() {
		return simMatrix;
	}

	public HashMap<Integer, String> getPosition2ReferenceID() {
		return position2ReferenceID;
	}

	public HashMap<Integer, String> getPosition2QueryID() {
		return position2QueryID;
	}
	
	public static boolean checkBLASTinstallation(){
		
		boolean test = false;
		Runtime rt = Runtime.getRuntime();
		try {
			Process p = rt.exec("formatdb");
			p.waitFor();
			p = rt.exec("blastall -p");
			p.waitFor();
			p.destroy();
			test=true;
		} catch (IOException e) {
			test = false;
		} catch (InterruptedException e) {
			test = false;
		}
		
		return test;
		
	}

}
