package petriNet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PNResultInputReader {

	public PNResultInputReader() {

	}

	public HashMap<String, List<Double>> readResult(File file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		ArrayList<String> text = new ArrayList<String>();
		ArrayList<String> columnName = new ArrayList<String>();
		
		String line = null;
		while ((line = in.readLine()) != null) {
			text.add(line);
		}
		in.close();

		HashMap<String, List<Double>> result = new HashMap<String, List<Double>>();

		String head = text.get(0);

		// "\" added, because names / labels may contain ","
		String[] headNames = head.split(";");
		
		int lines = text.size();
		int cols = headNames.length;
		String name;
		for (int i = 0; i < cols; i++) {
			name = headNames[i];
			result.put(name, new ArrayList<Double>());
			columnName.add(name);
		}
		// System.out.println("cols:");
		// System.out.println(columnName);

		String[][] content = new String[lines][cols];

		for (int i = 0; i < lines; i++) {
			content[i] = text.get(i).split("[,;]");
		}

		for (int i = 0; i < cols; i++) {
			name = columnName.get(i);
			//System.out.println(name);
				for (int j = 1; j < lines; j++) {
					//System.out.println(j);
					if(content[j][i].isEmpty()){
						
					}else{
					result.get(name).add(Double.parseDouble(content[j][i]));
					}
					//System.out.print(content[j][i]+" ");
				}
				//System.out.println();
				// System.out.println(columns.get(i) + " " +v.size());
			// System.out.println(k+" nicht enthalten");
		}
		// System.out.println(node);
		return result;
	}
}
