package gui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

public class ToolBarPanel extends JPanel implements ComponentListener {
    public ToolBarPanel() {
        super();
        addComponentListener(this);
        setProperties();
    }

    private void setProperties() {
        setMaximumSize(getPreferredSize());
        revalidate();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        setProperties();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        setProperties();
    }

    @Override
    public void componentShown(ComponentEvent e) {
        setProperties();
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        setProperties();
    }
}
