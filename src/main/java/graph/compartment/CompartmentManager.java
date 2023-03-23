package graph.compartment;

import biologicalObjects.nodes.BiologicalNodeAbstract;

import java.awt.*;
import java.util.List;
import java.util.*;

public class CompartmentManager {
	private final Set<Compartment> compartments = new HashSet<>();
	private final Map<BiologicalNodeAbstract, Compartment> bnaToCompartment = new HashMap<>();
	private final Map<Compartment, HashSet<BiologicalNodeAbstract>> compartmentToBNA = new HashMap<>();
	private boolean drawCompartments = false;

	public Compartment getCompartment(String name) {
		for (Compartment comp : compartments) {
			if (comp.getName().equals(name.trim()))
				return comp;
		}
		return null;
	}

	public void add(Compartment c) {
		for (Compartment comp : compartments) {
			if (comp.getName().equals(c.getName()))
				return;
		}
		compartments.add(c);
		compartmentToBNA.put(c, new HashSet<>());
	}

	public void remove(Compartment c) {
		// avoids concurrent modification exception
		compartments.remove(c);
		Set<BiologicalNodeAbstract> toRemove = new HashSet<>();
		for (BiologicalNodeAbstract bna : bnaToCompartment.keySet()) {
			if (bnaToCompartment.get(bna).equals(c)) {
				toRemove.add(bna);
			}
		}
		for (BiologicalNodeAbstract bna : toRemove) {
			bnaToCompartment.remove(bna);
		}

		compartmentToBNA.remove(c);
	}

	public List<Compartment> getAllCompartmentsAlphabetically() {
		Map<String, Compartment> map = new HashMap<>();
		List<String> names = new ArrayList<>();
		for (Compartment c : compartments) {
			map.put(c.getName(), c);
			names.add(c.getName());
		}
		names.sort(String.CASE_INSENSITIVE_ORDER);
		List<Compartment> list = new ArrayList<>();
		for (String name : names) {
			list.add(map.get(name));
		}
		return list;
	}

	public void setCompartment(BiologicalNodeAbstract bna, Compartment c) {
		if (bnaToCompartment.containsKey(bna)) {
			compartmentToBNA.get(bnaToCompartment.get(bna)).remove(bna);
			bnaToCompartment.remove(bna);
		}
		if (c != null) {
			bnaToCompartment.put(bna, c);
			compartmentToBNA.get(c).add(bna);
		}
	}

	public String getCompartment(BiologicalNodeAbstract bna) {
		if (bnaToCompartment.containsKey(bna)) {
			return bnaToCompartment.get(bna).getName();
		}
		return "";
	}

	public Set<BiologicalNodeAbstract> getAllBNA(Compartment c) {
		return compartmentToBNA.get(c);
	}

	public boolean isDrawCompartments() {
		return drawCompartments;
	}

	public void setDrawCompartments(boolean drawCompartments) {
		this.drawCompartments = drawCompartments;
	}

	public void addDefaultCompartments() {
		this.add(new Compartment("Cytoplasma", new Color(0, 255, 0, 50)));
		this.add(new Compartment("Nucleus", new Color(0, 0, 255, 50)));
		this.add(new Compartment("Membrane", new Color(255, 128, 0, 50)));
		this.add(new Compartment("InsideTheCell", new Color(127, 0, 255, 50)));
	}
}
