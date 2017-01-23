/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.contextanalyzer.data.Action;
import edu.kit.ipd.parse.contextanalyzer.data.CommandType;
import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SpeakerEntity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.SRLArgumentRelation;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.contextanalyzer.util.GraphUtils;
import edu.kit.ipd.parse.contextanalyzer.util.WordNetUtils;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.ontology_connection.Domain;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;

/**
 * This {@link IContextAnalyzer} analyzes the current {@link IGraph} and
 * {@link Context} for occurring {@link Action}s
 * 
 * @author Tobias Hey
 *
 */
public class ActionRecognizer implements IContextAnalyzer {

	private static final String COMMAND_TYPE = "commandType";
	private static final String SRL_ARCTYPE_NAME = "srl";
	private static final String ROLE_VALUE_NAME = "role";
	private static final String CORRESPONDING_VERB = "correspondingVerb";
	private static final String VN_ROLE_NAME = "vnRole";
	private static final String ROLE_CONFIDENCE_NAME = "roleConfidence";
	private static final String IOBES = "IOBES";
	private static final String PROPBANK_ROLE_DESCRIPTION = "pbRole";
	private static final String EVENT_TYPES = "eventTypes";
	private static final String FRAME_NET_FRAMES = "frameNetFrames";
	private static final String VERB_NET_FRAMES = "verbNetFrames";
	private static final String PROP_BANK_ROLESET_DESCR = "propBankRolesetDescr";
	private static final String PROP_BANK_ROLESET_ID = "propBankRolesetID";
	private static final String FN_ROLE_NAME = "fnRole";
	private Dictionary dictionary;
	private IGraph graph;
	private Context currentContext;
	private Domain domain;

	private static final Logger logger = LoggerFactory.getLogger(ActionRecognizer.class);

	/**
	 * Constructs new {@link ActionRecognizer}
	 * 
	 * @param dictionary
	 *            The WordNet {@link Dictionary}
	 * @param domain
	 *            The domainknowledge {@link Domain}
	 */
	public ActionRecognizer(Dictionary dictionary, Domain domain) {
		this.dictionary = dictionary;
		this.domain = domain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.kit.ipd.parse.contextanalyzer.IContextAnalyzer#analyze(edu.kit.ipd.
	 * parse.luna.graph.IGraph, edu.kit.ipd.parse.contextanalyzer.data.Context)
	 */
	@Override
	public void analyze(IGraph graph, Context context) throws MissingDataException {
		logger.info("Starting Action detection...");
		this.graph = graph;
		this.currentContext = context;
		List<INode> utteranceNodes = GraphUtils.getNodesOfUtterance(graph);
		context.addActions(getActions(utteranceNodes, context));

	}

