package de.unikiel.inf.comsys.neo4j;

import de.unikiel.inf.comsys.neo4j.inference.QueryRewriter;
import de.unikiel.inf.comsys.neo4j.inference.Rule;
import de.unikiel.inf.comsys.neo4j.inference.Rules;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleWriterFactory;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;

public class QueryRewriterFactory {
	
	public final String ontologyContext;
	
	private static final WeakHashMap<SailRepository, QueryRewriterFactory>
		map = new WeakHashMap<>();
	
	private final SailRepository rep;
	private List<Rule> rules;
 
	private class RepositorySource implements OWLOntologyDocumentSource {

		private final SailRepositoryConnection conn;
		private final URI ctx;
		private final IRI iri;
		
		public RepositorySource(SailRepositoryConnection conn) {
			this.conn = conn;
			this.ctx  = conn.getValueFactory().createURI(ontologyContext);
			this.iri  = IRI.create(ontologyContext);
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
			try {
				return conn.isOpen() && conn.size(ctx) > 0;
			} catch (RepositoryException ex) {
				return false;
			}
		}

		@Override
		public InputStream getInputStream() {
			if (isInputStreamAvailable()) {
				PipedOutputStream out = null;
				try {
					PipedInputStream in = new PipedInputStream(2048);
					out = new PipedOutputStream(in);
					TurtleWriterFactory factory = new TurtleWriterFactory();
					conn.export(factory.getWriter(out), ctx);
					return in;
				} catch (IOException | RepositoryException |
						 RDFHandlerException ex) {
					try {
						if (out != null) {
							out.close();
						}
					} catch (IOException ex1) {
						ex.addSuppressed(ex1);
					}
					throw new RuntimeException(ex);
				} finally {
					try {
						if (out != null) {
							out.close();
						}
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
			}
			return null;
		}

		@Override
		public IRI getDocumentIRI() {
			return iri;
		}
		
	}
	
    private QueryRewriterFactory(SailRepository rep) {
		this.ontologyContext = SPARQLExtensionProps
			.getProperty("inference.graph");
		this.rules = new ArrayList<>();
		this.rep   = rep;
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
				rules = Rules.fromOntology(new RepositorySource(conn));
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
	
}