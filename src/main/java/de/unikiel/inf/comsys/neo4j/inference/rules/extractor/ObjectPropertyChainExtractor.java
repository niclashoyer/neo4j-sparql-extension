
package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

import de.unikiel.inf.comsys.neo4j.inference.rules.Rule;
import de.unikiel.inf.comsys.neo4j.inference.rules.ObjectPropertyChain;
import static de.unikiel.inf.comsys.neo4j.inference.rules.extractor.AbstractExtractor.getString;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

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
		for (OWLTransitiveObjectPropertyAxiom a :
				ot.getAxioms(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
			String op = getString(a.getProperty());
			list.add(new ObjectPropertyChain(op, op, op));
		}
		return list;
	}
	
}
