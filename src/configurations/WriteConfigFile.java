package configurations;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class WriteConfigFile {

	public WriteConfigFile() {

	}

	public void writeFile() {
		try {
			// Create file
			FileWriter fstream = new FileWriter("Configuration_Details.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("Hello world");
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

}
