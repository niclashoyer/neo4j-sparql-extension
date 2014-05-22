
package de.unikiel.inf.comsys.neo4j.http;

import de.unikiel.inf.comsys.neo4j.rio.SailsRDFHandler;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.turtle.TurtleParser;
import org.openrdf.sail.SailConnection;

public class GraphStore extends AbstractSailsResource {

	public GraphStore(SailConnection sc, ValueFactory vf) {
		super(sc, vf);
	}
	
	@PUT
	@Consumes("text/turtle")
	public Response graph(
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
			p.setRDFHandler(new SailsRDFHandler(sc, dctx));
			p.parse(in, base);
			String str = graphString + "\n" + def;
			return Response.status(Response.Status.OK).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		} catch (Exception ex) {
			// DEBUG
			String str = ex.toString();
			return Response.status(500).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		}
	}
	
}
