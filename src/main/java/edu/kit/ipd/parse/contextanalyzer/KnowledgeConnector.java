/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.contextanalyzer.data.AbstractConcept;
import edu.kit.ipd.parse.contextanalyzer.data.Action;
import edu.kit.ipd.parse.contextanalyzer.data.ActionConcept;
import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.contextanalyzer.data.EntityConcept;
import edu.kit.ipd.parse.contextanalyzer.data.ObjectConcept;
import edu.kit.ipd.parse.contextanalyzer.data.State;
import edu.kit.ipd.parse.contextanalyzer.data.SubjectConcept;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.GrammaticalNumber;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity.Gender;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ActionConceptRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.EntityConceptRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.contextanalyzer.util.LeastCommonSubsumer;
import edu.kit.ipd.parse.contextanalyzer.util.WordNetUtils;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.ontology_connection.Domain;
import edu.kit.ipd.parse.ontology_connection.IIndividual;
import edu.kit.ipd.parse.ontology_connection.method.IMethod;
import edu.kit.ipd.parse.ontology_connection.object.IObject;
import edu.kit.ipd.parse.ontology_connection.state.IState;
import edu.kit.ipd.parse.ontology_connection.system.ISystemClass;
import info.debatty.java.stringsimilarity.JaroWinkler;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;

/**
 * This {@link IContextAnalyzer} analyzes the found {@link Entity}s and
 * {@link Action}s for equivalents in the domain knowledge ontology or builds
 * new concepts from the Informations in the {@link ContextIndividual}s.
 * 
 * @author Tobias Hey
 *
 */
public class KnowledgeConnector implements IContextAnalyzer {

	private static final Double LIN_SIMILARITY_THRESHOLD = 0.75;

	private static final Double jwSimilarityThreshold = 0.92;

	private Dictionary dictionary;
	private Domain domain;
	private Context context;
	private JaroWinkler jaroWinkler = new JaroWinkler();
	private static final Logger logger = LoggerFactory.getLogger(KnowledgeConnector.class);

	/**
	 * Constructs new {@link KnowledgeConnector}
	 * 
	 * @param dictionary
	 *            The WordNet {@link Dictionary}
	 * @param domain
	 *            The domainknowledge {@link Domain}
	 */
	public KnowledgeConnector(Dictionary dictionary, Domain domain) {
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
		this.context = context;
		List<Entity> entities = new ArrayList<>(context.getEntities());
		Collections.sort(entities);
		for (Entity entity : entities) {
			connectEntity(entity);
		}
		for (Action action : context.getActions()) {
			connectAction(action);
		}
		Set<AbstractConcept> concepts = new HashSet<>(context.getConcepts());
		for (AbstractConcept concept : concepts) {
			if (concept instanceof ObjectConcept) {
				findCommonSubsumers((ObjectConcept) concept);
			}
		}
	}

	/**
	 * Connects the specified {@link Action} with the Domainknowledge and
	 * creates {@link AbstractConcept}s for the found connections
	 * 
	 * @param action
	 *            The {@link Action} to connect
	 */
	private void connectAction(Action action) {
		List<Pair<IIndividual, Double>> directCandidates = getDirectCandidates(action.getName(), domain.getMethods().asSet());

		if (directCandidates.size() == 1) {
			IMethod method = (IMethod) directCandidates.get(0).getLeft();
			extractMethodConcept(action, method, directCandidates.get(0).getRight());

		} else if (directCandidates.size() > 1) {
			List<Pair<IIndividual, Double>> candidates = getMostLikelyCandidate(directCandidates);
			if (candidates.size() == 1) {
				IMethod method = (IMethod) candidates.get(0).getLeft();
				extractMethodConcept(action, method, candidates.get(0).getRight());
			} else {
				//TODO use parameters to try match the right one
			}

		} else if (directCandidates.isEmpty()) {
			List<Pair<IIndividual, Double>> synonymCandidates = getSynonymCandidates(action.getSynonyms(), domain.getMethods().asSet());

			if (synonymCandidates.size() == 1) {
				IMethod method = (IMethod) synonymCandidates.get(0).getLeft();
				extractMethodConcept(action, method, synonymCandidates.get(0).getRight());
			} else if (synonymCandidates.size() > 1) {
				List<Pair<IIndividual, Double>> candidates = getMostLikelyCandidate(synonymCandidates);
				if (candidates.size() == 1) {
					IMethod method = (IMethod) candidates.get(0).getLeft();
					extractMethodConcept(action, method, candidates.get(0).getRight());
				} else {
					//TODO use parameters to try match the right one
				}

			} else if (synonymCandidates.isEmpty()) {
				buildMethodConceptFromWordNet(action);
			}
		}

	}

