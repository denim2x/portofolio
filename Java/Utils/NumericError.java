package utils;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Cristian
 */
public enum NumericError {

	NEGATIVE_INFINITY,
	POSITIVE_INFINITY,
	NAN;
	
	private static Map<NumericError, String> strings;

	static {
		strings = new TreeMap<>();
		strings.put(NEGATIVE_INFINITY, "-Infinity");
		strings.put(POSITIVE_INFINITY, "+Infinity");
		strings.put(NAN, "NaN");
	}

	public static String toString(NumericError error) {
		return strings.get(error);
	}

	@Override
	public String toString() {
		return strings.get(this);
	}
}
