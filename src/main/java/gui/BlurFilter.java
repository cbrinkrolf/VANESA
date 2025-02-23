package gui;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

public class BlurFilter implements BufferedImageOp {
	private static final float[] blurMatrix = { 1 / 14f, 2 / 14f, 1 / 14f, 2 / 14f, 2 / 14f, 2 / 14f, 1 / 14f, 2 / 14f,
			1 / 14f };

	@Override
	public BufferedImage filter(final BufferedImage src, BufferedImage dest) {
		if (dest == null)
			dest = createCompatibleDestImage(src, null);
		int width = src.getWidth();
		int height = src.getHeight();
		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		// Get input pixels
		if (src.getType() == BufferedImage.TYPE_INT_ARGB || src.getType() == BufferedImage.TYPE_INT_RGB) {
			src.getRaster().getDataElements(0, 0, width, height, inPixels);
		} else {
			src.getRGB(0, 0, width, height, inPixels, 0, width);
		}
		convolve(inPixels, outPixels, width, height);
		// Set output pixels
		if (dest.getType() == BufferedImage.TYPE_INT_ARGB || dest.getType() == BufferedImage.TYPE_INT_RGB) {
			dest.getRaster().setDataElements(0, 0, width, height, outPixels);
		} else {
			dest.setRGB(0, 0, width, height, outPixels, 0, width);
		}
		return dest;
	}

	private void convolve(final int[] inPixels, final int[] outPixels, final int width, final int height) {
		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				float r = 0;
				float g = 0;
				float b = 0;
				for (int row = -1; row <= 1; row++) {
					// Clamp edges
					final int iy = Math.max(0, Math.min(height - 1, y + row)) * width;
					final int matrixOffset = 3 * (row + 1) + 1;
					for (int col = -1; col <= 1; col++) {
						// Clamp edges
						final int ix = Math.max(0, Math.min(width - 1, x + col));
						final float f = blurMatrix[matrixOffset + col];
						final int rgb = inPixels[iy + ix];
						r += f * ((rgb >> 16) & 0xff);
						g += f * ((rgb >> 8) & 0xff);
						b += f * (rgb & 0xff);
					}
				}
				final int ir = Math.max(0, Math.min(255, (int) (r + 0.5)));
				final int ig = Math.max(0, Math.min(255, (int) (g + 0.5)));
				final int ib = Math.max(0, Math.min(255, (int) (b + 0.5)));
				outPixels[index++] = (255 << 24) | (ir << 16) | (ig << 8) | ib;
			}
		}
	}

	@Override
	public Rectangle2D getBounds2D(final BufferedImage src) {
		return new Rectangle(0, 0, src.getWidth(), src.getHeight());
	}

	@Override
	public BufferedImage createCompatibleDestImage(final BufferedImage src, ColorModel destCM) {
		if (destCM == null) {
			destCM = src.getColorModel();
		}
		return new BufferedImage(destCM, destCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()),
				destCM.isAlphaPremultiplied(), null);
	}

	@Override
	public Point2D getPoint2D(final Point2D srcPt, final Point2D dstPt) {
		if (dstPt == null) {
			return new Point2D.Double(srcPt.getX(), srcPt.getY());
		}
		dstPt.setLocation(srcPt.getX(), srcPt.getY());
		return dstPt;
	}

	@Override
	public RenderingHints getRenderingHints() {
		return null;
	}
}
