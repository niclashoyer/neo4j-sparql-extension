
package de.unikiel.inf.comsys.neo4j.inference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

class RuleTransformationVisitor
extends QueryModelVisitorBase<RuntimeException> {

	private final ValueFactory vf;
	private final List<Rule> rules;
	private final HashMap<QueryModelNode, List<Rule>> applied;
	
	public RuleTransformationVisitor(ValueFactory vf, List<Rule> rules) {
		this.vf      = vf;
		this.rules   = rules;
		this.applied = new HashMap<>();
	}
	
	private List<Rule> getRules(QueryModelNode node) {
		if (applied.containsKey(node)) {
			return applied.get(node);
		} else {
			return rules;
		}
	}
	
	private void removeRule(QueryModelNode node, Rule r) {
		if (applied.containsKey(node)) {
			applied.get(node).remove(r);
		} else {
			ArrayList<Rule> reduced = new ArrayList<>(rules);
			reduced.remove(r);
			applied.put(node, reduced);
		}
	}
	
	@Override
	public void meet(StatementPattern node) throws RuntimeException {
		List<Rule> toApply = getRules(node);
		for (Rule r : toApply) {
			if (r.canApply(node)) {
				r.apply(node);
				for (QueryModelNode toVisit : r.getNextVisits()) {
					removeRule(toVisit, r);
					toVisit.visit(this);
				}
			}
		}
	}
	
}
