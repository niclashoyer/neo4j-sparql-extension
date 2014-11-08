
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

import com.nebhale.jsonpath.JsonPath;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.ServerSocket;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.server.CommunityNeoServer;
import org.neo4j.server.helpers.CommunityServerBuilder;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

public class RDFServerExtensionTest {
	protected static CommunityNeoServer server;
	protected static RepositoryConnection conn;

	@BeforeClass
	public static void setUp() throws IOException, RepositoryException, RDFParseException {
		int port;
		try (final ServerSocket serverSocket = new ServerSocket(0)) {
			port = serverSocket.getLocalPort();
		}
		server = CommunityServerBuilder.server()
			.onPort(port)
			.withThirdPartyJaxRsPackage("de.unikiel.inf.comsys.neo4j", "/rdf")
			.build();
		server.start();
		GraphDatabaseService db = server.getDatabase().getGraph();
		Repository rep = RepositoryRegistry.getInstance(db).getRepository();
		conn = rep.getConnection();
		InputStream testdata = RDFServerExtensionTest.class.getResourceAsStream("/sp2b.n3");
		conn.add(testdata, "http://example.com/", RDFFormat.N3);
	}

	@AfterClass
	public static void tearDown() throws RepositoryException {
		try {
			Thread.sleep(3000l);
		} catch (InterruptedException ex) {
		}
		conn.close();
		server.stop();
	}

	public RDFServerExtensionTest() {
	}

	protected Client request() {
		DefaultClientConfig defaultClientConfig = new DefaultClientConfig();
		defaultClientConfig.getClasses().add(JacksonJsonProvider.class);
		return Client.create(defaultClientConfig);
	}

	protected WebResource request(String path) {
		return request().resource(server.baseUri().toString() + path);
	}
	
	protected String getQueryAsString(String file) throws IOException {
		InputStream in = RDFServerExtensionTest.class.getResourceAsStream(file);
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer, "UTF-8");
		return writer.toString();
	}
	
	protected <T> T getJsonValue(ClientResponse res, String path, Class<T> clazz) {
		String json = res.getEntity(String.class);
		return JsonPath.read(path, json, clazz);
	}
	
	protected String getType(ClientResponse res) {
		return res.getType().getType() + "/" + res.getType().getSubtype();
	}
}
