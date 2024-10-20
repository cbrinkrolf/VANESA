package io.pnResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.csv.CSVReader;
import util.TableData;

public class PNResultReader {

	public PNResultReader() {
	}

	public HashMap<String, List<Double>> readResult(File file) throws IOException {
		CSVReader reader = new CSVReader(file, ",;", true);
		TableData<String> data = reader.read();

		HashMap<String, List<Double>> result = new HashMap<String, List<Double>>();
		List<String> names = new ArrayList<>();
		// "\" added, because names / labels may contain ","
		for (String name : data.getHeaders()) {
			result.put(name.replaceAll("\"", ""), new ArrayList<Double>());
			names.add(name.replaceAll("\"", ""));
		}

		String name;
		String cell;
		for (List<String> row : data.getContent()) {
			for (int i = 0; i < row.size(); i++) {
				name = names.get(i);
				cell = row.get(i).replaceAll("\"", "");
				if (cell.isBlank()) {
					// System.out.println("is empty");
				} else {
					result.get(name).add(Double.parseDouble(cell));
				}
			}
		}
		return result;
	}
}
