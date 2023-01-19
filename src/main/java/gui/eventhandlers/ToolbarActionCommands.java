package gui.eventhandlers;

public enum ToolbarActionCommands {
    newNetwork("new Network"),
    parallelView("parallelview"),
    pick("pick"),
    hierarchy("hierarchy"),
    discretePlace("discretePlace"),
    continuousPlace("continuousPlace"),
    discreteTransition("discreteTransition"),
    continuousTransition("continuousTransition"),
    stochasticTransition("stochasticTransition"),
    center("center"),
    move("move"),
    zoomIn("zoom in"),
    zoomOut("zoom out"),
    del("del"),
    info("info"),
    infoExtended("infoextended"),
    mergeSelectedNodes("mergeSelectedNodes"),
    splitNode("splitNode"),
    coarseSelectedNodes("coarseSelectedNodes"),
    flatSelectedNodes("flatSelectedNodes"),
    group("group"),
    deleteGroup("deleteGroup"),
    enterNode("enterNode"),
    autoCoarse("autocoarse"),
    newWindow("newWindow"),
    convertIntoPetriNet("convertIntoPetriNet"),
    fullScreen("full screen"),
    stretchEdges("stretchEdges"),
    compressEdges("compressEdges"),
    merge("merge"),
    createPetriNet("createPetriNet"),
    createCov("createCov"),
    editElements("editElements"),
    loadModResult("loadModResult"),
    modelling("modelling"),
    heatmap("heatmap"),
    edit("edit"),
    adjustDown("adjustDown"),
    adjustLeft("adjustLeft"),
    adjustHorizontalSpace("adjustHorizontalSpace"),
    adjustVerticalSpace("adjustVerticalSpace");

    public final String value;

    ToolbarActionCommands(final String value) {
        this.value = value;
    }

    public static ToolbarActionCommands get(String value) {
        for (final ToolbarActionCommands command : values())
            if (command.value.equals(value))
                return command;
        return null;
    }
}
