/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import edu.kit.ipd.parse.contextanalyzer.data.CommandType;
import edu.kit.ipd.parse.contextanalyzer.data.State;
import edu.kit.ipd.parse.contextanalyzer.data.relations.EntityStateRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.util.GraphUtils;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.graph.Pair;

/**
 * @author Tobias Hey
 *
 */
public class ObjectEntity extends Entity implements IStateOwner {

	protected static final String TYPE = "Object";

	public static enum DeterminerType {
		SPECIFIC, GENERAL, UNKNOWN;
	}

	private DeterminerType determiner = DeterminerType.UNKNOWN;

	private String quantity = "one";

	private Pair<String, Double> wnSense = null;

	private List<String> possessivePronouns;

	private List<String> describingAdjectives;

	private Set<String> synonyms;

	private Set<String> directHypernyms;

	private Set<String> directHyponyms;

	private Set<String> meronyms;

	private Set<String> holonyms;

	/**
	 * @param name
	 * @param grammaticalNumber
	 * @param reference
	 * @param list
	 * @param hypernyms
	 */
	public ObjectEntity(String name, GrammaticalNumber grammaticalNumber, DeterminerType det, String quantity,
			List<String> possessivePronouns, List<INode> reference, List<String> describingAdjectives, List<String> synonyms,
			List<String> directHypernyms, List<String> directHyponyms, List<String> meronyms, List<String> holonyms) {
		super(name, grammaticalNumber, reference);
		this.setDeterminer(det);
		this.setQuantity(quantity);
		this.setPossessivePronouns(possessivePronouns);
		this.synonyms = new HashSet<>();
		this.synonyms.addAll(synonyms);
		this.directHypernyms = new HashSet<>();
		this.directHypernyms.addAll(directHypernyms);
		this.directHyponyms = new HashSet<>();
		this.directHyponyms.addAll(directHyponyms);
		this.holonyms = new HashSet<>();
		this.holonyms.addAll(holonyms);
		this.meronyms = new HashSet<>();
		this.meronyms.addAll(meronyms);
		this.setDescribingAdjectives(describingAdjectives);
		this.changed = false;
	}

	public ObjectEntity(String name, GrammaticalNumber gNumber, DeterminerType det, String quantity, List<String> possessivePronouns,
			List<INode> reference, List<String> describingAdjectives) {
		super(name, gNumber, reference);
		this.setDeterminer(det);
		this.setQuantity(quantity);
		this.setPossessivePronouns(possessivePronouns);
		this.setDescribingAdjectives(describingAdjectives);
		this.synonyms = new HashSet<>();

		this.directHypernyms = new HashSet<>();

		this.directHyponyms = new HashSet<>();

		this.holonyms = new HashSet<>();

		this.meronyms = new HashSet<>();
		this.changed = false;
	}

	/**
	 * @return the synonyms
	 */
	public Set<String> getSynonyms() {
		return synonyms;
	}

	/**
	 * @param synonyms
	 *            the synonyms to set
	 */
	public void setSynonyms(Set<String> synonyms) {
		if (!Objects.equals(this.synonyms, synonyms)) {
			this.changed = true;
			this.synonyms = synonyms;
		}
	}

	/**
	 * @return the directHypernyms
	 */
	public Set<String> getDirectHypernyms() {
		return directHypernyms;
	}

	/**
	 * @param directHypernyms
	 *            the directHypernyms to set
	 */
	public void setDirectHypernyms(Set<String> directHypernyms) {
		if (!Objects.equals(this.directHypernyms, directHypernyms)) {
			this.changed = true;
			this.directHypernyms = directHypernyms;
		}
	}

	/**
	 * @return the directHyponyms
	 */
	public Set<String> getDirectHyponyms() {
		return directHyponyms;
	}

	/**
	 * @param directHyponyms
	 *            the directHyponyms to set
	 */
	public void setDirectHyponyms(Set<String> directHyponyms) {
		if (!Objects.equals(this.directHyponyms, directHyponyms)) {
			this.changed = true;
			this.directHyponyms = directHyponyms;
		}
	}

	/**
	 * @return the meronyms
	 */
	public Set<String> getMeronyms() {
		return meronyms;
	}

