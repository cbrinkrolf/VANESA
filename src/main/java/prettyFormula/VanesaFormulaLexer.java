// Generated from java-escape by ANTLR 4.11.1
package prettyFormula;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class VanesaFormulaLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INDEX=1, VARIABLE=2, NUMBER=3, WS=4, DIGIT=5, CHAR=6, PLUS=7, MINUS=8, 
		MULT=9, DIV=10, POW=11, LODASH=12, LPAREN=13, RPAREN=14, COMMA=15, DOT=16;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"INDEX", "VARIABLE", "NUMBER", "WS", "DIGIT", "CHAR", "PLUS", "MINUS", 
			"MULT", "DIV", "POW", "LODASH", "LPAREN", "RPAREN", "COMMA", "DOT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, "'+'", "'-'", "'*'", "'/'", 
			"'^'", "'_'", "'('", "')'", "','", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INDEX", "VARIABLE", "NUMBER", "WS", "DIGIT", "CHAR", "PLUS", "MINUS", 
			"MULT", "DIV", "POW", "LODASH", "LPAREN", "RPAREN", "COMMA", "DOT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public VanesaFormulaLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "VanesaFormula.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0010R\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000%\b"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0005\u0001*\b\u0001\n\u0001"+
		"\f\u0001-\t\u0001\u0001\u0002\u0004\u00020\b\u0002\u000b\u0002\f\u0002"+
		"1\u0001\u0003\u0004\u00035\b\u0003\u000b\u0003\f\u00036\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n"+
		"\u0001\n\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001"+
		"\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0000\u0000\u0010\u0001\u0001"+
		"\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f"+
		"\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f"+
		"\u001f\u0010\u0001\u0000\u0003\u0003\u0000\t\n\r\r  \u0001\u000009\u0002"+
		"\u0000AZazV\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000"+
		"\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000"+
		"\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000"+
		"\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000"+
		"\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000"+
		"\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000"+
		"\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000"+
		"\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000"+
		"\u0001!\u0001\u0000\u0000\u0000\u0003&\u0001\u0000\u0000\u0000\u0005/"+
		"\u0001\u0000\u0000\u0000\u00074\u0001\u0000\u0000\u0000\t:\u0001\u0000"+
		"\u0000\u0000\u000b<\u0001\u0000\u0000\u0000\r>\u0001\u0000\u0000\u0000"+
		"\u000f@\u0001\u0000\u0000\u0000\u0011B\u0001\u0000\u0000\u0000\u0013D"+
		"\u0001\u0000\u0000\u0000\u0015F\u0001\u0000\u0000\u0000\u0017H\u0001\u0000"+
		"\u0000\u0000\u0019J\u0001\u0000\u0000\u0000\u001bL\u0001\u0000\u0000\u0000"+
		"\u001dN\u0001\u0000\u0000\u0000\u001fP\u0001\u0000\u0000\u0000!$\u0003"+
		"\u0017\u000b\u0000\"%\u0003\u0003\u0001\u0000#%\u0003\u0005\u0002\u0000"+
		"$\"\u0001\u0000\u0000\u0000$#\u0001\u0000\u0000\u0000%\u0002\u0001\u0000"+
		"\u0000\u0000&+\u0003\u000b\u0005\u0000\'*\u0003\u000b\u0005\u0000(*\u0003"+
		"\t\u0004\u0000)\'\u0001\u0000\u0000\u0000)(\u0001\u0000\u0000\u0000*-"+
		"\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000\u0000+,\u0001\u0000\u0000"+
		"\u0000,\u0004\u0001\u0000\u0000\u0000-+\u0001\u0000\u0000\u0000.0\u0003"+
		"\t\u0004\u0000/.\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u00001/\u0001"+
		"\u0000\u0000\u000012\u0001\u0000\u0000\u00002\u0006\u0001\u0000\u0000"+
		"\u000035\u0007\u0000\u0000\u000043\u0001\u0000\u0000\u000056\u0001\u0000"+
		"\u0000\u000064\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u000078\u0001"+
		"\u0000\u0000\u000089\u0006\u0003\u0000\u00009\b\u0001\u0000\u0000\u0000"+
		":;\u0007\u0001\u0000\u0000;\n\u0001\u0000\u0000\u0000<=\u0007\u0002\u0000"+
		"\u0000=\f\u0001\u0000\u0000\u0000>?\u0005+\u0000\u0000?\u000e\u0001\u0000"+
		"\u0000\u0000@A\u0005-\u0000\u0000A\u0010\u0001\u0000\u0000\u0000BC\u0005"+
		"*\u0000\u0000C\u0012\u0001\u0000\u0000\u0000DE\u0005/\u0000\u0000E\u0014"+
		"\u0001\u0000\u0000\u0000FG\u0005^\u0000\u0000G\u0016\u0001\u0000\u0000"+
		"\u0000HI\u0005_\u0000\u0000I\u0018\u0001\u0000\u0000\u0000JK\u0005(\u0000"+
		"\u0000K\u001a\u0001\u0000\u0000\u0000LM\u0005)\u0000\u0000M\u001c\u0001"+
		"\u0000\u0000\u0000NO\u0005,\u0000\u0000O\u001e\u0001\u0000\u0000\u0000"+
		"PQ\u0005.\u0000\u0000Q \u0001\u0000\u0000\u0000\u0006\u0000$)+16\u0001"+
		"\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}