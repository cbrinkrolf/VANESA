package cluster;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 * @author mlewinsk
 * June 2014
 */
public interface IJobServer extends Remote {

        /**
         * Submit method for the compute cluster, returns true if job could be queued.
         * Available "job-types" can be drawn from JobTypes.java
         * @param jobType - Type of the submitted job, according to JobTypes.
         * @param adjmatrix - the adjacency matrix of the network.
         * @param helper - Callback object to the client. Handles progress and results of the compute job.
         * @return true if job could be queued, false if queue is full.
         * @throws RemoteException
         */
        public boolean submitJob(int jobType, short[][] adjmatrix, IComputeCallback helper) throws RemoteException;

        /**
         * submit job method for adjacency lists
         */
        
        /**
         * Submit method for the compute cluster, returns true if job could be queued.
         * Available "job-types" can be drawn from JobTypes.java
         * @param jobType - Type of the submitted job, according to JobTypes.
         * @param adjarray - the adjacency array of the network.
         * @param helper - Callback object to the client. Handles progress and results of the compute job.
         * @return true if job could be queued, false if queue is full.
         * @throws RemoteException
         */
        public boolean submitJob(int jobType, int[] adjarray, int nodes, IComputeCallback helper) throws RemoteException;
        
        /**
         * Submit method for the graph database, returns true if job could be queued.
         * This method invokes Depth searches on the graph db. 
         * @param startname
         * @param depth
         * @param helper
         * @return
         * @throws RemoteException
         */
        public boolean submitSearch(String startname, int depth, ISearchCallback helper) throws RemoteException;
        
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
         * @param experimentl
         * @param mapping
         * @param helper
         * @return
         * @throws RemoteException
         */
        public boolean submitMapping(int jobType, String[] experiment, HashMap<String, Double>[] mapping, IMappingCallback helper) throws RemoteException;
        
}

