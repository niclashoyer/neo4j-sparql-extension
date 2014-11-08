package de.unikiel.inf.comsys.neo4j.inference.rules;

import java.util.List;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;

/**
 * A rule implements a single transformation of a statement pattern.
 *
 * Each rule implements a transformation of one statement pattern. It does not
 * necessarily have to do a transformation. It should return false when the
 * canApply method is called. If the apply method is called a transformation
 * should add inference using a union with the old pattern and a new expression
 * that evaluates to inferred solutions.
 *
 * A rule can return a list of expressions that need further transformation,
 * e.g. if the rules creates new statement patterns.
 */
public interface Rule {

	/**
	 * Called to set the value factory that is used by the caller.
	 *
	 * Useful for consistent generation of URIs and blank nodes. Must be called
	 * before using any other methods.
	 *
	 * @param vf the value factory to use
	 */
	public void setValueFactory(ValueFactory vf);

	/**
	 * Returns if this rule is applicable to a statement pattern.
	 *
	 * @param node the statement pattern to check
	 * @return true, if this rule would transform the pattern, false otherwise
	 */
	public boolean canApply(StatementPattern node);

	/**
	 * Transforms a statement pattern to include inference.
	 *
	 * @param node the statement pattern to transform
	 * @return a list of expressions that still need to be transformed
	 */
	public List<QueryModelNode> apply(StatementPattern node);
}
