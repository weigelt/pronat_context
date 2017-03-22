/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import edu.kit.ipd.parse.contextanalyzer.data.CommandType;
import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.contextanalyzer.data.EntityConcept;
import edu.kit.ipd.parse.contextanalyzer.data.relations.EntityConceptRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.EntityEntityRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.graph.INodeType;
import edu.kit.ipd.parse.luna.graph.Pair;

/**
 * @author Tobias Hey
 *
 */
public abstract class Entity extends ContextIndividual implements Comparable<Entity> {

	public static final String ENTITY_NODE_TYPE = "contextEntity";
	protected static final String REFERENCE = "reference";
	protected static final String HOLONYMS = "holonyms";
	protected static final String MERONYMS = "meronyms";
	protected static final String DIRECT_HYPONYMS = "directHyponyms";
	protected static final String DIRECT_HYPERNYMS = "directHypernyms";
	protected static final String SYNONYMS = "synonyms";
	protected static final String DESCRIBING_ADJECTIVES = "describingAdjectives";
	protected static final String QUANTITY = "quantity";
	protected static final String POSSESSIVE_PRONOUN = "posessivePronouns";
	protected static final String DETERMINER = "determiner";
	protected static final String GENDER = "gender";
	protected static final String ENTITY_TYPE = "typeOfEntity";
	protected static final String GRAMMATICAL_NUMBER = "grammaticalNumber";
	public static final String ENTITY_NAME = "name";
	protected static final String COMMAND_TYPE = "cmdtype";
	protected static final String STATEMENT = "statementNumber";
	protected static final String IS_SYSTEM = "isSystem";
	protected static final String WN_SENSE = "wnSense";

	private String name = "";

	private GrammaticalNumber grammaticalNumber;

	private CommandType commandType = CommandType.INDEPENDENT_STATEMENT;

	private int statement = -1;

	private List<INode> reference = null;

	protected Entity(String name, GrammaticalNumber grammaticalNumber, List<INode> reference) {
		super();
		this.name = name;
		this.grammaticalNumber = grammaticalNumber;
		this.reference = reference;
		this.commandType = CommandType.INDEPENDENT_STATEMENT;
		this.statement = -1;
		this.changed = false;
	}

	/**
	 * @return the grammaticalNumber
	 */
	public GrammaticalNumber getGrammaticalNumber() {
		return grammaticalNumber;
	}

