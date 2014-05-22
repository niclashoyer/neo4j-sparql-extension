package de.unikiel.inf.comsys.neo4j;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import com.tinkerpop.blueprints.oupls.sail.GraphSail;
import de.unikiel.inf.comsys.neo4j.http.GraphStore;
import de.unikiel.inf.comsys.neo4j.http.SPARQLQuery;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.neo4j.graphdb.GraphDatabaseService;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

@Path("/")
public class RDFServerExtension {
	
    private final GraphDatabaseService database;
	private final SPARQLQuery query;
	private final GraphStore graphStore;

    public RDFServerExtension(@Context GraphDatabaseService database) throws SailException {
        this.database = database;
		Graph graph = new Neo4j2Graph(database);
		Sail sail = new GraphSail((KeyIndexableGraph) graph);
		sail.initialize();
		SailConnection sc = sail.getConnection();
		ValueFactory vf = sail.getValueFactory();
		query = new SPARQLQuery(sc, vf);
		graphStore = new GraphStore(sc, vf);
    }

	@Path("/graph")
	public GraphStore graph() {
			return graphStore;
	}
	
    @Path("/query")
    public SPARQLQuery query() {
		return query;
    }
	
}
