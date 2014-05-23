package de.unikiel.inf.comsys.neo4j;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.IOException;
import java.net.ServerSocket;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.server.CommunityNeoServer;
import org.neo4j.server.helpers.CommunityServerBuilder;

public class SPARQLQueryTest {

	private static CommunityNeoServer server;
	private static GraphDatabaseService db;

	public SPARQLQueryTest() {
	}

	@BeforeClass
	public static void setUp() throws IOException {
		int port;
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			port = serverSocket.getLocalPort();
		}
		server = CommunityServerBuilder
				.server()
				.onPort(port)
				.withThirdPartyJaxRsPackage("de.unikiel.inf.comsys.neo4j", "/rdf")
				.build();
		server.start();
		db = server.getDatabase().getGraph();
	}

	@AfterClass
	public static void tearDown() {
		server.stop();
	}

	@Test
	public void query() {
		ClientResponse res = request("rdf/query").queryParam("query",
				"select ?s ?p ?o where {?s ?p ?o}")
				.get(ClientResponse.class);
		System.out.println(res);
		assertEquals("Should return 200 response code", 200, res.getStatus());
	}

	private Client request() {
		DefaultClientConfig defaultClientConfig = new DefaultClientConfig();
		defaultClientConfig.getClasses().add(JacksonJsonProvider.class);
		return Client.create(defaultClientConfig);
	}

	private WebResource request(String path) {
		return request().resource(server.baseUri().toString() + path);
	}
}
