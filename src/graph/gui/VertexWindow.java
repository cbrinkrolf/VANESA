/*package graph.gui;

import graph.GraphInstance;
import gui.eventhandlers.PropertyWindowListener;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.BiologicalNodeAbstract;

//ElementProperties
public class VertexWindow implements ItemListener{

	JPanel p;
	BiologicalNodeAbstract ab;
	GraphInstance graphInstance = new GraphInstance();
	TitledTab tab;
	
	public VertexWindow(Object element){
					
		this.ab = (BiologicalNodeAbstract)graphInstance.getPathwayElement(element);
		
		PropertyWindowListener pwl = new PropertyWindowListener(element);
		
		JButton colorButton = new JButton("Colour");
		colorButton.setBackground(ab.getColor());
		colorButton.setToolTipText("Colour");
		colorButton.setActionCommand("colour");
		//colorButton.addActionListener(new ColourButtonListener(this));
		
		JTextField label = new JTextField(20);
		JTextField name = new JTextField(20);
		JTextArea comment = new JTextArea(20,5);
		JComboBox compartment = new JComboBox();
		addCompartmentItems(compartment);
		AutoCompleteDecorator.decorate(compartment);
		//compartment.setMaximumSize(new Dimension(250,300));
		compartment.setSelectedItem(ab.getCompartment());
		compartment.addItemListener(this);
		
		label.setText(ab.getLabel());
		name.setText(ab.getName());
		comment.setText(ab.getComments());
		
		label.setName("label");
		name.setName("name");
		comment.setName("comment");
		
		label.addFocusListener(pwl);
		name.addFocusListener(pwl);
		comment.addFocusListener(pwl);
	
		MigLayout headerlayout = new MigLayout("fillx", "[right]rel[grow,fill]", "");
		JPanel headerPanel = new JPanel(headerlayout);
		headerPanel.setBackground(new Color(192,215,227)); 
		headerPanel.add(new JLabel(ab.getBiologicalElement()),   "");
		headerPanel.add(new JSeparator(),          "gap 10");
		
		MigLayout layout = new MigLayout("fillx", "[grow,fill]", "[]10[]5[]5[]10[]");
		p = new JPanel(layout);
		p.add(headerPanel,"wrap, span");
		p.add(new JLabel("Label 5555"), "gap 5 ");
	    p.add(label,"wrap,span 3");
		p.add(new JLabel("Name"),"gap 5 ");
		p.add(name,"wrap ,span 3");
		p.add(new JLabel("Compartment"),"gap 5 ");
		p.add(compartment,"wrap ,span 3");
		
		JPanel seperatorPanel = new JPanel(headerlayout);
		seperatorPanel.add(new JLabel("Comment"),"");
		seperatorPanel.add(new JSeparator(),          "gap 10");		
		p.add(seperatorPanel,"wrap, span");
		
		p.add(comment,"span,wrap,growx ,gap 10");
		p.add(new JLabel("Settings"),"span 4");
		p.add(new JSeparator(),       "span, growx, wrap 15, gaptop 10, gap 5");
		p.add(new JLabel("Change Colour"), "span 2, gap 5 ");
	    p.add(colorButton,"span,wrap,growx ,gap 10");
	    
	}
	
	private void addCompartmentItems(JComboBox compartment){
		
		List compartmentList = new Elementdeclerations().getAllCompartmentDeclaration();
		Iterator it = compartmentList.iterator();
		
		while(it.hasNext()){
			String element = it.next().toString();
			compartment.addItem(element);
		}	
	}
	
	
	
	public Color getElementColor(){
		return ab.getColor();
	}
	
	public void setElementColor(Color color){
		ab.setColor(color);
	}

	public JPanel getPanel(){
		return p;
	}

	
	public void itemStateChanged(ItemEvent event) {
		
		int state = event.getStateChange();
		String item = (String)event.getItem();
		ab.setCompartment(item);
		
	}
}*/
