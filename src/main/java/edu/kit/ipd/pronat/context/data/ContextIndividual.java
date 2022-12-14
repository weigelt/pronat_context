/**
 * 
 */
package edu.kit.ipd.pronat.context.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.ipd.pronat.context.data.relations.Relation;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 *
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public abstract class ContextIndividual {

	protected boolean changed = false;
	private List<Relation> relations;

	private long ID = -1;

	public ContextIndividual() {
		relations = new ArrayList<Relation>();
		this.changed = false;
	}

	public abstract INode printToGraph(IGraph graph);

	public Set<Relation> updateNode(INode node, IGraph graph, HashMap<Long, INode> graphNodes) {
		Set<Relation> alreadyUpdated = new HashSet<>();
		Set<IArc> arcs = new HashSet<>();
		for (Relation relation : relations) {
			boolean found = false;
			//TODO check whether fields have changed in updateARC
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
	public List<Relation> getRelations() {
		return relations;
	}

	public boolean removeRelation(Relation relation) {
		boolean hasChanged = getRelations().remove(relation);
		changed = changed || hasChanged;
		return hasChanged;
	}

	/**
	 * @param relation
	 *            the relations to set
	 */
	public boolean addRelation(Relation relation) {
		if (!this.getRelations().contains(relation)) {
			changed = true;
			return this.relations.add(relation);

		}

		return false;
	}

	/**
	 * @param relation
	 *            the relations to set
	 */
	public boolean addRelationWithoutSettingChanged(Relation relation) {
		if (!this.getRelations().contains(relation)) {
			return this.relations.add(relation);

		}
		return false;
	}

	/**
	 * @return the id
	 */
	public long getID() {
		return ID;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setID(long id) {
		this.ID = id;
	}

}
