package io.image;

import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.Page;
import io.BaseWriter;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ChartImageWriter extends BaseWriter<JFreeChart> {
    public static final String IMAGE_TYPE_SVG = "svg";
    public static final String IMAGE_TYPE_PNG = "png";
    public static final String IMAGE_TYPE_PDF = "pdf";

    private final String imageType;
    private final int width;
    private final int height;

    public ChartImageWriter(File file, String imageType, int width, int height) {
        super(file);
        this.imageType = imageType;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void internalWrite(OutputStream outputStream, JFreeChart chart) throws Exception {
        if (IMAGE_TYPE_PNG.equals(imageType)) {
            exportPNG(outputStream, chart);
        } else if (IMAGE_TYPE_SVG.equals(imageType)) {
            exportSVG(outputStream, chart);
        } else if (IMAGE_TYPE_PDF.equals(imageType)) {
            exportPDF(outputStream, chart);
        }
    }

    private void exportPNG(OutputStream outputStream, JFreeChart chart) throws IOException {
        ChartUtils.writeChartAsPNG(outputStream, chart, width * 2, height * 2);
    }

    private void exportSVG(OutputStream outputStream, JFreeChart chart) throws SVGGraphics2DIOException {
        DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        svgGenerator.setSVGCanvasSize(new Dimension(width, height));
        chart.draw(svgGenerator, new Rectangle(width, height));
        svgGenerator.stream(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
    }

    private void exportPDF(OutputStream outputStream, JFreeChart chart) throws IOException {
        PDFDocument pdfDoc = new PDFDocument();
        pdfDoc.setAuthor("VANESA");
        Page page = pdfDoc.createPage(new Rectangle(width, height));
        PDFGraphics2D g2 = page.getGraphics2D();
        chart.draw(g2, new Rectangle(0, 0, width, height));
        outputStream.write(pdfDoc.getPDFBytes());
    }
}
