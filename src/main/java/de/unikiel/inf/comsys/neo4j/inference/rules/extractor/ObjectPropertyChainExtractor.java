
package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

import de.unikiel.inf.comsys.neo4j.inference.Rule;
import de.unikiel.inf.comsys.neo4j.inference.rules.ObjectPropertyChain;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

public class ObjectPropertyChainExtractor extends AbstractExtractor {
	
	@Override
	public List<Rule> extract(OWLOntology ot) {
		ArrayList<Rule> list = new ArrayList<>();
		for (OWLSubPropertyChainOfAxiom a :
				ot.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF)) {
			String op          = getString(a.getSuperProperty());
			List<String> chain = getStrings(a.getPropertyChain());
			list.add(new ObjectPropertyChain(op, chain));
		}
		return list;
	}
	
}