	/**
	 * Returns all {@link Action} ocurring in the specified utterance
	 * {@link INode}s and integrates new Information into possibly already
	 * detected {@link Action} in the specified {@link Context}
	 * 
	 * @param utteranceNodes
	 * @param context
	 * @return
	 */
	private List<Action> getActions(List<INode> utteranceNodes, Context context) {
		ArrayList<Action> actions = new ArrayList<Action>();
		List<List<INode>> actionSequences;
		if (actionSequencesExist()) {
			actionSequences = getActionSequences();
			//TODO getActions from action sequence Agent
		} else {
			actionSequences = getVerbPhrases(utteranceNodes);
			for (List<INode> actionSequence : actionSequences) {
				INode verb = searchSRLVerb(actionSequence);
				Set<Relation> srlRelations = new HashSet<>();
				Action action = null;
				String actionName = "";
				CommandType cmdType = CommandType.INDEPENDENT_STATEMENT;
				if (verb == null) {
					for (INode iNode : actionSequence) {
						if (((String) iNode.getAttributeValue("pos")).startsWith("VB")) {
							actionName = actionName + " " + iNode.getAttributeValue("value");
							actionName = actionName.trim();
						}
					}
					if (!actionName.equals("")) {
						action = new Action(actionName, WordNetUtils.getAntonyms(actionName, POS.VERB, dictionary),
								WordNetUtils.getSynonyms(actionName, POS.VERB, dictionary),
								WordNetUtils.getDirectHyponyms(actionName, POS.VERB, dictionary),
								WordNetUtils.getDirectHypernyms(actionName, POS.VERB, dictionary), actionSequence);
						action.setCommandType(ContextUtils.getMostLikelyCmdType(actionSequence));
					}
				} else {

					if (verb.getAttributeNames().contains(COMMAND_TYPE)) {
						if (verb.getAttributeValue(COMMAND_TYPE) != null) {
							cmdType = (CommandType) verb.getAttributeValue(COMMAND_TYPE);

						}
					}
					IArc srlVerbArc = getSRLVerbArc(verb);
					actionName = (String) srlVerbArc.getAttributeValue(CORRESPONDING_VERB);

					if ((String) srlVerbArc.getAttributeValue(PROP_BANK_ROLESET_ID) != null) {

						action = getSRLInformation(srlVerbArc, actionSequence);
						action.setCommandType(cmdType);

					} else {
						//TODO match correct synset
						action = new Action(actionName, WordNetUtils.getAntonyms(actionName, POS.VERB, dictionary),
								WordNetUtils.getSynonyms(actionName, POS.VERB, dictionary),
								WordNetUtils.getDirectHyponyms(actionName, POS.VERB, dictionary),
								WordNetUtils.getDirectHypernyms(actionName, POS.VERB, dictionary), actionSequence);
						action.setCommandType(cmdType);
					}

					Set<? extends IArc> srlArcs = verb.getOutgoingArcsOfType(graph.getArcType(SRL_ARCTYPE_NAME));

					for (IArc arc : srlArcs) {
						if (arc.getTargetNode() != verb) {
							List<INode> roleNodes = getRoleNodes(arc);
							Entity correspondingEntity = getIntersectingEntity(context.getEntities(), roleNodes);
							if (correspondingEntity != null && action != null) {
								String verbNetRoleString = (String) arc.getAttributeValue(VN_ROLE_NAME);
								List<String> verbNetRoles = GraphUtils.getListFromArrayToString(verbNetRoleString);
								String frameNetRoleString = (String) arc.getAttributeValue(FN_ROLE_NAME);
								List<String> frameNetRoles = GraphUtils.getListFromArrayToString(frameNetRoleString);
								Relation relation = new SRLArgumentRelation((String) arc.getAttributeValue(ROLE_VALUE_NAME),
										(String) arc.getAttributeValue(PROPBANK_ROLE_DESCRIPTION), verbNetRoles, frameNetRoles, action,
										correspondingEntity);
								srlRelations.add(relation);
								//action.addRelation(relation);
								//correspondingEntity.addRelation(relation);
							}
						}

					}
				}
				if (action != null) {
					if (!currentContext.hasAction(actionName, actionSequence)) {
						actions.add(action);
						for (Relation relation : srlRelations) {
							SRLArgumentRelation rel = (SRLArgumentRelation) relation;
							action.addRelation(rel);
							rel.getEntity().addRelation(rel);
						}
					} else {
						Action old = currentContext.getAction(actionName, actionSequence);
						if (old.integrateActionInformation(action)) {
							logger.debug("Already existing Action found: Updating old Action!");
						}
						List<Relation> oldSrlRels = old.getRelationsOfType(SRLArgumentRelation.class);
						for (Relation relation : srlRelations) {
							SRLArgumentRelation rel = (SRLArgumentRelation) relation;

							rel.setAction(old);
							for (Relation oldSRLRel : oldSrlRels) {
								if (((SRLArgumentRelation) oldSRLRel).getEntity().equals(rel.getEntity())) {
									action.getRelations().remove(oldSRLRel);
									((SRLArgumentRelation) oldSRLRel).getEntity().getRelations().remove(oldSRLRel);
								}
							}
							old.addRelation(rel);
							rel.getEntity().addRelation(rel);
						}
					}
				}
			}

		}

		logger.info(actions.size() + " new Actions detected.");
		return actions;

	}

