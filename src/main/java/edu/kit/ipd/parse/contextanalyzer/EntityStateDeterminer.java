/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import edu.kit.ipd.parse.contextanalyzer.data.AbstractConcept;
import edu.kit.ipd.parse.contextanalyzer.data.ActionConcept;
import edu.kit.ipd.parse.contextanalyzer.data.CommandType;
import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.EntityConcept;
import edu.kit.ipd.parse.contextanalyzer.data.ObjectConcept;
import edu.kit.ipd.parse.contextanalyzer.data.State;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.IStateOwner;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ActionConceptRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.EntityConceptRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.EntityStateRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ReferentRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.SRLArgumentRelation;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.contextanalyzer.util.WordNetUtils;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;
import info.debatty.java.stringsimilarity.JaroWinkler;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;

/**
 * This class analyzes the constructed Context {@link Entity}s for possible
 * {@link State} they can be in. Adds {@link EntityStateRelation} for found
 * {@link State}s
 * 
 * @author Tobias Hey
 *
 */
public class EntityStateDeterminer implements IContextAnalyzer {

	private static final double JW_SIMILARITY_THRESHOLD = 0.92;
	private Dictionary dictionary;
	private JaroWinkler jaroWinkler = new JaroWinkler();

	/**
	 * Constructs new {@link EntityStateDeterminer}
	 * 
	 * @param dictionary
	 *            The WordNet {@link Dictionary}
	 * 
	 */
	public EntityStateDeterminer(Dictionary dictionary) {
		this.dictionary = dictionary;
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
		List<Entity> entities = new ArrayList<>(context.getEntities());
		Collections.sort(entities);
		for (Entity entity : entities) {
			if (entity instanceof ObjectEntity) {
				// has entity concept relation
				if (entity.hasRelationsOfType(EntityConceptRelation.class)) {
					EntityConceptRelation rel = ContextUtils
							.getMostLikelyEntityConceptRelation(entity.getRelationsOfType(EntityConceptRelation.class));
					if (rel != null && rel.getEnd() instanceof ObjectConcept) {
						ObjectConcept entityConcept = (ObjectConcept) rel.getEnd();

						// check adjectives for state info
						Set<State> adjectiveStates = checkAdjectives((ObjectEntity) entity, entityConcept);
						Set<State> prevStates = new TreeSet<>();
						// check coref
						if (entity.hasRelationsOfType(ReferentRelation.class)) {
							Entity lastBefore = ContextUtils.getLastPrecedingReferent(entity);
							if (lastBefore != null && lastBefore instanceof IStateOwner) {

								IStateOwner stateOwner = (IStateOwner) lastBefore;
								if (stateOwner.hasState()) {
									prevStates.addAll(stateOwner.getStates());
								}
							}
						}
						Set<State> srlStates = new TreeSet<>();
						if (!entity.getCommandType().equals(CommandType.IF_STATEMENT)) {
							// is entity part of an action recognized by srl
							if (entity.hasRelationsOfType(SRLArgumentRelation.class)) {
								List<SRLArgumentRelation> srlRels = getSRLArgumentRelationsInOrderOfVerbOccurrence(
										entity.getRelationsOfType(SRLArgumentRelation.class));
								// for each action with concept relation
								for (SRLArgumentRelation srlArgumentRelation : srlRels) {

									if (isProtoPatient(srlArgumentRelation)) {
										if (srlArgumentRelation.getAction().hasRelationsOfType(ActionConceptRelation.class)) {

											ActionConceptRelation acRel = ContextUtils.getMostLikelyActionConceptRelation(
													srlArgumentRelation.getAction().getRelationsOfType(ActionConceptRelation.class));
											if (acRel != null) {
												ActionConcept actionConcept = (ActionConcept) acRel.getEnd();

												srlStates = getResultingStates(entityConcept, actionConcept);
											}
										}
									}
								}
							}
							Set<State> resultingStates = combineStates(prevStates, adjectiveStates, srlStates);
							for (State state : resultingStates) {

								// remove previous state relation
								if (entity.hasRelationsOfType(EntityStateRelation.class)) {
									List<Relation> prevESRels = entity.getRelationsOfType(EntityStateRelation.class);
									for (Relation prevRel : prevESRels) {
										EntityStateRelation prevESRel = (EntityStateRelation) prevRel;
										if (prevESRel.getEnd().equals(state) || state.getAssociatedStates().contains(prevESRel.getEnd())) {
											entity.getRelations().remove(prevRel);
											prevESRel.getEnd().getRelations().remove(prevRel);
										}
									}
								}
								// add new
								EntityStateRelation relation = new EntityStateRelation(entity, state, 1.0);
								entity.addRelation(relation);
								state.addRelation(relation);
							}
						}
					}
				}
			} else if (entity instanceof PronounEntity) {
				Set<State> prevStates = new TreeSet<>();
				// check coref
				if (entity.hasRelationsOfType(ReferentRelation.class)) {
					Entity lastBefore = ContextUtils.getLastPrecedingReferent(entity);
					if (lastBefore != null && lastBefore instanceof IStateOwner) {

						IStateOwner stateOwner = (IStateOwner) lastBefore;
						if (stateOwner.hasState()) {
							prevStates.addAll(stateOwner.getStates());
						}
					}
				}
				Set<State> srlStates = new TreeSet<>();
				if (!entity.getCommandType().equals(CommandType.IF_STATEMENT)) {
					if (entity.hasRelationsOfType(SRLArgumentRelation.class)) {
						List<SRLArgumentRelation> srlRels = getSRLArgumentRelationsInOrderOfVerbOccurrence(
								entity.getRelationsOfType(SRLArgumentRelation.class));
						// for each action with concept relation
						for (SRLArgumentRelation srlArgumentRelation : srlRels) {

							if (isProtoPatient(srlArgumentRelation)) {
								if (srlArgumentRelation.getAction().hasRelationsOfType(ActionConceptRelation.class)) {

									ActionConceptRelation acRel = ContextUtils.getMostLikelyActionConceptRelation(
											srlArgumentRelation.getAction().getRelationsOfType(ActionConceptRelation.class));
									if (acRel != null) {
										ActionConcept actionConcept = (ActionConcept) acRel.getEnd();

										srlStates = actionConcept.getStatesChangedTo();

									}
								}
							}
						}
					}
				}
				Set<State> resultingStates = combineStates(prevStates, srlStates);
				for (State state : resultingStates) {

					// remove previous state relation
					if (entity.hasRelationsOfType(EntityStateRelation.class)) {
						List<Relation> prevESRels = entity.getRelationsOfType(EntityStateRelation.class);
						for (Relation prevRel : prevESRels) {
							EntityStateRelation prevESRel = (EntityStateRelation) prevRel;
							if (prevESRel.getEnd().equals(state) || state.getAssociatedStates().contains(prevESRel.getEnd())) {
								entity.getRelations().remove(prevRel);
								prevESRel.getEnd().getRelations().remove(prevRel);
							}
						}
					}
					// add new
					EntityStateRelation relation = new EntityStateRelation(entity, state, 1.0);
					entity.addRelation(relation);
					state.addRelation(relation);
				}
			}

		}

	}

