package work.tap.quiz;

import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start( Stage primaryStage ) throws IOException {
		View.init( primaryStage, this );
		
		
	}

	public static void main( String[] args ) {
		launch( args );
	}
}
