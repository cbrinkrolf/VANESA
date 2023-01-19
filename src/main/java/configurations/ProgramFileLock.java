package configurations;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import org.apache.log4j.Logger;

public class ProgramFileLock {
    private static RandomAccessFile file = null;
    private static FileLock lock = null;

    public static boolean writeLock() {
        File lockFile = new File(System.getProperty("java.io.tmpdir"), "vanesa.lock");
        try {
            file = new RandomAccessFile(lockFile, "rw");
            lock = file.getChannel().tryLock();
        } catch (IOException e) {
            Logger.getRootLogger().error(e.getMessage(), e);
        }
        if (lock != null) {
            lockFile.deleteOnExit();
            return true;
        }
        return false;
    }

    public static void releaseLock() {
        if (lock != null && lock.isValid()) {
            try {
                lock.release();
            } catch (IOException e) {
                Logger.getRootLogger().error(e.getMessage(), e);
            }
        }
        if (file != null) {
            try {
                file.close();
            } catch (IOException e) {
                Logger.getRootLogger().error(e.getMessage(), e);
            }
        }
    }
}	