	/**
	 * Extracts an {@link ActionConcept} from the specified {@link Action}
	 * {@link IMethod} connection
	 * 
	 * @param action
	 * @param method
	 * @param similarity
	 * @return
	 */
	private ActionConcept extractMethodConcept(Action action, IMethod method, Double similarity) {
		ActionConcept actionConcept = extractMethodConcept(method);
		if (!actionConcept.hasIndexWordLemma()) {
			IndexWord indexWord = WordNetUtils.getIndexWord(action.getName(), POS.VERB, dictionary);
			if (indexWord != null) {
				actionConcept.setIndexWordLemma(indexWord.getLemma());
			}
		}
		for (String synonym : action.getSynonyms()) {

			actionConcept.addSynonym(synonym);

		}

		for (String antonym : action.getAntonyms()) {
			List<Pair<IIndividual, Double>> directCandidates = getDirectCandidates(antonym.toLowerCase(), domain.getMethods().asSet());
			ActionConcept candidate = getMethodConcept((int) action.getReference().get(0).getAttributeValue("position"), directCandidates);
			if (candidate != null && !candidate.equals(actionConcept)) {
				actionConcept.addAntonymAction(candidate);
				candidate.addAntonymAction(actionConcept);
			}
		}
		boolean hasRel = false;
		if (action.hasRelationsOfType(ActionConceptRelation.class)) {
			List<Relation> relations = action.getRelationsOfType(ActionConceptRelation.class);
			for (Relation relation : relations) {
				ActionConceptRelation acRel = (ActionConceptRelation) relation;
				if (acRel.getEnd().equals(actionConcept) && acRel.getStart().equals(action)) {
					if (acRel.getConfidence() != similarity) {
						action.getRelations().remove(relation);
						actionConcept.getRelations().remove(relation);
						ActionConceptRelation newRel = new ActionConceptRelation(action, actionConcept, similarity);
						action.addRelation(newRel);
						actionConcept.addRelation(newRel);
						hasRel = true;
					} else {
						hasRel = true;
					}

				}
			}
		}
		if (!hasRel) {
			ActionConceptRelation relation = new ActionConceptRelation(action, actionConcept, similarity);
			action.addRelation(relation);
			actionConcept.addRelation(relation);
		}
		return actionConcept;

	}

	/**
	 * Gives or creates an {@link ActionConcept} from the specified
	 * {@link IMethod}
	 * 
	 * @param method
	 * @return
	 */
	private ActionConcept extractMethodConcept(IMethod method) {
		ActionConcept actionConcept;
		if (context.hasConceptOfIndividual(method.getFullName())) {
			actionConcept = (ActionConcept) context.getConceptOfIndividual(method.getFullName());
		} else {
			actionConcept = new ActionConcept(method.getShortName());
			actionConcept.setOntologyIndividual(method.getFullName());
			context.addConcept(actionConcept);
			IndexWord indexWord = WordNetUtils.getIndexWord(method.getShortName(), POS.VERB, dictionary);
			if (indexWord != null) {
				actionConcept.setIndexWordLemma(indexWord.getLemma());
			}
			for (String synonym : WordNetUtils.getSynonyms(indexWord)) {

				actionConcept.addSynonym(synonym);

			}

			for (String antonym : WordNetUtils.getAntonyms(indexWord)) {
				List<Pair<IIndividual, Double>> directCandidates = getDirectCandidates(antonym.toLowerCase(), domain.getMethods().asSet());
				ActionConcept candidate = getMethodConcept(0, directCandidates);
				if (candidate != null && !candidate.equals(actionConcept)) {
					actionConcept.addAntonymAction(candidate);
					candidate.addAntonymAction(actionConcept);
				}
			}

			for (IState state : method.getChangedStates()) {
				actionConcept.addStateChangedTo(extractStateConcept(state));
			}
			if (method.hasSameIndividuals()) {
				for (IIndividual sameIndividual : method.getSameIndividuals()) {
					AbstractConcept sameConcept = null;
					if (context.hasConceptOfIndividual(sameIndividual.getFullName())) {
						sameConcept = context.getConceptOfIndividual(sameIndividual.getFullName());
					} else {
						if (sameIndividual instanceof IMethod) {
							sameConcept = extractMethodConcept((IMethod) sameIndividual);
						}
					}
					if (sameConcept != null) {
						actionConcept.addEqualConcept(sameConcept);
						sameConcept.addEqualConcept(actionConcept);
					}
				}
			}
		}
		return actionConcept;
	}

	/**
	 * Builds an {@link ActionConcept} from the Information extracted from
	 * WordNet and saved in the {@link Action}
	 * 
	 * @param action
	 * @return
	 */
	private ActionConcept buildMethodConceptFromWordNet(Action action) {
		ActionConcept actionConcept = null;
		double confidence = 0.1;
		IndexWord indexWord = WordNetUtils.getIndexWord(action.getName(), POS.VERB, dictionary);
		if (indexWord != null) {
			if (context.hasConcept(indexWord.getLemma())) {
				AbstractConcept concept = context.getConcept(indexWord.getLemma());

				if (concept instanceof ActionConcept) {
					actionConcept = (ActionConcept) concept;
					// only if is first run
					if (!context.isReadFromGraph()) {
						if (actionConcept.hasRelationsOfType(ActionConceptRelation.class)) {
							List<Relation> acRel = actionConcept.getRelationsOfType(ActionConceptRelation.class);
							confidence = ((ActionConceptRelation) acRel.get(0)).getConfidence();
							confidence *= 3;
							if (confidence > 1.0) {
								confidence = 1.0;
							}
							for (Relation relation : acRel) {
								if (relation instanceof ActionConceptRelation) {
									((ActionConceptRelation) relation).setConfidence(confidence);
								}
							}
						}
					}
				} else {
					actionConcept = new ActionConcept(indexWord.getLemma());
					context.addConcept(actionConcept);
				}
			} else {
				actionConcept = new ActionConcept(indexWord.getLemma());
				context.addConcept(actionConcept);
			}
			if (!actionConcept.hasIndexWordLemma()) {
				if (indexWord != null) {
					actionConcept.setIndexWordLemma(indexWord.getLemma());
				}
			}
			for (String synonym : action.getSynonyms()) {

				actionConcept.addSynonym(synonym);

			}

			for (String antonym : action.getAntonyms()) {
				AbstractConcept candidate = getWordNetCandidate(antonym, (int) action.getReference().get(0).getAttributeValue("position"),
						domain.getMethods().asSet());
				if (candidate != null) {
					actionConcept.addEqualConcept(candidate);
					candidate.addEqualConcept(candidate);
				}

			}
			// check previously defined relations
			boolean hasRel = false;
			if (action.hasRelationsOfType(ActionConceptRelation.class)) {
				List<Relation> relations = action.getRelationsOfType(ActionConceptRelation.class);
				for (Relation relation : relations) {
					ActionConceptRelation acRel = (ActionConceptRelation) relation;
					if (acRel.getEnd().equals(actionConcept) && acRel.getStart().equals(action)) {
						if (acRel.getConfidence() < confidence) {
							action.getRelations().remove(relation);
							actionConcept.getRelations().remove(relation);
							ActionConceptRelation newRel = new ActionConceptRelation(action, actionConcept, confidence);
							action.addRelation(newRel);
							actionConcept.addRelation(newRel);
							hasRel = true;
						} else {
							hasRel = true;
						}

					}
				}
			}
			if (!hasRel) {
				ActionConceptRelation relation = new ActionConceptRelation(action, actionConcept, confidence);
				action.addRelation(relation);
				actionConcept.addRelation(relation);
			}
		}
		return actionConcept;
	}

