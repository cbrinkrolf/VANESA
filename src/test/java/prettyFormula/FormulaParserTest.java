package prettyFormula;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormulaParserTest {
    @Test
    void parseToLatex() {
        assertEquals("1", FormulaParser.parseToLatex("1"));
        assertEquals("\\left(\\frac{a}{b}\\right)+15", FormulaParser.parseToLatex("(a/b)+15"));
        assertEquals("\\left(-1\\right)\\cdot {z}_{1}", FormulaParser.parseToLatex("(-1) * z_1"));
    }
}