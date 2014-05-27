
package de.unikiel.inf.comsys.neo4j.http;

import java.nio.charset.Charset;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class SPARQLUpdate extends AbstractSailsResource {
	
	public SPARQLUpdate(RepositoryConnection conn) {
		super(conn);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response updatePOSTEncoded(
			@FormParam("update") String query,
			@FormParam("using-graph-uri") List<String> defgraphs,
			@FormParam("using-named-graph-uri") List<String> namedgraphs) {
		return handleUpdate(query, defgraphs, namedgraphs);
	}

	@POST
	@Consumes(RDFMediaType.SPARQL_UPDATE)
	public Response updatePOSTDirect(
			@QueryParam("using-graph-uri") List<String> defgraphs,
			@QueryParam("using-named-graph-uri") List<String> namedgraphs,
			String query) {
		return handleUpdate(query, defgraphs, namedgraphs);
	}
	
	private Response handleUpdate(
			String query,
			List<String> defgraphs,
			List<String> namedgraphs) {
		try {
			Update update = conn.prepareUpdate(QueryLanguage.SPARQL, query);
			update.execute();
			return Response.ok().build();
		} catch (MalformedQueryException ex) {
			String str = ex.getMessage();
			return Response.status(Response.Status.BAD_REQUEST).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		} catch (RepositoryException | UpdateExecutionException ex) {
			throw new WebApplicationException(ex);
		}
	}
}
