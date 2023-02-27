package api.payloads.dbBrenda;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DBBrendaTurnoverNumberValue {
    @JsonProperty("organism_name")
    public String organismName;
    @JsonProperty("metabolite_name")
    public String metaboliteName;
    public String tn;
}
