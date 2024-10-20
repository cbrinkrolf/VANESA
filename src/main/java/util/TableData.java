package util;

import java.util.ArrayList;
import java.util.List;

public class TableData<T> {

	private List<T> headers;
	private List<List<T>> content;

	public TableData() {

	}

	public TableData(List<List<T>> content) {
		this.content = content;
	}

	public TableData(List<T> headers, List<List<T>> content) {
		this.headers = headers;
		this.content = content;
	}

	public List<T> getHeaders() {
		return headers;
	}

	public void setHeaders(List<T> headers) {
		this.headers = headers;
	}

	public List<List<T>> getContent() {
		return content;
	}

	public void setContent(List<List<T>> content) {
		this.content = content;
	}

	public boolean hasHeaders() {
		if (headers == null || headers.size() == 0) {
			return true;
		}
		return false;
	}

	public int getRowNumber() {
		if (content == null) {
			return 0;
		}
		return content.size();
	}

	public List<T> getRow(int rowNumber) {
		if (rowNumber < content.size()) {
			return content.get(rowNumber);
		} else {
			return null;
		}
	}

	public void addRow(List<T> row) {
		if (content == null) {
			content = new ArrayList<>();
		}
		content.add(row);
	}
}
