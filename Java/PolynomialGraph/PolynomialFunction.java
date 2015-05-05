/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javafx.geometry.Point2D;
import utils.functions.Function;
import utils.functions.FunctionGraph;
import utils.NumericRangeException;
import utils.uMath;
import utils.Utils;

/**
 *
 * @author Cristian
 */
public class PolynomialFunction implements Function<Double, Double> {

	public static class Builder {

		Iterable<Byte> degrees;
		PolynomialFunction.Term term;
		List<PolynomialFunction.Term> list;
		private TreeMap<Byte, Double> terms = new TreeMap<>(); // maps ranks to coefficients

		public Builder() {
		}

		public Builder(PolynomialFunction function) {
			Utils.validate("function", function);
			for (Term _term : function.terms) {
				terms.put(_term.degree, _term.factor);
			}
		}

		public PolynomialFunction build() {
			if (terms.isEmpty()) {
				throw new IllegalStateException("Cannot create function with no terms.");
			}
			list = new ArrayList<>(terms.size());
			degrees = terms.descendingKeySet();
			for (byte degree : degrees) {
				//term = new PolynomialFunction.Term(degree, terms.get(degree));
				term = Term.create(degree, terms.get(degree));
				list.add(term);
			}
			return new PolynomialFunction(list);
		}

		public void addTerm(Term term) {
			Utils.validate("term", term);
			addTerm(term.degree, term.factor);
		}

		public void addTerm(int degree, double factor) {
			if (terms.containsKey($(degree))) {
				throw new IllegalStateException("The builder already contains a term of this degree.");
			}
			setTerm(degree, factor);
		}

		public void setTerm(int degree, double factor) {
			uMath.validate(factor);
			terms.put($(degree), factor);
		}

		public double getFactor(int degree) {
			return terms.get($(degree));
		}

		public void removeTerm(int degree) {
			terms.remove($(degree));
		}

		public boolean hasTerm(int degree) {
			return terms.containsKey($(degree));
		}

		void _addTerm(int degree, double factor) {
			terms.put($(degree), factor);
		}

		private byte $(int degree) {
			return Term.validate(degree);
		}
	}

	public static class Term {

		private byte degree;
		private double factor;
		private Term derivative;

		//public Term(int degree, double factor) {
		public Term(int degree, double factor) {
			uMath.validate(factor);
			this.degree = validate(degree);
			this.factor = factor;
		}

		public Term getDerivative() {
			if (derivative == null) {
				derivative = new Term();
				derivative.factor = factor * degree;
				derivative.degree = degree;
				if (degree > 0) {
					derivative.degree--;
				}
			}
			return derivative;
		}

		static byte validate(int degree) {
			if (degree < 0) {
				throw new NumericRangeException("The degree cannot be negative.");
			}
			return (byte) degree;
		}

		static Term create(byte degree, double factor) {
			Term term = new Term();
			term.degree = degree;
			term.factor = factor;
			return term;
		}

		private Term() {
		}
	}

	private double sum;
	private int degree;
	private Builder derivativeB;
	private FunctionGraph graph;
	private Iterable<Term> terms;  // terms in descending order by rank
	private PolynomialFunction derivative;

	public static PolynomialFunction parse(String input) {
		return Parser.parse(input);
	}

	private PolynomialFunction(Iterable<Term> terms) {
		this.terms = terms;
		setDegree();
	}

	public int getDegree() {
		return degree;
	}

	@Override
	public Double compute(Double input) {
		uMath.validate(input);
		sum = 0;
		for (Term term : terms) {
			sum += term.factor * Math.pow(input, term.degree);
		}
		return sum;
	}

	@Override
	public PolynomialFunction getDerivative() {
		if (derivative == null) {
			derivativeB = new Builder();
			if (degree == 0) {
				derivativeB.addTerm(new Term(0, 0));
			} else {
				for (Term term : terms) {
					if (term.degree > 0) {
						derivativeB.addTerm(term.getDerivative());
					}
				}
			}

			derivative = derivativeB.build();
		}
		return derivative;
	}

	@Override
	public FunctionGraph getGraph(Point2D p1, Point2D p2) {
		if (graph == null) {
			graph = new FunctionGraph(this);
		}
		graph.update(p1, p2);
		return graph;
		//return graph.build(p1, p2);
	}

	@Override
	public boolean isHLine() {
		return degree == 0;
	}

	private void setDegree() {
		degree = terms.iterator().next().degree;
	}
}
