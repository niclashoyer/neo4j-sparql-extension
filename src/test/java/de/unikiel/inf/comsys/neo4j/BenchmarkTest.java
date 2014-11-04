package de.unikiel.inf.comsys.neo4j;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.sun.jersey.api.client.ClientResponse;
import de.unikiel.inf.comsys.neo4j.http.RDFMediaType;
import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

@BenchmarkOptions(callgc = false, benchmarkRounds = 50, warmupRounds = 10)

public class BenchmarkTest extends RDFServerExtensionTest {
	
	@Rule
	public TestRule benchmarkRun = new BenchmarkRule();
	
	@BeforeClass
	public static void setUp() throws Exception {
		RDFServerExtensionTest.setUp("/kai/data.ttl", "/kai/kai-tbox.rdf");
	}
	
	private void runquery(String name, boolean inf) throws IOException {
		ClientResponse res;
		if (inf) {
			res = request("rdf/query/inference")
				.accept(RDFMediaType.SPARQL_RESULTS_JSON)
				.entity(getQueryAsString("/kai/" + name + ".sparql"))
				.type(RDFMediaType.SPARQL_QUERY)
				.post(ClientResponse.class);
		} else {
			res = request("rdf/query")
				.accept(RDFMediaType.SPARQL_RESULTS_JSON)
				.entity(getQueryAsString("/kai/" + name + ".sparql"))
				.type(RDFMediaType.SPARQL_QUERY)
				.post(ClientResponse.class);
		}
		assertEquals("Should return 200 response code", 200, res.getStatus());
	}

	@Test
	public void query1() throws IOException { runquery("q1", false); }
	@Test
	public void queryi1() throws IOException { runquery("q1", true); }
	@Test
	public void query2() throws IOException { runquery("q2", false); }
	@Test
	public void queryi2() throws IOException { runquery("q2", true); }
	@Test
	public void query10() throws IOException { runquery("q10", false); }
	@Test
	public void queryi10() throws IOException { runquery("q10", true); }
	
}
