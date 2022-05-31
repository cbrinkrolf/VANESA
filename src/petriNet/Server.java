package petriNet;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
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
	private int port;
	private SimulationResult simResult;

	private Set<PNArc> toRefineEdges = new HashSet<>();
	private Set<PNArc> edgesFlow;
	private Set<PNArc> edgeSum;
	private Set<Transition> transitionSpeed;
	private Set<Transition> transitionFire;
	private Set<Place> placeToken;

	private long lastSyso = 0;

	// size of modelica int;
	private final int sizeOfInt;

	public Server(Pathway pw, HashMap<BiologicalEdgeAbstract, String> bea2key, String simId, int port) {

		if (SystemUtils.IS_OS_WINDOWS) {
			sizeOfInt = 8;
		} else {
			sizeOfInt = 8;
		}

		this.pw = pw;
		this.bea2key = bea2key;
		this.simId = simId;
		this.port = port;
		this.pw.setPlotColorPlacesTransitions(false);
	}

	public void start() throws IOException {

		Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				Socket client;
				DataInputStream is;
				while (running) {
					try {
						boolean boundCorrectly = false;
						while (!boundCorrectly && running) {
							try {
								serverSocket = new java.net.ServerSocket(port);
								boundCorrectly = true;
							} catch (Exception e) {
								boundCorrectly = false;
								e.printStackTrace();
								running = false;
								// port++;
							}
						}

						simResult = pw.getPetriPropertiesNet().getSimResController().get(simId);
						System.out.println(simId);
						MainWindow.getInstance().initSimResGraphs();
						while (!serverSocket.isClosed()) {
							client = waitForClient(serverSocket);
							// leseNachricht(client);

							// InputStream is = new
							// BufferedInputStream(client.getInputStream());
							is = new DataInputStream(client.getInputStream());
							readData(is);
							// System.out.println("server: " + nachricht);
							// schreibeNachricht(client, nachricht);

						}
					} catch (IOException e) {
						running = false;
						// System.err.println("Unable to process client request");
						e.printStackTrace();
					}
				}
				simResult.refineEdgeFlow(toRefineEdges);
			}
		};
		serverThread = new Thread(serverTask);
		serverThread.start();
	}

	private Socket waitForClient(ServerSocket serverSocket) throws IOException {
		System.out.println("waiting for accept ...");
		Socket socket = serverSocket.accept();
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
			this.createSets();
			System.out.println();
			System.out.println("sum: " + counter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("ende");
		byte btmp;
		ArrayList<Object> values;
		String[] sValues;

		try {
			long startDigest = System.currentTimeMillis();
			while (running) {
				/*
				 * try { Thread.sleep(1); } catch (InterruptedException e) {
				 * e.printStackTrace(); }
				 */
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

						sValues = (new String(buffer, expected, length - expected)).split("\u0000");

						for (int i = 0; i < sValues.length; i++) {
							values.add(sValues[i]);
							// System.out.print(sValues[i] + "\t");
						}
						this.setData(values);
						// System.out.println("nach dem set");
					}
					break;
				case 6:
					System.out.println("data handling: " + (System.currentTimeMillis() - startDigest) + "ms");
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
			// serverThread.stop();
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
			// serverThread.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createSets() {
		edgesFlow = new HashSet<>();
		edgeSum = new HashSet<>();
		transitionSpeed = new HashSet<>();
		transitionFire = new HashSet<>();
		placeToken = new HashSet<>();

		Iterator<BiologicalEdgeAbstract> itBea = pw.getAllEdges().iterator();
		BiologicalEdgeAbstract bea;
		PNArc e;

		while (itBea.hasNext()) {
			bea = itBea.next();
			if (bea instanceof PNArc) {
				e = (PNArc) bea;
				if (name2index.get("der(" + bea2key.get(bea) + ")") != null) {
					this.edgesFlow.add(e);
				} else {
					if (!toRefineEdges.contains(e)) {
						toRefineEdges.add(e);
					}
				}
				if (name2index.get(bea2key.get(bea)) != null) {
					this.edgeSum.add(e);
				}
			}
		}

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			if (!bna.isLogical()) {
				if (bna instanceof Place) {
					if (name2index.get("'" + bna.getName() + "'.t") != null) {
						placeToken.add((Place) bna);
					} else {
						System.out.println(bna.getName() + " does not exist. Cannot set simulation result");
					}
				} else if (bna instanceof Transition) {
					if (bna instanceof ContinuousTransition) {
						if (name2index.get("'" + bna.getName() + "'.fire") != null) {
							this.transitionFire.add((Transition) bna);
						}
						if (name2index.get("'" + bna.getName() + "'.actualSpeed") != null) {
							this.transitionSpeed.add((Transition) bna);
						}
					} else {
						if (name2index.get("'" + bna.getName() + "'.active") != null) {
							this.transitionFire.add((Transition) bna);
						}
					}
				}
			}
		}
	}

	private void setData(ArrayList<Object> values) {
		Object o;
		for (PNArc e : this.edgesFlow) {
			o = values.get(name2index.get("der(" + bea2key.get(e) + ")"));
			this.checkAndAddValue(e, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW, o);
		}

		for (PNArc e : this.edgeSum) {
			o = values.get(name2index.get(bea2key.get(e)));
			this.checkAndAddValue(e, SimulationResultController.SIM_SUM_OF_TOKEN, o);
		}

		for (Place p : this.placeToken) {
			o = values.get(name2index.get("'" + p.getName() + "'.t"));
			this.checkAndAddValue(p, SimulationResultController.SIM_TOKEN, o);
		}

		for (Transition t : this.transitionFire) {
			if (t instanceof ContinuousTransition) {
				o = values.get(name2index.get("'" + t.getName() + "'.fire"));
			} else {
				o = values.get(name2index.get("'" + t.getName() + "'.active"));
			}
			this.checkAndAddValue(t, SimulationResultController.SIM_FIRE, o);
		}

		for (Transition t : this.transitionSpeed) {
			o = values.get(name2index.get("'" + t.getName() + "'.actualSpeed"));
			this.checkAndAddValue(t, SimulationResultController.SIM_ACTUAL_FIRING_SPEED, o);
		}

		long now = System.currentTimeMillis();
		if ((now - lastSyso) > 1000) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String time = sdf.format(new Date());
			System.out.println(time + ": " + values.get(name2index.get("time")));
			lastSyso = now;
		}

		this.simResult.addTime((Double) values.get(name2index.get("time")));
		System.out.println("Time size: " + simResult.getTime().size());
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
		if (gea instanceof PNArc) {
			// System.out.println(gea.getName() + " :" + value + " org:" + o);
		}
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
			// serverThread.stop();
			// serverThread.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}