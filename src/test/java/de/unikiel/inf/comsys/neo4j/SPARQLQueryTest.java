package de.unikiel.inf.comsys.neo4j;

import com.sun.jersey.api.client.ClientResponse;
import static org.junit.Assert.*;
import org.junit.Test;

public class SPARQLQueryTest extends RDFServerExtensionTest {
	
	public SPARQLQueryTest() {
	}

	@Test
	public void query() {
		ClientResponse res = request("rdf/query").queryParam("query",
				"select ?s ?p ?o where {?s ?p ?o}")
				.get(ClientResponse.class);
		System.out.println(res);
		assertEquals("Should return 200 response code", 200, res.getStatus());
	}
	
}
