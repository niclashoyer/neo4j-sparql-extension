
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
import java.util.Arrays;
import java.util.List;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.Var;

/**
 * A rule that transforms a statement pattern according to the
 * SubObjectPropertyOf(ObjectPropertyChain) OWL-2 Axiom.
 *
 * @see
 * <a href="http://www.w3.org/TR/owl2-semantics/#Object_Property_Expression_Axioms">
 * OWL-2 property expression axioms
 * </a>
 */
public class ObjectPropertyChain extends AbstractRule {
	
	private final String op;
	private final List<String> chain;
	
	/**
	 * Create a new object property chain rule with a property and
	 * a list of chain elements.
	 * 
	 * @param op a property
	 * @param chain the chain elements
	 */
	public ObjectPropertyChain(String op, String... chain) {
		this(op, Arrays.asList(chain));
	}
	
	/**
	 * Create a new object property chain rule with a property and
	 * a list of chain elements.
	 * 
	 * @param op a property
	 * @param chain the chain elements
	 */
	public ObjectPropertyChain(String op, List<String> chain) {
		this.op = op;
		this.chain = chain;
	}
	
	/**
	 * Returns true if this rule is applicable to a node.
	 *
	 * @param node to a node
	 * @return true if the rule is applicable, false otherwise
	 */
	@Override
	public boolean canApply(StatementPattern node) {
		// empty chains are never applicable
		if (chain.isEmpty()) {
			return false;
		}
		// check if predicate is given object property
		String op1 = getPredicate(node);
		return op1 != null && op1.equals(op);
	}
	
	/**
	 * Transform a statement pattern according to OWL-2 property chain
	 * axiom.
	 * 
	 * @param node the node to transform
	 * @return list of nodes to visit next
	 */
	@Override
	public List<QueryModelNode> apply(StatementPattern node) {
		List<QueryModelNode> next = newNextList();
		Var s = node.getSubjectVar();
		Var o = node.getObjectVar();
		Var c = node.getContextVar();
		TupleExpr left  = node.clone();
		TupleExpr right = getChain(s, o, c);
		node.replaceWith(new Union(left, right));
		next.add(left);
		next.add(right);
		return next;
	}
	
	/**
	 * Create a object property chain for a given triple.
	 * 
	 * @param subject the subject of the triple
	 * @param object the object of the triple
	 * @param context the graph that the triple is stored in
	 * @return an object property chain expression
	 */
	private TupleExpr getChain(Var subject, Var object, Var context) {
		TupleExpr ret;
		if (chain.size() == 1) {
			// if the chain has just one element treat it as a SubObjectProperty
			// axiom
			Var p = new ConstVar(vf.createURI(chain.get(0)));
			ret   = new StatementPattern(subject, p, object, context);
		} else {
			Var p;
			Var p2;
			Var o;
			StatementPattern left;
			StatementPattern right;
			Join join;
			Join newjoin;
			// start with a chain with two elements
			p  = new ConstVar(vf.createURI(chain.get(0)));
			p2 = new ConstVar(vf.createURI(chain.get(1)));
			o  = new Var();
			o.setName(p.getName() + "-0");
			o.setAnonymous(true);
			left  = new StatementPattern(subject, p, o, context);
			right = new StatementPattern(o, p2, object, context);
			join = new Join(left, right);
			ret = join;
			// for each additional element replace the right side of the last
			// join with a new join that matches the chain element
			for (int i=2; i < chain.size(); i++) {
				p = new ConstVar(vf.createURI(chain.get(i)));
				o = new Var();
				o.setName(p.getName() + "-" + i);
				o.setAnonymous(true);
				right.setObjectVar(o);
				left = right;
				right = new StatementPattern(o, p, object, context);
				newjoin = new Join(left, right);
				join.setRightArg(newjoin);
				join = newjoin;
			}
		}
		return ret;
	}

	@Override
	public String toString() {
		String str = "";
		if (!chain.isEmpty()) {
			str += " <";
			str += Joiner.on("> <").join(chain) + ">";
		}
		return "ObjectPropertyChain(<" + op + ">" + str + ")";
	}
	
}
