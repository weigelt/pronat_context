/**
 * 
 */
package edu.kit.ipd.pronat.context.data.relations;

import java.util.HashMap;

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
public class EntityEntityRelation extends Relation {

	protected static final String TYPE = "entityEntityRelation";

	private Entity start;

	private Entity end;

	/**
	 * @param role
	 */
	public EntityEntityRelation(String role, Entity start, Entity end) {
		super(role);
		setStart(start);
		setEnd(end);
	}

	/**
	 * @return the start
	 */
	public Entity getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(Entity start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public Entity getEnd() {
		return end;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(Entity end) {
		this.end = end;
	}

	@Override
	public IArc printToGraph(IGraph graph, HashMap<Long, INode> graphNodes) {
		IArcType arcType;
		if (graph.hasArcType(RELATION_ARC_TYPE)) {
			arcType = graph.getArcType(RELATION_ARC_TYPE);
		} else {
			arcType = createRelationArcType(graph);
		}
		IArc arc = graph.createArc(graphNodes.get(getStart().getID()), graphNodes.get(getEnd().getID()), arcType);
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
	public boolean isRepresentedByArc(IArc arc, HashMap<Long, INode> graphNodes) {
		boolean result = super.isRepresentedByArc(arc, graphNodes);
		result = result && arc.getAttributeValue(RELATION_TYPE).equals(getCompareType());
		if (result) {
			result = result && arc.getSourceNode().equals(graphNodes.get(getStart().getID()))
					&& arc.getTargetNode().equals(graphNodes.get(getEnd().getID()));
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityEntityRelation) {
			EntityEntityRelation other = (EntityEntityRelation) obj;
			return start.equalsWithoutRelation((other.getStart())) && end.equalsWithoutRelation((other.getEnd())) && super.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = this.start == null ? hash : 31 * hash + this.start.hashCode();
		hash = this.end == null ? hash : 31 * hash + this.end.hashCode();
		return hash;
	}

	public static Relation readFromArc(IArc arc, ContextIndividual[] graphMap, IGraph graph) {
		Relation relation;
		String name = (String) arc.getAttributeValue(RELATION_NAME);
		Entity start;
		Entity end;
		if (graphMap[graph.getNodes().indexOf(arc.getSourceNode())] instanceof Entity) {
			start = (Entity) graphMap[graph.getNodes().indexOf(arc.getSourceNode())];
		} else {
			throw new IllegalArgumentException("the mapping between node and contextIndividual is defect");
		}
		if (graphMap[graph.getNodes().indexOf(arc.getTargetNode())] instanceof Entity) {
			end = (Entity) graphMap[graph.getNodes().indexOf(arc.getTargetNode())];
		} else {
			throw new IllegalArgumentException("the mapping between node and contextIndividual is defect");
		}
		relation = new EntityEntityRelation(name, start, end);
		start.addRelationWithoutSettingChanged(relation);
		end.addRelationWithoutSettingChanged(relation);
		if (arc.getAttributeValue(CONFIDENCE_VERIFIED) != null) {
			relation.setConfidenceVerified((boolean) arc.getAttributeValue(CONFIDENCE_VERIFIED));
		}
		return relation;
	}

	@Override
	public String toString() {
		return "[" + this.getStart().getName() + " --" + this.getName() + "-> " + this.getEnd().getName() + "]";
	}

	@Override
	protected String getCompareType() {
		return TYPE;
	}

}
