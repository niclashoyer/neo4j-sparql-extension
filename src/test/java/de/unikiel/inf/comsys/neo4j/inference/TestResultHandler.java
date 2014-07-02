
package de.unikiel.inf.comsys.neo4j.inference;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.List;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;

public class TestResultHandler implements TupleQueryResultHandler {
	
	private List<String> bindingNames;
	private final Multiset<BindingSet> solutions;

	public TestResultHandler() {
		this.solutions = HashMultiset.create();
	}

	public List<String> getBindingNames() {
		return bindingNames;
	}

	public Multiset<BindingSet> getSolutions() {
		return solutions;
	}

	@Override
	public void handleBoolean(boolean value) throws QueryResultHandlerException {
	}

	@Override
	public void handleLinks(List<String> linkUrls) throws QueryResultHandlerException {
	}

	@Override
	public void startQueryResult(List<String> bindingNames) throws TupleQueryResultHandlerException {
		this.bindingNames = bindingNames;
	}

	@Override
	public void endQueryResult() throws TupleQueryResultHandlerException {
	}

	@Override
	public void handleSolution(BindingSet set) throws TupleQueryResultHandlerException {
		this.solutions.add(set);
	}
	
}
