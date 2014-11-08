
package de.unikiel.inf.comsys.neo4j.inference.sail;

import de.unikiel.inf.comsys.neo4j.inference.QueryRewriter;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.repository.sail.SailGraphQuery;
import org.openrdf.repository.sail.SailRepositoryConnection;

/**
 * A subclass of {@link SailGraphQuery} with a public constructor to
 * pass in a graph query containing a tuple expression.
 * 
 * The original constructor of {@link SailGraphExprQuery} is protected, thus
 * it is not possible to create a new graph query from a parsed query
 * that is used to create a query from a {@link TupleExpr}.
 * 
 * @see QueryRewriter
 */
public class SailGraphExprQuery extends SailGraphQuery {

	public SailGraphExprQuery(ParsedGraphQuery graphQuery, SailRepositoryConnection sailConnection) {
		super(graphQuery, sailConnection);
	}
	
}
