package configurations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import gui.PopUpDialog;
import io.image.ComponentImageWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Settings {
	private static final String FILE_NAME = "settings.xml";

	private final Logger logger = LogManager.getRootLogger();
	private final Path settingsFilePath;
	private final Workspace workspace;
	private Thread watcherThread;
	private final AtomicBoolean isWatcherRunning = new AtomicBoolean();
	private final AtomicBoolean isSaving = new AtomicBoolean();
	private final AtomicBoolean isBatchEditing = new AtomicBoolean();
	private final XmlMapper xmlMapper = new XmlMapper();

	private Model model;

	Settings(final Workspace workspace) {
		settingsFilePath = workspace.resolve(FILE_NAME);
		this.workspace = workspace;
		xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
		xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
		isWatcherRunning.set(true);
		watcherThread = new Thread(() -> {
			final FileSystem fs = FileSystems.getDefault();
			try {
				final WatchService ws = fs.newWatchService();
				workspace.getPath().register(ws, StandardWatchEventKinds.ENTRY_MODIFY);
				while (isWatcherRunning.get()) {
					final WatchKey key;
					try {
						key = ws.poll(1, TimeUnit.SECONDS);
					} catch (InterruptedException ignored) {
						continue;
					}
					if (key != null && !isSaving.get()) {
						for (WatchEvent<?> event : key.pollEvents()) {
							final Path changed = (Path) event.context();
							if (changed.toString().equals(FILE_NAME)) {
								load();
								workspace.invokeOnChanged();
								break;
							}
						}
						key.reset();
					}
				}
				ws.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		watcherThread.start();
		load();
	}

	private void load() {
		if (settingsFilePath.toFile().exists()) {
			try (final InputStream stream = Files.newInputStream(settingsFilePath)) {
				final Model model = xmlMapper.readValue(stream, Model.class);
				if (model != null) {
					this.model = validate(model);
				}
			} catch (IOException e) {
				Logger.getRootLogger().warn("Failed to load settings file '" + settingsFilePath + "'", e);
				model = validate(new Model());
			}
		} else {
			model = validate(new Model());
		}
	}

	private Model validate(final Model model) {
		if (model.apiUrl == null) {
			model.apiUrl = XMLResourceBundle.SETTINGS.getString("settings.default.api.url");
		}
		if (model.defaultImageExportFormat != null && !ComponentImageWriter.IMAGE_TYPE_PDF.equals(
				model.defaultImageExportFormat) && !ComponentImageWriter.IMAGE_TYPE_SVG.equals(
				model.defaultImageExportFormat) && !ComponentImageWriter.IMAGE_TYPE_PNG.equals(
				model.defaultImageExportFormat)) {
			model.defaultImageExportFormat = ComponentImageWriter.IMAGE_TYPE_PNG;
		}
		return model;
	}

	private void save() {
		if (isBatchEditing.get()) {
			return;
		}
		isSaving.set(true);
		try (final OutputStream stream = Files.newOutputStream(settingsFilePath)) {
			xmlMapper.writerWithDefaultPrettyPrinter().withRootName("configuration").writeValue(stream, model);
		} catch (IOException e) {
			Logger.getRootLogger().warn("Failed to save settings file '" + settingsFilePath + "'", e);
		}
		isSaving.set(false);
		workspace.invokeOnChanged();
	}

	void close() {
		if (watcherThread != null) {
			isWatcherRunning.set(false);
			try {
				watcherThread.join();
			} catch (InterruptedException ignored) {
			}
			watcherThread = null;
		}
	}

	public String getFileSaveDirectory() {
		if (StringUtils.isNotEmpty(model.saveDialogPath)) {
			return model.saveDialogPath;
		}
		if (StringUtils.isNotEmpty(model.openDialogPath)) {
			return model.openDialogPath;
		}
		return "";
	}

	public void setFileSaveDirectory(final String fileDir) {
		model.saveDialogPath = fileDir;
		save();
	}

	public String getSaveDialogPath() {
		return model.saveDialogPath;
	}

	public String getFileOpenDirectory() {
		if (StringUtils.isNotEmpty(model.openDialogPath)) {
			return model.openDialogPath;
		}
		if (StringUtils.isNotEmpty(model.saveDialogPath)) {
			return model.saveDialogPath;
		}
		return "";
	}

	public void setFileOpenDirectory(String fileDir) {
		model.openDialogPath = fileDir;
		save();
	}

	public void setYamlVisualizationFile(String filePath) {
		model.yamlVisualizationPath = filePath;
		save();
	}

	public String getYamlVisualizationFile() {
		return model.yamlVisualizationPath;
	}

	/**
	 * @return the proxy host
	 */
	public String getProxyHost() {
		return System.getProperty("http.proxyHost");
	}

	/**
	 * @param proxyHost the proxy host to set
	 */
	public void setProxyHost(String proxyHost) {
		System.setProperty("http.proxyHost", proxyHost);
	}

	/**
	 * @return the proxy port
	 */
	public String getProxyPort() {
		return System.getProperty("http.proxyPort");
	}

	/**
	 * @param proxyPort the proxy port to set
	 */
	public void setProxyPort(String proxyPort) {
		System.setProperty("http.proxyPort", proxyPort);
	}

	public String getApiUrl() {
		return model.apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		model.apiUrl = apiUrl;
		save();
	}

	public boolean isDeveloperMode() {
		return model.isDeveloperMode != null ? model.isDeveloperMode : false;
	}

	public void setDeveloperMode(Boolean developerMode) {
		if (developerMode == null || !developerMode) {
			// Reset to default value
			model.isDeveloperMode = null;
		} else {
			model.isDeveloperMode = true;
		}
		save();
	}

	public void setOMPath(String path, boolean warn) {
		if (isValidDirectory(path)) {
			model.omPath = path;
			save();
		} else if (warn) {
			PopUpDialog.getInstance().show("Wrong path", "Given path: " + path + " is not valid!");
		}
	}

	private boolean isValidDirectory(final String path) {
		if (StringUtils.isEmpty(path)) {
			return false;
		}
		final File file = new File(path);
		return file.exists() && file.isDirectory();
	}

	public String getOMPath() {
		return isValidDirectory(model.omPath) ? model.omPath : "";
	}

	public void setPNlibPath(String path, boolean warn) {
		if (isValidDirectory(path)) {
			model.pnLibPath = path;
			save();
		} else if (warn) {
			PopUpDialog.getInstance().show("Wrong path", "Given path: " + path + " is not valid!");
		}
	}

	public String getPNlibPath() {
		return isValidDirectory(model.pnLibPath) ? model.pnLibPath : "";
	}

	public boolean isOverrideOMPath() {
		return model.isOverrideOMPath != null ? model.isOverrideOMPath : false;
	}

	public void setOverrideOMPath(Boolean override) {
		if (override == null || !override) {
			// Reset to default value
			model.isOverrideOMPath = null;
		} else {
			model.isOverrideOMPath = true;
		}
		save();
	}

	public boolean isOverridePNlibPath() {
		return model.isOverridePNlibPath != null ? model.isOverridePNlibPath : false;
	}

	public void setOverridePNlibPath(Boolean override) {
		if (override == null || !override) {
			// Reset to default value
			model.isOverridePNlibPath = null;
		} else {
			model.isOverridePNlibPath = true;
		}
		save();
	}

	public boolean isSVGClipPaths() {
		return model.isSVGClipPaths != null ? model.isSVGClipPaths : false;
	}

	public void setSVGClipPaths(Boolean svgClipPaths) {
		if (svgClipPaths == null || !svgClipPaths) {
			// Reset to default value
			model.isSVGClipPaths = null;
		} else {
			model.isSVGClipPaths = true;
		}
		save();
	}

	public boolean isPDFClipPaths() {
		return model.isPDFClipPaths != null ? model.isPDFClipPaths : false;
	}

	public void setPDFClipPaths(Boolean pdfClipPaths) {
		if (pdfClipPaths == null || !pdfClipPaths) {
			// Reset to default value
			model.isPDFClipPaths = null;
		} else {
			model.isPDFClipPaths = true;
		}
		save();
	}

	public String getDefaultImageExportFormat() {
		if (StringUtils.isNotEmpty(model.defaultImageExportFormat)) {
			switch (model.defaultImageExportFormat) {
			case ComponentImageWriter.IMAGE_TYPE_PDF:
				return ComponentImageWriter.IMAGE_TYPE_PDF;
			case ComponentImageWriter.IMAGE_TYPE_SVG:
				return ComponentImageWriter.IMAGE_TYPE_SVG;
			case ComponentImageWriter.IMAGE_TYPE_PNG:
				return ComponentImageWriter.IMAGE_TYPE_PNG;
			}
		}
		return ComponentImageWriter.IMAGE_TYPE_PNG;
	}

	public void setDefaultImageExportFormat(String format) {
		if (format == null || format.equals(ComponentImageWriter.IMAGE_TYPE_PNG)) {
			// Reset to default value
			model.defaultImageExportFormat = null;
		} else {
			model.defaultImageExportFormat = format;
		}
		save();
	}

	/**
	 * Setting whether unnecessary files should be removed from the simulation directory after compilation. (Default:
	 * true)
	 */
	public boolean isCleanWorkingDirAfterCompilation() {
		return model.isCleanWorkingDirAfterCompilation == null || model.isCleanWorkingDirAfterCompilation;
	}

	public void setCleanWorkingDirAfterCompilation(Boolean cleanWorkingDirAfterCompilation) {
		if (cleanWorkingDirAfterCompilation == null || cleanWorkingDirAfterCompilation) {
			// Reset to default value
			model.isCleanWorkingDirAfterCompilation = null;
		} else {
			model.isCleanWorkingDirAfterCompilation = false;
		}
		save();
	}

	/**
	 * Batch edit settings while postponing saving and listener events until the provided consumer has finished.
	 */
	public void batchEdit(final Consumer<Settings> editCallback) {
		if (editCallback != null) {
			isBatchEditing.set(true);
			try {
				editCallback.accept(this);
			} catch (final Exception e) {
				logger.error("An error occurred during batch editing settings", e);
			}
			isBatchEditing.set(false);
			save();
		}
	}

	/**
	 * Adding new settings requires adding a new field to this model class with appropriate datatype and
	 * {@link JsonProperty} annotation. A getter and setter method needs to be defined above. The getter handles
	 * returning an appropriate default value. The setter resets the model in case of setting a default value and
	 * invokes the save mechanism via {@link #save()}. Resetting to default values keeps the settings XML small and
	 * reduces the risk of old default values being persisted. Loaded values should be validated in the
	 * {@link #validate(Model)} method.
	 * <p/>
	 * Changing existing settings datatype is dangerous and needs to be carefully tested, so that old settings files are
	 * still loaded correctly! If, for example, a simple boolean flag setting is changed to an enum, it's best to mark
	 * the old setting field deprecated (so it is still loaded), removing the getter and setter and creating a new
	 * setting field with a more appropriate name. Otherwise, upgrading or validating old values should be performed in
	 * the {@link #validate(Model)} method.
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class Model {
		@JsonProperty("SaveDialogPath")
		public String saveDialogPath;
		@JsonProperty("OpenDialogPath")
		public String openDialogPath;
		@JsonProperty("YamlVisualizationPath")
		public String yamlVisualizationPath;
		@JsonProperty("apiUrl")
		public String apiUrl;
		@JsonProperty("isDeveloperMode")
		public Boolean isDeveloperMode;

		// Simulation settings
		@JsonProperty("OMPath")
		public String omPath;
		@JsonProperty("PNlibPath")
		public String pnLibPath;
		@JsonProperty("isOverrideOMPath")
		public Boolean isOverrideOMPath;
		@JsonProperty("isOverridePNlibPath")
		public Boolean isOverridePNlibPath;
		@JsonProperty("isCleanWorkingDirAfterCompilation")
		public Boolean isCleanWorkingDirAfterCompilation;

		// Image export settings
		@JsonProperty("isSVGClipPaths")
		public Boolean isSVGClipPaths;
		@JsonProperty("isPDFClipPaths")
		public Boolean isPDFClipPaths;
		@JsonProperty("defaultImageExportFormat")
		public String defaultImageExportFormat;
	}
}
