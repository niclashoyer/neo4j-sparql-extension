
package de.unikiel.inf.comsys.neo4j.inference;

import de.unikiel.inf.comsys.neo4j.inference.rules.Rule;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

/**
 * A {@link QueryModelVisitorBase} implementation that transforms an expression
 * according to a list of rules.
 * 
 * This visitor visits each node and tries to apply all rules. If a rules
 * has been applied to a parent node it won't be applied in any child nodes.
 */
class RuleTransformationVisitor
		extends QueryModelVisitorBase<RuntimeException> {

	private final ArrayList<Rule> rules;
	/**
	 * A map to store reduced rule sets for rules.
	 * 
	 * An identity hash map is used because the Sesame implementation of
	 * the hash method of {@link QueryModelNode} is misleading.
	 * The hash method returns equal hashes for different statement patterns
	 * with the same entries. But the visitor needs to distinguish them, so
	 * the identity of the Java object is used instead of the hash value.
	 */
	private final IdentityHashMap<QueryModelNode, ArrayList<Rule>> applied;
	
	/**
	 * Creates a new rule transformation visitor that uses the given rules
	 * for query rewriting.
	 * 
	 * @param rules the rules used for rewriting
	 */
	public RuleTransformationVisitor(ArrayList<Rule> rules) {
		this.rules   = rules;
		this.applied = new IdentityHashMap<>();
	}
	
	/**
	 * Returns the rules that may be applicable for a given node.
	 * 
	 * @param node a node
	 * @return rules that may be applicable to the node
	 */
	private ArrayList<Rule> getRules(QueryModelNode node) {
		// check if there is an entry in the map
		if (applied.containsKey(node)) {
			return applied.get(node);
		} else {
			// if there is no entry in the map,
			// traverse branch up until root to find a reduced rule set
			QueryModelNode parent = node.getParentNode();
			while (parent != null) {
				if (applied.containsKey(parent)) {
					return applied.get(parent);
				} else {
					parent = parent.getParentNode();
				}
			}
			// if there are no reduced rule sets return the full set
			return rules;
		}
	}
	
	/**
	 * Removes a rule from a rule set for a node.
	 * 
	 * If there is no entry in the map use the base rule set instead and
	 * save it in the map.
	 * @param base the rule set to use if there is no reduced set in the map
	 * @param node a node
	 * @param r the rule to remove
	 */
	private void removeRule(List<Rule> base, QueryModelNode node, Rule r) {
		if (applied.containsKey(node)) {
			applied.get(node).remove(r);
		} else {
			ArrayList<Rule> reduced = new ArrayList<>(base);
			reduced.remove(r);
			applied.put(node, reduced);
		}
	}
	
	/**
	 * Visits a statement pattern and tries to transform using all rules
	 * in the given rule set.
	 * 
	 * Tries to apply a rule from the rule set. If it is applicable
	 * it will recurse using the visitor pattern to the nodes returned
	 * from the rule after application.
	 * @param node
	 * @throws RuntimeException 
	 */
	@Override
	public void meet(StatementPattern node) throws RuntimeException {
		// get a list of rules that may be applicable
		ArrayList<Rule> toApply = new ArrayList<>(getRules(node));
		for (Rule r : toApply) {
			// if a rule can be applied
			if (r.canApply(node)) {
				// apply the rule
				List<QueryModelNode> next = r.apply(node);
				// visit all nodes that the rule returned
				for (QueryModelNode toVisit : next) {
					removeRule(toApply, toVisit, r);
					toVisit.visit(this);
				}
				// halt execution, because the rule transformed the original
				// node and this visit can't transform further. Remaining rules
				// are applied in the recursion (see above).
				break;
			}
		}
	}
	
}
