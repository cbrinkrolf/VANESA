package api.payloads.dbBrenda;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DBBrendaReaction {
    public String ec;
    @JsonProperty("enzyme_name")
    public String enzymeName;
    public String[] educts;
    public String[] products;
}
