
package de.unikiel.inf.comsys.neo4j.inference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.Dataset;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.algebra.TupleExpr;
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
		Dataset ds = parsed.getDataset();
		RuleTransformationVisitor visitor =
				new RuleTransformationVisitor(vf, rules);
		expr.visit(visitor);
		return new SailTupleExprQuery(
				new ParsedTupleQuery(expr), conn);
	}
	
}
