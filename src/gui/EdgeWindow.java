package gui;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.utils.Pair;
import graph.GraphInstance;
import gui.eventhandlers.PropertyWindowListener;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class EdgeWindow {

	JPanel p;
	BiologicalEdgeAbstract ab;
	GraphInstance graphInstance = new GraphInstance();
	TitledTab tab;
	
	public EdgeWindow(Object element){
		
		this.ab = (BiologicalEdgeAbstract)graphInstance.getPathwayElement(element);
		MigLayout layout = new MigLayout("", "[left]");
		p = new JPanel(layout);
		
		Edge myEdge = ab.getEdge();
		Pair points = myEdge.getEndpoints();
		
		PropertyWindowListener pwl = new PropertyWindowListener(element);
					
		JTextField label = new JTextField(20);
		JTextField name = new JTextField(20);
		JTextArea comment = new JTextArea(20,5);
		
		label.setText(ab.getLabel());
		name.setText(ab.getName());
		comment.setText(ab.getComments());
		
		label.setName("label");
		name.setName("name");
		comment.setName("comment");
		
		label.addFocusListener(pwl);
		name.addFocusListener(pwl);
		comment.addFocusListener(pwl);
		
		p.add(new JLabel(ab.getBiologicalElement()),"span 4");
		p.add(new JSeparator(),       "span, growx, wrap 15, gaptop 10, gap 5");
		
		p.add(new JLabel("Label"), "span 2, gap 5 ");
	    p.add(label,"span,wrap,growx ,gap 10");
		p.add(new JLabel("Name"),"span 2, gap 5 ");
		p.add(name,"span, wrap 15, growx, gap 10");
		
		if (ab.isDirected()){
			p.add(new JLabel("From : "),"span 4,gap 5");
			p.add(new JLabel(((BiologicalNodeAbstract)graphInstance.getPathwayElement(points.getFirst())).getName()),"span,wrap,growx ,gap 10");
			p.add(new JLabel("To : "),"span 4,gap 5");
			p.add(new JLabel(((BiologicalNodeAbstract)graphInstance.getPathwayElement(points.getSecond())).getName()),"span,wrap,growx ,gap 10");		
		}else{
			p.add(new JLabel("Endpoint : "),"span 4,gap 5");
			p.add(new JLabel(((BiologicalNodeAbstract)graphInstance.getPathwayElement(points.getFirst())).getName()),"span,wrap,growx ,gap 10");
			p.add(new JLabel("Endpoint : "),"span 4,gap 5");
			p.add(new JLabel(((BiologicalNodeAbstract)graphInstance.getPathwayElement(points.getSecond())).getName()),"span,wrap,growx ,gap 10");		
		}
		
		p.add(new JLabel("Comment"),"span 4, gaptop 5");
		p.add(new JSeparator(),       "span, growx, wrap 15, gaptop 10, gap 5");
		p.add(comment,"span,wrap,growx ,gap 10");
		
		tab = new TitledTab("User Properties", null, p, null);
		tab.getProperties().setHighlightedRaised(2);
		tab.getProperties().getHighlightedProperties().getComponentProperties().setBackgroundColor(Color.WHITE);
		tab.getProperties().getNormalProperties().getComponentProperties().setBackgroundColor(Color.LIGHT_GRAY);
	}
	
	public TitledTab getTab(){
		return tab;
	}
}
