package api.payloads.dbBrenda;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DBBrendaCofactor {
    public String ec;
    public String cofactor;
    @JsonProperty("organism_name")
    public String organismName;
}
