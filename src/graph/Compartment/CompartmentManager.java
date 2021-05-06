package graph.Compartment;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public class CompartmentManager {

	Set<Compartment> compartments = new HashSet<>();
	Map<BiologicalNodeAbstract, Compartment> bnaToCompartment = new HashMap<>();
	Map<Compartment, HashSet<BiologicalNodeAbstract>> compartmentToBNA = new HashMap<Compartment, HashSet<BiologicalNodeAbstract>>();

	public CompartmentManager() {

	}
	
	public Compartment getCompartment(String name){
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
		compartments.remove(c);
		for (BiologicalNodeAbstract bna : bnaToCompartment.keySet()) {
			if (bnaToCompartment.get(bna).equals(c)) {
				bnaToCompartment.remove(bna);
			}
		}
		compartmentToBNA.remove(c);
	}

	public List<Compartment> getAllCompartmentsAlphabetically() {
		Map<String, Compartment> map = new HashMap<>();
		List<String> names = new ArrayList<>();
		for(Compartment c : compartments){
			map.put(c.getName(), c);
			names.add(c.getName());
		}
		
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		
		List<Compartment> list = new ArrayList<>();
		for(String name : names){
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
	
	public String getCompartment(BiologicalNodeAbstract bna){
		if(bnaToCompartment.containsKey(bna)){
			return bnaToCompartment.get(bna).getName();
		}else{
			return "";
		}
	}

	public Set<BiologicalNodeAbstract> getAllBNA(Compartment c) {
		return compartmentToBNA.get(c);
	}

	public void addDefaultCompartments() {
		this.add(new Compartment("Cytoplasma", new Color(0, 255, 0, 50)));
		this.add(new Compartment("Nucleus", new Color(00, 0, 255, 50)));
		this.add(new Compartment("Membrane", new Color(255, 128, 0, 50)));
		this.add(new Compartment("InsideTheCell", new Color(127, 0, 255, 50)));

	}
}
