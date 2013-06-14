package database.kegg;

import java.util.ArrayList;

import pojos.DBColumn;
import biologicalObjects.nodes.KEGGNode;
import configurations.Wrapper;

public class GetKEGGNode {

	private Wrapper wrapper = new Wrapper();

	public GetKEGGNode() 
	{	}

	public KEGGNode getNode(String name, String elementDescription)
	{
		String[] entry={name+"%", elementDescription};
		ArrayList<DBColumn> v=wrapper.requestDbContent(2, KEGGQueries.getPossibleKeggEntry, entry);

		if (v.size()>0)
		{
			return processKeggElements(v.get(0).getColumn());
		}
		else
		{
			return null;
		}
	}
	
//	public KEGGNode getNode(String name, String elementDescription) {
//
//		String[] entry = { name+"%", elementDescription};
//		Vector v = wrapper.requestDbContent(2, KEGGQueries.getPossibleKeggEntry,
//				entry);
//		
//		if (v.size() > 0) {
//			return processKeggElements((String[]) v.elementAt(0));
//		} else {
//			return null;
//		}
//	}
	
	private KEGGNode processKeggElements(String[] set)
	{
		boolean check=false;

		KEGGNode node=new KEGGNode();
		node.setKEGGentryID(set[0]);
		node.setKEGGentryMap(set[5]);
		node.setKEGGentryName(set[1]);
		node.setKEGGentryType(set[2]);
		node.setKEGGentryLink(set[3]);
		node.setKEGGentryReaction(set[4]);

		String[] param={set[1]};

		if (set[2].equals("enzyme"))
		{

			ArrayList<DBColumn> result=wrapper.requestDbContent(2, KEGGQueries.getPathwayEnzymeDetails, param);

			for (DBColumn column : result)
			{
				String[] resultDetails=column.getColumn();
				check=true;
				
				node.setKeggComment(resultDetails[4]);
				node.setKeggenzymeClass(resultDetails[5]);
				node.setKeggsysName(resultDetails[6]);
				node.setKeggreaction(resultDetails[7]);
				node.setKeggsubstrate(resultDetails[9]);
				node.setKeggprodukt(resultDetails[10]);
				node.setKeggcofactor(resultDetails[11]);
				node.setKeggreference(resultDetails[12]);
				node.setKeggorthology(resultDetails[13]);
				node.setKeggeffector(resultDetails[14]);

			}

			result=wrapper.requestDbContent(2, KEGGQueries.getEnzymeNames, param);
			
			for (DBColumn column : result)
			{
				String[] resultDetails=column.getColumn();

				node.addAlternativeName(resultDetails[1]);
			}

			result=wrapper.requestDbContent(2, KEGGQueries.getEnzymeDbLinks, param);
			
			for (DBColumn column : result)
			{
				String[] resultDetails=column.getColumn();

				node.addDBLink(resultDetails[1]+" ("+resultDetails[2]+")");
			}

			result=wrapper.requestDbContent(2, KEGGQueries.getEnzymeStructures, param);
			
			for (DBColumn column : result)
			{
				String[] resultDetails=column.getColumn();

				node.addStructure(resultDetails[1]);
			}
		}
		else if (set[2].equals("gene"))
		{
			ArrayList<DBColumn> result=wrapper.requestDbContent(2, KEGGQueries.getGeneDetails, 	param);

			for (DBColumn column : result)
			{
				String[] resultDetails=column.getColumn();
				check=true;
				
				node.setGeneName(resultDetails[1]);
				node.setGeneDefinition(resultDetails[2]);
				node.setGenePosition(resultDetails[3]);
				node.setGeneCodonUsage(resultDetails[4]);
				node.setGeneAAseqNr(resultDetails[5]);
				node.setGeneAAseq(resultDetails[6]);
				node.setGeneNtseqNr(resultDetails[7]);
				node.setGeneNtSeq(resultDetails[8]);

			}

			result=wrapper.requestDbContent(2, KEGGQueries.getGeneDbLinks, param);
			
			for (DBColumn column : result)
			{
				String[] resultDetails=column.getColumn();
				
				node.addDBLink(resultDetails[1]+" ("+resultDetails[2]+")");
			}

			result =wrapper.requestDbContent(2, KEGGQueries.getGeneMotifs, param);
			
			for (DBColumn column : result)
			{
				String[] resultDetails=column.getColumn();

				node.addGeneMotif(resultDetails[1]+" ("+resultDetails[1]+") ");
			}

			result =wrapper.requestDbContent(2, KEGGQueries.getGeneOrthology, param);
			
			for (DBColumn column : result)
			{
				String[] resultDetails=column.getColumn();

				node.setGeneOrthology(resultDetails[1]);
				node.setGeneOrthologyName(resultDetails[2]);

			}

			result =wrapper.requestDbContent(2, KEGGQueries.getGeneEnzyms, param);
			
			for (DBColumn column : result)
			{
				String[] resultDetails=column.getColumn();

				node.setGeneEnzyme(resultDetails[1]);
			}

		}

		else if (set[2].equals("ortholog"))
		{

		}
		else if (set[2].equals("compound"))
		{

			if (set[1].startsWith("G")||set[1].startsWith("g"))
			{
				ArrayList<DBColumn> result=wrapper.requestDbContent(2, KEGGQueries.getGlycanDetails, param);

				for (DBColumn column : result)
				{
					String[] resultDetails=column.getColumn();
					check=true;
					
					node.setGlycanName(resultDetails[1]);
					node.setCompoundMass(resultDetails[2]);
					node.setCompoundRemarks(resultDetails[3]);
					node.setGlycanOrthology(resultDetails[4]);
					node.setKeggreference(resultDetails[5]);
					node.setGlycanBracket(resultDetails[6]);
					node.setGlycanComposition(resultDetails[7]);
					node.setGlycanNode(resultDetails[8]);
					node.setGlycanEdge(resultDetails[9]);
				}

				result=wrapper.requestDbContent(2, KEGGQueries.getGlycanDbLinks, param);

				for (DBColumn column : result)
				{
					String[] resultDetails=column.getColumn();

					node.addDBLink(resultDetails[1]+" ("+resultDetails[2]+")");

				}
				
				result=wrapper.requestDbContent(2, KEGGQueries.getGlycanEnzyms, param);
				
				for (DBColumn column : result)
				{
					String[] resultDetails=column.getColumn();

					node.addInvolvedElement(resultDetails[1]);
				}
			}
			else
			{
				ArrayList<DBColumn> result=wrapper.requestDbContent(2, KEGGQueries.getCompoundDetails, param);
				
				for (DBColumn column : result)
				{
					String[] resultDetails=column.getColumn();
					check=true;
					
					node.setCompoundFormula(resultDetails[1]);
					node.setCompoundMass(resultDetails[2]);
					node.setCompoundComment(resultDetails[3]);
					node.setCompoundRemarks(resultDetails[4]);
					node.setCompoundAtomsNr(resultDetails[5]);
					node.setCompoundAtoms(resultDetails[6]);
					node.setCompoundBondNr(resultDetails[7]);
					node.setCompoundBonds(resultDetails[8]);
					node.setCompoundSequence(resultDetails[9]);
					node.setCompoundModule(resultDetails[10]);
					node.setCompoundOrganism(resultDetails[11]);

				}

				result=wrapper.requestDbContent(2, KEGGQueries.getCompoundNames, param);
				
				for (DBColumn column : result)
				{
					String[] resultDetails=column.getColumn();

					node.addAlternativeName(resultDetails[1]);
				}

				result=wrapper.requestDbContent(2, KEGGQueries.getCompoundDbLinks, param);
				
				for (DBColumn column : result)
				{
					String[] resultDetails=column.getColumn();

					node.addDBLink(resultDetails[1]+" ("+resultDetails[2]+")");
				}
			}
		}
		
		if (check)
		{
			return node;
		}
		else
		{
			return null;
		}

	}
//	private KEGGNode processKeggElements(String[] set) {
//
//		boolean check = false;
//
//		KEGGNode node = new KEGGNode();
//		node.setKEGGentryID(set[0]);
//		node.setKEGGentryMap(set[5]);
//		node.setKEGGentryName(set[1]);
//		node.setKEGGentryType(set[2]);
//		node.setKEGGentryLink(set[3]);
//		node.setKEGGentryReaction(set[4]);
//
//		String[] param = { set[1] };
//
//		if (set[2].equals("enzyme")) {
//
//			Iterator it = wrapper.requestDbContent(2, KEGGQueries.getPathwayEnzymeDetails, param).iterator();
//
//			while (it.hasNext()) {
//				
//				check = true;
//				String[] resultDetails = (String[]) it.next();
//				node.setKeggComment(resultDetails[4]);
//				node.setKeggenzymeClass(resultDetails[5]);
//				node.setKeggsysName(resultDetails[6]);
//				node.setKeggreaction(resultDetails[7]);
//				node.setKeggsubstrate(resultDetails[9]);
//				node.setKeggprodukt(resultDetails[10]);
//				node.setKeggcofactor(resultDetails[11]);
//				node.setKeggreference(resultDetails[12]);
//				node.setKeggorthology(resultDetails[13]);
//				node.setKeggeffector(resultDetails[14]);
//
//			}
//			Vector v = new Vector();
//
//			it = wrapper.requestDbContent(2, KEGGQueries.getEnzymeNames, param)
//					.iterator();
//			while (it.hasNext()) {
//
//				String[] resultDetails = (String[]) it.next();
//
//				node.addAlternativeName(resultDetails[1]);
//
//			}
//
//			it = wrapper.requestDbContent(2, KEGGQueries.getEnzymeDbLinks, param)
//					.iterator();
//			while (it.hasNext()) {
//
//				String[] resultDetails = (String[]) it.next();
//
//				node
//						.addDBLink(resultDetails[1] + " (" + resultDetails[2]
//								+ ")");
//
//			}
//
//			it = wrapper.requestDbContent(2, KEGGQueries.getEnzymeStructures, param)
//					.iterator();
//			while (it.hasNext()) {
//				String[] resultDetails = (String[]) it.next();
//
//				node.addStructure(resultDetails[1]);
//
//			}
/*
			it = wrapper.requestDbContent(2, KEGGQueries.getEnzymePathways, param)
					.iterator();
			while (it.hasNext()) {

				String[] resultDetails = (String[]) it.next();

				node.addPathwayLink(resultDetails[2] + resultDetails[1]);

			}
*/
//		} else if (set[2].equals("gene")) {
//
//			Iterator it = wrapper.requestDbContent(2, KEGGQueries.getGeneDetails,
//					param).iterator();
//
//			while (it.hasNext()) {
//				check = true;
//				String[] resultDetails = (String[]) it.next();
//				node.setGeneName(resultDetails[1]);
//				node.setGeneDefinition(resultDetails[2]);
//				node.setGenePosition(resultDetails[3]);
//				node.setGeneCodonUsage(resultDetails[4]);
//				node.setGeneAAseqNr(resultDetails[5]);
//				node.setGeneAAseq(resultDetails[6]);
//				node.setGeneNtseqNr(resultDetails[7]);
//				node.setGeneNtSeq(resultDetails[8]);
//
//			}
//
//			it = wrapper.requestDbContent(2, KEGGQueries.getGeneDbLinks, param)
//					.iterator();
//			while (it.hasNext()) {
//				String[] resultDetails = (String[]) it.next();
//
//				node
//						.addDBLink(resultDetails[1] + " (" + resultDetails[2]
//								+ ")");
//
//			}
//		
//			/*
//			it = wrapper.requestDbContent(2, KEGGQueries.getGenePathways, param)
//					.iterator();
//			while (it.hasNext()) {
//				String[] resultDetails = (String[]) it.next();
//
//				node.addPathwayLink(resultDetails[2] + resultDetails[1]);
//
//			}
//			*/
//			it = wrapper.requestDbContent(2, KEGGQueries.getGeneMotifs, param)
//					.iterator();
//			while (it.hasNext()) {
//				String[] resultDetails = (String[]) it.next();
//
//				node.addGeneMotif(resultDetails[1] + " (" + resultDetails[1]
//						+ ") ");
//
//			}
//
//			it = wrapper.requestDbContent(2, KEGGQueries.getGeneOrthology, param)
//					.iterator();
//			while (it.hasNext()) {
//				String[] resultDetails = (String[]) it.next();
//
//				node.setGeneOrthology(resultDetails[1]);
//				node.setGeneOrthologyName(resultDetails[2]);
//
//			}
//
//			it = wrapper.requestDbContent(2, KEGGQueries.getGeneEnzyms, param)
//					.iterator();
//			while (it.hasNext()) {
//				String[] resultDetails = (String[]) it.next();
//
//				node.setGeneEnzyme(resultDetails[1]);
//
//			}
//
//		}
//
//		else if (set[2].equals("ortholog")) {
//
//		} else if (set[2].equals("compound")) {
//
//			if (set[1].startsWith("G") || set[1].startsWith("g")) {
//
//				Iterator it = wrapper.requestDbContent(2,
//						KEGGQueries.getGlycanDetails, param).iterator();
//
//				while (it.hasNext()) {
//					check = true;
//					String[] resultDetails = (String[]) it.next();
//					node.setGlycanName(resultDetails[1]);
//					node.setCompoundMass(resultDetails[2]);
//					node.setCompoundRemarks(resultDetails[3]);
//					node.setGlycanOrthology(resultDetails[4]);
//					node.setKeggreference(resultDetails[5]);
//					node.setGlycanBracket(resultDetails[6]);
//					node.setGlycanComposition(resultDetails[7]);
//					node.setGlycanNode(resultDetails[8]);
//					node.setGlycanEdge(resultDetails[9]);
//
//				}
//
//				it = wrapper.requestDbContent(2, KEGGQueries.getGlycanDbLinks, param)
//						.iterator();
//
//				while (it.hasNext()) {
//					String[] resultDetails = (String[]) it.next();
//
//					node.addDBLink(resultDetails[1] + " (" + resultDetails[2]
//							+ ")");
//
//				}
///*
//				it = wrapper
//						.requestDbContent(2, KEGGQueries.getGlycanPathways, param)
//						.iterator();
//
//				while (it.hasNext()) {
//					String[] resultDetails = (String[]) it.next();
//
//					node.addPathwayLink(resultDetails[2] + resultDetails[1]);
//
//				}
//*/
//				it = wrapper.requestDbContent(2, KEGGQueries.getGlycanEnzyms, param)
//						.iterator();
//				while (it.hasNext()) {
//					String[] resultDetails = (String[]) it.next();
//
//					node.addInvolvedElement(resultDetails[1]);
//
//				}
//
//			} else {
//
//				Iterator it = wrapper.requestDbContent(2,
//						KEGGQueries.getCompoundDetails, param).iterator();
//				while (it.hasNext()) {
//					check = true;
//					String[] resultDetails = (String[]) it.next();
//					node.setCompoundFormula(resultDetails[1]);
//					node.setCompoundMass(resultDetails[2]);
//					node.setCompoundComment(resultDetails[3]);
//					node.setCompoundRemarks(resultDetails[4]);
//					node.setCompoundAtomsNr(resultDetails[5]);
//					node.setCompoundAtoms(resultDetails[6]);
//					node.setCompoundBondNr(resultDetails[7]);
//					node.setCompoundBonds(resultDetails[8]);
//					node.setCompoundSequence(resultDetails[9]);
//					node.setCompoundModule(resultDetails[10]);
//					node.setCompoundOrganism(resultDetails[11]);
//
//				}
//
//				it = wrapper.requestDbContent(2, KEGGQueries.getCompoundNames, param)
//						.iterator();
//				while (it.hasNext()) {
//					String[] resultDetails = (String[]) it.next();
//
//					node.addAlternativeName(resultDetails[1]);
//
//				}
//
//				it = wrapper.requestDbContent(2, KEGGQueries.getCompoundDbLinks,
//						param).iterator();
//				while (it.hasNext()) {
//					String[] resultDetails = (String[]) it.next();
//
//					node.addDBLink(resultDetails[1] + " (" + resultDetails[2]
//							+ ")");
//
//				}
//			}
//		}
//		if(check){
//			return node;
//		}else{
//			return null;
//		}
//		
//	}
}
