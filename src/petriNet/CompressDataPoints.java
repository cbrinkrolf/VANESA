package petriNet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class CompressDataPoints {

	private static ArrayList<Double> x;
	private static ArrayList<Double> y;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			readData(new File("PNResult.csv"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		compress(x, y);
	}

	private static void compress(ArrayList<Double> x, ArrayList<Double> y) {
		System.out.println(x.size());
		System.out.println(y.size());

		double tmp;
		double tmp1;
		for (int i = 2; i < x.size(); i++) {
			tmp = (y.get(i - 1) - y.get(i - 2)) / (x.get(i - 1) - x.get(i - 2));
			tmp1 = (y.get(i) - y.get(i - 1)) / (x.get(i) - x.get(i - 1));
			// System.out.println(i);
			//System.out.println("tmp: " + tmp);
			//System.out.println("tmp1: " + tmp1);
			System.out.println(Math.abs(tmp1-tmp));
			System.out.println("y: "+ y.get(i));
			if (Math.abs(tmp1-tmp) == 0 || Math.abs(tmp1-tmp) <= Math.max(y.get(i), y.get(i-2))*0.05) {
				x.remove(i - 1);
				y.remove(i - 1);
				i--;
			}
		}
		System.out.println(x.size());
		System.out.println(y.size());
	}

	private static void readData(File file) throws Exception {
		x = new ArrayList<Double>();
		y = new ArrayList<Double>();
		BufferedReader in = new BufferedReader(new FileReader(file));
		ArrayList<String> text = new ArrayList<String>();
		HashMap<String, Integer> columnName = new HashMap<String, Integer>();
		String line = null;
		while ((line = in.readLine()) != null) {
			text.add(line);
		}
		in.close();
		String head = text.get(0);

		// "\" added, because names / labels may contain ","
		String[] headNames = head.split(";");

		int lines = text.size();
		int cols = headNames.length;
		String name;
		for (int i = 0; i < cols; i++) {
			name = headNames[i];
			columnName.put(name, i);
		}
		// System.out.println("cols:");
		// System.out.println(columnName);

		String[][] content = new String[lines][cols];

		for (int i = 0; i < lines; i++) {
			content[i] = text.get(i).split("[;]");
		}

		HashMap<String, Vector<Double>> result = new HashMap<String, Vector<Double>>();
		// System.out.println(lines);
		Vector<Double> v = new Vector<Double>();
		String k = "";
		for (int i = 1; i < lines; i++) {
			//System.out.println(content[i][0] + " " + content[i][1]);
			x.add(Double.parseDouble(content[i][0]));
			y.add(Double.parseDouble(content[i][1]));
			// System.out.println(k+" nicht enthalten");
		}
		// System.out.println(result);
	}

}
