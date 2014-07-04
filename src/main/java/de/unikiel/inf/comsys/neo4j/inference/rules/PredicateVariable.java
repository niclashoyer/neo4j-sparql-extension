
package de.unikiel.inf.comsys.neo4j.inference.rules;

import de.unikiel.inf.comsys.neo4j.inference.algebra.ConstVar;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.Var;

public class PredicateVariable extends AbstractRule {

	private final String TOPOBJECTPROP =
		"http://www.w3.org/2002/07/owl#topObjectProperty";
	
	@Override
	public boolean canApply(StatementPattern node) {
		Var p = node.getPredicateVar();
		return !p.isConstant(); // TODO: check if check for variable is correct
	}

	@Override
	public void apply(StatementPattern node) {
		StatementPattern left  = node.clone();
		StatementPattern right =
			new StatementPattern(
				node.getSubjectVar(),
				new ConstVar(vf.createURI(TOPOBJECTPROP)), // FIXME: replacing ?p with :topObjectProperty is NOT equivalent, because ?p is now unbound
				node.getObjectVar(),
				node.getContextVar());
		node.replaceWith(
			new Union(left, right));
		visitNext(left);
		visitNext(right);
	}
	
}
