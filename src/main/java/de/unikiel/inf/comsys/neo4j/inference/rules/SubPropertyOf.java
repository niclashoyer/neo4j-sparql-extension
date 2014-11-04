
package de.unikiel.inf.comsys.neo4j.inference.rules;

import java.util.List;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Union;

public class SubPropertyOf extends AbstractRule {
	
	private final String op1;
	private final String op2;
	
	public SubPropertyOf(String op1, String op2) {
		this.op1 = op1;
		this.op2 = op2;
	}
	
	@Override
	public boolean canApply(StatementPattern node) {
		String op = getPredicate(node);
		return op != null && op.equals(op2);
	}

	@Override
	public List<QueryModelNode> apply(StatementPattern node) {
		List<QueryModelNode> next = newNextList();
		StatementPattern left  = node.clone();
		StatementPattern right =
			new StatementPattern(
				node.getSubjectVar(),
				new ConstVar(vf.createURI(op1)),
				node.getObjectVar(),
				node.getContextVar());
		node.replaceWith(
			new Union(left, right));
		next.add(left);
		next.add(right);
		return next;
	}
	
	@Override
	public String toString() {
		return "SubPropertyOf(<" + op1 + "> <" + op2 + ">)";
	}
	
}
