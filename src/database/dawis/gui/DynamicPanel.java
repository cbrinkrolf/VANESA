package database.dawis.gui;

import gui.images.ImagePath;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Olga
 * 
 */

/**
 * create a panel for the special object
 */
public class DynamicPanel{

	JPanel panel = null;
	String object = "";
	Vector <String> v = null;

	JCheckBox pathway = new JCheckBox();
	JCheckBox gene = new JCheckBox();
	JCheckBox protein = new JCheckBox();
	JCheckBox disease = new JCheckBox();
	JCheckBox go = new JCheckBox();
	JCheckBox reaction = new JCheckBox();
	JCheckBox drug = new JCheckBox();
	JCheckBox glycan = new JCheckBox();
	JCheckBox reaction_pair = new JCheckBox();
	JCheckBox compound = new JCheckBox();
	JCheckBox enzyme = new JCheckBox();
	JCheckBox[] boxes = new JCheckBox[11];

	JLabel depth1 = new JLabel("Depth 1:");
	JLabel depth2 = new JLabel("Depth 2:");
	JLabel depth3 = new JLabel("Depth 3:");
	JLabel depth4 = new JLabel("Depth 4:");

	JLabel rePa = new JLabel("Reaction Pair");
	JLabel geOn = new JLabel("Gene Ontology");
	JLabel di = new JLabel("Disease");
	JLabel pr = new JLabel("Protein");
	JLabel re = new JLabel("Reaction");
	JLabel dr = new JLabel("Drug");
	JLabel gl = new JLabel("Glycan");
	JLabel co = new JLabel("Compound");
	JLabel en = new JLabel("Enzyme");
	JLabel pa = new JLabel("Pathway Map");
	JLabel ge = new JLabel("Gene");

	Vector <String> elementsVector;

	JSpinner spinner = null;

	HashMap<String, Integer> table = new HashMap<String, Integer>();

	/**
	 * construct a dynamicPanel for special object with its spinner
	 * 
	 * @param object
	 * @param spinner
	 */
	public DynamicPanel(String object, JSpinner spinner) {

		this.object = object;
		this.spinner = spinner;

		pathway.setName("Pathway Map");
		boxes[0] = pathway;

		gene.setName("Gene");
		boxes[1] = gene;

		disease.setName("Disease");
		boxes[2] = disease;

		enzyme.setName("Enzyme");
		boxes[3] = enzyme;

		protein.setName("Protein");
		boxes[4] = protein;

		go.setName("Gene Ontology");
		boxes[5] = go;

		glycan.setName("Glycan");
		boxes[6] = glycan;

		compound.setName("Compound");
		boxes[7] = compound;

		drug.setName("Drug");
		boxes[8] = drug;

		reaction.setName("Reaction");
		boxes[9] = reaction;

		reaction_pair.setName("Reaction Pair");
		boxes[10] = reaction_pair;

	}

	/**
	 * get panel from special object
	 * 
	 * @param object
	 * @return panel
	 */
	public JPanel getPanel(String object) {
		JPanel panel = createPanelFor(object);
		return panel;
	}

