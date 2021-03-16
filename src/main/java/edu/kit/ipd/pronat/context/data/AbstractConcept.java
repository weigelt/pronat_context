/**
 *
 */
package edu.kit.ipd.pronat.context.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import edu.kit.ipd.pronat.context.data.entities.SubjectEntity;
import edu.kit.ipd.pronat.context.data.relations.Relation;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.graph.INodeType;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public abstract class AbstractConcept extends ContextIndividual implements Comparable<AbstractConcept> {

	public static final String CONCEPT_NODE_TYPE = "contextConcept";
	protected static final String CONCEPT_TYPE = "typeOfConcept";
	public static final String CONCEPT_NAME = "name";
	protected static final String ONTOLOGY_INDIVIDUAL = "ontologyIndividual";
	protected static final String SYNONYMS = "synonyms";
	protected static final String CONCEPT_ARC_TYPE = "conceptRelation";
	protected static final String TYPE_OF_RELATION = "conceptRelationType";
	protected static final String EQUAL_RELATION_TYPE = "equalConcept";
	protected static final String PART_OF_RELATION_TYPE = "partOfConcept";
	protected static final String PART_RELATION_TYPE = "partConcept";
	protected static final String SUB_RELATION_TYPE = "subConcept";
	protected static final String SUPER_RELATION_TYPE = "superConcept";
	protected static final String GENDER = "gender";
	protected static final String INDEXWORD = "indexWord";

	private String name;

	private String ontologyIndividual = "";

	private Set<String> synonyms = new TreeSet<String>();

	private Set<AbstractConcept> equalConcepts = new TreeSet<AbstractConcept>();

	private Set<AbstractConcept> superConcepts = new TreeSet<AbstractConcept>();

	private Set<AbstractConcept> subConcepts = new TreeSet<AbstractConcept>();

	private Set<AbstractConcept> partOfConcepts = new TreeSet<AbstractConcept>();

	private Set<AbstractConcept> partConcepts = new TreeSet<AbstractConcept>();

	protected AbstractConcept(String name) {
		super();
		this.name = name;
		changed = false;
	}

	@Override
	public int compareTo(AbstractConcept o) {
		int resultName = getName().compareTo(o.name);
		if (resultName == 0) {
			return getClass().getName().compareTo(o.getClass().getName());
		}
		return resultName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		if (!Objects.equals(this.name, name)) {
			changed = true;
			this.name = name;
		}
	}

	/**
	 * @return the superConcepts
	 */
	public Set<AbstractConcept> getSuperConcepts() {
		return superConcepts;
	}

	/**
	 * @return the subConcepts
	 */
	public Set<AbstractConcept> getSubConcepts() {
		return subConcepts;
	}

	/**
	 * The {@link AbstractConcept}s which this Concept is a Part of
	 *
	 *
	 * @return the partOfConcepts
	 */
	public Set<AbstractConcept> getPartOfConcepts() {
		return partOfConcepts;
	}

	/**
	 * The {@link AbstractConcept}s which are a Part of this Concept
	 *
	 * @return the partConcepts
	 */
	public Set<AbstractConcept> getPartConcepts() {
		return partConcepts;
	}

	public boolean hasSuperConcepts() {
		if (superConcepts != null && !superConcepts.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean hasSubConcepts() {
		if (subConcepts != null && !subConcepts.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean hasPartOfConcepts() {
		if (partOfConcepts != null && !partOfConcepts.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean hasPartConcepts() {
		if (partConcepts != null && !partConcepts.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean hasSynonyms() {
		if (synonyms != null && !synonyms.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean hasEqualConcepts() {
		if (equalConcepts != null && !equalConcepts.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * @return the synonyms
	 */
	public Set<String> getSynonyms() {
		return synonyms;
	}

	/**
	 * @param synonym
	 *            the synonym to add
	 */
	public boolean addSynonym(String synonym) {
		boolean hasChanged = synonyms.add(synonym);
		changed = changed || hasChanged;
		return hasChanged;
	}

	/**
	 * @return the ontologyIndividual
	 */
	public String getOntologyIndividual() {
		return ontologyIndividual;
	}

	/**
	 * @param ontologyIndividual
	 *            the ontologyIndividual to set
	 */
	public void setOntologyIndividual(String ontologyIndividual) {
		if (!Objects.equals(this.ontologyIndividual, ontologyIndividual)) {
			changed = true;
			this.ontologyIndividual = ontologyIndividual;
		}
	}

	/**
	 * @return the equalConcepts
	 */
	public Set<AbstractConcept> getEqualConcepts() {
		return equalConcepts;
	}

	/**
	 * @param equalConcept
	 *            the equalConcepts to set
	 */
	public boolean addEqualConcept(AbstractConcept equalConcept) {
		if (!equalConcept.equals(this)) {
			boolean hasChanged = equalConcepts.add(equalConcept);
			changed = changed || hasChanged;
			return hasChanged;
		}
		return false;
	}

	/**
	 * @param equalConcept
	 *            the equalConcepts to set
	 */
	public boolean addEqualConceptWithoutSettingChanged(AbstractConcept equalConcept) {
		if (!equalConcept.equals(this)) {
			return equalConcepts.add(equalConcept);
		}
		return false;
	}

	/**
	 *
	 */
	public boolean addPartOfConcept(AbstractConcept partOfConcept) {
		if (!partOfConcept.equals(this)) {
			boolean hasChanged = partOfConcepts.add(partOfConcept);
			changed = changed || hasChanged;
			return hasChanged;
		}
		return false;
	}

	/**
	 *
	 */
	public boolean addPartOfConceptWithoutSettingChanged(AbstractConcept partOfConcept) {
		if (!partOfConcept.equals(this)) {
			return partOfConcepts.add(partOfConcept);
		}
		return false;
	}

	/**
	 *
	 */
	public boolean addPartConcept(AbstractConcept partConcept) {
		if (!partConcept.equals(this)) {
			boolean hasChanged = partConcepts.add(partConcept);
			changed = changed || hasChanged;
			return hasChanged;
		}
		return false;
	}

	/**
	 *
	 */
	public boolean addPartConceptWithoutSettingChanged(AbstractConcept partConcept) {
		if (!partConcept.equals(this)) {
			return partConcepts.add(partConcept);

		}
		return false;
	}

	/**
	 *
	 */
	public boolean addSuperConcept(AbstractConcept superConcept) {
		if (!superConcept.equals(this)) {
			boolean hasChanged = superConcepts.add(superConcept);
			changed = changed || hasChanged;
			return hasChanged;
		}
		return false;
	}

	/**
	 *
	 */
	public boolean addSuperConceptWithoutSettingChanged(AbstractConcept superConcept) {
		if (!superConcept.equals(this)) {
			return superConcepts.add(superConcept);
		}
		return false;
	}

	/**
	 *
	 */
	public boolean addSubConcept(AbstractConcept subConcept) {
		if (!subConcept.equals(this)) {
			boolean hasChanged = subConcepts.add(subConcept);
			changed = changed || hasChanged;
			return hasChanged;
		}
		return false;
	}

	/**
	 *
	 */
	public boolean addSubConceptWithoutSettingChanged(AbstractConcept subConcept) {
		if (!subConcept.equals(this)) {
			return subConcepts.add(subConcept);
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractConcept) {
			AbstractConcept concept = (AbstractConcept) obj;
			boolean result = Objects.equals(name, concept.name) && Objects.equals(ontologyIndividual, concept.ontologyIndividual);

			return result;
		}
		return false;
	}

	public boolean equalsComplex(Object obj) {
		if (obj instanceof AbstractConcept) {
			AbstractConcept concept = (AbstractConcept) obj;
			boolean result = Objects.equals(name, concept.name) && Objects.equals(ontologyIndividual, concept.ontologyIndividual)
					&& Objects.equals(synonyms, concept.synonyms) && matchesConcepts(equalConcepts, concept.equalConcepts)
					&& matchesConcepts(subConcepts, concept.subConcepts) && matchesConcepts(superConcepts, concept.superConcepts)
					&& matchesConcepts(partConcepts, concept.partConcepts) && matchesConcepts(partOfConcepts, concept.partOfConcepts);

			if (concept.getRelations().size() != getRelations().size()) {
				return false;
			}
			for (Relation rel : getRelations()) {
				if (!concept.getRelations().contains(rel)) {
					return false;
				}
			}
			for (Relation rel : concept.getRelations()) {
				if (!getRelations().contains(rel)) {
					return false;
				}
			}
			return result;
		}
		return false;
	}

	protected boolean matchesConcepts(Set<? extends AbstractConcept> current, Set<? extends AbstractConcept> other) {
		if (current != null) {
			if (other == null) {
				return false;
			}
			if (current.size() == other.size()) {
				for (AbstractConcept abstractConcept : current) {
					boolean match = false;
					for (AbstractConcept otherConcept : other) {
						if (abstractConcept.getName().equals(otherConcept.getName())
								&& abstractConcept.getClass().isInstance(otherConcept)) {
							match = true;
							break;
						}
					}
					if (!match) {
						return false;
					}
				}
				return true;

			} else {
				return false;
			}
		} else if (other == null) {
			return true;
		}
		return false;
	}

	public boolean equalsWithoutRelation(Object obj) {
		if (obj instanceof AbstractConcept) {
			AbstractConcept concept = (AbstractConcept) obj;
			boolean result = Objects.equals(name, concept.name) && Objects.equals(ontologyIndividual, concept.ontologyIndividual)
					&& Objects.equals(synonyms, concept.synonyms) && matchesConcepts(equalConcepts, concept.equalConcepts)
					&& matchesConcepts(subConcepts, concept.subConcepts) && matchesConcepts(superConcepts, concept.superConcepts)
					&& matchesConcepts(partConcepts, concept.partConcepts) && matchesConcepts(partOfConcepts, concept.partOfConcepts);

			return result;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = name.hashCode();
		hash = 31 * hash + getClass().hashCode();

		hash = ontologyIndividual == null ? hash : 31 * hash + ontologyIndividual.hashCode();
		//
		//		for (AbstractConcept abstractConcept : equalConcepts) {
		//			hash = 31 * hash + abstractConcept.getName().hashCode();
		//		}
		//
		//		for (AbstractConcept abstractConcept : subConcepts) {
		//			hash = 31 * hash + abstractConcept.getName().hashCode();
		//		}
		//
		//		for (AbstractConcept abstractConcept : superConcepts) {
		//			hash = 31 * hash + abstractConcept.getName().hashCode();
		//		}
		//
		//		for (AbstractConcept abstractConcept : partConcepts) {
		//			hash = 31 * hash + abstractConcept.getName().hashCode();
		//		}
		//
		//		for (AbstractConcept abstractConcept : partOfConcepts) {
		//			hash = 31 * hash + abstractConcept.getName().hashCode();
		//		}
		//hash = this.synonyms == null ? hash : 31 * hash + this.synonyms.hashCode();
		return hash;
	}

	public static AbstractConcept readFromNode(INode conceptNode, IGraph graph) {
		switch ((String) conceptNode.getAttributeValue(CONCEPT_TYPE)) {
		case ObjectConcept.TYPE:
			return ObjectConcept.readFromNode(conceptNode, graph);
		case SubjectConcept.TYPE:
			return SubjectConcept.readFromNode(conceptNode, graph);
		case ActionConcept.TYPE:
			return ActionConcept.readFromNode(conceptNode, graph);
		case State.TYPE:
			return State.readFromNode(conceptNode, graph);
		default:
			return null;
		}
	}

	/**
	 * Invoke only after all concepts are read out
	 *
	 * @param node
	 * @param graphNodes
	 * @param graph
	 */
	public void readConceptRelationsOfNode(INode node, ContextIndividual[] graphNodes, IGraph graph) {
		for (IArc arc : node.getOutgoingArcsOfType(graph.getArcType(CONCEPT_ARC_TYPE))) {
			String type = (String) arc.getAttributeValue(TYPE_OF_RELATION);
			AbstractConcept target;
			switch (type) {
			case EQUAL_RELATION_TYPE:
				equalConcepts.add((AbstractConcept) graphNodes[graph.getNodes().indexOf(arc.getTargetNode())]);
				break;
			case PART_OF_RELATION_TYPE:
				target = (AbstractConcept) graphNodes[graph.getNodes().indexOf(arc.getTargetNode())];
				partOfConcepts.add(target);
				target.addPartConceptWithoutSettingChanged(this);
				break;
			case PART_RELATION_TYPE:
				target = (AbstractConcept) graphNodes[graph.getNodes().indexOf(arc.getTargetNode())];
				partConcepts.add(target);
				target.addPartOfConceptWithoutSettingChanged(this);
				break;
			case SUB_RELATION_TYPE:
				target = (AbstractConcept) graphNodes[graph.getNodes().indexOf(arc.getTargetNode())];
				subConcepts.add(target);
				target.addSuperConceptWithoutSettingChanged(this);
				break;
			case SUPER_RELATION_TYPE:
				target = (AbstractConcept) graphNodes[graph.getNodes().indexOf(arc.getTargetNode())];
				superConcepts.add(target);
				target.addSubConceptWithoutSettingChanged(this);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public Set<Relation> updateNode(INode node, IGraph graph, HashMap<Long, INode> graphNodes) {
		Set<Relation> alreadyUpdated = super.updateNode(node, graph, graphNodes);
		node.setAttributeValue(CONCEPT_NAME, getName());
		node.setAttributeValue(ONTOLOGY_INDIVIDUAL, getOntologyIndividual());
		node.setAttributeValue(SYNONYMS, Arrays.toString(getSynonyms().toArray()));
		updateConceptRelations(node, graph, graphNodes);
		return alreadyUpdated;
	}

	private void updateConceptRelations(INode node, IGraph graph, HashMap<Long, INode> graphNodes) {
		Set<IArc> arcs = new HashSet<IArc>();
		arcs.addAll(node.getOutgoingArcsOfType(graph.getArcType(CONCEPT_ARC_TYPE)));
		arcs.addAll(node.getIncomingArcsOfType(graph.getArcType(CONCEPT_ARC_TYPE)));
		Set<AbstractConcept> alreadyConsidered = new TreeSet<>();
		for (IArc arc : arcs) {
			if (arc.getType().getName().equals(CONCEPT_ARC_TYPE)) {
				String type = (String) arc.getAttributeValue(TYPE_OF_RELATION);
				boolean match = false;
				switch (type) {
				case EQUAL_RELATION_TYPE:
					match = false;
					for (AbstractConcept abstractConcept : equalConcepts) {
						if (graphNodes.get(abstractConcept.getID()) != null) {
							if (Objects.equals(arc.getSourceNode(), graphNodes.get(abstractConcept.getID()))
									|| Objects.equals(arc.getTargetNode(), graphNodes.get(abstractConcept.getID()))) {
								alreadyConsidered.add(abstractConcept);
								match = true;
							}
						}

					}
					if (!match) {
						graph.deleteArc(arc);
					}
					break;
				case PART_OF_RELATION_TYPE:
					match = false;
					for (AbstractConcept abstractConcept : partOfConcepts) {
						if (graphNodes.get(abstractConcept.getID()) != null) {
							if (Objects.equals(arc.getTargetNode(), graphNodes.get(abstractConcept.getID()))) {
								alreadyConsidered.add(abstractConcept);
								match = true;
							}
						}

					}
					for (AbstractConcept abstractConcept : partConcepts) {
						if (graphNodes.get(abstractConcept.getID()) != null) {
							if (Objects.equals(arc.getSourceNode(), graphNodes.get(abstractConcept.getID()))) {
								alreadyConsidered.add(abstractConcept);
								match = true;
							}
						}
					}
					if (!match) {
						graph.deleteArc(arc);
					}
					break;
				case PART_RELATION_TYPE:
					match = false;
					for (AbstractConcept abstractConcept : partConcepts) {
						if (graphNodes.get(abstractConcept.getID()) != null) {
							if (Objects.equals(arc.getTargetNode(), graphNodes.get(abstractConcept.getID()))) {
								alreadyConsidered.add(abstractConcept);
								match = true;
							}
						}

					}
					for (AbstractConcept abstractConcept : partOfConcepts) {
						if (graphNodes.get(abstractConcept.getID()) != null) {
							if (Objects.equals(arc.getSourceNode(), graphNodes.get(abstractConcept.getID()))) {
								alreadyConsidered.add(abstractConcept);
								match = true;
							}
						}
					}
					if (!match) {
						graph.deleteArc(arc);
					}
					break;
				case SUB_RELATION_TYPE:
					match = false;
					for (AbstractConcept abstractConcept : subConcepts) {
						if (graphNodes.get(abstractConcept.getID()) != null) {
							if (Objects.equals(arc.getTargetNode(), graphNodes.get(abstractConcept.getID()))) {
								alreadyConsidered.add(abstractConcept);
								match = true;
							}
						}

					}
					for (AbstractConcept abstractConcept : superConcepts) {
						if (graphNodes.get(abstractConcept.getID()) != null) {
							if (Objects.equals(arc.getSourceNode(), graphNodes.get(abstractConcept.getID()))) {
								alreadyConsidered.add(abstractConcept);
								match = true;
							}
						}
					}
					if (!match) {
						graph.deleteArc(arc);
					}
					break;
				case SUPER_RELATION_TYPE:
					match = false;
					for (AbstractConcept abstractConcept : superConcepts) {
						if (graphNodes.get(abstractConcept.getID()) != null) {
							if (Objects.equals(arc.getTargetNode(), graphNodes.get(abstractConcept.getID()))) {
								alreadyConsidered.add(abstractConcept);
								match = true;
							}
						}

					}
					for (AbstractConcept abstractConcept : subConcepts) {
						if (graphNodes.get(abstractConcept.getID()) != null) {
							if (Objects.equals(arc.getSourceNode(), graphNodes.get(abstractConcept.getID()))) {
								alreadyConsidered.add(abstractConcept);
								match = true;
							}
						}
					}
					if (!match) {
						graph.deleteArc(arc);
					}
					break;
				default:
					break;
				}
			}
		}
		for (AbstractConcept abstractConcept : equalConcepts) {
			if (!alreadyConsidered.contains(abstractConcept) && graphNodes.containsKey(abstractConcept.getID())) {
				INode current = graphNodes.get(getID());
				INode related = graphNodes.get(abstractConcept.getID());
				IArc arc = graph.createArc(current, related, graph.getArcType(CONCEPT_ARC_TYPE));
				arc.setAttributeValue(TYPE_OF_RELATION, EQUAL_RELATION_TYPE);
			}

		}
		for (AbstractConcept abstractConcept : partOfConcepts) {
			if (!alreadyConsidered.contains(abstractConcept) && graphNodes.containsKey(abstractConcept.getID())) {
				INode current = graphNodes.get(getID());
				INode related = graphNodes.get(abstractConcept.getID());
				IArc arc = graph.createArc(current, related, graph.getArcType(CONCEPT_ARC_TYPE));
				arc.setAttributeValue(TYPE_OF_RELATION, PART_OF_RELATION_TYPE);
			}
		}
		for (AbstractConcept abstractConcept : partConcepts) {
			if (!alreadyConsidered.contains(abstractConcept) && graphNodes.containsKey(abstractConcept.getID())) {
				INode current = graphNodes.get(getID());
				INode related = graphNodes.get(abstractConcept.getID());
				IArc arc = graph.createArc(current, related, graph.getArcType(CONCEPT_ARC_TYPE));
				arc.setAttributeValue(TYPE_OF_RELATION, PART_RELATION_TYPE);
			}
		}
		for (AbstractConcept abstractConcept : subConcepts) {
			if (!alreadyConsidered.contains(abstractConcept) && graphNodes.containsKey(abstractConcept.getID())) {
				INode current = graphNodes.get(getID());
				INode related = graphNodes.get(abstractConcept.getID());
				IArc arc = graph.createArc(current, related, graph.getArcType(CONCEPT_ARC_TYPE));
				arc.setAttributeValue(TYPE_OF_RELATION, SUB_RELATION_TYPE);
			}
		}
		for (AbstractConcept abstractConcept : superConcepts) {
			if (!alreadyConsidered.contains(abstractConcept) && graphNodes.containsKey(abstractConcept.getID())) {
				INode current = graphNodes.get(getID());
				INode related = graphNodes.get(abstractConcept.getID());
				IArc arc = graph.createArc(current, related, graph.getArcType(CONCEPT_ARC_TYPE));
				arc.setAttributeValue(TYPE_OF_RELATION, SUPER_RELATION_TYPE);

			}
		}

	}

	@Override
	public INode printToGraph(IGraph graph) {
		INodeType nodeType;
		if (graph.hasNodeType(CONCEPT_NODE_TYPE)) {
			nodeType = graph.getNodeType(CONCEPT_NODE_TYPE);
		} else {
			nodeType = createConceptNodeType(graph);
		}
		INode node = graph.createNode(nodeType);
		node.setAttributeValue(CONCEPT_NAME, getName());
		node.setAttributeValue(ONTOLOGY_INDIVIDUAL, getOntologyIndividual());
		node.setAttributeValue(SYNONYMS, Arrays.toString(getSynonyms().toArray()));
		return node;
	}

	/**
	 * Invoke only after printToGraph is called on all concepts
	 *
	 * @param graph
	 * @param graphNodes
	 */
	public IArcType printConceptRelations(IGraph graph, HashMap<Long, INode> graphNodes) {
		IArcType arcType;
		if (graph.hasArcType(CONCEPT_ARC_TYPE)) {
			arcType = graph.getArcType(CONCEPT_ARC_TYPE);
		} else {
			arcType = createConceptRelationArcType(graph);
		}
		for (AbstractConcept abstractConcept : equalConcepts) {
			INode current = graphNodes.get(getID());
			INode related = graphNodes.get(abstractConcept.getID());
			IArc arc = graph.createArc(current, related, arcType);
			arc.setAttributeValue(TYPE_OF_RELATION, EQUAL_RELATION_TYPE);

		}
		for (AbstractConcept abstractConcept : partOfConcepts) {
			INode current = graphNodes.get(getID());
			INode related = graphNodes.get(abstractConcept.getID());
			IArc arc = graph.createArc(current, related, arcType);
			arc.setAttributeValue(TYPE_OF_RELATION, PART_OF_RELATION_TYPE);
		}
		for (AbstractConcept abstractConcept : partConcepts) {
			INode current = graphNodes.get(getID());
			INode related = graphNodes.get(abstractConcept.getID());
			IArc arc = graph.createArc(current, related, arcType);
			arc.setAttributeValue(TYPE_OF_RELATION, PART_RELATION_TYPE);
		}
		for (AbstractConcept abstractConcept : subConcepts) {
			INode current = graphNodes.get(getID());
			INode related = graphNodes.get(abstractConcept.getID());
			IArc arc = graph.createArc(current, related, arcType);
			arc.setAttributeValue(TYPE_OF_RELATION, SUB_RELATION_TYPE);
		}
		for (AbstractConcept abstractConcept : superConcepts) {
			INode current = graphNodes.get(getID());
			INode related = graphNodes.get(abstractConcept.getID());
			IArc arc = graph.createArc(current, related, arcType);
			arc.setAttributeValue(TYPE_OF_RELATION, SUPER_RELATION_TYPE);
		}
		return arcType;
	}

	private IArcType createConceptRelationArcType(IGraph graph) {
		IArcType arcType = graph.createArcType(CONCEPT_ARC_TYPE);
		arcType.addAttributeToType("String", TYPE_OF_RELATION);
		return arcType;
	}

	private INodeType createConceptNodeType(IGraph graph) {
		INodeType nodeType = graph.createNodeType(CONCEPT_NODE_TYPE);
		nodeType.addAttributeToType("String", CONCEPT_NAME);
		nodeType.addAttributeToType("String", CONCEPT_TYPE);
		nodeType.addAttributeToType("String", ONTOLOGY_INDIVIDUAL);
		nodeType.addAttributeToType("String", SYNONYMS);
		nodeType.addAttributeToType(SubjectEntity.Gender.class.getName(), GENDER);
		nodeType.addAttributeToType("String", INDEXWORD);
		return nodeType;
	}

	@Override
	public String toString() {
		String output = getClass().getSimpleName() + "=" + getName();
		return output;
	}

}
