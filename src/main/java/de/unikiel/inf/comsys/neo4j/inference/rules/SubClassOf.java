package de.unikiel.inf.comsys.neo4j.inference.rules;

/*
 * #%L
 * neo4j-sparql-extension
 * %%
 * Copyright (C) 2014 Niclas Hoyer
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.List;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Union;

/**
 * A rule that transforms a statement pattern according to the SubClassOf OWL-2
 * Axiom.
 *
 * @see
 * <a href="http://www.w3.org/TR/owl2-semantics/#Class_Expression_Axioms">
 * OWL-2 class expression axioms
 * </a>
 */
public class SubClassOf extends AbstractRule {

	private final String RDFTYPE
			= "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	private final String ce1;
	private final String ce2;

	/**
	 * Create a new subclass rule.
	 *
	 * @param ce1 the subclass
	 * @param ce2 the superclass
	 */
	public SubClassOf(String ce1, String ce2) {
		this.ce1 = ce1;
		this.ce2 = ce2;
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
		String o = getObject(node);
		// check that the predicate is "rdf:type" and the object is the
		// superclass
		return op != null && o != null && op.equals(RDFTYPE) && o.equals(ce2);
	}

	/**
	 * Transform a statement pattern according to OWL-2 subclass axiom.
	 *
	 * @param node the node to transform
	 * @return list of nodes to visit next
	 */
	@Override
	public List<QueryModelNode> apply(StatementPattern node) {
		List<QueryModelNode> next = newNextList();
		StatementPattern left = node.clone();
		// replace the object with the subclass
		StatementPattern right
				= new StatementPattern(
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
