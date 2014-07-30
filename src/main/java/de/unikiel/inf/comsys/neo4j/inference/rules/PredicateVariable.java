
package de.unikiel.inf.comsys.neo4j.inference.rules;

import com.google.common.base.Joiner;
import de.unikiel.inf.comsys.neo4j.inference.algebra.ConstVar;
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

public class PredicateVariable extends AbstractRule {
	
	private final List<String> predicates;
	
	public PredicateVariable(String... predicates) {
		this(Arrays.asList(predicates));
	}
	
	public PredicateVariable(List<String> predicates) {
		this.predicates = predicates;
	}
	
	@Override
	public boolean canApply(StatementPattern node) {
		Var p = node.getPredicateVar();
		return !(predicates.isEmpty() || p.isConstant()); // TODO: check if check for variable is correct
	}

	private TupleExpr listAsUnion(List<TupleExpr> unions) {
		if (unions.isEmpty()) {
			return new EmptySet();
		}
		if (unions.size() == 1) {
			return unions.get(0);
		}
		Union last;
		Union tmp;
		Union first = new Union();
		first.setLeftArg(unions.get(0));
		first.setRightArg(unions.get(1));
		last = first;
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
