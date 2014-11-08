package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

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
