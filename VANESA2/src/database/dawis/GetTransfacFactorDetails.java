package database.dawis;

import java.util.ArrayList;

import pojos.DBColumn;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.Factor;
import configurations.Wrapper;

public class GetTransfacFactorDetails {
	
	private Factor factor = null;
	private DAWISNode don = null;
	
	public GetTransfacFactorDetails(Factor f){
		factor = f;
		don = factor.getDAWISNode();
		fillNodeWithInformations();
	}

	private void fillNodeWithInformations() {
		don.setID(factor.getLabel());
		don.setName(factor.getName());
		getFactorDetails();
	}
	
	private void getFactorDetails()
	{

		String[] det={factor.getLabel()};
		String query=DAWISQueries.getFactorDetails;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setSpecificityNeg(res[1]);
			don.setSpecificityPos(res[2]);
			don.setFactorClass(res[15]+"("+res[3]+")");
			don.setClassification(res[4]);
			don.setEncodingGene(res[5]+"("+res[6]+", Transfac"+")");
			don.setAminoAcidSeqLength(res[9]);
			don.setWeight(res[10]);
			don.setAminoAcidSeq(res[11]);
			don.setDBLink(res[12]);
			don.setSubfamily(res[13]);
			don.setSuperfamily(res[14]);
			don.setType(res[16]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getFactorDetails() {
//		
//		String[] det = { factor.getLabel() };
//		String query = DAWISQueries.getFactorDetails;
//		Vector <String []> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator <String []> it = results.iterator();
//
//		while (it.hasNext()) {
//
//			String[] res = it.next();
//			don.setSpecificityNeg(res[1]);
//			don.setSpecificityPos(res[2]);
//			don.setFactorClass(res[15]+"("+res[3]+")");
//			don.setClassification(res[4]);
//			don.setEncodingGene(res[5]+"("+res[6]+", Transfac"+")");
//			don.setAminoAcidSeqLength(res[9]);
//			don.setWeight(res[10]);
//			don.setAminoAcidSeq(res[11]);
//			don.setDBLink(res[12]);
//			don.setSubfamily(res[13]);
//			don.setSuperfamily(res[14]);
//			don.setType(res[16]);
//		}
//	}
}
