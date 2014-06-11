package cluster;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;

public interface IJobServer extends Remote {

        /**
         * Generic submitting method to enqueue a custom job on the cluster. 
         * @param jobType - Type of the submitted job, according to JobTypes.
         * @param adjmatrix - the adjacency matrix of the network.
         * @param helper - Callback object to the client. Handles progress and results of the compute job.
         * @return true if job could be queued, false if queue is full.
         * @throws RemoteException
         */
        public boolean submitJob(int jobType, int[][] adjmatrix, IComputeCallback helper) throws RemoteException;

        /**
         * submit job method for adjacency lists
         */
        
        /**
         * Submit method for the graph database, returns labels of elements in an adjacency list. 
         * @param startname
         * @param depth
         * @param helper
         * @return
         * @throws RemoteException
         */
        public HashMap<String, HashSet<String>> submitSearch(String startname, int depth, ISearchCallback helper) throws RemoteException;
}
