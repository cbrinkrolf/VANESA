package io.image;

import io.BaseWriter;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGSVGElement;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ComponentImageWriter extends BaseWriter<Component> {
    public static final String IMAGE_TYPE_SVG = "svg";
    public static final String IMAGE_TYPE_PNG = "png";

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
        }
    }

    private static void exportSVG(OutputStream outputStream, Component component) throws IOException {
        DOMImplementation domImplementation = SVGDOMImplementation.getDOMImplementation();
        Document svgDocument = domImplementation.createDocument("http://www.w3.org/2000/svg", "svg", null);
        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(svgDocument);
        svgGraphics2D.setSVGCanvasSize(new Dimension(component.getWidth(), component.getHeight()));
        component.paintAll(svgGraphics2D);
        // removing clip paths
        SVGSVGElement root = (SVGSVGElement) svgGraphics2D.getRoot();
        Element defs = root.getElementById("defs1");
        NodeList childs = defs.getChildNodes();
        for (int i = childs.getLength() - 1; i >= 0; i--) {
            defs.removeChild(childs.item(i));
        }
        try (Writer out = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            // dump root instead of svgGraphics2D because of XML manipulation
            svgGraphics2D.stream(root, out, true, false);
        }
    }

    private static void exportPNG(OutputStream outputStream, Component component) {
        BufferedImage bufferedImage = new BufferedImage(component.getWidth() * 2, component.getHeight() * 2,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.scale(2, 2);
        component.paint(g);
        for (Iterator<javax.imageio.ImageWriter> iw = ImageIO.getImageWritersByFormatName("png"); iw.hasNext(); ) {
            javax.imageio.ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(
                    BufferedImage.TYPE_INT_RGB);
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
}
