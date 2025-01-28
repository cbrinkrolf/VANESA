package graph.jung.graphDrawing;

import java.awt.Font;

import com.google.common.base.Function;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public class MyVertexFontTransformer implements Function<BiologicalNodeAbstract, Font>{

	@Override
	public Font apply(BiologicalNodeAbstract input) {
		// TODO Auto-generated method stub
		Font f = new Font("default", Font.PLAIN, 18);
		//System.out.println(f.getFontName());
		//System.out.println(f.getFamily());
		
		return new Font("default", Font.PLAIN, 18);
	}

}
