
package de.unikiel.inf.comsys.neo4j.inference.sail;

import de.unikiel.inf.comsys.neo4j.inference.QueryRewriter;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.repository.sail.SailBooleanQuery;
import org.openrdf.repository.sail.SailRepositoryConnection;

/**
 * A subclass of {@link SailBooleanQuery} with a public constructor to
 * pass in a boolean query containing a tuple expression.
 * 
 * The original constructor of {@link SailBooleanQuery} is protected, thus
 * it is not possible to create a new boolean query from a parsed query
 * that is used to create a query from a {@link TupleExpr}.
 * 
 * @see QueryRewriter
 */
public class SailBooleanExprQuery extends SailBooleanQuery {

	public SailBooleanExprQuery(ParsedBooleanQuery booleanQuery, SailRepositoryConnection sailConnection) {
		super(booleanQuery, sailConnection);
	}
	
}
