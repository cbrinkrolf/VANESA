package graph.layouts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSlider;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;
import com.jtattoo.plaf.aluminium.AluminiumSliderUI;

public class ColorSlider extends JSlider{

	private static final long serialVersionUID = 145324324L;
	
	public ColorSlider(String title, int value){
		setBorder(BorderFactory.createTitledBorder(title));
		setMinimum(0);
		setMaximum(240);
		setValue(value);
		setUI(new ColorSliderUI(this));
	}
	
	@Override
	public void setValue(int val){
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
	
	class ColorSliderUI extends AluminiumSliderUI
	{
	    
	    ColorSliderUI(JSlider s) {
	        super(s);
	    }
	 
	    @Override
	    public void paint( Graphics g, JComponent c ) {
	        recalculateIfInsetsChanged();
	        recalculateIfOrientationChanged();
	        Rectangle clip = g.getClipBounds();
	 
	        if ( slider.getPaintTrack() && clip.intersects( trackRect ) ) {
	            paintTrack( g );
	        }
	        if ( slider.getPaintTicks() && clip.intersects( tickRect ) ) {
	            paintTicks( g );
	        }
	        if ( slider.getPaintLabels() && clip.intersects( labelRect ) ) {
	            paintLabels( g );
	        }
	        if ( slider.hasFocus() && clip.intersects( focusRect ) ) {
	            paintFocus( g );     
	        }
	        paintThumb( g );
	    }

	    @Override
	    public void paintTrack(Graphics g) {
	    	boolean leftToRight = JTattooUtilities.isLeftToRight(slider);
	    	g.translate(trackRect.x, trackRect.y);
	    	int overhang = 4;
	    	int trackLeft = 0;
	    	int trackTop = 0;
	    	int trackRight = 0;
	    	int trackBottom = 0;
	    	if (slider.getOrientation() == JSlider.HORIZONTAL) {
	    	trackBottom = (trackRect.height - 1) - overhang;
	    	trackTop = trackBottom - (getTrackWidth() - 1);
	    	trackRight = trackRect.width - 1;
	    	} else {
	    	if (leftToRight) {
	    	trackLeft = (trackRect.width - overhang) - getTrackWidth();
	    	trackRight = (trackRect.width - overhang) - 1;
	    	} else {
	    	trackLeft = overhang;
	    	trackRight = overhang + getTrackWidth() - 1;
	    	}
	    	trackBottom = trackRect.height - 1;
	    	}
	    	g.setColor(getColor());
	    	g.drawRect(trackLeft, trackTop, (trackRight - trackLeft) - 1, (trackBottom - trackTop) - 1);
	    	int middleOfThumb = 0;
	    	int fillTop = 0;
	    	int fillLeft = 0;
	    	int fillBottom = 0;
	    	int fillRight = 0;
	    	if (slider.getOrientation() == JSlider.HORIZONTAL) {
	    	middleOfThumb = thumbRect.x + (thumbRect.width / 2);
	    	middleOfThumb -= trackRect.x;
	    	fillTop = trackTop + 1;
	    	fillBottom = trackBottom - 2;
	    	if (!drawInverted()) {
	    	fillLeft = trackLeft + 1;
	    	fillRight = middleOfThumb;
	    	} else {
	    	fillLeft = middleOfThumb;
	    	fillRight = trackRight - 2;
	    	}
	    	Color colors[] = null;
	    	if (!JTattooUtilities.isActive(slider)) {
	    	colors = AbstractLookAndFeel.getTheme().getInActiveColors();
	    	} else {
	    	if (slider.isEnabled()) {
	    	colors = AbstractLookAndFeel.getTheme().getSliderColors();
	    	} else {
	    	colors = AbstractLookAndFeel.getTheme().getDisabledColors();
	    	}
	    	}
	    	colors = new Color[1];
	    	colors[0] = getColor();
	    	JTattooUtilities.fillHorGradient(g, colors, fillLeft + 2, fillTop + 2, fillRight - fillLeft - 2, fillBottom - fillTop - 2);
	    	Color cHi = ColorHelper.darker(getColor(), 5);
	    	Color cLo = ColorHelper.darker(getColor(), 10);
	    	JTattooUtilities.draw3DBorder(g, cHi, cLo, fillLeft + 1, fillTop + 1, fillRight - fillLeft - 1, fillBottom - fillTop - 1);
	    	} else {
	    	middleOfThumb = thumbRect.y + (thumbRect.height / 2);
	    	middleOfThumb -= trackRect.y;
	    	fillLeft = trackLeft + 1;
	    	fillRight = trackRight - 2;
	    	if (!drawInverted()) {
	    	fillTop = middleOfThumb;
	    	fillBottom = trackBottom - 2;
	    	} else {
	    	fillTop = trackTop + 1;
	    	fillBottom = middleOfThumb;
	    	}
	    	Color colors[] = null;
	    	if (!JTattooUtilities.isActive(slider)) {
	    	colors = AbstractLookAndFeel.getTheme().getInActiveColors();
	    	} else {
	    	if (slider.isEnabled()) {
	    	colors = AbstractLookAndFeel.getTheme().getSliderColors();
	    	} else {
	    	colors = AbstractLookAndFeel.getTheme().getDisabledColors();
	    	}
	    	}
	    	colors = new Color[1];
	    	colors[0] = getColor();
	    	JTattooUtilities.fillVerGradient(g, colors, fillLeft + 2, fillTop + 2, fillRight - fillLeft - 2, fillBottom - fillTop - 2);
	    	Color cHi = ColorHelper.darker(getColor(), 5);
	    	Color cLo = ColorHelper.darker(getColor(), 10);
	    	JTattooUtilities.draw3DBorder(g, cHi, cLo, fillLeft + 1, fillTop + 1, fillRight - fillLeft - 1, fillBottom - fillTop - 1);
	    	}
	    	g.translate(-trackRect.x, -trackRect.y);
	        setForeground(getColor());
	    }

	}
	
}
