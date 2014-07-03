package de.unikiel.inf.comsys.neo4j.inference.rules;

import de.unikiel.inf.comsys.neo4j.inference.algebra.ConstVar;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.Var;

public class InverseObjectProperty extends AbstractRule {

	private final String op1;
	private final String op2;

	public InverseObjectProperty(String op1, String op2) {
		this.op1 = op1;
		this.op2 = op2;
	}

	@Override
	public boolean canApply(StatementPattern node) {
		Var p = node.getPredicateVar();
		if (p.isConstant()) {
			Value val = p.getValue();
			if (val instanceof URI) {
				URI uri = (URI) val;
				String op = uri.stringValue();
				return op.equals(op1) || op.equals(op2);
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
		URI uri = (URI) p.getValue();
		String op = uri.stringValue();
		Var p2;
		if (op.equals(op1)) {
			p2 = new ConstVar(vf.createURI(op2));
		} else {
			p2 = new ConstVar(vf.createURI(op1));
		}
		node.replaceWith(
			new Union(
				node.clone(),
				new StatementPattern(o, p2, s, c)));
	}

}
