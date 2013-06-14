package graph.algorithms;

import java.util.Enumeration;
import java.util.Vector;

import javax.vecmath.Vector3f;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * The layout for the placement of nodes / to get the extreme points.
 * 
 * This class is based on the ISOM layout of Bernd Meyer working on an unit hypersphere
 * which reduces the complexity of the formulas drastically.
 * 
 * Bernd Meyer: Self-Organizing Graphs  A Neural Network Perspective of Graph Layout. 
 * Lecture Notes in Computer Science: 1547, Springer Berlin / Heidelberg, 1998.
 * http://www.csse.monash.edu.au/~berndm/ISOM/index.html
 * 
 * For parameter testing see also the applet on:
 * http://www.csse.monash.edu.au/~berndm/ISOM/isom.html
 * 
 * @author (Bernd Meyer), Arne, Bjoern Sommer bjoern@CELLmicrocosmos.org, Sebastian Rubert
 *
 */
public class ISOMLayout{
	private int epoch = 1;
	private int epoch_max;
	private double radius;
	private double radius_min;
	private double narrowing_interval;
	private double narrowing_subtrahend;
	private double coolingFactor;
	private double min_adaption;
	private double max_adaption;	
	private double min_distance;
	private Vector<BiologicalISOMNode> nodes;
	private boolean sameEnzymeSamePosition;

	private Vector<UniqueRandom> uRandomVector;
	private UniqueRandom uRandom;
	private long uRandomSeed;
	private boolean flattened;

	private Pathway pathway;
	
	/**
	 * ISOM specific values:
	 * Epochs Section
	 *  Max. Epochs: The number of epochs (stimuli) after which the learning is stopped.
	 * Radius Section
	 *  Max. Radius: The initial radius of the topological neighborhood to be updated.
	 *  Min. Radius: The minimum radius of the topological neighborhood to be updated at which the narrowing is stopped.
	 *  Narrowing Interval: The number of epochs (stimuli) for which the radius is held constant.
	 *  Narrowing Subtrahend: The number which is substracted from the radius each narrowing interval
	 *   Note: Normally you should be sure that these values fitting to the time:
	 *         E.G.: Given: Epochs: 				1000
	 *               		Max. Radius: 			5.0
	 *               		Min. Radius: 			1.0
	 *               		Narrowing Interval: 	100
	 *               ->		Narrowing Sutrahend: 	0.5 = 4.0/8.0 = range max. to min. radius/number of narrowings 
	 * Adaption Section (learning factor alpha)
	 *  Max. Adaption: The initial adaption factor alpha.
	 *  Min. Adaption: The minimum adaption factor alpha at which the cooling is stopped.
	 *  Cooling Factor: The cooling factor c.
	 * Additional
	 *  Min. Distance: The closest distance two nodes may move together.
	 * (mainly taken from http://www.csse.monash.edu.au/~berndm/ISOM/instructions.html)
	 *  
	 * Cm4-specific values:
	 *  Nodes: The given nodes which positions should be computed.
	 *  Flattened: If this is true, the nodes are placed in a 2D Area in 3D Space (X/Y-Area with Z=0).
	 *  SameEnzymeSamePosition: Place enzymes of the same type at the same position. 
	 *   Important for using and comparing two pathways containing the same nodes (lower computing performance).
	 *  URandomSeed: The Random Seed for the Unique Random Placement used for computing the narrowing points.
	 */
	public ISOMLayout(int epoch_max, double radius_max, double radius_min, double narrowing_interval, double narrowing_subtrahend, 
							double max_adaption, double min_adaption, double coolingFactor, double min_distance, 
							boolean flattened, boolean sameEnzymeSamePosition, long uRandomSeed, Pathway pathway) {
		
		this.epoch_max = epoch_max;
		this.radius = radius_max;
		this.radius_min = radius_min;
		this.narrowing_interval = narrowing_interval;
		this.narrowing_subtrahend = narrowing_subtrahend;
		this.max_adaption = max_adaption;
		this.min_adaption = min_adaption;
		this.coolingFactor = coolingFactor;
		this.min_distance = min_distance;
		
		this.flattened = flattened;
		this.sameEnzymeSamePosition = sameEnzymeSamePosition;
		
		this.uRandomSeed = uRandomSeed;
		uRandomVector = new Vector<UniqueRandom>();
		uRandom = new UniqueRandom(this.uRandomSeed);
		
		this.pathway = pathway;
	}
	
