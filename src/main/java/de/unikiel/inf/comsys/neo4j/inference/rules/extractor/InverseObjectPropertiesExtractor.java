
package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

import de.unikiel.inf.comsys.neo4j.inference.Rule;
import de.unikiel.inf.comsys.neo4j.inference.rules.InverseObjectProperties;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

public class InverseObjectPropertiesExtractor extends AbstractExtractor {
	
	@Override
	public List<Rule> extract(OWLOntology ot) {
		ArrayList<Rule> list = new ArrayList<>();
		for (OWLInverseObjectPropertiesAxiom a :
				ot.getAxioms(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
			String op1 = getString(a.getFirstProperty());
			String op2 = getString(a.getSecondProperty());
			list.add(new InverseObjectProperties(op1, op2));
		}
		return list;
	}
	
}
