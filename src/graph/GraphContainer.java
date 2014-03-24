package graph;

import graph.jung.classes.MyGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;


public class GraphContainer {
	
	
	private HashMap<String, Pathway> map = new HashMap<String, Pathway>();
	private GraphElementAbstract selectedObject;
	private String mouseFunction = "edit";
	private boolean isPetriView = false; 
	
	private String PetriNetEditingMode = "discretePlace";
	
	public String getPetriNetEditingMode() {
		return PetriNetEditingMode;
	}

	public void setPetriNetEditingMode(String petriNetEditingMode) {
		PetriNetEditingMode = petriNetEditingMode;
	}

	public boolean isPetriView() {
		return isPetriView;
	}

	public void setPetriView(boolean isPetriView) {
		this.isPetriView = isPetriView;
	}
	
	public GraphContainer(){	
	
	}
	
	public GraphElementAbstract getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(GraphElementAbstract selectedObject) {
		this.selectedObject = selectedObject;
	}
	
	public String addPathway(String name,Pathway pathway){
		
		String realName= checkNameDuplicates(name,1);
		pathway.setName(realName);
		map.put(realName, pathway);
		
		return realName;
	}
	
	public String checkNameDuplicates(String name, int i){
		
		int count = i;
		String newName = name;
		
		if (i>1) newName = newName+"("+(count)+")";
		
		if(map.containsKey(newName)){
			count++;
		  newName= checkNameDuplicates(name,count);
		}
		return newName;
	}
	
	public String renamePathway(Pathway pw, String newName){
		
		removePathway(pw.getName());
		return addPathway(newName,pw);
	}
	
	public boolean containsPathway(){
		if (map.isEmpty()){
			return false;
		}else{
			return true;
		}
	}
	
	public int getPathwayNumbers(){
		return map.size();
	}
	
	public Collection<Pathway> getAllPathways(){
		return map.values();
	}
	
	public Pathway getPathway(String name){
		return (Pathway)map.get(name);
	}
	
	public void removePathway(String name){
		map.remove(name);
	}
	
	public Set<Entry<String, Pathway>> getContainerEntries(){
		return map.entrySet();
	}
	
	public void removeAllPathways(){
		map.clear();
	}
	
	public String getMouseFunction(){
		return mouseFunction;
	}
	
	public void changeMouseFunction(String function){
		
		mouseFunction = function;
		Iterator<Map.Entry<String, Pathway>> it = map.entrySet().iterator();
		Map.Entry<String, Pathway> entry;
		while(it.hasNext()){

			entry = it.next();
			Pathway p = (Pathway)entry.getValue();
			MyGraph g = p.getGraph();
			
			if (function.equals("move")){
				g.setMouseModeTransform();
			}else if (function.equals("pick")){
				g.setMouseModePick();
			}else if (function.equals("edit")){
				g.setMouseModeEditing();
			}else if (function.equals("add")){

			}
		}
	}
}