	/**
	 * @param meronyms
	 *            the meronyms to set
	 */
	public void setMeronyms(Set<String> meronyms) {
		if (!Objects.equals(this.meronyms, meronyms)) {
			this.changed = true;
			this.meronyms = meronyms;
		}
	}

	/**
	 * @return the holonyms
	 */
	public Set<String> getHolonyms() {
		return holonyms;
	}

	/**
	 * @param holonyms
	 *            the holonyms to set
	 */
	public void setHolonyms(Set<String> holonyms) {
		if (!Objects.equals(this.holonyms, holonyms)) {
			this.changed = true;
			this.holonyms = holonyms;
		}
	}

	public String getNameWithAdjectives() {
		String result = "";
		for (String string : describingAdjectives) {
			result += string + " ";
		}
		result += getName();
		return result;
	}

	@Override
	public String toString() {
		String output = "[";
		for (String string : describingAdjectives) {
			output += string + " ";
		}
		output += getName();
		output += ", " + getGrammaticalNumber();
		output += ", " + getDeterminer();
		output += ", " + quantity;
		output += "| Synonyms: {";
		for (String string : synonyms) {
			output += string + ", ";
		}
		output += "} | Hypernyms: {";
		for (String string : directHypernyms) {
			output += string + ", ";
		}
		output += "} | Hyponyms: {";
		for (String string : directHyponyms) {
			output += string + ", ";
		}
		output += "} | Meronyms: {";
		for (String string : meronyms) {
			output += string + ", ";
		}
		output += "} | Holonyms: {";
		for (String string : holonyms) {
			output += string + ", ";
		}
		output += "}]\n";
		return output;
	}

	/**
	 * @return the describingAdjectives
	 */
	public List<String> getDescribingAdjectives() {
		return describingAdjectives;
	}

	/**
	 * @param describingAdjectives
	 *            the describingAdjectives to set
	 */
	public void setDescribingAdjectives(List<String> describingAdjectives) {
		if (!Objects.equals(this.describingAdjectives, describingAdjectives)) {
			this.changed = true;
			this.describingAdjectives = describingAdjectives;
		}
	}

	/**
	 * @return the determiner
	 */
	public DeterminerType getDeterminer() {
		return determiner;
	}

	/**
	 * @param determiner
	 *            the determiner to set
	 */
	public void setDeterminer(DeterminerType determiner) {
		if (!Objects.equals(this.determiner, determiner)) {
			this.changed = true;
			this.determiner = determiner;
		}
	}

	/**
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 *            the quantity to set
	 */
	public void setQuantity(String quantity) {
		if (!Objects.equals(this.quantity, quantity)) {
			this.changed = true;
			this.quantity = quantity;
		}
	}

	@Override
	public Set<Relation> updateNode(INode node, IGraph graph, HashMap<Long, INode> graphNodes) {
		Set<Relation> alreadyUpdated = super.updateNode(node, graph, graphNodes);
		node.setAttributeValue(ENTITY_TYPE, TYPE);
		node.setAttributeValue(DETERMINER, getDeterminer());
		node.setAttributeValue(QUANTITY, getQuantity());
		node.setAttributeValue(WN_SENSE, getWNSense());
		node.setAttributeValue(POSSESSIVE_PRONOUN, Arrays.toString(getPossessivePronouns().toArray()));
		node.setAttributeValue(DESCRIBING_ADJECTIVES, Arrays.toString(getDescribingAdjectives().toArray()));
		node.setAttributeValue(SYNONYMS, Arrays.toString(getSynonyms().toArray()));
		node.setAttributeValue(DIRECT_HYPERNYMS, Arrays.toString(getDirectHypernyms().toArray()));
		node.setAttributeValue(DIRECT_HYPONYMS, Arrays.toString(getDirectHyponyms().toArray()));
		node.setAttributeValue(MERONYMS, Arrays.toString(getMeronyms().toArray()));
		node.setAttributeValue(HOLONYMS, Arrays.toString(getHolonyms().toArray()));
		return alreadyUpdated;
	}

