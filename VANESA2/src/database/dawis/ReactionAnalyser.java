package database.dawis;

import java.util.ArrayList;
import java.util.StringTokenizer;

import pojos.DBColumn;

public class ReactionAnalyser
{
//	Vector<String> substrates=new Vector<String>();
//	Vector<String> products=new Vector<String>();
	
	private ArrayList<DBColumn> substrates=new ArrayList<DBColumn>();
	private ArrayList<DBColumn> products=new ArrayList<DBColumn>();
	
	public ReactionAnalyser(String reaction)
	{
		findReactants(reaction);
	}

	private void findReactants(String reaction)
	{
		String[] subProd=reaction.split("<=>");

		StringTokenizer tokenizer=new StringTokenizer(subProd[0], "+");
		String[] s=new String[1];
		while (tokenizer.hasMoreTokens())
		{
			s[0]=testString(tokenizer.nextToken().trim());
			
			substrates.add(new DBColumn(s));

		}
		
		tokenizer=new StringTokenizer(subProd[1], "+");

		while (tokenizer.hasMoreTokens())
		{
			s[0]=testString(tokenizer.nextToken().trim());
			products.add(new DBColumn(s));
		}

	}

	private String testString(String s)
	{

		boolean hasGlycan=s.contains("G");
		boolean hasCompound=s.contains("C");

		int i=0;
		if (hasGlycan)
		{
			i=s.indexOf("G");
		}
		else if (hasCompound)
		{
			i=s.indexOf("C");
		}

		if (i>0)
		{
			s=s.substring(i);
		}

		if (s.startsWith("C")|s.startsWith("G"))
		{
			if (!s.endsWith(")"))
			{
			}
			else
			{
				int j=s.indexOf("(");
				s=s.substring(0, j);
			}
		}

		return s;
	}

//	public Vector<String> getSubstrates()
	public ArrayList<DBColumn> getSubstrates()
	{
		return this.substrates;
	}

//	public Vector<String> getProducts()
	public ArrayList<DBColumn> getProducts()
	{
		return this.products;
	}

}
