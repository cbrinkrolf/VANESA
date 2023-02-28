package gui.images;

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

    public URL getPath(String fileName) {
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
        return new ImageIcon(image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH));
    }

    public static ImagePath getInstance() {
        return instance;
    }
}
