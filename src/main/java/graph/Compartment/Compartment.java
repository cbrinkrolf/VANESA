package graph.Compartment;

import java.awt.Color;

public class Compartment {

	private String name;
	private Color color;
	
	public Compartment(String name, Color color){
		this.name = name;
		this.color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);
	}
}
