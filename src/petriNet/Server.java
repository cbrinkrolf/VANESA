package petriNet;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.SystemUtils;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import gui.MainWindow;

public class Server {

	private Thread serverThread;
	private java.net.ServerSocket serverSocket;
	private HashMap<BiologicalEdgeAbstract, String> bea2key;
	private ArrayList<String> names;
	private HashMap<String, Integer> name2index;
	private boolean running = true;
	private Pathway pw;
	private String simId;
	private SimulationResult simResult;

	// size of modelica int;
	private final int sizeOfInt;

	public Server(Pathway pw, HashMap<BiologicalEdgeAbstract, String> bea2key, String simId) {

		if (SystemUtils.IS_OS_WINDOWS) {
			sizeOfInt = 8;
		} else {
			sizeOfInt = 8;
		}

		this.pw = pw;
		this.bea2key = bea2key;
		this.simId = simId;
		this.init();
	}

	public void start() throws IOException {

		Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				while (running) {
					try {
						int port = 11111;
						serverSocket = new java.net.ServerSocket(port);
						simResult = pw.getPetriPropertiesNet().getSimResController().get(simId);
						System.out.println(simId);
						MainWindow.getInstance().initSimResGraphs();
						while (true) {
							java.net.Socket client = waitForClient(serverSocket);
							// leseNachricht(client);

							// InputStream is = new
							// BufferedInputStream(client.getInputStream());
							DataInputStream is = new DataInputStream(client.getInputStream());
							readData(is);
							// System.out.println("server: " + nachricht);
							// schreibeNachricht(client, nachricht);

						}
					} catch (IOException e) {
						running = false;
						//System.err.println("Unable to process client request");
						//e.printStackTrace();
					}
				}
			}
		};
		serverThread = new Thread(serverTask);
		serverThread.start();
	}

	java.net.Socket waitForClient(java.net.ServerSocket serverSocket) throws IOException {
		java.net.Socket socket = serverSocket.accept();
		return socket;
	}

	private void readData(DataInputStream socket) throws IOException {
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

		name2index = new HashMap<String, Integer>();

		// running = true;
		int expected = reals * 8 + ints * sizeOfInt + bools;

		try {
			System.out.println("expected: " + expected);
			System.out.println("Headers: " + names.size());
			counter = 0;

			// to avoid calls of names.indexOf(identifier)
			for (int i = 0; i < names.size(); i++) {
				System.out.print(names.get(i) + "\t");
				counter += names.get(i).length();
				name2index.put(names.get(i), i);
			}
			System.out.println();
			System.out.println("sum: " + counter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("ende");
		byte btmp;
		ArrayList<Object> values;

		try {
			while (running) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				values = new ArrayList<Object>();

				// System.out.println("av: "+socket.available());
				socket.readFully(buffer, 0, 5);
				id = (int) buffer[0];
				// System.out.println("id: " + (int) buffer[0]);
				// socket.getInputStream().read(buffer, 0, 4);
				// System.out.println("length: "+buffer[0]+
				// " "+buffer[1]+" "+buffer[2]+" "+buffer[3] );

				bb = ByteBuffer.wrap(Arrays.copyOfRange(buffer, 1, buffer.length - 2));
				bb.order(ByteOrder.LITTLE_ENDIAN);
				length = bb.getInt();
				// System.out.println("length in loop: " + length);

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
							bb = ByteBuffer.wrap(buffer, reals * 8 + i * sizeOfInt, sizeOfInt);
							bb.order(ByteOrder.LITTLE_ENDIAN);
							values.add(bb.getInt());
							// System.out.print(integ + "\t");
						}
						for (int b = 0; b < bools; b++) {
							// bb = ByteBuffer.wrap(buffer, b, 1);
							// bb.order(ByteOrder.LITTLE_ENDIAN);
							btmp = buffer[reals * 8 + ints * sizeOfInt + b];
							values.add(btmp);
							// values.add(buffer[reals * 8 + ints * 4 + b]);
							// System.out.print(buffer[reals * 8 + ints * 4 + b]
							// + "\t");
						}
						// System.out.println("left: "+(length-expected));
						bb = ByteBuffer.wrap(buffer, expected, length - expected);
						bb.order(ByteOrder.LITTLE_ENDIAN);

						String[] sValues = (new String(buffer, expected, length - expected)).split("\u0000");

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
					MainWindow w = MainWindow.getInstance();
					w.redrawGraphs();
					break;
				}
			}
		} catch (SocketException e) {
			System.out.println("server destroyed");
			serverSocket.close();
			running = false;
			//serverThread.stop();
			// serverThread.destroy();
		}
		// System.out.println(n[1]);
		// System.out.println(n[2]);
		// StringTokenizer st = new StringTokenizer(names., "");
		// st.
		// System.out.println(new String(buffer, 16, buffer.length - 17));
		this.serverSocket.close();
		try {
			running = false;
			//serverThread.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {

		Collection<BiologicalNodeAbstract> hs = pw.getAllGraphNodes();
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
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
		}
		it = hs.iterator();
		int i = 0;
		int j = 0;
		while (it.hasNext()) {
			bna = it.next();
			if (bna instanceof Place) {
				((Place) bna).setPlotColor(Color.getHSBColor(i * 1.0f / (places), 1, 1));
				i++;
			} else if (bna instanceof Transition) {
				((Transition) bna).setPlotColor(Color.getHSBColor(j * 1.0f / (transitions), 1, 1));
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
			}
		}
	}

	private void setData(ArrayList<Object> values) {

		// System.out.println("set Data");
		Collection<BiologicalNodeAbstract> hs = pw.getAllGraphNodes();
		// pnResult = pw.getPetriNet().getPnResult();
		// rowsSize = 0;
		// System.out.println("size: "+ pnResult.size());
		Iterator<BiologicalEdgeAbstract> itBea = pw.getAllEdges().iterator();
		BiologicalEdgeAbstract bea;
		PNEdge e;

		// System.out.println("hashmap size: "+bea2key.size());
		// Iterator<String> it5 = bea2key.values().iterator();
		// while(it5.hasNext()){
		// System.out.print(it5.next()+"\t");
		// }
		// System.out.println();
		double value;
		Object o;
		while (itBea.hasNext()) {
			bea = itBea.next();
			if (bea instanceof PNEdge) {
				e = (PNEdge) bea;
				if (name2index.get("der(" + bea2key.get(bea) + ")") != null) {
					o = values.get(name2index.get("der(" + bea2key.get(bea) + ")"));
					this.checkAndAddValue(e, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW, o);
				}
				if (name2index.get(bea2key.get(bea)) != null) {
					o = values.get(name2index.get(bea2key.get(bea)));
					this.checkAndAddValue(e, SimulationResultController.SIM_SUM_OF_TOKEN, o);
				}
			}
		}

		Iterator<BiologicalNodeAbstract> it = hs.iterator();
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			// System.out.println(bna.getName());
			if (!bna.hasRef()) {
				if (bna instanceof Place) {
					if (name2index.get("'" + bna.getName() + "'.t") != null) {
						o = values.get(name2index.get("'" + bna.getName() + "'.t"));
						this.checkAndAddValue(bna, SimulationResultController.SIM_TOKEN, o);
					} else {
						System.out.println(bna.getName() + " does not exist. Cannot set simulation result");
					}
				} else if (bna instanceof Transition) {
					if (name2index.get("'" + bna.getName() + "'.fire") != null) {
						o = values.get(name2index.get("'" + bna.getName() + "'.fire"));

						this.checkAndAddValue(bna, SimulationResultController.SIM_FIRE, o);
					}
					if (name2index.get("'" + bna.getName() + "'.actualSpeed") != null) {
						o = values.get(name2index.get("'" + bna.getName() + "'.actualSpeed"));
						this.checkAndAddValue(bna, SimulationResultController.SIM_ACTUAL_FIRING_SPEED, o);
					}
				}
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time = sdf.format(new Date());

		System.out.println(time + ": " + values.get(name2index.get("time")));
		value = (Double) values.get(name2index.get("time"));

		this.simResult.addTime(value);
		// System.out.println("Time size: " + simResult.getTime().getSize());
		// System.out.println("old size: " + pw.getPetriNet().getTime().size());
		// this.time = pnResult.get("time");
		// pw.setPetriNetSimulation(true);
	}

	private void checkAndAddValue(GraphElementAbstract gea, int type, Object o) {
		double value;
		// System.out.println(o.getClass());
		// System.out.println(gea.getName() + " type: " + type + " object: " + o);
		if (o instanceof Integer) {
			// System.out.println("integer value");
			value = (double) ((int) o);
		} else if (o instanceof Double) {
			value = (Double) o;
		} else if (o instanceof Byte) {
			value = (Byte) o;
		} else {
			value = 0;
			System.out.println("unsupported data type!!!");
		}
		// System.out.println(gea.getName()+" :"+value + " org:"+o );
		this.simResult.addValue(gea, type, value);
	}

	public boolean isRunning() {
		return this.running;
	}

	public void stop() {
		System.out.println("server destroyed");
		try {
			running = false;
			serverSocket.close();
			//serverThread.stop();
			// serverThread.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}