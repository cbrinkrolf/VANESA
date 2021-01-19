package graph.layouts.gemLayout;

public class GEMLayoutConfigSingleton {

	
	private static GEMLayoutConfig<?> config;
	
	public static <V> GEMLayoutConfig<V> getInstance(){
		if(config == null){
			config = new GEMLayoutConfig<V>();
		}
		return (GEMLayoutConfig<V>) config;
	}
}
