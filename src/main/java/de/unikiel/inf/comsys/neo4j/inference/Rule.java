
package de.unikiel.inf.comsys.neo4j.inference;

import java.util.List;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;

public interface Rule {
	public void setValueFactory(ValueFactory vf);
	public boolean canApply(StatementPattern node);
	public void apply(StatementPattern node);
	public List<QueryModelNode> getNextVisits();
}