	/**
	 * Start the layout with the standard values
	 */
	public ISOMLayout(Pathway pathway) {
		
		this.epoch_max 				= 500;
		this.radius 				= 3.0;
		this.radius_min 			= 1.0;
		this.narrowing_interval 	= 100;
		this.narrowing_subtrahend 	= 5.0/5.0;
		this.max_adaption 			= 2.8;
		this.min_adaption 			= 1.0;
		this.coolingFactor 			= 0.8;
		this.min_distance 			= 50.0;
		
		this.flattened 				= false;
		this.sameEnzymeSamePosition	= true;
		this.uRandomSeed 			= 1000;
	
//		this.epoch_max 				= 500;
//		this.radius 				= 3.0;
//		this.radius_min 			= 1.0;
//		this.narrowing_interval 	= 100;
//		this.narrowing_subtrahend 	= 2.0/5.0;
//		this.max_adaption 			= 2.0;
//		this.min_adaption 			= 1.0;
//		this.coolingFactor 			= 0.8;
//		this.min_distance 			= 0;
//		
//		this.flattened 				= false;
//		this.sameEnzymeSamePosition	= true;
//		this.uRandomSeed 			= 1000;
		
		uRandomVector = new Vector<UniqueRandom>();
		uRandom = new UniqueRandom(this.uRandomSeed);		
		
		this.pathway = pathway;
	}
	
	/**
	 * Compute the layout.
	 *
	 */
	public void doLayout() {
		
		nodes = new Vector<BiologicalISOMNode>();
		
		for (int i=0; i < pathway.getAllNodesAsVector().size();i++){
			BiologicalNodeAbstract node = (BiologicalNodeAbstract)pathway.getAllNodesAsVector().get(i);
			BiologicalISOMNode isomNode = new BiologicalISOMNode(node);
			nodes.add(isomNode);
		}
		
		// initial positioning of all nodes
		int countElements=0;
		for (Enumeration<BiologicalISOMNode> e = nodes.elements(); e.hasMoreElements();) {
			 BiologicalISOMNode currNode = e.nextElement();
			 uRandomVector.add(new UniqueRandom(currNode));
			 currNode.setPos(flattened?randomVectorFlattened(countElements):randomVector(countElements));
//				 System.out.println("PRE  "+currNode.getName()+" "+currNode.getPos());
			 nodes.set(countElements, currNode);
			 countElements++;
		}
	
		doSphereLayout();
//		doFreeLayout();

	}

	/**
	 * Compute the ISOMLayout using an unit hypersphere for the placement of nodes.
	 *
	 */
	private void doSphereLayout() {
		
		double adaption;
		Vector3f i;
		BiologicalISOMNode w;
		Vector<BiologicalISOMNode> suc;
		
		while(epoch <= epoch_max) {
			double epoch_d = new Double(epoch);
			adaption = Math.max(min_adaption, Math.exp(-coolingFactor*(epoch_d/epoch_max))*max_adaption);
//			System.out.println("adaption at t="+epoch+": "+adaption+
//					" ("+Math.exp(-coolingFactor*(epoch_d/epoch_max))+", "+(epoch_d/epoch_max)+", "+(epoch_max)+")");
			i = flattened?randomVectorFlattened():randomVector(); 
			w = min_dist_arc(i); 
			suc = successors_arc(w, radius);
			for (Enumeration<BiologicalISOMNode> e = suc.elements(); e.hasMoreElements();) {
				BiologicalISOMNode temp = e.nextElement();
				Vector3f savedPos = temp.getPos();
				temp.setPos( normalize (
							 sub( temp.getPos(), 
							 mul( Math.pow(2,-dist_arc(w.getPos(),temp.getPos())), 
								  mul(adaption,sub(temp.getPos(),i)) ) ) ) );
//				if (sameEnzymeSamePosition)
//					setPosForSameEnzymesOrMaps(temp);
				// restore saved position if min_distance is exceeded
				if (min_distance!=0 && dist_arc(w.getPos(),temp.getPos()) < min_distance) {
					temp.setPos(savedPos);
//					System.out.println("savedPos restored");
				}
//				System.out.println("Distance between "+w.getName()+" and "+temp.getName()+": "+
//						dist_arc(w.getPos(),temp.getPos())+" (min: "+min_distance+")");
			}
			epoch ++;
			if ( (epoch%narrowing_interval == 0) && (radius-narrowing_subtrahend >= radius_min) ) {
				radius = radius-narrowing_subtrahend;
//				System.out.println("new radius at t=: "+epoch+": "+radius);
			}
		}
	}
	
