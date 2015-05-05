package work.tap.quiz;
import java.util.List;

public class Model {
	static class Question {
		List<Answer> answers;
		String text;

		@Override
		public String toString() {
			return text;
		}

		String getAnswer( int index ) {
			return String.format( "%d. %s", index + 1, answers.get( index ) );
		}

		void add( Answer answer ) {
			answers.add( answer );
		}
	}

	static class Answer {
		String text;

		@Override
		public String toString() {
			return text;
		}
	}

	private Model() {
	}

	public static List<Question> questions;

	String getQuestion( int index ) {
		return String.format( "%d. %s", index + 1, questions.get( index ) );
	}
/*
	public void setQuestions( ListView<String> list ) {
		qList = list.getItems();
		qList.add( getQuestion( 0 ) );
	}

	public void setAnswers( ListView<String> list ) {
		aList = list.getItems();
		aList.add( questions.get( 0 ).getAnswer( 0 ) );
	}
	*/
}
