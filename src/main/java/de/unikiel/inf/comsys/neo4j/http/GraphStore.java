
package de.unikiel.inf.comsys.neo4j.http;

import de.unikiel.inf.comsys.neo4j.http.streams.RDFStreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Variant;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

public class GraphStore extends AbstractSailsResource {

	private final ValueFactory vf;
	
	public GraphStore(Repository rep) {
		super(rep);
		this.vf = rep.getValueFactory();
	}
	
	@GET
	@Produces({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_JSON
	})
	public Response graphIndirectGet(
			@Context Request req,
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def) {
		return handleGet(req, def, graphString);
	}
	
	@PUT
	@Consumes({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_XML
	})
	public Response graphIndirectPut(
			@Context UriInfo uriInfo,
			@HeaderParam("Content-Type") MediaType type,
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def,
			InputStream in) {
		return handleAdd(uriInfo, type, graphString, def, in, true);
	}
	
	@DELETE
	public Response graphIndirectDelete(
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def) {
		return handleClear(graphString);
	}
	
	@POST
	@Consumes({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_XML
	})
	public Response graphIndirectPost(
			@Context UriInfo uriInfo,
			@HeaderParam("Content-Type") MediaType type,
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def,
			InputStream in) {
		return handleAdd(uriInfo, type, graphString, def, in, false);
	}
	
	@GET
	@Produces({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_JSON
	})
	@Path("/{graph}")
	public Response graphDirectGet(
			@Context Request req,
			@Context UriInfo uriInfo,
			@PathParam("graph") String graphString) {
		String graphuri = uriInfo.getAbsolutePath().toASCIIString();
		return handleGet(req, null, graphuri);
	}
	
	@PUT
	@Consumes({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_XML
	})
	@Path("/{graph}")
	public Response graphDirectPut(
			@Context UriInfo uriInfo,
			@HeaderParam("Content-Type") MediaType type,
			InputStream in) {
		String graphuri = uriInfo.getAbsolutePath().toASCIIString();
		return handleAdd(uriInfo, type, graphuri, null, in, true);
	}
	
	@DELETE
	@Path("/{graph}")
	public Response graphDirectDelete(@Context UriInfo uriInfo) {
		String graphuri = uriInfo.getAbsolutePath().toASCIIString();
		return handleClear(graphuri);
	}
	
	@POST
	@Consumes({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_XML
	})
	@Path("/{graph}")
	public Response graphDirectPost(
			@Context UriInfo uriInfo,
			@HeaderParam("Content-Type") MediaType type,
			InputStream in) {
			String graphuri = uriInfo.getAbsolutePath().toASCIIString();
		return handleAdd(uriInfo, type, graphuri, null, in, false);
	}
	
	private Response handleAdd(
			UriInfo uriInfo,
			MediaType type,
			String graphString,
			String def,
			InputStream in,
			boolean clear) {
		try (CloseableRepositoryConnection conn = getConnection()) {
			Resource dctx = null;
			String base = uriInfo.getAbsolutePath().toASCIIString();
			if (graphString != null) {
				dctx = vf.createURI(graphString);
				base = dctx.stringValue();
			}
			String typestr = type.getType() + "/" + type.getSubtype();
			RDFFormat format = getRDFFormat(typestr);
			conn.begin();
			if (dctx != null) {
				if (clear) {
					conn.clear(dctx);
				}
				conn.add(in, base, format, dctx);
			} else {
				if (clear) {
					conn.clear();
				}
				conn.add(in, base, format);
			}
			conn.commit();
			return Response.status(Response.Status.OK).build();
		} catch (RDFParseException ex) {
			String str = ex.getMessage();
			return Response.status(400).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		} catch(IOException | RepositoryException ex) {
			throw new WebApplicationException(ex);
		}
	}
	
	private Response handleClear(String graphString) {
		try (CloseableRepositoryConnection conn = getConnection()) {
			conn.begin();
			if (graphString != null) {
				Resource ctx = vf.createURI(graphString);
				conn.clear(ctx);
			} else {
				conn.clear();
			}
			conn.commit();
		} catch (RepositoryException ex) {
			throw new WebApplicationException(ex);
		}
		return Response.status(Response.Status.OK).build();
	}
	
	private Response handleGet(
			Request req,
			String def,
			String graphString) {
		final Variant variant = req.selectVariant(rdfResultVariants);
		if (variant == null) {
			return Response.notAcceptable(rdfResultVariants).build();
		}
		final MediaType mt = variant.getMediaType();
		final String mtstr = mt.getType() + "/" + mt.getSubtype();
		final RDFFormat format = getRDFFormat(mtstr);
		StreamingOutput stream;
		RepositoryConnection conn = null;
		try {
			conn = getConnection();
			if (graphString != null) {
				Resource ctx = vf.createURI(graphString);
				stream = new RDFStreamingOutput(conn, format, ctx);
			} else {
				stream = new RDFStreamingOutput(conn, format);
			}
		} catch (RepositoryException ex) {
			close(conn, ex);
			throw new WebApplicationException(ex);
		}
		return Response.ok(stream).build();
	}
	
}
