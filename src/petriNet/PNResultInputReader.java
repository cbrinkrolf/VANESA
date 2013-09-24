package petriNet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class PNResultInputReader {

	public PNResultInputReader() {

	}

	public HashMap<String, Vector<Double>> readResult(String file)
			throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		ArrayList<String> text = new ArrayList<String>();
		String line = null;
		while ((line = in.readLine()) != null) {
			text.add(line);
		}
		in.close();

		HashMap<String, Vector<Double>> result = new HashMap<String, Vector<Double>>();
		String tmp = null;
		Iterator<String> it = text.iterator();
		String node = "";
		String[] tmpArray;
		boolean isData = false;
		Vector<Double> v = new Vector<Double>();

		while (it.hasNext()) {
			tmp = it.next();
			if (tmp.contains("DataSet: V")) {
				if (isData) {
				//	System.out.println(node);
					result.put(node, (Vector<Double>) v.clone());
					v.clear();
				}
				node = tmp.substring(9, tmp.length() - 2);
				isData = true;
			} else {
				if (isData && tmp.contains(", ")) {
					tmpArray = tmp.split(", ");
					Double d = new Double(tmpArray[1]);
					v.add(d);
				}
			}
			
		}
		result.put(node, v);
	//	System.out.println(node);
		return result;
	}
}
