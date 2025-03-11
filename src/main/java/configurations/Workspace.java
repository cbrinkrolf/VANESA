package configurations;

import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import util.LogConfig;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Workspace {
	private final Logger logger;
	private static Workspace current;
	private static final List<SettingsChangedListener> settingsChangedListener = new ArrayList<>();

	private final Path path;
	private RandomAccessFile file = null;
	private FileLock lock = null;

	private final Settings settings;

	private Workspace(final Path path) {
		this.path = path;
		final File lockFile = path.resolve("vanesa.lock").toFile();
		try {
			file = new RandomAccessFile(lockFile, "rw");
			lock = file.getChannel().tryLock();
			if (lock != null) {
				lockFile.deleteOnExit();
			}
		} catch (IOException ignored) {
		}
		// Redirect log to workspace
		LogConfig.configure(path);
		logger = LogManager.getRootLogger();

		settings = new Settings(this);

		logger.info("Switched workspace to '" + path + "'");
	}

	public static Workspace getCurrent() {
		return current;
	}

	public static void addSettingsChangedListener(final SettingsChangedListener listener) {
		if (!settingsChangedListener.contains(listener)) {
			settingsChangedListener.add(listener);
		}
	}

	public static void removeSettingsChangedListener(final SettingsChangedListener listener) {
		settingsChangedListener.remove(listener);
	}

	public Path getPath() {
		return path;
	}

	public Path resolve(final String path) {
		return this.path.resolve(path);
	}

	public Path resolve(final Path path) {
		return this.path.resolve(path);
	}

	public boolean hasLock() {
		return lock != null && lock.isValid();
	}

	public Settings getSettings() {
		return settings;
	}

	public static Settings getCurrentSettings() {
		return current.settings;
	}

	public static Workspace switchToDefaultWorkspace() {
		final Path basePath = Paths.get(System.getenv(SystemUtils.IS_OS_WINDOWS ? "APPDATA" : "HOME"));
		Path workingDirectory = basePath.resolve("vanesa");
		File f = workingDirectory.toFile();
		if (f.exists() && f.isDirectory()) {
			return switchWorkspace(workingDirectory);
		}
		if (!f.exists()) {
			f.mkdir();
			return switchWorkspace(workingDirectory);
		}
		int i = 0;
		while (f.exists() && !f.isDirectory()) {
			workingDirectory = basePath.resolve("vanesa" + i);
			f = workingDirectory.toFile();
			i++;
		}
		f.mkdir();
		return switchWorkspace(workingDirectory);
	}

	public static Workspace switchWorkspace(final Path path) {
		if (current != null) {
			current.close();
		}
		current = new Workspace(path);
		current.invokeOnChanged();
		return current;
	}

	void invokeOnChanged() {
		for (final SettingsChangedListener listener : settingsChangedListener) {
			listener.onSettingsChanged(settings);
		}
	}

	public synchronized void close() {
		settings.close();
		releaseLock();
		if (current == this) {
			current = null;
		}
	}

	private void releaseLock() {
		if (lock != null && lock.isValid()) {
			try {
				lock.release();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		if (file != null) {
			try {
				file.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		lock = null;
		file = null;
	}
}
