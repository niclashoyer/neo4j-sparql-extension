
package de.unikiel.inf.comsys.neo4j.inference.rules;

import java.util.List;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Union;

/**
 * A rule that transforms a statement pattern according to the
 * SubPropertyOf OWL-2 Axiom.
 *
 * @see
 * <a href="http://www.w3.org/TR/owl2-semantics/#Object_Property_Expression_Axioms">
 * OWL-2 property expression axioms
 * </a>
 */
public class SubPropertyOf extends AbstractRule {
	
	private final String op1;
	private final String op2;
	
	/**
	 * Create a new subproperty rule.
	 * 
	 * @param op1 the subproperty
	 * @param op2 the superproperty
	 */
	public SubPropertyOf(String op1, String op2) {
		this.op1 = op1;
		this.op2 = op2;
	}
	
	/**
	 * Returns true if this rule is applicable to a node.
	 *
	 * @param node to a node
	 * @return true if the rule is applicable, false otherwise
	 */
	@Override
	public boolean canApply(StatementPattern node) {
		String op = getPredicate(node);
		// check that the predicate is the superproperty
		return op != null && op.equals(op2);
	}

	/**
	 * Transform a statement pattern according to OWL-2 subproperty axiom.
	 *
	 * @param node the node to transform
	 * @return list of nodes to visit next
	 */
	@Override
	public List<QueryModelNode> apply(StatementPattern node) {
		List<QueryModelNode> next = newNextList();
		StatementPattern left  = node.clone();
		// replace the predicate with the subproperty
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
