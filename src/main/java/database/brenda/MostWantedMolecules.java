package database.brenda;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MostWantedMolecules {
    private static MostWantedMolecules instance = null;
    private final Map<String, Entry> molecules = new HashMap<>();

    private MostWantedMolecules() {
    }

    public static synchronized MostWantedMolecules getInstance() {
        if (instance == null) {
            instance = new MostWantedMolecules();
        }
        return instance;
    }

    public void fillMoleculeSet() {
        molecules.clear();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("MostWantedMolecules.csv")) {
            final CsvMapper mapper = new CsvMapper();
            CsvSchema schema = mapper.schemaFor(Entry.class).withColumnSeparator(',').withUseHeader(true);
            ObjectReader reader = mapper.readerFor(Entry.class).with(schema).with(CsvParser.Feature.WRAP_AS_ARRAY);
            MappingIterator<Entry> it = reader.readValues(in);
            while (it.hasNext()) {
                Entry entry = it.next();
                molecules.put(entry.name.trim(), entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Entry> getAllValues() {
        List<Entry> result = new ArrayList<>(molecules.values());
        Collections.sort(result);
        return result;
    }

    public Entry getEntry(String name) {
        return molecules.get(name);
    }

    @JsonPropertyOrder({"name", "amount", "disregarded"})
    public static class Entry implements Comparable<Entry> {
        public String name;
        public int amount;
        public boolean disregard;

        public int compareTo(Entry anotherPair) {
            return anotherPair.amount - amount;
        }
    }
}
