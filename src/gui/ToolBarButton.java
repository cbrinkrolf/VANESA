package gui;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JButton;

public class ToolBarButton extends JButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	 private Color hoverBackgroundColor = new Color(3, 59, 90).brighter();
     private Color pressedBackgroundColor =Color.PINK;
     
	public ToolBarButton(Icon icon){
		super(icon);
		setProperties();
	}
	
	public ToolBarButton(String txt){
		super(txt);
		setProperties();
	}
	
	private void setProperties(){
		this.setBorderPainted(false); 
		this.setPreferredSize(this.getMinimumSize());
		//this.setSize(10, 10);
		this.setContentAreaFilled(true); 
		//this.setFocusPainted(false); 
		
		//this.setOpaque(true);
	}
	
	/*@Override
    protected void paintComponent(Graphics g) {
        if (getModel().isPressed()) {
            g.setColor(pressedBackgroundColor);
        } else if (getModel().isRollover()) {
            g.setColor(hoverBackgroundColor);
        } else {
            g.setColor(getBackground());
        }
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    @Override
    public void setContentAreaFilled(boolean b) {
    }

    public Color getHoverBackgroundColor() {
        return hoverBackgroundColor;
    }

    public void setHoverBackgroundColor(Color hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    public Color getPressedBackgroundColor() {
        return pressedBackgroundColor;
    }

    public void setPressedBackgroundColor(Color pressedBackgroundColor) {
        this.pressedBackgroundColor = pressedBackgroundColor;
    }*/
}

