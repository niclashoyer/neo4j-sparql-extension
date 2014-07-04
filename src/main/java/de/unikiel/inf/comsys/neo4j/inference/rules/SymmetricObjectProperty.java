package de.unikiel.inf.comsys.neo4j.inference.rules;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.Var;

public class SymmetricObjectProperty extends AbstractRule {

	private final String op;

	public SymmetricObjectProperty(String op) {
		this.op = op;
	}

	@Override
	public boolean canApply(StatementPattern node) {
		String op1 = getPredicate(node);
		return op1 != null && op1.equals(op);
	}

	@Override
	public void apply(StatementPattern node) {
		Var s = node.getSubjectVar();
		Var p = node.getPredicateVar();
		Var o = node.getObjectVar();
		Var c = node.getContextVar();
		StatementPattern left  = node.clone();
		StatementPattern right = new StatementPattern(o, p, s, c);
		node.replaceWith(
			new Union(left, right));
		visitNext(left);
		visitNext(right);
	}

}
