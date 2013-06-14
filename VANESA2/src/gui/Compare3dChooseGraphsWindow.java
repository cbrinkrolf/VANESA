package gui;

import graph.algorithms.gui.CompareGraphs3D;

import java.util.ArrayList;
import javax.swing.SwingUtilities;
import biologicalElements.Pathway;

public class Compare3dChooseGraphsWindow extends ChooseGraphsWindow {
	private ProgressBar bar;
	private static final long serialVersionUID = 5807040718662024077L;
	
	public Compare3dChooseGraphsWindow() {
		super("Compare graphs in 3D");
	}
	
	@Override
	public void handleChosenGraphs(ArrayList<Pathway> pathways) {
		/*
		Das ganze etwas verzwickt wegen dem GUI-Thread:
		die Berechnungen sollten normalerweise außerhalb des GUI-Threads laufen,
		damit die Anwendung nicht blockiert. Jedoch erfordert insbesondere die MyGraph-Klasse
		dass sie im GUI-Thread ausgeführt wird. Auch die import & export Funktionen benötigen
		dies zur korrekten Ausführung. 
		
		Damit die ProgressBar jedoch trotzdem zu sehen ist, wird sie zuerst angezeigt.
		Dann wird der CompareGraphs3D einzeln in einem anderen Thread leicht verspätet gestartet
		(als runable im GUI-Thread)
		
		Wenn alles fertig ist, wird die ProgressBar wieder geschlossen.
		*/
		Runnable run = new Runnable() {
			public void run() {
					bar = new ProgressBar();
					bar.init(100, "  Compare chosen graphs for 3D view..  ", true);
					bar.setProgressBarString("please wait!");
			}
		};
		SwingUtilities.invokeLater(run);
		final ArrayList<Pathway> pws = pathways;
		Runnable run2 = new Runnable() {
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Runnable run3 = new Runnable() {
					public void run() {
						new CompareGraphs3D(pws);
						bar.closeWindow();
					}
				};
				SwingUtilities.invokeLater(run3);
			}
		};
		new Thread(run2).start();
		
	}
	
}
