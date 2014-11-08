package de.unikiel.inf.comsys.neo4j.inference;

import de.unikiel.inf.comsys.neo4j.inference.rules.Rules;
import de.unikiel.inf.comsys.neo4j.inference.rules.Rule;
import de.unikiel.inf.comsys.neo4j.SPARQLExtensionProps;
import de.unikiel.inf.comsys.neo4j.http.RDFMediaType;
import de.unikiel.inf.comsys.neo4j.http.SPARQLUpdate;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleWriterFactory;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

/**
 * A factory for query rewriters.
 *
 * This factory ensures that for each given repository connection there exists
 * at most one query rewriter.
 */
public class QueryRewriterFactory {

	private final String ontologyContext;

	private static final WeakHashMap<SailRepository, QueryRewriterFactory> map
			= new WeakHashMap<>();

	private static final Logger logger
			= Logger.getLogger(SPARQLUpdate.class.getName());

	private final SailRepository rep;
	private final ExecutorService executor;
	private List<Rule> rules;

	/**
	 * Implements a document source for the use with OWL-API.
	 *
	 * This class implements a OWL-API document source, that exports the
	 * contents of a graph in a Sesame repository. The OWL-API can't use a
	 * Sesame repository directly, thus the graph is serialized in a separate
	 * thread into Turtle and streamed to the OWL-API.
	 */
	private class RepositorySource implements OWLOntologyDocumentSource {

		private final URI ctx;
		private final IRI iri;

		/**
		 * Creates a new repository source.
		 */
		public RepositorySource() {
			this.ctx = rep.getValueFactory().createURI(ontologyContext);
			this.iri = IRI.create(ontologyContext);
		}

		@Override
		public boolean isReaderAvailable() {
			return false;
		}

		@Override
		public Reader getReader() {
			return null;
		}

		@Override
		public boolean isInputStreamAvailable() {
			return rep.isInitialized();
		}

		/**
		 * Returns a input stream that streams a graph serialized in Turtle.
		 *
		 * @return input stream that streams the graph
		 */
		@Override
		public InputStream getInputStream() {
			if (isInputStreamAvailable()) {
				// create a pipe to connect the output stream of the turtle
				// serializer to the given input stream
				final PipedOutputStream out;
				final PipedInputStream in = new PipedInputStream(2048);
				try {
					out = new PipedOutputStream(in);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				// run serialization in separate thread
				Runnable exporter = new Runnable() {
					@Override
					public void run() {
						SailRepositoryConnection conn = null;
						try {
							conn = rep.getConnection();
							TurtleWriterFactory factory
									= new TurtleWriterFactory();
							// export the graph as turtle
							conn.export(factory.getWriter(out), ctx);
							out.close();
							conn.close();
						} catch (RepositoryException |
								RDFHandlerException |
								IOException ex) {
							// server error
							try {
								if (conn != null && conn.isOpen()) {
									conn.close();
								}
							} catch (RepositoryException ex1) {
								ex.addSuppressed(ex1);
							}
							// catch a specific "Pipe closed" error that
							// is caused by the OWL-API, when the input stream
							// is closed prematurily
							if (!(ex instanceof RDFHandlerException
									&& ex.getCause() instanceof IOException
									&& ex.getCause().getMessage().equals("Pipe closed"))) {
								logger.log(
										Level.WARNING,
										"Error while exporting ontology",
										ex);
							}
						}
					}
				};
				// run in a separate thread
				executor.submit(exporter);
				return in;
			}
			return null;
		}

		@Override
		public IRI getDocumentIRI() {
			return iri;
		}

		@Override
		public OWLDocumentFormat getFormat() {
			return new TurtleDocumentFormat();
		}

		@Override
		public boolean isFormatKnown() {
			return true;
		}

		@Override
		public String getMIMEType() {
			return RDFMediaType.RDF_TURTLE;
		}

		@Override
		public boolean isMIMETypeKnown() {
			return true;
		}

	}

	/**
	 * Creates a new query rewriter factory.
	 *
	 * @param rep the repository to use
	 */
	private QueryRewriterFactory(SailRepository rep) {
		this.ontologyContext = SPARQLExtensionProps
				.getProperty("inference.graph");
		this.rules = new ArrayList<>();
		this.rep = rep;
		this.executor = Executors.newCachedThreadPool();
		// initial initialization of rewriting rules based on graph in
		// repository
		try {
			updateOntology(rep.getConnection());
		} catch (RepositoryException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Updates the set of rules used for query rewriting.
	 *
	 * @param conn the connection to use
	 */
	public final synchronized void
			updateOntology(SailRepositoryConnection conn) {
		try {
			// reload the graph and if not empty load rules from the graph
			URI ctx = conn.getValueFactory().createURI(ontologyContext);
			if (conn.size(ctx) > 0) {
				rules = Rules.fromOntology(new RepositorySource());
			}
		} catch (RepositoryException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns a query rewriter for a given repository connection.
	 *
	 * @param conn the connection to use
	 * @return a query rewriter that uses the TBox from the repository
	 */
	public synchronized QueryRewriter
			getRewriter(SailRepositoryConnection conn) {
		return new QueryRewriter(conn, rules);
	}

	/**
	 * Returns a new query rewriter factory instance.
	 *
	 * @param rep the repository connection to use
	 * @return query rewriter factory
	 */
	public static synchronized QueryRewriterFactory
			getInstance(SailRepository rep) {
		QueryRewriterFactory inst;
		if (!map.containsKey(rep)) {
			inst = new QueryRewriterFactory(rep);
			map.put(rep, inst);
		} else {
			inst = map.get(rep);
		}
		return inst;
	}

	/**
	 * Returns the graph that is used for the TBox.
	 *
	 * @return graph as string
	 */
	public String getOntologyContext() {
		return this.ontologyContext;
	}

}
