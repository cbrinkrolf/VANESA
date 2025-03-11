package petriNet;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.*;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import gui.MainWindow;
import org.apache.log4j.Logger;

public class Server {
	private static final String NAME_SEPARATOR = "\u0000";

	private final Logger logger = Logger.getRootLogger();
	private Thread serverThread;
	private java.net.ServerSocket serverSocket;
	private final Map<BiologicalEdgeAbstract, String> bea2key;
	private boolean running = true;
	private boolean readyToConnect = false;
	private final Pathway pw;
	private final String simId;
	private final int port;
	private SimulationResult simResult;

	private final Set<PNArc> toRefineEdges = new HashSet<>();
	private final Set<StochasticTransition> stochasticTransitions = new HashSet<>();
	private final Set<DiscreteTransition> discreteTransitions = new HashSet<>();
	private Set<PNArc> edgesFlow;
	private Set<PNArc> edgeSum;
	private Set<Transition> transitionSpeed;
	private Set<Transition> transitionActive;
	private Set<Transition> transitionFire;
	private Set<Place> placeToken;
	private Set<Transition> transitionDelay;
	private Set<Transition> transitionPutDelay;
	private Set<Transition> transitionFireTime;

	private long lastSyso = 0;

	public Server(final Pathway pw, final Map<BiologicalEdgeAbstract, String> bea2key, final String simId,
			final int port) {
		this.pw = pw;
		this.bea2key = bea2key;
		this.simId = simId;
		this.port = port;
		this.pw.setPlotColorPlacesTransitions(false);
	}

	public void start() throws IOException {
		serverThread = new Thread(this::runLoop, "OMServerThread-" + simId);
		serverThread.start();
	}

	private void runLoop() {
		while (running) {
			try {
				boolean boundCorrectly = false;
				while (!boundCorrectly && running) {
					try {
						serverSocket = new ServerSocket(port);
						boundCorrectly = true;
					} catch (Exception e) {
						boundCorrectly = false;
						e.printStackTrace();
						running = false;
						// port++;
					}
				}
				simResult = pw.getPetriPropertiesNet().getSimResController().get(simId);
				MainWindow.getInstance().addSimulationResults();
				System.out.println("waiting to accept");
				readyToConnect = true;
				while (!serverSocket.isClosed()) {
					final Socket client = waitForClient(serverSocket);
					System.out.println("connected to server!");
					final DataInputStream is = new DataInputStream(client.getInputStream());
					readData(is);
				}
			} catch (IOException e) {
				running = false;
				e.printStackTrace();
			}
		}
		simResult.refineDiscreteTransitionIsFiring(discreteTransitions);
		simResult.refineStochasticTransitionIsFiring(stochasticTransitions);
		simResult.refineEdgeFlow(toRefineEdges);
	}

	private Socket waitForClient(final ServerSocket serverSocket) throws IOException {
		System.out.println("waiting for accept ...");
		return serverSocket.accept();
	}

	private void readData(final DataInputStream socket) throws IOException {
		final byte[] idLengthBuffer = new byte[5];
		socket.readFully(idLengthBuffer, 0, 5);
		ByteBuffer bb = ByteBuffer.wrap(idLengthBuffer, 0, 5).order(ByteOrder.LITTLE_ENDIAN);
		int id = bb.get();
		int length = bb.getInt();
		final byte[] buffer = new byte[length];
		socket.readFully(buffer, 0, length);
		bb = ByteBuffer.wrap(buffer, 0, 16).order(ByteOrder.LITTLE_ENDIAN);
		final int reals = bb.getInt();
		final int ints = bb.getInt();
		final int bools = bb.getInt();
		final int strings = bb.getInt();
		final String[] names = (new String(buffer, 16, buffer.length - 17)).split(NAME_SEPARATOR);
		final int expectedPayloadSize = reals * 8 + ints * 8 + bools;
		// to avoid calls of names.indexOf(identifier)
		final Map<String, Integer> name2index = new HashMap<>();
		for (int i = 0; i < names.length; i++) {
			name2index.put(names[i], i);
		}
		System.out.printf(
				"OM Server: id=%d, reals=%d, ints=%d, bools=%d, strings=%d, headers=%d, expected payload size=%d%n", id,
				reals, ints, bools, strings, names.length, expectedPayloadSize);
		try {
			createSets(name2index);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			long startDigest = System.currentTimeMillis();
			while (running) {
				final List<Object> values = new ArrayList<>();
				socket.readFully(buffer, 0, 5);
				bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
				id = bb.get();
				length = bb.getInt();
				switch (id) {
				case 4:
					if (length > 0) {
						socket.readFully(buffer, 0, length);
						bb = ByteBuffer.wrap(buffer, 0, expectedPayloadSize).order(ByteOrder.LITTLE_ENDIAN);
						for (int r = 0; r < reals; r++) {
							values.add(bb.getDouble());
						}
						for (int i = 0; i < ints; i++) {
							values.add(bb.getLong());
						}
						for (int b = 0; b < bools; b++) {
							values.add(bb.get());
						}
						final String[] sValues = (new String(buffer, expectedPayloadSize, length - expectedPayloadSize))
								.split(NAME_SEPARATOR);
						Collections.addAll(values, sValues);
						setData(name2index, values);
					}
					break;
				case 6:
					System.out.println("data handling: " + (System.currentTimeMillis() - startDigest) + "ms");
					running = false;
					System.out.println("server shut down");
					break;
				}
			}
		} catch (SocketException e) {
			System.out.println("server destroyed");
			serverSocket.close();
			running = false;
		}
		serverSocket.close();
		running = false;
	}

