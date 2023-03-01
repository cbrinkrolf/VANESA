package api.payloads.ppi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IntActEntry {
    public Integer id;
    public String name;
    public String type;
    public String org;
    @JsonProperty("short_label")
    public String shortLabel;

    @Override
    public String toString() {
        return "IntActEntry{id='" + id + "', name='" + name + "', type='" + type + "', org='" + org +
                "', shortLabel='" + shortLabel + "'}";
    }
}
