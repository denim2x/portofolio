package plot;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Point2DBuilder;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import static plot.Main.lookup;
import utils.functions.FunctionGraph;
import utils.uMath;

/**
 *
 * @author Cristian
 */
public class Graph {

	private static class Stroke {

		Paint paint;
		double width;

		Stroke(Paint paint, double width) {
			this.paint = paint;
			this.width = width;
		}
	}

	private static final double tick = 8;    // tick size
	private static final double tickS = 2;   // space between tick and label
	private static final double minU = 10;   // min unit size
	private static final double maxU = 50;   // max unit size
	private static final Color mainC = Color.hsb(0, 0, 0.47); //Color for main path & labels
	private static Label label;
	private static Point2D point;
	private static boolean isStop;
	private static Point2D[] points;
	private static Dimension2D halfD;    // half dimensions
	private static Dimension2D fullD;    // full dimensions
	private static LabelBuilder labelB;
	private static PathElement[] origin;
	private static Point2DBuilder pointB;
	private static FunctionGraph functionG;
	private static double zoom, unit, unitV;
	private static boolean initialized = false;
	private static FunctionGraph.Bezier bezier;
	private static ObservableList<Node> overlay;
	private static FunctionGraph.Bezier.Builder bezierB;
	private static List<ObservableList<PathElement>> paths;
	private static ObservableList<PathElement> main, grid, graph;

	static void init() {
		if (initialized) {
			throw new IllegalStateException("The graph is already initialized.");
		}
		initialized = true;
		String[] ids = new String[]{"main grid graph"};
		Path path;
		List<ObservableList<PathElement>> list = new ArrayList<>(3);
		Stroke[] strokes = new Stroke[3];
		ids = ids[0].split(" ");
		strokes[0] = new Stroke(mainC, 2);                   // main
		strokes[1] = new Stroke(Color.hsb(0, 0, 0.75), 1);   // grid
		strokes[2] = new Stroke(Color.BLACK, 1);			 // graph

		for (int i = 0; i < ids.length; i++) {
			path = (Path) lookup(ids[i]);
			path.setStroke(strokes[i].paint);
			path.setStrokeWidth(strokes[i].width);
			list.add(path.getElements());
		}
		main = list.get(0);
		grid = list.get(1);
		graph = list.get(2);

		Pane pane = (Pane) lookup("canvas");
		fullD = new Dimension2D(pane.getWidth(), pane.getHeight());
		halfD = new Dimension2D(pane.getWidth() / 2, pane.getHeight() / 2);

		pane = (Pane) lookup("overlay");
		overlay = pane.getChildren();
		pane.setPrefSize(fullD.getWidth(), fullD.getHeight());

		labelB = LabelBuilder.create();

		// Draw coordinate system
		// Draw X-axis
		main.add(new MoveTo(0, halfD.getHeight()));
		main.add(new HLineTo(fullD.getWidth()));

		// Draw Y-axis
		main.add(new MoveTo(halfD.getWidth(), 0));
		main.add(new VLineTo(fullD.getHeight()));

		// Assign main to the ticks, as the main lines remain unchanged
		path = (Path) lookup("ticks");
		path.setStroke(strokes[0].paint);
		path.setStrokeWidth(strokes[0].width);
		main = path.getElements();

		paths = new ArrayList<>(3);
		paths.add(main);
		paths.add(grid);
		//paths.add(graph);
		origin = new PathElement[]{new MoveTo(0, 0), new HLineTo(0)};

		pointB = Point2DBuilder.create();
		bezierB = new FunctionGraph.Bezier.Builder();

		zoom = 0;
		drawGraph();
	}

	static void drawGraph() {
		double zoom = Graph.zoom;
		if (zoom != Main.getZoom()) {
			drawCanvas();
		}
		resetPath(graph);
		if (Main.getFunction() == null) {
			return;
		} else {
			if (zoom != Graph.zoom || points == null) {
				points = new Point2D[2];
				pointB.x(-halfD.getWidth() * unitV / unit);
				pointB.y(halfD.getHeight() * unitV / unit);
				points[0] = pointB.build();
				points[1] = new Point2D(-points[0].getX(), -points[0].getY());
			}
			functionG = Main.getFunction().getGraph(points[0], points[1]);
			if (functionG.isEmpty()) {
				return;
			}
		}
		//points[0] = new Point2D(-halfD.getWidth() / unit, halfD.getHeight() / unit);
		/*
		 bezier = functionG.iterator().next().scale(unit / unitV);
		 graph.add(MoveTo(bezier.get(1)));*/
		isStop = true;
		for (FunctionGraph.Bezier item : functionG) {
			for (int i = 1; i <= FunctionGraph.Bezier.SIZE; i++) {
				point = item.get(i);
				pointB.x(halfD.getWidth() + point.getX() * unit / unitV);
				pointB.y(halfD.getHeight() - point.getY() * unit / unitV);
				bezierB.set(i, pointB.build());
			}
			bezier = bezierB.build();
			//bezier = item.scale(unit / temp);
			if (isStop) {
				graph.add(MoveTo(bezier.get(1)));
			}
			graph.add(CubicCurveTo(bezier.get(2), bezier.get(3), bezier.get(4)));
			isStop = item.isStop();
		}

	}

