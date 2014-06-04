
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
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class RDFServerExtensionTest {
	protected static CommunityNeoServer server;
	protected static RepositoryConnection conn;

	@BeforeClass
	public static void setUp() throws IOException, RepositoryException {
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
	}

	@AfterClass
	public static void tearDown() throws RepositoryException {
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
	
}
