package de.unikiel.inf.comsys.neo4j.inference.rules;

import java.util.List;
import org.openrdf.model.URI;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.Var;

/**
 * A rule that transforms a statement pattern according to the
 * InverseObjectProperties OWL-2 Axiom.
 *
 * @see
 * <a href="http://www.w3.org/TR/owl2-semantics/#Object_Property_Expression_Axioms">
 * OWL-2 property expression axioms
 * </a>
 */
public class InverseObjectProperties extends AbstractRule {

	private final String op1;
	private final String op2;

	/**
	 * Create a new inverse properties rule with two inverse properties.
	 *
	 * @param op1 a property
	 * @param op2 a property
	 */
	public InverseObjectProperties(String op1, String op2) {
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
		// check if the predicate of the statement pattern matches one
		// or both rule predicates
		return op != null && (op.equals(op1) || op.equals(op2));
	}

	/**
	 * Transform a statement pattern according to OWL-2 inverse properties
	 * axiom.
	 *
	 * @param node the node to transform
	 * @return list of nodes to visit next
	 */
	@Override
	public List<QueryModelNode> apply(StatementPattern node) {
		List<QueryModelNode> next = newNextList();
		Var s = node.getSubjectVar();
		Var p = node.getPredicateVar();
		Var o = node.getObjectVar();
		Var c = node.getContextVar();
		URI uri = (URI) p.getValue();
		String op = uri.stringValue();
		Var p2;
		// check if need to replace with op1 or op2
		if (op.equals(op1)) {
			p2 = new ConstVar(vf.createURI(op2));
		} else {
			p2 = new ConstVar(vf.createURI(op1));
		}
		StatementPattern left = node.clone();
		// switch subject and object and replace predicate
		StatementPattern right = new StatementPattern(o, p2, s, c);
		node.replaceWith(new Union(left, right));
		next.add(left);
		next.add(right);
		return next;
	}

	@Override
	public String toString() {
		return "InverseObjectProperties(<" + op1 + "> <" + op2 + ">)";
	}

}
