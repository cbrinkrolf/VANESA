package database.brenda;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import configurations.Wrapper;
import pojos.DBColumn;
import io.MoleculesInput;

public class MostWantedMolecules {
    private static final String FILENAME = "MoleculesSBML.xml";
    private final Hashtable<String, Integer> table = new Hashtable<>();

    Vector<MoleculesPair> v = new Vector<>();

    public MostWantedMolecules() {
        ClassLoader loader = getClass().getClassLoader();
        InputStream is = loader.getResourceAsStream(FILENAME);
        if (is != null) {
            try {
                new MoleculesInput(is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            generateFile();
            try (InputStream isGenerated = loader.getResourceAsStream(FILENAME)) {
                new MoleculesInput(isGenerated);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateFile() {
        ArrayList<DBColumn> results = new Wrapper().requestDbContent(1, BRENDAQueries.getAllBRENDAenzymeDetails);
        while (results.size() == 0) {
            results = new Wrapper().requestDbContent(1, BRENDAQueries.getAllBRENDAenzymeDetails);
        }
        for (DBColumn column : results) {
            String[] details = column.getColumn();
            if (details[3] != null && details[3].length() > 0) {
                separateReaction(details[3]);
            }
        }
        sortElements();
        MoleculeBox.getInstance().fillTable(v);
    }

    private void sortElements() {
        for (String key : table.keySet()) {
            int amount = table.get(key);
            v.add(new MoleculesPair(key, amount, amount > 29));
        }
        Collections.sort(v);
    }

    private void separateReaction(String reaction) {
        StringTokenizer tokenizer = new StringTokenizer(reaction, "=");
        while (tokenizer.hasMoreTokens()) {
            String element = tokenizer.nextToken();
            String[] parts = element.split("\\s\\+\\s");
            for (String part : parts) {
                String temp = part.trim();
                if (table.containsKey(temp)) {
                    int i = table.get(temp);
                    table.remove(temp);
                    table.put(temp, i + 1);
                } else {
                    table.put(temp, 1);
                }
            }
        }
    }
}
