
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
import de.unikiel.inf.comsys.neo4j.inference.rules.SubClassOf;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiomSetShortCut;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiomShortCut;

/**
 * {@link Extractor} implementation that extracts
 * {@link SubClassOf} rules.
 */
public class SubClassOfExtractor extends AbstractExtractor {
	
	private void addAxiom(List<Rule> list, OWLSubClassOfAxiom a) {
		if (!(a.getSubClass().isAnonymous() ||
		      a.getSuperClass().isAnonymous())) {
			String ce1 = getString(a.getSubClass());
			String ce2 = getString(a.getSuperClass());
			list.add(new SubClassOf(ce1, ce2));
		}
	}
	
	private void addAxioms(ArrayList<Rule> list, Set<OWLSubClassOfAxiom> axs) {
		for (OWLSubClassOfAxiom a : axs) {
			addAxiom(list, a);
		}
	}
	
	/**
	 * Extracts {@link SubClassOf} rules.
	 * 
	 * @param ot ontology
	 * @return extracted rules
	 */
	@Override
	public List<Rule> extract(OWLOntology ot) {
		ArrayList<Rule> list = new ArrayList<>();
		// direct mapping of SubClassOf axioms
		addAxioms(list, ot.getAxioms(AxiomType.SUBCLASS_OF));
		// indirect mapping of shortcut axioms
		for (OWLAxiom a : ot.getAxioms()) {
			if (a instanceof OWLSubClassOfAxiomSetShortCut) {
				OWLSubClassOfAxiomSetShortCut sc =
						(OWLSubClassOfAxiomSetShortCut) a;
				addAxioms(list, sc.asOWLSubClassOfAxioms());
			}
			if (a instanceof OWLSubClassOfAxiomShortCut) {
				OWLSubClassOfAxiomShortCut sc = (OWLSubClassOfAxiomShortCut) a;
				addAxiom(list, sc.asOWLSubClassOfAxiom());
			}
		}
		return list;
	}
	
}
