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
	
	public static final List<String> distributionList = new ArrayList<String>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{add(distributionExponential);
		add(distributionUniform);
		add(distributionTriangular);
		add(distributionTruncatedNormal);
		add(distributionDiscreteProbability);
	}};
	
	public static final Set<String> distributionSet = new HashSet<String>(distributionList);
}
