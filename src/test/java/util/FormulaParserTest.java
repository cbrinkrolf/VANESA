package util;

import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FormulaParserTest {
	@Test
	void parseToLatex() throws ParseException {
		assertEquals("1", FormulaToLatexConverter.parseToLatex("1", null));
		assertEquals("\\left(a+b\\right)\\cdot 15", FormulaToLatexConverter.parseToLatex("(a+b)*15", null));
		assertEquals("\\sqrt{15}", FormulaToLatexConverter.parseToLatex("sqrt(15)", null));
		assertEquals("\\max{\\left(15,1,a\\right)}", FormulaToLatexConverter.parseToLatex("max(15,1,a)", null));
		assertEquals("\\frac{a}{b}+15", FormulaToLatexConverter.parseToLatex("(a/b)+15", null));
		assertEquals("\\left(-1\\right)\\cdot {z}_{1}", FormulaToLatexConverter.parseToLatex("(-1) * z_1", null));
		assertEquals("{hsa}_{miR}_{516a}_{5p}", FormulaToLatexConverter.parseToLatex("hsa_miR_516a_5p", null));
		assertEquals("\\text{hsa_miR_516a_5p}",
				FormulaToLatexConverter.parseToLatex("hsa_miR_516a_5p", Set.of("hsa_miR_516a_5p")));
	}
}