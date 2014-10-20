package database.ppi;

import gui.MainWindow;
import gui.ProgressBar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import pojos.DBColumn;

import configurations.Wrapper;
import database.Connection.DatabaseQueryValidator;

public class PPISearch extends SwingWorker{

	private String fullName, alias, acNumber, database;

//	private DatabaseQueryValidator dqv = new DatabaseQueryValidator();
	
	private MainWindow w;
	private ProgressBar bar;
	private ArrayList<DBColumn> results;
	private PPISearchResultWindow ppiSearchResultWindow;
	
	public PPISearch(String[] input,MainWindow w, ProgressBar bar) {

		database = input[0];
		fullName = input[1];
		alias = input[2];
		acNumber = input[3];
		
		this.w =w;
		this.bar = bar;

	}

	private ArrayList<DBColumn> requestDbContent()
	{

		//MARTIN Abfrage f�r neue und alte DB (switch)
		
		if (database.equals("HPRD"))
		{

			if (acNumber.length()>0)
			{

				String[] parameters={acNumber};
				if(MainWindow.useOldDB)
					return new Wrapper().requestDbContent(3, PPIqueries.hprd_resultForACnumber, parameters);
				else
					return new Wrapper().requestDbContent(3, PPIqueries.hprd_resultForACnumber_new, parameters);
			}
			else if (alias.length()>0)
			{

				String[] parameters={"%"+alias+"%"};
				if(MainWindow.useOldDB)
					return new Wrapper().requestDbContent(3, PPIqueries.hprd_resultForAlias, parameters);
				else				
					return new Wrapper().requestDbContent(3, PPIqueries.hprd_resultForAlias_new, parameters);
			}
			else if (fullName.length()>0)
			{

				String[] parameters={"%"+fullName+"%"};
				if(MainWindow.useOldDB)
					return new Wrapper().requestDbContent(3, PPIqueries.hprd_resultForName, parameters);
				else
					return new Wrapper().requestDbContent(3, PPIqueries.hprd_resultForName_new, parameters);

			}

		}
		
		//MARTIN Neue DB hat Mint und Intact in ppi_db, nicht in dawis_md
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
		w.setLockedPane(true);
		results=requestDbContent();
		w.setLockedPane(false);
		return null;
	}
	
   
	@Override
	public void done() {
		
    	Boolean continueProgress = false;
		endSearch(w, bar);
		
		if (results.size() > 0) {
			continueProgress = true;	
			ppiSearchResultWindow = new PPISearchResultWindow(results, database);		
		} else {
			endSearch(w, bar);
			JOptionPane.showMessageDialog(w,
					"Sorry, no entries have been found.");
		}
		
		if (continueProgress) {
			Vector results = ppiSearchResultWindow.getAnswer();
			if (results.size() != 0) {			
				final Iterator it = results.iterator();
				while (it.hasNext()) {
					
					String[] details = (String[])it.next();
//					System.out.println(details[0] + " " + details[3] + " ");
//					System.out.println(ppiSearchResultWindow.getSerchDeapth());
					
					PPIConnector ppiCon = new PPIConnector(bar,details, database);
					ppiCon.setSearchDepth(ppiSearchResultWindow.getSerchDeapth());
					ppiCon.setFinaliseGraph(ppiSearchResultWindow.getFinaliseGraph());
					ppiCon.setAutoCoarse(ppiSearchResultWindow.getAutoCoarse());
					ppiCon.setIncludeBinaryInteractions(ppiSearchResultWindow.getBinaryInteractions());
					ppiCon.setIncludeComplexInteractions(ppiSearchResultWindow.getComplexInteractions());
					
					ppiCon.execute();
					
				}
			}
		}
		endSearch(w, bar);
	} 
	
	
	
	private void endSearch(final MainWindow w, final ProgressBar bar) 
	{
		Runnable run=new Runnable()
		{
			public void run()
			{
				bar.closeWindow();
				w.setEnable(true);
			}
		};
		
		SwingUtilities.invokeLater(run);
	}
	
}
