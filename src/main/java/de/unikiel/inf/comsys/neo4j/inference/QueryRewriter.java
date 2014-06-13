
package de.unikiel.inf.comsys.neo4j.inference;

import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.algebra.Extension;
import org.openrdf.query.algebra.ExtensionElem;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.ProjectionElemList;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.QueryParserFactory;
import org.openrdf.query.parser.QueryParserRegistry;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class QueryRewriter {
	
	public static void main(String [] args) throws MalformedQueryException, Exception {
		QueryRewriter rewriter = new QueryRewriter();
		String q = "PREFIX ex: <http://example.com/>\n" +
				"SELECT ?s ?o WHERE { ?s ?p ?o. BIND(ex:Foo as ?bar) }";
		System.out.println(rewriter.rewrite(
				QueryLanguage.SPARQL, q, "http://example.com"));
	}
	
	public Query rewrite(QueryLanguage ql, String query)
			throws MalformedQueryException, RepositoryException {
		return rewrite(ql, query, null);
	}
	
	public Query rewrite(QueryLanguage ql, String query, String baseuri)
			throws MalformedQueryException, RepositoryException {
		SailRepository repository = new SailRepository(new MemoryStore());
		repository.initialize();
		QueryParserFactory f = QueryParserRegistry.getInstance().get(ql);
		QueryParser parser = f.getParser();
		ParsedQuery parsed = parser.parseQuery(query, baseuri);
		System.out.println(parsed);
		Projection p1 = new Projection(
			new Extension(
				new StatementPattern(
					new Var("_s"),
					new Var("foobar",
						new URIImpl("http://example.com/hasChild")),
						new Var("_o")),
				new ExtensionElem(
					new ValueConstant(
						new URIImpl("http://example.com/hasParent")),
					"p")),
			new ProjectionElemList(
				new ProjectionElem("_o", "s"),
				new ProjectionElem("_s", "o")));
		Union u = new Union(
			new StatementPattern(
				new Var("s"),
				new Var("p"),
				new Var("o")),
			p1);
		TupleQuery squery = new SailTupleExprQuery(
				new ParsedTupleQuery(u), repository.getConnection());
		return squery;
	}
	
}
