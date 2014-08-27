package de.unikiel.inf.comsys.neo4j;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.representation.Form;
import de.unikiel.inf.comsys.neo4j.http.RDFMediaType;
import javax.ws.rs.core.MediaType;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class SPARQLUpdateTest extends RDFServerExtensionTest {
	
	@BeforeClass
	public static void setUp() throws Exception {
		RDFServerExtensionTest.setUp("/sp2b.n3");
	}

	@Test
	public void empty() {
		ClientResponse res;
		res = request("rdf/update")
			.get(ClientResponse.class);
		assertEquals("Should return 405 response code", 405, res.getStatus());
		Form f = new Form();
		f.add("query", "");
		res = request("rdf/update")
			.type(MediaType.APPLICATION_FORM_URLENCODED)
			.entity(f)
			.post(ClientResponse.class);
		System.out.println(res.getEntity(String.class));
		assertEquals("Should return 400 response code", 400, res.getStatus());
		res = request("rdf/update")
			.type(RDFMediaType.SPARQL_UPDATE)
			.post(ClientResponse.class);
		assertEquals("Should return 200 response code", 200, res.getStatus());
	}
	
	@Test
	public void malformed() {
		ClientResponse res;
		res = request("rdf/update")
			.type(RDFMediaType.SPARQL_UPDATE)
			.entity("foobar")
			.post(ClientResponse.class);
		assertEquals("Should return 400 response code", 400, res.getStatus());
	}
	
}
