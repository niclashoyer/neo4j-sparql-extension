
package de.unikiel.inf.comsys.neo4j.rio;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

public class SailsRDFHandler implements RDFHandler {

	private final SailConnection sc;
	private final Resource dctx;
	
	public SailsRDFHandler(SailConnection sc) throws SailException {
		this(sc, null);
	}
	
	public SailsRDFHandler(SailConnection sc, Resource defaultContext) throws SailException {
		this.sc = sc;
		this.dctx = defaultContext;
	}
	
	@Override
	public void startRDF() throws RDFHandlerException {
		try {
			sc.begin();
		} catch (SailException ex) {
			throw new RDFHandlerException(ex);
		}
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		try {
			sc.commit();
		} catch (SailException ex) {
			throw new RDFHandlerException(ex);
		}
	}

	@Override
	public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
		try {
			sc.setNamespace(prefix, uri);
		} catch (SailException ex) {
			throw new RDFHandlerException(ex);
		}
	}

	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		Resource ctx = s.getContext();
		if (ctx == null) {
			ctx = dctx;
		}
		System.out.println(ctx);
		try {
			sc.addStatement(s.getSubject(), s.getPredicate(), s.getObject(), ctx);
		} catch (SailException ex) {
			throw new RDFHandlerException(ex);
		}
	}

	@Override
	public void handleComment(String string) throws RDFHandlerException {
		
	}
	
}
