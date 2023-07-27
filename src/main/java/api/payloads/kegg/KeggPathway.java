package api.payloads.kegg;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KeggPathway {
    public String id;
    public String name;
    @JsonProperty("kgml_link")
    public String kgmlLink;
    @JsonProperty("taxonomy_id")
    public String taxonomyId;
    @JsonProperty("taxonomy_name")
    public String taxonomyName;

    @Override
    public String toString() {
        return "KeggPathway{id='" + id + "', name='" + name + "', taxonomy_id='" + taxonomyId + "', taxonomy_name='" +
               taxonomyName + "', kgml_link='" + kgmlLink + "'}";
    }
}