	/**
	 * Analyzes the given List for the most likely {@link IMethod} and extracts
	 * the {@link ActionConcept} belonging to this {@link IMethod}
	 * 
	 * @param candidates
	 * @return
	 */
	private ActionConcept getMethodConcept(int position, List<Pair<IIndividual, Double>> candidates) {
		if (candidates.size() == 1) {
			return extractMethodConcept((IMethod) candidates.get(0).getLeft());

		} else if (candidates.size() > 1) {
			List<Pair<IIndividual, Double>> likelyCandidates = getMostLikelyCandidate(candidates);
			if (likelyCandidates.size() == 1) {
				return extractMethodConcept((IMethod) likelyCandidates.get(0).getLeft());
			} else {
				//TODO use parameters to try match the right one
				Set<AbstractConcept> superConcepts = getAlreadyConnectedSuperConcepts(likelyCandidates);
				Pair<IIndividual, Double> candidate = getCandidateFromSuperConcepts(position, likelyCandidates, superConcepts);
				if (candidate != null) {
					return extractMethodConcept((IMethod) candidate.getLeft());
				}
			}

		} else if (candidates.isEmpty()) {
		}
		return null;
	}

	/**
	 * Connects the specified {@link Entity} with the Domainknowledge and
	 * creates {@link AbstractConcept}s for the found connections
	 * 
	 * @param entity
	 *            The {@link Entity} to connect
	 */
	private void connectEntity(Entity entity) {
		if (entity instanceof SubjectEntity) {
			SubjectEntity subject = (SubjectEntity) entity;
			Set<ISystemClass> systems = domain.getSystemClasses().asSet();
			for (ISystemClass system : systems) {
				if (subject.getGender().equals(Gender.NEUTRAL) || subject.getGender().equals(Gender.UNKNOWN)) {
					if (subject.getGrammaticalNumber().equals(GrammaticalNumber.SINGULAR)
							|| subject.getGrammaticalNumber().equals(GrammaticalNumber.MASS_OR_SINGULAR)) {
						Double similarity = jaroWinkler.similarity(getComparableName(entity), system.getShortName().toLowerCase());
						if (similarity > jwSimilarityThreshold) {
							extractSystemConcept(subject, system, similarity);

							break;
						}
					}
				}
			}
		} else if (entity instanceof ObjectEntity) {
			ObjectEntity objectEntity = (ObjectEntity) entity;
			Set<IObject> objects = domain.getObjects().asSet();

			String comparableName = getComparableName(objectEntity);
			List<Pair<IIndividual, Double>> directCandidates = getDirectCandidates(comparableName, objects);

			if (directCandidates.isEmpty()) {
				directCandidates = getDirectCandidates(getAdjectiveName(objectEntity), objects);
			}
			if (directCandidates.size() == 1) {
				IObject object = (IObject) directCandidates.get(0).getLeft();
				extractObjectConcept(objectEntity, object, directCandidates.get(0).getRight());
			} else if (directCandidates.size() > 1) {

				List<Pair<IIndividual, Double>> candidates = getMostLikelyCandidate(directCandidates);
				if (candidates.size() == 1) {
					IObject object = (IObject) candidates.get(0).getLeft();
					extractObjectConcept(objectEntity, object, candidates.get(0).getRight());
				} else {
					Set<AbstractConcept> superConcepts = getAlreadyConnectedSuperConcepts(directCandidates);
					Pair<IIndividual, Double> candidate = getCandidateFromSuperConcepts(
							(int) entity.getReference().get(0).getAttributeValue("position"), directCandidates, superConcepts);
					if (candidate != null) {
						IObject object = (IObject) candidate.getLeft();
						extractObjectConcept(objectEntity, object, candidate.getRight());
					} else {
						//TODO "door of ..."
					}
				}
			} else if (directCandidates.isEmpty()) {
				List<Pair<IIndividual, Double>> synonymCandidates = getSynonymCandidates(objectEntity.getSynonyms(), objects);
				if (synonymCandidates.size() == 1) {
					IObject object = (IObject) synonymCandidates.get(0).getLeft();
					extractObjectConcept(objectEntity, object, synonymCandidates.get(0).getRight() * 0.9);
				} else if (synonymCandidates.size() > 1) {
					List<Pair<IIndividual, Double>> candidates = getMostLikelyCandidate(synonymCandidates);
					if (candidates.size() == 1) {
						IObject object = (IObject) candidates.get(0).getLeft();
						extractObjectConcept(objectEntity, object, candidates.get(0).getRight());
					} else {
						Set<AbstractConcept> superConcepts = getAlreadyConnectedSuperConcepts(synonymCandidates);
						Pair<IIndividual, Double> candidate = getCandidateFromSuperConcepts(
								(int) entity.getReference().get(0).getAttributeValue("position"), synonymCandidates, superConcepts);
						if (candidate != null) {
							IObject object = (IObject) candidate.getLeft();
							extractObjectConcept(objectEntity, object, candidate.getRight());
						} else {
							//TODO "door of ..."
						}
					}
				} else if (synonymCandidates.isEmpty()) {
					buildObjectConceptFromWordNet(objectEntity);
					//					for (IObject object : objects) {
					//
					//						for (String hypernym : objectEntity.getDirectHypernyms()) {
					//							Double hypSim = jaroWinkler.similarity(object.getShortName().toLowerCase(),
					//									hypernym.replaceAll(" ", "").toLowerCase());
					//							if (hypSim > 0.9) {
					//								directCandidates.add(object);
					//								break;
					//							}
					//						}
					//
					//					}
				}
			}
		}

	}

