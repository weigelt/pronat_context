/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.util.GraphUtils;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public class ObjectConcept extends EntityConcept {

	protected static final String TYPE = "objectConcept";

	private static final String STATE_RELATION_TYPE = "stateOfConcept";

	private Set<State> states = new HashSet<State>();

	private String indexWordLemma = "";

	/**
	 * @param name
	 */
	public ObjectConcept(String name) {
		super(name);
		changed = false;
	}

	/**
	 * @return the states
	 */
	public Set<State> getStates() {
		return states;
	}

	/**
	 * @param states
	 *            the states to set
	 */
	public boolean addState(State state) {
		boolean hasChanged = this.states.add(state);
		changed = changed || hasChanged;
		return hasChanged;
	}

	public boolean hasStates() {
		if (states != null && !states.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	public String getIndexWordLemma() {
		return indexWordLemma;
	}

	/**
	 * @param
	 */
	public void setIndexWordLemma(String indexWordLemma) {
		if (!Objects.equals(this.indexWordLemma, indexWordLemma)) {
			changed = true;
			this.indexWordLemma = indexWordLemma;
		}
	}

	public boolean hasIndexWordLemma() {
		if (indexWordLemma != "") {
			return true;
		}
		return false;
	}

	@Override
	public Set<Relation> updateNode(INode node, IGraph graph, HashMap<ContextIndividual, INode> graphNodes) {
		Set<Relation> alreadyUpdated = super.updateNode(node, graph, graphNodes);
		node.setAttributeValue(CONCEPT_TYPE, TYPE);
		node.setAttributeValue(INDEXWORD, getIndexWordLemma());
		updateConceptRelations(node, graph, graphNodes);
		return alreadyUpdated;
	}

	private void updateConceptRelations(INode node, IGraph graph, HashMap<ContextIndividual, INode> graphNodes) {
		Set<IArc> arcs = new HashSet<IArc>();
		arcs.addAll(node.getOutgoingArcsOfType(graph.getArcType(CONCEPT_ARC_TYPE)));
		arcs.addAll(node.getIncomingArcsOfType(graph.getArcType(CONCEPT_ARC_TYPE)));
		Set<AbstractConcept> alreadyConsidered = new HashSet<>();
		for (IArc arc : arcs) {
			if (arc.getType().getName().equals(CONCEPT_ARC_TYPE)) {
				String type = (String) arc.getAttributeValue(TYPE_OF_RELATION);
				boolean match = false;
				switch (type) {

				case STATE_RELATION_TYPE:
					match = false;
					for (AbstractConcept abstractConcept : states) {
						if (Objects.equals(arc.getTargetNode(), graphNodes.get(abstractConcept))) {
							alreadyConsidered.add(abstractConcept);
							match = true;
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
		for (AbstractConcept abstractConcept : states) {
			if (!alreadyConsidered.contains(abstractConcept)) {
				INode current = graphNodes.get(this);
				INode related = graphNodes.get(abstractConcept);
				IArc arc = graph.createArc(current, related, graph.getArcType(CONCEPT_ARC_TYPE));
				arc.setAttributeValue(TYPE_OF_RELATION, STATE_RELATION_TYPE);
			}

		}

	}

	@Override
	public INode printToGraph(IGraph graph) {
		INode node = super.printToGraph(graph);
		node.setAttributeValue(CONCEPT_TYPE, TYPE);
		node.setAttributeValue(INDEXWORD, getIndexWordLemma());
		return node;

	}

	@Override
	public IArcType printConceptRelations(IGraph graph, HashMap<ContextIndividual, INode> graphNodes) {
		IArcType type = super.printConceptRelations(graph, graphNodes);
		for (State state : states) {
			INode current = graphNodes.get(this);
			INode related = graphNodes.get(state);
			IArc arc = graph.createArc(current, related, type);
			arc.setAttributeValue(TYPE_OF_RELATION, STATE_RELATION_TYPE);
		}
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObjectConcept) {
			ObjectConcept other = (ObjectConcept) obj;
			return super.equals(obj) && Objects.equals(states, other.states) && Objects.equals(this.indexWordLemma, other.indexWordLemma);

		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		//		for (AbstractConcept abstractConcept : states) {
		//			hash = 31 * hash + abstractConcept.getName().hashCode();
		//		}
		hash = this.indexWordLemma == null ? hash : 31 * hash + this.indexWordLemma.hashCode();
		return hash;
	}

	public static AbstractConcept readFromNode(INode node, IGraph graph) {
		ObjectConcept result;
		String name = (String) node.getAttributeValue(CONCEPT_NAME);
		String ontologyIndividual = (String) node.getAttributeValue(ONTOLOGY_INDIVIDUAL);
		List<String> synonyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(SYNONYMS));
		String indexWordLemma = (String) node.getAttributeValue(INDEXWORD);
		result = new ObjectConcept(name);
		result.setOntologyIndividual(ontologyIndividual);
		result.setIndexWordLemma(indexWordLemma);
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
	@Override
	public void readConceptRelationsOfNode(INode node, ContextIndividual[] graphNodes, IGraph graph) {
		super.readConceptRelationsOfNode(node, graphNodes, graph);
		for (IArc arc : node.getOutgoingArcsOfType(graph.getArcType(CONCEPT_ARC_TYPE))) {
			String type = (String) arc.getAttributeValue(TYPE_OF_RELATION);
			if (type.equals(STATE_RELATION_TYPE)) {
				State state = (State) graphNodes[graph.getNodes().indexOf(arc.getTargetNode())];
				this.states.add(state);
			}
		}
	}

}
