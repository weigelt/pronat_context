/**
 * 
 */
package edu.kit.ipd.pronat.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import edu.kit.ipd.pronat.context.data.Context;
import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.data.entities.PositionType;
import edu.kit.ipd.pronat.context.data.entities.SpeakerEntity;
import edu.kit.ipd.pronat.context.data.relations.ActionEntityRelation;
import edu.kit.ipd.pronat.context.data.relations.LocativeRelation;
import edu.kit.ipd.pronat.context.data.relations.Relation;
import edu.kit.ipd.pronat.context.util.ContextUtils;
import edu.kit.ipd.pronat.context.util.GraphUtils;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * This {@link IContextAnalyzer} analyzes the constructed Context {@link Entity}
 * s for locative Relations between them. Adds {@link LocativeRelation}.
 *
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class LocativeRelationAnalyzer implements IContextAnalyzer {

	private static final String[][] NEXT_TO = { { "next", "to" }, { "beside" } };
	private static final String[][] FRONT_OF = { { "in", "front", "of" } };
	private static final String[][] BETWEEN = { { "between" } };
	private static final String[][] BEHIND = { { "behind" } };
	private static final String[][] OPPOSITE = { { "opposite", "to" } };
	private static final String[][] LEFT = { { "left" }, { "left", "of" }, { "on", "the", "left", "side", "of" } };
	private static final String[][] RIGHT = { { "right" }, { "right", "of" }, { "on", "the", "right", "side", "of" } };
	private static final String[][] ON = { { "on" }, { "above" }, { "on", "top", "of" }, { "onto" } };
	private static final String[][] UNDER = { { "under" }, { "below" }, { "underneath" }, { "beneath" } };
	private static final String[][] IN = { { "in" }, { "inside" }, { "within" }, { "into" } };
	private static final String[][] FROM = { { "from" }, { "out", "of" } };

	private IGraph graph;
	private Context currentContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.kit.ipd.parse.contextanalyzer.IContextAnalyzer#analyze(edu.kit.ipd.
	 * parse.luna.graph.IGraph, edu.kit.ipd.parse.contextanalyzer.data.Context)
	 */
	@Override
	public void analyze(IGraph graph, Context context) throws MissingDataException {
		this.graph = graph;
		currentContext = context;
		List<Entity> entities = new ArrayList<Entity>(context.getEntities());
		Collections.sort(entities);
		searchForLocativeRelations(entities, GraphUtils.getNodesOfUtterance(graph));
	}

	/**
	 * Searches for any {@link LocativeRelation} between two {@link Entity}s next to
	 * each other and adds the {@link Relation} to the {@link Entity}s
	 * 
	 * @param entities
	 * @param utteranceNodes
	 */
	private void searchForLocativeRelations(List<Entity> entities, List<INode> utteranceNodes) {
		ListIterator<Entity> iterator = entities.listIterator();

		if (iterator.hasNext()) {
			Entity previous = iterator.next();
			while (iterator.hasNext()) {
				Entity current = iterator.next();
				if (!isFrontOf(current)) {

					// remove virtual SpeakerEntity from consideration
					if (!(previous instanceof SpeakerEntity) && !(current instanceof SpeakerEntity)) {

						if (!(ContextUtils.belongToSameAction(previous, current) && !ContextUtils.hasLocativeRole(current))
								&& !ContextUtils.actionInBetween(previous, current, currentContext)) {
							checkForLocatives(utteranceNodes, previous, current);
						} else {
							if (ContextUtils.belongToSameAction(previous, current)) {
								if (ContextUtils.hasLocativeRole(current)) {
									if (ContextUtils.isProtoPatient(previous)) {
										checkForLocatives(utteranceNodes, previous, current);
									} else {
										Entity protoPatient = ContextUtils
												.getProtoPatientForAction(ContextUtils.getActionForEntity(current));
										if (protoPatient != null && ContextUtils.getPositionInUtterance(protoPatient) < ContextUtils
												.getPositionInUtterance(current)) {
											checkForLocatives(utteranceNodes, protoPatient, current);
										}
									}
								}
								if (ContextUtils.isProtoPatient(previous)) {
									if (ContextUtils.hasLocativeRole(current)) {//|| ContextUtils.isSourceOrDestination(current)) {

									}
								}

							}
						}
					}
					previous = current;
				} else {
					for (Relation rel : current.getRelations()) {
						if (rel instanceof ActionEntityRelation) {
							((ActionEntityRelation) rel).getAction().getRelations().remove(rel);
						}
					}
					currentContext.getEntities().remove(current);

				}

			}
		}

	}

	private boolean isFrontOf(Entity current) {
		if (!current.getName().equalsIgnoreCase("front")) {
			return false;
		} else {
			if (GraphUtils.getPreviousNode(current.getReference().get(0), graph) != null) {
				if (((String) GraphUtils.getPreviousNode(current.getReference().get(0), graph).getAttributeValue("value"))
						.equalsIgnoreCase("in")) {
					if (GraphUtils.getNextNode(current.getReference().get(current.getReference().size() - 1), graph) != null) {
						if (((String) GraphUtils.getNextNode(current.getReference().get(current.getReference().size() - 1), graph)
								.getAttributeValue("value")).equalsIgnoreCase("of")) {
							return true;

						}
					}
				}
			}
		}
		return false;
	}

	private void checkForLocatives(List<INode> utteranceNodes, Entity previous, Entity current) {
		INode startOfCurrent = current.getReference().get(0);

		INode endOfPrevious = previous.getReference().get(previous.getReference().size() - 1);

		List<INode> nodesInBetween = utteranceNodes.subList(utteranceNodes.indexOf(endOfPrevious) + 1,
				utteranceNodes.indexOf(startOfCurrent));
		PositionType location = containsLocative(nodesInBetween);
		if (location != null) {
			LocativeRelation loc = new LocativeRelation(location.toString(), location, previous, current);
			boolean alreadyExisting = false;
			if (current.hasRelationsOfType(LocativeRelation.class)) {
				List<Relation> relations = current.getRelationsOfType(LocativeRelation.class);
				for (Relation relation : relations) {
					LocativeRelation locRel = (LocativeRelation) relation;
					if (locRel.equals(loc)) {
						alreadyExisting = true;
					}
				}
			}
			if (!alreadyExisting) {

				previous.addRelation(loc);
				current.addRelation(loc);
			}

		}
	}

	private boolean containsStringSequence(List<String> text, String[] sequence) {
		if (text.containsAll(Arrays.asList(sequence))) {
			int index = text.indexOf(sequence[0]);
			for (int i = 1; i < sequence.length; i++) {
				if (text.indexOf(sequence[i]) != index + i) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns the {@link PositionType} of the {@link LocativeRelation} in the
	 * {@link INode}s specified or <code>null</code> if none is found
	 * 
	 * @param nodesInBetween
	 * @return
	 */
	private PositionType containsLocative(List<INode> nodesInBetween) {
		List<String> textSequence = new ArrayList<>();
		//TODO check multiple locatives
		for (INode iNode : nodesInBetween) {
			textSequence.add((String) iNode.getAttributeValue("value"));
		}
		// dont consider splitted formulations
		if (!containsConjunction(nodesInBetween)) {
			for (String[] list : NEXT_TO) {
				if (containsStringSequence(textSequence, list)) {
					return PositionType.NEXT_TO;
				}
			}
			for (String[] list : FROM) {
				if (containsStringSequence(textSequence, list)) {
					return PositionType.FROM;
				}
			}
			for (String[] list : FRONT_OF) {
				if (containsStringSequence(textSequence, list)) {
					return PositionType.FRONT_OF;
				}
			}
			for (String[] list : BETWEEN) {
				if (containsStringSequence(textSequence, list)) {
					return PositionType.BETWEEN;
				}
			}
			for (String[] list : BEHIND) {
				if (containsStringSequence(textSequence, list)) {
					return PositionType.BEHIND;
				}
			}
			for (String[] list : OPPOSITE) {
				if (containsStringSequence(textSequence, list)) {
					return PositionType.OPPOSITE;
				}
			}
			for (String[] list : LEFT) {
				if (containsStringSequence(textSequence, list)) {
					return PositionType.LEFT;
				}
			}
			for (String[] list : RIGHT) {
				if (containsStringSequence(textSequence, list)) {
					return PositionType.RIGHT;
				}
			}
			for (String[] list : ON) {
				if (containsStringSequence(textSequence, list)) {
					if (list.length == 1 && list[0].equals("on")) {
						int index = textSequence.indexOf("on");
						if (nodesInBetween.get(index).getAttributeValue("pos").equals("IN")) {
							if (nodesInBetween.size() > index + 1) {
								if (nodesInBetween.get(index + 1).getAttributeValue("pos").equals("DT")) {
									return PositionType.ON;
								}
							} else {
								return PositionType.ON;
							}
						}

					} else {
						return PositionType.ON;
					}

				}
			}
			for (String[] list : UNDER) {
				if (containsStringSequence(textSequence, list)) {
					return PositionType.UNDER;
				}
			}
			for (String[] list : IN) {
				if (containsStringSequence(textSequence, list)) {
					if (list.length == 1 && list[0].equals("in")) {
						int index = textSequence.indexOf("in");
						if (nodesInBetween.get(index).getAttributeValue("pos").equals("IN")) {
							if (nodesInBetween.size() > index + 1) {
								if (nodesInBetween.get(index + 1).getAttributeValue("pos").equals("DT")) {
									return PositionType.IN;
								}
							} else {
								return PositionType.IN;
							}

						}
					} else {
						return PositionType.IN;
					}
				}
			}
		}
		return null;
	}

	private boolean containsConjunction(List<INode> nodesInBetween) {
		for (INode iNode : nodesInBetween) {
			if (((String) iNode.getAttributeValue("pos")).equals("CC")) {
				return true;
			}
		}
		return false;
	}

}
