/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SpeakerEntity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public class Context {

	private Set<Entity> entities;

	private Set<Action> actions;

	private Set<AbstractConcept> concepts;

	private HashMap<ContextIndividual, INode> nodesMap;

	private boolean readFromGraph = false;

	public Context() {
		entities = new HashSet<Entity>();
		actions = new HashSet<Action>();
		setConcepts(new HashSet<AbstractConcept>());
	}

	private Context(HashMap<ContextIndividual, INode> graphNodeMap) {
		entities = new HashSet<Entity>();
		actions = new HashSet<Action>();
		setConcepts(new HashSet<AbstractConcept>());
		this.nodesMap = graphNodeMap;
		if (!graphNodeMap.isEmpty()) {
			readFromGraph = true;
		}

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
		return this.entities.add(entity);
	}

	public boolean addEntities(List<Entity> entities) {
		return this.entities.addAll(entities);
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
		return this.actions.add(action);

	}

	public boolean addActions(List<Action> actions) {
		return this.actions.addAll(actions);

	}

	public void printToGraph(IGraph graph) {
		HashMap<ContextIndividual, INode> graphNodesMap = new HashMap<>(nodesMap);
		HashSet<Relation> relations = new HashSet<>();
		HashSet<Relation> alreadyUpdated = new HashSet<>();
		if (readFromGraph) {
			for (Entity entity : entities) {
				if (entity.hasChanged()) {
					if (this.nodesMap.containsKey(entity)) {

						alreadyUpdated.addAll(entity.updateNode(this.nodesMap.get(entity), graph, graphNodesMap));

					} else {
						graphNodesMap.put(entity, entity.printToGraph(graph));

					}
					relations.addAll(entity.getRelations());
				} else if (!this.nodesMap.containsKey(entity)) {
					graphNodesMap.put(entity, entity.printToGraph(graph));

					relations.addAll(entity.getRelations());

				}
			}

			for (Action action : actions) {
				if (action.hasChanged()) {
					if (this.nodesMap.containsKey(action)) {
						alreadyUpdated.addAll(action.updateNode(this.nodesMap.get(action), graph, graphNodesMap));
					} else {
						graphNodesMap.put(action, action.printToGraph(graph));

					}
					relations.addAll(action.getRelations());
				} else if (!this.nodesMap.containsKey(action)) {
					graphNodesMap.put(action, action.printToGraph(graph));

					relations.addAll(action.getRelations());
				} else {
					graphNodesMap.put(action, this.nodesMap.get(action));
				}

			}

			for (AbstractConcept concept : concepts) {
				if (concept.hasChanged()) {
					if (this.nodesMap.containsKey(concept)) {
						alreadyUpdated.addAll(concept.updateNode(this.nodesMap.get(concept), graph, graphNodesMap));
					} else {
						graphNodesMap.put(concept, concept.printToGraph(graph));

					}
					relations.addAll(concept.getRelations());
				} else if (!this.nodesMap.containsKey(concept)) {
					graphNodesMap.put(concept, concept.printToGraph(graph));

					relations.addAll(concept.getRelations());
				} else {
					graphNodesMap.put(concept, this.nodesMap.get(concept));
				}
			}
			for (AbstractConcept concept : concepts) {
				if (concept.hasChanged() || !this.nodesMap.containsKey(concept)) {

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
				graphNodesMap.put(entity, entity.printToGraph(graph));

				relations.addAll(entity.getRelations());
			}
			for (Action action : actions) {
				graphNodesMap.put(action, action.printToGraph(graph));
				relations.addAll(action.getRelations());
			}
			for (AbstractConcept concept : concepts) {
				graphNodesMap.put(concept, concept.printToGraph(graph));
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

		HashMap<INode, ContextIndividual> graphMap = new HashMap<INode, ContextIndividual>();
		HashMap<ContextIndividual, INode> graphNodeMap = new HashMap<>();
		List<Action> actions = new ArrayList<Action>();
		List<Entity> entities = new ArrayList<Entity>();
		List<AbstractConcept> concepts = new ArrayList<AbstractConcept>();

		if (graph.hasNodeType(Entity.ENTITY_NODE_TYPE)) {
			Set<INode> entityNodes = graph.getNodesOfType(graph.getNodeType(Entity.ENTITY_NODE_TYPE));
			for (INode entityNode : entityNodes) {

				Entity entity = Entity.readFromNode(entityNode, graph);
				graphMap.put(entityNode, entity);
				graphNodeMap.put(entity, entityNode);
				entities.add(entity);

			}
		}
		if (graph.hasNodeType(Action.ACTION_NODE_TYPE)) {
			Set<INode> actionNodes = graph.getNodesOfType(graph.getNodeType(Action.ACTION_NODE_TYPE));
			for (INode actionNode : actionNodes) {
				Action action = Action.readFromNode(actionNode, graph);
				graphMap.put(actionNode, action);
				graphNodeMap.put(action, actionNode);
				actions.add(action);
			}
		}
		if (graph.hasNodeType(AbstractConcept.CONCEPT_NODE_TYPE)) {
			Set<INode> conceptNodes = graph.getNodesOfType(graph.getNodeType(AbstractConcept.CONCEPT_NODE_TYPE));
			for (INode node : conceptNodes) {
				AbstractConcept concept = AbstractConcept.readFromNode(node, graph);
				graphMap.put(node, concept);
				graphNodeMap.put(concept, node);
				concepts.add(concept);
			}
		}
		for (AbstractConcept concept : concepts) {
			concept.readConceptRelationsOfNode(graphNodeMap.get(concept), graphMap, graph);
		}
		if (graph.hasArcType(Relation.RELATION_ARC_TYPE)) {
			Set<IArc> relationArcs = graph.getArcsOfType(graph.getArcType(Relation.RELATION_ARC_TYPE));
			for (IArc relationArc : relationArcs) {
				Relation.readFromArc(relationArc, graphMap, graph);
			}
		}
		Context result = new Context(graphNodeMap);
		result.addActions(actions);
		result.addEntities(entities);
		result.addConcepts(concepts);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Context) {
			Context other = (Context) obj;
			if (this.entities.size() == other.entities.size() && this.actions.size() == other.actions.size()
					&& this.concepts.size() == other.concepts.size()) {
				boolean entitiesmatch = contextIndividualMatch(this.entities, other.entities);//this.entities.containsAll(other.entities) && other.entities.containsAll(this.entities);

				boolean actionsmatch = contextIndividualMatch(this.actions, other.actions);
				boolean conceptsmatch = contextIndividualMatch(this.concepts, other.concepts);//this.concepts.containsAll(other.concepts) && other.concepts.containsAll(this.concepts);
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
		return this.concepts.add(concept);
	}

	public boolean addConcepts(List<AbstractConcept> concepts) {
		return this.concepts.addAll(concepts);
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
	 * Returns the {@link AbstractConcept} with the specified
	 * ontologyIndividualName
	 * 
	 * @param ontologyIndividual
	 *            the ontologyIndividualName to search for
	 * @return the {@link AbstractConcept} with the specified
	 *         ontologyIndividualName
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