	/**
	 * create panel for special object
	 * 
	 * @param object
	 * @return panel
	 */
	private JPanel createPanelFor(String object) {

		panel = new JPanel();
		panel.setLayout(new MigLayout("wrap 3"));

		Border border = BorderFactory.createEtchedBorder();
		panel.setBorder(border);
		panel.add(new JLabel("Please select additional elements"), "span");
		panel.add(new JLabel("to be shown"), "span");

		panel.add(new JSeparator(), "span 3, growx");

		if (object.equals("Pathway Map")) {

			panel.add(depth1, "span 1 7, gapright 30");

			panel.add(ge);
			panel.add(gene);
			table.put(gene.getName(), 1);

			panel.add(en);
			panel.add(enzyme);
			table.put(enzyme.getName(), 1);

			panel.add(dr);
			panel.add(drug);
			table.put(drug.getName(), 1);

			panel.add(co);
			panel.add(compound);
			table.put(compound.getName(), 1);

			panel.add(re);
			panel.add(reaction);
			table.put(reaction.getName(), 1);

			panel.add(gl);
			panel.add(glycan);
			table.put(glycan.getName(), 1);

			panel.add(pa);
			panel.add(pathway);
			table.put(pathway.getName(), 1);
			
//			panel.add(rePa);
//			panel.add(reaction_pair);
//			table.put(reaction_pair.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth2, "span 1 3, gapright 30");
			
			panel.add(di);
			panel.add(disease);
			table.put(disease.getName(), 2);

			panel.add(pr);
			panel.add(protein);
			table.put(protein.getName(), 2);

			panel.add(geOn);
			panel.add(go);
			table.put(go.getName(), 2);

		} else if (object.equals("Disease")) {

			panel.add(depth1, "span 1 3, gapright 30");

			panel.add(ge);
			panel.add(gene);
			table.put(gene.getName(), 1);

			panel.add(pr);
			panel.add(protein);
			table.put(protein.getName(), 1);
			
			panel.add(co);
			panel.add(compound);
			table.put(compound.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth2, "span 1 5, gapright 30");

			panel.add(geOn);
			panel.add(go);
			table.put(go.getName(), 2);

			panel.add(en);
			panel.add(enzyme);
			table.put(enzyme.getName(), 2);

			panel.add(pa);
			panel.add(pathway);
			table.put(pathway.getName(), 2);
			
			panel.add(re);
			panel.add(reaction);
			table.put(reaction.getName(), 2);

			panel.add(di);
			panel.add(disease);
			table.put(disease.getName(), 2);
			
//			panel.add(rePa);
//			panel.add(reaction_pair);
//			table.put(reaction_pair.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth3, "span 1 2, gapright 30");

			panel.add(dr);
			panel.add(drug);
			table.put(drug.getName(), 3);

			panel.add(gl);
			panel.add(glycan);
			table.put(glycan.getName(), 3);

		} else if (object.equals("Protein")) {

			panel.add(depth1, "span 1 3, gapright 30");

			panel.add(ge);
			panel.add(gene);
			table.put(gene.getName(), 1);

			panel.add(di);
			panel.add(disease);
			table.put(disease.getName(), 1);

			panel.add(geOn);
			panel.add(go);
			table.put(go.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth2, "span 1 4, gapright 30");

			panel.add(en);
			panel.add(enzyme);
			table.put(enzyme.getName(), 2);

			panel.add(pa);
			panel.add(pathway);
			table.put(pathway.getName(), 2);
			
			panel.add(pr);
			panel.add(protein);
			table.put(protein.getName(), 2);
			
			panel.add(co);
			panel.add(compound);
			table.put(compound.getName(), 2);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth3, "span 1 4, gapright 30");

			panel.add(dr);
			panel.add(drug);
			table.put(drug.getName(), 3);

			panel.add(re);
			panel.add(reaction);
			table.put(reaction.getName(), 3);

			panel.add(gl);
			panel.add(glycan);
			table.put(glycan.getName(), 3);
			
//			panel.add(rePa);
//			panel.add(reaction_pair);
//			table.put(reaction_pair.getName(), 1);

		} else if (object.equals("Enzyme")) {

			panel.add(depth1, "span 1 7, gapright 30");

			panel.add(ge);
			panel.add(gene);
			table.put(gene.getName(), 1);

			panel.add(co);
			panel.add(compound);
			table.put(compound.getName(), 1);
			
			panel.add(pr);
			panel.add(protein);
			table.put(protein.getName(), 1);

			panel.add(re);
			panel.add(reaction);
			table.put(reaction.getName(), 1);

			panel.add(gl);
			panel.add(glycan);
			table.put(glycan.getName(), 1);

			panel.add(pa);
			panel.add(pathway);
			table.put(pathway.getName(), 1);

			panel.add(geOn);
			panel.add(go);
			table.put(go.getName(), 1);
			
//			panel.add(rePa);
//			panel.add(reaction_pair);
//			table.put(reaction_pair.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth2, "span 1 3, gapright 30");

			panel.add(en);
			panel.add(enzyme);
			table.put(enzyme.getName(), 2);

			panel.add(dr);
			panel.add(drug);
			table.put(drug.getName(), 2);

			panel.add(di);
			panel.add(disease);
			table.put(disease.getName(), 2);

		} else if (object.equals("Gene")) {

			panel.add(depth1, "span 1 5, gapright 30");

			panel.add(di);
			panel.add(disease);
			table.put(disease.getName(), 1);

			panel.add(pr);
			panel.add(protein);
			table.put(protein.getName(), 1);

			panel.add(en);
			panel.add(enzyme);
			table.put(enzyme.getName(), 1);

			panel.add(pa);
			panel.add(pathway);
			table.put(pathway.getName(), 1);
			
			panel.add(co);
			panel.add(compound);
			table.put(compound.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth2, "span 1 5, gapright 30");

			panel.add(ge);
			panel.add(gene);
			table.put(gene.getName(), 2);

			panel.add(geOn);
			panel.add(go);
			table.put(go.getName(), 2);

			panel.add(dr);
			panel.add(drug);
			table.put(drug.getName(), 2);

			panel.add(re);
			panel.add(reaction);
			table.put(reaction.getName(), 2);

			panel.add(gl);
			panel.add(glycan);
			table.put(glycan.getName(), 2);
			
//			panel.add(rePa);
//			panel.add(reaction_pair);
//			table.put(reaction_pair.getName(), 1);

		} else if (object.equals("Gene Ontology")) {

			panel.add(depth1, "span 1 4, gapright 30");
			panel.add(depth1, "span 1 3, gapright 30");

			panel.add(pr);
			panel.add(protein);
			table.put(protein.getName(), 1);

			panel.add(en);
			panel.add(enzyme);
			table.put(enzyme.getName(), 1);
			
			panel.add(co);
			panel.add(compound);
			table.put(compound.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth2, "span 1 6, gapright 30");

			panel.add(pa);
			panel.add(pathway);
			table.put(pathway.getName(), 2);

			panel.add(di);
			panel.add(disease);
			table.put(disease.getName(), 2);

			panel.add(ge);
			panel.add(gene);
			table.put(gene.getName(), 2);

			panel.add(geOn);
			panel.add(go);
			table.put(go.getName(), 2);

			panel.add(re);
			panel.add(reaction);
			table.put(reaction.getName(), 2);

			panel.add(gl);
			panel.add(glycan);
			table.put(glycan.getName(), 2);
			
//			panel.add(rePa);
//			panel.add(reaction_pair);
//			table.put(reaction_pair.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth3, "span 1 1, gapright 30");

			panel.add(dr);
			panel.add(drug);
			table.put(drug.getName(), 3);

		} 
	else if (object.equals("Compound")) {

			panel.add(depth1, "span 1 7, gapright 30");

			panel.add(en);
			panel.add(enzyme);
			table.put(enzyme.getName(), 1);

			panel.add(pa);
			panel.add(pathway);
			table.put(pathway.getName(), 1);

			panel.add(re);
			panel.add(reaction);
			table.put(reaction.getName(), 1);
			
			panel.add(geOn);
			panel.add(go);
			table.put(go.getName(), 1);
			
			panel.add(ge);
			panel.add(gene);
			table.put(gene.getName(), 1);
			
			panel.add(pr);
			panel.add(protein);
			table.put(protein.getName(), 1);

			panel.add(di);
			panel.add(disease);
			table.put(disease.getName(), 1);
			
//			panel.add(rePa);
//			panel.add(reaction_pair);
//			table.put(reaction_pair.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth2, "span 1 3, gapright 30");

			panel.add(dr);
			panel.add(drug);
			table.put(drug.getName(), 2);

			panel.add(co);
			panel.add(compound);
			table.put(compound.getName(), 2);

			panel.add(gl);
			panel.add(glycan);
			table.put(glycan.getName(), 2);

		} else if (object.equals("Reaction")) {

			panel.add(depth1, "span 1 5, gapright 30");

			panel.add(en);
			panel.add(enzyme);
			table.put(enzyme.getName(), 1);

			panel.add(pa);
			panel.add(pathway);
			table.put(pathway.getName(), 1);

			panel.add(rePa);
			panel.add(reaction_pair);
			table.put(reaction_pair.getName(), 1);

			panel.add(co);
			panel.add(compound);
			table.put(compound.getName(), 1);

			panel.add(gl);
			panel.add(glycan);
			table.put(glycan.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth2, "span 1 6, gapright 30");

			panel.add(re);
			panel.add(reaction);
			table.put(reaction.getName(), 2);

			panel.add(geOn);
			panel.add(go);
			table.put(go.getName(), 2);

			panel.add(dr);
			panel.add(drug);
			table.put(drug.getName(), 2);

			panel.add(ge);
			panel.add(gene);
			table.put(gene.getName(), 2);
			
			panel.add(pr);
			panel.add(protein);
			table.put(protein.getName(), 2);

			panel.add(di);
			panel.add(disease);
			table.put(disease.getName(), 2);
			
//			panel.add(rePa);
//			panel.add(reaction_pair);
//			table.put(reaction_pair.getName(), 1);

		} else if (object.equals("Glycan")) {

			panel.add(depth1, "span 1 3, gapright 30");

			panel.add(en);
			panel.add(enzyme);
			table.put(enzyme.getName(), 1);

			panel.add(pa);
			panel.add(pathway);
			table.put(pathway.getName(), 1);

			panel.add(re);
			panel.add(reaction);
			table.put(reaction.getName(), 1);
			
//			panel.add(rePa);
//			panel.add(reaction_pair);
//			table.put(reaction_pair.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth2, "span 1 6, gapright 30");

			panel.add(co);
			panel.add(compound);
			table.put(compound.getName(), 2);

			panel.add(gl);
			panel.add(glycan);
			table.put(glycan.getName(), 2);

			panel.add(geOn);
			panel.add(go);
			table.put(go.getName(), 2);

			panel.add(dr);
			panel.add(drug);
			table.put(drug.getName(), 2);

			panel.add(ge);
			panel.add(gene);
			table.put(gene.getName(), 2);
			
			panel.add(pr);
			panel.add(protein);
			table.put(protein.getName(), 2);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth3, "span 1 1, gapright 30");

