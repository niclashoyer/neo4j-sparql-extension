
package de.unikiel.inf.comsys.neo4j.inference.rules;

import de.unikiel.inf.comsys.neo4j.inference.Rule;
import java.util.List;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;

public class TransitiveObjectProperty implements Rule {
	
	private final ObjectPropertyChain prop;
	
	public TransitiveObjectProperty(String op) {
		this.prop = new ObjectPropertyChain(op, op, op);
	}

	@Override
	public boolean canApply(StatementPattern node) {
		return prop.canApply(node);
	}

	@Override
	public List<QueryModelNode> apply(StatementPattern node) {
		return prop.apply(node);
	}

	@Override
	public void setValueFactory(ValueFactory vf) {
		prop.setValueFactory(vf);
	}
	
	@Override
	public String toString() {
		return "TransitiveObjectProperty(" + prop.toString() + ")";
	}
	
}
