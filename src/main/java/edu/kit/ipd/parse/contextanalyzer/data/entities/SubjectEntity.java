/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
public class SubjectEntity extends Entity {

	private Gender gender;

	private boolean isSystem;

	public static enum Gender {
		MALE, FEMALE, NEUTRAL, UNKNOWN;
	}

	protected static final String TYPE = "Subject";

	/**
	 * @param name
	 * @param grammaticalNumber
	 * @param reference
	 */
	public SubjectEntity(String name, GrammaticalNumber grammaticalNumber, List<INode> reference, Gender gender, boolean isSystem) {
		super(name, grammaticalNumber, reference);
		this.gender = gender;
		this.setSystem(isSystem);
	}

	/**
	 * @param name
	 * @param grammaticalNumber
	 * @param reference
	 */
	public SubjectEntity(String name, GrammaticalNumber grammaticalNumber, List<INode> reference) {
		super(name, grammaticalNumber, reference);
		this.gender = Gender.UNKNOWN;
	}

	/**
	 * @return the gender
	 */
	public Gender getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(Gender gender) {
		this.changed = true;
		this.gender = gender;
	}

	@Override
	public Set<Relation> updateNode(INode node, IGraph graph, HashMap<ContextIndividual, INode> graphNodes) {
		Set<Relation> alreadyUpdated = super.updateNode(node, graph, graphNodes);
		node.setAttributeValue(ENTITY_TYPE, TYPE);
		node.setAttributeValue(GENDER, getGender());
		node.setAttributeValue(IS_SYSTEM, isSystem);
		return alreadyUpdated;
	}

	@Override
	public INode printToGraph(IGraph graph) {
		INode node = super.printToGraph(graph);
		node.setAttributeValue(ENTITY_TYPE, TYPE);
		node.setAttributeValue(GENDER, getGender());
		node.setAttributeValue(IS_SYSTEM, isSystem);
		return node;

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SubjectEntity) {
			SubjectEntity other = (SubjectEntity) obj;
			return super.equals(obj) && Objects.equals(gender, other.gender) && isSystem == other.isSystem;

		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = this.gender == null ? hash : 31 * hash + this.gender.hashCode();
		hash = 31 * hash + Boolean.hashCode(isSystem);
		return hash;
	}

	public static Entity readFromNode(INode node, IGraph graph) {
		SubjectEntity entity = null;
		String name = (String) node.getAttributeValue(ENTITY_NAME);
		GrammaticalNumber gNumber = (GrammaticalNumber) node.getAttributeValue(GRAMMATICAL_NUMBER);
		CommandType cmdType = (CommandType) node.getAttributeValue(COMMAND_TYPE);
		int statement = (int) node.getAttributeValue(STATEMENT);
		Gender gender = (Gender) node.getAttributeValue(GENDER);
		boolean isSystem = (Boolean) node.getAttributeValue(IS_SYSTEM);
		Set<? extends IArc> references = node.getOutgoingArcsOfType(graph.getArcType(REFERENCE));
		List<List<INode>> refs = new ArrayList<List<INode>>();
		for (IArc arc : references) {
			List<INode> reference = GraphUtils.getNodesOfArcChain(arc, graph);
			refs.add(reference);
		}
		entity = new SubjectEntity(name, gNumber, refs.get(0), gender, isSystem);
		entity.setCommandType(cmdType);
		entity.setStatement(statement);
		for (List<INode> list : refs.subList(1, refs.size())) {
			entity.setReference(list);
		}
		return entity;

	}

	@Override
	public boolean integrateEntityInformation(Entity entity) {
		if (entity instanceof SubjectEntity) {
			SubjectEntity other = (SubjectEntity) entity;

			boolean changed = super.integrateEntityInformation(entity);
			if (!Objects.equals(gender, other.getGender())) {
				setGender(other.getGender());
				changed = true;
			}
			this.changed = changed;
			return changed;
		} else {
			throw new IllegalArgumentException("Entity to integrate has not a matching Type");
		}

	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
}
