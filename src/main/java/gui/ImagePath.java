package gui;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.SVGConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

/**
 * This class is to get the path to image in jar and normal form of the application
 */
public class ImagePath {
	private static final ImagePath instance = new ImagePath();
	private static final SVGTranscoder svgTranscoder = new SVGTranscoder();
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
		if (fileName.toLowerCase(Locale.ROOT).endsWith(".svg")) {
			return getImageIconSVG(fileName, null, null);
		}
		return new ImageIcon(getPath(fileName));
	}

	private ImageIcon getImageIconSVG(final String fileName, final Float width, final Float height) {
		TranscodingHints hints = new TranscodingHints();
		if (width != null) {
			hints.put(ImageTranscoder.KEY_WIDTH, width);
		}
		if (height != null) {
			hints.put(ImageTranscoder.KEY_HEIGHT, height);
		}
		hints.put(ImageTranscoder.KEY_DOM_IMPLEMENTATION, SVGDOMImplementation.getDOMImplementation());
		hints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, SVGConstants.SVG_NAMESPACE_URI);
		hints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT, SVGConstants.SVG_SVG_TAG);
		hints.put(ImageTranscoder.KEY_XML_PARSER_VALIDATING, false);

		svgTranscoder.setTranscodingHints(hints);
		try {
			TranscoderInput ti = new TranscoderInput(getPath(fileName).openStream());
			svgTranscoder.transcode(ti, null);
		} catch (TranscoderException | IOException e) {
			e.printStackTrace();
			return null;
		}
		return new ImageIcon(svgTranscoder.getImage());
	}

	public ImageIcon getImageIcon(String fileName, int width, int height) {
		if (fileName.toLowerCase(Locale.ROOT).endsWith(".svg")) {
			return getImageIconSVG(fileName, (float) width, (float) height);
		}
		ImageIcon imageIcon = new ImageIcon(getPath(fileName));
		Image image = imageIcon.getImage();
		return new ImageIcon(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH));
	}

	public static ImagePath getInstance() {
		return instance;
	}

	public static ImageIcon scaleIcon(ImageIcon icon, int maxDimension) {
		// if already the correct dimensions return the original
		if ((icon.getIconWidth() == maxDimension && icon.getIconHeight() <= maxDimension) || (
				icon.getIconHeight() == maxDimension && icon.getIconWidth() <= maxDimension)) {
			return icon;
		}
		if (icon.getIconWidth() >= icon.getIconHeight()) {
			float scale = maxDimension / (float) icon.getIconWidth();
			return new ImageIcon(icon.getImage()
					.getScaledInstance(maxDimension, (int) (icon.getIconHeight() * scale), Image.SCALE_SMOOTH));
		}
		float scale = maxDimension / (float) icon.getIconHeight();
		return new ImageIcon(icon.getImage()
				.getScaledInstance((int) (icon.getIconWidth() * scale), maxDimension, Image.SCALE_SMOOTH));
	}

	private static class SVGTranscoder extends ImageTranscoder {
		private BufferedImage image = null;

		public BufferedImage createImage(int w, int h) {
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			return image;
		}

		public void writeImage(BufferedImage img, TranscoderOutput out) {
		}

		public BufferedImage getImage() {
			return image;
		}
	}
}
