/**
 * 
 */
package edu.kit.ipd.pronat.context.data.relations;

import java.util.HashMap;

import edu.kit.ipd.pronat.context.data.ContextIndividual;
import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class ConjunctionRelation extends EntityEntityRelation {

	protected static final String TYPE = "conjunctionRelation";

	public ConjunctionRelation(String role, Entity start, Entity end) {
		super(role, start, end);
	}

	@Override
	public IArc printToGraph(IGraph graph, HashMap<Long, INode> graphNodes) {
		IArc arc = super.printToGraph(graph, graphNodes);
		arc.setAttributeValue(RELATION_TYPE, TYPE);
		return arc;
	}

	@Override
	public IArc updateArc(IArc arc) {
		super.updateArc(arc);
		arc.setAttributeValue(RELATION_TYPE, TYPE);
		return arc;
	}

	@Override
	public boolean isRepresentedByArc(IArc arc, HashMap<Long, INode> graphNodes) {
		boolean result = super.isRepresentedByArc(arc, graphNodes);
		if (result) {
			result = result && arc.getAttributeValue(RELATION_TYPE).equals(getCompareType());
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConjunctionRelation) {
			ConjunctionRelation other = (ConjunctionRelation) obj;
			return super.equals(other);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
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

		relation = new ConjunctionRelation(name, start, end);
		start.addRelationWithoutSettingChanged(relation);
		end.addRelationWithoutSettingChanged(relation);
		if (arc.getAttributeValue(CONFIDENCE_VERIFIED) != null) {
			relation.setConfidenceVerified((boolean) arc.getAttributeValue(CONFIDENCE_VERIFIED));
		}
		return relation;
	}

	@Override
	protected String getCompareType() {
		return TYPE;
	}

}
