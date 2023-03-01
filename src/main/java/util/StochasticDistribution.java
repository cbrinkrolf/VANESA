package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StochasticDistribution {
    public static final String distributionExponential = "Exponential distribution";
    public static final String distributionTriangular = "Triangular distribution";
    public static final String distributionUniform = "Uniform distribution";
    public static final String distributionTruncatedNormal = "Truncated normal distribution";
    public static final String distributionDiscreteProbability = "Discrete probability distribution";
    public static final List<String> distributionList;
    public static final Set<String> distributionSet;

    static {
        distributionList = new ArrayList<>();
        distributionList.add(distributionExponential);
        distributionList.add(distributionUniform);
        distributionList.add(distributionTriangular);
        distributionList.add(distributionTruncatedNormal);
        // implemented in PNlib, but not considered for simulation, yet (08.2021)
        // distributionList.add(distributionDiscreteProbability);
        distributionSet = new HashSet<>(distributionList);
    }
}
