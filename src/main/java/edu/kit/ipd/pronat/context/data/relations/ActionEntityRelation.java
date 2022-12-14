/**
 * 
 */
package edu.kit.ipd.pronat.context.data.relations;

import java.util.HashMap;

import edu.kit.ipd.pronat.context.data.Action;
import edu.kit.ipd.pronat.context.data.ContextIndividual;
import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class ActionEntityRelation extends Relation {

	protected static final String TYPE = "actionEntityRelation";

	private Action action;

	private Entity entity;

	/**
	 * @param name
	 * @param action
	 * @param entity
	 */
	public ActionEntityRelation(String name, Action action, Entity entity) {
		super(name);
		setAction(action);
		setEntity(entity);
	}

	/**
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	/**
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * @param entity
	 *            the entity to set
	 */
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public boolean isRepresentedByArc(IArc arc, HashMap<Long, INode> graphNodes) {
		boolean result = super.isRepresentedByArc(arc, graphNodes);
		result = result && arc.getAttributeValue(RELATION_TYPE).equals(getCompareType());
		if (result) {
			result = result && arc.getSourceNode().equals(graphNodes.get(getAction().getID()))
					&& arc.getTargetNode().equals(graphNodes.get(getEntity().getID()));
		}
		return result;
	}

	@Override
	public IArc printToGraph(IGraph graph, HashMap<Long, INode> graphNodes) {
		IArcType arcType;
		if (graph.hasArcType(RELATION_ARC_TYPE)) {
			arcType = graph.getArcType(RELATION_ARC_TYPE);
		} else {
			arcType = createRelationArcType(graph);
		}
		IArc arc = graph.createArc(graphNodes.get(getAction().getID()), graphNodes.get(getEntity().getID()), arcType);
		arc.setAttributeValue(RELATION_NAME, getName());
		arc.setAttributeValue(RELATION_TYPE, TYPE);

		arc.setAttributeValue(CONFIDENCE_VERIFIED, confidenceIsVerified());
		return arc;

	}

	@Override
	public IArc updateArc(IArc arc) {
		arc.setAttributeValue(RELATION_NAME, getName());
		arc.setAttributeValue(RELATION_TYPE, TYPE);

		arc.setAttributeValue(CONFIDENCE_VERIFIED, confidenceIsVerified());
		return arc;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ActionEntityRelation) {
			ActionEntityRelation other = (ActionEntityRelation) obj;
			return action.equalsWithoutRelation((other.getAction())) && entity.equalsWithoutRelation(other.getEntity())
					&& super.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = this.action == null ? hash : 31 * hash + this.action.hashCode();
		hash = this.entity == null ? hash : 31 * hash + this.entity.hashCode();
		return hash;
	}

	public static Relation readFromArc(IArc arc, ContextIndividual[] graphMap, IGraph graph) {
		Relation relation;
		String name = (String) arc.getAttributeValue(RELATION_NAME);
		Action action;
		Entity entity;
		if (graphMap[graph.getNodes().indexOf(arc.getSourceNode())] instanceof Action) {
			action = (Action) graphMap[graph.getNodes().indexOf(arc.getSourceNode())];
		} else {
			throw new IllegalArgumentException("the mapping between node and contextIndividual is defect");
		}
		if (graphMap[graph.getNodes().indexOf(arc.getTargetNode())] instanceof Entity) {
			entity = (Entity) graphMap[graph.getNodes().indexOf(arc.getTargetNode())];
		} else {
			throw new IllegalArgumentException("the mapping between node and contextIndividual is defect");
		}
		relation = new ActionEntityRelation(name, action, entity);
		action.addRelationWithoutSettingChanged(relation);
		entity.addRelationWithoutSettingChanged(relation);
		if (arc.getAttributeValue(CONFIDENCE_VERIFIED) != null) {
			relation.setConfidenceVerified((boolean) arc.getAttributeValue(CONFIDENCE_VERIFIED));
		}
		return relation;
	}

	@Override
	public String toString() {
		return "[" + this.getAction().getName() + " --" + this.getName() + "-> " + this.getEntity().getName() + "]";
	}

	@Override
	protected String getCompareType() {
		return TYPE;
	}

}
