package de.unikiel.inf.comsys.neo4j.inference.rules;

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

import java.util.LinkedList;
import java.util.List;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;

/**
 * An abstract superclass for rules that contains utility methods for statement
 * pattern handling.
 */
public abstract class AbstractRule implements Rule {

	protected ValueFactory vf;

	/**
	 * Set the reference to a value factory.
	 *
	 * @param vf the value factory
	 */
	@Override
	public void setValueFactory(ValueFactory vf) {
		this.vf = vf;
	}

	/**
	 * Returns the URI of a constant {@link Var} object.
	 *
	 * @param v the {@link Var} object
	 * @return URI as string if it is a URI, null otherwise
	 */
	protected String getURIString(Var v) {
		if (v.isConstant()) {
			Value val = v.getValue();
			if (val instanceof URI) {
				URI uri = (URI) val;
				return uri.stringValue();
			}
		}
		return null;
	}

	/**
	 * Returns the URI of a subject of a statement pattern.
	 *
	 * @param sp a statement pattern
	 * @return the URI as string, null otherwise
	 */
	protected String getSubject(StatementPattern sp) {
		return getURIString(sp.getSubjectVar());
	}

	/**
	 * Returns the URI of a predicate of a statement pattern.
	 *
	 * @param sp a statement pattern
	 * @return the URI as string, null otherwise
	 */
	protected String getPredicate(StatementPattern sp) {
		return getURIString(sp.getPredicateVar());
	}

	/**
	 * Returns the URI of a object of a statement pattern.
	 *
	 * @param sp a statement pattern
	 * @return the URI as string, null otherwise
	 */
	protected String getObject(StatementPattern sp) {
		return getURIString(sp.getObjectVar());
	}

	/**
	 * Returns the URI of a graph of a statement pattern.
	 *
	 * @param sp a statement pattern
	 * @return the URI as string, null otherwise
	 */
	protected String getContext(StatementPattern sp) {
		return getURIString(sp.getContextVar());
	}

	/**
	 * Creates a new list that can be used as a reference to nodes that need to
	 * be visited after rule application.
	 *
	 * @return new query model node list
	 */
	protected List<QueryModelNode> newNextList() {
		return new LinkedList<>();
	}

}
