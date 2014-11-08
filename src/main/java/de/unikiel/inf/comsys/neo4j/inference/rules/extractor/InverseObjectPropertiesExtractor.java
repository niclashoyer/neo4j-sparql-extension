package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

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

import de.unikiel.inf.comsys.neo4j.inference.rules.Rule;
import de.unikiel.inf.comsys.neo4j.inference.rules.InverseObjectProperties;
import static de.unikiel.inf.comsys.neo4j.inference.rules.extractor.AbstractExtractor.getString;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;

/**
 * {@link Extractor} implementation that extracts
 * {@link InverseObjectProperties} rules.
 */
public class InverseObjectPropertiesExtractor extends AbstractExtractor {

	/**
	 * Extracts {@link InverseObjectProperties} rules.
	 *
	 * @param ot ontology
	 * @return extracted rules
	 */
	@Override
	public List<Rule> extract(OWLOntology ot) {
		ArrayList<Rule> list = new ArrayList<>();
		// direct mapping of InverseObjectProperties axioms
		for (OWLInverseObjectPropertiesAxiom a
				: ot.getAxioms(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
			String op1 = getString(a.getFirstProperty());
			String op2 = getString(a.getSecondProperty());
			list.add(new InverseObjectProperties(op1, op2));
		}
		// indirect mapping of SymmetricObjectProperties axioms
		for (OWLSymmetricObjectPropertyAxiom a
				: ot.getAxioms(AxiomType.SYMMETRIC_OBJECT_PROPERTY)) {
			String op = getString(a.getProperty());
			list.add(new InverseObjectProperties(op, op));
		}
		return list;
	}

}
