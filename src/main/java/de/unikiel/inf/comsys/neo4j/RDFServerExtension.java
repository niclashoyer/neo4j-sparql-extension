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

/**
 * JAX-RS class as entry point for the Neo4j-Plugin.
 */
@Path("/")
public class RDFServerExtension {

	private final SPARQLQuery query;
	private final SPARQLUpdate update;
	private final GraphStore graphStore;

	/**
	 * The constructor is called by Neo4j with a
	 * {@link GraphDatabaseService} object.
	 * The constructor creates a new repository based on the given
	 * database and initializes it.
	 *
	 * @param database
	 * @throws SailException
	 * @throws RepositoryException
	 */
	public RDFServerExtension(@Context GraphDatabaseService database)
			throws SailException, RepositoryException {
		SailRepository rep = RepositoryRegistry
				.getInstance(database)
				.getRepository();
		query = new SPARQLQuery(rep);
		update = new SPARQLUpdate(rep);
		graphStore = new GraphStore(rep);
	}

	/**
	 * Entry point for the implementation of SPARQL 1.1 query features.
	 *
	 * @return an instance of the implementation, see {@link SPARQLQuery}
	 */
	@Path("/query")
	public SPARQLQuery query() {
		return query;
	}

	/**
	 * Entry point for the implementation of SPARQL 1.1 update features.
	 *
	 * @return an instance of the implementation, see {@link SPARQLUpdate}
	 */
	@Path("/update")
	public SPARQLUpdate update() {
		return update;
	}

	/**
	 * Entry point for the implementation of SPARQL 1.1 Graph Store Protocol
	 * features.
	 *
	 * @return an instance of the implementation, see {@link GraphStore}
	 */
	@Path("/graph")
	public GraphStore graph() {
		return graphStore;
	}

}
