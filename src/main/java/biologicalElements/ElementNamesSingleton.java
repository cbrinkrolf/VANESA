package biologicalElements;

public class ElementNamesSingleton extends ElementNames {
    private static ElementNames settings = null;

    protected ElementNamesSingleton() {
    }

    public static ElementNames getInstance() {
        if (settings == null) {
            settings = new ElementNamesSingleton();
        }
        return settings;
    }

//	public static void setInstance(ElementNames settings) {
//		settings = settings;
//	}
}