	/**
	 * This layout transforms the nodes freely in space. 
	 * Note: Please be sure that the nodes are placed outside the cell components,
	 *       otherwise the translation to the surface will not work correctly.
	 *
	 */
	private void doFreeLayout() {
		
		double adaption;
		Vector3f i;
		BiologicalISOMNode w;
		Vector<BiologicalISOMNode> suc;
		
		while(epoch <= epoch_max) {
			double epoch_d = new Double(epoch);
			adaption = Math.max(min_adaption, Math.exp(-coolingFactor*(epoch_d/epoch_max))*max_adaption);
//			System.out.println("adaption at t="+epoch+": "+adaption+
//					" ("+Math.exp(-coolingFactor*(epoch_d/epoch_max))+", "+(epoch_d/epoch_max)+", "+(epoch_max)+")");
			i = flattened?randomVectorFlattened():randomVector(); 
			w = min_dist(i); 
			suc = successors(w, radius);
			for (Enumeration<BiologicalISOMNode> e = suc.elements(); e.hasMoreElements();) {
				BiologicalISOMNode temp = e.nextElement();
				Vector3f savedPos = temp.getPos();
				temp.setPos( sub( temp.getPos(), 
							 mul( Math.pow(2,-dist(w.getPos(),temp.getPos())), 
								  mul(adaption,sub(temp.getPos(),i)) ) ) );
//				if (sameEnzymeSamePosition)
//					setPosForSameEnzymesOrMaps(temp);
				// restore saved position if min_distance is exceeded
				if (min_distance!=0 && dist(w.getPos(),temp.getPos()) < min_distance) {
					temp.setPos(savedPos);
//					System.out.println("savedPos restored");
				}
			}
			epoch ++;
			if ( (epoch%narrowing_interval == 0) && (radius-narrowing_subtrahend >= radius_min) ) {
				radius = radius-narrowing_subtrahend;
//				System.out.println("new radius at t=: "+epoch+": "+radius);
			}
		}
	}
	
	public Vector<BiologicalISOMNode> getNodes(){
		return this.nodes;
	}
	
//	/**
//	 * Set the position of all internal enzyme nodes of the same name like the given node.
//	 * @param nodeName
//	 * @param position
//	 */
//	private void setPosForSameEnzymesOrMaps(BiologicalNodeAbstract node) {
//		BiologicalNodeAbstract temp;
//		if (node.getType().equals("enzyme")) {
//			for (Enumeration<BiologicalNodeAbstract> e = nodes.elements(); e.hasMoreElements();) {
//				temp =  e.nextElement();
//				// take this to place nodes with same names in the same pathway
//				// on different positions, but there will be no connection to
//				// nodes with the same name in other pathways
//				//if (temp.getName().equals(node.getName()))
//				if (temp.getShortName().equals(node.getShortName()))
//					temp.setPos(node.getPos());
//			}	
//		} else if (node.getType().equals("map")) {
//			for (Enumeration<BiologicalNodeAbstract> e = nodes.elements(); e.hasMoreElements();) {
//				temp =  e.nextElement();
//				if (temp.getName().equals(node.getName()))
//					temp.setPos(node.getPos());
//			}	
//		}
//	}

	/**
	 * Returns all Successors (incl. given node) of the given node 
	 * which are in the range (direct distance) of the given radius.
	 * @param w
	 * @param involvedRadius
	 * @return
	 */
	private Vector<BiologicalISOMNode> successors(BiologicalISOMNode w, double involvedRadius) {
		Vector<BiologicalISOMNode> suc = new Vector<BiologicalISOMNode>();
		BiologicalISOMNode temp;
		suc.add(w);
		for (int i=0; i<nodes.size();i++){
			temp = nodes.get(i);
			if (dist_arc(temp.getPos(),w.getPos()) <= involvedRadius){
				suc.add(temp);
			}
		}
		return suc;
	}
	
