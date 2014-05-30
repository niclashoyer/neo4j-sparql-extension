
package de.unikiel.inf.comsys.neo4j;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.IOException;
import java.net.ServerSocket;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.server.CommunityNeoServer;
import org.neo4j.server.helpers.CommunityServerBuilder;

public class RDFServerExtensionTest {
	protected static CommunityNeoServer server;
	protected static GraphDatabaseService db;

	@BeforeClass
	public static void setUp() throws IOException {
		int port;
		try (final ServerSocket serverSocket = new ServerSocket(0)) {
			port = serverSocket.getLocalPort();
		}
		server = CommunityServerBuilder.server().onPort(port).withThirdPartyJaxRsPackage("de.unikiel.inf.comsys.neo4j", "/rdf").build();
		server.start();
		db = server.getDatabase().getGraph();
	}

	@AfterClass
	public static void tearDown() {
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
	
}
