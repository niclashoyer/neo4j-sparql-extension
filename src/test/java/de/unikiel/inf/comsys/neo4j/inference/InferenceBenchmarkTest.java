package de.unikiel.inf.comsys.neo4j.inference;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import de.unikiel.inf.comsys.neo4j.inference.rules.Rule;
import de.unikiel.inf.comsys.neo4j.inference.rules.Rules;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.TestRule;
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
import org.openrdf.query.resultio.QueryResultParseException;
import org.openrdf.query.resultio.TupleQueryResultParser;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLParserFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

@BenchmarkOptions(callgc = false, benchmarkRounds = 50, warmupRounds = 10)

@RunWith(Parameterized.class)
public class InferenceBenchmarkTest {
	
	@org.junit.Rule
	public TestRule benchmarkRun = new BenchmarkRule();
	
	private SailRepository repo;
	private SailRepositoryConnection conn;
	private ValueFactory vf;
	private final String data;
	private final String queryString;
	private TupleQueryResultParser parser;
	private QueryRewriter rewriter;
	
	private static InputStream getResource(String name) {
		if (name == null) {
			return null;
		}
		String pref = "file://";
		if (name.startsWith(pref)) {
			name = name.substring(pref.length());
		}
		return InferenceBenchmarkTest.class.getResourceAsStream(name);
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
			getResource("/kai/manifest.ttl"),
			"file:///kai/",
			RDFFormat.TURTLE);
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
			if (b.hasBinding("comment")) {
				comment = b.getValue("comment").stringValue();
			} else {
				comment = "";
			}
			if (data != null && query != null) {
				Object[] test = {name, comment, datastr, query, resultstr};
				tests.add(test);
			}
		}
		return tests;
    }
	
	public InferenceBenchmarkTest(
		String name, String comment, String data, String query,
		String expected) throws RepositoryException {
		this.data = data;
		this.queryString = query;
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
		rewriter = new QueryRewriter(conn, rules);
	}
	
	@After
	public void after() throws RepositoryException {
		conn.close();
		repo.shutDown();
	}
	
	@org.junit.Rule
    public ErrorCollector collector = new ErrorCollector();
	
	@Test
	public void rewrite() throws MalformedQueryException, RepositoryException {
		TupleQuery query = (TupleQuery) rewriter.rewrite(QueryLanguage.SPARQL, queryString);
		assertNotNull(query);
	}
	
}
