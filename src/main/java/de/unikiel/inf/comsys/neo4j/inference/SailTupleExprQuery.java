
package de.unikiel.inf.comsys.neo4j.inference;

import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.repository.sail.SailTupleQuery;

public class SailTupleExprQuery extends SailTupleQuery {

	public SailTupleExprQuery(ParsedTupleQuery tupleQuery, SailRepositoryConnection sailConnection) {
		super(tupleQuery, sailConnection);
	}
	
}
