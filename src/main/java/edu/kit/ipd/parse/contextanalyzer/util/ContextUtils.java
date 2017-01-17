/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.ipd.parse.conditionDetection.CommandType;
import edu.kit.ipd.parse.contextanalyzer.data.AbstractConcept;
import edu.kit.ipd.parse.contextanalyzer.data.Action;
import edu.kit.ipd.parse.contextanalyzer.data.ActionConcept;
import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.EntityConcept;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ActionConceptRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ActionEntityRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.EntityConceptRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ReferentRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.SRLArgumentRelation;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public final class ContextUtils {

	private static final String COMMAND_TYPE = "commandType";
	private static final String STATEMENT_NUMBER = "conditionNumber";

	public static final boolean belongToSameAction(Entity first, Entity second) {
		Set<Action> currentActions = new HashSet<>();
		if (first.hasRelationsOfType(ActionEntityRelation.class)) {
			List<Relation> relations = first.getRelationsOfType(ActionEntityRelation.class);
			for (Relation relation : relations) {
				ActionEntityRelation aeRel = (ActionEntityRelation) relation;
				currentActions.add(aeRel.getAction());
			}

		}
		if (second.hasRelationsOfType(ActionEntityRelation.class)) {
			List<Relation> relations = second.getRelationsOfType(ActionEntityRelation.class);
			for (Relation relation : relations) {
				ActionEntityRelation aeRel = (ActionEntityRelation) relation;
				if (currentActions.contains(aeRel.getAction())) {
					return true;
				}
			}

		}
		return false;

	}

	public static final boolean hasLocativeRole(Entity first) {

		if (first.hasRelationsOfType(ActionEntityRelation.class)) {
			List<Relation> relations = first.getRelationsOfType(ActionEntityRelation.class);
			for (Relation relation : relations) {
				ActionEntityRelation aeRel = (ActionEntityRelation) relation;
				if (aeRel instanceof SRLArgumentRelation) {
					SRLArgumentRelation srl = (SRLArgumentRelation) aeRel;
					if (srl.getName().equals("AM-LOC")) {
						return true;
					}
				}

			}

		}

		return false;

	}

	/**
	 * Checks if the {@link Entity} takes a proto patient
	 * 
	 * @param sentity
	 * @return
	 */
	public static final boolean isProtoPatient(Entity entity) {
		if (entity.hasRelationsOfType(ActionEntityRelation.class)) {
			List<Relation> relations = entity.getRelationsOfType(ActionEntityRelation.class);
			for (Relation relation : relations) {
				ActionEntityRelation aeRel = (ActionEntityRelation) relation;
				if (aeRel instanceof SRLArgumentRelation) {
					SRLArgumentRelation srl = (SRLArgumentRelation) aeRel;
					if (isProtoPatientRelation(srl)) {
						return true;
					}
				}

			}

		}

		return false;
	}

	private static boolean isProtoPatientRelation(SRLArgumentRelation srl) {
		if (srl.getVerbNetRoles().contains("Theme") || srl.getVerbNetRoles().contains("Patient")) {
			return true;
		}
		if (srl.getFrameNetRoles().contains("Theme")) {
			return true;
		}
		return false;
	}

	public static final Set<String> getVerbNetRoles(Entity entity) {
		Set<String> result = new HashSet<>();
		if (entity.hasRelationsOfType(SRLArgumentRelation.class)) {
			List<Relation> relations = entity.getRelationsOfType(SRLArgumentRelation.class);
			for (Relation relation : relations) {
				SRLArgumentRelation srlRel = (SRLArgumentRelation) relation;
				result.addAll(srlRel.getVerbNetRoles());
			}
		}
		return result;
	}

	public static final EntityConcept getMostLikelyEntityConcept(Collection<? extends Relation> relations) {
		EntityConcept result = null;
		double confidence = -1.0;
		for (Relation relation : relations) {
			if (relation instanceof EntityConceptRelation) {
				EntityConceptRelation ecRel = (EntityConceptRelation) relation;

				if (ecRel.getConfidence() > confidence) {
					if (ecRel.getEnd() instanceof EntityConcept) {
						result = (EntityConcept) ecRel.getEnd();
						confidence = ecRel.getConfidence();
					}
				}
			}
		}
		return result;
	}

	public static final EntityConceptRelation getMostLikelyEntityConceptRelation(Collection<? extends Relation> relations) {
		EntityConceptRelation result = null;
		double confidence = -1.0;
		for (Relation relation : relations) {
			if (relation instanceof EntityConceptRelation) {
				EntityConceptRelation ecRel = (EntityConceptRelation) relation;
				if (ecRel.getConfidence() > confidence) {
					if (ecRel.getEnd() instanceof EntityConcept) {
						result = ecRel;
						confidence = ecRel.getConfidence();
					}
				}
			}
		}
		return result;
	}

	public static final ReferentRelation getMostLikelyReferentRelation(Collection<? extends Relation> list, Entity current) {
		ReferentRelation result = null;
		double confidence = -1.0;
		for (Relation relation : list) {
			if (relation instanceof ReferentRelation) {
				ReferentRelation refRel = (ReferentRelation) relation;
				if (refRel.getConfidence() > confidence) {
					if (refRel.getEnd() instanceof Entity && refRel.getStart().equals(current)) {
						result = (ReferentRelation) relation;
						confidence = refRel.getConfidence();
					}
				}
			}

		}
		return result;
	}

	public static final ActionConcept getMostLikelyActionConcept(Collection<? extends Relation> relations) {
		ActionConcept result = null;
		double confidence = -1.0;
		for (Relation relation : relations) {
			if (relation instanceof ActionConceptRelation) {
				ActionConceptRelation acRel = (ActionConceptRelation) relation;
				if (acRel.getConfidence() > confidence) {
					if (acRel.getEnd() instanceof ActionConcept) {
						result = (ActionConcept) acRel.getEnd();
						confidence = acRel.getConfidence();
					}
				}
			}
		}
		return result;
	}

	public static final ActionConceptRelation getMostLikelyActionConceptRelation(Collection<? extends Relation> relations) {
		ActionConceptRelation result = null;
		double confidenceMax = -1.0;
		for (Relation relation : relations) {
			if (relation instanceof ActionConceptRelation) {
				ActionConceptRelation acRel = (ActionConceptRelation) relation;
				if ((acRel.getEnd() instanceof ActionConcept)) {
					if (acRel.getConfidence() > confidenceMax) {
						result = acRel;
						confidenceMax = acRel.getConfidence();
					}
				}
			}
		}
		return result;
	}

	public static final boolean isSubsumed(AbstractConcept concept, AbstractConcept possibleSubsumer) {
		if (concept.equals(possibleSubsumer)) {
			return true;
		}
		if (concept.getSuperConcepts().contains(possibleSubsumer)) {
			return true;
		} else if (!concept.getSuperConcepts().isEmpty()) {
			boolean isSubsumed = false;
			for (AbstractConcept superConcept : concept.getSuperConcepts()) {
				isSubsumed = isSubsumed || isSubsumed(superConcept, possibleSubsumer);
			}
			return isSubsumed;
		}

		return false;
	}

	public static final int getPositionInUtterance(Entity entity) {
		return (Integer) entity.getReference().get(0).getAttributeValue("position");
	}

	public static final int getInstructionNumber(Entity entity) {
		return (Integer) entity.getReference().get(0).getAttributeValue("instructionNumber");
	}

	public static final Entity getLastPrecedingReferent(Entity entity) {
		if (entity.hasRelationsOfType(ReferentRelation.class)) {
			List<Relation> referentRels = entity.getRelationsOfType(ReferentRelation.class);
			ReferentRelation mostLikely = getMostLikelyReferent(referentRels, entity);
			if (mostLikely != null) {
				if (mostLikely.getEnd() instanceof PronounEntity) {
					return mostLikely.getEnd();
				} else {
					Entity lastBefore = getLastRefBefore(mostLikely.getEnd(), ContextUtils.getPositionInUtterance(mostLikely.getEnd()),
							ContextUtils.getPositionInUtterance(entity));
					return lastBefore;
				}
			}
		}
		return null;

	}

	private static final Entity getLastRefBefore(Entity start, int current, int max) {
		Entity result = start;
		if (start.hasRelationsOfType(ReferentRelation.class)) {
			List<Relation> referentRels = start.getRelationsOfType(ReferentRelation.class);
			Set<Entity> candidates = new HashSet<>();
			for (Relation relation : referentRels) {
				ReferentRelation refRel = (ReferentRelation) relation;
				if (refRel.getEnd().equals(start)) {
					if (ContextUtils.getMostLikelyReferentRelation(refRel.getStart().getRelationsOfType(ReferentRelation.class),
							refRel.getStart()) != null
							&& refRel.equals(ContextUtils.getMostLikelyReferentRelation(
									refRel.getStart().getRelationsOfType(ReferentRelation.class), refRel.getStart()))) {
						if (ContextUtils.getPositionInUtterance(refRel.getStart()) > current
								&& ContextUtils.getPositionInUtterance(refRel.getStart()) < max) {
							candidates
									.add(getLastRefBefore(refRel.getStart(), ContextUtils.getPositionInUtterance(refRel.getStart()), max));
						}

					}
				}
			}
			int pos = current;
			for (Entity entity : candidates) {
				if (ContextUtils.getPositionInUtterance(entity) > pos) {
					result = entity;
					pos = ContextUtils.getPositionInUtterance(entity);
				}
			}

		}
		return result;
	}

	private static final ReferentRelation getMostLikelyReferent(Collection<? extends Relation> list, Entity start) {
		ReferentRelation result = null;
		double confidence = -1.0;
		for (Relation relation : list) {
			if (relation instanceof ReferentRelation) {
				ReferentRelation refRel = (ReferentRelation) relation;
				if (refRel.getConfidence() > confidence) {
					if (refRel.getEnd() instanceof Entity && refRel.getStart().equals(start)) {
						result = (ReferentRelation) relation;
						confidence = refRel.getConfidence();
					}
				}
			}

		}
		return result;
	}

	/**
	 * Checks if there is an idetified Action in between the two Entities
	 * 
	 * @param previous
	 * @param current
	 * @param currentContext
	 * @return
	 */
	public static final boolean actionInBetween(Entity previous, Entity current, Context currentContext) {
		Integer startOfCurrent = (Integer) current.getReference().get(0).getAttributeValue("position");

		Integer endOfPrevious = (Integer) previous.getReference().get(previous.getReference().size() - 1).getAttributeValue("position");
		for (Action action : currentContext.getActions()) {
			Integer actionPos = (Integer) action.getReference().get(0).getAttributeValue("position");
			if (endOfPrevious < actionPos && actionPos < startOfCurrent) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the most likely cmdType of the {@link INode}s of the phrase
	 * 
	 * @param phrase
	 * @return
	 */
	public static final CommandType getMostLikelyCmdType(List<INode> phrase) {
		List<CommandType> cmdTypes = new ArrayList<>();
		for (INode node : phrase) {
			if (node.getAttributeNames().contains(COMMAND_TYPE) && node.getAttributeValue(COMMAND_TYPE) != null) {
				CommandType cmdType = (CommandType) node.getAttributeValue(COMMAND_TYPE);
				if (!cmdTypes.contains(cmdType)) {
					cmdTypes.add(cmdType);
				}
			}
		}
		if (cmdTypes.size() == 1) {
			return cmdTypes.get(0);
		} else {
			HashMap<CommandType, Integer> map = new HashMap<>();

			for (INode node : phrase) {
				if (node.getAttributeNames().contains(COMMAND_TYPE) && node.getAttributeValue(COMMAND_TYPE) != null) {
					CommandType cmdType = (CommandType) node.getAttributeValue(COMMAND_TYPE);
					Integer value = map.get(cmdType);
					if (value != null) {
						map.replace(cmdType, value + 1);
					} else {
						map.put(cmdType, 1);
					}
				}
			}
			CommandType max = CommandType.INDEPENDENT_STATEMENT;
			int maxValue = 0;
			for (java.util.Map.Entry<CommandType, Integer> entry : map.entrySet()) {
				if (entry.getValue() > maxValue) {
					maxValue = entry.getValue();
					max = entry.getKey();
				}
			}
			return max;
		}
	}

	/**
	 * Returns the most likely condition number of the phrase {@link INode}s
	 * 
	 * @param phrase
	 * @return
	 */
	public static final int getMostLikelyConditionNumber(List<INode> phrase) {
		List<Integer> statements = new ArrayList<>();
		for (INode node : phrase) {
			Integer statement = -1;
			if (node.getAttributeNames().contains(STATEMENT_NUMBER)) {
				statement = (Integer) node.getAttributeValue(STATEMENT_NUMBER);
			}

			if (!statements.contains(statement)) {
				statements.add(statement);
			}
		}
		if (statements.size() == 1) {
			return statements.get(0);
		} else {
			int[] buckets = new int[statements.size()];
			for (int i = 0; i < buckets.length; i++) {
				buckets[i] = 0;
			}
			for (INode node : phrase) {
				Integer statement = statements.indexOf(-1);
				if (node.getAttributeNames().contains(STATEMENT_NUMBER)) {
					statement = statements.indexOf((Integer) node.getAttributeValue(STATEMENT_NUMBER));
				}
				buckets[statement]++;
			}
			int maxIndex = 0;
			int maxValue = 0;
			for (int i = 0; i < buckets.length; i++) {
				int value = buckets[i];
				if (value > maxValue) {
					maxValue = value;
					maxIndex = i;
				}
			}
			return statements.get(maxIndex);
		}
	}

	public static final boolean isSourceOrDestination(Entity entity) {
		if (entity.hasRelationsOfType(ActionEntityRelation.class)) {
			List<Relation> relations = entity.getRelationsOfType(ActionEntityRelation.class);
			for (Relation relation : relations) {
				ActionEntityRelation aeRel = (ActionEntityRelation) relation;
				if (aeRel instanceof SRLArgumentRelation) {
					SRLArgumentRelation srl = (SRLArgumentRelation) aeRel;
					if (srl.getVerbNetRoles().contains("Destination") || srl.getVerbNetRoles().contains("Source")) {
						return true;
					}
					if (srl.getFrameNetRoles().contains("Destination") || srl.getFrameNetRoles().contains("Source")) {
						return true;
					}
				}

			}

		}
		return false;
	}

	/**
	 * Returns the first Action the specified Entity is part of
	 * 
	 * @param current
	 * @return
	 */
	public static final Action getActionForEntity(Entity current) {
		if (current.hasRelationsOfType(ActionEntityRelation.class)) {
			List<Relation> rels = current.getRelationsOfType(ActionEntityRelation.class);
			ActionEntityRelation aeRel = (ActionEntityRelation) rels.get(0);
			return aeRel.getAction();
		}
		return null;

	}

	/**
	 * Returns the {@link Entity} which takes a ProtoPatient role in the
	 * specified Action
	 * 
	 * @param action
	 * @return
	 */
	public static final Entity getProtoPatientForAction(Action action) {
		if (action.hasRelationsOfType(SRLArgumentRelation.class)) {
			List<Relation> rels = action.getRelationsOfType(SRLArgumentRelation.class);
			for (Relation relation : rels) {
				SRLArgumentRelation argRel = (SRLArgumentRelation) relation;
				if (isProtoPatientRelation(argRel)) {
					return argRel.getEntity();
				}
			}
			for (Relation relation : rels) {
				if (relation.getName().equals("A1")) {
					return ((SRLArgumentRelation) relation).getEntity();
				}
			}
		}
		return null;
	}
}
