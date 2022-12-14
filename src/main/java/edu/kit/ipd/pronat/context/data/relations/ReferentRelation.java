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
public class ReferentRelation extends EntityEntityRelation {

	public static final String TYPE = "referentRelation";

	private double confidence = -1.0;

	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return confidence;
	}

	/**
	 * @param confidence
	 *            the confidence to set
	 */
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	/**
	 * @param role
	 * @param start
	 * @param end
	 */
	public ReferentRelation(String role, double confidence, Entity start, Entity end) {
		super(role, start, end);
		this.confidence = confidence;
	}

	@Override
	public IArc printToGraph(IGraph graph, HashMap<Long, INode> graphNodes) {
		IArc arc = super.printToGraph(graph, graphNodes);
		arc.setAttributeValue(RELATION_TYPE, TYPE);
		arc.setAttributeValue(CONFIDENCE, this.confidence);
		return arc;
	}

	@Override
	public IArc updateArc(IArc arc) {
		super.updateArc(arc);
		arc.setAttributeValue(RELATION_TYPE, TYPE);
		arc.setAttributeValue(CONFIDENCE, this.confidence);
		return arc;
	}

	@Override
	public boolean isRepresentedByArc(IArc arc, HashMap<Long, INode> graphNodes) {
		boolean result = super.isRepresentedByArc(arc, graphNodes);
		result = result && arc.getAttributeValue(RELATION_TYPE).equals(getCompareType());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReferentRelation) {
			ReferentRelation other = (ReferentRelation) obj;
			return super.equals(other) && this.confidence == other.confidence;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = 31 * hash + Double.hashCode(confidence);
		return hash;
	}

	public static Relation readFromArc(IArc arc, ContextIndividual[] graphMap, IGraph graph) {
		Relation relation;
		String name = (String) arc.getAttributeValue(RELATION_NAME);
		double confidence = (double) arc.getAttributeValue(CONFIDENCE);
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
		relation = new ReferentRelation(name, confidence, start, end);
		start.addRelationWithoutSettingChanged(relation);
		end.addRelationWithoutSettingChanged(relation);
		if (arc.getAttributeValue(CONFIDENCE_VERIFIED) != null) {
			relation.setConfidenceVerified((boolean) arc.getAttributeValue(CONFIDENCE_VERIFIED));
		}
		return relation;
	}

	@Override
	public String toString() {
		return "[" + this.getStart().getName() + "[" + this.getStart().getReference().get(0).getAttributeValue("position") + "]" + " --"
				+ this.getName() + ":" + this.confidence + "-> " + this.getEnd().getName() + "["
				+ this.getEnd().getReference().get(0).getAttributeValue("position") + "]" + "]";
	}

	@Override
	protected String getCompareType() {
		return TYPE;
	}

}
