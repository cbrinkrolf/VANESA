package api.payloads.ppi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MintEntry {
    public Integer id;
    public String name;
    public String type;
    public String org;
    @JsonProperty("short_label")
    public String shortLabel;

    @Override
    public String toString() {
        return "MintEntry{id='" + id + "', name='" + name + "', type='" + type + "', org='" + org +
                "', shortLabel='" + shortLabel + "'}";
    }
}
