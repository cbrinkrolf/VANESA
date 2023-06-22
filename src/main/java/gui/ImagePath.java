package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * This class is to get the path to image in jar and normal form of the application
 */
public class ImagePath {
    private static final ImagePath instance = new ImagePath();
    private final ClassLoader loader;

    private ImagePath() {
        loader = getClass().getClassLoader();
    }

    private URL getPath(String fileName) {
        URL url = loader.getResource("images/" + fileName);
        if (url == null) {
            System.err.println("Couldn't find file: " + fileName);
        }
        return url;
    }

    public ImageIcon getImageIcon(String fileName) {
        return new ImageIcon(getPath(fileName));
    }

    public ImageIcon getImageIcon(String fileName, int width, int height) {
        ImageIcon imageIcon = new ImageIcon(getPath(fileName));
        Image image = imageIcon.getImage();
        return new ImageIcon(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH));
    }

    public static ImagePath getInstance() {
        return instance;
    }

    public static ImageIcon scaleIcon(ImageIcon icon, int maxDimension) {
        // if already the correct dimensions return the original
        if ((icon.getIconWidth() == maxDimension && icon.getIconHeight() <= maxDimension) ||
                (icon.getIconHeight() == maxDimension && icon.getIconWidth() <= maxDimension)) {
            return icon;
        }
        if (icon.getIconWidth() >= icon.getIconHeight()) {
            float scale = maxDimension / (float) icon.getIconWidth();
            return new ImageIcon(icon.getImage().getScaledInstance(maxDimension, (int) (icon.getIconHeight() * scale),
                    Image.SCALE_SMOOTH));
        }
        float scale = maxDimension / (float) icon.getIconHeight();
        return new ImageIcon(icon.getImage().getScaledInstance((int) (icon.getIconWidth() * scale), maxDimension,
                Image.SCALE_SMOOTH));
    }
}
