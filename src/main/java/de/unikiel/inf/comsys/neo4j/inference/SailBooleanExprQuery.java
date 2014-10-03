
package de.unikiel.inf.comsys.neo4j.inference;

import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.repository.sail.SailBooleanQuery;
import org.openrdf.repository.sail.SailRepositoryConnection;

public class SailBooleanExprQuery extends SailBooleanQuery {

	public SailBooleanExprQuery(ParsedBooleanQuery booleanQuery, SailRepositoryConnection sailConnection) {
		super(booleanQuery, sailConnection);
	}
	
}
