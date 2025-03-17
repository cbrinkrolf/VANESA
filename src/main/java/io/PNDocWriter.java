package io;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.*;
import graph.VanesaGraph;
import graph.gui.Parameter;
import org.apache.batik.ext.awt.image.codec.png.PNGEncodeParam;
import org.apache.batik.ext.awt.image.codec.png.PNGImageEncoder;
import org.apache.commons.lang3.StringUtils;
import prettyFormula.FormulaParser;

public class PNDocWriter extends BaseWriter<Pathway> {
	public PNDocWriter(final File file) {
		super(file);
	}

	@Override
	protected void internalWrite(final OutputStream outputStream, final Pathway pw) throws Exception {
		// TODO: use image as base64 in html or remove
		final Dimension size = new Dimension(800, 600);
		final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_BYTE_INDEXED);
		final Graphics2D grp = image.createGraphics();
		pw.getGraphRenderer().render(grp, new Rectangle(0, 0, size.width, size.height));
		final PNGEncodeParam param = PNGEncodeParam.getDefaultEncodeParam(image);
		try (final OutputStream fos = new ByteArrayOutputStream()) {
			final PNGImageEncoder encoder = new PNGImageEncoder(fos, param);
			encoder.encode(image);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		final Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
		writer.write("<!doctype html>\n");
		writer.write("<html lang=\"en\">\n");
		writer.write("  <head>\n");
		writer.write("    <meta charset=\"utf-8\">\n");
		writer.write("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n");
		writer.write("    <title>VANESA documentation for " + pw.getName() + "</title>\n");
		writer.write(
				"    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH\" crossorigin=\"anonymous\">\n");
		writer.write("  </head>\n");
		writer.write("  <body>\n");
		writer.write("    <div class=\"container-fluid\">\n");
		writer.write("      <div class=\"text-center\">\n");
		writer.write("        <h1>VANESA documentation for " + pw.getName() + "</h1>\n");
		writer.write("        <p><strong>");
		writer.write(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(LocalDateTime.now()));
		writer.write("</strong></p>\n");
		writer.write("      </div>\n");
		writer.write(
				"      <div id=\"graphContainer\" style=\"width:100%;height:400px;border: 1px solid grey;\"></div>\n");
		writer.write("      <h2>Initial Values</h2>\n");
		writer.write("      <table class=\"table table-sm table-striped\">\n");
		writer.write("        <thead>\n");
		writer.write("          <tr><th>Name</th><th>Value</th><th>Unit</th></tr>\n");
		writer.write("        </thead>\n");
		writer.write("        <tbody>\n");
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodesSortedAlphabetically()) {
			if (bna instanceof Place && !bna.isLogical()) {
				final Place p = (Place) bna;
				writer.write("          <tr>");
				writer.write("<td>" + p.getName() + "</td>");
				writer.write(String.format(Locale.ROOT, "<td class=\"text-end\">%.3f</td>", p.getTokenStart()));
				// TODO: mmol?
				writer.write("<td>mmol" + (bna.isConstant() ? " (const.)" : "") + "</td>");
				writer.write("</tr>\n");
			}
		}
		writer.write("        </tbody>\n");
		writer.write("      </table>\n");
		writer.write("      <hr>\n");
		writer.write("      <h2>Equations</h2>\n");
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodesSortedAlphabetically()) {
			if (bna instanceof ContinuousTransition) {
				final ContinuousTransition t = (ContinuousTransition) bna;
				writer.write("      <h3>" + t.getName());
				if (t.isKnockedOut()) {
					writer.write(" (knocked out, \\(v=0\\))");
				}
				writer.write("</h3>\n");
				writer.write("      \"" + t.getName() + "\" : \\(");
				final Iterator<BiologicalEdgeAbstract> inEdgesIt = pw.getGraph2().getInEdges(t).iterator();
				while (inEdgesIt.hasNext()) {
					final BiologicalEdgeAbstract bea = inEdgesIt.next();
					String weight = "";
					if (bea instanceof PNArc) {
						final PNArc edge = (PNArc) bea;
						if (!edge.getFunction().equals("1")) {
							weight = '(' + edge.getFunction() + ")\\ ";
						}
					}
					BiologicalNodeAbstract from = bea.getFrom();
					if (from.isLogical()) {
						from = from.getLogicalReference();
					}
					writer.write(weight + from.getName());
					if (inEdgesIt.hasNext()) {
						writer.write("\\ +\\ ");
					}
				}
				writer.write("\\ \\rightarrow\\ ");
				final Iterator<BiologicalEdgeAbstract> outEdgesIt = pw.getGraph2().getOutEdges(t).iterator();
				while (outEdgesIt.hasNext()) {
					final BiologicalEdgeAbstract bea = outEdgesIt.next();
					String weight = "";
					if (bea instanceof PNArc) {
						PNArc edge = (PNArc) bea;
						if (!edge.getFunction().equals("1")) {
							weight = '(' + edge.getFunction() + ")\\ ";
						}
					}
					BiologicalNodeAbstract to = bea.getTo();
					if (to.isLogical()) {
						to = to.getLogicalReference();
					}
					writer.write(weight + to.getName());
					if (outEdgesIt.hasNext()) {
						writer.write("\\ +\\ ");
					}
				}
				writer.write("\\)\n");
				writer.write("      <hr>\n");
				writer.write("      \\(f = " + FormulaParser.parseToLatex(t.getMaximalSpeed()) + "\\)\n");
				final List<Parameter> parameters = t.getParameters();
				if (parameters.size() > 0) {
					writer.write("      <table class=\"table table-sm table-striped\">\n");
					writer.write("        <thead>\n");
					writer.write("          <tr><th>Name</th><th>Value</th><th>Unit</th></tr>\n");
					writer.write("        </thead>\n");
					writer.write("        <tbody>\n");
					for (Parameter p : parameters) {
						writer.write("          <tr>");
						writer.write("<td>" + p.getName() + "</td>");
						writer.write("<td class=\"text-end\">" + p.getValue() + "</td>");
						writer.write("<td>" + p.getUnit() + "</td>");
						writer.write("</tr>\n");
					}
					writer.write("        </tbody>\n");
					writer.write("      </table>\n");
				}
			}
		}
		writer.write("    </div>\n");
		writer.write("    <script src=\"https://cdn.jsdelivr.net/npm/echarts@5.6.0/dist/echarts.min.js\"></script>\n");
		writer.write(
				"    <script id=\"MathJax-script\" async src=\"https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js\"></script>\n\n");
		writer.write(
				"    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz\" crossorigin=\"anonymous\"></script>\n");
		writer.write("    <script>\n");
		writer.write("const container = document.getElementById('graphContainer');\n");
		writer.write("const chart = echarts.init(container);\n");
		writer.write("let symbolSize = 50;\n");
		writer.write("chart.setOption({\n");
		writer.write("  series: [\n");
		writer.write("    {\n");
		writer.write("      type: 'graph',\n");
		writer.write("      layout: 'none',\n");
		writer.write("      roam: true,\n");
		writer.write("      emphasis: { edgeLabel: { show: true, color: 'red', formatter: '{c}' } },\n");
		writer.write(
				"      label: { show: true, color: 'red', fontSize: 14, fontWeight: 'bold', textBorderColor: 'white', textBorderWidth: 2 },\n");
		writer.write("      edgeLabel: { show: false, color: 'red', fontSize: 14, fontWeight: 'bold', textBorderColor: 'white', textBorderWidth: 2 },\n");
		writer.write("      edgeSymbol: ['none', 'arrow'],\n");
		writer.write("      lineStyle: { width: 3 },\n");
		writer.write("      symbolSize: symbolSize,\n");
		writer.write("      symbolKeepAspect: true,\n");
		writer.write("      categories: [\n");
		writer.write("        {\n");
		writer.write("          name: 'DiscreteTransition',\n");
		writer.write(
				"          symbol: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAABACAIAAAD9B0KDAAABgmlDQ1BzUkdCIElFQzYxOTY2LTIuMQAAKJF1kc8rRFEUxz8ziJiJYmGhvDRkgfwosbEY+VVYzIzyazPzvDejZsbrvTdpslW2ihIbvxb8BWyVtVJEStZsiQ3Tc96MGsmc2z33c7/3nNO954I3klRTVnk3pNK2GRoLKrNz80rlCx6a8eGnPapaxlR4NEJJ+7iTaLGbTrdW6bh/rWZJs1TwVAkPqYZpC48LT67ahsvbwg1qIrokfCrcYcoFhW9dPVbgZ5fjBf5y2YyEhsFbJ6zEf3HsF6sJMyUsLyeQSmbUn/u4L/Fp6ZmwrC0ym7AIMUYQhQlGGKafHgbF99NJL12yo0R+dz5/mhXJVcUbZDFZJk4Cmw5RM1Jdk1UXXZORJOv2/29fLb2vt1DdF4SKJ8d5a4XKLchtOs7noePkjqDsES7SxfyVAxh4F32zqAX2oXYdzi6LWmwHzjeg8cGImtG8VCbTq+vwegL+Oai/huqFQs9+zjm+h8iafNUV7O5Bm8TXLn4DcCln6opTjJ8AAAAJcEhZcwAALiMAAC4jAXilP3YAAABbSURBVFiF7dghDoAwDEDRwaVqd6teb3anAs3EQDTBvO+aLH2pXWvSpmOZI6L3XrJ6jDHn3L3IzKuozFyWnyVHfAwGg8FgMBgMBoPBYDAYDAaDwWBv/fohLT26AbeoVusDEiBQAAAAAElFTkSuQmCC',\n");
		writer.write("        },\n");
		writer.write("        {\n");
		writer.write("          name: 'ContinuousTransition',\n");
		writer.write(
				"          symbol: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAABACAIAAAD9B0KDAAABgmlDQ1BzUkdCIElFQzYxOTY2LTIuMQAAKJF1kc8rRFEUxz8ziJiJYmGhvDRkgfwosbEY+VVYzIzyazPzvDejZsbrvTdpslW2ihIbvxb8BWyVtVJEStZsiQ3Tc96MGsmc2z33c7/3nNO954I3klRTVnk3pNK2GRoLKrNz80rlCx6a8eGnPapaxlR4NEJJ+7iTaLGbTrdW6bh/rWZJs1TwVAkPqYZpC48LT67ahsvbwg1qIrokfCrcYcoFhW9dPVbgZ5fjBf5y2YyEhsFbJ6zEf3HsF6sJMyUsLyeQSmbUn/u4L/Fp6ZmwrC0ym7AIMUYQhQlGGKafHgbF99NJL12yo0R+dz5/mhXJVcUbZDFZJk4Cmw5RM1Jdk1UXXZORJOv2/29fLb2vt1DdF4SKJ8d5a4XKLchtOs7noePkjqDsES7SxfyVAxh4F32zqAX2oXYdzi6LWmwHzjeg8cGImtG8VCbTq+vwegL+Oai/huqFQs9+zjm+h8iafNUV7O5Bm8TXLn4DcCln6opTjJ8AAAAJcEhZcwAALiMAAC4jAXilP3YAAADFSURBVFiF7dmxCcMwEAVQJ6Rwpc7gSlscGkADaAmN5QG0hAYw3kKVwJ0qd0lnQjA5YckJif+vhDjuIa47NQ1SIZf11Pe9Uqo6MI5jjPH11hhzPyDGmJW4Vn/Km3wUu23eDsMwTdPupkRkrc3FvPfOud1YSmkTO9vMnhNCaNuWLVuWRUpZinVdl4mxNf87M2DAgAEDBgwYMGDAgAEDBgzYD2D8HmSe51p7EB5j9zb5OdvMtNZCiN1NiYipwEK6IF/6REAK8gD6YdamzHAzKQAAAABJRU5ErkJggg==',\n");
		writer.write("        },\n");
		writer.write("        {\n");
		writer.write("          name: 'StochasticTransition',\n");
		writer.write(
				"          symbol: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAABACAIAAAD9B0KDAAABgmlDQ1BzUkdCIElFQzYxOTY2LTIuMQAAKJF1kc8rRFEUxz8ziJiJYmGhvDRkgfwosbEY+VVYzIzyazPzvDejZsbrvTdpslW2ihIbvxb8BWyVtVJEStZsiQ3Tc96MGsmc2z33c7/3nNO954I3klRTVnk3pNK2GRoLKrNz80rlCx6a8eGnPapaxlR4NEJJ+7iTaLGbTrdW6bh/rWZJs1TwVAkPqYZpC48LT67ahsvbwg1qIrokfCrcYcoFhW9dPVbgZ5fjBf5y2YyEhsFbJ6zEf3HsF6sJMyUsLyeQSmbUn/u4L/Fp6ZmwrC0ym7AIMUYQhQlGGKafHgbF99NJL12yo0R+dz5/mhXJVcUbZDFZJk4Cmw5RM1Jdk1UXXZORJOv2/29fLb2vt1DdF4SKJ8d5a4XKLchtOs7noePkjqDsES7SxfyVAxh4F32zqAX2oXYdzi6LWmwHzjeg8cGImtG8VCbTq+vwegL+Oai/huqFQs9+zjm+h8iafNUV7O5Bm8TXLn4DcCln6opTjJ8AAAAJcEhZcwAALiMAAC4jAXilP3YAAABeSURBVFiF7dghDsAgDEBRtiwhtdz/jFiCm10QbKKZet/RkD7fUqRNx/KutUZEyuoxxpzzObmWHxHRWkvBeu8Ldqbs/RgMBoPBYDAYDAaDwWAwGAwGg8He+vUgLW27AYYzEnPNN1qVAAAAAElFTkSuQmCC',\n");
		writer.write("        },\n");
		writer.write("        {\n");
		writer.write("          name: 'DiscretePlace',\n");
		writer.write(
				"          symbol: 'image://data:image/png;base64,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',\n");
		writer.write("        },\n");
		writer.write("        {\n");
		writer.write("          name: 'ContinuousPlace',\n");
		writer.write(
				"          symbol: 'image://data:image/png;base64,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',\n");
		writer.write("        },\n");
		writer.write("      ],\n");
		writer.write("      data: [\n");
		final VanesaGraph graph = pw.getGraph2();
		for (final BiologicalNodeAbstract bea : graph.getNodes()) {
			// TODO: logical places?
			final Point2D position = graph.getNodePosition(bea);
			writer.write("        {\n");
			writer.write("          name: '" + bea.getName() + "',\n");
			writer.write("          x: " + position.getX() + ",\n");
			writer.write("          y: '" + position.getY() + "',\n");
			if (bea instanceof DiscreteTransition) {
				writer.write("          category: 'DiscreteTransition',\n");
			} else if (bea instanceof ContinuousTransition) {
				writer.write("          category: 'ContinuousTransition',\n");
			} else if (bea instanceof StochasticTransition) {
				writer.write("          category: 'StochasticTransition',\n");
			} else if (bea instanceof DiscretePlace) {
				writer.write("          category: 'DiscretePlace',\n");
			} else if (bea instanceof ContinuousPlace) {
				writer.write("          category: 'ContinuousPlace',\n");
			}
			writer.write("        },\n");
		}
		writer.write("      ],\n");
		writer.write("      links: [\n");
		for (final BiologicalEdgeAbstract bea : pw.getAllEdges()) {
			writer.write("        { ");
			writer.write("source: '" + bea.getFrom().getName() + "', ");
			writer.write("target: '" + bea.getTo().getName() + "', ");
			writer.write("value: '");
			if (StringUtils.isNotEmpty(bea.getLabel()))
				writer.write(bea.getLabel());
			else if (StringUtils.isNotEmpty(bea.getName()))
				writer.write(bea.getName());
			writer.write("', ");
			writer.write("},\n");
		}
		writer.write("      ],\n");
		writer.write("    },\n");
		writer.write("  ],\n");
		writer.write("});\n");
		writer.write("chart.on('graphroam', function (params) {\n");
		writer.write("  if (params.zoom) {\n");
		writer.write("    symbolSize = symbolSize - (params.zoom - 1.0) * 10;\n");
		writer.write("    chart.setOption({ series: [{ symbolSize: Math.max(10, symbolSize) }] });\n");
		writer.write("  }\n");
		writer.write("});\n");
		writer.write("new ResizeObserver(() => chart.resize()).observe(container);\n");
		writer.write("    </script>\n");
		writer.write("  </body>\n");
		writer.write("</html>");
		writer.flush();
	}
}
