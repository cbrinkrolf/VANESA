package configurations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

public class XMLResourceBundle {
    public static final XMLResourceBundle SETTINGS = new XMLResourceBundle("settings");
    private static final String SUFFIX = ".properties.xml";
    private final Properties properties;

    /**
     * Resource bundle using the specified base name and the caller's class loader.
     *
     * @param baseName - the base name of the resource bundle, a fully qualified class name
     */
    public XMLResourceBundle(String baseName) {
        this(baseName, null);
    }

    /**
     * Resource bundle using the specified base name, locale, and class loader.
     *
     * @param baseName    - the base name of the resource bundle, a fully qualified class name
     * @param classloader - the class loader from which to load the resource bundle
     */
    public XMLResourceBundle(String baseName, ClassLoader classloader) {
        ClassLoader loader = classloader != null ? classloader : this.getClass().getClassLoader();
        properties = new Properties();
        try (InputStream is = loader.getResourceAsStream(baseName + SUFFIX)) {
            properties.loadFromXML(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets an object for the given key from this resource bundle or one of its parents. This method first tries to
     * obtain the object from this resource bundle using handleGetObject. If not successful, and the parent resource
     * bundle is not null, it calls the parent's getObject method. If still not successful, it throws a
     * MissingResourceException.
     */
    public Object getObject(String key) {
        return handleGetObject(key);
    }

    /**
     * Gets a string for the given key from this resource bundle or one of its parents. Calling this method is
     * equivalent to calling.
     *
     * @param key - the key for the desired object
     * @return the object for the given key
     */
    public String getString(String key) {
        return properties.getProperty(key);
    }

    /**
     * Returns a collection of the keys.
     */
    public Collection<String> getKeys() {
        return properties.stringPropertyNames();
    }

    /**
     * Gets an object for the given key from this resource bundle. Returns null if this resource bundle does not contain
     * an object for the given key.
     *
     * @param key - the key for the desired object
     * @return the object for the given key, or null
     */
    protected Object handleGetObject(String key) {
        return properties.get(key);
    }
}
