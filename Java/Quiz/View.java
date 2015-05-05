package work.tap.quiz;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

public class View {

	private static class SizeListener implements ChangeListener<Number> {
		Control ctl;
		Region reg;
		Label label;
		double[] size;

		public SizeListener( Control obj, String name ) {
			this.ctl = obj;
			init( obj, name );
		}

		public SizeListener( Region obj, String name ) {
			this.reg = obj;
			init( obj, name );
		}

		void init( Node obj, String name ) {
			Scene scene = obj.getScene();
			label = (Label) scene.lookup( "#" + name );
			size = new double[2];
			scene.lookup( "#pTest" ).setVisible( true );
			label.setVisible( true );
		}

		public void changed( ObservableValue<? extends Number> ov,
				Number old_val, Number new_val ) {
			if (ctl != null) {
				size[0] = ctl.getWidth();
				size[1] = ctl.getHeight();
			} else {
				size[0] = reg.getWidth();
				size[1] = reg.getHeight();
			}
			label.setText( size[0] + " " + size[1] );
		}

		public static void setCtl( String oName, String lName ) {
			Control obj = (Control) scene.lookup( "#" + oName );
			SizeListener listener = new SizeListener( obj, lName );
			obj.widthProperty().addListener( listener );
			obj.heightProperty().addListener( listener );
		}

		public static void setReg( String oName, String lName ) {
			Region obj = (Region) scene.lookup( "#" + oName );
			SizeListener listener = new SizeListener( obj, lName );
			obj.widthProperty().addListener( listener );
			obj.heightProperty().addListener( listener );
		}
	}

	private static class GripManager {
		static class PressedHandler implements EventHandler<MouseEvent> {
			GripManager manager;

			PressedHandler( GripManager manager ) {
				this.manager = manager;
			}

			@Override
			public void handle( MouseEvent event ) {
				manager.isPressed = true;
				manager.x = event.getScreenX();
				manager.y = event.getScreenY();
			}
		}

		static class ReleasedHandler implements EventHandler<MouseEvent> {
			GripManager manager;

			ReleasedHandler( GripManager manager ) {
				this.manager = manager;
			}

			@Override
			public void handle( MouseEvent event ) {
				manager.isPressed = false;
			}
		}

		static class MovedHandler implements EventHandler<MouseEvent> {
			GripManager manager;
			double x, y;

			MovedHandler( GripManager manager ) {
				this.manager = manager;
			}

			@Override
			public void handle( MouseEvent event ) {
				if (manager.isPressed) {
					System.out.println( "GripMoved" );
					x = event.getScreenX() - manager.x;
					y = event.getSceneY() - manager.y;
					stage.setWidth( stage.getWidth() + x );
					stage.setHeight( stage.getHeight() + y );
				}
			}
		}

		boolean isPressed;
		double x, y;

		GripManager( Node grip ) {
			grip.addEventHandler( MouseEvent.MOUSE_PRESSED, new PressedHandler(
					this ) );
			grip.addEventHandler( MouseEvent.MOUSE_RELEASED,
					new ReleasedHandler( this ) );
			scene.addEventHandler( MouseEvent.MOUSE_MOVED, new MovedHandler(
					this ) );
			isPressed = false;
		}

		public static void setGrip( String id ) {
			Node grip = lookup( id );
			new GripManager( grip );
		}
	}

	private static class TabManager {
		static class VisibleHandler implements ChangeListener<Boolean> {
			Node node;

			VisibleHandler( Node node ) {
				this.node = node;
			}

			@Override
			public void changed( ObservableValue<? extends Boolean> obs,
					Boolean oldVal, Boolean newVal ) {
				if (newVal) {
					node.requestFocus();
				}
			}
		}

		static class KeyHandler implements EventHandler<KeyEvent> {
			SingleSelectionModel<Tab> model;
			int last;

			KeyHandler( TabPane pane ) {
				model = pane.getSelectionModel();
				last = pane.getTabs().size() - 1;
			}

			@Override
			public void handle( KeyEvent event ) {
				if (event.isControlDown() && event.getCode() == KeyCode.TAB) {
					if (model.getSelectedIndex() < last) {
						model.selectNext();
					} else {
						model.selectFirst();
					}
				}
			}
		}

		public static class FocusHandler implements ChangeListener<Boolean> {
			SingleSelectionModel<Tab> model;

			FocusHandler( TabPane pane ) {
				model = pane.getSelectionModel();
			}

			@Override
			public void changed( ObservableValue<? extends Boolean> obs,
					Boolean oldVal, Boolean newVal ) {
				if (newVal) {
					model.getSelectedItem().getContent().requestFocus();
				}
			}
		}

		public static void setPane( String id ) {
			TabPane pane = (TabPane) lookup( id );
			ObservableList<Tab> list = pane.getTabs();
			Node node;
			for (Tab tab : list) {
				node = tab.getContent();
				node.visibleProperty().addListener( new VisibleHandler( node ) );
			}
			pane.addEventFilter( KeyEvent.KEY_PRESSED, new KeyHandler( pane ) );
		}
	}

