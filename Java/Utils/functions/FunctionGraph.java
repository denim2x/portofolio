package utils.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Point2D;
import utils.uMath;
import utils.Utils;

/**
 * This class provides graphing functionality for arbitrary functions by
 * constructing Bezier approximations. Note: For optimal performance the class
 * of the input function should correctly implement the isHLine() method in
 * order to determine whether the given function is a horizontal straight line,
 * thus avoiding placing unnecessary points along the resulting graph and
 * reducing computation overhead.
 */
public class FunctionGraph implements Iterable<FunctionGraph.Bezier> {

	/**
	 * This class implements a cubic Bezier curve.
	 */
	public static class Bezier implements Iterable<Point2D> {

		public static class Builder {

			private Bezier bezier;
			Bezier result;

			public Builder() {
				bezier = new Bezier();
			}

			public Builder(Bezier bezier) {
				Utils.validate("bezier", bezier);
				bezier = new Bezier(bezier.isStop);
				Collections.copy(bezier.points, bezier.points);
			}

			public Bezier build() {
				result = new Bezier(bezier.isStop);
				Collections.copy(result.points, bezier.points);
				return result;
			}

			public void set(int iPoint, Point2D point) {
				Utils.validate("point", point);
				bezier.set(iPoint, point.getX(), point.getY());
				//bezier.points[p - 1] = value;
			}

			public void set(int iPoint, double x, double y) {
				uMath.validate(x, y);
				bezier.set(iPoint, x, y);
			}

			public void setIsStop(boolean isStop) {
				bezier.isStop = isStop;
			}
		}

		public static final int SIZE = 4;
		private boolean isStop;
		//private Point2D[] points = new Point2D[4];
		private List<Point2D> points = new ArrayList<>(SIZE);

		/**
		 * Returns the point having the given index.
		 *
		 * @param p The index of the point, starting from 1.
		 */
		public Point2D get(int p) {
			if (p < 1 || SIZE < p) {
				throw new IndexOutOfBoundsException("The index must reside between 1 and " + SIZE + ".");
			}
			return points.get(p - 1);
			/*
			 if (p < 1 || points.length < p) {
			 throw new IndexOutOfBoundsException("The index must reside between 1 and " + points.length + ".");
			 }
			 return points[p - 1];*/
		}

		/**
		 * Returns true if this Bezier object represents a curve followed by a
		 * discontinuity, and false otherwise.
		 */
		public boolean isStop() {
			return isStop;
		}

		@Override
		public Iterator iterator() {
			return points.iterator();
		}

		private void set(int p, double x, double y) {
			uMath.validate(x, y);
			points.set(p - 1, new Point2D(x, y));
		}

		private Bezier() {
			for (int i = 0; i < SIZE; i++) {
				points.add(null);
			}
		}

		private Bezier(boolean isStop) {
			this();
			this.isStop = isStop;
		}
	}

	private static final class Point {

		boolean isStop = false;
		double x, y, slope;

		Point() {
		}

		Point(double x, double y, double slope) {
			update(x, y, slope);
		}

		void update(double x, double y, double slope) {
			this.x = x;
			this.y = y;
			this.slope = slope;
		}
	}

	private final double step = 1.0E-5;
	private Point point;
	private Bezier bezier;
	private Point2D p1, p2;
	private List<Point> points;
	private List<Bezier> graph;
	private Bezier.Builder bezierB;
	private double x, y, slope, slope2;
	private Function function, derivative;

	public FunctionGraph(Function function) {
		Utils.validate("function", function);
		this.function = function;
		derivative = function.getDerivative();
		bezierB = new Bezier.Builder();
	}

	//public Iterable<Bezier> build(Point2D p1, Point2D p2) {
	public void update(Point2D p1, Point2D p2) {
		Utils.validate("p1 p2", p1, p2);
		if (!isEqual(this.p1, p1) || !isEqual(this.p2, p2)) {
			uMath.validate(p1, p2);
			if (p2.getX() - p1.getX() < step) {
				throw new IllegalArgumentException("Cannot have p1.x >= p2.x.");
			}
			if (p1.getY() - p2.getY() < step) {
				throw new IllegalArgumentException("Cannot have p1.y <= p2.y.");
			}
			this.p1 = p1;
			this.p2 = p2;

			graph = null;
			points = new ArrayList<>(2);

			/*
			 * Serach for the first point inside the graph area
			 */
			for (x = p1.getX(); x <= p2.getX(); step()) {
				computeF();
				if (isInside()) {
					break;
				}
			}

			/*
			 * If there is no point inside the graph, skip the rest
			 */
			if (!isInside()) {
				return;
			}

			/*
			 * Add the computed point to the list
			 */
			slope = computeD();
			addPoint();

			/*
			 * If the function is a horizontal line, then compute
			 * the other endpoint, add it to the graph and skip the rest
			 */
			if (function.isHLine()) {
				x = p2.getX();
				addPoint();
				return;
			}

			/*
			 * If the function is not a horizontal line,  
			 * then start searching for critical points
			 * (i.e. points where the slope is zero).
			 */
			point = new Point();
			for (step(); x <= p2.getX(); step()) {
				computeF();
				if (isInside()) {
					slope2 = computeD();
					if (point.isStop) {
						slope = slope2;
						addPoint();
						continue;
					} else {
						point.update(x, y, slope2);
						if (slope != 0 && slope * slope2 <= 0) {
							points.add(point);
							point = new Point();
							if (p2.getX() - x < step) {
								break;
							}
						}
					}
					slope = slope2;
				} else {
					addStop();
				}
			}
			addStop();
			/*
			 * Compute bezier curves
			 */
			if (points.size() >= 2) {
				graph = new ArrayList<>(points.size() - 1);
				for (int i = 0; i < points.size() - 1; i++) {
					addBezier(points.get(i), points.get(i + 1));
				}
			}
		}
	}

	public boolean isEmpty() {
		return graph == null;
	}

	@Override
	public Iterator<Bezier> iterator() {
		if (graph != null) {
			return graph.iterator();
		} else {
			return null;
		}
	}

	private void addPoint() {
		point = new Point(x, y, slope);
		points.add(point);
	}

	private void addStop() {
		if (!point.isStop) {
			point.isStop = true;
			points.add(point);
		}
	}

	private void addBezier(Point p1, Point p2) {
		/*
		 h = d - c
		 (c, f(c))
		 (c + h/3, f(c) + (h/3)f'(c))
		 (d - h/3, f(d) - (h/3)f'(d))
		 (d, f(d)) 
		 */
		if (p1.isStop) {
			return;
		}
		x = (p2.x - p1.x) / 3;
		bezierB.set(1, p1.x, p1.y);
		bezierB.set(2, p1.x + x, p1.y + x * p1.slope);
		bezierB.set(3, p2.x - x, p2.y - x * p2.slope);
		bezierB.set(4, p2.x, p2.y);
		bezierB.setIsStop(p2.isStop);
		bezier = bezierB.build();
		graph.add(bezier);
	}

	private void step() {
		x += step;
	}

	private void computeF() {
		y = function.compute(x).doubleValue();
	}

	private double computeD() {
		return computeD(x);
	}

	private double computeD(double input) {
		return derivative.compute(input).doubleValue();
	}

	private boolean isInside() {
		return p2.getY() <= y && y <= p1.getY();
	}

	private boolean isEqual(Point2D p1, Point2D p2) {
		if (p1 == null) {
			return false;
		} else {
			return p1.equals(p2);
		}
	}
}
