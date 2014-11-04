
package de.unikiel.inf.comsys.neo4j;

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
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.server.CommunityNeoServer;
import org.neo4j.server.helpers.CommunityServerBuilder;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

public class RDFServerExtensionTest {
	protected static CommunityNeoServer server;
	protected static RepositoryConnection conn;

	public static void setUp() throws Exception {
		setUp(null, null);
	}
	
	public static void setUp(String testdatastr)
			throws Exception {
		setUp(testdatastr, null);
	}
	
	public static void setUp(String testdatastr, String tboxdatastr)
			throws Exception {
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
		if (testdatastr != null) {
			InputStream testdata =
				RDFServerExtensionTest.class.getResourceAsStream(testdatastr);
			conn.add(
				testdata,
				"http://example.com/",
				RDFFormat.forFileName(testdatastr));
		}
		if (tboxdatastr != null) {
			InputStream testdata =
				RDFServerExtensionTest.class.getResourceAsStream(tboxdatastr);
			URI ctx = conn.getValueFactory().createURI("urn:sparqlextension:tbox");
			conn.add(
				testdata,
				"http://example.com/",
				RDFFormat.forFileName(tboxdatastr),
				ctx);
		}
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
