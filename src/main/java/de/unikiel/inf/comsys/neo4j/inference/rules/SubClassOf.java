
package de.unikiel.inf.comsys.neo4j.inference.rules;

import java.util.List;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Union;

public class SubClassOf extends AbstractRule {

	private final String RDFTYPE =
		"http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	
	private final String ce1;
	private final String ce2;
	
	public SubClassOf(String ce1, String ce2) {
		this.ce1 = ce1;
		this.ce2 = ce2;
	}
	
	@Override
	public boolean canApply(StatementPattern node) {
		String op = getPredicate(node);
		String o  = getObject(node);
		return op != null && o != null && op.equals(RDFTYPE) && o.equals(ce2);
	}

	@Override
	public List<QueryModelNode> apply(StatementPattern node) {
		List<QueryModelNode> next = newNextList();
		StatementPattern left  = node.clone();
		StatementPattern right =
			new StatementPattern(
				node.getSubjectVar(),
				node.getPredicateVar(),
				new ConstVar(vf.createURI(ce1)),
				node.getContextVar());
		node.replaceWith(
			new Union(left, right));
		next.add(left);
		next.add(right);
		return next;
	}

	@Override
	public String toString() {
		return "SubClassOf(<" + ce1 + "> <" + ce2 + ">)";
	}
	
}
