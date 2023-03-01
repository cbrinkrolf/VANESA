package gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ToolBarButton extends JButton implements MouseListener {
    public ToolBarButton(Icon icon) {
        super(icon);
        setProperties();
    }

    public ToolBarButton(String txt) {
        super(txt);
        setProperties();
    }

    private void setProperties() {
        addMouseListener(this);
        setBorder(new LineBorder(Color.black));
        setBorderPainted(false);
        setMaximumSize(this.getPreferredSize());
        setBackground(Color.LIGHT_GRAY);
        setContentAreaFilled(false);
        revalidate();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setContentAreaFilled(true);
        setBorderPainted(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setContentAreaFilled(false);
        setBorderPainted(false);
    }
}
