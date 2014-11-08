package de.unikiel.inf.comsys.neo4j.http;

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

/**
 * An abstraction for RDF MIME-Types.
 */
public final class RDFMediaType {

	public static final String SPARQL_RESULTS_JSON = "application/sparql-results+json";
	public static final String SPARQL_RESULTS_XML = "application/sparql-results+xml";
	public static final String SPARQL_RESULTS_CSV = "text/csv";
	public static final String SPARQL_RESULTS_TSV = "text/tab-separated-values";
	public static final String SPARQL_QUERY = "application/sparql-query";
	public static final String SPARQL_UPDATE = "application/sparql-update";
	public static final String RDF_TURTLE = "text/turtle";
	public static final String RDF_JSON = "application/rdf+json";
	public static final String RDF_XML = "application/rdf+xml";
	public static final String RDF_NTRIPLES = "application/n-triples";

	private RDFMediaType() {
	}
}
