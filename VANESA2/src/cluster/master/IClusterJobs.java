package cluster.master;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;

import cluster.slave.IComputeCallback;
import cluster.slave.IMappingCallback;
import cluster.slave.ISearchCallback;

public interface IClusterJobs extends Remote {
	
	/**
	 * For TESTING ONLY!
	 * @param jobType
	 * @return
	 * @throws RemoteException
	 */
	public boolean submitJob(int jobType, IComputeCallback helper) throws RemoteException;
	

	 /**
     * Submit method for the compute cluster, returns true if job could be queued.
     * Available "job-types" can be drawn from JobTypes.java
     * @param jobType - Type of the submitted job, according to JobTypes.
     * @param jobinformation - bytearray containing serialized objects.
     * @param helper - Callback object to the client. Handles progress and results of the compute job.
     * @return true if job could be queued, false if queue is full.
     * @throws RemoteException
     */
    public boolean submitJob(int jobType, byte[] jobinformation, IComputeCallback helper) throws RemoteException;
    /**
     * Submit method for the graph database, returns true if job could be queued.
     * This method invokes Depth searches on the graph db. 
     * @param startname
     * @param depth
     * @param helper
     * @param db
     * @param direction
     * @return
     * @throws RemoteException
     */
    public boolean submitSearch(String startname, int depth, ISearchCallback helper, String db, int direction) throws RemoteException;
    
    /**
     * Submit method for the graph database, returns true if job could be queued.
     * This method invokes minimal network searches on the graph db. 
     * @param startnodes
     * @param depth
     * @param database - mint, intact, hprd, ... , any
     * @param helper
     * @return
     * @throws RemoteException
     */
    public boolean submitSearch(HashSet<String> startnodes, int depth, String database, ISearchCallback helper) throws RemoteException;
    
    /**
     * Submit method for the graph database, returns true if job could be queued.
     * This method invokes minimal network searches on the graph db. 
     * @param startnodes
     * @param depth
     * @param database - mint, intact, hprd, ... , any
     * @param helper
     * @return
     * @throws RemoteException
     */
    public boolean submitSearch(HashSet<String> datasets, int depth, ISearchCallback helper) throws RemoteException;
    
    /**
     * Submit method for the graph database, returns true if job could be queued.
     * This method invokes a experimental data-mapping on the graph db.
     * @param jobType
     * @param experiment
     * @param mapping
     * @param helper
     * @return
     * @throws RemoteException
     */
    public boolean submitMapping(int jobType, String[] experiment, HashMap<String, Double>[] mapping, IMappingCallback helper) throws RemoteException;
    
}


