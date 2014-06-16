
package de.unikiel.inf.comsys.neo4j.inference.visitor;

import de.unikiel.inf.comsys.neo4j.inference.QueryTransformation;
import de.unikiel.inf.comsys.neo4j.inference.ValueFactoryTransformation;
import de.unikiel.inf.comsys.neo4j.inference.algebra.ConstVar;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

public class SymmetricPropertyTransformation extends QueryModelVisitorBase implements
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
				if (uri.stringValue().equals("http://kai.uni-kiel.de/hasParent")) {
					Var p2 = new ConstVar(vf.createURI("http://kai.uni-kiel.de/hasChild"));
					node.replaceWith(
						new Union(
							node.clone(),
							new StatementPattern(
									node.getObjectVar(),
									p2,
									node.getSubjectVar())));
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
