package Copy;

import java.util.Vector;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;


public class CopySelectionSingelton extends CopySelection {



	public CopySelectionSingelton(Vector<Vertex> vertices) {
		super(vertices);
		// TODO Auto-generated constructor stub
	}

	private static CopySelection instance =null;

	public static CopySelection getInstance(){
		if(instance ==null){
			instance =new CopySelectionSingelton(new Vector<Vertex>());
		}
		return instance;
	}

	public static void setInstance(CopySelection submitted){
		instance=submitted;
	
	}


	
}

