
package de.unikiel.inf.comsys.neo4j.inference.rules;

import de.unikiel.inf.comsys.neo4j.inference.algebra.ConstVar;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Union;

public class SubObjectPropertyOf extends AbstractRule {
	
	private final String op1;
	private final String op2;
	
	public SubObjectPropertyOf(String op1, String op2) {
		this.op1 = op1;
		this.op2 = op2;
	}
	
	@Override
	public boolean canApply(StatementPattern node) {
		String op = getPredicate(node);
		return op != null && op.equals(op2);
	}

	@Override
	public void apply(StatementPattern node) {
		StatementPattern left  = node.clone();
		StatementPattern right =
			new StatementPattern(
				node.getSubjectVar(),
				new ConstVar(vf.createURI(op1)),
				node.getObjectVar(),
				node.getContextVar());
		node.replaceWith(
			new Union(left, right));
		visitNext(left);
		visitNext(right);
	}
	
	@Override
	public String toString() {
		return "SubObjectPropertyOf(<" + op1 + "> <" + op2 + ">)";
	}
	
}
