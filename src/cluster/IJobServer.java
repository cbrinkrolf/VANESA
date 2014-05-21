package cluster;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Hashtable;

public interface IJobServer extends Remote {

        /**
         * Generic submitting method to enqueue a custom job on the cluster. 
         * @param jobType - Type of the submitted job, according to JobTypes.
         * @param adjmatrix - the adjacency matrix of the network.
         * @param helper - Callback object to the client. Handles progress and results of the compute job.
         * @return true if job could be queued, false if queue is full.
         * @throws RemoteException
         */
        public boolean submitJob(int jobType, int[][] adjmatrix, IClientHelper helper) throws RemoteException;

        /**
         * submit job method for adjacency lists
         */
}
