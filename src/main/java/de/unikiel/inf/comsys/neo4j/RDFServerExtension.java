package de.unikiel.inf.comsys.neo4j;

import de.unikiel.inf.comsys.neo4j.http.GraphStore;
import de.unikiel.inf.comsys.neo4j.http.SPARQLQuery;
import de.unikiel.inf.comsys.neo4j.http.SPARQLUpdate;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.neo4j.graphdb.GraphDatabaseService;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.SailException;

@Path("/")
public class RDFServerExtension {

	private final SPARQLQuery query;
	private final SPARQLUpdate update;
	private final GraphStore graphStore;

	public RDFServerExtension(@Context GraphDatabaseService database)
			throws SailException, RepositoryException {
		SailRepository rep = RepositoryRegistry
				.getInstance(database)
				.getRepository();
		query = new SPARQLQuery(rep);
		update = new SPARQLUpdate(rep);
		graphStore = new GraphStore(rep);
	}

	@Path("/query")
	public SPARQLQuery query() {
		return query;
	}

	@Path("/update")
	public SPARQLUpdate update() {
		return update;
	}

	@Path("/graph")
	public GraphStore graph() {
		return graphStore;
	}

}
