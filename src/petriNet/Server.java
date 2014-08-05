package petriNet;

// import java.net.ServerSocket;
// import java.net.Socket;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;


public class Server {

	private Thread serverThread;
	private java.net.ServerSocket serverSocket;
	
	void test() throws IOException {

		Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				try {
					int port = 11111;
					serverSocket = new java.net.ServerSocket(
							port);

					while (true) {
						java.net.Socket client = warteAufAnmeldung(serverSocket);
						leseNachricht(client);
						//System.out.println("server: " + nachricht);
						//schreibeNachricht(client, nachricht);
						
					}
				} catch (IOException e) {
					System.err.println("Unable to process client request");
					e.printStackTrace();
				}
			}
		};
		serverThread = new Thread(serverTask);
		serverThread.start();

		
	}

	java.net.Socket warteAufAnmeldung(java.net.ServerSocket serverSocket)
			throws IOException {
		java.net.Socket socket = serverSocket.accept(); // blockiert, bis sich
														// ein Client angemeldet
														// hat
		return socket;
	}

	private void leseNachricht(java.net.Socket socket) throws IOException {

		int lengthMax = 2048;
		// char[] buffer = new char[200];
		byte[] buffer = new byte[lengthMax];
		int anzahlZeichen = socket.getInputStream().read(buffer, 0, 1); // blockiert
		// bis
		// Nachricht
		// empfangen
		int id = (int) buffer[0];
		System.out.println("Server: id: " + id);
		socket.getInputStream().read(buffer, 0, 4);
		// System.out.println("length: "+buffer[0]+
		// " "+buffer[1]+" "+buffer[2]+" "+buffer[3] );

		ByteBuffer bb = ByteBuffer.wrap(buffer);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int length = bb.getInt();
		System.out.println("length: " + length);
		if (lengthMax < length) {
			lengthMax = length;
			buffer = new byte[length];
		}
		socket.getInputStream().read(buffer, 0, length);
		bb = ByteBuffer.wrap(buffer, 0, 4);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		int reals = bb.getInt();
		System.out.println("real: " + reals);
		bb = ByteBuffer.wrap(buffer, 4, 4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int ints = bb.getInt();
		System.out.println("int: " + ints);

		bb = ByteBuffer.wrap(buffer, 8, 4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int bools = bb.getInt();
		System.out.println("bool: " + bools);

		bb = ByteBuffer.wrap(buffer, 12, 4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int strings = bb.getInt();
		System.out.println("string: " + strings);

		String n = new String(buffer, 16, buffer.length - 17);
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(n
				.split("\u0000")));

		boolean test = true;
		int j = 0;
		int expected = 8 * reals + 4 * ints + bools;

		for (int i = 0; i < names.size(); i++) {
			System.out.print(names.get(i) + "\t");

		}
		System.out.println();

		while (test) {

			socket.getInputStream().read(buffer, 0, 5); // blockiert
			// bis
			// Nachricht
			// empfangen
			id = (int) buffer[0];
			// System.out.println("id: " + (int) buffer[0]);
			// socket.getInputStream().read(buffer, 0, 4);
			// System.out.println("length: "+buffer[0]+
			// " "+buffer[1]+" "+buffer[2]+" "+buffer[3] );

			bb = ByteBuffer.wrap(Arrays.copyOfRange(buffer, 1,
					buffer.length - 2));
			bb.order(ByteOrder.LITTLE_ENDIAN);
			length = bb.getInt();
			// System.out.println("length: " + length);

			switch (id) {
			case 4:
				if (length > 0) {
					socket.getInputStream().read(buffer, 0, length);

					for (int r = 0; r < reals; r++) {
						bb = ByteBuffer.wrap(buffer, r * 8, 8);
						bb.order(ByteOrder.LITTLE_ENDIAN);
						System.out.print(bb.getDouble() + "\t");
					}
					for (int i = 0; i < ints; i++) {
						bb = ByteBuffer.wrap(buffer, reals * 8 + i * 4, 4);
						bb.order(ByteOrder.LITTLE_ENDIAN);
						System.out.print(bb.getInt() + "\t");
					}
					for (int b = 0; b < bools; b++) {
						// bb = ByteBuffer.wrap(buffer, b, 1);
						// bb.order(ByteOrder.LITTLE_ENDIAN);
						System.out.print(buffer[reals * 8 + ints * 4 + b]
								+ "\t");
					}
					bb = ByteBuffer.wrap(buffer, expected, length - expected);
					bb.order(ByteOrder.LITTLE_ENDIAN);

					String[] sValues = (new String(buffer, expected, length
							- expected)).split("\u0000");

					for (int i = 0; i < sValues.length; i++) {
						System.out.print(sValues[i] + "\t");
					}
					System.out.println();

				}
				break;

			case 6:
				test = false;
				break;
			}
			j++;
		}
		// System.out.println(n[1]);
		// System.out.println(n[2]);
		// StringTokenizer st = new StringTokenizer(names., "");
		// st.
		// System.out.println(new String(buffer, 16, buffer.length - 17));
		this.serverSocket.close();
		serverThread.stop();
		serverThread.destroy();
		//String nachricht = new String(buffer, 0, anzahlZeichen);
		//return nachricht;
	}

	void schreibeNachricht(java.net.Socket socket, String nachricht)
			throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		printWriter.print(nachricht);
		printWriter.flush();
	}
}