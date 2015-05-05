package work.tap.quiz;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class Controller {
	public static class newQuestion implements EventHandler<ActionEvent> {

		@Override
		public void handle( ActionEvent event ) {
			// model.questions.add( new Question() );

		}
	}

	public static class delQuestion implements EventHandler<ActionEvent> {

		@Override
		public void handle( ActionEvent event ) {

		}
	}

	public static class newAnswer implements EventHandler<ActionEvent> {

		@Override
		public void handle( ActionEvent event ) {

		}
	}

	public static class delAnswer implements EventHandler<ActionEvent> {

		@Override
		public void handle( ActionEvent event ) {

		}
	}
	
	public static class FileNew implements EventHandler<ActionEvent>{
		@Override
		public void handle( ActionEvent event ) {

		}
	}
	
	public static class FileOpen implements EventHandler<ActionEvent>{
		@Override
		public void handle( ActionEvent event ) {

		}
	}
	
	public static class FileSave implements EventHandler<ActionEvent>{
		@Override
		public void handle( ActionEvent event ) {

		}
	}
	
	public static class FileSaveAs implements EventHandler<ActionEvent>{
		@Override
		public void handle( ActionEvent event ) {

		}
	}
	public static class FileRevert implements EventHandler<ActionEvent>{
		@Override
		public void handle( ActionEvent event ) {

		}
	}
	
	public static class FileClose implements EventHandler<ActionEvent>{
		@Override
		public void handle( ActionEvent event ) {

		}
	}
	
	public static class AppExit implements EventHandler<ActionEvent>{
		@Override
		public void handle( ActionEvent event ) {

		}
	}
	
	public static class HelpAbout implements EventHandler<ActionEvent>{
		@Override
		public void handle( ActionEvent event ) {

		}
	}

	private static int qIndex;
	private static int aIndex;

	static ObservableList<String> qList;
	static ObservableList<String> aList;

	private static List<EventHandler<ActionEvent>> dataHandlers;
	private static List<EventHandler<ActionEvent>> fileHandlers;
	private static List<EventHandler<ActionEvent>> appHandlers;

	static {
		// Add data action handlers
		dataHandlers.add( new newQuestion() );
		dataHandlers.add( new delQuestion() );
		dataHandlers.add( new newAnswer() );
		dataHandlers.add( new delAnswer() );
		
		// Add file action handlers
		fileHandlers.add( new FileNew() );
		fileHandlers.add( new FileOpen() );
		fileHandlers.add( new FileSave() );
		fileHandlers.add( new FileSaveAs() );
		fileHandlers.add( new FileRevert() );
		fileHandlers.add( new FileClose() );
		
		// Add application action handlers
		appHandlers.add( new AppExit() );
		appHandlers.add( new HelpAbout() );
	}

	public static Iterable<EventHandler<ActionEvent>> getDataHandlers() {
		return dataHandlers;
	}
	
	public static Iterable<EventHandler<ActionEvent>> getFileHandlers() {
		return fileHandlers;
	}
	
	public static Iterable<EventHandler<ActionEvent>> getAppHandlers() {
		return appHandlers;
	}
}