			panel.add(di);
			panel.add(disease);
			table.put(disease.getName(), 3);

		} else if (object.equals("Reaction Pair")) {

			panel.add(depth1, "span 1 7, gapright 30");

			panel.add(en);
			panel.add(enzyme);
			table.put(enzyme.getName(), 1);

			panel.add(pa);
			panel.add(pathway);
			table.put(pathway.getName(), 1);

			panel.add(re);
			panel.add(reaction);
			table.put(reaction.getName(), 1);
			
			panel.add(geOn);
			panel.add(go);
			table.put(go.getName(), 1);
			
			panel.add(ge);
			panel.add(gene);
			table.put(gene.getName(), 1);
			
			panel.add(pr);
			panel.add(protein);
			table.put(protein.getName(), 1);

			panel.add(di);
			panel.add(disease);
			table.put(disease.getName(), 1);
			
//			panel.add(rePa);
//			panel.add(reaction_pair);
//			table.put(reaction_pair.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth2, "span 1 3, gapright 30");

			panel.add(dr);
			panel.add(drug);
			table.put(drug.getName(), 2);

			panel.add(co);
			panel.add(compound);
			table.put(compound.getName(), 2);

			panel.add(gl);
			panel.add(glycan);
			table.put(glycan.getName(), 2);

		} else if (object.equals("Drug")) {

			panel.add(depth1);

			panel.add(pa);
			panel.add(pathway);
			table.put(pathway.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth2, "span 1 6, gapright 30");

			panel.add(re);
			panel.add(reaction);
			table.put(reaction.getName(), 2);

			panel.add(co);
			panel.add(compound);
			table.put(compound.getName(), 2);

			panel.add(gl);
			panel.add(glycan);
			table.put(glycan.getName(), 2);

			panel.add(en);
			panel.add(enzyme);
			table.put(enzyme.getName(), 2);

			panel.add(dr);
			panel.add(drug);
			table.put(drug.getName(), 2);

			panel.add(ge);
			panel.add(gene);
			table.put(gene.getName(), 2);
			
//			panel.add(rePa);
//			panel.add(reaction_pair);
//			table.put(reaction_pair.getName(), 1);

			panel.add(new JSeparator(), "span 3, growx");

			panel.add(depth3, "span 1 4, gapright 30");

			panel.add(rePa);
			panel.add(reaction_pair);
			table.put(reaction_pair.getName(), 3);

			panel.add(geOn);
			panel.add(go);
			table.put(go.getName(), 3);

			panel.add(pr);
			panel.add(protein);
			table.put(protein.getName(), 3);

			panel.add(di);
			panel.add(disease);
			table.put(disease.getName(), 3);

		}
		
		ImagePath imagePath = ImagePath.getInstance();
		JButton info = new JButton(new ImageIcon(imagePath.getPath("info.png")));
		panel.add(new JSeparator(), "span 3, wrap 20, growx");
		panel.add(info, "span 3, align center");
		info.setToolTipText("How to use this panel");
		info.setActionCommand("info");
		info.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("info")){
					new DynamicPanelInfoWindow();
				}
			}});

		return panel;
	}

	/**
	 * test, which elements should be chosen to show these elements
	 * 
	 * @param elementsToShow
	 */

	public Vector <String> testDependences(Vector <String> elementsToShow) {

		Vector <String> toChoose = new Vector <String> ();
		Vector <String> preVector;

		boolean ok = false;

		Iterator <String> it = elementsToShow.iterator();

		// look up for each element, whether there are chosen parents
		while (it.hasNext()) {

			// boolean, that controls whether one parent was found
			ok = false;

			// memory vector, is needed to remove the elements from
			// toChooseVector if one parent was found
			preVector = new Vector <String> ();

			String element = it.next();
		
			// mark the child with '/' for further handling
			toChoose.add("/" + element);

			// test whether the possible parents are chosen
			// test only for elements from depth > 1 needed
			if (table.get(element) != 1) {

				String[] possibleParents = findPossibleParentElements(element);

				for (int i = 0; i < possibleParents.length; i++) {

					int elementsDepth = table.get(element);
					int parentsDepth = table.get(possibleParents[i]);

					if (elementsDepth > parentsDepth) {
						if (elementsToShow.contains(possibleParents[i])) {

							// one parent is enough
							ok = ok || true;

						} else {
							// if the possible parent is not chosen and
							// if the possible parent is at the lower depth
							// add it to the vector

							if (!ok) {

								// memory vector
								preVector.add(possibleParents[i]);
								// message vector
								toChoose.add(possibleParents[i]);

							}
						}
					}

					SpinnerNumberModel smodel = (SpinnerNumberModel) spinner
							.getModel();

					if (smodel.getNumber().intValue() < elementsDepth) {
						smodel.setValue(elementsDepth);
						spinner.validate();
					}

				}
			}
			if (ok) {
				for (int i = 0; i < preVector.size(); i++) {
					String s = (String) preVector.get(i);
					toChoose.remove(s);
				}

			}
		}

		return toChoose;

	}

	/**
	 * test whether all chosen elements are reachable create a string for
	 * dialog, if additional elements needed
	 * 
	 * return string
	 */
	public String allOk() {

		v = new Vector <String> ();

		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].isSelected()) {
				v.add(boxes[i].getName());
			}
		}

		Vector <String> elementsToChoose = testDependences(v);

		Iterator <String> it = elementsToChoose.iterator();

		String queryPart = "";
		String query = "";

		boolean oneFound = false;

		while (it.hasNext()) {

			String element = (String) it.next();

			if (element.startsWith("/")) {

				if (oneFound) {
					query = query + queryPart;
					oneFound = false;
				}

				queryPart = "";
				queryPart = queryPart + "To show " + element.substring(1)
						+ " chose one of the following Elements:\n";

			} else {

				queryPart = queryPart + element + "\n";
				oneFound = true;
			}

		}
		if (oneFound) {
			query = query + queryPart;
		}

		return query;
	}

	/**
	 * get the possible parents of the element
	 * 
	 * @param childElement
	 * @return array of possible parents
	 */
	public String[] findPossibleParentElements(String childElement) {

		String possibleParents[] = null;

		if (childElement.equals("Pathway Map")) {

			possibleParents = new String[7];

			possibleParents[0] = "Gene";
			possibleParents[1] = "Enzyme";
			possibleParents[2] = "Reaction";
			possibleParents[3] = "Glycan";
			possibleParents[4] = "Compound";
			possibleParents[5] = "Drug";
			possibleParents[6] = "Pathway Map";

		} else if (childElement.equals("Disease")) {

			possibleParents = new String[3];

			possibleParents[0] = "Gene";
			possibleParents[1] = "Protein";
			possibleParents[2] = "Compound";

		} else if (childElement.equals("Enzyme")) {

			possibleParents = new String[7];

			possibleParents[0] = "Gene";
			possibleParents[1] = "Gene Ontology";
			possibleParents[2] = "Pathway Map";
			possibleParents[3] = "Glycan";
			possibleParents[4] = "Compound";
			possibleParents[5] = "Protein";
			possibleParents[6] = "Reaction";

		} else if (childElement.equals("Protein")) {

			possibleParents = new String[5];

			possibleParents[0] = "Gene";
			possibleParents[1] = "Gene Ontology";
			possibleParents[2] = "Compound";
			possibleParents[3] = "Disease";
			possibleParents[4] = "Enzyme";

		} else if (childElement.equals("Gene")) {

			possibleParents = new String[5];

			possibleParents[0] = "Enzyme";
			possibleParents[1] = "Disease";
			possibleParents[2] = "Protein";
			possibleParents[3] = "Pathway Map";
			possibleParents[4] = "Gene";

		} else if (childElement.equals("Compound")) {

			possibleParents = new String[7];

			possibleParents[0] = "Enzyme";
			possibleParents[1] = "Reaction";
			possibleParents[2] = "Pathway Map";
			possibleParents[3] = "Protein";
			possibleParents[4] = "Gene Ontology";
			possibleParents[5] = "Disease";
			possibleParents[6] = "Gene";

		} else if (childElement.equals("Drug")) {

			possibleParents = new String[1];

			possibleParents[0] = "Pathway Map";

		} else if (childElement.equals("Glycan")) {

			possibleParents = new String[3];

			possibleParents[0] = "Enzyme";
			possibleParents[1] = "Reaction";
			possibleParents[2] = "Pathway Map";

		} else if (childElement.equals("Reaction")) {

			possibleParents = new String[4];

			possibleParents[0] = "Enzyme";
			possibleParents[1] = "Glycan";
			possibleParents[2] = "Compound";
			possibleParents[3] = "Pathway Map";

		} else if (childElement.equals("Reaction Pair")) {

			possibleParents = new String[2];

			possibleParents[0] = "Compound";
			possibleParents[1] = "Reaction";

		} else if (childElement.equals("Gene Ontology")) {

			possibleParents = new String[3];

			possibleParents[0] = "Protein";
			possibleParents[1] = "Enzyme";
			possibleParents[2] = "Compound";

		} 
		return possibleParents;
	}

	/**
	 * pack boolean variables of all elements in an array
	 * 
	 * @return array of element settings
	 */
	public boolean[] getSettings() {

		boolean[] settings = { pathway.isSelected(), disease.isSelected(),
				go.isSelected(), gene.isSelected(), protein.isSelected(),
				enzyme.isSelected(), compound.isSelected(), 
				glycan.isSelected(), drug.isSelected(), reaction.isSelected(),
				reaction_pair.isSelected()};

		return settings;
	}
	
}
