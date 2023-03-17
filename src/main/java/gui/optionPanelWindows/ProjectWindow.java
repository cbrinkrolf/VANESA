package gui.optionPanelWindows;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import biologicalElements.Pathway;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ProjectWindow implements FocusListener {
    private final JPanel panel = new JPanel();
    private boolean emptyPane = true;
    private Pathway pw;

    public JPanel getPanel() {
        panel.setVisible(false);
        return panel;
    }

    public void removeAllElements() {
        emptyPane = true;
        panel.removeAll();
        panel.setVisible(false);
    }

    public void revalidateView() {
        if (!emptyPane) {
            panel.removeAll();
        }
        updateWindow();
        panel.setVisible(true);
        panel.repaint();
        panel.revalidate();
        emptyPane = false;
    }

    private void updateWindow() {
        pw = GraphInstance.getPathway();
        MigLayout layout = new MigLayout("fillx", "[grow,fill]", "");
        panel.setLayout(layout);

        panel.add(new JLabel("Pathway"), "gap 5");
        JTextField pathway = new JTextField(pw.getTitle(), 20);
        pathway.setName("pathway");
        pathway.addFocusListener(this);
        panel.add(pathway, "wrap,span 3");

        panel.add(new JLabel("Organism"), "gap 5");
        JTextField organism = new JTextField(pw.getOrganism(), 20);
        organism.setName("organism");
        organism.addFocusListener(this);
        panel.add(organism, "wrap ,span 3");

        panel.add(new JLabel("Author"), "gap 5");
        JTextField author = new JTextField(pw.getAuthor(), 20);
        author.setName("author");
        author.addFocusListener(this);
        panel.add(author, "wrap ,span 3");

        panel.add(new JLabel("Version"), "gap 5");
        JTextField version = new JTextField(pw.getVersion(), 20);
        version.setName("version");
        version.addFocusListener(this);
        panel.add(version, "wrap ,span 3");

        panel.add(new JLabel("Date"), "gap 5");
        JTextField date = new JTextField(pw.getDate(), 20);
        date.setName("date");
        date.addFocusListener(this);
        panel.add(date, "wrap ,span 3");

        MigLayout headerLayout = new MigLayout("fillx", "[right]rel[grow,fill]", "");
        JPanel separatorPanel = new JPanel(headerLayout);
        separatorPanel.add(new JLabel("Description"), "");
        separatorPanel.add(new JSeparator(), "gap 10");
        panel.add(separatorPanel, "wrap, span");

        JTextArea comment = new JTextArea(15, 5);
        comment.setName("comment");
        comment.setText(pw.getDescription());
        comment.addFocusListener(this);
        panel.add(comment, "span,wrap,growx ,gap 10");

        updateWindowTab(pw.getTitle());
    }

    public void updateWindowTab(String name) {
        String newName = GraphContainer.getInstance().renamePathway(pw, name);
        pw.setName(newName);
        MainWindow.getInstance().renameSelectedTab(pw.getName());
        GraphContainer.getInstance().setPetriView(pw.isPetriNet());
        Component[] c = MainWindow.getInstance().getFrame().getContentPane().getComponents();
        for (Component component : c) {
            if (component.getClass().getName().equals("javax.swing.JPanel")) {
                MainWindow.getInstance().getBar().paintToolbar(pw.isPetriNet());
                MainWindow.getInstance().getMenu().setPetriView(pw.isPetriNet() ||
                                                                pw.getTransformationInformation() != null &&
                                                                pw.getTransformationInformation().getPetriNet() !=
                                                                null);
                break;
            }
        }
    }

    @Override
    public void focusGained(FocusEvent event) {
        event.getComponent().setBackground(new Color(200, 227, 255));
    }

    @Override
    public void focusLost(FocusEvent event) {
        String source = event.getComponent().getName();
        String value = ((JTextField) event.getSource()).getText();
        switch (source) {
            case "pathway":
                String newName = GraphContainer.getInstance().renamePathway(pw, value);
                pw.setTitle(newName);
                MainWindow.getInstance().renameSelectedTab(pw.getName());
                ((JTextField) event.getSource()).setText(newName);
                break;
            case "author":
                pw.setAuthor(value);
                break;
            case "version":
                pw.setVersion(value);
                break;
            case "date":
                pw.setDate(value);
                break;
            case "organism":
                pw.setOrganism(value);
                break;
            case "comment":
                pw.setDescription(value);
                break;
        }
        event.getComponent().setBackground(Color.WHITE);
    }
}
