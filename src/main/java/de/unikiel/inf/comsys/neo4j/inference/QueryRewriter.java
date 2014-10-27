
package de.unikiel.inf.comsys.neo4j.inference;

import de.unikiel.inf.comsys.neo4j.inference.sail.SailTupleExprQuery;
import de.unikiel.inf.comsys.neo4j.inference.sail.SailBooleanExprQuery;
import de.unikiel.inf.comsys.neo4j.inference.sail.SailGraphExprQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.QueryParserFactory;
import org.openrdf.query.parser.QueryParserRegistry;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepositoryConnection;

public class QueryRewriter {
	
	private SailRepositoryConnection conn;
	private ValueFactory vf;
	private ArrayList<Rule> rules;
	
	public QueryRewriter(SailRepositoryConnection conn) {
		this(conn, Collections.EMPTY_LIST);
	}
	
	public QueryRewriter(
			SailRepositoryConnection conn,
			Rule... rules) {
		this(conn, Arrays.asList(rules));
	}
	
	public QueryRewriter(
			SailRepositoryConnection conn,
			List<Rule> rules) {
		this.rules = new ArrayList<>(rules);
		this.conn = conn;
		this.vf   = conn.getValueFactory();
		for (Rule r : rules) {
			r.setValueFactory(vf);
		}
	}
	
	public Query rewrite(QueryLanguage ql, String query)
			throws MalformedQueryException, RepositoryException {
		return rewrite(ql, query, null);
	}
	
	public Query rewrite(QueryLanguage ql, String query, String baseuri)
			throws MalformedQueryException, RepositoryException,
			       RuntimeException {
		QueryParserFactory f = QueryParserRegistry.getInstance().get(ql);
		QueryParser parser = f.getParser();
		ParsedQuery parsed = parser.parseQuery(query, baseuri);
		TupleExpr expr = parsed.getTupleExpr();
		RuleTransformationVisitor visitor =
				new RuleTransformationVisitor(vf, rules);
		expr.visit(visitor);
		return getExprQuery(parsed, expr);
	}
	

	/**
	 * Creates a new query based on a tuple expression and original
	 * query.
	 * The new query will have the same type
	 * ({@link org.openrdf.query.TupleQuery},
	 * {@link org.openrdf.query.GraphQuery} or
	 * {@link org.openrdf.query.BooleanQuery}) as the given original query.
	 * @param orig the original query
	 * @param expr the expression used for the new query
	 * @return new query based on expression
	 */
	protected Query getExprQuery(ParsedQuery orig, TupleExpr expr) {
		if (orig instanceof ParsedTupleQuery) {
			return new SailTupleExprQuery(
					new ParsedTupleQuery(expr), conn);
		} else if (orig instanceof ParsedGraphQuery) {
			return new SailGraphExprQuery(
				new ParsedGraphQuery(expr), conn);
		} else {
			return new SailBooleanExprQuery(
				new ParsedBooleanQuery(expr), conn);
		}
	}
}
