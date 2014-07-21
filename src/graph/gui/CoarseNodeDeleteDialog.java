/**
 * 
 */
package graph.gui;

import graph.GraphInstance;

import java.awt.Checkbox;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * @author tloka
 * 
 */
public class CoarseNodeDeleteDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel panel;
	JOptionPane pane;
	Checkbox saveAnswer;

	GraphInstance graphInstance = new GraphInstance();
	Pathway pw = graphInstance.getPathway();
	JComboBox<String> edges = new JComboBox<String>();
	HashMap<Integer,BiologicalEdgeAbstract> edgeMap = new HashMap<Integer, BiologicalEdgeAbstract>();

	/**
	 * 
	 */
	public CoarseNodeDeleteDialog(BiologicalNodeAbstract bna) {

		//Container contentPane = getContentPane();
		MigLayout layout = new MigLayout("", "[left]");

		panel = new JPanel(layout);
		
		saveAnswer = new Checkbox("Save Answer", false);
		
		panel.add(new JLabel("Do you really want to delete the coarse node '" + 
				bna.getLabel() + "' including the entire subgraph?"));
		panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 1 ");
		panel.add(saveAnswer, "");

		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.YES_NO_OPTION);
	}


	/**
	 * @return The original, hierarchical edge to delete all sub-Edges. One single sub-Edge to be deleted.
	 * Null if nothing selected or aborted.
	 */
	public Integer[] getAnswer() {

		JDialog dialog = pane.createDialog(CoarseNodeDeleteDialog.this, "Delete a coarse node");
		//dialog.show();
		dialog.setVisible(true);
		Integer[] answer = new Integer[2];
		Integer value = (Integer) pane.getValue();
		answer[0] = value.intValue();
		if(saveAnswer.getState()){
			answer[1]=1;
		} else {
			answer[1]=0;
		}
		return answer;
	}
}
