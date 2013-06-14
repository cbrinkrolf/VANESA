/***************************************************************
 * Copyright (c) Benjamin Kormeier 2006-2010.                  *
 * All rights reserved.                                        *
 ***************************************************************/
package configurations;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.log4j.Logger;

/**
 * @author Benjamin Kormeier
 * @version 1.0 08.02.2011
 */
public class ProgramFileLock
{
	private static RandomAccessFile file = null;
	private static FileChannel f = null;
	private static FileLock lock = null;
	
	
	public static boolean writeLock()
	{		
		File lockfile = new File(System.getProperty("java.io.tmpdir"), "vanesa.lock");
		
		try
		{
			file = new RandomAccessFile(lockfile, "rw");
			f = file.getChannel();
		    lock = f.tryLock();
		}
		catch (IOException e)
		{
			Logger.getRootLogger().error(e.getMessage(), e);
		}
	    
	    
	    if (lock != null) 
	    {
	    	lockfile.deleteOnExit();
	    	
	    	return true;
	    }
	    else
	    {
	    	return false;
	    }
	}
	
	public static void releaseLock()
	{
		if (lock!=null&&lock.isValid())
			try
			{
				lock.release();
			}
			catch (IOException e)
			{
				Logger.getRootLogger().error(e.getMessage(), e);
			}
		
		if (file!=null)
			try
			{
				file.close();
			}
			catch (IOException e)
			{
				Logger.getRootLogger().error(e.getMessage(), e);
			}
	}
}	