	/**
	 * @param position2
	 * @param objectEntity
	 * @param directCandidates
	 * @param superConcepts
	 */
	private Pair<IIndividual, Double> getCandidateFromSuperConcepts(int position, List<Pair<IIndividual, Double>> directCandidates,
			Set<AbstractConcept> superConcepts) {
		List<Entity> entities = new ArrayList<>(context.getEntities());
		Collections.sort(entities);
		int index = 0;
		for (Entity entity : entities) {
			if (((int) entity.getReference().get(0).getAttributeValue("position")) >= position) {
				index = entities.indexOf(entity);
				break;
			}
		}
		List<Entity> preceeding = entities.subList(0, index);
		Collections.reverse(preceeding);
		for (Entity preceedingEntity : preceeding) {
			if (preceedingEntity instanceof ObjectEntity) {
				if (preceedingEntity.hasAssociatedConcept()) {
					EntityConcept preceedingConcept = ContextUtils.getMostLikelyEntityConcept(preceedingEntity.getAssociatedConcepts());
					if (superConcepts.contains(preceedingConcept)) {
						return getCandidateOfSuperConcept(preceedingConcept, directCandidates);

					}
				}
			}
		}
		return null;
	}

	private Pair<IIndividual, Double> getCandidateOfSuperConcept(EntityConcept preceedingConcept,
			List<Pair<IIndividual, Double>> directCandidates) {
		for (Pair<IIndividual, Double> pair : directCandidates) {
			IIndividual individual = pair.getLeft();
			if (individual instanceof IObject) {
				IObject object = (IObject) individual;
				if (object.hasSuperObject()) {
					IObject superObject = object.getSuperObject();
					if (preceedingConcept.getOntologyIndividual().equals(superObject.getFullName())) {
						return pair;
					}
				}
			}
		}
		return null;
	}

	private Set<AbstractConcept> getAlreadyConnectedSuperConcepts(List<Pair<IIndividual, Double>> directCandidates) {
		Set<AbstractConcept> result = new HashSet<>();
		for (Pair<IIndividual, Double> pair : directCandidates) {
			IIndividual individual = pair.getLeft();
			if (individual instanceof IObject) {
				IObject object = (IObject) individual;
				if (object.hasSuperObject()) {
					IObject superObject = object.getSuperObject();
					if (context.hasConceptOfIndividual(superObject.getFullName())) {
						AbstractConcept concept = context.getConceptOfIndividual(superObject.getFullName());
						result.add(concept);
					}
				}

			}

		}
		return result;
	}

	/**
	 * Extracts an {@link EntityConcept} from the specified {@link ObjectEntity}
	 * {@link IObject} connection
	 * 
	 * @param objectEntity
	 * @param object
	 * @param similarity
	 * @return
	 */
	private EntityConcept extractObjectConcept(ObjectEntity objectEntity, IObject object, Double similarity) {
		ObjectConcept objectConcept = extractObjectConcept(object);
		for (String synonym : objectEntity.getSynonyms()) {
			List<Pair<IIndividual, Double>> synonymCandidates = getDirectCandidates(synonym.toLowerCase(), domain.getObjects().asSet());
			EntityConcept candidate = getObjectConcept((int) objectEntity.getReference().get(0).getAttributeValue("position"),
					synonymCandidates);
			if (candidate != null && !candidate.equals(objectConcept)) {
				objectConcept.addEqualConcept(candidate);
				candidate.addEqualConcept(objectConcept);
			} else {
				objectConcept.addSynonym(synonym);
			}
		}
		if (!objectConcept.hasIndexWordLemma()) {
			IndexWord indexWord = WordNetUtils.getIndexWord(getComparableName(objectEntity), POS.NOUN, dictionary);
			if (indexWord != null) {
				objectConcept.setIndexWordLemma(indexWord.getLemma());
			}
		}
		boolean hasRel = false;
		if (objectEntity.hasRelationsOfType(EntityConceptRelation.class)) {
			List<Relation> relations = objectEntity.getRelationsOfType(EntityConceptRelation.class);
			for (Relation relation : relations) {
				EntityConceptRelation ecRel = (EntityConceptRelation) relation;
				if (ecRel.getEnd().equals(objectConcept) && ecRel.getStart().equals(objectEntity)) {
					if (ecRel.getConfidence() != similarity) {
						objectEntity.getRelations().remove(relation);
						objectConcept.getRelations().remove(relation);
						EntityConceptRelation newRel = new EntityConceptRelation(objectEntity, objectConcept, similarity);
						objectEntity.addRelation(newRel);
						objectConcept.addRelation(newRel);
						hasRel = true;
					} else {
						hasRel = true;
					}

				}
			}
		}
		if (!hasRel) {
			EntityConceptRelation newRel = new EntityConceptRelation(objectEntity, objectConcept, similarity);
			objectEntity.addRelation(newRel);
			objectConcept.addRelation(newRel);
		}

		return objectConcept;
	}

