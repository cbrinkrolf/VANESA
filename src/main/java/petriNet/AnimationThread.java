package petriNet;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JSlider;

public class AnimationThread extends Thread {
	// This method is called when the thread runs

	private JSlider slider = null;
	private final Vector<Integer> colors = new Vector<>();
	private int animationStartPosition = 2;
	private int animationEndPosition = 0;
	private int animationSpeed = 1;
	private boolean color = false;
	private JButton animationButton;
	private boolean runThread = true;
	private int threadStep = 0;
	private JButton stopButton;

	public AnimationThread(JSlider slider, int animationStart, int animationEnd, boolean color, int animationSpeed,
			JButton animationButton, JButton stopButton) {
		this.slider = slider;
		this.color = color;
		this.animationSpeed = animationSpeed;
		this.animationStartPosition = animationStart;
		this.animationEndPosition = animationEnd;
		this.animationButton = animationButton;
		this.threadStep = animationStart;
		this.stopButton = stopButton;

		colors.add(0, 0x0000ff);
		colors.add(1, 0xff0000);
		colors.add(2, 0xdedede);
	}

	public boolean stopThread() {
		runThread = false;
		return true;
	}

	public int getThreadStep() {
		return threadStep;
	}

	public void run() {
		
		//CHRIS reimplement if necessary

		/*GraphInstance graphInstance = new GraphInstance();
		MainWindow w = MainWindowSingelton.getInstance();

		// initialize loop variables
		Double now;
		Double past;
		Double temp;
		Double tempStep;

		Double ref;
		int take = 0;
		//Vertex v;

		Pathway pw = graphInstance.getPathway();
		Collection<BiologicalNodeAbstract> ns = pw.getGraph().getAllVertices();

		if (animationStartPosition == 1) {
			animationStartPosition = 2;
		}

		if (animationStartPosition < animationEndPosition) {

			animationButton.setEnabled(false);
			slider.setEnabled(false);
			for (int i = animationStartPosition; i <= animationEndPosition; i++) {
				if (!runThread)
					break;
				slider.setValue(i);
				threadStep = i;
				
				for (int j = 1; j <= 50; j++) {
					if (!runThread)
						break;
					if (ns != null) {
						Iterator<Vertex> it = ns.iterator();
						while (it.hasNext()) {
							v = it.next();

							BiologicalNodeAbstract bna = (BiologicalNodeAbstract) pw
									.getElement(v);
							if (bna.getPetriNetSimulationData().size() > 0
									&& i >= 2) {
								temp = bna.getMicroArrayValue(i - 2);
								if (i != 2) {
									
									now = bna.getMicroArrayValue(i - 2);
									past = bna.getMicroArrayValue(i - 3);

									if (now - past > 0) {

										tempStep = j * ((now - past) / 50);
										temp = past + tempStep;
										take = 1;

									} else if (now - past < 0) {

										tempStep = j * ((past - now) / 50);
										temp = past - tempStep;
										take = 0;

									} else {

										temp = bna.getMicroArrayValue(i - 2);
										take = 2;

									}
								}
								if (bna instanceof Place) ((Place)bna).setToken(temp);
								v.setUserDatum("madata", 0.4, UserData.SHARED);
								
								Double temp2=Math.sqrt(Math.sqrt(temp));
								
								if (temp2>3.0) temp2 = 3.0;
							
								
								NodeRankingVertexSizeFunction sf = new NodeRankingVertexSizeFunction(
										"madata", (temp2));
								VertexShapes vs = new VertexShapes(sf,
										new ConstantVertexAspectRatioFunction(
												1.0f));
								bna.rebuildShape(vs);
								if (color) {
									bna.setColor(new Color(colors.get(take)));
								}

							}
						}
					}
					try {
						Thread.sleep(40 / animationSpeed);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					w.repaint();
				}
			}
		}
		animationButton.setEnabled(true);
		slider.setEnabled(true);
		threadStep = -1;*/
	}
}