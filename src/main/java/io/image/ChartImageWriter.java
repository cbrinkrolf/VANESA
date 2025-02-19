package io.image;

import io.BaseWriter;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.internal.chartpart.Chart;

import java.io.*;

public class ChartImageWriter extends BaseWriter<Chart<?, ?>> {
	public static final String IMAGE_TYPE_SVG = "svg";
	public static final String IMAGE_TYPE_PNG = "png";
	public static final String IMAGE_TYPE_PDF = "pdf";

	private final String imageType;

	public ChartImageWriter(final File file, final String imageType) {
		super(file);
		this.imageType = imageType;
	}

	@Override
	protected void internalWrite(final OutputStream outputStream, final Chart<?, ?> chart) throws Exception {
		if (IMAGE_TYPE_PNG.equals(imageType)) {
			exportPNG(outputStream, chart);
		} else if (IMAGE_TYPE_SVG.equals(imageType)) {
			exportSVG(outputStream, chart);
		} else if (IMAGE_TYPE_PDF.equals(imageType)) {
			exportPDF(outputStream, chart);
		}
	}

	private void exportPNG(final OutputStream outputStream, final Chart<?, ?> chart) throws IOException {
		BitmapEncoder.saveBitmap(chart, outputStream, BitmapEncoder.BitmapFormat.PNG);
	}

	private void exportSVG(final OutputStream outputStream, final Chart<?, ?> chart) throws IOException {
		VectorGraphicsEncoder.saveVectorGraphic(chart, outputStream, VectorGraphicsEncoder.VectorGraphicsFormat.SVG);
	}

	private void exportPDF(final OutputStream outputStream, final Chart<?, ?> chart) throws IOException {
		VectorGraphicsEncoder.saveVectorGraphic(chart, outputStream, VectorGraphicsEncoder.VectorGraphicsFormat.PDF);
	}
}
