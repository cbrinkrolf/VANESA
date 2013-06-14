package graph;

public class ContainerSingelton extends GraphContainer {

	private static GraphContainer instance =null;

	public static GraphContainer getInstance(){
		if(instance ==null){
			instance =new ContainerSingelton();
		}
		return instance;
	}

	public static void setInstance(GraphContainer submitted){
		instance=submitted;
	
	}

	protected ContainerSingelton(){
	}
}
