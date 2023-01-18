// Generated from java-escape by ANTLR 4.11.1
package prettyFormula;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class VanesaFormulaParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INDEX=1, VARIABLE=2, NUMBER=3, WS=4, DIGIT=5, CHAR=6, PLUS=7, MINUS=8, 
		MULT=9, DIV=10, POW=11, LODASH=12, LPAREN=13, RPAREN=14, COMMA=15, DOT=16;
	public static final int
		RULE_expr = 0, RULE_term = 1, RULE_atom = 2, RULE_number = 3, RULE_neg_number = 4, 
		RULE_variable = 5, RULE_neg_variable = 6, RULE_function = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"expr", "term", "atom", "number", "neg_number", "variable", "neg_variable", 
			"function"
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

	@Override
	public String getGrammarFileName() { return "java-escape"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public VanesaFormulaParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprContext extends ParserRuleContext {
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VanesaFormulaVisitor ) return ((VanesaFormulaVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16);
			term(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TermContext extends ParserRuleContext {
		public Token operator;
		public TerminalNode LPAREN() { return getToken(VanesaFormulaParser.LPAREN, 0); }
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(VanesaFormulaParser.RPAREN, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(VanesaFormulaParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(VanesaFormulaParser.COMMA, i);
		}
		public TerminalNode POW() { return getToken(VanesaFormulaParser.POW, 0); }
		public TerminalNode DIV() { return getToken(VanesaFormulaParser.DIV, 0); }
		public TerminalNode MULT() { return getToken(VanesaFormulaParser.MULT, 0); }
		public TerminalNode PLUS() { return getToken(VanesaFormulaParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(VanesaFormulaParser.MINUS, 0); }
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VanesaFormulaVisitor ) return ((VanesaFormulaVisitor<? extends T>)visitor).visitTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		return term(0);
	}

	private TermContext term(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TermContext _localctx = new TermContext(_ctx, _parentState);
		TermContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_term, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(19);
				match(LPAREN);
				setState(20);
				term(0);
				setState(21);
				match(RPAREN);
				}
				break;
			case 2:
				{
				setState(23);
				atom();
				}
				break;
			case 3:
				{
				setState(24);
				function();
				setState(25);
				match(LPAREN);
				setState(26);
				term(0);
				setState(31);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(27);
					match(COMMA);
					setState(28);
					term(0);
					}
					}
					setState(33);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(34);
				match(RPAREN);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(52);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(50);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
					case 1:
						{
						_localctx = new TermContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_term);
						setState(38);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(39);
						((TermContext)_localctx).operator = match(POW);
						setState(40);
						term(4);
						}
						break;
					case 2:
						{
						_localctx = new TermContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_term);
						setState(41);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(42);
						((TermContext)_localctx).operator = match(DIV);
						setState(43);
						term(4);
						}
						break;
					case 3:
						{
						_localctx = new TermContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_term);
						setState(44);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(45);
						((TermContext)_localctx).operator = match(MULT);
						setState(46);
						term(3);
						}
						break;
					case 4:
						{
						_localctx = new TermContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_term);
						setState(47);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(48);
						((TermContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((TermContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(49);
						term(2);
						}
						break;
					}
					} 
				}
				setState(54);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AtomContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public Neg_numberContext neg_number() {
			return getRuleContext(Neg_numberContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public Neg_variableContext neg_variable() {
			return getRuleContext(Neg_variableContext.class,0);
		}
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VanesaFormulaVisitor ) return ((VanesaFormulaVisitor<? extends T>)visitor).visitAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_atom);
		try {
			setState(59);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(55);
				number();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(56);
				neg_number();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(57);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(58);
				neg_variable();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NumberContext extends ParserRuleContext {
		public List<TerminalNode> NUMBER() { return getTokens(VanesaFormulaParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(VanesaFormulaParser.NUMBER, i);
		}
		public TerminalNode DOT() { return getToken(VanesaFormulaParser.DOT, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VanesaFormulaVisitor ) return ((VanesaFormulaVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			match(NUMBER);
			setState(64);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(62);
				match(DOT);
				setState(63);
				match(NUMBER);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Neg_numberContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(VanesaFormulaParser.LPAREN, 0); }
		public TerminalNode MINUS() { return getToken(VanesaFormulaParser.MINUS, 0); }
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(VanesaFormulaParser.RPAREN, 0); }
		public Neg_numberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_neg_number; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VanesaFormulaVisitor ) return ((VanesaFormulaVisitor<? extends T>)visitor).visitNeg_number(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Neg_numberContext neg_number() throws RecognitionException {
		Neg_numberContext _localctx = new Neg_numberContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_neg_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			match(LPAREN);
			setState(67);
			match(MINUS);
			setState(68);
			number();
			setState(69);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableContext extends ParserRuleContext {
		public TerminalNode VARIABLE() { return getToken(VanesaFormulaParser.VARIABLE, 0); }
		public List<TerminalNode> INDEX() { return getTokens(VanesaFormulaParser.INDEX); }
		public TerminalNode INDEX(int i) {
			return getToken(VanesaFormulaParser.INDEX, i);
		}
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VanesaFormulaVisitor ) return ((VanesaFormulaVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_variable);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			match(VARIABLE);
			setState(75);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(72);
					match(INDEX);
					}
					} 
				}
				setState(77);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Neg_variableContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(VanesaFormulaParser.LPAREN, 0); }
		public TerminalNode MINUS() { return getToken(VanesaFormulaParser.MINUS, 0); }
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(VanesaFormulaParser.RPAREN, 0); }
		public Neg_variableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_neg_variable; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VanesaFormulaVisitor ) return ((VanesaFormulaVisitor<? extends T>)visitor).visitNeg_variable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Neg_variableContext neg_variable() throws RecognitionException {
		Neg_variableContext _localctx = new Neg_variableContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_neg_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			match(LPAREN);
			setState(79);
			match(MINUS);
			setState(80);
			variable();
			setState(81);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionContext extends ParserRuleContext {
		public TerminalNode VARIABLE() { return getToken(VanesaFormulaParser.VARIABLE, 0); }
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VanesaFormulaVisitor ) return ((VanesaFormulaVisitor<? extends T>)visitor).visitFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			match(VARIABLE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return term_sempred((TermContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean term_sempred(TermContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 4);
		case 1:
			return precpred(_ctx, 3);
		case 2:
			return precpred(_ctx, 2);
		case 3:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0010V\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0005\u0001\u001e\b\u0001\n\u0001\f\u0001!\t\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001%\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0005\u00013\b\u0001\n\u0001\f\u0001"+
		"6\t\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002"+
		"<\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003A\b\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001"+
		"\u0005\u0005\u0005J\b\u0005\n\u0005\f\u0005M\t\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0000\u0001\u0002\b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0000\u0001"+
		"\u0001\u0000\u0007\bY\u0000\u0010\u0001\u0000\u0000\u0000\u0002$\u0001"+
		"\u0000\u0000\u0000\u0004;\u0001\u0000\u0000\u0000\u0006=\u0001\u0000\u0000"+
		"\u0000\bB\u0001\u0000\u0000\u0000\nG\u0001\u0000\u0000\u0000\fN\u0001"+
		"\u0000\u0000\u0000\u000eS\u0001\u0000\u0000\u0000\u0010\u0011\u0003\u0002"+
		"\u0001\u0000\u0011\u0001\u0001\u0000\u0000\u0000\u0012\u0013\u0006\u0001"+
		"\uffff\uffff\u0000\u0013\u0014\u0005\r\u0000\u0000\u0014\u0015\u0003\u0002"+
		"\u0001\u0000\u0015\u0016\u0005\u000e\u0000\u0000\u0016%\u0001\u0000\u0000"+
		"\u0000\u0017%\u0003\u0004\u0002\u0000\u0018\u0019\u0003\u000e\u0007\u0000"+
		"\u0019\u001a\u0005\r\u0000\u0000\u001a\u001f\u0003\u0002\u0001\u0000\u001b"+
		"\u001c\u0005\u000f\u0000\u0000\u001c\u001e\u0003\u0002\u0001\u0000\u001d"+
		"\u001b\u0001\u0000\u0000\u0000\u001e!\u0001\u0000\u0000\u0000\u001f\u001d"+
		"\u0001\u0000\u0000\u0000\u001f \u0001\u0000\u0000\u0000 \"\u0001\u0000"+
		"\u0000\u0000!\u001f\u0001\u0000\u0000\u0000\"#\u0005\u000e\u0000\u0000"+
		"#%\u0001\u0000\u0000\u0000$\u0012\u0001\u0000\u0000\u0000$\u0017\u0001"+
		"\u0000\u0000\u0000$\u0018\u0001\u0000\u0000\u0000%4\u0001\u0000\u0000"+
		"\u0000&\'\n\u0004\u0000\u0000\'(\u0005\u000b\u0000\u0000(3\u0003\u0002"+
		"\u0001\u0004)*\n\u0003\u0000\u0000*+\u0005\n\u0000\u0000+3\u0003\u0002"+
		"\u0001\u0004,-\n\u0002\u0000\u0000-.\u0005\t\u0000\u0000.3\u0003\u0002"+
		"\u0001\u0003/0\n\u0001\u0000\u000001\u0007\u0000\u0000\u000013\u0003\u0002"+
		"\u0001\u00022&\u0001\u0000\u0000\u00002)\u0001\u0000\u0000\u00002,\u0001"+
		"\u0000\u0000\u00002/\u0001\u0000\u0000\u000036\u0001\u0000\u0000\u0000"+
		"42\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u00005\u0003\u0001\u0000"+
		"\u0000\u000064\u0001\u0000\u0000\u00007<\u0003\u0006\u0003\u00008<\u0003"+
		"\b\u0004\u00009<\u0003\n\u0005\u0000:<\u0003\f\u0006\u0000;7\u0001\u0000"+
		"\u0000\u0000;8\u0001\u0000\u0000\u0000;9\u0001\u0000\u0000\u0000;:\u0001"+
		"\u0000\u0000\u0000<\u0005\u0001\u0000\u0000\u0000=@\u0005\u0003\u0000"+
		"\u0000>?\u0005\u0010\u0000\u0000?A\u0005\u0003\u0000\u0000@>\u0001\u0000"+
		"\u0000\u0000@A\u0001\u0000\u0000\u0000A\u0007\u0001\u0000\u0000\u0000"+
		"BC\u0005\r\u0000\u0000CD\u0005\b\u0000\u0000DE\u0003\u0006\u0003\u0000"+
		"EF\u0005\u000e\u0000\u0000F\t\u0001\u0000\u0000\u0000GK\u0005\u0002\u0000"+
		"\u0000HJ\u0005\u0001\u0000\u0000IH\u0001\u0000\u0000\u0000JM\u0001\u0000"+
		"\u0000\u0000KI\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000L\u000b"+
		"\u0001\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000NO\u0005\r\u0000\u0000"+
		"OP\u0005\b\u0000\u0000PQ\u0003\n\u0005\u0000QR\u0005\u000e\u0000\u0000"+
		"R\r\u0001\u0000\u0000\u0000ST\u0005\u0002\u0000\u0000T\u000f\u0001\u0000"+
		"\u0000\u0000\u0007\u001f$24;@K";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}