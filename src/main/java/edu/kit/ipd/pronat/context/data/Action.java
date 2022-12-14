package edu.kit.ipd.pronat.context.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import edu.kit.ipd.pronat.context.data.relations.ActionConceptRelation;
import edu.kit.ipd.pronat.context.data.relations.Relation;
import edu.kit.ipd.pronat.context.data.relations.SRLArgumentRelation;
import edu.kit.ipd.pronat.context.util.GraphUtils;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.graph.INodeType;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class Action extends ContextIndividual {

	public static final String ACTION_NODE_TYPE = "contextAction";
	public static final String ACTION_NAME = "name";
	private static final String SRL_CONFIDENCE = "srlConfidence";
	private static final String COMMAND_TYPE = "cmdtype";
	private static final String PB_ROLESET_ID = "pbRolesetID";
	private static final String PB_ROLESET_DESCR = "pbRolesetDescr";
	private static final String VB_FRAMES = "vbFrames";
	private static final String FN_FRAMES = "fnFrames";
	private static final String EVENT_TYPES = "eventTypes";
	private static final String ANTONYMS = "antonyms";
	private static final String SYNONYMS = "synonyms";
	private static final String DIRECT_HYPERNYMS = "directHypernyms";
	private static final String DIRECT_HYPONYMS = "directHyponyms";
	private static final String REFERENCE = "reference";

	private String name;

	private CommandType commandType = CommandType.INDEPENDENT_STATEMENT;

	private double srlConfidence = -1.0;
	private String propBankRolesetID = "";
	private String propBankRoleSetDescription = "";

	private List<String> verbNetFrames;
	private List<String> frameNetFrames;
	private List<String> eventTypes;

	private Set<String> antonyms;

	private Set<String> synonyms;

	private Set<String> directHyponyms;

	private Set<String> directHypernyms;

	private List<INode> reference;

	/**
	 * @param name
	 * @param srlConfidence
	 * @param propBankRolesetID
	 * @param propBankRoleSetDescription
	 * @param verbNetFrames
	 * @param frameNetFrames
	 * @param eventTypes
	 * @param antonyms
	 * @param synonyms
	 * @param directHyponyms
	 * @param directHypernyms
	 * @param reference
	 */
	public Action(String name, double srlConfidence, String propBankRolesetID, String propBankRoleSetDescription,
			List<String> verbNetFrames, List<String> frameNetFrames, List<String> eventTypes, List<String> antonyms, List<String> synonyms,
			List<String> directHyponyms, List<String> directHypernyms, List<INode> reference) {
		super();
		this.name = name;
		this.srlConfidence = srlConfidence;
		this.propBankRolesetID = propBankRolesetID;
		this.propBankRoleSetDescription = propBankRoleSetDescription;
		this.verbNetFrames = verbNetFrames;
		this.frameNetFrames = frameNetFrames;
		this.eventTypes = eventTypes;
		this.antonyms = new TreeSet<>();
		this.antonyms.addAll(antonyms);
		this.synonyms = new TreeSet<>();
		this.synonyms.addAll(synonyms);
		this.directHyponyms = new TreeSet<>();
		this.directHyponyms.addAll(directHyponyms);
		this.directHypernyms = new TreeSet<>();
		this.directHypernyms.addAll(directHypernyms);
		this.reference = reference;
		commandType = CommandType.INDEPENDENT_STATEMENT;
		changed = false;
	}

	public Action(String name, List<String> antonyms, List<String> synonyms, List<String> directHyponyms, List<String> directHypernyms,
			List<INode> reference) {
		super();
		this.name = name;
		this.antonyms = new TreeSet<>();
		this.antonyms.addAll(antonyms);
		this.synonyms = new TreeSet<>();
		this.synonyms.addAll(synonyms);
		this.directHyponyms = new TreeSet<>();
		this.directHyponyms.addAll(directHyponyms);
		this.directHypernyms = new TreeSet<>();
		this.directHypernyms.addAll(directHypernyms);
		verbNetFrames = new ArrayList<>();
		frameNetFrames = new ArrayList<>();
		eventTypes = new ArrayList<>();
		this.reference = reference;
		commandType = CommandType.INDEPENDENT_STATEMENT;
		srlConfidence = -1.0;
		changed = false;
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
			changed = true;
			this.name = name;
		}
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
			changed = true;
			this.commandType = commandType;
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
			changed = true;
			this.reference = reference;
		}
	}

	/**
	 * @return the srlConfidence
	 */
	public double getSrlConfidence() {
		return srlConfidence;
	}

	/**
	 * @param confidence
	 *            the srlConfidence to set
	 */
	public void setSrlConfidence(double confidence) {
		if (srlConfidence != confidence) {
			changed = true;
			srlConfidence = confidence;
		}
	}

	/**
	 * @return the propBankRolesetID
	 */
	public String getPropBankRolesetID() {
		return propBankRolesetID;
	}

	/**
	 * @param propBankRolesetID
	 *            the propBankRolesetID to set
	 */
	public void setPropBankRolesetID(String propBankRolesetID) {
		if (!Objects.equals(this.propBankRolesetID, propBankRolesetID)) {
			changed = true;
			this.propBankRolesetID = propBankRolesetID;
		}
	}

	/**
	 * @return the propBankRoleSetDescription
	 */
	public String getPropBankRoleSetDescription() {
		return propBankRoleSetDescription;
	}

	/**
	 * @param propBankRoleSetDescription
	 *            the propBankRoleSetDescription to set
	 */
	public void setPropBankRoleSetDescription(String propBankRoleSetDescription) {
		if (!Objects.equals(this.propBankRoleSetDescription, propBankRoleSetDescription)) {
			changed = true;
			this.propBankRoleSetDescription = propBankRoleSetDescription;
		}
	}

	/**
	 * @return the verbNetFrames
	 */
	public List<String> getVerbNetFrames() {
		return verbNetFrames;
	}

	/**
	 * @param verbNetFrames
	 *            the verbNetFrames to set
	 */
	public void setVerbNetFrames(List<String> verbNetFrames) {
		if (!Objects.equals(this.verbNetFrames, verbNetFrames)) {
			changed = true;
			this.verbNetFrames = verbNetFrames;
		}
	}

	/**
	 * @return the frameNetFrames
	 */
	public List<String> getFrameNetFrames() {
		return frameNetFrames;
	}

	/**
	 * @param frameNetFrames
	 *            the frameNetFrames to set
	 */
	public void setFrameNetFrames(List<String> frameNetFrames) {
		if (!Objects.equals(this.frameNetFrames, frameNetFrames)) {
			changed = true;
			this.frameNetFrames = frameNetFrames;
		}
	}

	/**
	 * @return the eventTypes
	 */
	public List<String> getEventTypes() {
		return eventTypes;
	}

	/**
	 * @param eventTypes
	 *            the eventTypes to set
	 */
	public void setEventTypes(List<String> eventTypes) {
		if (!Objects.equals(this.eventTypes, eventTypes)) {
			changed = true;
			this.eventTypes = eventTypes;
		}
	}

	/**
	 * @return the antonyms
	 */
	public Set<String> getAntonyms() {
		return antonyms;
	}

	/**
	 * @param antonyms
	 *            the antonyms to set
	 */
	public void setAntonyms(Set<String> antonyms) {
		if (!Objects.equals(this.antonyms, antonyms)) {
			changed = true;
			this.antonyms = antonyms;
		}
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
			changed = true;
			this.synonyms = synonyms;
		}
	}

	/**
	 * @return the Direct Hyponyms
	 */
	public Set<String> getDirectHyponyms() {
		return directHyponyms;
	}

	/**
	 * @param directHyponyms
	 *            the direct Hyponyms to set
	 */
	public void setDirectHyponyms(Set<String> directHyponyms) {
		if (!Objects.equals(this.directHyponyms, directHyponyms)) {
			changed = true;
			this.directHyponyms = directHyponyms;
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
			changed = true;
			this.directHypernyms = directHypernyms;
		}
	}

	@Override
	public String toString() {
		String output = "[" + getName() + ": " + getPropBankRolesetID() + ", " + getVerbNetFrames() + getFrameNetFrames() + getEventTypes()
				+ "| ";
		for (Relation rel : getRelations()) {
			if (rel instanceof SRLArgumentRelation) {
				SRLArgumentRelation srl = (SRLArgumentRelation) rel;
				output += "{ " + srl.getName() + ", " + srl.getVerbNetRoles() + ", " + srl.getFrameNetRoles() + ", "
						+ srl.getPropBankRoleDescr() + " -> " + srl.getEntity().getName() + " } ";
			}
		}
		output += "]";
		return output;
	}

	@Override
	public Set<Relation> updateNode(INode node, IGraph graph, HashMap<Long, INode> graphNodes) {
		Set<Relation> alreadyUpdated = super.updateNode(node, graph, graphNodes);
		fillWithInformation(node);
		return alreadyUpdated;
	}

	@Override
	public INode printToGraph(IGraph graph) {
		INodeType nodeType;
		if (graph.hasNodeType(ACTION_NODE_TYPE)) {
			nodeType = graph.getNodeType(ACTION_NODE_TYPE);
		} else {
			nodeType = createActionNodeType(graph);
		}
		INode node = graph.createNode(nodeType);
		fillWithInformation(node);
		setReferenceArcs(graph, node);
		return node;

	}

	public static Action readFromNode(INode node, IGraph graph) {
		Action action;
		String name = (String) node.getAttributeValue(ACTION_NAME);
		CommandType cmdType = (CommandType) node.getAttributeValue(COMMAND_TYPE);
		double confidence = (Double) node.getAttributeValue(SRL_CONFIDENCE);
		String propBankRolesetID = (String) node.getAttributeValue(PB_ROLESET_ID);
		String propBankRolesetDescr = (String) node.getAttributeValue(PB_ROLESET_DESCR);
		List<String> verbNetFrames = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(VB_FRAMES));
		List<String> frameNetFrames = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(FN_FRAMES));
		List<String> eventTypes = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(EVENT_TYPES));
		List<String> antonyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(ANTONYMS));
		List<String> synonyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(SYNONYMS));
		List<String> directHypernyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(DIRECT_HYPERNYMS));
		List<String> directHyponyms = GraphUtils.getListFromArrayToString((String) node.getAttributeValue(DIRECT_HYPONYMS));

		List<? extends IArc> references = node.getOutgoingArcsOfType(graph.getArcType(REFERENCE));
		List<List<INode>> refs = new ArrayList<List<INode>>();
		for (IArc arc : references) {
			List<INode> reference = GraphUtils.getNodesOfArcChain(arc, graph);
			refs.add(reference);
		}
		action = new Action(name, confidence, propBankRolesetID, propBankRolesetDescr, verbNetFrames, frameNetFrames, eventTypes, antonyms,
				synonyms, directHyponyms, directHypernyms, refs.get(0));
		action.setCommandType(cmdType);
		for (List<INode> list : refs.subList(1, refs.size())) {
			action.setReference(list);
		}
		action.changed = false;
		return action;
	}

	private void fillWithInformation(INode node) {
		node.setAttributeValue(ACTION_NAME, getName());
		node.setAttributeValue(SRL_CONFIDENCE, getSrlConfidence());
		node.setAttributeValue(COMMAND_TYPE, getCommandType());
		node.setAttributeValue(PB_ROLESET_ID, getPropBankRolesetID());
		node.setAttributeValue(PB_ROLESET_DESCR, getPropBankRoleSetDescription());
		node.setAttributeValue(VB_FRAMES, Arrays.toString(getVerbNetFrames().toArray()));
		node.setAttributeValue(FN_FRAMES, Arrays.toString(getFrameNetFrames().toArray()));
		node.setAttributeValue(EVENT_TYPES, Arrays.toString(getEventTypes().toArray()));
		node.setAttributeValue(ANTONYMS, Arrays.toString(getAntonyms().toArray()));
		node.setAttributeValue(SYNONYMS, Arrays.toString(getSynonyms().toArray()));
		node.setAttributeValue(DIRECT_HYPERNYMS, Arrays.toString(getDirectHypernyms().toArray()));
		node.setAttributeValue(DIRECT_HYPONYMS, Arrays.toString(getDirectHyponyms().toArray()));

	}

	private INodeType createActionNodeType(IGraph graph) {
		INodeType nodeType = graph.createNodeType(ACTION_NODE_TYPE);
		nodeType.addAttributeToType("String", ACTION_NAME);
		nodeType.addAttributeToType("Double", SRL_CONFIDENCE);
		nodeType.addAttributeToType(CommandType.class.getName(), COMMAND_TYPE);
		nodeType.addAttributeToType("String", PB_ROLESET_ID);
		nodeType.addAttributeToType("String", PB_ROLESET_DESCR);
		nodeType.addAttributeToType("String", VB_FRAMES);
		nodeType.addAttributeToType("String", FN_FRAMES);
		nodeType.addAttributeToType("String", EVENT_TYPES);
		nodeType.addAttributeToType("String", ANTONYMS);
		nodeType.addAttributeToType("String", SYNONYMS);
		nodeType.addAttributeToType("String", DIRECT_HYPERNYMS);
		nodeType.addAttributeToType("String", DIRECT_HYPONYMS);
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
		if (obj instanceof Action) {
			Action other = (Action) obj;
			if (Objects.equals(name, other.name) && Objects.equals(commandType, other.commandType) && srlConfidence == other.srlConfidence
					&& Objects.equals(propBankRolesetID, other.propBankRolesetID)
					&& Objects.equals(propBankRoleSetDescription, other.propBankRoleSetDescription)
					&& Objects.equals(verbNetFrames, other.verbNetFrames) && Objects.equals(frameNetFrames, other.frameNetFrames)
					&& Objects.equals(eventTypes, other.eventTypes) && Objects.equals(antonyms, other.antonyms)
					&& Objects.equals(synonyms, other.synonyms) && Objects.equals(directHypernyms, other.directHypernyms)
					&& Objects.equals(directHyponyms, other.directHyponyms)) {
				boolean result = reference.equals(other.reference);
				if (other.getRelations().size() != getRelations().size()) {
					return false;
				}
				for (Relation rel : getRelations()) {
					if (!other.getRelations().contains(rel)) {
						return false;
					}
				}
				for (Relation rel : other.getRelations()) {
					if (!getRelations().contains(rel)) {
						return false;
					}
				}
				return result;
			}
		}
		return false;
	}

	public boolean equalsWithoutRelation(Object obj) {
		if (obj instanceof Action) {
			Action other = (Action) obj;
			if (Objects.equals(name, other.name) && commandType.equals(other.commandType) && srlConfidence == other.srlConfidence
					&& Objects.equals(propBankRolesetID, other.propBankRolesetID)
					&& Objects.equals(propBankRoleSetDescription, other.propBankRoleSetDescription)
					&& Objects.equals(verbNetFrames, other.verbNetFrames) && Objects.equals(frameNetFrames, other.frameNetFrames)
					&& Objects.equals(eventTypes, other.eventTypes) && Objects.equals(antonyms, other.antonyms)
					&& Objects.equals(synonyms, other.synonyms) && Objects.equals(directHypernyms, other.directHypernyms)
					&& Objects.equals(directHyponyms, other.directHyponyms)) {
				boolean result = reference.equals(other.reference);
				return result;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = name.hashCode();
		hash = commandType == null ? hash : 31 * hash + commandType.hashCode();
		hash = Double.hashCode(srlConfidence) + 31 * hash;
		hash = propBankRolesetID == null ? hash : 31 * hash + propBankRolesetID.hashCode();
		hash = propBankRoleSetDescription == null ? hash : 31 * hash + propBankRoleSetDescription.hashCode();
		hash = verbNetFrames == null ? hash : 31 * hash + verbNetFrames.hashCode();
		hash = frameNetFrames == null ? hash : 31 * hash + frameNetFrames.hashCode();
		hash = eventTypes == null ? hash : 31 * hash + eventTypes.hashCode();
		hash = antonyms == null ? hash : 31 * hash + antonyms.hashCode();
		hash = synonyms == null ? hash : 31 * hash + synonyms.hashCode();
		hash = directHypernyms == null ? hash : 31 * hash + directHypernyms.hashCode();
		hash = directHyponyms == null ? hash : 31 * hash + directHyponyms.hashCode();
		hash = reference == null ? hash : 31 * hash + reference.hashCode();

		return hash;
	}

	public boolean integrateActionInformation(Action other) {
		boolean changed = false;
		if (!name.equals(other.getName())) {
			setName(other.getName());
			changed = true;
		}
		if (!commandType.equals(other.getCommandType())) {
			setCommandType(other.getCommandType());
			changed = true;
		}
		if (!(srlConfidence == other.srlConfidence)) {
			setSrlConfidence(other.getSrlConfidence());
			changed = true;
		}
		if (!propBankRoleSetDescription.equals(other.getPropBankRoleSetDescription())) {
			setPropBankRoleSetDescription(other.getPropBankRoleSetDescription());
			changed = true;
		}
		if (!propBankRolesetID.equals(other.getPropBankRolesetID())) {
			setPropBankRolesetID(other.getPropBankRolesetID());
			changed = true;
		}
		for (String vnFrame : other.getVerbNetFrames()) {
			if (!getVerbNetFrames().contains(vnFrame)) {
				getVerbNetFrames().add(vnFrame);
				changed = true;
			}
		}
		for (String fnFrame : other.getFrameNetFrames()) {
			if (!getFrameNetFrames().contains(fnFrame)) {
				getFrameNetFrames().add(fnFrame);
				changed = true;
			}
		}
		for (String eventType : other.getEventTypes()) {
			if (!getEventTypes().contains(eventType)) {
				getEventTypes().add(eventType);
				changed = true;
			}
		}

		for (String synonym : other.getSynonyms()) {
			if (!getSynonyms().contains(synonym)) {
				getSynonyms().add(synonym);
				changed = true;
			}
		}
		for (String directHypernyms : other.getDirectHypernyms()) {
			if (!getDirectHypernyms().contains(directHypernyms)) {
				getDirectHypernyms().add(directHypernyms);
				changed = true;
			}
		}
		for (String directHyponyms : other.getDirectHyponyms()) {
			if (!getDirectHyponyms().contains(directHyponyms)) {
				getDirectHyponyms().add(directHyponyms);
				changed = true;
			}
		}
		for (String antonyms : other.getAntonyms()) {
			if (!getAntonyms().contains(antonyms)) {
				getAntonyms().add(antonyms);
				changed = true;
			}
		}
		for (INode iNode : other.getReference()) {
			if (!reference.contains(iNode)) {
				reference.add(iNode);
				changed = true;
			}
		}
		changed = changed || getRelations().addAll(other.getRelations());
		this.changed = changed;
		return changed;

	}

	public boolean hasRelatedConcept() {
		return hasRelationsOfType(ActionConceptRelation.class);
	}

	public ActionConcept getRelatedConcept() {
		ActionConcept result = null;
		double confidenceMax = 0.0;
		for (Relation relation : getRelationsOfType(ActionConceptRelation.class)) {
			ActionConceptRelation acRel = (ActionConceptRelation) relation;
			if ((acRel.getEnd() instanceof ActionConcept)) {
				if (acRel.getConfidence() > confidenceMax) {
					result = (ActionConcept) acRel.getEnd();
					confidenceMax = acRel.getConfidence();
				}
			}
		}
		return result;
	}
}
