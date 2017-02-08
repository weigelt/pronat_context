/**
 *
 */
package edu.kit.ipd.parse.contextanalyzer.data.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.ipd.parse.contextanalyzer.data.CommandType;
import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.contextanalyzer.data.State;
import edu.kit.ipd.parse.contextanalyzer.data.relations.EntityStateRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ReferentRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.util.GraphUtils;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public class PronounEntity extends Entity implements IStateOwner {

	protected static final String REFERENT = "referent";
	protected static final String TYPE = "Pronoun";

	/**
	 * @param name
	 * @param grammaticalNumber
	 * @param reference
	 */
	public PronounEntity(String name, GrammaticalNumber grammaticalNumber, List<INode> reference) {
		super(name, grammaticalNumber, reference);
	}

	/**
	 * @return the referent
	 */
	public Entity getReferent() {
		Entity referent = null;
		if (hasRelationsOfType(ReferentRelation.class)) {
			List<Relation> relations = getRelationsOfType(ReferentRelation.class);
			double confidence = 0;

			for (Relation relation : relations) {
				ReferentRelation rel = (ReferentRelation) relation;
				if (rel.getStart().equals(this) && rel.getConfidence() > confidence) {
					referent = rel.getEnd();
					confidence = rel.getConfidence();
				}
			}

		}
		return referent;
	}

	@Override
	public Set<Relation> updateNode(INode node, IGraph graph, HashMap<ContextIndividual, INode> graphNodes) {
		Set<Relation> alreadyUpdated = super.updateNode(node, graph, graphNodes);
		node.setAttributeValue(ENTITY_TYPE, TYPE);
		return alreadyUpdated;
	}

	@Override
	public INode printToGraph(IGraph graph) {
		INode node = super.printToGraph(graph);
		node.setAttributeValue(ENTITY_TYPE, TYPE);
		return node;

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PronounEntity) {
			PronounEntity other = (PronounEntity) obj;
			return super.equals(other);

		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public static Entity readFromNode(INode node, IGraph graph) {
		PronounEntity entity = null;
		String name = (String) node.getAttributeValue(ENTITY_NAME);
		GrammaticalNumber gNumber = (GrammaticalNumber) node.getAttributeValue(GRAMMATICAL_NUMBER);
		CommandType cmdType = (CommandType) node.getAttributeValue(COMMAND_TYPE);
		int statement = (int) node.getAttributeValue(STATEMENT);
		List<? extends IArc> references = node.getOutgoingArcsOfType(graph.getArcType(REFERENCE));
		List<List<INode>> refs = new ArrayList<List<INode>>();
		for (IArc arc : references) {
			List<INode> reference = GraphUtils.getNodesOfArcChain(arc, graph);
			refs.add(reference);
		}
		entity = new PronounEntity(name, gNumber, refs.get(0));
		entity.setCommandType(cmdType);
		entity.setStatement(statement);
		for (List<INode> list : refs.subList(1, refs.size())) {
			entity.setReference(list);
		}
		return entity;

	}

	@Override
	public boolean integrateEntityInformation(Entity entity) {
		if (entity instanceof PronounEntity) {
			PronounEntity other = (PronounEntity) entity;

			boolean changed = super.integrateEntityInformation(other);
			this.changed = changed;
			return changed;
		} else {
			throw new IllegalArgumentException("Entity to integrate has not a matching Type");
		}

	}

	@Override
	public boolean hasState() {
		return hasRelationsOfType(EntityStateRelation.class);
	}

	@Override
	public Set<State> getStates() {
		Set<State> states = new HashSet<>();
		for (Relation rel : getRelationsOfType(EntityStateRelation.class)) {
			EntityStateRelation esRel = (EntityStateRelation) rel;
			states.add((State) esRel.getEnd());
		}
		return states;
	}

}
