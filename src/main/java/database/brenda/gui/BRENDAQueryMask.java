package database.brenda.gui;

import api.payloads.dbBrenda.DBBrendaReaction;
import database.brenda.BRENDASearch;
import database.brenda.BrendaConnector;
import database.gui.QueryMask;
import gui.MainWindow;
import gui.eventhandlers.TextFieldColorChanger;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

public class BRENDAQueryMask extends QueryMask {
    private final JTextField ecNumber;
    private final JTextField name;
    private final JTextField synonym;
    private final JTextField metabolite;
    private final JTextField organism;

    public BRENDAQueryMask() {
        ecNumber = new JTextField(20);
        ecNumber.addFocusListener(new TextFieldColorChanger());

        name = new JTextField(20);
        name.setText("mutase");
        name.addFocusListener(new TextFieldColorChanger());

        synonym = new JTextField(20);
        synonym.addFocusListener(new TextFieldColorChanger());

        metabolite = new JTextField(20);
        metabolite.setText("glycerat");
        metabolite.addFocusListener(new TextFieldColorChanger());

        organism = new JTextField(20);
        organism.setText("bacillus");
        organism.addFocusListener(new TextFieldColorChanger());

        panel.add(new JLabel("BRENDA Search Window"), "span 4");
        panel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10, gap 5");

        panel.add(new JLabel(imagePath.getImageIcon("database-search-outline.png", 48, 48)), "span 2 5");

        panel.add(new JLabel("EC-Number"), "span 2, gap 5");
        panel.add(ecNumber, "span,wrap,growx ,gap 10");
        panel.add(new JLabel("Name"), "span 2, gap 5");
        panel.add(name, "span, wrap, growx, gap 10");
        panel.add(new JLabel("Synonym"), "span 2, gap 5");
        panel.add(synonym, "span, wrap, growx, gap 10");
        panel.add(new JLabel("Metabolite"), "span 2, gap 5");
        panel.add(metabolite, "span, wrap, growx, gap 10");
        panel.add(new JLabel("Organism"), "span 2, gap 5");
        panel.add(organism, "span, wrap 15, growx, gap 10");

        addControlButtons();
    }

    @Override
    public String getMaskName() {
        return "BRENDA";
    }

    @Override
    protected void reset() {
        ecNumber.setText("");
        name.setText("");
        synonym.setText("");
        metabolite.setText("");
        organism.setText("");
    }

    @Override
    protected String search() {
        DBBrendaReaction[] reactions = BRENDASearch.searchReactions(ecNumber.getText().trim(), name.getText().trim(),
                metabolite.getText().trim(), organism.getText().trim(), synonym.getText().trim());
        if (reactions == null || reactions.length == 0) {
            return null;
        }
        MainWindow.getInstance().closeProgressBar();
        BrendaSearchResultWindow searchResultWindow = new BrendaSearchResultWindow(reactions);
        if (!searchResultWindow.show()) {
            return null;
        }
        DBBrendaReaction[] results = searchResultWindow.getSelectedValues();
        if (results != null && results.length > 0) {
            MainWindow.getInstance().showProgressBar("Fetching Network");
            for (DBBrendaReaction reaction : results) {
                BrendaConnector bc = new BrendaConnector(reaction, null, searchResultWindow.getAutoCoarseDepth(),
                        searchResultWindow.getAutoCoarseEnzymeNomenclature(),
                        searchResultWindow.getCoFactorsDecision(),
                        searchResultWindow.getInhibitorsDecision(),
                        searchResultWindow.getSearchDepth(), searchResultWindow.getDisregarded(),
                        searchResultWindow.getOrganismSpecificDecision());
                bc.search();
            }
        }
        return null;
    }

    @Override
    protected boolean doSearchCriteriaExist() {
        return StringUtils.isNotBlank(ecNumber.getText()) || StringUtils.isNotBlank(name.getText()) ||
                StringUtils.isNotBlank(synonym.getText()) || StringUtils.isNotBlank(metabolite.getText()) ||
                StringUtils.isNotBlank(organism.getText());
    }

    @Override
    protected void showInfoWindow() {
        String instructions =
                "<html>" +
                        "<h3>The BRENDA search window</h3>" +
                        "<ul>" +
                        "<li>BRENDA is the comprehensive enzyme information database.<p>" +
                        "BRENDA is maintained and developed at the Institute of Biochemistry <p>" +
                        "and Bioinformatics at the Technical University of Braunschweig, Germany.<p>" +
                        "Data on enzyme function are extracted directly from the primary literature <p>" +
                        "by scientists holding a degree in Biology or Chemistry. Formal and consistency<p>" +
                        "checks are done by computer programs, each data set on a classified enzyme is<p>" +
                        "checked manually by at least one biologist and one chemist.<p>" +
                        "<li>The search window is a query mask that gives the user the<p>" +
                        "possibility to consult the BRENDA database for information of interest. <p>" +
                        "<li>By searching the database for one of the following attributes EC-Number,<p>" +
                        "name, substrate, product or organism the database will be checked <p>" +
                        "for all enzymes that meet the given demands. As a result a list of possible enzymes <p>" +
                        "will be displayed to the user. In the following step the user can choose either one or more <p>" +
                        "enzymes of interest he would like to examine in detail.<p>" +
                        "<li>Additionally the software will try to calculate a possible pathway with a given search depth.<p>" +
                        "The calculation is an iterative procedure in which possible connections to other enzymes<p>" +
                        "will be checked<p>" +
                        "</ul>" +
                        "<h3>How to use the search form</h3>" +
                        "<ul>" +
                        "<li>To search for one attribute simply type " +
                        "in the attribute of interest.  <p><font color=\"#000099\">Example: Homosapiens </font><p>" +
                        "<li>To search for two attributes in one field use the ' & ' char " +
                        "to connect them.  <p><font color=\"#000099\">Example: 5.4.2.1 & 5.3.2.1 </font><p>" +
                        "<li>To search for either one or another attribute in one field " +
                        "use the ' | ' char to connect them. <p><font color=\"#000099\">Example: 5.4.2.1 | 5.3.2.1 </font><p>" +
                        "<li>To search for data where given attributes should not appear " +
                        "put an exclamation mark before that attribute. <p><font color=\"#000099\">Example: !homo </font><p>" +
                        "<ul>" +
                        "</html>";
        JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(), instructions, "BRENDA Information",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