	/**
	 * REturns an {@link Action} build from the semantic role label Information
	 * included in the specified {@link IArc}
	 * 
	 * @param srlVerbArc
	 * @param actionSequence
	 * @return
	 */
	private Action getSRLInformation(IArc srlVerbArc, List<INode> actionSequence) {
		Action action;
		String actionName = (String) srlVerbArc.getAttributeValue(CORRESPONDING_VERB);
		double confidence = (Double) srlVerbArc.getAttributeValue(ROLE_CONFIDENCE_NAME);
		String propBankRolesetID = (String) srlVerbArc.getAttributeValue(PROP_BANK_ROLESET_ID);
		String propBankRolesetDescription = (String) srlVerbArc.getAttributeValue(PROP_BANK_ROLESET_DESCR);
		String verbNetFramesString = (String) srlVerbArc.getAttributeValue(VERB_NET_FRAMES);
		List<String> verbNetFrames = GraphUtils.getListFromArrayToString(verbNetFramesString);
		String frameNetFramesString = (String) srlVerbArc.getAttributeValue(FRAME_NET_FRAMES);
		List<String> frameNetFrames = GraphUtils.getListFromArrayToString(frameNetFramesString);
		String eventTypesString = (String) srlVerbArc.getAttributeValue(EVENT_TYPES);
		List<String> eventTypes = GraphUtils.getListFromArrayToString(eventTypesString);

		action = new Action(actionName, confidence, propBankRolesetID, propBankRolesetDescription, verbNetFrames, frameNetFrames,
				eventTypes, WordNetUtils.getAntonyms(actionName, POS.VERB, dictionary),
				WordNetUtils.getSynonyms(actionName, POS.VERB, dictionary),
				WordNetUtils.getDirectHyponyms(actionName, POS.VERB, dictionary),
				WordNetUtils.getDirectHypernyms(actionName, POS.VERB, dictionary), actionSequence);
		return action;
	}

	private Entity getIntersectingEntity(Set<Entity> entities, List<INode> nodes) {
		Collections.reverse(nodes);
		for (INode node : nodes) {
			for (Entity entity : entities) {

				if (entity.getReference().contains(node) && !(entity instanceof SpeakerEntity)) {
					return entity;
				}

			}

		}
		return null;
	}

	/**
	 * REtruns the {@link INode}s belonging to the specified role {@link IArc}
	 * 
	 * @param arc
	 * @return
	 */
	private List<INode> getRoleNodes(IArc arc) {
		List<INode> roleNodes = new ArrayList<>();
		INode current = arc.getTargetNode();
		String role = (String) arc.getAttributeValue(ROLE_VALUE_NAME);
		if (role != "V") {
			String correspondingVerb = (String) arc.getAttributeValue(CORRESPONDING_VERB);
			roleNodes.add(current);

			while (GraphUtils.hasOutgoingArcOfType(current, SRL_ARCTYPE_NAME, graph)) {
				Set<? extends IArc> arcs = current.getOutgoingArcsOfType(graph.getArcType(SRL_ARCTYPE_NAME));
				INode last = current;
				for (IArc iArc : arcs) {
					if (iArc.getAttributeValue(CORRESPONDING_VERB).equals(correspondingVerb)
							&& iArc.getAttributeValue(ROLE_VALUE_NAME).equals(role)) {
						roleNodes.add(iArc.getTargetNode());
						current = iArc.getTargetNode();
					}
				}
				if (current.equals(last)) {
					break;
				}
			}
		}
		return roleNodes;
	}

	private IArc getSRLVerbArc(INode node) {
		for (IArc arc : node.getIncomingArcsOfType(graph.getArcType(SRL_ARCTYPE_NAME))) {
			if (arc.getAttributeValue(ROLE_VALUE_NAME).equals("V")) {
				return arc;
			}
		}
		return null;
	}

	private INode searchSRLVerb(List<INode> actionSequence) {
		for (INode node : actionSequence) {

			if (getSRLVerbArc(node) != null) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Returns all {@link INode}s belonging to all verb phrases of the specified
	 * utterance {@link INode}s
	 * 
	 * @param utteranceNodes
	 * @return
	 */
	private List<List<INode>> getVerbPhrases(List<INode> utteranceNodes) {
		ArrayList<List<INode>> result = new ArrayList<>();
		for (INode node : utteranceNodes) {
			String chunkIOB = (String) node.getAttributeValue("chunkIOB");
			if (chunkIOB.startsWith("B-VP")) {
				ArrayList<INode> phrase = new ArrayList<>();
				phrase.add(node);
				INode current = node;
				while ((current = GraphUtils.getNextNode(current, graph)) != null) {
					String chunkIOBNext = (String) current.getAttributeValue("chunkIOB");
					if (chunkIOBNext.startsWith("I-VP")) {
						phrase.add(current);
					} else {
						break;
					}
				}
				result.add(phrase);
			}
		}
		return result;
	}

	private List<List<INode>> getActionSequences() {
		// TODO get action sequences from Graph
		return null;
	}

	private boolean actionSequencesExist() {
		// TODO Search for action sequences in Graph
		return false;
	}

}
