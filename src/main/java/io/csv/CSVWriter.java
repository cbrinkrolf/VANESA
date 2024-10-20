package io.csv;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import biologicalElements.Pathway;
import io.BaseWriter;
import util.TableData;

public class CSVWriter extends BaseWriter<Pathway> {
	private List<String> headers;
	private List<List<String>> content;
	private Character delimiter;
	private String lineBreak;

	public CSVWriter(File file, List<String> headers, String[][] content, Character delimiter, String lineBreak) {
		super(file);
		this.headers = headers;
		this.delimiter = delimiter;
		this.lineBreak = lineBreak;
		this.content = new ArrayList<List<String>>();
		for (int i = 0; i < content.length; i++) {
			this.content.add(Arrays.asList(content[i]));
		}
	}

	public CSVWriter(File file, TableData<String> data, Character delimiter, String lineBreak) {
		super(file);
		this.delimiter = delimiter;
		this.lineBreak = lineBreak;
		this.headers = data.getHeaders();
		this.content = data.getContent();
	}

	@Override
	protected void internalWrite(OutputStream outputStream, Pathway pw) throws Exception {
		String content = buildFileContent();
		outputStream.write(content.getBytes());
	}

	private String buildFileContent() {
		StringBuilder sb = new StringBuilder();
		if (headers != null) {
			for (int i = 0; i < headers.size() - 1; i++) {
				sb.append(headers.get(i) + delimiter);
			}
			sb.append(headers.get(headers.size() - 1) + lineBreak);
		}

		if (content != null) {
			for (List<String> row : content) {
				for (int j = 0; j < row.size() - 1; j++) {
					sb.append(row.get(j) + delimiter);
				}
				sb.append(row.get(row.size() - 1) + lineBreak);
			}
		}
		return sb.toString();
	}
}
