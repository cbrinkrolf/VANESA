/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package configurations.asyncWebservice;

/**
 *
 * @author mwesterm
 */
public class DatabaseRequest {


    private String query = null;

    private String databae = null;

    /**
     * Default Constructor, do not remove this, because 
     * this class represents a POJO and needs this constrcutor.
     */
    public DatabaseRequest(){

    }

    public DatabaseRequest(String query, String database){

        this.query = query;
        this.databae = database;
    }
    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @return the databae
     */
    public String getDatabae() {
        return databae;
    }

    /**
     * @param databae the databae to set
     */
    public void setDatabae(String databae) {
        this.databae = databae;
    }

}
