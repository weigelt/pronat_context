/**
 * 
 */
package edu.kit.ipd.pronat.context.data;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import edu.kit.ipd.pronat.context.data.relations.Relation;
import edu.kit.ipd.pronat.context.util.GraphUtils;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.pronat.context.data.entities.SubjectEntity;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class SubjectConcept extends EntityConcept {

	protected static final String TYPE = "subjectConcept";

	private SubjectEntity.Gender gender = SubjectEntity.Gender.UNKNOWN;

	/**
	 * @param name
	 */
	public SubjectConcept(String name) {
		super(name);

	}

	/**
	 * @return the gender
	 */
	public SubjectEntity.Gender getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(SubjectEntity.Gender gender) {
		if (!Objects.equals(this.gender, gender)) {
			changed = true;
			this.gender = gender;
		}
	}

	@Override
	public Set<Relation> updateNode(INode node, IGraph graph, HashMap<Long, INode> graphNodes) {
		Set<Relation> alreadyUpdated = super.updateNode(node, graph, graphNodes);
		node.setAttributeValue(CONCEPT_TYPE, TYPE);
		node.setAttributeValue(GENDER, getGender());
		return alreadyUpdated;
	}

	@Override
	public INode printToGraph(IGraph graph) {
		INode node = super.printToGraph(graph);
		node.setAttributeValue(CONCEPT_TYPE, TYPE);
		node.setAttributeValue(GENDER, getGender());
		return node;

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SubjectConcept) {
			SubjectConcept other = (SubjectConcept) obj;
			return super.equals(obj) && Objects.equals(gender, other.gender);

		}
		return false;
	}

	@Override
	public boolean equalsComplex(Object obj) {
		if (obj instanceof SubjectConcept) {
			SubjectConcept other = (SubjectConcept) obj;
			return super.equals(obj) && Objects.equals(gender, other.gender);

		}
		return false;
	}

	@Override
	public boolean equalsWithoutRelation(Object obj) {
		if (obj instanceof SubjectConcept) {
			SubjectConcept other = (SubjectConcept) obj;
			boolean result = super.equalsWithoutRelation(obj) && Objects.equals(gender, other.gender);

			return result;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = this.gender == null ? hash : 31 * hash + this.gender.hashCode();

		return hash;
	}

	public static AbstractConcept readFromNode(INode node, IGraph graph) {
		SubjectConcept result;
		String name = (String) node.getAttributeValue(CONCEPT_NAME);
		String ontologyIndividual = (String) node.getAttributeValue(ONTOLOGY_INDIVIDUAL);
		List<String> synonyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(SYNONYMS));
		SubjectEntity.Gender gender = (SubjectEntity.Gender) node.getAttributeValue(GENDER);
		result = new SubjectConcept(name);
		result.setOntologyIndividual(ontologyIndividual);
		for (String string : synonyms) {
			result.addSynonym(string);
		}
		result.setGender(gender);
		result.changed = false;
		return result;

	}

}
