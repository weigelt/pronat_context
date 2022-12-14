/**
 * 
 */
package edu.kit.ipd.pronat.context.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.data.entities.SpeakerEntity;
import edu.kit.ipd.pronat.context.data.relations.Relation;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 *
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class Context {

	private Set<Entity> entities;

	private Set<Action> actions;

	private Set<AbstractConcept> concepts;

	private HashMap<Long, INode> nodesMap;

	private boolean readFromGraph = false;

	public static long id = 0;

	public Context() {
		entities = new HashSet<Entity>();
		actions = new HashSet<Action>();
		concepts = new TreeSet<AbstractConcept>();
		id = 0;
	}

	private Context(boolean readFromGraph) {
		entities = new HashSet<Entity>();
		actions = new HashSet<Action>();
		concepts = new TreeSet<AbstractConcept>();
		this.nodesMap = new HashMap<Long, INode>();
		this.readFromGraph = readFromGraph;
		id = 0;
	}

	/**
	 * @return the entities
	 */
	public Set<Entity> getEntities() {
		return entities;
	}

	/**
	 * @param entities
	 *            the entities to set
	 */
	public void setEntities(Set<Entity> entities) {
		this.entities = entities;
	}

	public boolean addEntity(Entity entity) {
		entity.setID(id);
		id++;
		return this.entities.add(entity);
	}

	/**
	 * @return the actions
	 */
	public Set<Action> getActions() {
		return actions;
	}

	/**
	 * @param actions
	 *            the actions to set
	 */
	public void setActions(Set<Action> actions) {
		this.actions = actions;
	}

	public boolean addAction(Action action) {
		action.setID(id);
		id++;
		return this.actions.add(action);

	}

	public void printToGraph(IGraph graph) {
		HashMap<Long, INode> graphNodesMap = new HashMap<>(nodesMap);
		HashSet<Relation> relations = new HashSet<>();
		HashSet<Relation> alreadyUpdated = new HashSet<>();
		if (readFromGraph) {
			Entity speaker = getSpeaker();
			if (speaker != null && !this.nodesMap.containsKey(speaker.getID())) {
				graphNodesMap.put(speaker.getID(), speaker.printToGraph(graph));
				relations.addAll(speaker.getRelations());
			}
			for (Entity entity : entities) {
				if (!(entity instanceof SpeakerEntity)) {
					if (entity.hasChanged()) {
						if (this.nodesMap.containsKey(entity.getID())) {

							alreadyUpdated.addAll(entity.updateNode(this.nodesMap.get(entity.getID()), graph, graphNodesMap));

						} else {
							graphNodesMap.put(entity.getID(), entity.printToGraph(graph));

						}
						relations.addAll(entity.getRelations());
					} else if (!this.nodesMap.containsKey(entity.getID())) {
						graphNodesMap.put(entity.getID(), entity.printToGraph(graph));

						relations.addAll(entity.getRelations());

					} else {
						graphNodesMap.put(entity.getID(), this.nodesMap.get(entity.getID()));
						//	relations.addAll(entity.getRelations());
					}
				}
			}

			for (Action action : actions) {
				if (action.hasChanged()) {
					if (this.nodesMap.containsKey(action.getID())) {
						alreadyUpdated.addAll(action.updateNode(this.nodesMap.get(Long.valueOf(action.getID())), graph, graphNodesMap));
					} else {
						graphNodesMap.put(action.getID(), action.printToGraph(graph));

					}
					relations.addAll(action.getRelations());
				} else if (!this.nodesMap.containsKey(action.getID())) {
					graphNodesMap.put(action.getID(), action.printToGraph(graph));

					relations.addAll(action.getRelations());
				} else {
					graphNodesMap.put(action.getID(), this.nodesMap.get(action.getID()));

					//relations.addAll(action.getRelations());
				}

			}

			for (AbstractConcept concept : concepts) {
				if (concept.hasChanged()) {
					if (this.nodesMap.containsKey(concept.getID())) {
						alreadyUpdated.addAll(concept.updateNode(this.nodesMap.get(concept.getID()), graph, graphNodesMap));
					} else {
						graphNodesMap.put(concept.getID(), concept.printToGraph(graph));

					}
					relations.addAll(concept.getRelations());
				} else if (!this.nodesMap.containsKey(concept.getID())) {
					graphNodesMap.put(concept.getID(), concept.printToGraph(graph));

					relations.addAll(concept.getRelations());
				} else {
					graphNodesMap.put(concept.getID(), this.nodesMap.get(concept.getID()));

					//relations.addAll(concept.getRelations());
				}
			}
			for (AbstractConcept concept : concepts) {
				if (concept.hasChanged() || !this.nodesMap.containsKey(concept.getID())) {

					concept.printConceptRelations(graph, graphNodesMap);
				}

			}
			for (Relation relation : relations) {
				if (!alreadyUpdated.contains(relation)) {
					relation.printToGraph(graph, graphNodesMap);
				}
			}
		} else {

			for (Entity entity : entities) {
				graphNodesMap.put(entity.getID(), entity.printToGraph(graph));

				relations.addAll(entity.getRelations());
			}
			for (Action action : actions) {
				graphNodesMap.put(action.getID(), action.printToGraph(graph));
				relations.addAll(action.getRelations());
			}
			for (AbstractConcept concept : concepts) {
				graphNodesMap.put(concept.getID(), concept.printToGraph(graph));
				relations.addAll(concept.getRelations());
			}
			for (AbstractConcept concept : concepts) {
				concept.printConceptRelations(graph, graphNodesMap);
			}
			for (Relation relation : relations) {
				relation.printToGraph(graph, graphNodesMap);
			}
		}
	}

	private SpeakerEntity getSpeaker() {
		for (Entity entity : entities) {
			if (entity instanceof SpeakerEntity) {
				return (SpeakerEntity) entity;
			}
		}
		return null;
	}

	public static final Context readFromGraph(IGraph graph) {
		List<INode> graphNodes = graph.getNodes();
		ContextIndividual[] graphNodesToIndividuals = new ContextIndividual[graphNodes.size()];
		HashMap<ContextIndividual, INode> graphNodeMap = new HashMap<>();
		List<Action> actions = new ArrayList<Action>();
		List<Entity> entities = new ArrayList<Entity>();
		List<AbstractConcept> concepts = new ArrayList<AbstractConcept>();

		if (graph.hasNodeType(Entity.ENTITY_NODE_TYPE)) {
			List<INode> entityNodes = graph.getNodesOfType(graph.getNodeType(Entity.ENTITY_NODE_TYPE));
			for (INode entityNode : entityNodes) {

				Entity entity = Entity.readFromNode(entityNode, graph);
				graphNodesToIndividuals[graphNodes.indexOf(entityNode)] = entity;
				//graphMap.put(entityNode, entity);
				graphNodeMap.put(entity, entityNode);
				entities.add(entity);

			}
		}
		if (graph.hasNodeType(Action.ACTION_NODE_TYPE)) {
			List<INode> actionNodes = graph.getNodesOfType(graph.getNodeType(Action.ACTION_NODE_TYPE));
			for (INode actionNode : actionNodes) {
				Action action = Action.readFromNode(actionNode, graph);
				graphNodesToIndividuals[graphNodes.indexOf(actionNode)] = action;
				//graphMap.put(actionNode, action);
				graphNodeMap.put(action, actionNode);
				actions.add(action);
			}
		}
		if (graph.hasNodeType(AbstractConcept.CONCEPT_NODE_TYPE)) {
			List<INode> conceptNodes = graph.getNodesOfType(graph.getNodeType(AbstractConcept.CONCEPT_NODE_TYPE));
			for (INode node : conceptNodes) {
				AbstractConcept concept = AbstractConcept.readFromNode(node, graph);
				graphNodesToIndividuals[graphNodes.indexOf(node)] = concept;
				//graphMap.put(node, concept);
				graphNodeMap.put(concept, node);
				concepts.add(concept);
			}
		}
		for (AbstractConcept concept : concepts) {
			concept.readConceptRelationsOfNode(graphNodes.get(Arrays.asList(graphNodesToIndividuals).indexOf(concept)),
					graphNodesToIndividuals, graph);
		}
		if (graph.hasArcType(Relation.RELATION_ARC_TYPE)) {
			List<IArc> relationArcs = graph.getArcsOfType(graph.getArcType(Relation.RELATION_ARC_TYPE));
			for (IArc relationArc : relationArcs) {
				Relation.readFromArc(relationArc, graphNodesToIndividuals, graph);
			}
		}

		Context result = new Context(true);
		for (Action action : actions) {
			result.addAction(action);

		}
		for (Entity entity : entities) {
			result.addEntity(entity);
		}
		for (AbstractConcept abstractConcept : concepts) {
			result.addConcept(abstractConcept);
		}
		HashMap<Long, INode> nodes = new HashMap<>();
		for (ContextIndividual ind : graphNodeMap.keySet()) {
			nodes.put(ind.getID(), graphNodeMap.get(ind));
		}
		result.setNodeMap(nodes);
		return result;
	}

	private void setNodeMap(HashMap<Long, INode> nodes) {
		this.nodesMap = nodes;

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Context) {
			Context other = (Context) obj;
			if (this.entities.size() == other.entities.size() && this.actions.size() == other.actions.size()
					&& this.concepts.size() == other.concepts.size()) {
				boolean entitiesmatch = contextIndividualMatch(this.entities, other.entities);//this.entities.containsAll(other.entities) && other.entities.containsAll(this.entities);

				boolean actionsmatch = contextIndividualMatch(this.actions, other.actions);
				boolean conceptsmatch = conceptsMatch(this.concepts, other.concepts);//this.concepts.containsAll(other.concepts) && other.concepts.containsAll(this.concepts);
				return entitiesmatch && actionsmatch && conceptsmatch;
			}
		}
		return false;
	}

	private boolean contextIndividualMatch(Set<? extends ContextIndividual> current, Set<? extends ContextIndividual> other) {
		if (current.size() == other.size()) {
			boolean result = true;
			for (ContextIndividual currentIndividual : current) {
				boolean individualResult = false;
				for (ContextIndividual otherIndividual : other) {
					if (currentIndividual.equals(otherIndividual)) {
						individualResult = true;
						break;
					}
				}
				if (individualResult != true) {
					result = false;
				}
			}
			return result;
		}
		return false;
	}

	private boolean conceptsMatch(Set<? extends AbstractConcept> current, Set<? extends AbstractConcept> other) {
		if (current.size() == other.size()) {
			boolean result = true;
			for (AbstractConcept currentIndividual : current) {
				boolean individualResult = false;
				for (AbstractConcept otherIndividual : other) {
					if (currentIndividual.equalsComplex(otherIndividual)) {
						individualResult = true;
						break;
					}
				}
				if (individualResult != true) {
					result = false;
				}
			}
			return result;
		}
		return false;
	}

	public boolean hasEntity(String name, List<INode> reference) {
		for (Entity entity : entities) {
			if (entity.getName().equals(name) && entity.getReference().containsAll(reference)) {
				return true;
			}
		}
		return false;
	}

	public Entity getEntity(String name, List<INode> reference) {
		for (Entity entity : entities) {
			if (entity.getName().equals(name) && entity.getReference().containsAll(reference)) {
				return entity;
			}
		}
		return null;
	}

	public boolean hasAction(String name, List<INode> reference) {
		for (Action action : actions) {
			if (action.getName().equals(name) && action.getReference().containsAll(reference)) {
				return true;
			}
		}
		return false;
	}

	public Action getAction(String name, List<INode> reference) {
		for (Action action : actions) {
			if (action.getName().equals(name) && action.getReference().containsAll(reference)) {
				return action;
			}
		}
		return null;
	}

	public boolean isEmpty() {
		return entities.isEmpty() && actions.isEmpty() && concepts.isEmpty();
	}

	/**
	 * @return the concepts
	 */
	public Set<AbstractConcept> getConcepts() {
		return concepts;
	}

	/**
	 * @param concepts
	 *            the concepts to set
	 */
	public void setConcepts(Set<AbstractConcept> concepts) {
		this.concepts = concepts;
	}

	public boolean addConcept(AbstractConcept concept) {
		concept.setID(id);
		id++;
		return this.concepts.add(concept);
	}

	/**
	 * Returns if the current context has an {@link AbstractConcept} with the
	 * specified ontologyIndividualName
	 * 
	 * @param ontologyIndividual
	 * @return
	 */
	public boolean hasConceptOfIndividual(String ontologyIndividual) {
		for (AbstractConcept abstractConcept : concepts) {
			if (abstractConcept.getOntologyIndividual().equals(ontologyIndividual)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the {@link AbstractConcept} with the specified ontologyIndividualName
	 * 
	 * @param ontologyIndividual
	 *            the ontologyIndividualName to search for
	 * @return the {@link AbstractConcept} with the specified ontologyIndividualName
	 */
	public AbstractConcept getConceptOfIndividual(String ontologyIndividual) {
		for (AbstractConcept abstractConcept : concepts) {
			if (abstractConcept.getOntologyIndividual().equals(ontologyIndividual)) {
				return abstractConcept;
			}
		}
		return null;
	}

	/**
	 * Returns if the current context has an {@link AbstractConcept} with the
	 * specified name
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasConcept(String name) {
		for (AbstractConcept abstractConcept : concepts) {
			if (abstractConcept.getName().toLowerCase().equals(name.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the {@link AbstractConcept} with the specified name
	 * 
	 * @param name
	 *            The name to search for
	 * @return the {@link AbstractConcept} with the specified name
	 */
	public AbstractConcept getConcept(String name) {
		for (AbstractConcept abstractConcept : concepts) {
			if (abstractConcept.getName().toLowerCase().equals(name.toLowerCase())) {
				return abstractConcept;
			}
		}
		return null;
	}

	/**
	 * Returns if the Context was read in from the graph or newly created
	 * 
	 * @return
	 */
	public boolean isReadFromGraph() {
		return readFromGraph;
	}

}
