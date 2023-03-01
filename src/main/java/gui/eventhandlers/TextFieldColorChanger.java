package gui.eventhandlers;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TextFieldColorChanger implements FocusListener {
    @Override
    public void focusGained(FocusEvent event) {
        event.getComponent().setBackground(new Color(200, 227, 225));
    }

    @Override
    public void focusLost(FocusEvent event) {
        event.getComponent().setBackground(Color.WHITE);
    }
}