	/**
	 * @param grammaticalNumber
	 *            the grammaticalNumber to set
	 */
	public void setGrammaticalNumber(GrammaticalNumber grammaticalNumber) {
		if (!Objects.equals(this.grammaticalNumber, grammaticalNumber)) {
			this.changed = true;
			this.grammaticalNumber = grammaticalNumber;
		}
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
			this.name = name;
			this.changed = true;
		}
	}

	/**
	 * @return the reference
	 */
	public List<INode> getReference() {
		return reference;
	}

	/**
	 * @param reference
	 *            the reference to set
	 */
	public void setReference(List<INode> reference) {
		if (!Objects.equals(this.reference, reference)) {
			this.reference = reference;
			this.changed = true;
		}
	}

	@Override
	public String toString() {
		return "[" + name + ", " + grammaticalNumber + "]\n";
	}

	@Override
	public Set<Relation> updateNode(INode node, IGraph graph, HashMap<Long, INode> graphNodes) {
		Set<Relation> alreadyUpdated = super.updateNode(node, graph, graphNodes);
		node.setAttributeValue(ENTITY_NAME, getName());
		node.setAttributeValue(GRAMMATICAL_NUMBER, getGrammaticalNumber());
		node.setAttributeValue(COMMAND_TYPE, getCommandType());
		node.setAttributeValue(STATEMENT, getStatement());
		return alreadyUpdated;
	}

	public INode printToGraph(IGraph graph) {
		INodeType nodeType;
		if (graph.hasNodeType(ENTITY_NODE_TYPE)) {
			nodeType = graph.getNodeType(ENTITY_NODE_TYPE);
		} else {
			nodeType = createEntityNodeType(graph);
		}
		INode node = graph.createNode(nodeType);
		node.setAttributeValue(ENTITY_NAME, getName());
		node.setAttributeValue(GRAMMATICAL_NUMBER, getGrammaticalNumber());
		node.setAttributeValue(COMMAND_TYPE, getCommandType());
		node.setAttributeValue(STATEMENT, getStatement());
		setReferenceArcs(graph, node);
		return node;
	}

	private INodeType createEntityNodeType(IGraph graph) {
		INodeType nodeType = graph.createNodeType(ENTITY_NODE_TYPE);
		nodeType.addAttributeToType("String", ENTITY_NAME);
		nodeType.addAttributeToType("String", GRAMMATICAL_NUMBER);
		nodeType.addAttributeToType("String", COMMAND_TYPE);
		nodeType.addAttributeToType("int", STATEMENT);
		nodeType.addAttributeToType("String", ENTITY_TYPE);
		nodeType.addAttributeToType("String", GENDER);
		nodeType.addAttributeToType("String", DETERMINER);
		nodeType.addAttributeToType("String", QUANTITY);
		nodeType.addAttributeToType("String", POSSESSIVE_PRONOUN);
		nodeType.addAttributeToType("String", DESCRIBING_ADJECTIVES);
		nodeType.addAttributeToType("String", SYNONYMS);
		nodeType.addAttributeToType("String", DIRECT_HYPERNYMS);
		nodeType.addAttributeToType("String", DIRECT_HYPONYMS);
		nodeType.addAttributeToType("String", MERONYMS);
		nodeType.addAttributeToType("String", HOLONYMS);
		nodeType.addAttributeToType("String", IS_SYSTEM);
		nodeType.addAttributeToType(new Pair<String, Double>("", 0.0d).getClass().getName(), WN_SENSE);
		return nodeType;
	}

	private void setReferenceArcs(IGraph graph, INode node) {
		IArcType arcType;
		if (graph.hasArcType(REFERENCE)) {
			arcType = graph.getArcType(REFERENCE);
		} else {
			arcType = graph.createArcType(REFERENCE);
		}

		graph.createArc(node, reference.get(0), arcType);

		Iterator<INode> iterator = reference.iterator();
		INode src = null;
		if (iterator.hasNext()) {
			src = iterator.next();
		}
		while (iterator.hasNext()) {
			INode tar = iterator.next();
			graph.createArc(src, tar, arcType);
			src = tar;
		}

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Entity) {
			Entity other = (Entity) obj;
			boolean result = Objects.equals(name, other.name) && Objects.equals(grammaticalNumber, other.grammaticalNumber)
					&& Objects.equals(commandType, other.commandType) && Objects.equals(statement, other.statement)
					&& Objects.equals(reference, other.reference) && Objects.equals(this.getRelations(), other.getRelations());

			return result;
		}
		return false;
	}

	public boolean equalsWithoutRelation(Object obj) {
		if (obj instanceof Entity) {
			Entity other = (Entity) obj;
			boolean result = Objects.equals(name, other.name) && Objects.equals(grammaticalNumber, other.grammaticalNumber)
					&& Objects.equals(commandType, other.commandType) && statement == other.statement
					&& Objects.equals(reference, other.reference);
			return result;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = name.hashCode();
		hash = this.commandType == null ? hash : 31 * hash + this.commandType.hashCode();
		hash = Integer.hashCode(statement) + 31 * hash;
		hash = this.grammaticalNumber == null ? hash : 31 * hash + this.grammaticalNumber.hashCode();
		hash = this.reference == null ? hash : 31 * hash + this.reference.hashCode();

		return hash;
	}

	public static Entity readFromNode(INode entityNode, IGraph graph) {
		switch ((String) entityNode.getAttributeValue(ENTITY_TYPE)) {
		case SubjectEntity.TYPE:
			return SubjectEntity.readFromNode(entityNode, graph);

		case PronounEntity.TYPE:
			return PronounEntity.readFromNode(entityNode, graph);

		case ObjectEntity.TYPE:
			return ObjectEntity.readFromNode(entityNode, graph);
		case SpeakerEntity.TYPE:
			return SpeakerEntity.readFromNode(entityNode, graph);
		default:
			return null;
		}
	}

	public boolean integrateEntityInformation(Entity entity) {
		boolean changed = false;
		if (!this.name.equals(entity.getName())) {
			setName(entity.getName());
			changed = true;
		}
		if (!this.grammaticalNumber.equals(entity.getGrammaticalNumber())) {
			setGrammaticalNumber(entity.getGrammaticalNumber());
			changed = true;
		}
		if (!this.commandType.equals(entity.getCommandType())) {
			setCommandType(entity.getCommandType());
			changed = true;
		}
		if (this.statement != entity.getStatement()) {
			setStatement(entity.getStatement());
			changed = true;
		}
		for (INode iNode : entity.getReference()) {
			if (!this.reference.contains(iNode)) {
				this.reference.add(iNode);
				changed = true;
			}
		}
		for (Relation relation : entity.getRelations()) {
			if (relation instanceof EntityEntityRelation) {
				EntityEntityRelation rel = (EntityEntityRelation) relation;
				if (rel.getStart().equals(entity)) {
					rel.setStart(this);
					changed = changed || this.getRelations().add(rel);

				} else if (rel.getEnd().equals(entity)) {
					rel.setEnd(this);
					changed = changed || this.getRelations().add(rel);
				}
			}
		}
		this.changed = changed;
		return changed;
	}

	@Override
	public int compareTo(Entity o) {
		int positionThis = (int) reference.get(0).getAttributeValue("position");
		int positionOther = (int) o.reference.get(0).getAttributeValue("position");
		return Integer.compare(positionThis, positionOther);
	}

	/**
	 * @return the commandType
	 */
	public CommandType getCommandType() {
		return commandType;
	}

	/**
	 * @param commandType
	 *            the commandType to set
	 */
	public void setCommandType(CommandType commandType) {
		if (!Objects.equals(this.commandType, commandType)) {
			this.changed = true;
			this.commandType = commandType;
		}
	}

	/**
	 * @return the statement
	 */
	public int getStatement() {
		return statement;
	}

	/**
	 * @param statement
	 *            the statement to set
	 */
	public void setStatement(int statement) {
		if (this.statement != statement) {
			this.changed = true;
			this.statement = statement;
		}
	}

	public boolean hasAssociatedConcept() {
		if (hasRelationsOfType(EntityConceptRelation.class)) {
			for (Relation rel : getRelationsOfType(EntityConceptRelation.class)) {
				EntityConceptRelation ecRel = (EntityConceptRelation) rel;
				if (ecRel.getEnd() instanceof EntityConcept) {
					return true;
				}
			}
		}
		return false;
	}

	public Set<EntityConceptRelation> getAssociatedConcepts() {
		Set<EntityConceptRelation> result = new HashSet<>();
		for (Relation rel : getRelationsOfType(EntityConceptRelation.class)) {
			EntityConceptRelation ecRel = (EntityConceptRelation) rel;
			if (ecRel.getEnd() instanceof EntityConcept) {
				result.add(ecRel);
			}
		}
		return result;
	}

}
