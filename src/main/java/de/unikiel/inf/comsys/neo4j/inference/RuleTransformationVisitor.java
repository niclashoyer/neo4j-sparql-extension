
package de.unikiel.inf.comsys.neo4j.inference;

import java.util.ArrayList;
import java.util.List;
import java.util.IdentityHashMap;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

class RuleTransformationVisitor
extends QueryModelVisitorBase<RuntimeException> {

	private final ValueFactory vf;
	private final ArrayList<Rule> rules;
	private final IdentityHashMap<QueryModelNode, ArrayList<Rule>> applied;
	
	public RuleTransformationVisitor(ValueFactory vf, ArrayList<Rule> rules) {
		this.vf      = vf;
		this.rules   = rules;
		this.applied = new IdentityHashMap<>();
	}
	
	private ArrayList<Rule> getRules(QueryModelNode node) {
		if (applied.containsKey(node)) {
			return applied.get(node);
		} else {
			// traverse branch up until root, to find reduced rule set
			QueryModelNode parent = node.getParentNode();
			while (parent != null) {
				if (applied.containsKey(parent)) {
					return applied.get(parent);
				} else {
					parent = parent.getParentNode();
				}
			}
			return rules;
		}
	}
	
	private void removeRule(List<Rule> base, QueryModelNode node, Rule r) {
		if (applied.containsKey(node)) {
			applied.get(node).remove(r);
		} else {
			ArrayList<Rule> reduced = new ArrayList<>(base);
			reduced.remove(r);
			applied.put(node, reduced);
		}
	}
	
	@Override
	public void meet(StatementPattern node) throws RuntimeException {
		ArrayList<Rule> toApply = new ArrayList<>(getRules(node));
		/*
		for (Rule r : toApply) {
			System.out.print("[APPLY] ");
			if (r.canApply(node)) {
				System.out.println("[YES] " + r);
			} else {
				System.out.println("[NO] " + r);
			}
		}
		*/
		for (Rule r : toApply) {
			if (r.canApply(node)) {
				r.apply(node);
				for (QueryModelNode toVisit : r.getNextVisits()) {
					removeRule(toApply, toVisit, r);
					//System.out.println("[VISITING] " + toVisit);
					toVisit.visit(this);
				}
				break;
			}
		}
	}
	
}
