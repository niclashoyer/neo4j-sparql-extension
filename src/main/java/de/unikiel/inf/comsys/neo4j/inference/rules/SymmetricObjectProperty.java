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
		Var p = node.getPredicateVar();
		if (p.isConstant()) {
			Value val = p.getValue();
			if (val instanceof URI) {
				URI uri = (URI) val;
				return uri.stringValue().equals(op);
			}
		}
		return false;
	}

	@Override
	public void apply(StatementPattern node) {
		Var s = node.getSubjectVar();
		Var p = node.getPredicateVar();
		Var o = node.getObjectVar();
		Var c = node.getContextVar();
		node.replaceWith(
			new Union(
				node.clone(),
				new StatementPattern(o, p, s, c)));
	}

}