	/**
	 * Creates or returns the {@link EntityConcept} to the specified
	 * {@link IObject}
	 * 
	 * @param object
	 * @return
	 */
	private ObjectConcept extractObjectConcept(IObject object) {
		ObjectConcept objectConcept;
		if (context.hasConceptOfIndividual(object.getFullName())
				&& context.getConceptOfIndividual(object.getFullName()) instanceof ObjectConcept) {
			objectConcept = (ObjectConcept) context.getConceptOfIndividual(object.getFullName());
		} else {
			objectConcept = new ObjectConcept(object.getName());
			objectConcept.setOntologyIndividual(object.getFullName());
			IndexWord indexWord = WordNetUtils.getIndexWord(object.getShortName(), POS.NOUN, dictionary);
			if (indexWord != null) {
				objectConcept.setIndexWordLemma(indexWord.getLemma());
			} else {
				indexWord = WordNetUtils.getIndexWord(object.getName().replace(".", " "), POS.NOUN, dictionary);
				if (indexWord != null) {
					objectConcept.setIndexWordLemma(indexWord.getLemma());
				} else {
					String[] split = object.getShortName().split("(?<=.)(?=(\\p{Upper}))");
					String compare = "";
					for (String string : split) {
						compare += string + " ";
					}
					compare.trim();
					indexWord = WordNetUtils.getIndexWord(compare, POS.NOUN, dictionary);
					if (indexWord != null) {
						if (indexWord.getLemma().contains(split[split.length - 1].toLowerCase())) {
							objectConcept.setIndexWordLemma(indexWord.getLemma());
						} else {
							indexWord = WordNetUtils.getIndexWord(split[split.length - 1], POS.NOUN, dictionary);
							if (indexWord != null) {
								objectConcept.setIndexWordLemma(indexWord.getLemma());
							}
						}

					}
				}
			}
			context.addConcept(objectConcept);

			if (indexWord != null) {
				List<String> synonyms = WordNetUtils.getSynonyms(indexWord);
				for (String synonym : synonyms) {
					List<Pair<IIndividual, Double>> synonymCandidates = getDirectCandidates(synonym.toLowerCase(),
							domain.getObjects().asSet());
					EntityConcept candidate = getObjectConcept(0, synonymCandidates);
					if (candidate != null && !candidate.equals(objectConcept)) {
						objectConcept.addEqualConcept(candidate);
						candidate.addEqualConcept(objectConcept);
					} else {
						objectConcept.addSynonym(synonym);
					}
				}

			}

			for (IState state : object.getStates()) {

				objectConcept.addState(extractStateConcept(state));
			}
			if (object.hasSubObjects()) {
				for (IObject subObject : object.getSubObjects()) {
					EntityConcept subObjectConcept;
					if (context.hasConceptOfIndividual(subObject.getFullName())) {
						subObjectConcept = (EntityConcept) context.getConceptOfIndividual(subObject.getFullName());
					} else {
						subObjectConcept = extractObjectConcept(subObject);
					}
					objectConcept.addPartConcept(subObjectConcept);
					subObjectConcept.addPartOfConcept(objectConcept);
				}
			}
			if (object.hasSuperObject()) {
				IObject superObject = object.getSuperObject();
				EntityConcept superObjectConcept;
				if (context.hasConceptOfIndividual(superObject.getFullName())) {
					superObjectConcept = (EntityConcept) context.getConceptOfIndividual(superObject.getFullName());
				} else {
					superObjectConcept = extractObjectConcept(superObject);
				}
				objectConcept.addPartOfConcept(superObjectConcept);
				superObjectConcept.addPartConcept(objectConcept);
			}
			if (object.hasSameIndividuals()) {
				for (IIndividual sameIndividual : object.getSameIndividuals()) {
					AbstractConcept sameConcept = null;
					if (context.hasConceptOfIndividual(sameIndividual.getFullName())) {
						sameConcept = context.getConceptOfIndividual(sameIndividual.getFullName());
					} else {
						if (sameIndividual instanceof IObject) {
							sameConcept = extractObjectConcept((IObject) sameIndividual);
						}
					}
					if (sameConcept != null) {
						objectConcept.addEqualConcept(sameConcept);
						sameConcept.addEqualConcept(objectConcept);
					}
				}
			}

		}
		return objectConcept;
	}

