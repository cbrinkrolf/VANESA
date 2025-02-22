package gui.eventhandlers;

public enum MenuActionCommands {
    dataMappingColor("dataMappingColor"),
    datamining("datamining"),
    dataLabelMapping("dataLabelMapping"),
    enrichGene("enrichGene"),
    enrichMirna("enrichMirna"),
    shake("shake"),
    wuff("wuff"),
    newNetwork("new Network"),
    openNetwork("open Network"),
    exportNetwork("export Network"),
    closeNetwork("close Network"),
    closeAllNetworks("close All Networks"),
    save("save"),
    saveAs("save as"),
    graphPicture("graphPicture"),
    visualizationSettings("visualizationSettings"),
    simulationSettings("simulationSettings"),
    exportSettings("exportSettings"),
    allPopUps("allPopUps"),
    nodesEdgesTypes("nodesEdgesTypes"),
    internet("internet"),
    rendererSettings("rendererSettings"),
    mathGraph("mathGraph"),
    biGraph("biGraph"),
    regularGraph("regularGraph"),
    connectedGraph("connectedGraph"),
    hamiltonGraph("hamiltonGraph"),
    graphSettings("graphSettings"),
    transform("transform"),
    showTransformResult("showTransformResult"),
    showPN("showPN"),
    ruleManager("ruleManager"),
    about("about"),
    exit("exit"),
    openTestP("openTestP"),
    openTestT("openTestT"),
    testP("testP"),
    testT("testT"),
    openCov("openCov"),
    cov("cov"),
    createCov("createCov"),
    loadModResult("loadModResult"),
    simulate("simulate"),
    createDoc("createDoc"),
    editElements("editElements"),
    devMode("devMode");

    public final String value;

    MenuActionCommands(final String value) {
        this.value = value;
    }

    public static MenuActionCommands get(String value) {
        for (final MenuActionCommands command : values())
            if (command.value.equals(value))
                return command;
        return null;
    }
}
