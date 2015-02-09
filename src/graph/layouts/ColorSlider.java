package graph.layouts;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JSlider;

public class ColorSlider extends JSlider{

	private static final long serialVersionUID = 145324324L;
	
	public ColorSlider(String title, int value){
		setBorder(BorderFactory.createTitledBorder(title));
		setMinimum(0);
		setMaximum(240);
		setValue(value);
		setBackground(getColor());
	}
	
	@Override
	public void setValue(int val){
		setBackground(getColor());
		super.setValue(val);
	}
	
	public Color getColor(){
		
		if(getValue()<0){
			return Color.GRAY;
		}
		
		int red;
		
		int green;
		
		int blue;

		// grayscale
		if(getValue()<=30){
			red = 240-getValue()*8;
			blue = 240-getValue()*8;
			green = 240-getValue()*8;
		// colorscale
		} else if(getValue()<=60){
			red = (getValue()-30)*8;
			blue = 0;
			green = 0;
		} else if(getValue()<=90){
			red = 240;
			green = (getValue()-60)*8;
			blue = 0;
		} else if(getValue()<=120){
			red = 240-(getValue()-90)*8;
			green = 240;
			blue = 0;
		} else if(getValue()<=150){
			red = 0;
			green = 240;
			blue = (getValue()-120)*8;
		} else if(getValue()<=180){
			red = 0;
			green = 240-(getValue()-150)*8;
			blue = 240;
		} else if(getValue()<=210){
			red = (getValue()-180)*8;
			green = 0;
			blue = 240;
		} else if(getValue()<=240){
			red = 240;
			green = 0;
			blue = 240-(getValue()-210)*8;
		} else {
			return Color.GRAY;
		}
		
		return new Color(red,green,blue);
	}

	public Color getColor(int opacity){
		Color col = getColor();
		return new Color(col.getRed(),col.getGreen(),col.getBlue(),opacity);
	}
}
