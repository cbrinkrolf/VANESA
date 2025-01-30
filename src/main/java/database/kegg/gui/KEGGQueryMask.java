package database.kegg.gui;

import database.gui.QueryMask;
import database.kegg.KeggSearch;
import gui.MainWindow;
import gui.eventhandlers.TextFieldColorChanger;
import gui.ImagePath;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

public class KEGGQueryMask extends QueryMask {
    private final JTextField pathway;
    private final JTextField organism;
    private final JTextField enzyme;
    private final JTextField gene;
    private final JTextField compound;

    public KEGGQueryMask() {
        pathway = new JTextField(20);
        pathway.setText("Cell Cycle");
        pathway.addFocusListener(new TextFieldColorChanger());

        organism = new JTextField(20);
        organism.setText("homo sapiens");
        organism.addFocusListener(new TextFieldColorChanger());

        enzyme = new JTextField(20);
        enzyme.addFocusListener(new TextFieldColorChanger());

        gene = new JTextField(20);
        gene.addFocusListener(new TextFieldColorChanger());

        compound = new JTextField(20);
        compound.addFocusListener(new TextFieldColorChanger());

        JButton info = new JButton(ImagePath.getInstance().getImageIcon("infoButton.png"));
        info.addActionListener(e -> showInfoWindow());
        info.setBorderPainted(false);

        panel.add(new JLabel("Pathway"), "span 2, gap 5 ");
        panel.add(pathway, "span,wrap,growx ,gap 10");
        panel.add(new JLabel("Organism"), "span 2, gap 5 ");
        panel.add(organism, "span, wrap, growx, gap 10");
        panel.add(new JLabel("Enzyme"), "span 2, gap 5 ");
        panel.add(enzyme, "span, wrap, growx, gap 10");
        panel.add(new JLabel("Gene"), "span 2, gap 5 ");
        panel.add(gene, "span, wrap, growx, gap 10");
        panel.add(new JLabel("Compound"), "span 2, gap 5 ");
        panel.add(compound, "span, wrap, growx, gap 10");
        addControlButtons();
    }

    @Override
    public String getMaskName() {
        return "KEGG";
    }

    @Override
    protected void reset() {
        pathway.setText("");
        organism.setText("");
        enzyme.setText("");
        gene.setText("");
        compound.setText("");
    }

    @Override
    protected String search() {
        KeggSearch.searchPathways(pathway.getText().trim(), organism.getText().trim(), enzyme.getText().trim(),
                gene.getText().trim(), compound.getText().trim());
        return null;
    }

    @Override
    public boolean doSearchCriteriaExist() {
        return StringUtils.isNotBlank(pathway.getText()) || StringUtils.isNotBlank(organism.getText()) ||
                StringUtils.isNotBlank(enzyme.getText()) || StringUtils.isNotBlank(gene.getText()) ||
                StringUtils.isNotBlank(compound.getText());
    }

    @Override
    protected void showInfoWindow() {
        String instructions =
                "<html>" +
                        "<h3>The KEGG search window</h3>" +
                        "<ul>" +
                        "<li>KEGG (Kyoto Encyclopedia of Genes and Genomes) is a collection of online databases<p>" +
                        "dealing with genomes, enzymatic pathways, and biological chemicals.<p>" +
                        "The PATHWAY database records networks of molecular interactions in the cells,<p>" +
                        "and variants of them specific to particular organisms.<p>" +
                        "<li>The search window is a query mask that gives the user the<p>" +
                        "possibilty to consult the KEGG database for information of interest. <p>" +
                        "<li>By searching the database for one of the following attributes pathway,<p>" +
                        "organism, enzyme, gene or compound the database will be checked <p>" +
                        "for all pathways that meet the given demands. As a result a list of possible pathways <p>" +
                        "will be displayed to the user. In the following step the user can choose either one or more <p>" +
                        "pathways of interest.<p>" +
                        "</ul>" +
                        "</ul>" +
                        "<h3>How to use the search form</h3>" +
                        "<ul>" +
                        "<li>To search for one attribute simply type " +
                        "in the attribute of interest.  <p><font color=\"#000099\">Example: Homosapien </font><p>" +
                        "<li>To search for two attributes in one field use the ' & ' char " +
                        "to connect them.  <p><font color=\"#000099\">Example: 5.4.2.1 & 5.3.2.1 </font><p>" +
                        "<li>To search for either one or another attribute in one field " +
                        "use the ' | ' char to connect them. <p><font color=\"#000099\">Example: 5.4.2.1 | 5.3.2.1 </font><p>" +
                        "<li>To search for data where given attributes should not appear " +
                        "put an exclamation mark before that attribute. <p><font color=\"#000099\">Example: !homo </font><p>" +
                        "<ul>" +
                        "</html>";
        JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(), instructions, "KEGG Information",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
