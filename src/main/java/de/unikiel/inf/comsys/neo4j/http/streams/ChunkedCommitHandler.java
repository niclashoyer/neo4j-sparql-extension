
package de.unikiel.inf.comsys.neo4j.http.streams;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

public class ChunkedCommitHandler implements RDFHandler {
	
	private final RepositoryConnection conn;
	private final long size;
	private final Resource dctx;
	private long count;

	public ChunkedCommitHandler(
			RepositoryConnection conn, long size, Resource dctx) {
		this.conn  = conn;
		this.size  = size;
		this.dctx  = dctx;
		this.count = 0;
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		this.count = 0;
	}

	@Override
	public void endRDF() throws RDFHandlerException {
	}

	@Override
	public void handleNamespace(String string, String string1) throws RDFHandlerException {
	}

	@Override
	public void handleStatement(Statement stmnt) throws RDFHandlerException {
		try {
			if (dctx != null) {
				conn.add(stmnt, dctx);
			} else {
				conn.add(stmnt);
			}
			count++;
			if (count >= size) {
				count = 0;
				conn.commit();
				conn.begin();
			}
		} catch (RepositoryException ex) {
			throw new RDFHandlerException(ex);
		}
	}

	@Override
	public void handleComment(String string) throws RDFHandlerException {
	}
	
}