	/**
	 * Builds an {@link EntityConcept} from the Information extracted from
	 * WordNet and saved in the {@link ObjectEntity}
	 * 
	 * @param objectEntity
	 * @return
	 */
	private EntityConcept buildObjectConceptFromWordNet(ObjectEntity objectEntity) {
		ObjectConcept objectConcept = null;
		double confidence = 0.1;
		IndexWord indexWord = WordNetUtils.getIndexWord(objectEntity.getName(), POS.NOUN, dictionary);
		String[] split = objectEntity.getName().split(" ");
		if (indexWord != null && !indexWord.getLemma().contains(split[split.length - 1].toLowerCase())) {
			indexWord = WordNetUtils.getIndexWord(split[split.length - 1], POS.NOUN, dictionary);
		}
		if (indexWord != null) {
			if (context.hasConcept(getComparableName(objectEntity))) {
				AbstractConcept concept = context.getConcept(getComparableName(objectEntity));

				if (concept instanceof ObjectConcept) {
					objectConcept = (ObjectConcept) concept;
					// only if is first run
					if (!context.isReadFromGraph()) {

						if (objectConcept.hasRelationsOfType(EntityConceptRelation.class)) {
							List<Relation> ecRel = objectConcept.getRelationsOfType(EntityConceptRelation.class);
							confidence = ((EntityConceptRelation) ecRel.get(0)).getConfidence();

							confidence *= 3;
							if (confidence > 1.0) {
								confidence = 1.0;
							}

							for (Relation relation : ecRel) {
								if (relation instanceof EntityConceptRelation) {
									((EntityConceptRelation) relation).setConfidence(confidence);
								}

							}
						}
					}
				} else {
					objectConcept = new ObjectConcept(getComparableName(objectEntity));
					context.addConcept(objectConcept);
				}
			} else {
				objectConcept = new ObjectConcept(getComparableName(objectEntity));
				context.addConcept(objectConcept);
			}
			if (!objectConcept.hasIndexWordLemma()) {
				if (indexWord != null) {
					objectConcept.setIndexWordLemma(indexWord.getLemma());
				}
			}
			for (String synonym : objectEntity.getSynonyms()) {
				AbstractConcept candidate = getWordNetCandidate(synonym,
						(int) objectEntity.getReference().get(0).getAttributeValue("position"), domain.getObjects().asSet());
				if (candidate != null && !candidate.equals(objectConcept)) {
					objectConcept.addEqualConcept(candidate);
					candidate.addEqualConcept(candidate);

				} else {
					objectConcept.addSynonym(synonym);
				}
			}

			for (String meronym : objectEntity.getMeronyms()) {
				AbstractConcept candidate = getWordNetCandidate(meronym,
						(int) objectEntity.getReference().get(0).getAttributeValue("position"), domain.getObjects().asSet());
				if (candidate != null && !candidate.equals(objectConcept)) {

					objectConcept.addPartConcept(candidate);
					candidate.addPartOfConcept(objectConcept);

				}
			}
			for (String holonym : objectEntity.getHolonyms()) {
				AbstractConcept candidate = getWordNetCandidate(holonym,
						(int) objectEntity.getReference().get(0).getAttributeValue("position"), domain.getObjects().asSet());
				if (candidate != null && !candidate.equals(objectConcept)) {
					objectConcept.addPartOfConcept(candidate);
					candidate.addPartConcept(objectConcept);

				}
			}
			for (String hypernym : objectEntity.getDirectHypernyms()) {
				AbstractConcept candidate = getWordNetCandidate(hypernym,
						(int) objectEntity.getReference().get(0).getAttributeValue("position"), domain.getObjects().asSet());
				if (candidate != null && !candidate.equals(objectConcept)) {
					objectConcept.addSuperConcept(candidate);
					candidate.addSubConcept(objectConcept);
				}

			}
			for (String hyponym : objectEntity.getDirectHyponyms()) {
				AbstractConcept candidate = getWordNetCandidate(hyponym,
						(int) objectEntity.getReference().get(0).getAttributeValue("position"), domain.getObjects().asSet());
				if (candidate != null && !candidate.equals(objectConcept)) {
					objectConcept.addSubConcept(candidate);
					candidate.addSuperConcept(objectConcept);
				}
			}
			boolean hasRel = false;
			if (objectEntity.hasRelationsOfType(EntityConceptRelation.class)) {
				List<Relation> relations = objectEntity.getRelationsOfType(EntityConceptRelation.class);
				for (Relation relation : relations) {
					EntityConceptRelation ecRel = (EntityConceptRelation) relation;
					if (ecRel.getEnd().equals(objectConcept) && ecRel.getStart().equals(objectEntity)) {
						if (ecRel.getConfidence() < confidence) {
							objectEntity.getRelations().remove(relation);
							objectConcept.getRelations().remove(relation);
							EntityConceptRelation newRel = new EntityConceptRelation(objectEntity, objectConcept, confidence);
							objectEntity.addRelation(newRel);
							objectConcept.addRelation(newRel);
							hasRel = true;
						} else {
							hasRel = true;
						}

					}
				}
			}
			if (!hasRel) {
				EntityConceptRelation newRel = new EntityConceptRelation(objectEntity, objectConcept, confidence);
				objectEntity.addRelation(newRel);
				objectConcept.addRelation(newRel);
			}
		}
		return objectConcept;
	}

	/**
	 * Analyzes the given List for the most likely {@link IObject} and extracts
	 * the {@link EntityConcept} belonging to this {@link IObject}
	 * 
	 * @param entity
	 * @param candidates
	 * @return
	 */
	private EntityConcept getObjectConcept(int position, List<Pair<IIndividual, Double>> candidates) {
		if (candidates.size() == 1) {
			return extractObjectConcept((IObject) candidates.get(0).getLeft());
		} else if (candidates.size() > 1) {
			List<Pair<IIndividual, Double>> likelyCandidates = getMostLikelyCandidate(candidates);
			if (likelyCandidates.size() == 1) {
				return extractObjectConcept((IObject) likelyCandidates.get(0).getLeft());
			} else {
				Set<AbstractConcept> superConcepts = getAlreadyConnectedSuperConcepts(likelyCandidates);
				Pair<IIndividual, Double> candidate = getCandidateFromSuperConcepts(position, likelyCandidates, superConcepts);
				if (candidate != null) {
					return extractObjectConcept((IObject) candidate.getLeft());
				}

			}

		} else if (candidates.isEmpty()) {

		}
		return null;
	}

	/**
	 * Returns a String representation of the specified {@link Entity} which can
	 * be compared with the domain knowledge
	 * 
	 * @param objectEntity
	 * @return
	 */
	private String getComparableName(Entity objectEntity) {
		String comparableName = objectEntity.getName();
		comparableName = comparableName.replaceAll(" ", "").toLowerCase();
		return comparableName;
	}

	/**
	 * Returns a String representation of the specified {@link Entity} which
	 * includes the adjectives
	 * 
	 * @param objectEntity
	 * @return
	 */
	private String getAdjectiveName(ObjectEntity objectEntity) {
		String comparableName = objectEntity.getName();
		if (!objectEntity.getDescribingAdjectives().isEmpty()) {
			String name = "";
			for (String adjective : objectEntity.getDescribingAdjectives()) {
				name += adjective;
			}
			name += comparableName.replaceAll(" ", "");
			comparableName = name;
		}
		comparableName = comparableName.replaceAll(" ", "").toLowerCase();
		return comparableName;
	}

