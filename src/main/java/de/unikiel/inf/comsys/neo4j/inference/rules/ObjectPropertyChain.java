
package de.unikiel.inf.comsys.neo4j.inference.rules;

import de.unikiel.inf.comsys.neo4j.inference.algebra.ConstVar;
import java.util.Arrays;
import java.util.List;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.Var;

public class ObjectPropertyChain extends AbstractRule {
	
	private final String op;
	private final List<String> chain;
	
	public ObjectPropertyChain(String op, String... chain) {
		this(op, Arrays.asList(chain));
	}
	
	public ObjectPropertyChain(String op, List<String> chain) {
		this.op = op;
		this.chain = chain;
	}
	
	@Override
	public boolean canApply(StatementPattern node) {
		if (chain.isEmpty()) {
			return false;
		}
		Var p = node.getPredicateVar();
		if (p.isConstant()) {
			Value val = p.getValue();
			if (val instanceof URI) {
				URI uri = (URI) val;
				return uri.stringValue().equals(op);
			}
		}
		return false;
	}
	
	@Override
	public void apply(StatementPattern node) {
		Var s = node.getSubjectVar();
		Var p = node.getPredicateVar();
		Var o = node.getObjectVar();
		Var c = node.getContextVar();
		node.replaceWith(
			new Union(
				node.clone(),
				getChain(s, o, c)));
	}
	
	private TupleExpr getChain(Var subject, Var object, Var context) {
		TupleExpr ret;
		if (chain.size() == 1) {
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
			p  = new ConstVar(vf.createURI(chain.get(0)));
			p2 = new ConstVar(vf.createURI(chain.get(1)));
			o  = new Var();
			o.setName(p.getName() + "-0");
			o.setAnonymous(true);
			left  = new StatementPattern(subject, p, o, context);
			right = new StatementPattern(o, p2, object, context);
			join = new Join(left, right);
			ret = join;
			for (int i=2; i < chain.size(); i++) {
				p = new ConstVar(vf.createURI(chain.get(i)));
				o = new Var();
				o.setName(p.getName() + "-" + i);
				o.setAnonymous(true);
				right.setObjectVar(o);
				left = right;
				right = new StatementPattern(o, p, object);
				newjoin = new Join(left, right);
				join.setRightArg(newjoin);
				join = newjoin;
			}
		}
		return ret;
	}
	
}
