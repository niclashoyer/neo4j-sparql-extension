
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

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openrdf.query.algebra.EmptySet;
import org.openrdf.query.algebra.Extension;
import org.openrdf.query.algebra.ExtensionElem;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.Var;

/**
 * A rule that transforms a statement pattern that contains a variable as
 * predicate to include inferred triples.
 */
public class PredicateVariable extends AbstractRule {
	
	private final List<String> predicates;
	
	/**
	 * Create a new predicate variable rule.
	 * 
	 * @param predicates all possible predicates of the TBox
	 */
	public PredicateVariable(String... predicates) {
		this(Arrays.asList(predicates));
	}

	/**
	 * Create a new predicate variable rule.
	 * 
	 * @param predicates all possible predicates of the TBox
	 */
	public PredicateVariable(List<String> predicates) {
		this.predicates = predicates;
	}
	
	/**
	 * Returns true if this rule is applicable to a node.
	 *
	 * @param node to a node
	 * @return true if the rule is applicable, false otherwise
	 */
	@Override
	public boolean canApply(StatementPattern node) {
		Var p = node.getPredicateVar();
		// check if predicate is variable
		return !(predicates.isEmpty() || p.isConstant());
	}

	/**
	 * Returns a list of expressions as a chain of {@link Union} objects, that
	 * will unify all expressions.
	 * 
	 * @param unions the expressions to unify
	 * @return an expression that unifies all given expressions
	 */
	private TupleExpr listAsUnion(List<TupleExpr> unions) {
		// nothing to unify
		if (unions.isEmpty()) {
			return new EmptySet();
		}
		// no need to add a union
		if (unions.size() == 1) {
			return unions.get(0);
		}
		Union last;
		Union tmp;
		// start with a union of two elements
		Union first = new Union();
		first.setLeftArg(unions.get(0));
		first.setRightArg(unions.get(1));
		last = first;
		// for each additional element replace the right side with another
		// union
		for (int i = 2; i < unions.size(); i++) {
			tmp = new Union(
				last.getRightArg(),
				unions.get(i)
			);
			last.setRightArg(tmp);
			last = tmp;
		}
		return first;
	}
	
	/**
	 * Creates an expression that extends a statement pattern to include
	 * inferred predicates.
	 * 
	 * @param assign a list of predicates that could be inferred
	 * @param source the source statement pattern
	 * @param next a list of expressions that should be visited next
	 * @return a union of statement patterns that include inferred triples
	 * for a predicate variable
	 */
	private TupleExpr assignPredicates(
			List<String> assign, StatementPattern source,
			List<QueryModelNode> next) {
		if (assign.isEmpty()) {
			return source;
		}
		Var s  = source.getSubjectVar();
		Var p  = source.getPredicateVar();
		Var o  = source.getObjectVar();
		Var c  = source.getContextVar();
		Var p2;
		StatementPattern sp;
		ArrayList<TupleExpr> union = new ArrayList<>();
		// for each possible predicate create an extension element that will
		// bind the predicate to the variable, if the predicate can be inferred.
		for (String a : assign) {
			p2 = new ConstVar(vf.createURI(a));
			sp = new StatementPattern(s, p2, o, c);
			next.add(sp);
			union.add(new Extension(
				sp,
				new ExtensionElem(
					new ValueConstant(vf.createURI(a)),
					p.getName())));
		}
		return listAsUnion(union);
	}
	
	/**
	 * Transform a statement pattern to infer triples for a predicate variable.
	 * 
	 * @param node the node to transform
	 * @return list of nodes to visit next
	 */
	@Override
	public List<QueryModelNode> apply(StatementPattern node) {
		List<QueryModelNode> next = newNextList();
		StatementPattern left = node.clone();
		next.add(left);
		TupleExpr right = assignPredicates(predicates, node.clone(), next);
		node.replaceWith(new Union(left, right));
		return next;
	}
	
	@Override
	public String toString() {
		String str = "";
		if (!predicates.isEmpty()) {
			str += "<";
			str += Joiner.on("> <").join(predicates) + ">";
		}
		return "PredicateVariable("+ str + ")";
	}
	
}
