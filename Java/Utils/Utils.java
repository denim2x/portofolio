package utils;

/**
 *
 * @author Cristian
 */
public class Utils {

	private static final String MSG_NULL, MSG_NO, MSG_VOID;

	static {
		MSG_NULL = "Parameter %s cannot be null.";
		MSG_NO = "The number of names cannot be less than the number of objects.";
		MSG_VOID = "The names cannot be empty.";
	}

	/**
	 * This method traverses the specified list of objects and throws a
	 * NullPointerException at the first null object.
	 *
	 * @param names A space-delimited list of names in the same order as the
	 * corresponding objects.
	 * @param objects The list of objects to be validates.
	 */
	public static void validate(String names, Object... objects) {
		validate_("names objects", new Object[]{names, objects});
		validate_(names, objects);
	}

	public static void validate1(String name, Object object) {
		validate_("name object", new Object[]{name, object});
	}

	private static void validate_(String names, Object[] objects) {
		String[] names_ = names.split(" ");
		if (names_.length < objects.length) {
			throw new IllegalArgumentException(MSG_NO);
		}
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == null) {
				if (names_[i].isEmpty()) {
					throw new IllegalArgumentException(MSG_VOID);
				} else {
					throw new NullPointerException(String.format(MSG_NULL, names_[i]));
				}
			}
		}
	}

	private Utils() {
	}
}
