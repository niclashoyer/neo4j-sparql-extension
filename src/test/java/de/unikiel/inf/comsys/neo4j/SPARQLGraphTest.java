package de.unikiel.inf.comsys.neo4j;

import com.sun.jersey.api.client.ClientResponse;
import de.unikiel.inf.comsys.neo4j.http.RDFMediaType;
import javax.ws.rs.core.MediaType;
import static org.junit.Assert.*;
import org.junit.Test;

public class SPARQLGraphTest extends RDFServerExtensionTest {
	
	public SPARQLGraphTest() {
	}

	@Test
	public void empty() {
		ClientResponse res;
		res = request("rdf/graph").post(ClientResponse.class);
		assertEquals("Should return 400 response code", 400, res.getStatus());
		res = request("rdf/graph?default").post(ClientResponse.class);
		assertEquals("Should return 400 response code", 400, res.getStatus());
		res = request("rdf/graph?default")
			.type(RDFMediaType.RDF_TURTLE)
			.post(ClientResponse.class);
		assertEquals("Should return 200 response code", 204, res.getStatus());
	}
	
	@Test
	public void malformed() {
		ClientResponse res;
		res = request("rdf/graph?default")
			.type(RDFMediaType.RDF_TURTLE)
			.entity("foobar")
			.post(ClientResponse.class);
		assertEquals("Should return 400 response code", 400, res.getStatus());
	}
	
	@Test
	public void indirect() {
		ClientResponse res;
		res = request("rdf/graph")
			.queryParam("graph", "http://example.com")
			.type(RDFMediaType.RDF_TURTLE)
			.entity(SPARQLGraphTest.class.getResourceAsStream("/ex.ttl"))
			.put(ClientResponse.class);
		assertEquals("Should return 204 response code", 204, res.getStatus());
		res = request("rdf/graph")
			.queryParam("graph", "http://example.com")
			.type(RDFMediaType.RDF_TURTLE)
			.entity(SPARQLGraphTest.class.getResourceAsStream("/ex.ttl"))
			.post(ClientResponse.class);
		assertEquals("Should return 204 response code", 204, res.getStatus());
		res = request("rdf/graph")
			.queryParam("graph", "http://example.com")
			.accept(RDFMediaType.RDF_TURTLE)
			.get(ClientResponse.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
		res = request("rdf/graph")
			.queryParam("graph", "http://example.com")
			.delete(ClientResponse.class);
		assertEquals("Should return 204 response code", 204, res.getStatus());
		res = request("rdf/graph")
			.queryParam("graph", "http://example.com")
			.accept(RDFMediaType.RDF_TURTLE)
			.get(ClientResponse.class);
		assertEquals("Should return 404 response code", 404, res.getStatus());
	}
	
	@Test
	public void direct() {
		ClientResponse res;
		res = request("rdf/graph/example")
			.type(RDFMediaType.RDF_TURTLE)
			.entity(SPARQLGraphTest.class.getResourceAsStream("/ex.ttl"))
			.put(ClientResponse.class);
		assertEquals("Should return 204 response code", 204, res.getStatus());
		res = request("rdf/graph/example")
			.type(RDFMediaType.RDF_TURTLE)
			.entity(SPARQLGraphTest.class.getResourceAsStream("/ex.ttl"))
			.post(ClientResponse.class);
		assertEquals("Should return 204 response code", 204, res.getStatus());
		res = request("rdf/graph/example")
			.accept(RDFMediaType.RDF_TURTLE)
			.get(ClientResponse.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
		res = request("rdf/graph/example")
			.delete(ClientResponse.class);
		assertEquals("Should return 204 response code", 204, res.getStatus());
		res = request("rdf/graph/example")
			.accept(RDFMediaType.RDF_TURTLE)
			.get(ClientResponse.class);
		assertEquals("Should return 404 response code", 404, res.getStatus());
	}
	
	@Test
	public void defaultGraph() {
		ClientResponse res;
		res = request("rdf/graph?default")
			.type(RDFMediaType.RDF_TURTLE)
			.entity(SPARQLGraphTest.class.getResourceAsStream("/ex.ttl"))
			.put(ClientResponse.class);
		assertEquals("Should return 204 response code", 204, res.getStatus());
		res = request("rdf/graph?default")
			.type(RDFMediaType.RDF_TURTLE)
			.entity(SPARQLGraphTest.class.getResourceAsStream("/ex.ttl"))
			.post(ClientResponse.class);
		assertEquals("Should return 204 response code", 204, res.getStatus());
		res = request("rdf/graph?default")
			.accept(RDFMediaType.RDF_TURTLE)
			.get(ClientResponse.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
		res = request("rdf/graph?default")
			.delete(ClientResponse.class);
		assertEquals("Should return 204 response code", 204, res.getStatus());
		res = request("rdf/graph?default")
			.accept(RDFMediaType.RDF_TURTLE)
			.get(ClientResponse.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
		res = request("rdf/graph?default")
			.accept(MediaType.APPLICATION_OCTET_STREAM)
			.get(ClientResponse.class);
		assertEquals("Should return 406 response code", 406, res.getStatus());
	}
	
}
