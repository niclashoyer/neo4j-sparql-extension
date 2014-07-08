
package de.unikiel.inf.comsys.neo4j.inference.rules;

import de.unikiel.inf.comsys.neo4j.inference.Rule;
import java.util.LinkedList;
import java.util.List;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;

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
	
	protected String getURIString(Var v) {
		if (v.isConstant()) {
			Value val = v.getValue();
			if (val instanceof URI) {
				URI uri = (URI) val;
				return uri.stringValue();
			}
		}
		return null;
	}
	
	protected String getSubject(StatementPattern sp) {
		return getURIString(sp.getSubjectVar());
	}
	
	protected String getPredicate(StatementPattern sp) {
		return getURIString(sp.getPredicateVar());
	}
	
	protected String getObject(StatementPattern sp) {
		return getURIString(sp.getObjectVar());
	}
	
	protected String getContext(StatementPattern sp) {
		return getURIString(sp.getContextVar());
	}
	
}
