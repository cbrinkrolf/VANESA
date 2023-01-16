package gui;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ToolTipListCellRenderer extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, String> map;

	public ToolTipListCellRenderer() {
		map = new HashMap<>();
	}

	public ToolTipListCellRenderer(Map<String, String> map) {
		this.map = map;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (map.containsKey(value)) {
			setToolTipText(map.get(value));
		}
		return this;
	}

	public void setToolTips(Map<String, String> map) {
		this.map = map;
	}
}