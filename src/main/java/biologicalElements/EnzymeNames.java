package biologicalElements;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Vector;

public class EnzymeNames {
    private static EnzymeNames singleton = null;

    private EnzymeNames() {
    }

    public static EnzymeNames getInstance() {
        if (singleton == null) {
            singleton = new EnzymeNames();
        }
        return singleton;
    }

    private final Vector<String> enzymes = new Vector<>();

    public void fillEnzymeSet() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("EnzymeNames.csv")) {
            final CsvMapper mapper = new CsvMapper();
            CsvSchema schema = mapper.schemaFor(EnzymeName.class).withColumnSeparator(',').withUseHeader(true);
            ObjectReader reader = mapper.readerFor(EnzymeName.class).with(schema).with(CsvParser.Feature.WRAP_AS_ARRAY);
            MappingIterator<EnzymeName> it = reader.readValues(in);
            HashSet<String> enzymeSet = new HashSet<>();
            while (it.hasNext()) {
                EnzymeName name = it.next();
                if (StringUtils.isNotEmpty(name.ec)) {
                    enzymeSet.add(name.ec);
                }
                if (StringUtils.isNotEmpty(name.name)) {
                    enzymeSet.add(name.name);
                }
            }
            enzymes.addAll(enzymeSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vector<String> getEnzymes() {
        return enzymes;
    }

    @JsonPropertyOrder({"ec", "name"})
    private static class EnzymeName {
        @JsonProperty("ec")
        public String ec;
        @JsonProperty("name")
        public String name;
    }
}
