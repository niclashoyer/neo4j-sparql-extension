package de.unikiel.inf.comsys.neo4j;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.representation.Form;
import de.unikiel.inf.comsys.neo4j.http.RDFMediaType;
import java.io.IOException;
import java.util.List;
import javax.ws.rs.core.MediaType;
import static org.junit.Assert.*;
import org.junit.Test;

public class SPARQLQueryTest extends RDFServerExtensionTest {
	
	public SPARQLQueryTest() {
	}

	@Test
	public void empty() {
		ClientResponse res;
		res = request("rdf/query").queryParam("query", "")
			.get(ClientResponse.class);
		assertEquals("Should return 400 response code", 400, res.getStatus());
		res = request("rdf/query")
			.get(ClientResponse.class);
		assertEquals("Should return 400 response code", 400, res.getStatus());
	}
	
	@Test
	public void queryParam() {
		ClientResponse res = request("rdf/query").queryParam("query",
				"select ?s ?p ?o where {?s ?p ?o} limit 10")
				.get(ClientResponse.class);
		List<?> bindings = getJsonValue(res, "$.results.bindings", List.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
		assertEquals("Should return 10 results", 10, bindings.size());
	}
	
	@Test
	public void queryPost() throws IOException {
		ClientResponse res = request("rdf/query")
			.accept(RDFMediaType.SPARQL_RESULTS_JSON)
			.entity(getQueryAsString("/q1.sparql"))
			.type(RDFMediaType.SPARQL_QUERY)
			.post(ClientResponse.class);
		String yr = getJsonValue(res, "$.results.bindings[0].yr.value", String.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
		assertEquals("Should return 1940", "1940", yr);
	}
	
	@Test
	public void queryPostEncoded() throws IOException {
		Form f = new Form();
		f.add("query", getQueryAsString("/q1.sparql"));
		ClientResponse res = request("rdf/query")
			.accept(RDFMediaType.SPARQL_RESULTS_JSON)
			.type(MediaType.APPLICATION_FORM_URLENCODED)
			.entity(f)
			.post(ClientResponse.class);
		String yr = getJsonValue(res, "$.results.bindings[0].yr.value", String.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
		assertEquals("Should return 1940", "1940", yr);
	}
	
	@Test
	public void queryResultTypes() {
		ClientResponse res;
		res = request("rdf/query").queryParam("query",
				"select ?s ?p ?o where {?s ?p ?o} limit 5")
				.accept(RDFMediaType.SPARQL_RESULTS_JSON)
				.get(ClientResponse.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
		assertEquals("Should return " + RDFMediaType.SPARQL_RESULTS_JSON,
				RDFMediaType.SPARQL_RESULTS_JSON, getType(res));
		res = request("rdf/query").queryParam("query",
				"select ?s ?p ?o where {?s ?p ?o} limit 5")
				.accept(RDFMediaType.SPARQL_RESULTS_XML)
				.get(ClientResponse.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
		assertEquals("Should return " + RDFMediaType.SPARQL_RESULTS_XML,
				RDFMediaType.SPARQL_RESULTS_XML, getType(res));
		res = request("rdf/query").queryParam("query",
				"select ?s ?p ?o where {?s ?p ?o} limit 5")
				.accept(RDFMediaType.SPARQL_RESULTS_CSV)
				.get(ClientResponse.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
		assertEquals("Should return " + RDFMediaType.SPARQL_RESULTS_CSV,
				RDFMediaType.SPARQL_RESULTS_CSV, getType(res));
		res = request("rdf/query").queryParam("query",
				"select ?s ?p ?o where {?s ?p ?o} limit 5")
				.accept(RDFMediaType.SPARQL_RESULTS_TSV)
				.get(ClientResponse.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
		assertEquals("Should return " + RDFMediaType.SPARQL_RESULTS_TSV,
				RDFMediaType.SPARQL_RESULTS_TSV, getType(res));
	}
	
	@Test
	public void queryConstruct() {
		ClientResponse res;
		res = request("rdf/query").queryParam("query",
				"construct {?s ?p ?o} where {?s ?p ?o} limit 5")
				.accept(RDFMediaType.SPARQL_RESULTS_JSON)
				.get(ClientResponse.class);
		assertEquals("Should return 406 response code", 406, res.getStatus());
		res = request("rdf/query").queryParam("query",
				"construct {?s ?p ?o} where {?s ?p ?o} limit 5")
				.accept(RDFMediaType.RDF_TURTLE)
				.get(ClientResponse.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
		assertEquals("Should return " + RDFMediaType.RDF_TURTLE,
				RDFMediaType.RDF_TURTLE, getType(res));
	}
}
