package graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import biologicalElements.ElementDeclarations;
import biologicalElements.GraphicalElementAbstract;
import biologicalElements.Pathway;
import graph.jung.classes.MyGraph;
import gui.PopUpDialog;

public class GraphContainer {
	private final HashMap<String, Pathway> map = new HashMap<>();
	private GraphicalElementAbstract selectedObject;
	private String mouseFunction = "edit";
	private boolean isPetriView = false;

	private String PetriNetEditingMode = ElementDeclarations.discretePlace;

	private static GraphContainer instance;

	public static synchronized GraphContainer getInstance() {
		if (GraphContainer.instance == null) {
			GraphContainer.instance = new GraphContainer();
		}
		return GraphContainer.instance;
	}

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

	public GraphicalElementAbstract getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(GraphicalElementAbstract selectedObject) {
		this.selectedObject = selectedObject;
	}

	public Pathway addPathway(final String name, final Pathway pathway) {
		final String realName = findUniqueName(name);
		pathway.setName(realName);
		map.put(realName, pathway);
		return pathway;
	}

	private String findUniqueName(final String name) {
		String newName = name;
		int count = 1;
		while (map.containsKey(newName)) {
			count++;
			newName = name + "(" + count + ")";
		}
		return newName;
	}

	public void renamePathway(final Pathway pw, final String newName) {
		removePathway(pw.getName());
		addPathway(newName, pw);
	}

	public boolean containsPathway() {
		return !map.isEmpty();
	}

	public int getPathwayNumbers() {
		return map.size();
	}

	public Collection<Pathway> getAllPathways() {
		return map.values();
	}

	public Pathway getPathway(String name) {
		return map.get(name);
	}

	public void removePathway(String name) {
		map.remove(name);
	}

	public Set<Entry<String, Pathway>> getContainerEntries() {
		return map.entrySet();
	}

	public void removeAllPathways() {
		map.clear();
	}

	public String getMouseFunction() {
		return mouseFunction;
	}

	public void changeMouseFunction(String function) {
		mouseFunction = function;
		for (final Pathway pathway : map.values()) {
			final MyGraph g = pathway.getGraph();
			if (function.equals("move")) {
				g.setMouseModeTransform();
			} else if (function.equals("pick")) {
				g.setMouseModePick();
			} else if (function.equals("edit")) {
				g.setMouseModeEditing();
			} else if (function.equals("add")) {
				// ignored
			} else if (function.equals("hierarchy")) {
				g.setMouseModeHierarchy();
			}
		}
	}

	public boolean ensurePathwayWithAtLeastOneElement() {
		final GraphContainer con = GraphContainer.getInstance();
		if (!con.containsPathway()) {
			PopUpDialog.getInstance().show("Error", "Please create a network first.");
			return false;
		}
		Pathway pw = GraphInstance.getPathway();
		if (pw == null || !pw.hasGotAtLeastOneElement()) {
			PopUpDialog.getInstance().show("Error", "Please create a network first.");
			return false;
		}
		return true;
	}
}
