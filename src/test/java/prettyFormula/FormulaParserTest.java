package prettyFormula;

import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.api.Test;
import util.FormulaParser;

import static org.junit.jupiter.api.Assertions.*;

class FormulaParserTest {
	@Test
	void parseToLatex() throws ParseException {
		assertEquals("1", FormulaParser.parseToLatex("1"));
		assertEquals("\\left(a+b\\right)\\cdot 15", FormulaParser.parseToLatex("(a+b)*15"));
		assertEquals("\\sqrt{15}", FormulaParser.parseToLatex("sqrt(15)"));
		assertEquals("\\max{\\left(15,1,a\\right)}", FormulaParser.parseToLatex("max(15,1,a)"));
		assertEquals("\\frac{a}{b}+15", FormulaParser.parseToLatex("(a/b)+15"));
		assertEquals("\\left(-1\\right)\\cdot {z}_{1}", FormulaParser.parseToLatex("(-1) * z_1"));
		assertEquals("{hsa}_{miR}_{516a}_{5p}", FormulaParser.parseToLatex("hsa_miR_516a_5p"));
	}
}