	private static void drawCanvas() {
		//zoom = getZoom() + 0.5;
		zoom = Main.getZoom();
		double zoom = Graph.zoom + 0.5;

		// Clear previous canvas
		for (ObservableList<PathElement> path : paths) {
			resetPath(path);
		}

		// Compute unit size
		unit = halfD.getHeight() / zoom;
		unitV = 0;
		int sign = 1;
		if (unit < minU) {
			unitV = uMath.log2(minU / unit);
		}
		if (unit > maxU) {
			unitV = uMath.log2(unit / maxU);
			sign = -1;
		}
		unitV = sign * Math.ceil(unitV);
		unitV = Math.pow(2, unitV);
		unit *= unitV;

		// Prepare label formatting
		double x;
		double y;
		double val;
		String text, format;

		if (unitV >= 1) {
			format = "%.0f";
		} else {
			format = "%.1f";
		}

		final double gridS = 0.5; // grid line shift

		labelB.font(Font.font("Tw Cen MT", FontWeight.BOLD, 14));
		labelB.textFill(mainC);
		labelB.textAlignment(TextAlignment.CENTER);
		labelB.alignment(Pos.TOP_CENTER);

		// Add label for origin point
		y = halfD.getHeight() - tick / 2;
		addLabel("0", halfD.getWidth() + tick + tickS, y + tick + tickS);

		// Draw x-axis lines
		double[] limits = new double[]{fullD.getWidth(), 0};
		double[] shifts = new double[]{3, 7.5};   // x-offset workaround
		sign = 1;
		for (int i = 0; i < limits.length; i++) {
			x = halfD.getWidth() + unit;
			val = 0;
			do {
				// draw tick
				main.add(new MoveTo(x, y));
				main.add(new VLineTo(y + tick));

				// add label
				val += unitV;
				text = String.format(format, val);
				addLabel(text, x - shifts[i], y + tick + tickS);

				// draw grid line
				grid.add(new MoveTo(x + gridS, 0));
				grid.add(new VLineTo(fullD.getHeight()));

				x += unit;
			} while (sign * x < sign * limits[i]);
			unit *= -1;
			unitV *= -1;
			sign *= -1;
		}

		labelB.textAlignment(TextAlignment.LEFT);
		labelB.alignment(Pos.CENTER_LEFT);

		// Draw y-axis lines
		limits = new double[]{0, fullD.getHeight()};
		shifts = new double[]{7, 8.5};   // y-offset workaround
		x = halfD.getWidth() - tick / 2;
		double tickS = Graph.tickS + 1;
		//double xS=3;
		for (int i = 0; i < limits.length; i++) {
			y = halfD.getHeight() - unit;
			val = 0;
			do {
				// draw tick
				main.add(new MoveTo(x, y));
				main.add(new HLineTo(x + tick));

				// add label
				val += unitV;
				text = String.format(format, val);
				//addLabel(text, x - shifts[i], y + tick + tickS);
				addLabel(text, x + tick + tickS, y - shifts[i]);

				// draw grid line
				grid.add(new MoveTo(0, y + gridS));
				grid.add(new HLineTo(fullD.getWidth()));

				y -= unit;
			} while (sign * y > sign * limits[i]);
			//xS=0;
			unit *= -1;
			unitV *= -1;
			sign *= -1;
		}
	}

	private static void resetPath(ObservableList<PathElement> path) {
		path.clear();
		// Workaround: Fix layout origin
		path.addAll(origin);
	}

	private static void addLabel(String text, double x, double y) {
		labelB.text(text);
		labelB.layoutX(x);
		labelB.layoutY(y);
		//labelB.style("-fx-background-color:green;");
		label = labelB.build();
		overlay.add(label);
		label.snapshot(null, null);

		//label.setLayoutX(x - label.getWidth() / 2);
	}

	private static MoveTo MoveTo(Point2D point) {
		return new MoveTo(point.getX(), point.getY());
	}

	private static CubicCurveTo CubicCurveTo(Point2D c1, Point2D c2, Point2D p) {
		return new CubicCurveTo(c1.getX(), c1.getY(), c2.getX(), c2.getY(), p.getX(), p.getY());
	}

	private Graph() {
		// Empty
	}
}
