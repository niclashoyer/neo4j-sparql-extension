package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

import de.unikiel.inf.comsys.neo4j.inference.Rule;
import de.unikiel.inf.comsys.neo4j.inference.rules.TransitiveObjectProperty;
import static de.unikiel.inf.comsys.neo4j.inference.rules.extractor.AbstractExtractor.getString;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

public class TransitiveObjectPropertyExtractor extends AbstractExtractor {
	
	@Override
	public List<Rule> extract(OWLOntology ot) {
		ArrayList<Rule> list = new ArrayList<>();
		for (OWLTransitiveObjectPropertyAxiom a :
				ot.getAxioms(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
			String op = getString(a.getProperty());
			list.add(new TransitiveObjectProperty(op));
		}
		return list;
	}
	
}
