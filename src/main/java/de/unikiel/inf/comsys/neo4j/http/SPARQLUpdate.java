package de.unikiel.inf.comsys.neo4j.http;

import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;

/**
 * Implementation of the "update operation" part of the SPARQL 1.1 Protocol
 * standard.
 *
 * @see <a href="http://www.w3.org/TR/sparql11-protocol/#update-operation">
 * SPARQL 1.1 Protocol
 * </a>
 */
public class SPARQLUpdate extends AbstractSailResource {

	private static final Logger logger
			= Logger.getLogger(SPARQLUpdate.class.getName());

	/**
	 * Create a new SPARQL 1.1 update resource based on a repository.
	 *
	 * @param rep the repository this resources operates on
	 */
	public SPARQLUpdate(SailRepository rep) {
		super(rep);
	}

	/**
	 * Update via URL-encoded POST.
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-protocol/#update-operation">
	 * SPARQL 1.1 Protocol
	 * </a>
	 * @param query the "update" form encoded parameter
	 * @param defgraphs the "using-graph-uri" form encoded parameter
	 * @param namedgraphs the "using-named-graph-uri" form encoded parameter
	 * @return "204 No Content", if the operation was successful
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response updatePOSTEncoded(
			@FormParam("update") String query,
			@FormParam("using-graph-uri") List<String> defgraphs,
			@FormParam("using-named-graph-uri") List<String> namedgraphs) {
		return handleUpdate(query, defgraphs, namedgraphs);
	}

	/**
	 * Update via POST directly.
	 * 
	 * @see <a href="http://www.w3.org/TR/sparql11-protocol/#update-operation">
	 * SPARQL 1.1 Protocol
	 * </a>
	 * @param defgraphs the "using-graph-uri" query parameter
	 * @param namedgraphs the "using-named-graph-uri" query parameter
	 * @param query query as string
	 * @return "204 No Content", if the operation was successful
	 */
	@POST
	@Consumes(RDFMediaType.SPARQL_UPDATE)
	public Response updatePOSTDirect(
			@QueryParam("using-graph-uri") List<String> defgraphs,
			@QueryParam("using-named-graph-uri") List<String> namedgraphs,
			String query) {
		return handleUpdate(query, defgraphs, namedgraphs);
	}

	/**
	 * Executes a SPARQL 1.1 update operation on a graph in the repository.
	 * @param query the update query
	 * @param defgraphs graph URI list for RDF dataset
	 * @param namedgraphs named graph URI list for RDF dataset
	 * @return "204 No Content", if the operation was successful
	 */
	private Response handleUpdate(
			String query,
			List<String> defgraphs,
			List<String> namedgraphs) {
		SailRepositoryConnection conn;
		try {
			conn = getConnection();
		} catch (RepositoryException ex) {
			throw new WebApplicationException(ex);
		}
		try {
			// check if query is empty
			if (query == null || query.length() == 0) {
				throw new MalformedQueryException("empty query");
			}
			// execute update query
			Update update = conn.prepareUpdate(QueryLanguage.SPARQL, query);
			logger.log(Level.FINER, "[BEGIN] Update transaction begin");
			conn.begin();
			logger.log(Level.FINER, "[EXEC] Update execution");
			update.execute();
			logger.log(Level.FINER, "[COMMIT] Update transaction commit");
			conn.commit();
			close(conn);
			return Response.ok().build();
		} catch (MalformedQueryException ex) {
			// syntax error
			String str = ex.getMessage();
			close(conn, ex);
			return Response.status(Response.Status.BAD_REQUEST).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		} catch (RepositoryException | UpdateExecutionException ex) {
			// server error
			close(conn, ex);
			throw new WebApplicationException(ex);
		}
	}
}
