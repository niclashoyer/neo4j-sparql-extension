
package de.unikiel.inf.comsys.neo4j.inference;

import de.unikiel.inf.comsys.neo4j.inference.visitor.SymmetricPropertyTransformation;
import de.unikiel.inf.comsys.neo4j.inference.visitor.ObjectPropertyChainTransformation;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.Dataset;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.algebra.QueryModelVisitor;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.QueryParserFactory;
import org.openrdf.query.parser.QueryParserRegistry;
import org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.memory.MemoryStore;

public class QueryRewriter {
	
	protected SailRepository rep;
	protected ValueFactory vf;
	protected List<QueryTransformation> transformations;
	
	public QueryRewriter(SailRepository rep) {
		this(rep, Collections.EMPTY_LIST);
	}
	
	public QueryRewriter(SailRepository rep, QueryTransformation... tranformations) {
		this(rep, Arrays.asList(tranformations));
	}
	
	public QueryRewriter(SailRepository rep, List<QueryTransformation> transformations) {
		this.transformations = new LinkedList<>();
		addAll(transformations);
		this.rep = rep;
		this.vf  = rep.getValueFactory();
	}
	
	public void add(QueryTransformation tf) {
		if (tf instanceof ValueFactoryTransformation) {
			((ValueFactoryTransformation) tf).setValueFactory(vf);
		}
		this.transformations.add(tf);
	}
	
	public final void addAll(List<QueryTransformation> tfs) {
		for (QueryTransformation tf : tfs) {
			add(tf);
		}
	}
	
	public static void main(String [] args) throws MalformedQueryException, Exception {
		SailRepository repository = new SailRepository(new MemoryStore());
		repository.initialize();
		SailRepositoryConnection conn = repository.getConnection();
		ValueFactory valf = conn.getValueFactory();
		List<Statement> stmts = new LinkedList<>();
		stmts.add(valf.createStatement(
			valf.createURI("http://kai.uni-kiel.de/PersonC"),
			valf.createURI("http://kai.uni-kiel.de/hasParent"),
			valf.createURI("http://kai.uni-kiel.de/PersonB")));
		stmts.add(valf.createStatement(
			valf.createURI("http://kai.uni-kiel.de/PersonA"),
			valf.createURI("http://kai.uni-kiel.de/hasChild"),
			valf.createURI("http://kai.uni-kiel.de/PersonB")));
		stmts.add(valf.createStatement(
			valf.createURI("http://kai.uni-kiel.de/PersonA"),
			valf.createURI("http://kai.uni-kiel.de/hasChild"),
			valf.createURI("http://kai.uni-kiel.de/PersonD")));
		conn.add(stmts);
		QueryRewriter rewriter = new QueryRewriter(repository);
		rewriter.add(new ObjectPropertyChainTransformation());
		rewriter.add(new SymmetricPropertyTransformation());
		String qstr = "PREFIX : <http://kai.uni-kiel.de/>\n" +
				"SELECT ?s ?o WHERE { ?s :hasGrandparent ?o }";
		TupleQuery q = (TupleQuery) rewriter.rewrite(
				QueryLanguage.SPARQL, qstr, "http://example.com");
		q.evaluate(new SPARQLResultsTSVWriter(System.out));
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
		System.out.println(expr);
		for (QueryModelVisitor<RuntimeException> t : transformations) {
			System.out.println(t.getClass().getCanonicalName());
			expr.visit(t);
			System.out.println(expr);
		}
		return new SailTupleExprQuery(
				new ParsedTupleQuery(expr), rep.getConnection());
	}
	
}
