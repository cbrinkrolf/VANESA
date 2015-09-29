package database.ppi;

import gui.MainWindow;
import gui.MainWindowSingleton;
import gui.ProgressBar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import pojos.DBColumn;
import configurations.Wrapper;
import database.ppi.gui.PPISearchResultWindow;

public class PPISearch extends SwingWorker<Object, Object>{

	private String fullName, alias, acNumber, database;

//	private DatabaseQueryValidator dqv = new DatabaseQueryValidator();
	
	private MainWindow w;
	private ArrayList<DBColumn> results;
	private PPISearchResultWindow ppiSearchResultWindow;
	private boolean headless;
	
	
	public PPISearch(String[] input,MainWindow w, boolean headless) {

		database = input[0];
		fullName = input[1];
		alias = input[2];
		acNumber = input[3];
		
		this.w =w;
		
		this.headless = headless;

	}

	private ArrayList<DBColumn> requestDbContent()
	{

		if (database.equals("HPRD")) {

			if (acNumber.length() > 0) {

				String[] parameters = { acNumber };
				return new Wrapper().requestDbContent(3,
						PPIqueries.hprd_resultForACnumber, parameters);
			} else if (alias.length() > 0) {

				String[] parameters = { "%" + alias + "%" };
				return new Wrapper().requestDbContent(3,
						PPIqueries.hprd_resultForAlias, parameters);
			} else if (fullName.length() > 0) {

				String[] parameters = { "%" + fullName + "%" };
				return new Wrapper().requestDbContent(3,
						PPIqueries.hprd_resultForName, parameters);

			}

		}
		
		else if (database.equals("MINT"))
		{
		
		if (acNumber.length()>0)
			{

				String[] parameters={acNumber};
				return new Wrapper().requestDbContent(4, PPIqueries.mint_resultForACnumber, parameters);

			}
			else if (alias.length()>0)
			{

				String[] parameters={"%"+alias+"%"};
				return new Wrapper().requestDbContent(4, PPIqueries.mint_resultForAlias, parameters);

			}
			else if (fullName.length()>0)
			{

				String[] parameters={"%"+fullName+"%"};
				return new Wrapper().requestDbContent(4, PPIqueries.mint_resultForName, parameters);

			}

		}
		else if (database.equals("IntAct"))
		{


			if (acNumber.length()>0)
			{

				String[] parameters={acNumber};
				return new Wrapper().requestDbContent(4, PPIqueries.intact_resultForACnumber, parameters);

			}
			else if (alias.length()>0)
			{

				String[] parameters={"%"+alias+"%"};
				return new Wrapper().requestDbContent(4, PPIqueries.intact_resultForAlias, parameters);

			}
			else if (fullName.length()>0)
			{

				String[] parameters={"%"+fullName+"%"};
				return new Wrapper().requestDbContent(4, PPIqueries.intact_resultForName, parameters);

			}

		}

		return null;
	}
	 
//	 private Vector requestDbContent() {
//
//		if (database.equals("HPRD")) {
//			
//			if (acNumber.length() > 0) {
//				
//				String[] parameters = {acNumber};
//				return new Wrapper().requestDbContent(3, PPIqueries.hprd_resultForACnumber, parameters);
//				
//			} else if(alias.length() > 0){
//				
//				String[] parameters = {"%"+alias+"%"};
//				return new Wrapper().requestDbContent(3, PPIqueries.hprd_resultForAlias, parameters);
//				
//			} else if(fullName.length() > 0){
//				
//				String[] parameters = {"%"+fullName+"%"};
//				return new Wrapper().requestDbContent(3, PPIqueries.hprd_resultForName, parameters);
//				
//			}
//			
//		} else if (database.equals("MINT")) {
//			
//			if (acNumber.length() > 0) {
//				
//				String[] parameters = {acNumber};
//				return new Wrapper().requestDbContent(4, PPIqueries.mint_resultForACnumber, parameters);
//				
//			} else if(alias.length() > 0){
//				
//				String[] parameters = {"%"+alias+"%"};
//				return new Wrapper().requestDbContent(4, PPIqueries.mint_resultForAlias, parameters);
//				
//			}else if(fullName.length() > 0){
//				
//				String[] parameters = {"%"+fullName+"%"};
//				return new Wrapper().requestDbContent(4, PPIqueries.mint_resultForName, parameters);
//				
//			}
//			
//		} else if (database.equals("IntAct")) {
//			
//			if (acNumber.length() > 0) {
//				
//				String[] parameters = {acNumber};
//				return new Wrapper().requestDbContent(4, PPIqueries.intact_resultForACnumber, parameters);
//				
//			} else if(alias.length() > 0){
//				
//				String[] parameters = {"%"+alias+"%"};
//				return new Wrapper().requestDbContent(4, PPIqueries.intact_resultForAlias, parameters);
//				
//			}else if(fullName.length() > 0){
//				
//				String[] parameters = {"%"+fullName+"%"};
//				return new Wrapper().requestDbContent(4, PPIqueries.intact_resultForName, parameters);
//				
//			}
//			
//		}
//
//		return null;
//	}
	
	
	
	@Override
	protected Object doInBackground() throws Exception
	{
		results=requestDbContent();
		return null;
	}
	
   
	@Override
	public void done() {
		
    	Boolean continueProgress = false;
    	MainWindowSingleton.getInstance().closeProgressBar();
    	
		
		if (results.size() > 0) {
			continueProgress = true;	
			ppiSearchResultWindow = new PPISearchResultWindow(results, database);		
		} else {
			JOptionPane.showMessageDialog(w,
					"Sorry, no entries have been found.");
		}
		
		if (continueProgress) {
			Vector<String[]> results = ppiSearchResultWindow.getAnswer();
			if (results.size() > 0) {			
				MainWindowSingleton.getInstance().showProgressBar("getting PPI Network");
				final Iterator<String[]> it = results.iterator();
				String[] details;
				PPIConnector ppiCon;				
				while (it.hasNext()) {
					
					details = it.next();
//					System.out.println(details[0] + " " + details[3] + " ");
//					System.out.println(ppiSearchResultWindow.getSerchDeapth());
					
					ppiCon = new PPIConnector(details, database, headless);
					ppiCon.setSearchDepth(ppiSearchResultWindow.getSerchDeapth());
					ppiCon.setFinaliseGraph(ppiSearchResultWindow.getFinaliseGraph());
					ppiCon.setAutoCoarse(ppiSearchResultWindow.getAutoCoarse());
					ppiCon.setIncludeBinaryInteractions(ppiSearchResultWindow.getBinaryInteractions());
					ppiCon.setIncludeComplexInteractions(ppiSearchResultWindow.getComplexInteractions());
					
					ppiCon.execute();
					
				}
			}
		}
	}
}
