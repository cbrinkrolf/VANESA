package petriNet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class OMCTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String path = "C:\\OpenModelica1.9.1Nightly";
		
		JFileChooser chooser = new JFileChooser(path);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		
		if (path.charAt(path.length() - 1) != '\\')
			path += "\\";
		boolean abort = false;
		String missing = "";
		if (!new File(path + "PNlib_ver1_4.mo").exists()) {
			abort = true;
			missing += "PNlib_ver1_4.mo\n";
		}
		
		long zstVorher;
		long zstNachher;

		zstVorher = System.currentTimeMillis();

		final Process p;
		try {
			p = new ProcessBuilder(path + "bin\\omc.exe", path+"simulation.mos").start();
			OutputStream out = p.getOutputStream();
			InputStream in = p.getInputStream();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			out.write(100);
			
		Thread t = new Thread() {
			public void run() {
				long totalTime = 120000;
				try {
					for (long t = 0; t < totalTime; t += 1000) {
						sleep(1000);
					}
					p.destroy();
					//stopped = true;
				} catch (Exception e) {
				}
			}
		};
		t.start();
		p.waitFor();
		byte[] b = new byte[1000];
		System.out.println(in.read(b));
		System.out.println(new String(b));
		//out.w
		try {
			t.stop();
		} catch (Exception e) {
		}
		zstNachher = System.currentTimeMillis();
		System.out.println("Zeit benötigt: "
				+ ((zstNachher - zstVorher) / 1000) + " sec");
		System.out.println("Zeit benötigt: " + ((zstNachher - zstVorher))
				+ " millisec");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		ProcessBuilder pb = new ProcessBuilder( "cmd", "/c", "echo", "%OPENMODELICAHOME%" );
		Map<String, String> env = pb.environment();
		Iterator<String> it = env.keySet().iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
		
		//System.out.println(env.keySet().iterator());
		//env.put( "JAVATUTOR", "Christian Ullenboom" );
		Process p1;
		try {
			p1 = pb.start();
			System.out.println( new Scanner(p1.getInputStream()).nextLine() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	

}
