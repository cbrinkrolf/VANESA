package io.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import io.BaseReader;
import util.TableData;

public class CSVReader extends BaseReader<TableData<String>> {
	private String delimiter;
	private boolean containsHeaders;

	public CSVReader(File file, String delimiter, boolean containsHeaders) {
		super(file);
		this.delimiter = delimiter;
		this.containsHeaders = containsHeaders;
	}

	@Override
	protected TableData<String> internalRead(InputStream inputStream) throws IOException {

		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

		String line = null;
		int lines = 0;
		String split = "[" + delimiter + "]";
		String[] tokens;
		TableData<String> data = new TableData<>();
		while ((line = in.readLine()) != null) {
			tokens = line.split(split);

			if (lines == 0 && containsHeaders) {
				data.setHeaders(Arrays.asList(tokens));
			} else {
				data.addRow(Arrays.asList(tokens));
			}
			lines++;
		}
		in.close();
		return data;
	}
}
