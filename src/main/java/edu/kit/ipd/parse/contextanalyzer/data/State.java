package edu.kit.ipd.parse.contextanalyzer.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.util.GraphUtils;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

public class State extends AbstractConcept {

	protected static final String TYPE = "stateConcept";

	private static final String ASSOCIATED_STATE_RELATION_TYPE = "associatedState";

	private Set<State> associatedStates = new TreeSet<State>();

	public State(String name) {
		super(name);
	}

	public boolean hasAssociatedStates() {
		if (associatedStates != null && !associatedStates.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * @return the associatedStates
	 */
	public Set<State> getAssociatedStates() {
		return associatedStates;
	}

	/**
	 * @param associatedStates
	 *            the associatedStates to set
	 */
	public boolean addAssociatedState(State associatedState) {
		if (!Objects.equals(associatedState, this)) {
			boolean hasChanged = this.associatedStates.add(associatedState);
			changed = changed || hasChanged;
			return hasChanged;
		}
		return false;
	}

	/**
	 * @param associatedStates
	 *            the associatedStates to set
	 */
	public boolean addAssociatedStateWithoutSettingChanged(State associatedState) {
		if (!Objects.equals(associatedState, this)) {
			return this.associatedStates.add(associatedState);
		}
		return false;
	}

	@Override
	public Set<Relation> updateNode(INode node, IGraph graph, HashMap<Long, INode> graphNodes) {
		Set<Relation> alreadyUpdated = super.updateNode(node, graph, graphNodes);
		node.setAttributeValue(CONCEPT_TYPE, TYPE);
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

				case ASSOCIATED_STATE_RELATION_TYPE:
					match = false;
					for (AbstractConcept abstractConcept : associatedStates) {
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

				default:
					break;
				}
			}
		}
		for (AbstractConcept abstractConcept : associatedStates) {
			if (!alreadyConsidered.contains(abstractConcept) && graphNodes.containsKey(abstractConcept.getID())) {
				INode current = graphNodes.get(this.getID());
				INode related = graphNodes.get(abstractConcept.getID());
				IArc arc = graph.createArc(current, related, graph.getArcType(CONCEPT_ARC_TYPE));
				arc.setAttributeValue(TYPE_OF_RELATION, ASSOCIATED_STATE_RELATION_TYPE);
			}

		}

	}

	@Override
	public INode printToGraph(IGraph graph) {
		INode node = super.printToGraph(graph);
		node.setAttributeValue(CONCEPT_TYPE, TYPE);
		return node;

	}

	@Override
	public IArcType printConceptRelations(IGraph graph, HashMap<Long, INode> graphNodes) {
		IArcType type = super.printConceptRelations(graph, graphNodes);
		for (State state : associatedStates) {
			INode current = graphNodes.get(this.getID());
			INode related = graphNodes.get(state.getID());
			IArc arc = graph.createArc(current, related, type);
			arc.setAttributeValue(TYPE_OF_RELATION, ASSOCIATED_STATE_RELATION_TYPE);
		}
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof State) {
			State other = (State) obj;
			return super.equals(obj) && matchesConcepts(associatedStates, other.associatedStates);

		}
		return false;
	}

	@Override
	public boolean equalsWithoutRelation(Object obj) {
		if (obj instanceof State) {
			State other = (State) obj;
			boolean result = super.equalsWithoutRelation(obj) && matchesConcepts(associatedStates, other.associatedStates);

			return result;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		//		for (State state : associatedStates) {
		//			hash = hash * 31 + state.getName().hashCode();
		//		}
		return hash;
	}

	public static AbstractConcept readFromNode(INode node, IGraph graph) {
		State result;
		String name = (String) node.getAttributeValue(CONCEPT_NAME);
		String ontologyIndividual = (String) node.getAttributeValue(ONTOLOGY_INDIVIDUAL);
		List<String> synonyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(SYNONYMS));
		result = new State(name);
		result.setOntologyIndividual(ontologyIndividual);
		for (String string : synonyms) {
			result.addSynonym(string);
		}
		result.changed = false;
		return result;

	}

	/**
	 * Invoke only after all concepts are read out
	 * 
	 * @param node
	 * @param graphNodes
	 * @param graph
	 */
	public void readConceptRelationsOfNode(INode node, ContextIndividual[] graphNodes, IGraph graph) {
		super.readConceptRelationsOfNode(node, graphNodes, graph);
		for (IArc arc : node.getOutgoingArcsOfType(graph.getArcType(CONCEPT_ARC_TYPE))) {
			String type = (String) arc.getAttributeValue(TYPE_OF_RELATION);
			if (type.equals(ASSOCIATED_STATE_RELATION_TYPE)) {
				this.associatedStates.add((State) graphNodes[graph.getNodes().indexOf(arc.getTargetNode())]);
				((State) graphNodes[graph.getNodes().indexOf(arc.getTargetNode())]).addAssociatedStateWithoutSettingChanged(this);
			}
		}
	}

}