	/**
	 * Returns all Successors (incl. given node) of the given node 
	 * which are in the range (arc distance) of the given radius.
	 * @param w
	 * @param involvedRadius
	 * @return
	 */
	private Vector<BiologicalISOMNode> successors_arc(BiologicalISOMNode w, double involvedRadius) {
		Vector<BiologicalISOMNode> suc = new Vector<BiologicalISOMNode>();
		BiologicalISOMNode temp;
		suc.add(w);
		for (int i=0; i<nodes.size();i++){
			temp = nodes.get(i);
			if (dist_arc(temp.getPos(),w.getPos()) <= involvedRadius){
				suc.add(temp);
			}
		}
		return suc;
	}

	/**
	 * Returns the node with the smalles distance to the given position.
	 * @param i
	 * @return CM4Node
	 */
	private BiologicalISOMNode min_dist(Vector3f i) {
		double min_dist = 10; // TODO not nice but unreachable on unit sphere
		double actual_min_dist = 10;
		BiologicalISOMNode min = null;
		BiologicalISOMNode temp;
		if (nodes.size() > 0) {
			min = nodes.firstElement();
		}
		for (Enumeration<BiologicalISOMNode> e = nodes.elements(); e.hasMoreElements();) {
			temp =  e.nextElement();
			actual_min_dist = dist(temp.getPos(), i);
			if (actual_min_dist < min_dist) {
				min_dist = actual_min_dist;
				min = temp;
			}
		}	
		return min;
	}

	/**
	 * Returns the node with the smalles arc distance to the given position.
	 * @param i
	 * @return CM4Node
	 */
	private BiologicalISOMNode min_dist_arc(Vector3f i) {
		double min_dist = 10; // TODO not nice but unreachable on unit sphere
		double actual_min_dist = 10;
		BiologicalISOMNode min = null;
		BiologicalISOMNode temp;
		if (nodes.size() > 0) {
			min = nodes.firstElement();
		}
		for (Enumeration<BiologicalISOMNode> e = nodes.elements(); e.hasMoreElements();) {
			temp =  e.nextElement();
			actual_min_dist = dist_arc(temp.getPos(), i);
			if (actual_min_dist < min_dist) {
				min_dist = actual_min_dist;
				min = temp;
			}
		}	
		return min;
	}
	
	/**
	 * Creating a 3f vector by using the Unique Random Numbers of the overall uRandom values
	 * for creating the random points which are used for narrowing the nodes.
	 * @return Vector3f
	 */
	private Vector3f randomVector() {
		double x = uRandom.getNextDouble(true);// 0.0 .. <1.0
		double y = uRandom.getNextDouble(true);
		if ((Math.pow(x,2)+Math.pow(y,2)) > 1) {
			x = x/2;
			y = y/2;
		}
		double z = Math.sqrt(1-Math.pow(x,2)-Math.pow(y,2));
		// try to make it uniform by preventing z from being always positive here
		if (x-y<0)
			z = -z;
		int quadrant = (int) Math.round(7*uRandom.getNextDouble(true));
		switch (quadrant) {
		case 1: 
			y = -y;
			break;
		case 2:
			x = -x;
			y = -y;
			break;
		case 3:
			x = -x;
			break;
		case 4:
			z = -z;
			break;
		case 5:
			y = -y;
			z = -z;
			break;
		case 6:
			x = -x;
			y = -y;
			z = -z;
		case 7:
			x = -x;
			z = -z;
		default:
			// +X, +Y, +Z 
		}
		return new Vector3f((float) x,(float) y,(float) z); 
	}
	
