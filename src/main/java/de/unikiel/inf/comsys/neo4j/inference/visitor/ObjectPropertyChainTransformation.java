
package de.unikiel.inf.comsys.neo4j.inference.visitor;

import de.unikiel.inf.comsys.neo4j.inference.QueryTransformation;
import de.unikiel.inf.comsys.neo4j.inference.ValueFactoryTransformation;
import de.unikiel.inf.comsys.neo4j.inference.algebra.ConstVar;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

public class ObjectPropertyChainTransformation extends QueryModelVisitorBase implements
		ValueFactoryTransformation, QueryTransformation {

	protected ValueFactory vf;
	
	@Override
	public void meet(StatementPattern node) throws Exception {
		Var s = node.getSubjectVar();
		Var p = node.getPredicateVar();
		Var o = node.getObjectVar();
		Var c = node.getContextVar();
		if (p.isConstant()) {
			Value val = p.getValue();
			if (val instanceof URI) {
				URI uri = (URI) val;
				if (uri.stringValue().equals("http://kai.uni-kiel.de/hasGrandparent")) {
					Var p2 = new ConstVar(vf.createURI("http://kai.uni-kiel.de/hasParent"));
					Var o2 = new Var();
					o2.setName(p.getName() + "-0");
					o2.setAnonymous(true);
					node.replaceWith(
						new Union(
							node.clone(),
							new Join(
								new StatementPattern(s, p2, o2, c),
								new StatementPattern(o2, p2, o, c))));
				}
			}
		}
		super.meet(node);
	}

	@Override
	public void setValueFactory(ValueFactory vf) {
		this.vf = vf;
	}
	
}
