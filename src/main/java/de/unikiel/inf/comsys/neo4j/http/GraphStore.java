
package de.unikiel.inf.comsys.neo4j.http;

import java.io.InputStream;
import java.nio.charset.Charset;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.turtle.TurtleParser;

public class GraphStore extends AbstractSailsResource {

	private final ValueFactory vf;
	
	public GraphStore(RepositoryConnection conn) {
		super(conn);
		this.vf = conn.getValueFactory();
	}
	
	@GET
	public Response graphIndirectGet(
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def) {
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@PUT
	@Consumes("text/turtle")
	public Response graphIndirectPut(
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def,
			InputStream in) {
		try {
			
			RDFParser p = new TurtleParser();
			Resource dctx = null;
			String base = "http://example.com"; // FIXME
			if (graphString != null) {
				dctx = vf.createURI(graphString);
				base = dctx.stringValue();
			}
			conn.add(in, base, RDFFormat.TURTLE, dctx);
			return Response.status(Response.Status.OK).build();
		} catch (Exception ex) {
			// DEBUG
			String str = ex.toString();
			return Response.status(500).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		}
	}
	
	@DELETE
	public Response graphIndirectDelete(
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def) {
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@POST
	public Response graphIndirectPost(
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def,
			InputStream in) {
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@GET
	@Path("/{graph}")
	public Response graphDirectGet(
			@PathParam("graph") String graphString) {
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@PUT
	@Path("/{graph}")
	public Response graphDirectPut(
			@PathParam("graph") String graphString,
			InputStream in) {
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@DELETE
	@Path("/{graph}")
	public Response graphDirectDelete(
			@PathParam("graph") String graphString) {
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@POST
	@Path("/{graph}")
	public Response graphDirectPost(
			@PathParam("graph") String graphString,
			InputStream in) {
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
}
