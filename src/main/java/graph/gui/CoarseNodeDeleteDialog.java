/**
 * 
 */
package graph.gui;

import java.awt.Checkbox;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

/**
 * @author tloka
 * 
 */
public class CoarseNodeDeleteDialog {

	private JPanel panel;
	private JOptionPane pane;
	private Checkbox saveAnswer;

	public CoarseNodeDeleteDialog(BiologicalNodeAbstract bna) {

		// Container contentPane = getContentPane();
		MigLayout layout = new MigLayout("", "[left]");

		panel = new JPanel(layout);

		saveAnswer = new Checkbox("Save Answer", false);

		panel.add(new JLabel("Do you really want to delete the coarse node '" + bna.getLabel()
				+ "' including the entire subgraph?"));
		panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 1 ");
		panel.add(saveAnswer, "");

		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION);
	}

	/**
	 * @return The original, hierarchical edge to delete all sub-Edges. One single
	 *         sub-Edge to be deleted. Null if nothing selected or aborted.
	 */
	public Integer[] getAnswer() {

		JDialog dialog = pane.createDialog(MainWindow.getInstance().getFrame(), "Delete a coarse node");
		// dialog.show();
		dialog.setVisible(true);
		Integer[] answer = new Integer[2];
		Integer value = (Integer) pane.getValue();
		answer[0] = value.intValue();
		if (saveAnswer.getState()) {
			answer[1] = 1;
		} else {
			answer[1] = 0;
		}
		return answer;
	}
}
