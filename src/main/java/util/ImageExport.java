package util;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * Export to figure formats for Components. Created by Ken on 12/1/2017.
 * https://github.com/wenbostar/PDV/blob/master/src/main/java/PDVGUI/utils/Export.java
 */
public class ImageExport {
    public static final String IMAGE_TYPE_SVG = "svg";
    public static final String IMAGE_TYPE_PNG = "png";

    public static void exportPic(Component component, Rectangle bounds, File exportFile, String imageType)
            throws IOException {
        if (IMAGE_TYPE_PNG.equals(imageType)) {
            exportPNG(component, bounds, exportFile);
        } else if (IMAGE_TYPE_SVG.equals(imageType)) {
            exportSVG(component, bounds, exportFile);
        }
    }

    private static void exportSVG(Component component, Rectangle bounds, File exportFile) throws IOException {
        DOMImplementation domImplementation = SVGDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        SVGDocument svgDocument = (SVGDocument) domImplementation.createDocument(svgNS, "svg", null);
        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(svgDocument);
        svgGraphics2D.setSVGCanvasSize(bounds.getSize());
        component.paintAll(svgGraphics2D);
        // removing clip paths
        SVGSVGElement root = (SVGSVGElement) svgGraphics2D.getRoot();
        Element defs = root.getElementById("defs1");
        NodeList childs = defs.getChildNodes();
        for (int i = childs.getLength() - 1; i >= 0; i--) {
            defs.removeChild(childs.item(i));
        }
        if (new File(exportFile.getAbsolutePath() + ".temp").exists()) {
            // new File(exportFile.getAbsolutePath() + ".temp").delete();
        }
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(exportFile));
             Writer out = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            // dump root instead of svgGraphics2D because of XML manipulation
            svgGraphics2D.stream(root, out, true, false);
        }
    }

    private static void exportPNG(Component component, Rectangle bounds, File exportFile) {
        BufferedImage bufferedImage = new BufferedImage(bounds.width * 2, bounds.height * 2, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.scale(2, 2);
        component.paint(g);
        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName("png"); iw.hasNext(); ) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }
            try (ImageOutputStream stream = ImageIO.createImageOutputStream(exportFile)) {
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
