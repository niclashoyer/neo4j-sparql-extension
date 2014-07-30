package de.unikiel.inf.comsys.neo4j.inference;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.QueryResultParseException;
import org.openrdf.query.resultio.TupleQueryResultParser;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLParserFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

@RunWith(Parameterized.class)
public class SPARQLInferenceTest {
	
	private SailRepository repo;
	private SailRepositoryConnection conn;
	private ValueFactory vf;
	private final String name;
	private final String comment;
	private final String data;
	private final String expected;
	private final String queryString;
	private TupleQuery query;
	private TupleQuery nonInfQuery;
	private TupleQueryResultParser parser;
	
	private static InputStream getResource(String name) {
		if (name == null) {
			return null;
		}
		String pref = "file://";
		if (name.startsWith(pref)) {
			name = name.substring(pref.length());
		}
		return SPARQLInferenceTest.class.getResourceAsStream(name);
	}
	
	private static String getResourceAsString(String name) throws IOException {
		InputStream in = getResource(name);
		if (in == null) {
			return null;
		}
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer, "UTF-8");
		return writer.toString();
	}
	
    @Parameters(name = "{0} - {1}")
    public static Iterable<Object[]> data()
			throws RepositoryException, IOException, RDFParseException,
			       MalformedQueryException, QueryEvaluationException {
		SailRepository repository = new SailRepository(new MemoryStore());
		repository.initialize();
		SailRepositoryConnection conn = repository.getConnection();
		conn.add(
			getResource("/inference/kiel/manifest.ttl"),
			"file:///inference/kiel/",
			RDFFormat.TURTLE);
		TupleQuery q = conn.prepareTupleQuery(
			QueryLanguage.SPARQL,
			getResourceAsString("/inference/queryeval.sparql"));
		TupleQueryResult r = q.evaluate();
		BindingSet b;
		String name;
		InputStream data;
		String query;
		InputStream result;
		String comment;
		ArrayList<Object[]> tests = new ArrayList<>();
		while(r.hasNext()) {
			b = r.next();
			String datastr   = b.getValue("data").stringValue();
			String resultstr = b.getValue("result").stringValue();
			name   = b.getValue("name").stringValue();
			data   = getResource(datastr);
			query  = getResourceAsString(b.getValue("query").stringValue());
			result = getResource(resultstr);
			if (b.hasBinding("comment")) {
				comment = b.getValue("comment").stringValue();
			} else {
				comment = "";
			}
			if (data != null && query != null && result != null) {
				Object[] test = {name, comment, datastr, query, resultstr};
				tests.add(test);
			}
		}
		return tests;
    }
	
	public SPARQLInferenceTest(
		String name, String comment, String data, String query,
		String expected) throws RepositoryException {
		this.name = name;
		this.comment = comment;
		this.data = data;
		this.queryString = query;
		this.expected = expected;
		this.query = null;
		this.parser = null;
	}
	
	@Before
	public void before()
			throws RepositoryException, IOException, RDFParseException,
			       MalformedQueryException, QueryResultParseException,
				   QueryResultHandlerException {
		repo = new SailRepository(new MemoryStore());
		repo.initialize();
		conn = repo.getConnection();
		vf = conn.getValueFactory();
		conn.add(getResource(data), "file://", RDFFormat.TURTLE);
		SPARQLResultsXMLParserFactory factory =
				new SPARQLResultsXMLParserFactory();
		parser = factory.getParser();
		parser.setValueFactory(vf);
		List<Rule> rules;
		rules = Rules.fromOntology(getResource(data));
		QueryRewriter rewriter = new QueryRewriter(conn, rules);
		query = (TupleQuery) rewriter.rewrite(QueryLanguage.SPARQL, queryString);
		nonInfQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		System.out.println("== QUERY (" + this.name + ") ==");
		System.out.println(nonInfQuery);
		System.out.println("== REWRITTEN QUERY (" + this.name + ") ==");
		System.out.println(query);
	}
	
	@After
	public void after() throws RepositoryException {
		conn.close();
		repo.shutDown();
	}
	
	private class QueryResult {
		private final Multiset<BindingSet> expect;
		private final Multiset<BindingSet> actual;
		private final Multiset<BindingSet> noninf;
		
		public QueryResult(
				Multiset<BindingSet> expected, Multiset<BindingSet> actual,
				Multiset<BindingSet> noninf) {
			this.expect = expected;
			this.actual = actual;
			this.noninf = noninf;
		}

		public Multiset<BindingSet> getExpected() {
			return expect;
		}

		public Multiset<BindingSet> getActual() {
			return actual;
		}
		
		public Multiset<BindingSet> getNonInferred() {
			return noninf;
		}
		
		public Multiset<BindingSet> getDiff() {
			return Multisets.difference(expect, actual);
		}
		
		@Override
		public String toString() {
			return getActual() + "\n" + getExpected();
		}
	}
	
	private QueryResult runQuery()
			throws IOException, QueryEvaluationException,
			       QueryResultParseException, TupleQueryResultHandlerException,
				   QueryResultHandlerException {
		TestResultHandler noninf = new TestResultHandler();
		TestResultHandler actual = new TestResultHandler();
		TestResultHandler expect = new TestResultHandler();
		parser.setQueryResultHandler(expect);
		parser.parseQueryResult(getResource(expected));
		nonInfQuery.evaluate(noninf);
		query.evaluate(actual);
		Multiset<BindingSet> noninfset = noninf.getSolutions();
		Multiset<BindingSet> expectset = expect.getSolutions();
		Multiset<BindingSet> actualset = actual.getSolutions();
		return new QueryResult(expectset, actualset, noninfset);
	}
	
	@org.junit.Rule
    public ErrorCollector collector = new ErrorCollector();
	
	@Test
	public void subset()
			throws QueryEvaluationException, TupleQueryResultHandlerException,
			       IOException, QueryResultParseException,
				   QueryResultHandlerException {
		QueryResult q = this.runQuery();
		assertTrue(
			q.getNonInferred() + " should be a subset of " + q.getActual(),
			Multisets.containsOccurrences(
				q.getActual(),
				q.getNonInferred()
			));
	}
	
	@Test
	public void missing()
			throws QueryEvaluationException, TupleQueryResultHandlerException,
			       IOException, QueryResultParseException,
				   QueryResultHandlerException {
		QueryResult q = this.runQuery();
		Multiset<BindingSet> missing = q.getExpected();
		missing.removeAll(q.getActual());
		for (BindingSet b : missing) {
			collector.addError(new Throwable("Missing " + b + " in result set"));
		}
	}
	
	@Test
	public void additional()
			throws QueryEvaluationException, TupleQueryResultHandlerException,
			       IOException, QueryResultParseException,
				   QueryResultHandlerException {
		QueryResult q = this.runQuery();
		Multiset<BindingSet> additional = q.getActual();
		additional.removeAll(q.getExpected());
		for (BindingSet b : additional) {
			collector.addError(new Throwable(b + " shouldn't be in result set"));
		}
	}
	
}
