package petriNet;

// import java.net.ServerSocket;
// import java.net.Socket;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.awt.Color;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Server {

	private Thread serverThread;
	private java.net.ServerSocket serverSocket;
	private HashMap<BiologicalEdgeAbstract, String> bea2key;
	private ArrayList<String> names;
	private boolean running = true;

	public Server(HashMap<BiologicalEdgeAbstract, String> bea2key) {
		this.bea2key = bea2key;
		this.init();
	}

	void test() throws IOException {

		Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				try {
					int port = 11111;
					serverSocket = new java.net.ServerSocket(port);

					while (true) {
						java.net.Socket client = warteAufAnmeldung(serverSocket);
						// leseNachricht(client);

						// InputStream is = new
						// BufferedInputStream(client.getInputStream());
						DataInputStream is = new DataInputStream(
								client.getInputStream());
						leseNachricht(is);
						// System.out.println("server: " + nachricht);
						// schreibeNachricht(client, nachricht);

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

	private void leseNachricht(DataInputStream socket) throws IOException {

		int lengthMax = 2048;
		// char[] buffer = new char[200];
		byte[] buffer = new byte[lengthMax];
		socket.readFully(buffer, 0, 1); // blockiert
		// bis
		// Nachricht
		// empfangen
		int id = (int) buffer[0];
		System.out.println("Server: id: " + id);
		socket.readFully(buffer, 0, 4);
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
		System.out.println("av: " + socket.available());

		socket.readFully(buffer, 0, length);
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

		System.out.println("bufferlenght: " + buffer.length);
		String n = new String(buffer, 16, buffer.length - 17);
		System.out.println("stringsize: " + n.length());
		// System.out.println(n);
		System.out.println("av: " + socket.available());

		String[] test = n.split("\u0000");
		System.out.println("testsize: " + test.length);
		// System.out.println("av: "+socket.available());
		// System.out.println("av: "+socket.available());
		int counter = 0;
		for (int i = 22300; i < 23500; i++) {
			// System.out.println(i+": "+n.charAt(i));
		}
		System.out.println("testcounter: " + counter);
		// System.out.println("av: "+socket.available());
		names = new ArrayList<String>(Arrays.asList(n.split("\u0000")));

		// running = true;
		int j = 0;
		int expected = 8 * reals + 4 * ints + bools;

		System.out.println("Headers: " + names.size());
		counter = 0;
		for (int i = 0; i < names.size(); i++) {
			// System.out.print(names.get(i) + "\t");
			counter += names.get(i).length();
		}
		System.out.println("sum: " + counter);
		byte btmp;
		ArrayList<Object> values;
		GraphInstance graphInstance = new GraphInstance();
		Pathway pw = graphInstance.getPathway();

		while (running) {
			try {
				if (pw.getPetriNet().getTime().size() > 0
						&& pw.getPetriNet().getTime()
								.get(pw.getPetriNet().getTime().size() - 1) > 0.01) {
					Thread.sleep(1);
				} else {
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			values = new ArrayList<Object>();

			socket.readFully(buffer, 0, 5); // blockiert
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
					socket.readFully(buffer, 0, length);

					for (int r = 0; r < reals; r++) {
						bb = ByteBuffer.wrap(buffer, r * 8, 8);
						bb.order(ByteOrder.LITTLE_ENDIAN);
						values.add(bb.getDouble());
						// System.out.print(bb.getDouble() + "\t");
					}
					for (int i = 0; i < ints; i++) {
						bb = ByteBuffer.wrap(buffer, reals * 8 + i * 4, 4);
						bb.order(ByteOrder.LITTLE_ENDIAN);
						values.add(bb.getInt());
						// System.out.print(bb.getInt() + "\t");
					}
					for (int b = 0; b < bools; b++) {
						// bb = ByteBuffer.wrap(buffer, b, 1);
						// bb.order(ByteOrder.LITTLE_ENDIAN);
						btmp = buffer[reals * 8 + ints * 4 + b];
						values.add(new Double(btmp));
						// values.add(buffer[reals * 8 + ints * 4 + b]);
						// System.out.print(buffer[reals * 8 + ints * 4 + b]
						// + "\t");
					}
					bb = ByteBuffer.wrap(buffer, expected, length - expected);
					bb.order(ByteOrder.LITTLE_ENDIAN);

					String[] sValues = (new String(buffer, expected, length
							- expected)).split("\u0000");

					for (int i = 0; i < sValues.length; i++) {
						values.add(sValues[i]);
						// System.out.print(sValues[i] + "\t");
					}
					this.setData(values);
					// System.out.println("nach dem set");

				}
				break;

			case 6:
				running = false;
				System.out.println("server shut down");
				MainWindow w = MainWindowSingelton.getInstance();
				// w.redrawGraphs();
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
		// String nachricht = new String(buffer, 0, anzahlZeichen);
		// return nachricht;
	}

	void schreibeNachricht(java.net.Socket socket, String nachricht)
			throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		printWriter.print(nachricht);
		printWriter.flush();
	}

	private void init() {

		GraphInstance graphInstance = new GraphInstance();
		Pathway pw = graphInstance.getPathway();
		Collection<BiologicalNodeAbstract> hs = pw.getAllNodes();
		Iterator<BiologicalNodeAbstract> it = pw.getAllNodes().iterator();
		BiologicalNodeAbstract bna;
		int places = 0;
		int transitions = 0;

		while (it.hasNext()) {
			bna = it.next();
			if (bna instanceof Place) {
				places++;
				// pw.getGraph().getVisualizationViewer().getPickedVertexState().pick(bna,
				// true);
			} else if (bna instanceof Transition) {
				transitions++;
			}
			bna.getPetriNetSimulationData().clear();
		}
		it = hs.iterator();
		int i = 0;
		int j = 0;
		while (it.hasNext()) {
			bna = it.next();
			if (bna instanceof Place) {
				((Place) bna).setPlotColor(Color.getHSBColor(i * 1.0f
						/ (places), 1, 1));
				i++;
			} else if (bna instanceof Transition) {
				((Transition) bna).setPlotColor(Color.getHSBColor(j * 1.0f
						/ (transitions), 1, 1));
				((Transition)bna).getSimActualSpeed().clear();
				j++;
			}
		}

		Iterator<BiologicalEdgeAbstract> it2 = pw.getAllEdges().iterator();
		BiologicalEdgeAbstract bea;
		PNEdge e;
		while (it2.hasNext()) {
			bea = it2.next();
			if (bea instanceof PNEdge) {
				e = (PNEdge) bea;
				e.getSim_tokens().clear();
				e.getSim_tokensSum().clear();
			}
		}
		pw.getPetriNet().getTime().clear();
	}

	private void setData(ArrayList<Object> values) {

		//System.out.println("set Data");
		GraphInstance graphInstance = new GraphInstance();
		Pathway pw = graphInstance.getPathway();
		Collection<BiologicalNodeAbstract> hs = pw.getAllNodes();
		// pnResult = pw.getPetriNet().getPnResult();
		// rowsSize = 0;
		// System.out.println("size: "+ pnResult.size());
		Iterator<BiologicalEdgeAbstract> itBea = pw.getAllEdges().iterator();
		BiologicalEdgeAbstract bea;
		PNEdge e;
		while (itBea.hasNext()) {
			bea = itBea.next();
			if (bea instanceof PNEdge) {
				e = (PNEdge) bea;

				// v = pnResult.get(bea2key.get(bea));
				// v2 = pnResult.get("der("+bea2key.get(bea)+")");
				// e.setSim_tokensSum(v);
				// e.setSim_tokens(v2);
				// System.out.println("index: "+names.indexOf("der("
				// + bea2key.get(bea) + ")"));
				e.getSim_tokens().add(
						(Double) values.get(names.indexOf("der("
								+ bea2key.get(bea) + ")")));
				e.getSim_tokensSum().add(
						(Double) values.get(names.indexOf(bea2key.get(bea))));
				//System.out.println(values.get(names.indexOf(bea2key.get(bea))));

			}
		}

		Iterator<BiologicalNodeAbstract> it = hs.iterator();
		BiologicalNodeAbstract bna;

		while (it.hasNext()) {
			bna = it.next();
			//System.out.println(bna.getName());
			if (!bna.hasRef()) {
				if (bna instanceof Place) {

					bna.getPetriNetSimulationData().add(
							(Double) (values.get(names.indexOf("'"
									+ bna.getName() + "'.t"))));
				} else if (bna instanceof Transition) {

					// System.out.println(bna.getName()+".fire" +
					// names.indexOf("'"
					// + bna.getName()
					// + "'.fire"));
					bna.getPetriNetSimulationData().add(
							(Double) values.get(names.indexOf("'"
									+ bna.getName() + "'.fire")));
					((Transition) bna).getSimActualSpeed().add(
							(Double) values.get(names.indexOf("'"
									+ bna.getName() + "'.actualSpeed")));
					//System.out.println(bna.getName()+" "+(Double) values.get(names.indexOf("'"
						//			+ bna.getName() + "'.actualSpeed")));
					//System.out.println("speed: "+((Transition)bna).getSimActualSpeed());
				}
			}
		}
		System.out.println(values.get(names.indexOf("time")));
		pw.getPetriNet().addTime((Double) values.get(names.indexOf("time")));
		// this.time = pnResult.get("time");
		// pw.setPetriNetSimulation(true);
	}

	public boolean isRunning() {
		return this.running;
	}

}