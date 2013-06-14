package database.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnection
{

	private Connection con = null;
	private Statement stmt = null;
	private String user=new String();
	private String password=new String();
	private String database=new String();
	private String server=new String();
	
	private String dawisDBName=new String();
	private String ppiDBName=new String();
	private String mirnaDBName=new String();
	
	public DBconnection(String user, String password, String database, String server)
	{
		this.user=user;
		this.password=password;
		this.database=database;
		this.server=server;
	}

	public boolean connect_to_Server() 
	{
		try
		{
			connect2mysql(server, user, password);
			useDatabase(database);
			
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public void checkConnection() throws Exception
	{
		if (con==null)
		{
			connect2mysql(server, user, password);
			useDatabase(database);
			
		}
		else if (con.isClosed())
		{
			connect2mysql(server, user, password);
			useDatabase(database);
		}
	}

	public void useDatabase(String database)
	{
		try
		{ con.setCatalog(database);	}
		catch (SQLException e)
		{ 
			// TODO logging
		}
	}
	
	/**
	 * 
	 * @param server
	 * @param user
	 * @param password
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private void connect2mysql(String server, String user, String password) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();

		String url="jdbc:mysql://"+server+"/";
		con=DriverManager.getConnection(url, user, password);
	}
	
	
	public ResultSet selectQuery(String query) throws Exception
	{
		ResultSet rs=null;
		
		checkConnection();

		stmt=con.createStatement();
		rs=stmt.executeQuery(query);
		
		return rs;
	}

	public ResultSet selectQuery(String preparedStatement, String[] inserts) throws Exception 
	{
		checkConnection();

		PreparedStatement ps=con.prepareStatement(preparedStatement);
		ResultSet rs=null;
		
		for (int i=0; i<inserts.length; i++)
		{
			ps.setString((i+1), inserts[i].toString());
		}

		rs=ps.executeQuery();
		
		return rs;
	}

	/**
	 * @return the user
	 */
	public String getUser()
	{
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user)
	{
		this.user=user;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password)
	{
		this.password=password;
	}

	/**
	 * @return the database
	 */
	public String getDatabase()
	{
		return database;
	}

	/**
	 * @param database the database to set
	 */
	public void setDatabase(String database)
	{
		this.database=database;
	}

	/**
	 * @return the server
	 */
	public String getServer()
	{
		return server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(String server)
	{
		this.server=server;
	}

	/**
	 * @return the davisDBName
	 */
	public String getDawisDBName()
	{
		return dawisDBName;
	}

	/**
	 * @param davisDBName the davisDBName to set
	 */
	public void setDawisDBName(String davisDBName)
	{
		this.dawisDBName=davisDBName;
	}

	/**
	 * @return the ppiDBName
	 */
	public String getPpiDBName()
	{
		return ppiDBName;
	}

	/**
	 * @param ppiDBName the ppiDBName to set
	 */
	public void setPpiDBName(String ppiDBName)
	{
		this.ppiDBName=ppiDBName;
	}
	
	public String getmirnaDBName()
	{
		return mirnaDBName;
	}

	/**
	 * @param ppiDBName the ppiDBName to set
	 */
	public void setmirnaDBName(String mirnaDBName)
	{
		this.mirnaDBName=mirnaDBName;
	}
}
