
package de.unikiel.inf.comsys.neo4j.inference.rules;

import de.unikiel.inf.comsys.neo4j.inference.Rule;
import java.util.List;
import org.semanticweb.owlapi.model.OWLOntology;

public interface Extractor {
	
	public List<Rule> extract(OWLOntology ot);
	
}
