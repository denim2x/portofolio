package work.tap.race;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

public class Worker {
	public static class PlayAction implements EventHandler<ActionEvent> {
		AnimationTimer timer;

		PlayAction( AnimationTimer timer ) {
			this.timer = timer;
		}

		@Override
		public void handle( ActionEvent event ) {
			timer.start();

		}
	}

	public static class PauseAction implements EventHandler<ActionEvent> {
		RaceTimer timer;

		PauseAction( RaceTimer timer ) {
			this.timer = timer;
		}

		@Override
		public void handle( ActionEvent event ) {
			timer.pause();
		}
	}

	public static class StopAction implements EventHandler<ActionEvent> {
		AnimationTimer timer;

		StopAction( AnimationTimer timer ) {
			this.timer = timer;
		}

		@Override
		public void handle( ActionEvent event ) {
			timer.stop();
		}
	}

	private static class RaceTimer extends AnimationTimer {
		public static enum State {
			Stopped, Playing, Paused
		}

		Worker worker;
		State state;
		Node node;
		double present;
		//int count;

		RaceTimer( Worker worker ) {
			this.worker = worker;
			state = State.Stopped;
			// /setState( State.Stopped );
		}

		@Override
		public void start() {
			if (state == State.Playing) {
				reset();
			} else {
				if (state == State.Stopped) {
					reset();
				}
				super.start();
				setState( State.Playing );
			}
		}

		public void pause() {
			switch (state) {
			case Paused:
				start();
				break;
			case Playing:
				super.stop();
				setState( State.Paused );
				break;
			case Stopped: // Do nothing.
				break;
			default: // No more cases.
				break;
			}
		}

		@Override
		public void stop() {
			super.stop();
			reset();
			setState( State.Stopped );
		}

		@Override
		public void handle( long now ) {
			//count = 0;
			// for (Node node : worker.track) {
			for (int i = 0; i < worker.track.size(); i++) {
				node = worker.track.get( i );
				present = node.getTranslateX();
				if (present <= worker.finish) {
					if (worker.random.nextDouble() < 0.2) {
						present += worker.getStep();
						node.setTranslateX( present );
					}
				} else {
					// count++;
					super.stop();
					// setState( State.Stopped );
					state = State.Stopped;
					worker.setWinner( i );
				}
			}
			/*
			 * if (count == worker.track.size()) { super.stop();
			 * setState( State.Stopped ); }
			 */
		}

		private void reset() {
			for (Node node : worker.track) {
				node.setTranslateX( 0 );
			}
		}

		private void setState( State state ) {
			this.state = state;
			worker.setStatus( state );
		}
	}

	private Random random;
	private RaceTimer timer;
	private double finish;
	private double minStep;
	private double maxStep;
	private ObservableList<Node> track;
	private List<EventHandler<ActionEvent>> handlers;
	private StringProperty status;
	private BooleanProperty pause;
	private BooleanProperty stop;

	Worker( ObservableList<Node> track ) {
		this.track = track;
		random = new Random();
		timer = new RaceTimer( this );

		handlers = new ArrayList<EventHandler<ActionEvent>>( 3 );
		handlers.add( new PlayAction( timer ) );
		handlers.add( new PauseAction( timer ) );
		handlers.add( new StopAction( timer ) );
	}

	private void setWinner( int index ) {
		pause.set( true );
		stop.set( true );
		status.set( "Winner: " + ( index + 1 ) );
	}

	void setLabel( StringProperty status ) {
		this.status = status;
	}

	void setButtons( BooleanProperty pause, BooleanProperty stop ) {
		this.pause = pause;
		this.stop = stop;
	}

	private double getStep() {
		return minStep + random.nextDouble() * ( maxStep - minStep );
	}

	void setStepRange( double min, double max ) {
		minStep = min;
		maxStep = max;
	}

	void setFinish( double finish ) {
		this.finish = finish;
	}

	Iterable<EventHandler<ActionEvent>> getHandlers() {
		return handlers;
	}

	private void setStatus( RaceTimer.State state ) {
		switch (state) {
		case Paused:
			status.set( "Paused" );
			break;
		case Playing:
			status.set( "Playing" );
			pause.set( false );
			stop.set( false );
			break;
		case Stopped:
			status.set( "Stopped" );
			pause.set( true );
			stop.set( true );
			break;
		default: // No more cases.
			break;
		}
	}
}
