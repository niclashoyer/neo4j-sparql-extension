package de.unikiel.inf.comsys.neo4j.inference;

import de.unikiel.inf.comsys.neo4j.inference.rules.Rule;
import de.unikiel.inf.comsys.neo4j.inference.rules.Rules;
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

/**
 * A query rewriting implementation that takes a SPARQL query and rewrites
 * according to a OWL-2 TBox to include inference.
 *
 * The query rewriter uses a set of rules to transform SPARQL algebra
 * expressions. To get a set of rules from an OWL-2 TBox use the {@link Rules}
 * class.
 */
public class QueryRewriter {

	private SailRepositoryConnection conn;
	private ValueFactory vf;
	private ArrayList<Rule> rules;

	/**
	 * Create a new query rewriter.
	 *
	 * @param conn the repository connection used for query execution
	 */
	public QueryRewriter(SailRepositoryConnection conn) {
		this(conn, Collections.EMPTY_LIST);
	}

	/**
	 * Create a new query rewriter with a set of rules.
	 *
	 * @param conn the repository connection used for query execution
	 * @param rules the set of rules used for rewriting
	 */
	public QueryRewriter(
			SailRepositoryConnection conn,
			Rule... rules) {
		this(conn, Arrays.asList(rules));
	}

	/**
	 * Create a new query rewriter with a set of rules.
	 *
	 * @param conn the repository connection used for query execution
	 * @param rules the set of rules used for rewriting
	 */
	public QueryRewriter(
			SailRepositoryConnection conn,
			List<Rule> rules) {
		this.rules = new ArrayList<>(rules);
		this.conn = conn;
		this.vf = conn.getValueFactory();
		for (Rule r : rules) {
			r.setValueFactory(vf);
		}
	}

	/**
	 * Rewrite a given query to add inference.
	 *
	 * @param ql the query language used for the query
	 * @param query the query to rewrite
	 * @return rewritten query that includes inference
	 * @throws MalformedQueryException if the query is malformed
	 * @throws RepositoryException if there was a problem while rewriting
	 */
	public Query rewrite(QueryLanguage ql, String query)
			throws MalformedQueryException, RepositoryException {
		return rewrite(ql, query, null);
	}

	/**
	 * Rewrite a given query to add inference.
	 *
	 * @param ql the query language used for the query
	 * @param query the query to rewrite
	 * @param baseuri a base URI to use for the query
	 * @return rewritten query that includes inference
	 * @throws MalformedQueryException if the query is malformed
	 * @throws RepositoryException if there was a problem while rewriting
	 */
	public Query rewrite(QueryLanguage ql, String query, String baseuri)
			throws MalformedQueryException, RepositoryException {
		// parse query using Sesame
		QueryParserFactory f = QueryParserRegistry.getInstance().get(ql);
		QueryParser parser = f.getParser();
		ParsedQuery parsed = parser.parseQuery(query, baseuri);
		// get SPARQL algebra expression from parsed query
		TupleExpr expr = parsed.getTupleExpr();
		// rewrite query using visitor pattern
		RuleTransformationVisitor visitor
				= new RuleTransformationVisitor(rules);
		expr.visit(visitor);
		// return new query based on rewritten algebra expression
		return getExprQuery(parsed, expr);
	}

	/**
	 * Creates a new query based on a tuple expression and original query. The
	 * new query will have the same type null
	 * ({@link org.openrdf.query.TupleQuery},
	 * {@link org.openrdf.query.GraphQuery} or
	 * {@link org.openrdf.query.BooleanQuery}) as the given original query.
	 *
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
