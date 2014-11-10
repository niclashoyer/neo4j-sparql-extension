package de.unikiel.inf.comsys.neo4j;

/*
 * #%L
 * neo4j-sparql-extension
 * %%
 * Copyright (C) 2014 Niclas Hoyer
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
		f.add("update", "");
		res = request("rdf/update")
			.type(MediaType.APPLICATION_FORM_URLENCODED)
			.entity(f)
			.post(ClientResponse.class);
		assertEquals("Should return 400 response code", 400, res.getStatus());
		res = request("rdf/update")
			.type(RDFMediaType.SPARQL_UPDATE)
			.post(ClientResponse.class);
		assertEquals("Should return 200 response code", 400, res.getStatus());
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
	
	@Test
	public void insert() {
		ClientResponse res;
		String query =
			"PREFIX : <http://example.com/> " +
			"INSERT DATA { :test a :Test }";
		res = request("rdf/update")
			.type(RDFMediaType.SPARQL_UPDATE)
			.entity(query)
			.post(ClientResponse.class);
		System.out.println(res.getEntity(String.class));
		assertEquals("Should return 200 response code", 200, res.getStatus());
	}
	
}
