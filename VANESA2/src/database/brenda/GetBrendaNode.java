package database.brenda;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import pojos.DBColumn;

import biologicalObjects.nodes.BRENDANode;
import configurations.Wrapper;
import database.brenda.gui.PossibleBrendaEnzyme;

public class GetBrendaNode {

	public GetBrendaNode() 
	{ }

	public BRENDANode getElementDetails(String element) throws SQLException 
	{
		return processBrendaElement(element);
	}
	
	private ArrayList<DBColumn> getPossibleEnzymes(String e) throws SQLException 
	{
		String enzyme = "%" + e + "%";

		String[] param = { enzyme, enzyme, enzyme, enzyme };
		ArrayList<DBColumn> results = new Wrapper().requestDbContent(1, BRENDAQueries.getEnzymeSynonyms, param);

		return results;
	}
	
//	private Vector getPossibleEnzymes(String e) throws SQLException {
//
//		String enzyme = "%" + e + "%";
//
//		String[] param = { enzyme, enzyme, enzyme, enzyme };
//		Vector results = new Wrapper().requestDbContent(1,
//				BRENDAQueries.getEnzymeSynonyms, param);
//
//		return results;
//
//	}

	private BRENDANode processFoundElement(String enzyme) throws SQLException
	{
		if (enzyme!=null)
		{
			String[] param={enzyme};
			ArrayList<DBColumn> results=new Wrapper().requestDbContent(1, BRENDAQueries.getBRENDAenzymeDetails2, param);

			
			for (DBColumn column : results)
			{
				String[] resultDetails=column.getColumn();
				BRENDANode brendaNode=new BRENDANode();

				brendaNode.setEc_number(resultDetails[0]);
				brendaNode.setName(resultDetails[1]);

				brendaNode.setSysName(resultDetails[2]);

				if (resultDetails[3]!=null)
				{
					brendaNode.setReaction(resultDetails[3]);

					String[] gesplittet=resultDetails[3].split("=");
					brendaNode.setSubstrate(gesplittet[0]);
					brendaNode.setProduct(gesplittet[1]);
				}

				return brendaNode;
			}
		}
		
		return null;
	}
	
//	private BRENDANode processFoundElement(String enzyme) throws SQLException {
//		
//		if (enzyme != null) {
//			String[] param = { enzyme };
//			
//			Vector results = new Wrapper().requestDbContent(1,
//					BRENDAQueries.getBRENDAenzymeDetails2, param);
//			
//			Iterator it = results.iterator();
//			while (it.hasNext()) {
//				
//				BRENDANode brendaNode = new BRENDANode();
//				String[] resultDetails = (String[]) it.next();
//
//				brendaNode.setEc_number(resultDetails[0]);
//				brendaNode.setName(resultDetails[1]);
//
//				brendaNode.setSysName(resultDetails[2]);
//				
//				if(resultDetails[3]!=null){
//					brendaNode.setReaction(resultDetails[3]);
//
//					String[] gesplittet = resultDetails[3].split("=");
//					brendaNode.setSubstrate(gesplittet[0]);
//					brendaNode.setProduct(gesplittet[1]);
//				}
//				
//				return brendaNode;
//			}
//		}
//		return null;
//	}

	private BRENDANode processBrendaElement(String enzyme) throws SQLException 
	{
		String[] param = { enzyme };
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(1, BRENDAQueries.getBRENDAenzymeDetails, param);
		boolean exist = false;

		for (DBColumn column : results)
		{
			String[] resultDetails=column.getColumn();
			BRENDANode brendaNode=new BRENDANode();
			
			exist=true;

			brendaNode.setEc_number(resultDetails[0]);
			brendaNode.setName(resultDetails[1]);

			brendaNode.setSysName(resultDetails[2]);
			brendaNode.setReaction(resultDetails[3]);
			
			if (resultDetails[3]!=null)
			{
				String[] gesplittet=resultDetails[3].split("=");
				brendaNode.setSubstrate(gesplittet[0]);
				brendaNode.setProduct(gesplittet[1]);
			}
			return brendaNode;
		}

		if (exist==false)
		{
			ArrayList<DBColumn> p_enzyme=getPossibleEnzymes(enzyme);
			
			if (p_enzyme.isEmpty())
			{
				return null;
			}
			else if (p_enzyme.size()==1)
			{
				return processFoundElement(p_enzyme.get(0).getColumn()[0]);
				// return processFoundElement(((String[])p_enzyme.get(0))[0]);
			}
			else
			{
				PossibleBrendaEnzyme pbe=new PossibleBrendaEnzyme(p_enzyme, enzyme);

				return processFoundElement(pbe.getSelectedEnzyme());
			}
		}
		return null;
	}
	
//	private BRENDANode processBrendaElement(String enzyme) throws SQLException {
//
//		String[] param = { enzyme };
//		Vector results = new Wrapper().requestDbContent(1,
//				BRENDAQueries.getBRENDAenzymeDetails, param);
//		Iterator it = results.iterator();
//
//		boolean exist = false;
//
//		while (it.hasNext()) {
//			
//			exist = true;
//			
//			BRENDANode brendaNode = new BRENDANode();
//			String[] resultDetails = (String[]) it.next();
//
//			brendaNode.setEc_number(resultDetails[0]);
//			brendaNode.setName(resultDetails[1]);
//
//			brendaNode.setSysName(resultDetails[2]);
//			brendaNode.setReaction(resultDetails[3]);
//			if(resultDetails[3]!=null){
//				String[] gesplittet = resultDetails[3].split("=");
//				brendaNode.setSubstrate(gesplittet[0]);
//				brendaNode.setProduct(gesplittet[1]);
//			}
//			return brendaNode;
//		}
//
//		if (exist == false) {
//
//			Vector v = getPossibleEnzymes(enzyme);
//			if (v.isEmpty()) {
//				return null;
//			}else if(v.size()==1){
//				return processFoundElement(((String[])v.get(0))[0]);
//			}else {
//				PossibleBrendaEnzyme pbe = new PossibleBrendaEnzyme(v, enzyme);
//				
//				return processFoundElement(pbe.getSelectedEnzyme());
//			}
//		}
//		return null;
//	}
}
