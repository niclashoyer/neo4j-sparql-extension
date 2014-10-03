
package de.unikiel.inf.comsys.neo4j.inference;

import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.repository.sail.SailGraphQuery;
import org.openrdf.repository.sail.SailRepositoryConnection;

public class SailGraphExprQuery extends SailGraphQuery {

	public SailGraphExprQuery(ParsedGraphQuery graphQuery, SailRepositoryConnection sailConnection) {
		super(graphQuery, sailConnection);
	}
	
}
