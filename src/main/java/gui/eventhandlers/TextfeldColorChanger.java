package gui.eventhandlers;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TextfeldColorChanger implements FocusListener{
	
	public TextfeldColorChanger(){
		
	}
	
	public void focusGained(FocusEvent event) {
		
			event.getComponent().setBackground(new Color(200,227,225));	
	}

	public void focusLost(FocusEvent event) {
		
		//String source = event.getComponent().getName();	
		event.getComponent().setBackground(Color.WHITE);
	}
}
