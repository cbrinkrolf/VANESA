package api.payloads.kegg;

import api.payloads.Payload;

public class PathwaySearchRequestPayload extends Payload {
    public String pathway;
    public String organism;
    public String enzyme;
    public String gene;
    public String compound;
}
