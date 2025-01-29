package gui;

import graph.CreatePathway;
import graph.GraphInstance;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NewDocumentToolBarButton extends ToolBarButton {
    public NewDocumentToolBarButton() {
        super(ImagePath.getInstance().getImageIcon("newDocumentSmall.png"));
        setToolTipText("Create New Network");
        final JPopupMenu newDocContextMenu = new JPopupMenu();
        final JMenuItem newDocBiologicalGraphItem = new JMenuItem("Biological Graph");
        newDocBiologicalGraphItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNetwork(false);
            }
        });
        final JMenuItem newDocPetriNetItem = new JMenuItem("Petri Net");
        newDocPetriNetItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNetwork(true);
            }
        });
        newDocContextMenu.add(newDocBiologicalGraphItem);
        newDocContextMenu.add(newDocPetriNetItem);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                newDocContextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }

    private void createNetwork(final boolean isPetriNet) {
        new CreatePathway();
        GraphInstance.getPathway().setIsPetriNet(isPetriNet);
        MainWindow.getInstance().getBar().paintToolbar(isPetriNet);
        MainWindow.getInstance().updateAllGuiElements();
    }
}
