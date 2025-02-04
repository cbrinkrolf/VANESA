package io.image;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGSVGElement;

import configurations.SettingsManager;
import io.BaseWriter;

public class ComponentImageWriter extends BaseWriter<Component> {
	// TODO unify as enum e.g., ImageFormat, and merge with those constants of ChartImageWriter
	public static final String IMAGE_TYPE_SVG = "svg";
	public static final String IMAGE_TYPE_PNG = "png";
	public static final String IMAGE_TYPE_PDF = "pdf";

	private final String imageType;
	
	public ComponentImageWriter(File file, String imageType) {
		super(file);
		this.imageType = imageType;
	}

	@Override
	protected void internalWrite(OutputStream outputStream, Component component) throws Exception {
		if (IMAGE_TYPE_PNG.equals(imageType)) {
			exportPNG(outputStream, component);
		} else if (IMAGE_TYPE_SVG.equals(imageType)) {
			exportSVG(outputStream, component);
		} else if (IMAGE_TYPE_PDF.equals(imageType)) {
			exportPDF(outputStream, component);
		}
	}

	private static void exportSVG(OutputStream outputStream, Component component) throws IOException {

		SVGGraphics2D svgGraphics2D = generateSVGGraphics(component);
		SVGSVGElement root = (SVGSVGElement) svgGraphics2D.getRoot();
		if(SettingsManager.getInstance().isSVGClipPaths()){
			removeClipPaths(root);
		}
		
		try (Writer out = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
			// dump root instead of svgGraphics2D because of possible XML manipulation
			svgGraphics2D.stream(root, out, true, false);
		}

		// output of svg and transformation of svg to pdf to test if clip path worked as intended.
		/* Transcoder transcoder = new PDFTranscoder();
		TranscoderInput transcoderInput = new TranscoderInput(new FileInputStream(new File("filepath\\test.svg")));
		TranscoderOutput transcoderOutput = new TranscoderOutput(new FileOutputStream(new File("filepath\\test.pdf")));
		try {
			transcoder.transcode(transcoderInput, transcoderOutput);
		} catch (TranscoderException e) {
			e.printStackTrace();
		}*/
	}

	private static void exportPNG(OutputStream outputStream, Component component) {
		BufferedImage bufferedImage = new BufferedImage(component.getWidth() * 2, component.getHeight() * 2,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
		g.scale(2, 2);
		component.paint(g);
		for (Iterator<javax.imageio.ImageWriter> iw = ImageIO.getImageWritersByFormatName("png"); iw.hasNext();) {
			javax.imageio.ImageWriter writer = iw.next();
			ImageWriteParam writeParam = writer.getDefaultWriteParam();
			ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier
					.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
			IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
			if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
				continue;
			}
			try (ImageOutputStream stream = ImageIO.createImageOutputStream(outputStream)) {
				setDPI(metadata);
				writer.setOutput(stream);
				writer.write(metadata, new IIOImage(bufferedImage, null, metadata), writeParam);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	private static void setDPI(IIOMetadata metadata) throws IIOInvalidTreeException {
		int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		double dotsPerMilli = 1000.0 * dpi / 10 / 2.54;
		IIOMetadataNode horizontalPixelSize = new IIOMetadataNode("HorizontalPixelSize");
		horizontalPixelSize.setAttribute("value", String.valueOf(dotsPerMilli));
		IIOMetadataNode verticalPixelSize = new IIOMetadataNode("VerticalPixelSize");
		verticalPixelSize.setAttribute("value", String.valueOf(dotsPerMilli));
		IIOMetadataNode dimension = new IIOMetadataNode("Dimension");
		dimension.appendChild(horizontalPixelSize);
		dimension.appendChild(verticalPixelSize);
		IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
		root.appendChild(dimension);
		metadata.mergeTree("javax_imageio_1.0", root);
	}

	private static void exportPDF(OutputStream outputStream, Component component) {

		SVGGraphics2D svgGraphics2D = generateSVGGraphics(component);
		SVGSVGElement root = (SVGSVGElement) svgGraphics2D.getRoot();
		
		if(SettingsManager.getInstance().isPDFClipPaths()){
			removeClipPaths(root);
		}
		
		ByteArrayOutputStream svgOutputStream = new ByteArrayOutputStream();
		Writer out;
		try {
			out = new OutputStreamWriter(svgOutputStream, "UTF-8");
			// dump root instead of svgGraphics2D because of possible XML manipulation
			svgGraphics2D.stream(root, out, true, false);
			String svg = new String(svgOutputStream.toByteArray(), "UTF-8");
			TranscoderInput input = new TranscoderInput(new StringReader(svg));
			TranscoderOutput transOutput = new TranscoderOutput(outputStream);
			SVGAbstractTranscoder transcoder = new PDFTranscoder();

			transcoder.transcode(input, transOutput);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (SVGGraphics2DIOException e) {
			e.printStackTrace();
		} catch (TranscoderException e) {
			e.printStackTrace();
		}
	}

	private static SVGGraphics2D generateSVGGraphics(Component component) {
		DOMImplementation domImplementation = SVGDOMImplementation.getDOMImplementation();
		Document svgDocument = domImplementation.createDocument("http://www.w3.org/2000/svg", "svg", null);
		SVGGraphics2D svgGraphics2D = new SVGGraphics2D(svgDocument);
		svgGraphics2D.setSVGCanvasSize(new Dimension(component.getWidth(), component.getHeight()));
		component.paintAll(svgGraphics2D);
		return svgGraphics2D;
	}
	
	private static void removeClipPaths(SVGSVGElement root){
		Element defs = root.getElementById("defs1");
		NodeList childs = defs.getChildNodes();
		for (int i = childs.getLength() - 1; i >= 0; i--) {
			defs.removeChild(childs.item(i));
		}
		NodeList nodeList = root.getElementsByTagName("*");
		for (int i = 0; i < nodeList.getLength(); i++) {
	        Node node = nodeList.item(i);
	        if (node.getNodeType() == Node.ELEMENT_NODE) {
	            NamedNodeMap map = node.getAttributes();
	            if(map.getNamedItem("clip-path") != null){
	            	map.removeNamedItem("clip-path");
	            }
	        }
	    }
	}
}
