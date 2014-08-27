package de.unikiel.inf.comsys.neo4j;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.sun.jersey.api.client.ClientResponse;
import de.unikiel.inf.comsys.neo4j.http.RDFMediaType;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

public class BenchmarkTest extends RDFServerExtensionTest {
	
	@Rule
	public TestRule benchmarkRun = new BenchmarkRule();
	
	@BeforeClass
	public static void setUp() throws Exception {
		RDFServerExtensionTest.setUp("/kai/data.ttl", "/kai/kai-tbox.rdf");
	}
	
	@Test
	public void query() throws IOException {
		ClientResponse res = request("rdf/query")
			.accept(RDFMediaType.SPARQL_RESULTS_JSON)
			.entity(getQueryAsString("/kai/q1.sparql"))
			.type(RDFMediaType.SPARQL_QUERY)
			.post(ClientResponse.class);
		List<String> b = getJsonValue(res, "$.results.bindings", List.class);
		System.out.println("[RESULTS] " + b.size());
		assertEquals("Should return 200 response code", 200, res.getStatus());
	}
	
	@Test
	public void queryInference() throws IOException {
		ClientResponse res = request("rdf/query")
			.queryParam("inference", "true")
			.accept(RDFMediaType.SPARQL_RESULTS_JSON)
			.entity(getQueryAsString("/kai/q1.sparql"))
			.type(RDFMediaType.SPARQL_QUERY)
			.post(ClientResponse.class);
		
		List<String> b = getJsonValue(res, "$.results.bindings", List.class);
		System.out.println(b);
		System.out.println("[RESULTS INF] " + b.size());
		assertEquals("Should return 200 response code", 200, res.getStatus());
	}
	
}
