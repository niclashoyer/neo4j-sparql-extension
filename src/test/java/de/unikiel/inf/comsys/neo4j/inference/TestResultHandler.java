
package de.unikiel.inf.comsys.neo4j.inference;

/*
 * #%L
 * neo4j-sparql-extension
 * %%
 * Copyright (C) 2014 Niclas Hoyer
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