	@Override
	public INode printToGraph(IGraph graph) {
		INode node = super.printToGraph(graph);

		node.setAttributeValue(ENTITY_TYPE, TYPE);
		node.setAttributeValue(DETERMINER, getDeterminer());
		node.setAttributeValue(QUANTITY, getQuantity());
		node.setAttributeValue(WN_SENSE, getWNSense());
		node.setAttributeValue(POSSESSIVE_PRONOUN, Arrays.toString(getPossessivePronouns().toArray()));
		node.setAttributeValue(DESCRIBING_ADJECTIVES, Arrays.toString(getDescribingAdjectives().toArray()));
		node.setAttributeValue(SYNONYMS, Arrays.toString(getSynonyms().toArray()));
		node.setAttributeValue(DIRECT_HYPERNYMS, Arrays.toString(getDirectHypernyms().toArray()));
		node.setAttributeValue(DIRECT_HYPONYMS, Arrays.toString(getDirectHyponyms().toArray()));
		node.setAttributeValue(MERONYMS, Arrays.toString(getMeronyms().toArray()));
		node.setAttributeValue(HOLONYMS, Arrays.toString(getHolonyms().toArray()));
		return node;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObjectEntity) {
			ObjectEntity other = (ObjectEntity) obj;
			boolean result = super.equals(obj) && Objects.equals(determiner, other.getDeterminer())
					&& Objects.equals(quantity, other.getQuantity()) && Objects.equals(possessivePronouns, other.getPossessivePronouns())
					&& Objects.equals(describingAdjectives, other.getDescribingAdjectives()) && Objects.equals(synonyms, other.synonyms)
					&& Objects.equals(directHypernyms, other.getDirectHypernyms())
					&& Objects.equals(directHyponyms, other.getDirectHyponyms()) && Objects.equals(meronyms, other.getMeronyms())
					&& Objects.equals(holonyms, other.getHolonyms());
			if (wnSense != null && other.getWNSense() != null) {
				result &= Objects.equals(wnSense.getLeft(), other.getWNSense().getLeft());
				if (wnSense.getRight() != null) {
					if (other.getWNSense() == null) {
						return false;
					} else {
						result &= Math.abs(wnSense.getRight() - other.getWNSense().getRight()) < 0.0000001d;
					}
				} else {
					result &= other.getWNSense().getRight() == null;
				}
				return result;
			} else if (wnSense == null && other.getWNSense() == null) {
				return result;
			}
			return false;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = this.determiner == null ? hash : 31 * hash + this.determiner.hashCode();
		hash = this.quantity == null ? hash : 31 * hash + this.quantity.hashCode();
		hash = this.possessivePronouns == null ? hash : 31 * hash + this.possessivePronouns.hashCode();
		hash = this.describingAdjectives == null ? hash : 31 * hash + this.describingAdjectives.hashCode();
		hash = this.synonyms == null ? hash : 31 * hash + this.synonyms.hashCode();
		hash = this.directHypernyms == null ? hash : 31 * hash + this.directHypernyms.hashCode();
		hash = this.directHyponyms == null ? hash : 31 * hash + this.directHyponyms.hashCode();
		hash = this.meronyms == null ? hash : 31 * hash + this.meronyms.hashCode();
		hash = this.holonyms == null ? hash : 31 * hash + this.holonyms.hashCode();
		hash = this.wnSense == null ? hash : 31 * hash + this.wnSense.hashCode();

		return hash;
	}

