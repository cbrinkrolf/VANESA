package graph;import graph.jung.classes.MyGraph;import gui.MainWindow;import gui.MainWindowSingleton;import biologicalElements.GraphElementAbstract;import biologicalElements.Pathway;public class GraphInstance{    MainWindow w = MainWindowSingleton.getInstance();    GraphContainer con = ContainerSingelton.getInstance();    public GraphInstance() {    }        public static MyGraph getMyGraph() {        MainWindow w = MainWindowSingleton.getInstance();        GraphContainer con = ContainerSingelton.getInstance();        try {            return con.getPathway(w.getCurrentPathway()).getGraph();        } catch (Exception e) {            return null;        }            }        public static Pathway getPathwayStatic() {    	MainWindow w = MainWindowSingleton.getInstance();        GraphContainer con = ContainerSingelton.getInstance();        try {        	return con.getPathway(w.getCurrentPathway());        } catch (Exception e) {            return null;        }          }        public Pathway getPathway() {        return con.getPathway(w.getCurrentPathway());    }    /*public Object getPathwayElement(Object element) {        return con.getPathway(w.getCurrentPathway()).getElement(element);    }*/    public GraphContainer getContainer() {        return con;    }    public GraphElementAbstract getSelectedObject() {        return con.getSelectedObject();    }    public void setSelectedObject(GraphElementAbstract selectedObject) {        con.setSelectedObject(selectedObject);    }}