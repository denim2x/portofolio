package plot.model;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static utils.uMath.validate;

/**
 *
 * @author Cristian
 */
class Parser {

	private static class Token {

		Pattern pattern;
		byte next;

		Token(String pattern, int next) {
			this.pattern = Pattern.compile(pattern);
			this.next = new Integer(next).byteValue();
		}
	}

	/*
	 Constant fields; token flags: ENPVFS
	 */
	private static final byte SIGN = 1;          // S flag
	private static final byte NAT = 1 << 4;      // N flag
	private static final byte END = 1 << 5;      // E flag
	private static final byte FLOAT = 1 << 1;    // F flag
	private static final byte POWER = 1 << 3;    // P flag
	private static final byte VARIABLE = 1 << 2; // V flag
	private static final Map<Byte, Token> TOKENS;
	private static final Pattern SPACE = Pattern.compile("\\p{Blank}*");
	/*
	 Variable fields
	 */
	private static int start;
	private static double factor;
	private static boolean isFirst;
	private static Matcher matcher;
	private static String token, sign;
	private static byte degree, flags, mask;
	private static PolynomialFunction.Builder functionB;

	static {
		Map<Byte, Token> tokens = new TreeMap<>();
		tokens.put(NAT, new Token("[1-9]+", SIGN | END));
		tokens.put(SIGN, new Token("[-+]", FLOAT | VARIABLE));
		tokens.put(FLOAT, new Token("(\\d|\\.)+", VARIABLE | SIGN | END));
		tokens.put(POWER, new Token("\\^", NAT));
		tokens.put(VARIABLE, new Token("x", POWER | SIGN | END));
		TOKENS = tokens;
	}

	public static PolynomialFunction parse(String input) {
		if (input == null) {
			throw new NullPointerException("Input string cannot be null.");
		}

		input = input.trim();
		if (input.length() == 0) {
			throw new IllegalArgumentException("Input string must contain non-whitespace characters.");
		}

		flags = SIGN | FLOAT | VARIABLE;
		functionB = new PolynomialFunction.Builder();
		matcher = SPACE.matcher(input);
		start = 0;

		reset();
		do {
			for (mask = SIGN; mask < END; mask <<= 1) {
				if ((flags & mask) > 0) {
					setPattern();
					if (matcher.find(start)) {
						if (matcher.start() == start) {
							break;
						}
					}
				}
			}

			if (mask == END) {
				alert();
			}
			token = matcher.group();
			setFlags();
			if (matcher.hitEnd()) {
				if ((flags & END) == 0) {
					alert();
				}
			} else {
				isFirst = matcher.start() == 0;

				// Skip blank characters
				matcher.usePattern(SPACE);
				matcher.find();
				start = matcher.end();
			}

			switch (mask) {
			case SIGN:
				if (!isFirst) {
					addTerm();
					reset();
				}

				sign = token;
				break;
			case FLOAT:
				setFactor();
				break;
			case VARIABLE:
				degree = 1;
				break;
			case NAT:
				setDegree();
				break;
			}

		} while (!matcher.hitEnd());
		addTerm();

		return functionB.build();
	}

	private static void reset() {
		sign = "+";
		factor = 1;
		degree = 0;
	}

	private static void addTerm() {
		if (sign.equals("-") && factor == 1) {
			factor = -1;
		}
		functionB.addTerm(degree, factor);
	}

	private static void setFactor() {
		factor = Double.parseDouble(sign + token);
		validate(factor);
	}

	private static void setDegree() {
		degree = Byte.parseByte(token);
	}

	private static void setPattern() {
		matcher.usePattern(TOKENS.get(mask).pattern);
	}

	private static void setFlags() {
		flags = TOKENS.get(mask).next;
	}

	private static void alert() {
		throw new IllegalArgumentException("Input string has invalid syntax.");
	}
}