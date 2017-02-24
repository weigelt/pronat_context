/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public abstract class ContextIndividual {

	protected boolean changed = false;
	private Set<Relation> relations;

	public ContextIndividual() {
		relations = new HashSet<Relation>();
	}

	public abstract INode printToGraph(IGraph graph);

	public Set<Relation> updateNode(INode node, IGraph graph, HashMap<ContextIndividual, INode> graphNodes) {
		Set<Relation> alreadyUpdated = new HashSet<>();
		Set<IArc> arcs = new HashSet<>();
		for (Relation relation : relations) {
			boolean found = false;
			for (IArc arc : node.getOutgoingArcs()) {
				if (arc.getType().getName().equals("contextRelation")) {
					if (relation.isRepresentedByArc(arc, graphNodes)) {
						relation.updateArc(arc);
						found = true;
						alreadyUpdated.add(relation);
						break;
					}
					arcs.add(arc);
				}
			}
			if (!found) {
				for (IArc arc : node.getIncomingArcs()) {
					if (arc.getType().getName().equals("contextRelation")) {
						if (relation.isRepresentedByArc(arc, graphNodes)) {
							relation.updateArc(arc);
							alreadyUpdated.add(relation);
							found = true;
							break;
						}
						arcs.add(arc);
					}
				}

			}
		}
		for (IArc arc : arcs) {
			boolean isRepresented = false;
			for (Relation relation : relations) {
				if (relation.isRepresentedByArc(arc, graphNodes)) {
					isRepresented = true;
					break;
				}
			}
			if (!isRepresented) {
				graph.deleteArc(arc);
			}
		}
		return alreadyUpdated;

	}

	public boolean hasRelationsOfType(Class<? extends Relation> type) {
		for (Relation relation : relations) {
			if (relation.getClass().equals(type)) {
				return true;
			}
		}
		return false;
	}

	public List<Relation> getRelationsOfType(Class<? extends Relation> type) {
		List<Relation> result = new ArrayList<>();
		for (Relation relation : relations) {
			if (relation.getClass().equals(type)) {
				result.add(relation);
			}
		}
		return result;
	}

	public boolean hasChanged() {
		return changed;
	}

	/**
	 * @return the relations
	 */
	public Set<Relation> getRelations() {
		return relations;
	}

	/**
	 * @param relations
	 *            the relations to set
	 */
	public boolean addRelation(Relation relation) {
		boolean hasChanged = this.relations.add(relation);
		changed = changed || hasChanged;
		return hasChanged;
	}

}