	/**
	 * Creates or returns the {@link State} to the specified {@link IState}
	 * 
	 * @param state
	 * @return
	 */
	private State extractStateConcept(IState state) {
		State stateConcept;
		if (context.hasConceptOfIndividual(state.getFullName())) {
			stateConcept = (State) context.getConceptOfIndividual(state.getFullName());
		} else {
			stateConcept = new State(state.getShortName());
			stateConcept.setOntologyIndividual(state.getFullName());
			context.addConcept(stateConcept);
			for (IState domainState : state.getAssociatedStates()) {
				stateConcept.addAssociatedState(extractStateConcept(domainState));
			}
			if (state.hasSameIndividuals()) {
				for (IIndividual sameIndividual : state.getSameIndividuals()) {
					AbstractConcept sameConcept = null;
					if (context.hasConceptOfIndividual(sameIndividual.getFullName())) {
						sameConcept = context.getConceptOfIndividual(sameIndividual.getFullName());
					} else {
						if (sameIndividual instanceof IState) {
							sameConcept = extractStateConcept((IState) sameIndividual);
						}
					}
					if (sameConcept != null) {
						stateConcept.addEqualConcept(sameConcept);
						sameConcept.addEqualConcept(stateConcept);
					}
				}
			}
		}
		return stateConcept;
	}

	/**
	 * Extracts an {@link EntityConcept} from the specified
	 * {@link SubjectEntity} {@link ISystemClass} connection
	 * 
	 * @param subject
	 * @param system
	 * @param similarity
	 */
	private void extractSystemConcept(SubjectEntity subject, ISystemClass system, Double similarity) {
		SubjectConcept systemConcept;
		if (context.hasConceptOfIndividual(system.getFullName())
				&& context.getConceptOfIndividual(system.getFullName()) instanceof SubjectConcept) {
			systemConcept = (SubjectConcept) context.getConceptOfIndividual(system.getFullName());
			if (systemConcept.getGender().equals(Gender.UNKNOWN)) {
				systemConcept.setGender(subject.getGender());
			}
		} else {
			systemConcept = new SubjectConcept(system.getShortName());
			systemConcept.setGender(subject.getGender());
			systemConcept.setOntologyIndividual(system.getFullName());
			EntityConceptRelation relation = new EntityConceptRelation(subject, systemConcept, similarity);
			subject.addRelation(relation);
			systemConcept.addRelation(relation);
			context.addConcept(systemConcept);

		}
	}

	/**
	 * Returns the found {@link AbstractConcept} to the specified WordNet word
	 * with respect to the specified domain knowledge individuals
	 * 
	 * @param wordNetWord
	 * @param individuals
	 * @return
	 */
	private AbstractConcept getWordNetCandidate(String wordNetWord, int position, Set<? extends IIndividual> individuals) {
		List<Pair<IIndividual, Double>> directCandidates = getDirectCandidates(wordNetWord.toLowerCase().replace(" ", ""), individuals);
		AbstractConcept candidate;
		if (individuals.iterator().next() instanceof IObject) {
			candidate = getObjectConcept(position, directCandidates);
		} else {
			candidate = getMethodConcept(position, directCandidates);
		}
		if (candidate != null) {
			return candidate;
		} else if (context.hasConcept(wordNetWord.toLowerCase().replaceAll(" ", ""))) {
			AbstractConcept concept = context.getConcept(wordNetWord.toLowerCase().replaceAll(" ", ""));
			if (!individuals.isEmpty()) {
				Object ind = individuals.toArray()[0];
				if (ind.getClass().getCanonicalName().equals(concept.getClass().getCanonicalName())) {
					return concept;
				}
			}
		}
		return null;

	}

	private List<Pair<IIndividual, Double>> getDirectCandidates(String name, Set<? extends IIndividual> individuals) {
		List<Pair<IIndividual, Double>> result = new ArrayList<>();
		for (IIndividual individual : individuals) {
			Double similarity;
			Double similarityShortName = jaroWinkler.similarity(individual.getShortName().toLowerCase(), name.toLowerCase());
			Double similarityFullName = jaroWinkler.similarity(individual.getName().toLowerCase(), name.toLowerCase());
			if (similarityShortName > similarityFullName) {
				similarity = similarityShortName;
			} else {
				similarity = similarityFullName;
			}
			if (similarity > jwSimilarityThreshold) {
				result.add(new ImmutablePair<IIndividual, Double>(individual, similarity));
			}
		}
		return result;
	}

	private List<Pair<IIndividual, Double>> getSynonymCandidates(Collection<String> synonyms, Set<? extends IIndividual> individuals) {
		List<Pair<IIndividual, Double>> result = new ArrayList<>();
		for (IIndividual individual : individuals) {
			for (String synonym : synonyms) {
				Double synSim = jaroWinkler.similarity(individual.getShortName().toLowerCase(), synonym.replaceAll(" ", "").toLowerCase());
				if (synSim > jwSimilarityThreshold) {
					result.add(new ImmutablePair<IIndividual, Double>(individual, synSim));
				}
			}
		}
		return result;
	}

	/**
	 * Returns the most likely candidate of the specified candidates
	 * 
	 * @param candidates
	 * @return
	 */
	private List<Pair<IIndividual, Double>> getMostLikelyCandidate(List<Pair<IIndividual, Double>> candidates) {
		Double sim = 0.0;
		for (Pair<IIndividual, Double> pair : candidates) {
			if (pair.getRight() > sim) {

				sim = pair.getRight();
			}
		}
		List<Pair<IIndividual, Double>> resultList = new ArrayList<>();
		for (Pair<IIndividual, Double> pair : candidates) {
			if (Math.abs(sim - pair.getRight()) <= 0.000001) {
				resultList.add(pair);
			}
		}
		return resultList;
	}

