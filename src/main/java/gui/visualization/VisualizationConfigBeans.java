package gui.visualization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import gui.MainWindow;

public class VisualizationConfigBeans {
	private List<Bean> beansList = new ArrayList<Bean>();
	private HashMap<String, Map<String,Object>> parseInBean;
	private GraphInstance graphInstance = null;
	private Pathway pathway;
	
	
	public VisualizationConfigBeans(){
	}
	
	public List<Bean> parseAndAdjust(HashMap<String, Map<String,Object>> object, boolean doAdjust){
		parseInBean = object;
		for(String key : parseInBean.keySet()){
			Bean bean =  new Bean();
			bean.setName(key);
			bean.setShape(parseInBean.get(key).get("shape").toString());
			bean.setSizefactor(Double.parseDouble(parseInBean.get(key).get("sizefactor").toString()));
			bean.setColorRed((int)parseInBean.get(key).get("red"));
			bean.setColorGreen((int)parseInBean.get(key).get("green"));
			bean.setColorBlue((int)parseInBean.get(key).get("blue"));
			beansList.add(bean);
		}		
		
		if(doAdjust == true){
			graphInstance = new GraphInstance();
			MainWindow w = MainWindow.getInstance();
			if(w.getCurrentPathway() != null){
				pathway = graphInstance.getPathway();
				Collection<BiologicalNodeAbstract> allNodes = pathway.getAllGraphNodes();
				w.setBeansList(beansList);
				for(BiologicalNodeAbstract nodes : allNodes){
					w.nodeAttributeChanger(nodes, true);
				}
			}
		}
		return beansList;
	}
		
	
	public List<Bean> getBeansList() {
		return beansList;
	}

	public void setBeansList(List<Bean> beansList) {
		this.beansList = beansList;
	}



	public class Bean{
		private String name;
		private String shape;
		private double sizefactor;
		private int colorRed;
		private int colorGreen;
		private int colorBlue;
		
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getShape() {
			return shape;
		}
		public void setShape(String shape) {
			this.shape = shape;
		}
		public double getSizefactor() {
			return sizefactor;
		}
		public void setSizefactor(double sizefactor) {
			this.sizefactor = sizefactor;
		}
		public int getColorRed() {
			return colorRed;
		}
		public void setColorRed(int colorRed) {
			this.colorRed = colorRed;
		}
		public int getColorGreen() {
			return colorGreen;
		}
		public void setColorGreen(int colorGreen) {
			this.colorGreen = colorGreen;
		}
		public int getColorBlue() {
			return colorBlue;
		}
		public void setColorBlue(int colorBlue) {
			this.colorBlue = colorBlue;
		}	
	}
}