	public static Entity readFromNode(INode node, IGraph graph) {
		ObjectEntity entity = null;
		String name = (String) node.getAttributeValue(ENTITY_NAME);
		GrammaticalNumber gNumber = (GrammaticalNumber) node.getAttributeValue(GRAMMATICAL_NUMBER);
		CommandType cmdType = (CommandType) node.getAttributeValue(COMMAND_TYPE);
		int statement = (int) node.getAttributeValue(STATEMENT);
		DeterminerType det = (DeterminerType) node.getAttributeValue(DETERMINER);
		String quantity = (String) node.getAttributeValue(QUANTITY);
		List<String> possessivePronoun = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(POSSESSIVE_PRONOUN));
		List<String> describingAdjectives = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(DESCRIBING_ADJECTIVES));
		List<String> synonyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(SYNONYMS));
		List<String> directHypernyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(DIRECT_HYPERNYMS));
		List<String> directHyponyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(DIRECT_HYPONYMS));
		List<String> meronyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(MERONYMS));
		List<String> holonyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(HOLONYMS));

		List<? extends IArc> references = node.getOutgoingArcsOfType(graph.getArcType(REFERENCE));
		List<List<INode>> refs = new ArrayList<List<INode>>();
		for (IArc arc : references) {
			List<INode> reference = GraphUtils.getNodesOfArcChain(arc, graph);
			refs.add(reference);
		}
		entity = new ObjectEntity(name, gNumber, det, quantity, possessivePronoun, refs.get(0), describingAdjectives, synonyms,
				directHypernyms, directHyponyms, meronyms, holonyms);
		entity.setCommandType(cmdType);
		entity.setStatement(statement);
		if (node.getAttributeValue(WN_SENSE) != null) {
			Pair<String, Double> wnSense = (Pair<String, Double>) node.getAttributeValue(WN_SENSE);
			entity.setWNSense(wnSense);

		}
		for (List<INode> list : refs.subList(1, refs.size())) {
			entity.setReference(list);
		}
		entity.changed = false;
		return entity;

	}

	@Override
	public boolean integrateEntityInformation(Entity entity) {
		if (entity instanceof ObjectEntity) {
			ObjectEntity other = (ObjectEntity) entity;

			boolean changed = super.integrateEntityInformation(entity);
			if (!Objects.equals(determiner, other.getDeterminer())) {
				setDeterminer(other.getDeterminer());
				changed = true;
			}
			if (!Objects.equals(quantity, other.getQuantity())) {
				setQuantity(other.getQuantity());
				changed = true;
			}
			if (!Objects.equals(wnSense, other.getWNSense())) {
				setWNSense(other.getWNSense());
				changed = true;
			}
			for (String pronoun : other.getPossessivePronouns()) {
				if (!this.possessivePronouns.contains(pronoun)) {
					this.possessivePronouns.add(pronoun);
					changed = true;
				}
			}
			for (String adjective : other.getDescribingAdjectives()) {
				if (!this.describingAdjectives.contains(adjective)) {
					this.describingAdjectives.add(adjective);
					changed = true;
				}
			}
			for (String synonym : other.getSynonyms()) {
				if (!this.getSynonyms().contains(synonym)) {
					this.getSynonyms().add(synonym);
					changed = true;
				}
			}
			for (String directHypernyms : other.getDirectHypernyms()) {
				if (!this.getDirectHypernyms().contains(directHypernyms)) {
					this.getDirectHypernyms().add(directHypernyms);
					changed = true;
				}
			}
			for (String directHyponyms : other.getDirectHyponyms()) {
				if (!this.getDirectHyponyms().contains(directHyponyms)) {
					this.getDirectHyponyms().add(directHyponyms);
					changed = true;
				}
			}
			for (String meronyms : other.getMeronyms()) {
				if (!this.getMeronyms().contains(meronyms)) {
					this.getMeronyms().add(meronyms);
					changed = true;
				}
			}
			for (String holonyms : other.getHolonyms()) {
				if (!this.getHolonyms().contains(holonyms)) {
					this.getHolonyms().add(holonyms);
					changed = true;
				}
			}
			this.changed = changed;
			return changed;
		} else {
			throw new IllegalArgumentException("Entity to integrate has not a matching Type");
		}

	}

	/**
	 * @return the possessivePronoun
	 */
	public List<String> getPossessivePronouns() {
		return possessivePronouns;
	}

	/**
	 * @param possessivePronoun
	 *            the possessivePronoun to set
	 */
	public void setPossessivePronouns(List<String> possessivePronouns) {
		if (!Objects.equals(this.possessivePronouns, possessivePronouns)) {
			this.changed = true;
			this.possessivePronouns = possessivePronouns;
		}
	}

	public boolean hasState() {
		return hasRelationsOfType(EntityStateRelation.class);
	}

	public Set<State> getStates() {
		Set<State> states = new HashSet<>();
		for (Relation rel : getRelationsOfType(EntityStateRelation.class)) {
			EntityStateRelation esRel = (EntityStateRelation) rel;
			states.add((State) esRel.getEnd());
		}
		return states;
	}

	/**
	 * @return the wnSense
	 */
	public Pair<String, Double> getWNSense() {
		return wnSense;
	}

	/**
	 * @param wnSense
	 *            the wnSense to set
	 */
	public void setWNSense(Pair<String, Double> wnSense) {
		if (!Objects.equals(this.wnSense, wnSense)) {
			this.changed = true;
			this.wnSense = wnSense;
		}
	}

}
