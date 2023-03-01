package gui;

import java.awt.Component;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ToolTipListCellRenderer extends DefaultListCellRenderer {
    private final Map<String, String> map;

    public ToolTipListCellRenderer(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String toolTip = map.get(value.toString());
        if (toolTip != null) {
            setToolTipText(toolTip);
        }
        return this;
    }
}