package util;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.ParseException;
import com.ezylang.evalex.parser.Token;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import simulation.VanesaExpression;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class FormulaToLatexConverter {
	/**
	 * Parses a mathematical formula like "(a+b)/c" to a pretty image and saves it as an SVG file.
	 *
	 * @param formula A raw formula input String.
	 * @param file    The SVG file to save to.
	 * @throws ParseException When parsing the LaTeX formula failed.
	 * @throws IOException    When writing the file failed.
	 */
	public static void saveToSVG(final String formula, final File file, final Set<String> textVariables)
			throws ParseException, IOException {
		final String latexFormula = FormulaToLatexConverter.parseToLatex(formula, textVariables);
		final TeXIcon icon = FormulaToLatexConverter.getTeXIcon(latexFormula);
		final DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
		final Document document = domImpl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
		final SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
		final SVGGraphics2D g2 = new SVGGraphics2D(ctx, true);
		g2.setSVGCanvasSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
		icon.paintIcon(null, g2, 0, 0);
		try (final FileOutputStream svgs = new FileOutputStream(file)) {
			final Writer out = new OutputStreamWriter(svgs, StandardCharsets.UTF_8);
			g2.stream(out, false);
			svgs.flush();
		}
	}

	/**
	 * Parses a mathematical formula String like "(a+b)/c" to a pretty image.
	 *
	 * @param formula A raw formula input String.
	 * @return An image object containing the rendered formula.
	 * @throws ParseException When parsing the LaTeX formula failed.
	 */
	public static BufferedImage parseToImage(final String formula, final Set<String> textVariables)
			throws ParseException {
		final String latexFormula = FormulaToLatexConverter.parseToLatex(formula, textVariables);
		final TeXIcon icon = FormulaToLatexConverter.getTeXIcon(latexFormula);
		// now create an actual image of the rendered equation
		final BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2 = image.createGraphics();
		g2.setColor(Color.white);
		g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
		icon.paintIcon(null, g2, 0, 0);
		return image;
	}

	/**
	 * Renders a valid LaTeX math formula to an icon.
	 *
	 * @param latexText Valid LaTeX formula.
	 * @return Rendered Icon.
	 */
	private static TeXIcon getTeXIcon(final String latexText) {
		final TeXFormula latexFormula = new TeXFormula(latexText);
		// render the formula to an icon of the same size as the formula.
		final TeXIcon icon = latexFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
		// insert a border
		icon.setInsets(new Insets(5, 5, 5, 5));
		return icon;
	}

	/**
	 * Parses a mathematical formula String like "(a+b)/c" to valid math LaTeX.
	 *
	 * @param formula A raw formula input String.
	 * @return The formula parsed to a small subset of LaTeX.
	 * @throws ParseException When parsing the LaTeX formula failed.
	 */
	public static String parseToLatex(final String formula, final Set<String> textVariables) throws ParseException {
		final var expression = new VanesaExpression(formula);
		return visit(textVariables, expression.getAbstractSyntaxTree(), 0);
	}

	/**
	 * Parses a mathematical formula like "(a+b)/c" to valid math LaTeX.
	 *
	 * @param expression A formula wrapped inside an expression object.
	 * @return The formula parsed to a small subset of LaTeX.
	 * @throws ParseException When parsing the LaTeX formula failed.
	 */
	public static String parseToLatex(final Expression expression, final Set<String> textVariables)
			throws ParseException {
		return visit(textVariables, expression.getAbstractSyntaxTree(), 0);
	}

	/**
	 * Parses a mathematical formula like "(a+b)/c" to valid math LaTeX.
	 *
	 * @param rootNode      The root node of a formula AST.
	 * @param textVariables Variable names that should be converted to \text{} and ignore underscores.
	 * @return The formula parsed to a small subset of LaTeX.
	 */
	public static String parseToLatex(final ASTNode rootNode, final Set<String> textVariables) {
		return visit(textVariables, rootNode, 0);
	}

	private static String visit(final Set<String> textVariables, final ASTNode node) {
		return visit(textVariables, node, 0);
	}

	private static String visit(final Set<String> textVariables, final ASTNode node, final int depth) {
		final Token token = node.getToken();
		switch (token.getType()) {
		case BRACE_OPEN:
			return "\\left(";
		case BRACE_CLOSE:
			return "\\right)";
		case COMMA:
			return ",";
		case STRING_LITERAL:
			return "\"" + token.getValue() + "\"";
		case NUMBER_LITERAL:
			return token.getValue();
		case VARIABLE_OR_CONSTANT:
			final String variable = token.getValue();
			if ((textVariables != null && textVariables.contains(variable))) {
				return "\\text{" + variable + "}";
			}
			if (!variable.contains("_")) {
				return variable;
			}
			final StringBuilder variableLatex = new StringBuilder();
			int index = 0;
			int previousIndex = 0;
			while ((index = variable.indexOf('_', index)) != -1) {
				variableLatex.append("{").append(variable, previousIndex, index).append("}_");
				index++;
				previousIndex = index;
			}
			if (previousIndex < variable.length()) {
				variableLatex.append("{").append(variable.substring(previousIndex)).append("}");
			}
			return variableLatex.toString();
		case INFIX_OPERATOR:
			final var operand1 = visit(textVariables, node.getParameters().get(0), depth + 1);
			final var operand2 = visit(textVariables, node.getParameters().get(1), depth + 1);
			if ("/".equals(token.getValue())) {
				return String.format("\\frac{%s}{%s}", operand1, operand2);
			}
			if ("*".equals(token.getValue())) {
				return String.format("%s\\cdot %s", operand1, operand2);
			}
			if ("^".equals(token.getValue())) {
				return String.format("{%s}^{%s}", operand1, operand2);
			}
			if (depth > 0) {
				return "\\left(" + operand1 + token.getValue() + operand2 + "\\right)";
			}
			return operand1 + token.getValue() + operand2;
		case PREFIX_OPERATOR:
			if ("+".equals(token.getValue())) {
				return visit(textVariables, node.getParameters().get(0), depth + 1);
			}
			if ("-".equals(token.getValue())) {
				return "\\left(-" + visit(textVariables, node.getParameters().get(0), depth + 1) + "\\right)";
			}
			return token.getValue() + visit(textVariables, node.getParameters().get(0), depth + 1);
		case POSTFIX_OPERATOR:
			return visit(textVariables, node.getParameters().get(0), depth + 1) + token.getValue();
		case FUNCTION:
			if ("sqrt".equalsIgnoreCase(token.getValue())) {
				return "\\sqrt{" + visit(textVariables, node.getParameters().get(0), depth + 1) + "}";
			} else if ("sin".equalsIgnoreCase(token.getValue()) || "cos".equalsIgnoreCase(token.getValue())
					|| "tan".equalsIgnoreCase(token.getValue()) || "abs".equalsIgnoreCase(token.getValue())) {
				return "\\" + token.getValue().toLowerCase(Locale.ROOT) + "{\\left(" + visit(textVariables,
						node.getParameters().get(0), depth + 1) + "\\right)}";
			} else if ("min".equalsIgnoreCase(token.getValue()) || "max".equalsIgnoreCase(token.getValue())) {
				return "\\" + token.getValue().toLowerCase(Locale.ROOT) + "{\\left(" + node.getParameters().stream()
						.map(p -> visit(textVariables, p, depth + 1)).collect(Collectors.joining(",")) + "\\right)}";
			}
			return token.getValue().toLowerCase(Locale.ROOT) + "{\\left(" + node.getParameters().stream().map(
					p -> visit(textVariables, p, depth + 1)).collect(Collectors.joining(",")) + "\\right)}";
		case FUNCTION_PARAM_START:
			// TODO
			break;
		case ARRAY_OPEN:
			// TODO
			break;
		case ARRAY_CLOSE:
			// TODO
			break;
		case ARRAY_INDEX:
			// TODO
			break;
		case STRUCTURE_SEPARATOR:
			// TODO
			break;
		}
		return "";
	}
}