	private void createSets(final Map<String, Integer> name2index) {
		edgesFlow = new HashSet<>();
		edgeSum = new HashSet<>();
		transitionSpeed = new HashSet<>();
		transitionActive = new HashSet<>();
		transitionFire = new HashSet<>();
		placeToken = new HashSet<>();
		transitionPutDelay = new HashSet<>();
		transitionFireTime = new HashSet<>();
		transitionDelay = new HashSet<>();

		for (final BiologicalEdgeAbstract bea : pw.getAllEdges()) {
			if (bea instanceof PNArc) {
				final PNArc e = (PNArc) bea;
				if (name2index.get("der(" + bea2key.get(bea) + ")") != null) {
					edgesFlow.add(e);
				} else {
					toRefineEdges.add(e);
				}
				if (name2index.get(bea2key.get(bea)) != null) {
					edgeSum.add(e);
				}
			}
		}
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
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
							transitionFire.add((Transition) bna);
						}
						if (name2index.get("'" + bna.getName() + "'.actualSpeed") != null) {
							transitionSpeed.add((Transition) bna);
						}
					} else {
						if (name2index.get("'" + bna.getName() + "'.active") != null) {
							transitionActive.add((Transition) bna);
						}
						if (name2index.get("'" + bna.getName() + "'.fireTime") != null) {
							transitionFireTime.add((Transition) bna);
						}
					}
					if (bna instanceof DiscreteTransition) {
						discreteTransitions.add((DiscreteTransition) bna);
						if (name2index.get("'" + bna.getName() + "'.delay") != null) {
							transitionDelay.add((Transition) bna);
						}
					} else if (bna instanceof StochasticTransition) {
						stochasticTransitions.add((StochasticTransition) bna);
						if (name2index.get("'" + bna.getName() + "'.putDelay") != null) {
							transitionPutDelay.add((Transition) bna);
						}
					}
				}
			}
		}
	}

	private void setData(final Map<String, Integer> name2index, final List<Object> values) {
		for (PNArc e : edgesFlow) {
			final Object o = values.get(name2index.get("der(" + bea2key.get(e) + ")"));
			checkAndAddValue(e, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW, o);
		}
		for (final PNArc e : edgeSum) {
			final Object o = values.get(name2index.get(bea2key.get(e)));
			checkAndAddValue(e, SimulationResultController.SIM_SUM_OF_TOKEN, o);
		}
		for (final Place p : placeToken) {
			final Object o = values.get(name2index.get("'" + p.getName() + "'.t"));
			checkAndAddValue(p, SimulationResultController.SIM_TOKEN, o);
		}
		for (final Transition t : transitionActive) {
			final Object o = values.get(name2index.get("'" + t.getName() + "'.active"));
			checkAndAddValue(t, SimulationResultController.SIM_ACTIVE, o);
		}
		for (final Transition t : transitionFire) {
			final Object o = values.get(name2index.get("'" + t.getName() + "'.fire"));
			checkAndAddValue(t, SimulationResultController.SIM_FIRE, o);
		}
		for (final Transition t : transitionSpeed) {
			final Object o = values.get(name2index.get("'" + t.getName() + "'.actualSpeed"));
			checkAndAddValue(t, SimulationResultController.SIM_ACTUAL_FIRING_SPEED, o);
		}
		for (final Transition t : transitionDelay) {
			final Object o = values.get(name2index.get("'" + t.getName() + "'.delay"));
			checkAndAddValue(t, SimulationResultController.SIM_DELAY, o);
		}
		for (final Transition t : transitionPutDelay) {
			final Object o = values.get(name2index.get("'" + t.getName() + "'.putDelay"));
			checkAndAddValue(t, SimulationResultController.SIM_PUT_DELAY, o);
		}
		for (final Transition t : transitionFireTime) {
			final Object o = values.get(name2index.get("'" + t.getName() + "'.fireTime"));
			checkAndAddValue(t, SimulationResultController.SIM_FIRE_TIME, o);
		}

		long now = System.currentTimeMillis();
		if ((now - lastSyso) > 1000) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String time = sdf.format(new Date());
			System.out.println(time + ": " + values.get(name2index.get("time")));
			lastSyso = now;
		}

		simResult.addTime((Double) values.get(name2index.get("time")));
	}

	private void checkAndAddValue(final GraphElementAbstract gea, final int type, final Object o) {
		// TODO: values should be stored in their original datatype. Especially long to
		// double may cause issues for large values.
		final double value;
		if (o instanceof Integer) {
			value = (double) (int) o;
		} else if (o instanceof Long) {
			value = (double) (long) o;
		} else if (o instanceof Float) {
			value = (double) (float) o;
		} else if (o instanceof Double) {
			value = (double) o;
		} else if (o instanceof Byte) {
			value = (byte) o;
		} else {
			value = 0;
			if (o != null) {
				logger.warn("Unsupported data type '" + o.getClass() + "' to add to simulation result.");
			} else {
				logger.warn("Trying to add null value to simulation result.");
			}
		}
		simResult.addValue(gea, type, value);
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		System.out.println("server destroyed");
		try {
			running = false;
			serverSocket.close();
		} catch (IOException e) {
			logger.warn("Failed to stop server", e);
		}
	}

	public boolean isReadyToConnect() {
		return readyToConnect;
	}
}