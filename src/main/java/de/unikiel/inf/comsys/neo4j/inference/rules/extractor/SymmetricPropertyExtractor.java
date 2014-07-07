
package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

import de.unikiel.inf.comsys.neo4j.inference.Rule;
import de.unikiel.inf.comsys.neo4j.inference.rules.SymmetricObjectProperty;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;

public class SymmetricPropertyExtractor extends AbstractExtractor {
	
	@Override
	public List<Rule> extract(OWLOntology ot) {
		ArrayList<Rule> list = new ArrayList<>();
		for (OWLSymmetricObjectPropertyAxiom a :
				ot.getAxioms(AxiomType.SYMMETRIC_OBJECT_PROPERTY)) {
			String op = getString(a.getProperty());
			list.add(new SymmetricObjectProperty(op));
		}
		return list;
	}
	
}