	private void findCommonSubsumers(ObjectConcept concept) {
		Set<ObjectConcept> objectConcepts = new HashSet<>();
		for (AbstractConcept abstractConcept : context.getConcepts()) {
			if (abstractConcept instanceof ObjectConcept && !abstractConcept.equals(concept)) {
				objectConcepts.add((ObjectConcept) abstractConcept);
			}
		}
		if (concept.hasIndexWordLemma()) {
			IndexWord current = WordNetUtils.getIndexWord(concept.getIndexWordLemma(), POS.NOUN, dictionary);

			for (ObjectConcept objectConcept : objectConcepts) {
				if (objectConcept.hasIndexWordLemma()) {
					Set<String> wnSynsets = getObjectConceptWNSynsets(concept);
					Set<String> wnSynsetsCandidate = getObjectConceptWNSynsets(objectConcept);
					IndexWord candidate = WordNetUtils.getIndexWord(objectConcept.getIndexWordLemma(), POS.NOUN, dictionary);
					LeastCommonSubsumer result = WordNetUtils.getLeastCommonSubsumer(current, candidate, wnSynsets, wnSynsetsCandidate,
							dictionary);
					if (result != null) {
						Double linSim = WordNetUtils.calculateLinSimilarity(result.getSynsetOne(), result.getSynsetTwo(),
								result.getLeastCommonSubsumer());
						if (linSim > LIN_SIMILARITY_THRESHOLD || result.getLeastCommonSubsumer().equals(result.getSynsetOne())
								|| result.getLeastCommonSubsumer().equals(result.getSynsetTwo())) {
							ObjectConcept newSubsumer = null;
							if (!context.hasConcept(result.getName()) || !(context.getConcept(result.getName()) instanceof ObjectConcept)) {
								if (!(result.getLeastCommonSubsumer().equals(result.getSynsetOne())
										&& result.getLeastCommonSubsumer().equals(result.getSynsetTwo()))) {
									newSubsumer = new ObjectConcept(result.getName());
									newSubsumer.setIndexWordLemma(result.getName());
									for (String synonym : WordNetUtils.getSynonyms(result.getName(), POS.NOUN, dictionary)) {
										newSubsumer.addSynonym(synonym);
									}
								}
							} else {
								AbstractConcept existingConcept = context.getConcept(result.getName());
								if (existingConcept instanceof ObjectConcept) {
									ObjectConcept subsumerConcept = (ObjectConcept) existingConcept;
									if (!ContextUtils.isSubsumed(concept, subsumerConcept)) {
										concept.addSuperConcept(subsumerConcept);
										subsumerConcept.addSubConcept(concept);
										removeRedundantEdges(concept, subsumerConcept);
									} else if (!concept.getSuperConcepts().contains(subsumerConcept)) {
										subsumerConcept.getSubConcepts().remove(concept);
									}
									if (!ContextUtils.isSubsumed(objectConcept, subsumerConcept)) {
										objectConcept.addSuperConcept(subsumerConcept);
										subsumerConcept.addSubConcept(objectConcept);
										removeRedundantEdges(objectConcept, subsumerConcept);
									} else if (!objectConcept.getSuperConcepts().contains(subsumerConcept)) {
										subsumerConcept.getSubConcepts().remove(objectConcept);
									}
								}
							}
							if (newSubsumer != null) {
								List<String> synonyms = WordNetUtils.getSynonyms(newSubsumer.getName(), POS.NOUN, dictionary);
								List<Pair<IIndividual, Double>> directCandidates = getDirectCandidates(newSubsumer.getName(),
										domain.getObjects().asSet());
								if (directCandidates.size() == 1) {
									newSubsumer = extractObjectConcept((IObject) directCandidates.get(0).getLeft());

								} else if (directCandidates.size() > 1) {
									context.addConcept(newSubsumer);
								} else if (directCandidates.isEmpty()) {
									List<Pair<IIndividual, Double>> synonymCandidates = getSynonymCandidates(synonyms,
											domain.getObjects().asSet());
									if (synonymCandidates.size() == 1) {
										newSubsumer = extractObjectConcept((IObject) synonymCandidates.get(0).getLeft());
									} else {
										context.addConcept(newSubsumer);

									}
								}
								if (!newSubsumer.hasIndexWordLemma()) {
									newSubsumer.setIndexWordLemma(result.getName());
								}
								concept.addSuperConcept(newSubsumer);
								objectConcept.addSuperConcept(newSubsumer);
								newSubsumer.addSubConcept(concept);
								newSubsumer.addSubConcept(objectConcept);
							}
						}
					}
				}
			}

		}

	}

	private Set<String> getObjectConceptWNSynsets(ObjectConcept concept) {
		Set<String> result = new HashSet<>();
		List<Relation> relations = concept.getRelationsOfType(EntityConceptRelation.class);
		for (Relation relation : relations) {
			EntityConceptRelation rel = (EntityConceptRelation) relation;
			if (rel.getStart() instanceof ObjectEntity) {
				ObjectEntity entity = (ObjectEntity) rel.getStart();
				if (entity.getWNSense() != null) {
					result.add(entity.getWNSense().getLeft());
				}
			}
		}
		return result;
	}

	private void removeRedundantEdges(AbstractConcept concept, AbstractConcept subsumerConcept) {
		if (concept.hasSubConcepts()) {
			for (AbstractConcept sub : concept.getSubConcepts()) {
				if (sub.getSuperConcepts().contains(subsumerConcept)) {
					sub.getSuperConcepts().remove(subsumerConcept);
					subsumerConcept.getSubConcepts().remove(sub);
				} else {
					removeRedundantEdges(sub, subsumerConcept);
				}
			}
		}
	}

}
