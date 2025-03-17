package io.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
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

import biologicalElements.Pathway;
import configurations.Workspace;
import graph.rendering.VanesaGraphRendererPanel;
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

import io.BaseWriter;

public class ComponentImageWriter extends BaseWriter<Pathway> {
	// TODO unify as enum e.g., ImageFormat, and merge with those constants of ChartImageWriter
	public static final String IMAGE_TYPE_SVG = "svg";
	public static final String IMAGE_TYPE_PNG = "png";
	public static final String IMAGE_TYPE_PDF = "pdf";

	private final String imageType;
	private final Rectangle viewportBounds;

	public ComponentImageWriter(final File file, final String imageType, final Rectangle viewportBounds) {
		super(file);
		this.imageType = imageType;
		this.viewportBounds = viewportBounds;
	}

	@Override
	protected void internalWrite(final OutputStream outputStream, final Pathway pathway) throws Exception {
		if (IMAGE_TYPE_PNG.equals(imageType)) {
			exportPNG(outputStream, pathway.getGraphRenderer());
		} else if (IMAGE_TYPE_SVG.equals(imageType)) {
			exportSVG(outputStream, pathway.getGraphRenderer());
		} else if (IMAGE_TYPE_PDF.equals(imageType)) {
			exportPDF(outputStream, pathway.getGraphRenderer());
		}
	}

	private void exportSVG(final OutputStream outputStream, final VanesaGraphRendererPanel renderer)
			throws IOException {
		final SVGGraphics2D svgGraphics2D = generateSVGGraphics(renderer);
		final SVGSVGElement root = (SVGSVGElement) svgGraphics2D.getRoot();
		if (Workspace.getCurrentSettings().isSVGClipPaths()) {
			removeClipPaths(root);
		}
		try (final Writer out = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
			// dump root instead of svgGraphics2D because of possible XML manipulation
			svgGraphics2D.stream(root, out, true, false);
		}
	}

	private void exportPNG(final OutputStream outputStream, final VanesaGraphRendererPanel renderer) {
		final BufferedImage bufferedImage = new BufferedImage(viewportBounds.width * 2, viewportBounds.height * 2,
				BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
		g.scale(2, 2);
		renderer.render(g, viewportBounds);
		final Iterator<javax.imageio.ImageWriter> iw = ImageIO.getImageWritersByFormatName("png");
		while (iw.hasNext()) {
			final javax.imageio.ImageWriter writer = iw.next();
			final ImageWriteParam writeParam = writer.getDefaultWriteParam();
			final ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(
					BufferedImage.TYPE_INT_RGB);
			final IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
			if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
				continue;
			}
			try (final ImageOutputStream stream = ImageIO.createImageOutputStream(outputStream)) {
				setDPI(metadata);
				writer.setOutput(stream);
				writer.write(metadata, new IIOImage(bufferedImage, null, metadata), writeParam);
			} catch (IOException e) {
				addError(e.getMessage());
			}
			break;
		}
	}

	private static void setDPI(final IIOMetadata metadata) throws IIOInvalidTreeException {
		final int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		final double dotsPerMilli = 1000.0 * dpi / 10 / 2.54;
		final IIOMetadataNode horizontalPixelSize = new IIOMetadataNode("HorizontalPixelSize");
		horizontalPixelSize.setAttribute("value", String.valueOf(dotsPerMilli));
		final IIOMetadataNode verticalPixelSize = new IIOMetadataNode("VerticalPixelSize");
		verticalPixelSize.setAttribute("value", String.valueOf(dotsPerMilli));
		final IIOMetadataNode dimension = new IIOMetadataNode("Dimension");
		dimension.appendChild(horizontalPixelSize);
		dimension.appendChild(verticalPixelSize);
		final IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
		root.appendChild(dimension);
		metadata.mergeTree("javax_imageio_1.0", root);
	}

	private void exportPDF(final OutputStream outputStream, final VanesaGraphRendererPanel renderer) {
		final SVGGraphics2D svgGraphics2D = generateSVGGraphics(renderer);
		final SVGSVGElement root = (SVGSVGElement) svgGraphics2D.getRoot();
		if (Workspace.getCurrentSettings().isPDFClipPaths()) {
			removeClipPaths(root);
		}
		final ByteArrayOutputStream svgOutputStream = new ByteArrayOutputStream();
		try {
			final Writer out = new OutputStreamWriter(svgOutputStream, StandardCharsets.UTF_8);
			// dump root instead of svgGraphics2D because of possible XML manipulation
			svgGraphics2D.stream(root, out, true, false);
			final String svg = svgOutputStream.toString(StandardCharsets.UTF_8);
			final TranscoderInput input = new TranscoderInput(new StringReader(svg));
			final TranscoderOutput transOutput = new TranscoderOutput(outputStream);
			final SVGAbstractTranscoder transcoder = new PDFTranscoder();
			transcoder.transcode(input, transOutput);
		} catch (SVGGraphics2DIOException | TranscoderException e) {
			addError(e.getMessage());
		}
	}

	private SVGGraphics2D generateSVGGraphics(final VanesaGraphRendererPanel renderer) {
		final DOMImplementation domImplementation = SVGDOMImplementation.getDOMImplementation();
		final Document svgDocument = domImplementation.createDocument("http://www.w3.org/2000/svg", "svg", null);
		final SVGGraphics2D svgGraphics2D = new SVGGraphics2D(svgDocument);
		svgGraphics2D.setSVGCanvasSize(new Dimension(viewportBounds.width, viewportBounds.height));
		renderer.render(svgGraphics2D, viewportBounds);
		return svgGraphics2D;
	}

	private static void removeClipPaths(final SVGSVGElement root) {
		final Element defs = root.getElementById("defs1");
		final NodeList childs = defs.getChildNodes();
		for (int i = childs.getLength() - 1; i >= 0; i--) {
			defs.removeChild(childs.item(i));
		}
		final NodeList nodeList = root.getElementsByTagName("*");
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				final NamedNodeMap map = node.getAttributes();
				if (map.getNamedItem("clip-path") != null) {
					map.removeNamedItem("clip-path");
				}
			}
		}
	}
}
