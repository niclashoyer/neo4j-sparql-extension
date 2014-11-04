package de.unikiel.inf.comsys.neo4j.inference;

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

public class QueryRewriterFactory {

	private final String ontologyContext;

	private static final WeakHashMap<SailRepository, QueryRewriterFactory> map
			= new WeakHashMap<>();

	private static final Logger logger
			= Logger.getLogger(SPARQLUpdate.class.getName());

	private final SailRepository rep;
	private final ExecutorService executor;
	private List<Rule> rules;

	private class RepositorySource implements OWLOntologyDocumentSource {

		private final URI ctx;
		private final IRI iri;

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

		@Override
		public InputStream getInputStream() {
			if (isInputStreamAvailable()) {
				final PipedOutputStream out;
				final PipedInputStream in = new PipedInputStream(2048);
				try {
					out = new PipedOutputStream(in);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				Runnable exporter = new Runnable() {
					@Override
					public void run() {
						SailRepositoryConnection conn = null;
						try {

							conn = rep.getConnection();
							TurtleWriterFactory factory
									= new TurtleWriterFactory();
							conn.export(factory.getWriter(out), ctx);
							out.close();
							conn.close();
						} catch (RepositoryException |
								RDFHandlerException |
								IOException ex) {
							try {
								if (conn != null && conn.isOpen()) {
									conn.close();
								}
							} catch (RepositoryException ex1) {
								ex.addSuppressed(ex1);
							}
							if (!(ex instanceof RDFHandlerException &&
								ex.getCause() instanceof IOException &&
								ex.getCause().getMessage().equals("Pipe closed"))) {
								logger.log(
										Level.WARNING,
										"Error while exporting ontology",
										ex);	
							}
						}
					}
				};
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

	private QueryRewriterFactory(SailRepository rep) {
		this.ontologyContext = SPARQLExtensionProps
				.getProperty("inference.graph");
		this.rules = new ArrayList<>();
		this.rep = rep;
		this.executor = Executors.newCachedThreadPool();
		try {
			updateOntology(rep.getConnection());
		} catch (RepositoryException ex) {
			throw new RuntimeException(ex);
		}
	}

	public final synchronized void
			updateOntology(SailRepositoryConnection conn) {
		try {
			URI ctx = conn.getValueFactory().createURI(ontologyContext);
			if (conn.size(ctx) > 0) {
				rules = Rules.fromOntology(new RepositorySource());
			}
		} catch (RepositoryException ex) {
			throw new RuntimeException(ex);
		}
	}

	public synchronized QueryRewriter
			getRewriter(SailRepositoryConnection conn) {
		return new QueryRewriter(conn, rules);
	}

	public static synchronized QueryRewriterFactory getInstance(
			SailRepository rep) {
		QueryRewriterFactory inst;
		if (!map.containsKey(rep)) {
			inst = new QueryRewriterFactory(rep);
			map.put(rep, inst);
		} else {
			inst = map.get(rep);
		}
		return inst;
	}
	
	public String getOntologyContext() {
		return this.ontologyContext;
	}

}
