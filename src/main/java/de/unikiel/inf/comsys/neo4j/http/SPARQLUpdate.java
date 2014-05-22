
package de.unikiel.inf.comsys.neo4j.http;

import java.io.InputStream;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.SailConnection;

public class SPARQLUpdate extends AbstractSailsResource {
	
	public SPARQLUpdate(SailConnection sc, ValueFactory vf) {
		super(sc, vf);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response updatePOSTEncoded(
			@FormParam("update") String query,
			@FormParam("using-graph-uri") List<String> defgraphs,
			@FormParam("using-named-graph-uri") List<String> namedgraphs) {
		return Response.status(Response.Status.NOT_IMPLEMENTED).build();
	}

	@POST
	@Consumes("application/sparql-update")
	public Response updatePOSTDirect(
			@FormParam("using-graph-uri") List<String> defgraphs,
			@FormParam("using-named-graph-uri") List<String> namedgraphs,
			InputStream query) {
		return Response.status(Response.Status.NOT_IMPLEMENTED).build();
	}
}
