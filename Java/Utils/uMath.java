package utils;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;

/**
 *
 * @author Cristian
 */
public class uMath {

	private static class Validation {

		private Number number;
		private Point2D point;
		private static List<Class> types;

		static {
			types = new ArrayList<>(3);
			types.add(Double.class);
			types.add(Float.class);
			types.add(Point2D.class);
		}

		static void validate(Object[] objects) {
			Validation validation = new Validation(objects);
			for (Object arg : objects) {
				validation.validate(arg);
			}
		}

		static NumericError[] inspect(Object[] objects) {
			List<NumericError> errors = new ArrayList<>(objects.length);
			Validation validation = new Validation(objects);
			for (Object obj : objects) {
				try {
					validate(obj);
					errors.add(null);
				} catch (NumericException e) {
					errors.add(e.getError());
				}
			}
			return errors.toArray(new NumericError[errors.size()]);
		}

		private Validation(Object[] objects) {
			Utils.validate1("objects", objects);
			boolean pass;
			for (Object obj : objects) {
				if (obj == null) {
					throw new NullPointerException("Cannot validate null objects.");
				}
				pass = false;
				for (Class type : types) {
					if (type.isInstance(obj)) {
						pass = true;
						break;
					}
				}
				if (!pass) {
					throw new IllegalArgumentException("Cannot validate this type: " + obj.getClass().getName());
				}
			}
		}

		private void validate(Object obj) {
			if (obj instanceof Double || obj instanceof Float) {
				number = (Number) obj;
				validate(number.doubleValue());
			}
			if (obj instanceof Point2D) {
				point = (Point2D) obj;
				validate(point.getX());
				validate(point.getY());
			}
		}

		private static void validate(Double value) {
			if (value.compareTo(Double.NEGATIVE_INFINITY) == 0) {
				throw new NumericException("The specified value is negative infinity.", NumericError.NEGATIVE_INFINITY);
			}

			if (value.compareTo(Double.POSITIVE_INFINITY) == 0) {
				throw new NumericException("The specified value is positive infinity.", NumericError.POSITIVE_INFINITY);
			}

			if (Double.isNaN(value)) {
				throw new NumericException("The specified value is NaN.", NumericError.NAN);
			}
		}
	}

	private uMath() {
	}

	public static double log2(double d) {
		return uMath.logB(d, 2);
	}

	public static double logB(double d, double b) {
		return Math.log(d) / Math.log(b);
	}

	public static int floor(double d) {
		return (int) Math.floor(d);
	}

	public static int ceil(double d) {
		return (int) Math.ceil(d);
	}

	public static int rint(double d) {
		return (int) Math.rint(d);
	}

	/**
	 * This method traverses the specified list of objects and throws a
	 * NumericException at the first legal object which is either infinite of NaN
	 * (not a number). A legal object is an instance of any of the following
	 * classes: Float, Double and Point2D (i.e. javafx.geometry.Point2D).
	 *
	 * @param objects The list of objects to be validated.
	 */
	public static void validate(Object... objects) {
		Validation.validate(objects);
	}

	/**
	 * This method performs the same process as validate(), except any
	 * NumericException encountered is logged rather than thrown. This may be
	 * useful for analyzing a certain list of numbers.
	 *
	 * @param objects The list of objects to be inspected.
	 * @return The resulting array of NumericError instances, in the same order as
	 * their corresponding objects. Note: If an object is valid (i.e. is a legal
	 * object which passed validation), then its corresponding NumericError
	 * instance is null.
	 */
	public static NumericError[] inspect(Object... objects) {
		return Validation.inspect(objects);
	}

	/**
	 * This method truncates the given floating-point number to the specified
	 * number of digits.
	 *
	 * @param d The number to be truncated.
	 * @param digits The number of digits after the decimal point to be
	 * maintained. Note: If digits is zero, then the result is the same as
	 * Math.floor(d); if digits is negative, then a number of digits equal to
	 * Math.abs(digits) before the the decimal point become zero, along with all
	 * digits following the decimal point.
	 */
	public static double truncate(double d, int digits) {
		double t = Math.pow(10, digits);
		return Math.floor(d * t) / t;
	}
}