package database.brenda;

import api.VanesaApi;
import api.payloads.Response;
import api.payloads.dbBrenda.*;
import com.fasterxml.jackson.core.type.TypeReference;
import gui.MyPopUp;

public final class BRENDASearch {
    private BRENDASearch() {
    }

    public static DBBrendaEnzyme[] searchEnzymes(String ecNumber, String ecName, String metabolite, String organism,
                                                 String synonym) {
        EnzymeSearchRequestPayload payload = new EnzymeSearchRequestPayload();
        payload.ec = ecNumber;
        payload.name = ecName;
        payload.metabolite = metabolite;
        payload.organism = organism;
        payload.synonym = synonym;
        Response<EnzymeSearchResponsePayload> response = VanesaApi.postSync("/db_brenda/enzyme/search", payload,
                new TypeReference<>() {
                });
        if (response.hasError()) {
            MyPopUp.getInstance().show("BRENDA search", "Sorry, no entries have been found.\n" + response.error);
            return null;
        }
        if (response.payload == null || response.payload.results == null || response.payload.results.length == 0) {
            MyPopUp.getInstance().show("BRENDA search", "Sorry, no entries have been found.");
            return null;
        }
        return response.payload.results;
    }

    public static DBBrendaTurnoverNumberValue[] requestTurnoverNumberValues(String ecNumber) {
        EnzymeTurnoverNumberValuesRequestPayload payload = new EnzymeTurnoverNumberValuesRequestPayload();
        payload.ec = ecNumber;
        Response<EnzymeTurnoverNumberValuesResponsePayload> response = VanesaApi.postSync(
                "/db_brenda/enzyme/turnover_number_values", payload, new TypeReference<>() {
                });
        if (response.hasError()) {
            MyPopUp.getInstance().show("BRENDA search", "Sorry, no entries have been found.\n" + response.error);
            return null;
        }
        if (response.payload == null || response.payload.results == null || response.payload.results.length == 0) {
            MyPopUp.getInstance().show("BRENDA search", "Sorry, no entries have been found.");
            return null;
        }
        return response.payload.results;
    }

    public static DBBrendaKMValue[] requestKMValues(String ecNumber) {
        EnzymeKMValuesRequestPayload payload = new EnzymeKMValuesRequestPayload();
        payload.ec = ecNumber;
        Response<EnzymeKMValuesResponsePayload> response = VanesaApi.postSync("/db_brenda/enzyme/km_values", payload,
                new TypeReference<>() {
                });
        if (response.hasError()) {
            MyPopUp.getInstance().show("BRENDA search", "Sorry, no entries have been found.\n" + response.error);
            return null;
        }
        if (response.payload == null || response.payload.results == null || response.payload.results.length == 0) {
            MyPopUp.getInstance().show("BRENDA search", "Sorry, no entries have been found.");
            return null;
        }
        return response.payload.results;
    }

    public static DBBrendaReaction[] searchReactions(String ecNumber, String ecName, String metabolite, String organism,
                                                     String synonym) {
        ReactionSearchRequestPayload payload = new ReactionSearchRequestPayload();
        payload.ec = ecNumber;
        payload.name = ecName;
        payload.metabolite = metabolite;
        payload.organism = organism;
        payload.synonym = synonym;
        Response<ReactionSearchResponsePayload> response = VanesaApi.postSync("/db_brenda/reaction/search", payload,
                new TypeReference<>() {
                });
        if (response.hasError()) {
            MyPopUp.getInstance().show("BRENDA search", "Sorry, no entries have been found.\n" + response.error);
            return null;
        }
        if (response.payload == null || response.payload.results == null || response.payload.results.length == 0) {
            MyPopUp.getInstance().show("BRENDA search", "Sorry, no entries have been found.");
            return null;
        }
        return response.payload.results;
    }
}
