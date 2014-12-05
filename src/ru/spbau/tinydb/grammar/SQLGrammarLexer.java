// Generated from ../src/SQLGrammar.g4 by ANTLR 4.4

    package ru.spbau.tinydb.grammar;

    import java.util.*;
    import ru.spbau.tinydb.queries.*;
    import ru.spbau.tinydb.expressions.*;
    import ru.spbau.tinydb.expressions.bool.*;
    import ru.spbau.tinydb.expressions.comparison.*;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SQLGrammarLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		OR=1, AND=2, NOT=3, TRUE=4, FALSE=5, INTEGER_TYPE=6, DOUBLE_TYPE=7, VAR_CHAR_TYPE=8, 
		CREATE=9, UNIQUE=10, INDEX=11, USING=12, BTREE=13, HASH=14, ASC=15, DESC=16, 
		DELETE=17, INSERT=18, INTO=19, VALUES=20, UPDATE=21, SET=22, TABLE=23, 
		SELECT=24, FROM=25, WHERE=26, INNER=27, JOIN=28, ON=29, COMMA=30, ASTERISC=31, 
		LEFT_PARENTHESIS=32, RIGHT_PARENTHESIS=33, SEMICOLON=34, WHITE_SPACE=35, 
		LOWER_CASE=36, UPPER_CASE=37, UNDERLINE=38, DECIMAL_DIGIT=39, SIGN=40, 
		DECIMAL_POINT=41, ARROW=42, EQUAL=43, NOT_EQUAL=44, LESS=45, LESS_OR_EQUAL=46, 
		GREATER=47, GREATER_OR_EQUAL=48, QUOTES=49;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'", "'\n'", "'\\u000B'", "'\f'", 
		"'\r'", "'\\u000E'", "'\\u000F'", "'\\u0010'", "'\\u0011'", "'\\u0012'", 
		"'\\u0013'", "'\\u0014'", "'\\u0015'", "'\\u0016'", "'\\u0017'", "'\\u0018'", 
		"'\\u0019'", "'\\u001A'", "'\\u001B'", "'\\u001C'", "'\\u001D'", "'\\u001E'", 
		"'\\u001F'", "' '", "'!'", "'\"'", "'#'", "'$'", "'%'", "'&'", "'''", 
		"'('", "')'", "'*'", "'+'", "','", "'-'", "'.'", "'/'", "'0'", "'1'"
	};
	public static final String[] ruleNames = {
		"OR", "AND", "NOT", "TRUE", "FALSE", "INTEGER_TYPE", "DOUBLE_TYPE", "VAR_CHAR_TYPE", 
		"CREATE", "UNIQUE", "INDEX", "USING", "BTREE", "HASH", "ASC", "DESC", 
		"DELETE", "INSERT", "INTO", "VALUES", "UPDATE", "SET", "TABLE", "SELECT", 
		"FROM", "WHERE", "INNER", "JOIN", "ON", "COMMA", "ASTERISC", "LEFT_PARENTHESIS", 
		"RIGHT_PARENTHESIS", "SEMICOLON", "WHITE_SPACE", "LOWER_CASE", "UPPER_CASE", 
		"UNDERLINE", "DECIMAL_DIGIT", "SIGN", "DECIMAL_POINT", "ARROW", "EQUAL", 
		"NOT_EQUAL", "LESS", "LESS_OR_EQUAL", "GREATER", "GREATER_OR_EQUAL", "QUOTES"
	};


	public SQLGrammarLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SQLGrammar.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 34: WHITE_SPACE_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WHITE_SPACE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:  skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\63\u0138\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\3\2\3\2\3\2\3"+
		"\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3"+
		"\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3"+
		"\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3"+
		"\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3"+
		"\27\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3"+
		"\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3 \3 "+
		"\3!\3!\3\"\3\"\3#\3#\3$\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3"+
		"+\3+\3+\3,\3,\3-\3-\3-\3.\3.\3/\3/\3/\3\60\3\60\3\61\3\61\3\61\3\62\3"+
		"\62\2\2\63\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33"+
		"\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67"+
		"\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63\3\2\4\4"+
		"\2\13\f\"\"\4\2--//\u0137\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2"+
		"\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2"+
		"\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]"+
		"\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\3e\3\2\2\2\5h\3\2\2\2\7l\3\2"+
		"\2\2\tp\3\2\2\2\13u\3\2\2\2\r{\3\2\2\2\17\u0083\3\2\2\2\21\u008a\3\2\2"+
		"\2\23\u0092\3\2\2\2\25\u0099\3\2\2\2\27\u00a0\3\2\2\2\31\u00a6\3\2\2\2"+
		"\33\u00ac\3\2\2\2\35\u00b2\3\2\2\2\37\u00b7\3\2\2\2!\u00bb\3\2\2\2#\u00c0"+
		"\3\2\2\2%\u00c7\3\2\2\2\'\u00ce\3\2\2\2)\u00d3\3\2\2\2+\u00da\3\2\2\2"+
		"-\u00e1\3\2\2\2/\u00e5\3\2\2\2\61\u00eb\3\2\2\2\63\u00f2\3\2\2\2\65\u00f7"+
		"\3\2\2\2\67\u00fd\3\2\2\29\u0103\3\2\2\2;\u0108\3\2\2\2=\u010b\3\2\2\2"+
		"?\u010d\3\2\2\2A\u010f\3\2\2\2C\u0111\3\2\2\2E\u0113\3\2\2\2G\u0115\3"+
		"\2\2\2I\u0118\3\2\2\2K\u011a\3\2\2\2M\u011c\3\2\2\2O\u011e\3\2\2\2Q\u0120"+
		"\3\2\2\2S\u0122\3\2\2\2U\u0124\3\2\2\2W\u0127\3\2\2\2Y\u0129\3\2\2\2["+
		"\u012c\3\2\2\2]\u012e\3\2\2\2_\u0131\3\2\2\2a\u0133\3\2\2\2c\u0136\3\2"+
		"\2\2ef\7Q\2\2fg\7T\2\2g\4\3\2\2\2hi\7C\2\2ij\7P\2\2jk\7F\2\2k\6\3\2\2"+
		"\2lm\7P\2\2mn\7Q\2\2no\7V\2\2o\b\3\2\2\2pq\7V\2\2qr\7T\2\2rs\7W\2\2st"+
		"\7G\2\2t\n\3\2\2\2uv\7H\2\2vw\7C\2\2wx\7N\2\2xy\7U\2\2yz\7G\2\2z\f\3\2"+
		"\2\2{|\7K\2\2|}\7P\2\2}~\7V\2\2~\177\7G\2\2\177\u0080\7I\2\2\u0080\u0081"+
		"\7G\2\2\u0081\u0082\7T\2\2\u0082\16\3\2\2\2\u0083\u0084\7F\2\2\u0084\u0085"+
		"\7Q\2\2\u0085\u0086\7W\2\2\u0086\u0087\7D\2\2\u0087\u0088\7N\2\2\u0088"+
		"\u0089\7G\2\2\u0089\20\3\2\2\2\u008a\u008b\7X\2\2\u008b\u008c\7C\2\2\u008c"+
		"\u008d\7T\2\2\u008d\u008e\7E\2\2\u008e\u008f\7J\2\2\u008f\u0090\7C\2\2"+
		"\u0090\u0091\7T\2\2\u0091\22\3\2\2\2\u0092\u0093\7E\2\2\u0093\u0094\7"+
		"T\2\2\u0094\u0095\7G\2\2\u0095\u0096\7C\2\2\u0096\u0097\7V\2\2\u0097\u0098"+
		"\7G\2\2\u0098\24\3\2\2\2\u0099\u009a\7W\2\2\u009a\u009b\7P\2\2\u009b\u009c"+
		"\7K\2\2\u009c\u009d\7S\2\2\u009d\u009e\7W\2\2\u009e\u009f\7G\2\2\u009f"+
		"\26\3\2\2\2\u00a0\u00a1\7K\2\2\u00a1\u00a2\7P\2\2\u00a2\u00a3\7F\2\2\u00a3"+
		"\u00a4\7G\2\2\u00a4\u00a5\7Z\2\2\u00a5\30\3\2\2\2\u00a6\u00a7\7W\2\2\u00a7"+
		"\u00a8\7U\2\2\u00a8\u00a9\7K\2\2\u00a9\u00aa\7P\2\2\u00aa\u00ab\7I\2\2"+
		"\u00ab\32\3\2\2\2\u00ac\u00ad\7D\2\2\u00ad\u00ae\7V\2\2\u00ae\u00af\7"+
		"T\2\2\u00af\u00b0\7G\2\2\u00b0\u00b1\7G\2\2\u00b1\34\3\2\2\2\u00b2\u00b3"+
		"\7J\2\2\u00b3\u00b4\7C\2\2\u00b4\u00b5\7U\2\2\u00b5\u00b6\7J\2\2\u00b6"+
		"\36\3\2\2\2\u00b7\u00b8\7C\2\2\u00b8\u00b9\7U\2\2\u00b9\u00ba\7E\2\2\u00ba"+
		" \3\2\2\2\u00bb\u00bc\7F\2\2\u00bc\u00bd\7G\2\2\u00bd\u00be\7U\2\2\u00be"+
		"\u00bf\7E\2\2\u00bf\"\3\2\2\2\u00c0\u00c1\7F\2\2\u00c1\u00c2\7G\2\2\u00c2"+
		"\u00c3\7N\2\2\u00c3\u00c4\7G\2\2\u00c4\u00c5\7V\2\2\u00c5\u00c6\7G\2\2"+
		"\u00c6$\3\2\2\2\u00c7\u00c8\7K\2\2\u00c8\u00c9\7P\2\2\u00c9\u00ca\7U\2"+
		"\2\u00ca\u00cb\7G\2\2\u00cb\u00cc\7T\2\2\u00cc\u00cd\7V\2\2\u00cd&\3\2"+
		"\2\2\u00ce\u00cf\7K\2\2\u00cf\u00d0\7P\2\2\u00d0\u00d1\7V\2\2\u00d1\u00d2"+
		"\7Q\2\2\u00d2(\3\2\2\2\u00d3\u00d4\7X\2\2\u00d4\u00d5\7C\2\2\u00d5\u00d6"+
		"\7N\2\2\u00d6\u00d7\7W\2\2\u00d7\u00d8\7G\2\2\u00d8\u00d9\7U\2\2\u00d9"+
		"*\3\2\2\2\u00da\u00db\7W\2\2\u00db\u00dc\7R\2\2\u00dc\u00dd\7F\2\2\u00dd"+
		"\u00de\7C\2\2\u00de\u00df\7V\2\2\u00df\u00e0\7G\2\2\u00e0,\3\2\2\2\u00e1"+
		"\u00e2\7U\2\2\u00e2\u00e3\7G\2\2\u00e3\u00e4\7V\2\2\u00e4.\3\2\2\2\u00e5"+
		"\u00e6\7V\2\2\u00e6\u00e7\7C\2\2\u00e7\u00e8\7D\2\2\u00e8\u00e9\7N\2\2"+
		"\u00e9\u00ea\7G\2\2\u00ea\60\3\2\2\2\u00eb\u00ec\7U\2\2\u00ec\u00ed\7"+
		"G\2\2\u00ed\u00ee\7N\2\2\u00ee\u00ef\7G\2\2\u00ef\u00f0\7E\2\2\u00f0\u00f1"+
		"\7V\2\2\u00f1\62\3\2\2\2\u00f2\u00f3\7H\2\2\u00f3\u00f4\7T\2\2\u00f4\u00f5"+
		"\7Q\2\2\u00f5\u00f6\7O\2\2\u00f6\64\3\2\2\2\u00f7\u00f8\7Y\2\2\u00f8\u00f9"+
		"\7J\2\2\u00f9\u00fa\7G\2\2\u00fa\u00fb\7T\2\2\u00fb\u00fc\7G\2\2\u00fc"+
		"\66\3\2\2\2\u00fd\u00fe\7K\2\2\u00fe\u00ff\7P\2\2\u00ff\u0100\7P\2\2\u0100"+
		"\u0101\7G\2\2\u0101\u0102\7T\2\2\u01028\3\2\2\2\u0103\u0104\7L\2\2\u0104"+
		"\u0105\7Q\2\2\u0105\u0106\7K\2\2\u0106\u0107\7P\2\2\u0107:\3\2\2\2\u0108"+
		"\u0109\7Q\2\2\u0109\u010a\7P\2\2\u010a<\3\2\2\2\u010b\u010c\7.\2\2\u010c"+
		">\3\2\2\2\u010d\u010e\7,\2\2\u010e@\3\2\2\2\u010f\u0110\7*\2\2\u0110B"+
		"\3\2\2\2\u0111\u0112\7+\2\2\u0112D\3\2\2\2\u0113\u0114\7=\2\2\u0114F\3"+
		"\2\2\2\u0115\u0116\t\2\2\2\u0116\u0117\b$\2\2\u0117H\3\2\2\2\u0118\u0119"+
		"\4c|\2\u0119J\3\2\2\2\u011a\u011b\4C\\\2\u011bL\3\2\2\2\u011c\u011d\7"+
		"a\2\2\u011dN\3\2\2\2\u011e\u011f\4\62;\2\u011fP\3\2\2\2\u0120\u0121\t"+
		"\3\2\2\u0121R\3\2\2\2\u0122\u0123\7\60\2\2\u0123T\3\2\2\2\u0124\u0125"+
		"\7/\2\2\u0125\u0126\7@\2\2\u0126V\3\2\2\2\u0127\u0128\7?\2\2\u0128X\3"+
		"\2\2\2\u0129\u012a\7#\2\2\u012a\u012b\7?\2\2\u012bZ\3\2\2\2\u012c\u012d"+
		"\7>\2\2\u012d\\\3\2\2\2\u012e\u012f\7>\2\2\u012f\u0130\7?\2\2\u0130^\3"+
		"\2\2\2\u0131\u0132\7@\2\2\u0132`\3\2\2\2\u0133\u0134\7@\2\2\u0134\u0135"+
		"\7?\2\2\u0135b\3\2\2\2\u0136\u0137\7)\2\2\u0137d\3\2\2\2\3\2\3\3$\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}