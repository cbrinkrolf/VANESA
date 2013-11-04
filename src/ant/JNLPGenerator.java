/***************************************************************
 * Copyright (c) Benjamin Kormeier 2006-2013.                  *
 * All rights reserved.                                        *
 ***************************************************************/
package ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author Benjamin Kormeier
 * @version 1.0 30.09.2013
 */
public class JNLPGenerator extends Task
{
	private final String DOCTYOPE=new String("<?xml version='1.0' encoding='UTF-8'?>");
	private final String SECURITY=new String("<security><all-permissions /></security>");
	
	private String codebase=null;
	private String href=null;
	private String spec=null;;
	
	private String title=null;
	private String description=null;
	private String vendor=null;
	
	private String jnlpName = new String("vanesa.jnlp");
	private String target = null;
	private String lib = null;
	private String main = null;
	
	
	private String version=null;
	private String xms = new String();
	private String xmx = new String();
	
	private BufferedWriter bf;
	
	@Override
	public void execute()
	{
		if (lib == null)
			throw new BuildException("No library path defined");
		else if (target==null)
			throw new BuildException("No target defined");
		else if (main==null)
			throw new BuildException("No main class defined");
		else if (!new File(target).exists())
			throw new BuildException("Target dir does not exists");
		else if (version==null)
			throw new BuildException("No Java version defined");
		else if (codebase==null)
			throw new BuildException("No codebase defined");
		else if (href==null)
			throw new BuildException("No HREF defined");
		else if (spec==null)
			throw new BuildException("No spec defined");
		
		jnlpName=href;
		
		// -- write script file --
		try
		{
			write();
		}
		catch (IOException e)
		{
			throw new BuildException("Unable to write script files");
		}
	}

	

	public String getCodebase()
	{
		return codebase;
	}

	public void setCodebase(String codebase)
	{
		this.codebase=codebase;
	}

	public String getHref()
	{
		return href;
	}

	public void setHref(String href)
	{
		this.href=href;
	}

	public String getSpec()
	{
		return spec;
	}

	public void setSpec(String spec)
	{
		this.spec=spec;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title=title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description=description;
	}

	public String getVendor()
	{
		return vendor;
	}

	public void setVendor(String vendor)
	{
		this.vendor=vendor;
	}

	public String getJnlpName()
	{
		return jnlpName;
	}

	public void setJnlpName(String jnlpName)
	{
		this.jnlpName=jnlpName;
	}

	public String getTarget()
	{
		return target;
	}

	public void setTarget(String target)
	{
		this.target=target;
	}

	public String getLib()
	{
		return lib;
	}

	public void setLib(String lib)
	{
		this.lib=lib;
	}

	public String getMain()
	{
		return main;
	}

	public void setMain(String main)
	{
		this.main=main;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version=version;
	}

	public String getXms()
	{
		return xms;
	}

	public void setXms(String xms)
	{
		this.xms=xms;
	}

	public String getXmx()
	{
		return xmx;
	}

	public void setXmx(String xmx)
	{
		this.xmx=xmx;
	}

	private String JNLP_Tag_Start()
	{
		return new String("<jnlp codebase='"+codebase+"' href='"+href+"' spec='"+spec+"'>");
	}
	
	private String Information_TAG()
	{
		String information=new String("<information>"+"\n");
		
		information=information.concat("<title>"+title+"</title>"+"\n");
		information=information.concat("<description>"+description+"</description>"+"\n");
		information=information.concat("<vendor>"+vendor+"</vendor>"+"\n");
		
		information=information.concat("</information>");
		
		return information;
	}
	
	private String JAR_Tag(String libname)
	{
		return new String("<jar href='"+libname+"' />");
	}
	
	private String J2SE_Tag()
	{
		return new String("<j2se version='"+version+"'  initial-heap-size=\""+xms+"\" max-heap-size=\""+xmx+"\" />");
	}
	
	private String Application_Tag()
	{
		return new String("<application-desc main-class='"+main+"' />");
	}
	
	private void write() throws IOException
	{
		File file = new File(target + File.separator + jnlpName);
		bf = new BufferedWriter(new FileWriter(file));
		
		String[] libraries = new File(lib).list();
		
		bf.write(DOCTYOPE+"\n");
		bf.write(JNLP_Tag_Start()+"\n");
		bf.write(Information_TAG()+"\n");
		
		bf.write(SECURITY+"\n");
		
		bf.write("<resources>"+"\n");
		bf.write(J2SE_Tag()+"\n");
		
		for (String l : libraries)
		{
			if (l.toLowerCase().endsWith(".jar"))
				bf.write(JAR_Tag(l)+"\n");
		}
		
		
		bf.write("</resources>"+"\n");
		bf.write(Application_Tag()+"\n");
		
		bf.write("</jnlp>");
		
		bf.flush();
		bf.close();
		
		log("Build " + target +" successfull");
	}

}