	private Set<State> combineStates(Set<State> prevStates, Set<State> srlStates) {
		Set<State> result = new TreeSet<>();
		if (!prevStates.isEmpty()) {
			for (State prevState : prevStates) {
				boolean isAssociated = false;
				for (State state : srlStates) {
					if (state.getAssociatedStates().contains(prevState)) {
						isAssociated = true;
					}
				}
				if (!isAssociated) {
					result.add(prevState);
				}
			}
		}
		result.addAll(srlStates);
		return result;
	}

	private Set<State> combineStates(Set<State> prevStates, Set<State> adjectiveStates, Set<State> srlStates) {
		Set<State> result = new TreeSet<>();
		Set<State> combined = new TreeSet<>();
		combined.addAll(adjectiveStates);
		combined.addAll(srlStates);

		for (State prevState : prevStates) {
			boolean isAssociated = false;
			for (State state : combined) {
				if (state.getAssociatedStates().contains(prevState)) {
					isAssociated = true;
				}
			}
			if (!isAssociated) {
				result.add(prevState);
			}
		}
		for (State state : adjectiveStates) {
			boolean isAssociated = false;
			for (State srlState : srlStates) {
				if (srlState.getAssociatedStates().contains(state)) {
					isAssociated = true;
				}
			}
			if (!isAssociated) {
				result.add(state);
			}
		}

		result.addAll(srlStates);
		return result;
	}

