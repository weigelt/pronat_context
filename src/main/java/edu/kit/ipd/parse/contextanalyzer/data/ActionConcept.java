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
public class ActionConcept extends AbstractConcept {

	protected static final String TYPE = "actionConcept";

	private static final String STATES_CHANGED_RELATION_TYPE = "statesChangedToByConcept";

	private static final String ANTONYM_ACTION_TYPE = "antonymActionConcept";

	private Set<State> statesChangedTo = new HashSet<State>();

	private Set<ActionConcept> antonymActions = new HashSet<ActionConcept>();

	private String indexWordLemma = "";

	public ActionConcept(String name) {
		super(name);
	}

	public boolean hasStatesChangedTo() {
		if (statesChangedTo != null && !statesChangedTo.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean hasAntonymActions() {
		if (antonymActions != null && !antonymActions.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * @return the statesChangedTo
	 */
	public Set<State> getStatesChangedTo() {
		return statesChangedTo;
	}

	/**
	 * @param statesChangedTo
	 *            the statesChangedTo to set
	 */
	public boolean addStateChangedTo(State stateChangedTo) {
		boolean hasChanged = this.statesChangedTo.add(stateChangedTo);
		changed = changed || hasChanged;
		return hasChanged;
	}

	/**
	 * @return the antonymActions
	 */
	public Set<ActionConcept> getAntonymActions() {
		return antonymActions;
	}

	/**
	 * @param antonymActions
	 *            the antonymActions to set
	 */
	public boolean addAntonymAction(ActionConcept antonymAction) {
		boolean hasChanged = this.antonymActions.add(antonymAction);
		changed = changed || hasChanged;
		return hasChanged;
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
		changed = true;
		this.indexWordLemma = indexWordLemma;
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

				case ANTONYM_ACTION_TYPE:
					match = false;
					for (AbstractConcept abstractConcept : antonymActions) {
						if (Objects.equals(arc.getSourceNode(), graphNodes.get(abstractConcept))
								|| Objects.equals(arc.getTargetNode(), graphNodes.get(abstractConcept))) {
							alreadyConsidered.add(abstractConcept);
							match = true;
						}

					}
					if (!match) {
						graph.deleteArc(arc);
					}
					break;
				case STATES_CHANGED_RELATION_TYPE:
					match = false;
					for (AbstractConcept abstractConcept : statesChangedTo) {
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
		for (AbstractConcept abstractConcept : antonymActions) {
			if (!alreadyConsidered.contains(abstractConcept)) {
				INode current = graphNodes.get(this);
				INode related = graphNodes.get(abstractConcept);
				IArc arc = graph.createArc(current, related, graph.getArcType(CONCEPT_ARC_TYPE));
				arc.setAttributeValue(TYPE_OF_RELATION, ANTONYM_ACTION_TYPE);
			}

		}
		for (AbstractConcept abstractConcept : statesChangedTo) {
			if (!alreadyConsidered.contains(abstractConcept)) {
				INode current = graphNodes.get(this);
				INode related = graphNodes.get(abstractConcept);
				IArc arc = graph.createArc(current, related, graph.getArcType(CONCEPT_ARC_TYPE));
				arc.setAttributeValue(TYPE_OF_RELATION, STATES_CHANGED_RELATION_TYPE);
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
		for (State state : statesChangedTo) {
			INode current = graphNodes.get(this);
			INode related = graphNodes.get(state);
			IArc arc = graph.createArc(current, related, type);
			arc.setAttributeValue(TYPE_OF_RELATION, STATES_CHANGED_RELATION_TYPE);
		}
		for (ActionConcept actionConcept : antonymActions) {
			INode current = graphNodes.get(this);
			INode related = graphNodes.get(actionConcept);
			IArc arc = graph.createArc(current, related, type);
			arc.setAttributeValue(TYPE_OF_RELATION, ANTONYM_ACTION_TYPE);
		}
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ActionConcept) {
			ActionConcept other = (ActionConcept) obj;
			return super.equals(obj) && statesChangedTo.size() == other.statesChangedTo.size()
					&& antonymActions.size() == (other.antonymActions.size()) && this.indexWordLemma.equals(other.indexWordLemma);

		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ this.indexWordLemma.hashCode();
	}

	public static AbstractConcept readFromNode(INode node, IGraph graph) {
		ActionConcept result;
		String name = (String) node.getAttributeValue(CONCEPT_NAME);
		String ontologyIndividual = (String) node.getAttributeValue(ONTOLOGY_INDIVIDUAL);
		List<String> synonyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(SYNONYMS));
		String indexWordLemma = (String) node.getAttributeValue(INDEXWORD);
		result = new ActionConcept(name);
		result.setOntologyIndividual(ontologyIndividual);
		result.setIndexWordLemma(indexWordLemma);
		for (String string : synonyms) {
			result.addSynonym(string);
		}
		return result;

	}

	/**
	 * Invoke only after all concepts are read out
	 * 
	 * @param node
	 * @param graphNodes
	 * @param graph
	 */
	public void readConceptRelationsOfNode(INode node, HashMap<INode, ContextIndividual> graphNodes, IGraph graph) {
		super.readConceptRelationsOfNode(node, graphNodes, graph);
		for (IArc arc : node.getOutgoingArcsOfType(graph.getArcType(CONCEPT_ARC_TYPE))) {
			String type = (String) arc.getAttributeValue(TYPE_OF_RELATION);
			if (type.equals(STATES_CHANGED_RELATION_TYPE)) {
				this.statesChangedTo.add((State) graphNodes.get(arc.getTargetNode()));
			} else if (type.equals(ANTONYM_ACTION_TYPE)) {
				this.antonymActions.add((ActionConcept) graphNodes.get(arc.getTargetNode()));
				((ActionConcept) graphNodes.get(arc.getTargetNode())).addAntonymAction(this);
			}
		}
	}

}