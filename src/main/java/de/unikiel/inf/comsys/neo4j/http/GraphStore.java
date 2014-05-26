
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Variant;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriterFactory;

public class GraphStore extends AbstractSailsResource {

	private final ValueFactory vf;
	
	public GraphStore(RepositoryConnection conn) {
		super(conn);
		this.vf = conn.getValueFactory();
	}
	
	@GET
	@Produces({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_JSON
	})
	public Response graphIndirectGet(
			Request req,
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
			UriInfo uriInfo,
			@HeaderParam("Content-Type") MediaType type,
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def,
			InputStream in) {
		handleClear(graphString);
		return handleAdd(uriInfo, type, graphString, def, in);
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
			UriInfo uriInfo,
			@HeaderParam("Content-Type") MediaType type,
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def,
			InputStream in) {
		return handleAdd(uriInfo, type, graphString, def, in);
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
			Request req,
			@PathParam("graph") String graphString) {
		return handleGet(req, null, graphString);
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
			UriInfo uriInfo,
			@HeaderParam("Content-Type") MediaType type,
			@PathParam("graph") String graphString,
			InputStream in) {
		String def = uriInfo.getAbsolutePath().toASCIIString();
		handleClear(graphString);
		return handleAdd(uriInfo, type, graphString, def, in);
	}
	
	@DELETE
	@Path("/{graph}")
	public Response graphDirectDelete(
			@PathParam("graph") String graphString) {
		return handleClear(graphString);
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
			UriInfo uriInfo,
			@HeaderParam("Content-Type") MediaType type,
			@PathParam("graph") String graphString,
			InputStream in) {
		return handleAdd(uriInfo, type, graphString, null, in);
	}
	
	private Response handleAdd(
			UriInfo uriInfo,
			MediaType type,
			String graphString,
			String def,
			InputStream in) {
		try {
			Resource dctx = null;
			String base = uriInfo.getAbsolutePath().toASCIIString();
			if (graphString != null) {
				dctx = vf.createURI(graphString);
				base = dctx.stringValue();
			}
			String typestr = type.getType() + "/" + type.getSubtype();
			RDFFormat format = getRDFFormat(typestr);
			if (dctx != null) {
				conn.add(in, base, format, dctx);
			} else {
				conn.add(in, base, format);
			}
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
		try {
			if (graphString != null) {
				Resource ctx = vf.createURI(graphString);
				conn.clear(ctx);
			} else {
				conn.clear();
			}
		} catch (RepositoryException ex) {
			throw new WebApplicationException(ex);
		}
		return Response.status(Response.Status.OK).build();
	}
	
	public Response handleGet(
			Request req,
			String def,
			String graphString) {
		final Variant variant = req.selectVariant(rdfResultVariants);
		if (variant == null) {
			return Response.notAcceptable(rdfResultVariants).build();
		}
		final MediaType mt = variant.getMediaType();
		final String mtstr = mt.getType() + "/" + mt.getSubtype();
		final RDFWriterFactory factory = getRDFWriterFactory(mtstr);
		StreamingOutput stream;
		if (graphString != null) {
			Resource ctx = vf.createURI(graphString);
			stream = new RDFStreamingOutput(conn, factory, ctx);
		} else {
			stream = new RDFStreamingOutput(conn, factory);
		}
		return Response.ok(stream).build();
	}
	
	private RDFFormat getRDFFormat(String mimetype) {
		switch(mimetype) {
			default:
			case RDFMediaType.RDF_TURTLE:
				return RDFFormat.TURTLE;
			case RDFMediaType.RDF_XML:
				return RDFFormat.RDFXML;
			case RDFMediaType.RDF_NTRIPLES:
				return RDFFormat.NTRIPLES;
			case RDFMediaType.RDF_JSON:
				return RDFFormat.RDFJSON;
		}
	}
	
}
