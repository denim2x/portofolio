package work.tap.race;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

	static Stage stage;

	static Node lookup( String id ) {
		return stage.getScene().lookup( "#" + id );
	}

	@Override
	public void start( Stage primaryStage ) throws IOException {
		stage = primaryStage;
		stage.initStyle( StageStyle.UTILITY );

		Pane root = (Pane) FXMLLoader.load( getClass()
				.getResource( "Race.fxml" ) );
		Scene scene = new Scene( root );
		stage.setTitle( "Turtle racing simulation" );
		stage.setScene( scene );
		stage.show();

		stage.setMinWidth( stage.getWidth() );
		stage.setMinHeight( stage.getHeight() );
		stage.setMaxWidth( stage.getMinWidth() );
		stage.setMaxHeight( stage.getMinHeight() );

		Pane track = (Pane) lookup( "pTrack" );
		ObservableList<Node> items = track.getChildrenUnmodifiable();
		Worker worker = new Worker( items );
		worker.setFinish( 670 );
		worker.setStepRange( 3, 10 );
		Label status = (Label) lookup( "lbStatus" );
		worker.setLabel( status.textProperty() );

		Button btn;
		String[] ids = "Play Pause Stop".split( " " );
		List<BooleanProperty> disable = new ArrayList<BooleanProperty>( 2 );
		Iterable<EventHandler<ActionEvent>> handlers;
		handlers = worker.getHandlers();

		int i = 0;
		for (EventHandler<ActionEvent> handler : handlers) {
			btn = (Button) lookup( "b" + ids[i] );
			if (i++ > 0) {
				btn.setDisable( true );
				disable.add( btn.disableProperty() );
			}
			btn.setOnAction( handler );
		}
		worker.setButtons( disable.get( 0 ), disable.get( 1 ) );

		Pane pNum = (Pane) lookup( "pNum" );
		Label label;
		items = pNum.getChildrenUnmodifiable();
		for (i = 1; i < items.size(); i++) {
			//label = (Label) items.get( i );
			//label.setText( String.valueOf( i ) );
		}
		/*
		 * //Label number; for( i=0; i<racers.size();i++){
		 * 
		 * /*number = new Label(); number.setText( String.valueOf( i )
		 * ); pNum.getChildren().add( number ); }
		 */
	}

	public static void main( String[] args ) {
		launch( args );

	}
}