	private Set<State> checkAdjectives(ObjectEntity entity, ObjectConcept concept) {
		Set<State> result = new TreeSet<>();
		for (String adjective : entity.getDescribingAdjectives()) {
			for (State state : concept.getStates()) {
				if (jaroWinkler.similarity(adjective.toLowerCase(),
						state.getName().trim().replaceAll(" ", "").toLowerCase()) > JW_SIMILARITY_THRESHOLD) {
					result.add(state);
					break;
				} else {
					List<String> synonyms = WordNetUtils.getSynonyms(adjective.toLowerCase(), POS.ADJECTIVE, dictionary);
					for (String string : synonyms) {
						if (jaroWinkler.similarity(string.toLowerCase(),
								state.getName().trim().replaceAll(" ", "").toLowerCase()) > JW_SIMILARITY_THRESHOLD) {
							result.add(state);
							break;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Checks if the {@link Entity} takes a proto patient role in this
	 * {@link SRLArgumentRelation}
	 * 
	 * @param srlArgumentRelation
	 * @return
	 */
	private boolean isProtoPatient(SRLArgumentRelation srlArgumentRelation) {
		List<String> verbNetRoles = srlArgumentRelation.getVerbNetRoles();
		if (verbNetRoles.contains("Theme") || verbNetRoles.contains("Patient")) {
			return true;
		}
		List<String> frameNetRoles = srlArgumentRelation.getFrameNetRoles();
		if (frameNetRoles.contains("Theme")) {
			return true;
		}

		return false;
	}

	/**
	 * Returns all resulting {@link State} which result from the changed States
	 * of the {@link ActionConcept} with any of the {@link State} of the
	 * {@link EntityConcept} or the part Concepts of the {@link EntityConcept}
	 * 
	 * @param objectConcept
	 * @param actionConcept
	 */
	private Set<State> getResultingStates(ObjectConcept objectConcept, ActionConcept actionConcept) {
		Set<State> result = new TreeSet<>();
		if (actionConcept.hasStatesChangedTo() && objectConcept.hasStates()) {
			Set<State> states = objectConcept.getStates();
			Set<State> statesChangedTo = actionConcept.getStatesChangedTo();
			for (State state : statesChangedTo) {
				if (states.contains(state)) {
					result.add(state);
				}
			}
		}
		if (objectConcept.hasPartConcepts()) {
			for (AbstractConcept concept : objectConcept.getPartConcepts()) {
				if (concept instanceof ObjectConcept) {
					result.addAll(getResultingStates((ObjectConcept) concept, actionConcept));
				}
			}
		}
		return result;
	}

	private List<SRLArgumentRelation> getSRLArgumentRelationsInOrderOfVerbOccurrence(List<Relation> srlRelations) {
		List<SRLArgumentRelation> result = new ArrayList<>();
		for (Relation relation : srlRelations) {
			if (relation instanceof SRLArgumentRelation) {
				SRLArgumentRelation srlRel = (SRLArgumentRelation) relation;
				result.add(srlRel);
			}
		}
		Collections.sort(result, new Comparator<SRLArgumentRelation>() {

			@Override
			public int compare(SRLArgumentRelation o1, SRLArgumentRelation o2) {
				return Integer.compare((int) o1.getAction().getReference().get(0).getAttributeValue("position"),
						(int) o2.getAction().getReference().get(0).getAttributeValue("position"));
			}
		});
		return result;
	}

}
