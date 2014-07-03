
package de.unikiel.inf.comsys.neo4j.inference.rules;

import de.unikiel.inf.comsys.neo4j.inference.Rule;
import java.util.LinkedList;
import java.util.List;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.QueryModelNode;

public abstract class AbstractRule implements Rule {

	protected ValueFactory vf;
	protected List<QueryModelNode> next;
	
	@Override
	public void setValueFactory(ValueFactory vf) {
		this.vf = vf;
		this.next = new LinkedList<>();
	}

	protected void visitNext(QueryModelNode node) {
		next.add(node);
	}
	
	@Override
	public List<QueryModelNode> getNextVisits() {
		return next;
	}
	
}
