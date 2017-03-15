/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.kit.ipd.parse.contextanalyzer.data.CommandType;
import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.util.GraphUtils;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public class SpeakerEntity extends Entity {

	protected static final String TYPE = "Speaker";

	public SpeakerEntity(String name, GrammaticalNumber grammaticalNumber, List<INode> reference) {
		super(name, grammaticalNumber, reference);
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
		if (obj instanceof SpeakerEntity) {
			SpeakerEntity other = (SpeakerEntity) obj;
			return super.equals(other);

		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public static Entity readFromNode(INode node, IGraph graph) {
		SpeakerEntity entity = null;
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
		entity = new SpeakerEntity(name, gNumber, refs.get(0));
		entity.setCommandType(cmdType);
		entity.setStatement(statement);
		for (List<INode> list : refs.subList(1, refs.size())) {
			entity.setReference(list);
		}
		entity.changed = false;
		return entity;

	}

	@Override
	public boolean integrateEntityInformation(Entity entity) {
		if (entity instanceof SpeakerEntity) {
			SpeakerEntity other = (SpeakerEntity) entity;

			return super.integrateEntityInformation(other);
		} else {
			throw new IllegalArgumentException("Entity to integrate has not a matching Type");
		}

	}

}
