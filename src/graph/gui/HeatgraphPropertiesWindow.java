package graph.gui;

import graph.ContainerSingelton;
import gui.HeatgraphLayer;
import gui.MainWindowSingelton;
import gui.eventhandlers.ToolBarListener;
import gui.images.ImagePath;
import heatmap.AdoptedHeatmap;
import heatmap.Gradient;
import heatmap.HeatmapPoint;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class HeatgraphPropertiesWindow implements ActionListener {

	JPanel p = new JPanel();
	
	ImageIcon[] icons;
    String[] names = {"GRADIENT_DEFAULT",
    				  "GRADIENT_BLACK_TO_WHITE",
                      "GRADIENT_BLUE_TO_RED",
                      "GRADIENT_GREEN_YELLOW_ORANGE_RED",
                      "GRADIENT_HEAT",
                      "GRADIENT_HOT",
                      "GRADIENT_MAROON_TO_GOLD",
                      "GRADIENT_RAINBOW",
                      "GRADIENT_RED_TO_GREEN",
                      "GRADIENT_ROY"};
    
    Color[][] gradients = { AdoptedHeatmap.GRADIENT_RED_BLUE_WHITE,
    						Gradient.GRADIENT_BLACK_TO_WHITE,
                            Gradient.GRADIENT_BLUE_TO_RED,
                            Gradient.GRADIENT_GREEN_YELLOW_ORANGE_RED,
                            Gradient.GRADIENT_HEAT,
                            Gradient.GRADIENT_HOT,
                            Gradient.GRADIENT_MAROON_TO_GOLD,
                            Gradient.GRADIENT_RAINBOW,
                            Gradient.GRADIENT_RED_TO_GREEN,
                            Gradient.GRADIENT_ROY};

	private JComboBox visualizationStyleComboBox;
    
    private JComboBox gradientComboBox;

	private JSlider radius;

	private JComboBox vertexFormComboBox;
	
	public HeatgraphPropertiesWindow() {
		this.p.setLayout(new GridLayout(0,1));
		
		visualizationStyleComboBox = new JComboBox();
		visualizationStyleComboBox.addItemListener(new ItemListener() {
			private String lastSelectedVisualisationStyle = AdoptedHeatmap.VISUALIZATION_HEAT;
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				//visualization style 
				if (e.getStateChange() == ItemEvent.SELECTED)
		        {
					Object item = e.getItem();
					if (!this.lastSelectedVisualisationStyle.equals(item)) {
						if (ContainerSingelton.getInstance().getPathwayNumbers() > 1) {
							this.lastSelectedVisualisationStyle = (String) item;
							HeatgraphLayer.getInstance().getHeatmapForActiveGraph().updateVisualizationStyle((String) item);
							AdoptedHeatmap.defaultVisualizationStyle = (String) item;
							HeatgraphLayer.getInstance().getHeatmapForActiveGraph().updateData();
							MainWindowSingelton.getInstance().repaint(); 
						}
						else {
							new ToolBarListener().showCreateBeforeMessage();
						}
					}
		        }
			}
			
		});
		visualizationStyleComboBox.addItem(AdoptedHeatmap.VISUALIZATION_HEAT);
		visualizationStyleComboBox.addItem(AdoptedHeatmap.VISUALIZATION_SEEDED_POINTS);
		
		vertexFormComboBox = new JComboBox();
		vertexFormComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
		        {
					Object item = e.getItem();
				//	System.out.println("change form to "+item);
					if (item.equals("normal")) {
						HeatgraphLayer.getInstance().setCanChangeVertexShape(false);
					}
					else if (item.equals("stars")) {
						HeatgraphLayer.getInstance().setCanChangeVertexShape(true);
					}
		        }
			}
			
		});
		vertexFormComboBox.addItem("normal");
		vertexFormComboBox.addItem("stars");
		
		
		radius = new JSlider();
		radius.setBorder(BorderFactory.createTitledBorder("Element Radius"));
		radius.setMajorTickSpacing(50);
		radius.setMinorTickSpacing(25);
		radius.setMinimum(10);
		radius.setMaximum(300);
		radius.setValue(HeatmapPoint.DRAWINGRADIUS);
		radius.setPaintTicks(true);
		radius.setPaintLabels(true);
		radius.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				HeatmapPoint.DRAWINGRADIUS = radius.getValue();
				HeatgraphLayer.getInstance().repaintActiveGraph();
				
			}
		});
		
		
        
        icons = new ImageIcon[names.length];
        Integer[] intArray = new Integer[names.length];
        for (int i = 0; i < names.length; i++)
        {
            intArray[i] = new Integer(i);
            icons[i] = new ImageIcon(ImagePath.getInstance().getPath(names[i] + ".gif"));
        }
        
        gradientComboBox = new JComboBox(intArray);
        ComboBoxRenderer renderer = new ComboBoxRenderer();
        gradientComboBox.setRenderer(renderer);
        gradientComboBox.addItemListener(new ItemListener() {
        	//changed the combobox selection 
        	@Override
        	public void itemStateChanged(ItemEvent e) {
        		Object item = e.getItem();
        		if (e.getStateChange() == ItemEvent.SELECTED)
                {
        				//gradient selection	
        				if (ContainerSingelton.getInstance().getPathwayNumbers() > 1) {
        					Integer ix = (Integer) item;
        			        
        		            HeatgraphLayer.getInstance().getHeatmapForActiveGraph().updateGradient(gradients[ix]);
        		            AdoptedHeatmap.defaultGradient = gradients[ix];
        		            MainWindowSingelton.getInstance().repaint();
        				}
        				else {
        					new ToolBarListener().showCreateBeforeMessage();
        				}	
                }
        	}
        });
        
        JPanel pan = new JPanel();
        pan.add(gradientComboBox);
        pan.setBorder(BorderFactory.createTitledBorder("Gradient"));
        
        JPanel pan2 = new JPanel();
        pan2.add(visualizationStyleComboBox);
        pan2.setBorder(BorderFactory.createTitledBorder("Visualization style"));
        
        JPanel pan3 = new JPanel();
        pan3.add(vertexFormComboBox);
        pan3.setBorder(BorderFactory.createTitledBorder("Vertex form"));
        
        
        this.p.add(pan2);
        this.p.add(pan);
        this.p.add(pan3);
		
		this.p.add(radius);
	}
	
	
	

	public JPanel getPanel() {
		//p.setVisible(false);
		return p;
	}

	public void actionPerformed(ActionEvent e) {
		
		String event = e.getActionCommand();
	
	}
	
	
	 class ComboBoxRenderer extends JLabel implements ListCellRenderer
	    {
		private static final long serialVersionUID = 1L;

			public ComboBoxRenderer()
	        {
	            setOpaque(true);
	            setHorizontalAlignment(LEFT);
	            setVerticalAlignment(CENTER);
	        }
	        
	        public Component getListCellRendererComponent(
	                                                JList list,
	                                                Object value,
	                                                int index,
	                                                boolean isSelected,
	                                                boolean cellHasFocus)
	        {
	            int selectedIndex = ((Integer)value).intValue();
	            if (isSelected) {
	                setBackground(list.getSelectionBackground());
	                setForeground(list.getSelectionForeground());
	            } else {
	                setBackground(list.getBackground());
	                setForeground(list.getForeground());
	            }

	            ImageIcon icon = icons[selectedIndex];
	            setIcon(icon);
	            setText(names[selectedIndex].substring(9));
	            return this;
	        }
	    }






}