	private static class SceneManager {
		private static class FocusHandler implements ChangeListener<Node> {
			@Override
			public void changed( ObservableValue<? extends Node> obs,
					Node oldVal, Node newVal ) {
				if (oldVal != newVal) {
					if (oldVal != null) {
						list = map.get( oldVal );
						if (list != null) {
							for (ChangeListener<Boolean> handler : list) {
								handler.changed( oldVal.focusedProperty(),
										true, false );
							}
						}
					}
					list = map.get( newVal );
					if (list != null) {
						for (ChangeListener<Boolean> handler : list) {
							handler.changed( newVal.focusedProperty(), false,
									true );
						}
					}
				}
			}
		}

		private static Map<Node, ArrayList<ChangeListener<Boolean>>> map;
		private static ArrayList<ChangeListener<Boolean>> list;
		private static String sNull = "Paramater '%s' cannot be null.";
		private static String sTemp;

		private static void validate( Object obj, String name ) {
			if (obj == null) {
				sTemp = String.format( sNull, name );
				throw new NullPointerException( sTemp );
			}
		}

		public static void setScene() {
			map = new HashMap<Node, ArrayList<ChangeListener<Boolean>>>();
			scene.focusOwnerProperty().addListener( new FocusHandler() );
		}

		public static void addFocusHandler( Node node,
				ChangeListener<Boolean> handler ) {
			validate( node, "node" );
			validate( handler, "handler" );

			// handlers.put( node, listener );
			list = map.get( node );
			if (list == null) {
				list = new ArrayList<ChangeListener<Boolean>>();
				list.add( handler );
				map.put( node, list );
			} else {
				list.add( handler );
			}
		}
	}

	private static Scene scene;

	private static Stage stage;

	private View() {
	}

	public static Scene getScene() {
		return scene;
	}

	public static Stage getStage() {
		return stage;
	}

	public static Node lookup( String id ) {
		return scene.lookup( "#" + id );
	}

	public static void init( Stage primaryStage, Application app )
			throws IOException {
		Class<? extends Application> appClass = app.getClass();
		stage = primaryStage;
		double[] size = new double[2];
		Pane root = (Pane) FXMLLoader.load( appClass
				.getResource( "QuizEditor.fxml" ) );
		scene = new Scene( root );
		stage.setTitle( "QuizEditor - Untitled.xml" );
		stage.setScene( scene );
		stage.show();

		size[0] = stage.getWidth() - root.getWidth();
		size[1] = stage.getHeight() - root.getHeight();
		stage.setMinWidth( root.getMinWidth() + size[0] );
		stage.setMinHeight( root.getMinHeight() + size[1] );

		Rectangle screenSize = java.awt.GraphicsEnvironment
				.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		stage.setMaxWidth( screenSize.getWidth() );
		stage.setMaxHeight( screenSize.getHeight() );

		TabPane tpEdit = (TabPane) lookup( "tpEdit" );
		TabManager.setPane( "tpEdit" );

		SceneManager.setScene();
		SceneManager.addFocusHandler( tpEdit, new TabManager.FocusHandler(
				tpEdit ) );

		// Build menu item dictionary
		Map<String, MenuItem> mItems = new TreeMap<String, MenuItem>();
		MenuBar mBar = (MenuBar) lookup( "mBar" );
		Iterable<Menu> menus = mBar.getMenus();
		Iterable<MenuItem> menuItems;
		String mId;
		for (Menu menu : menus) {
			menuItems = menu.getItems();
			for (MenuItem mItem : menuItems) {
				mId = mItem.getId();
				mItems.put( mId, mItem );
			}
		}

		String[] idStrings = new String[2];
		List<Iterable<EventHandler<ActionEvent>>> handlerList = new ArrayList<Iterable<EventHandler<ActionEvent>>>(
				2 );
		String[] ids;
		Button btn;
		MenuItem mItem;
		Iterable<EventHandler<ActionEvent>> handlers;
		int j;
		idStrings[0] = "nQ dQ nA dA";
		handlerList.add( 0, Controller.getDataHandlers() );
		handlerList.add( 1, Controller.getFileHandlers() );
		idStrings[1] = "New Open Save SaveAs Revert Close";

		for (int i = 0; i < idStrings.length; i++) {
			ids = idStrings[i].split( " " );
			handlers = handlerList.get( i );
			// Set handlers for scene buttons
			j = 0;
			for (EventHandler<ActionEvent> handler : handlers) {
				btn = (Button) lookup( "b" + ids[j++] );
				btn.setOnAction( handler );
			}

			// Set handlers for menu items
			j = 0;
			for (EventHandler<ActionEvent> handler : handlers) {
				mItem = mItems.get( "m" + ids[j++] );
				mItem.setOnAction( handler );
			}
		}

	}
}