	/**
	 * Creating a 3f vector by using the Unique Random Numbers of the node name
	 * for initializing the position of the nodes.
	 * @param internalNodeID
	 * @return Vector3f
	 */
	private Vector3f randomVector(int internalNodeID) {
		double x = uRandomVector.get(internalNodeID).getNextDouble(true);
		double y = uRandomVector.get(internalNodeID).getNextDouble(true);
		if ((Math.pow(x,2)+Math.pow(y,2)) > 1) {
			x = x/2;
			y = y/2;
		}
		double z = Math.sqrt(1-Math.pow(x,2)-Math.pow(y,2));
		// try to make it uniform by preventing z from being always positive here
		if (x-y<0)
			z = -z;
		int quadrant = (int) Math.round(7*uRandomVector.get(internalNodeID).getNextDouble(true));
		switch (quadrant) {
		case 1: 
			y = -y;
			break;
		case 2:
			x = -x;
			y = -y;
			break;
		case 3:
			x = -x;
			break;
		case 4:
			z = -z;
			break;
		case 5:
			y = -y;
			z = -z;
			break;
		case 6:
			x = -x;
			y = -y;
			z = -z;
		case 7:
			x = -x;
			z = -z;
		default:
			// +X, +Y, +Z 
		}
		return new Vector3f((float) x,(float) y,(float) z); 
	}

	/**
	 * Creating a 3f vector with z = 0 by using the Unique Random Numbers of the overall uRandom values
	 * for creating the random points which are used for narrowing the nodes.
	 * @return Vector3f
	 */
	private Vector3f randomVectorFlattened() {
		double x = uRandom.getNextDouble(true);
		double y = Math.sqrt(1-Math.pow(x,2));
		double z = 0.0d;
		int quadrant = (int) Math.round(3*uRandom.getNextDouble(true));
		switch (quadrant) {
		case 1: 
			y = -y;
			break;
		case 2:
			x = -x;
			break;
		case 3:
			x = -x;
			y = -y;
		default:
			// +X, +Y 
		}
		return new Vector3f((float) x,(float) y,(float) z); 
	}
	
	/**
	 * Creating a 3f vector with z = 0 by using the Unique Random Numbers of the node name
	 * for initializing the position of the nodes.
	 * @param internalNodeID
	 * @return Vector3f
	 */
	private Vector3f randomVectorFlattened(int internalNodeID) {
		double x = uRandomVector.get(internalNodeID).getNextDouble(true);
		double y = Math.sqrt(1-Math.pow(x,2));
		double z = 0.0d;
		int quadrant = (int) Math.round(3*uRandomVector.get(internalNodeID).getNextDouble(true));
		switch (quadrant) {
		case 1: 
			y = -y;
			break;
		case 2:
			x = -x;
			break;
		case 3:
			x = -x;
			y = -y;
		default:
			// +X, +Y 
		}
		return new Vector3f((float) x,(float) y,(float) z); 
	}

	/**
	 * Computing the direct distance between the two given vectors
	 * @param number
	 * @param vec
	 * @return absolute
	 */
	private double dist(Vector3f pos, Vector3f pos2) {
		double temp =  pos.x*pos2.x+ pos.y*pos2.y+pos.z*pos2.z;
		if (pos.x == pos2.x && pos.y == pos2.y && pos.z == pos2.z)
			temp = 0;
		//System.out.println("computed distance: "+temp);
		return Math.abs(temp);
	}
	
	/**
	 * Computing the distance using the dot product of two vectors
	 * @param number
	 * @param vec
	 * @return absolute
	 */
	private double dist_arc(Vector3f pos, Vector3f pos2) {
		double temp =  pos.x*pos2.x+pos.y*pos2.y+pos.z*pos2.z;
		if (pos.x == pos2.x && pos.y == pos2.y && pos.z == pos2.z)
			temp = 0;
		//System.out.println("computed distance: "+temp);
		return Math.abs(temp);
	}
	
	private Vector3f sub(Vector3f vec1, Vector3f vec2) {
		return new Vector3f(vec1.x-vec2.x,vec1.y-vec2.y, vec1.z-vec2.z);
	}

	private Vector3f mul(double number, Vector3f vec) {
		float numberFloat = Float.parseFloat(Double.toString(number));
		return new Vector3f(numberFloat*vec.x,numberFloat*vec.y, numberFloat*vec.z);
	}
	
	private Vector3f normalize(Vector3f vec) {
		float length = Float.parseFloat(Double.toString(Math.sqrt(Math.pow(vec.x, 2)+Math.pow(vec.y, 2)+Math.pow(vec.z, 2))));
		return new Vector3f(vec.x/length,vec.y/length,vec.z/length);
	}

}
