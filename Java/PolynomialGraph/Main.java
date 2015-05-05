package plot;

import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import plot.model.PolynomialFunction;

/**
 * Samples: - 2 x ^5 +3 x^ 2- 0.05x -x^3+5x^2-3x-3 -x^4-3x^3+6x^2+8x
 *
 * @author Cristian
 */
public class Main extends Application {

	private static class PlotHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent t) {
			if (!fInput.get()) {
				nInput.requestFocus();
			}
			try {
				if (!input.equals(tInput.getValue())) {
					input = tInput.getValue();
					function = PolynomialFunction.parse(input);
					Graph.drawGraph();
				}
			} catch (IllegalArgumentException e) {
				rInput.set(errorF);
			}
		}
	}

	private static class InputHandler implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> ov, String t, String t1) {
			rInput.set(idleF);
			//changed = true;
		}
	}

	//private GraphicsContext canvas;
	private static Node nInput;
	private static Stage stage;
	private static String input = "";
	private static Paint idleF, errorF;
	private static DoubleProperty vZoom;
	private static StringProperty tInput;
	private static ReadOnlyBooleanProperty fInput;
	private static PolynomialFunction function;
	private static ObjectProperty<Paint> rInput;

	static PolynomialFunction getFunction() {
		return function;
	}

	static int getZoom() {
		return vZoom.getValue().intValue();
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setTitle("Polynomial function grapher");
		stage.show();
		stage.setMinWidth(stage.getWidth());
		stage.setMaxWidth(stage.getWidth());
		stage.setMinHeight(stage.getHeight());
		stage.setMaxHeight(stage.getHeight());
		Main.stage = stage;

		/*Canvas canvas = (Canvas) lookup("canvas");
		 this.canvas = canvas.getGraphicsContext2D();
		 fullD = new Dimension2D(canvas.getWidth(), canvas.getHeight());
		 halfD = new Dimension2D(canvas.getWidth() / 2, canvas.getHeight() / 2);*/

		Slider sZoom = (Slider) lookup("sZoom");
		Main.vZoom = sZoom.valueProperty();


		Button button = (Button) lookup("bPlot");
		button.setOnAction(new PlotHandler());

		TextField field = (TextField) lookup("tInput");
		field.setOnAction(new PlotHandler());
		tInput = field.textProperty();
		fInput = field.focusedProperty();
		nInput = field;
		tInput.addListener(new InputHandler());

		Rectangle rect = (Rectangle) lookup("rInput");
		rInput = rect.fillProperty();
		idleF = rect.getFill();
		errorF = Color.hsb(0, 0.5, 1);

		Graph.init();
		/*
		 input=("x^4");
		 function = PolynomialFunction.parse(input);
		 Graph.drawGraph();*/
	}

	static Node lookup(String id) {
		return stage.getScene().lookup("#" + id);
	}

	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be launched
	 * through deployment artifacts, e.g., in IDEs with limited FX support.
	 * NetBeans ignores main().
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	static void test() {
		double x = Double.NaN;
		float y = Float.NEGATIVE_INFINITY;
		Number z = 0F;
		javafx.geometry.Point2D point = new javafx.geometry.Point2D(Double.POSITIVE_INFINITY, Double.NaN);
		for (utils.NumericError error : utils.uMath.inspect(x, y, z, point)) {
			if (error != null) {
				System.out.print(error + " ");
			} else {
				System.out.print("OK ");
			}
		}
	}
	/*
	 private void drawSystem() {
	 //Point2DBuilder pointB = Point2DBuilder.create();
	 //pointB.x(zoom)
	 
	 canvas.setLineWidth(1.2);
	 canvas.setStroke(Color.hsb(0, 0, 0.45));

	 canvas.beginPath();
	 canvas.moveTo(0, halfD.getHeight());
	 canvas.lineTo(fullD.getWidth(), halfD.getHeight());
	 canvas.closePath();
	 canvas.stroke();

	 //Draw Y-axis
	 canvas.beginPath();
	 canvas.moveTo(halfD.getWidth(), 0);
	 canvas.lineTo(halfD.getWidth(), fullD.getHeight());
	 canvas.closePath();
	 canvas.stroke();

	 // Compute unit size
	 double unit = halfD.getHeight() / zoom;
	 double temp = 0;
	 int sign = 1;
	 if (unit < minU) {
	 temp = uMath.log2(minU / unit);
	 }
	 if (unit > maxU) {
	 temp = uMath.log2(unit / maxU);
	 sign = -1;
	 }
	 temp = sign * Math.ceil(temp);
	 temp = Math.pow(2, temp);
	 unit *= temp;

	 // Draw x-axis ticks
	 double x = halfD.getWidth();
	 double y = halfD.getHeight() - tick / 2;
	 double val = 0;
	 String text;
	 labelB.textAlignment(TextAlignment.CENTER);
	 labelB.alignment(Pos.TOP_CENTER);
	 labelB.font(Font.font("DejaVu Sans", FontWeight.NORMAL, 10));
	 String format;
	 if (temp >= 1) {
	 format = "%.0f";
	 } else {
	 format = "%.1f";
	 }
	 do {
	 // draw tick
	 x += unit;

	 canvas.setLineWidth(1.2);
	 canvas.beginPath();
	 canvas.moveTo(x, y);
	 canvas.lineTo(x, y + tick);
	 canvas.closePath();
	 canvas.stroke();

	 // draw value
	 val += temp;
	 text = String.format(format, val);

	 addLabel(text, x, y + tick);
	 } while (x < fullD.getWidth());
	 }*/
}