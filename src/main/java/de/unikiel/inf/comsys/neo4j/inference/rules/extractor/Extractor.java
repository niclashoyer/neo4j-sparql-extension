package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

import de.unikiel.inf.comsys.neo4j.inference.rules.Rule;
import java.util.List;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * An extractor extracts rules from an OWL-2 ontology.
 *
 * Each extractor should extract a specific rule from an ontology.
 */
public interface Extractor {

	/**
	 * Extract rules from an ontology.
	 *
	 * @param ot the ontology to extact from
	 * @return list of rules
	 */
	public List<Rule> extract(OWLOntology ot);

}
