
package de.unikiel.inf.comsys.neo4j.inference.sail;

import de.unikiel.inf.comsys.neo4j.inference.QueryRewriter;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.repository.sail.SailTupleQuery;

/**
 * A subclass of {@link SailTupleQuery} with a public constructor to
 * pass in a tuple query containing a tuple expression.
 * 
 * The original constructor of {@link SailTupleExprQuery} is protected, thus
 * it is not possible to create a new tuple query from a parsed query
 * that is used to create a query from a {@link TupleExpr}.
 * 
 * @see QueryRewriter
 */
public class SailTupleExprQuery extends SailTupleQuery {

	public SailTupleExprQuery(ParsedTupleQuery tupleQuery, SailRepositoryConnection sailConnection) {
		super(tupleQuery, sailConnection);
	}
	
}
