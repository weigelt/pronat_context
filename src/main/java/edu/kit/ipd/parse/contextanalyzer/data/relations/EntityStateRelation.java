/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.relations;

import java.util.HashMap;

import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.contextanalyzer.data.State;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public class EntityStateRelation extends EntityConceptRelation {

	protected static final String TYPE = "entityStateRelation";

	public EntityStateRelation(Entity start, State end, double confidence) {
		super(start, end, confidence, TYPE);
	}

	@Override
	public IArc printToGraph(IGraph graph, HashMap<ContextIndividual, INode> graphNodes) {
		IArcType arcType;
		if (graph.hasArcType(RELATION_ARC_TYPE)) {
			arcType = graph.getArcType(RELATION_ARC_TYPE);
		} else {
			arcType = createRelationArcType(graph);
		}
		IArc arc = graph.createArc(graphNodes.get(getStart()), graphNodes.get(getEnd()), arcType);
		arc.setAttributeValue(RELATION_NAME, getName());
		arc.setAttributeValue(RELATION_TYPE, TYPE);
		arc.setAttributeValue(CONFIDENCE, getConfidence());

		arc.setAttributeValue(CONFIDENCE_VERIFIED, confidenceIsVerified());
		return arc;
	}

	@Override
	public IArc updateArc(IArc arc) {
		super.updateArc(arc);
		arc.setAttributeValue(RELATION_TYPE, TYPE);
		arc.setAttributeValue(CONFIDENCE, getConfidence());

		arc.setAttributeValue(CONFIDENCE_VERIFIED, confidenceIsVerified());
		return arc;
	}

	@Override
	public boolean isRepresentedByArc(IArc arc, HashMap<ContextIndividual, INode> graphNodes) {
		boolean result = super.isRepresentedByArc(arc, graphNodes);
		result = result && arc.getAttributeValue(RELATION_TYPE).equals(getCompareType());
		if (result) {
			result = result && arc.getSourceNode().equals(graphNodes.get(getStart()))
					&& arc.getTargetNode().equals(graphNodes.get(getEnd()));
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityStateRelation) {
			EntityStateRelation other = (EntityStateRelation) obj;
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
		double confidence = (double) arc.getAttributeValue(CONFIDENCE);
		Entity start;
		State end;
		if (graphMap[graph.getNodes().indexOf(arc.getSourceNode())] instanceof Entity) {
			start = (Entity) graphMap[graph.getNodes().indexOf(arc.getSourceNode())];
		} else {
			throw new IllegalArgumentException("the mapping between node and contextIndividual is defect");
		}
		if (graphMap[graph.getNodes().indexOf(arc.getTargetNode())] instanceof State) {
			end = (State) graphMap[graph.getNodes().indexOf(arc.getTargetNode())];
		} else {
			throw new IllegalArgumentException("the mapping between node and contextIndividual is defect");
		}

		relation = new EntityStateRelation(start, end, confidence);
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
