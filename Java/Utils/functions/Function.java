package utils.functions;

import javafx.geometry.Point2D;

/**
 *
 * @author Cristian
 */
public interface Function<TIn extends Number, TOut extends Number> {

	/**
	 * Computes the value of this function given the specified
	 * input.
	 */
	TOut compute(TIn input);

	/**
	 * Returns a new function that represents the derivative of
	 * this function.
	 */
	Function getDerivative();

	/**
	 * Returns a Bezier spline approximation of this function's
	 * graph within the rectangle defined by points p1 and p2.
	 *
	 * @param p1 The top-left point of the graph.
	 * @param p2 The bottom-right point of the graph.
	 * @return The Bezier approximation of this function's graph.
	 */
	FunctionGraph getGraph(Point2D p1, Point2D p2);

	/**
	 * Returns true if this function represents a horizontal line,
	 * and false otherwise. Note: It is recommended that this method have
	 * proper implementations within subclasses, especially if they are meant
	 * to be plotted (either by using getGraph() or the FunctionGraph class).
	 */
	boolean isHLine();
}
