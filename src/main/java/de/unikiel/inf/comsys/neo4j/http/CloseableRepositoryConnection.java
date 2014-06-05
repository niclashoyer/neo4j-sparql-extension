
package de.unikiel.inf.comsys.neo4j.http;

import info.aduna.iteration.Iteration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import org.apache.log4j.Logger;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.Update;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.UnknownTransactionStateException;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

public class CloseableRepositoryConnection implements
	RepositoryConnection, AutoCloseable {
	
	private final RepositoryConnection conn;
	private final Logger logger = Logger.getRootLogger();
	
	public CloseableRepositoryConnection(RepositoryConnection conn) {
		logger.debug("[NEW] RepositoryConnection (" + conn.hashCode() + ")");
		this.conn = conn;
	}

	@Override
	public Repository getRepository() {
		return conn.getRepository();
	}

	@Override
	public void setParserConfig(ParserConfig config) {
		conn.setParserConfig(config);
	}

	@Override
	public ParserConfig getParserConfig() {
		return conn.getParserConfig();
	}

	@Override
	public ValueFactory getValueFactory() {
		return conn.getValueFactory();
	}

	@Override
	public boolean isOpen() throws RepositoryException {
		return conn.isOpen();
	}

	@Override
	public void close() throws RepositoryException {
		if (isActive()) {
			logger.debug("[CLOSED ROLLBACK] RepositoryConnection rolled back (" + conn.hashCode() + ")");
			conn.rollback();
		}
		if (isOpen()) {
			logger.debug("[CLOSED] RepositoryConnection closed (" + conn.hashCode() + ")");
			conn.close();
		}
	}

	@Override
	public Query prepareQuery(QueryLanguage ql, String query) throws RepositoryException, MalformedQueryException {
		return conn.prepareQuery(ql, query);
	}

	@Override
	public Query prepareQuery(QueryLanguage ql, String query, String baseURI) throws RepositoryException, MalformedQueryException {
		return conn.prepareQuery(ql, query, baseURI);
	}

	@Override
	public TupleQuery prepareTupleQuery(QueryLanguage ql, String query) throws RepositoryException, MalformedQueryException {
		return conn.prepareTupleQuery(ql, query);
	}

	@Override
	public TupleQuery prepareTupleQuery(QueryLanguage ql, String query, String baseURI) throws RepositoryException, MalformedQueryException {
		return conn.prepareTupleQuery(ql, query, baseURI);
	}

	@Override
	public GraphQuery prepareGraphQuery(QueryLanguage ql, String query) throws RepositoryException, MalformedQueryException {
		return conn.prepareGraphQuery(ql, query);
	}

	@Override
	public GraphQuery prepareGraphQuery(QueryLanguage ql, String query, String baseURI) throws RepositoryException, MalformedQueryException {
		return conn.prepareGraphQuery(ql, query, baseURI);
	}

	@Override
	public BooleanQuery prepareBooleanQuery(QueryLanguage ql, String query) throws RepositoryException, MalformedQueryException {
		return conn.prepareBooleanQuery(ql, query);
	}

	@Override
	public BooleanQuery prepareBooleanQuery(QueryLanguage ql, String query, String baseURI) throws RepositoryException, MalformedQueryException {
		return conn.prepareBooleanQuery(ql, query, baseURI);
	}

	@Override
	public Update prepareUpdate(QueryLanguage ql, String update) throws RepositoryException, MalformedQueryException {
		return conn.prepareUpdate(ql, update);
	}

	@Override
	public Update prepareUpdate(QueryLanguage ql, String update, String baseURI) throws RepositoryException, MalformedQueryException {
		return conn.prepareUpdate(ql, update, baseURI);
	}

	@Override
	public RepositoryResult<Resource> getContextIDs() throws RepositoryException {
		return conn.getContextIDs();
	}

	@Override
	public RepositoryResult<Statement> getStatements(Resource subj, URI pred, Value obj, boolean includeInferred, Resource... contexts) throws RepositoryException {
		return conn.getStatements(subj, pred, obj, includeInferred, contexts);
	}

	@Override
	public boolean hasStatement(Resource subj, URI pred, Value obj, boolean includeInferred, Resource... contexts) throws RepositoryException {
		return conn.hasStatement(subj, pred, obj, includeInferred, contexts);
	}

	@Override
	public boolean hasStatement(Statement st, boolean includeInferred, Resource... contexts) throws RepositoryException {
		return conn.hasStatement(st, includeInferred, contexts);
	}

	@Override
	public void exportStatements(Resource subj, URI pred, Value obj, boolean includeInferred, RDFHandler handler, Resource... contexts) throws RepositoryException, RDFHandlerException {
		conn.exportStatements(subj, pred, obj, includeInferred, handler, contexts);
	}

	@Override
	public void export(RDFHandler handler, Resource... contexts) throws RepositoryException, RDFHandlerException {
		conn.export(handler, contexts);
	}

	@Override
	public long size(Resource... contexts) throws RepositoryException {
		return conn.size(contexts);
	}

	@Override
	public boolean isEmpty() throws RepositoryException {
		return conn.isEmpty();
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws RepositoryException {
		conn.setAutoCommit(autoCommit);
	}

	@Override
	public boolean isAutoCommit() throws RepositoryException {
		return conn.isAutoCommit();
	}

	@Override
	public boolean isActive() throws UnknownTransactionStateException, RepositoryException {
		return conn.isActive();
	}

	@Override
	public void begin() throws RepositoryException {
		logger.debug("[BEGIN] Begin transaction (" + conn.hashCode() + ")");
		conn.begin();
	}

	@Override
	public void commit() throws RepositoryException {
		logger.debug("[COMMIT] Commit transaction (" + conn.hashCode() + ")");
		conn.commit();
	}

	@Override
	public void rollback() throws RepositoryException {
		logger.debug("[ROLLBACK] Commit rollback (" + conn.hashCode() + ")");
		conn.rollback();
	}

	@Override
	public void add(InputStream in, String baseURI, RDFFormat dataFormat, Resource... contexts) throws IOException, RDFParseException, RepositoryException {
		conn.add(in, baseURI, dataFormat, contexts);
	}

	@Override
	public void add(Reader reader, String baseURI, RDFFormat dataFormat, Resource... contexts) throws IOException, RDFParseException, RepositoryException {
		conn.add(reader, baseURI, dataFormat, contexts);
	}

	@Override
	public void add(URL url, String baseURI, RDFFormat dataFormat, Resource... contexts) throws IOException, RDFParseException, RepositoryException {
		conn.add(url, baseURI, dataFormat, contexts);
	}

	@Override
	public void add(File file, String baseURI, RDFFormat dataFormat, Resource... contexts) throws IOException, RDFParseException, RepositoryException {
		conn.add(file, baseURI, dataFormat, contexts);
	}

	@Override
	public void add(Resource subject, URI predicate, Value object, Resource... contexts) throws RepositoryException {
		conn.add(subject, predicate, object, contexts);
	}

	@Override
	public void add(Statement st, Resource... contexts) throws RepositoryException {
		conn.add(st, contexts);
	}

	@Override
	public void add(Iterable<? extends Statement> statements, Resource... contexts) throws RepositoryException {
		conn.add(statements, contexts);
	}

	@Override
	public <E extends Exception> void add(Iteration<? extends Statement, E> statements, Resource... contexts) throws RepositoryException, E {
		conn.add(statements, contexts);
	}

	@Override
	public void remove(Resource subject, URI predicate, Value object, Resource... contexts) throws RepositoryException {
		conn.remove(subject, predicate, object, contexts);
	}

	@Override
	public void remove(Statement st, Resource... contexts) throws RepositoryException {
		conn.remove(st, contexts);
	}

	@Override
	public void remove(Iterable<? extends Statement> statements, Resource... contexts) throws RepositoryException {
		conn.remove(statements, contexts);
	}

	@Override
	public <E extends Exception> void remove(Iteration<? extends Statement, E> statements, Resource... contexts) throws RepositoryException, E {
		conn.remove(statements, contexts);
	}

	@Override
	public void clear(Resource... contexts) throws RepositoryException {
		conn.clear(contexts);
	}

	@Override
	public RepositoryResult<Namespace> getNamespaces() throws RepositoryException {
		return conn.getNamespaces();
	}

	@Override
	public String getNamespace(String prefix) throws RepositoryException {
		return conn.getNamespace(prefix);
	}

	@Override
	public void setNamespace(String prefix, String name) throws RepositoryException {
		conn.setNamespace(prefix, name);
	}

	@Override
	public void removeNamespace(String prefix) throws RepositoryException {
		conn.removeNamespace(prefix);
	}

	@Override
	public void clearNamespaces() throws RepositoryException {
		conn.clearNamespaces();
	}
	
}
