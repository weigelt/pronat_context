/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.relations;

import java.util.HashMap;

import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
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
	public IArc printToGraph(IGraph graph, HashMap<ContextIndividual, INode> graphNodes) {
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
	public boolean isRepresentedByArc(IArc arc, HashMap<ContextIndividual, INode> graphNodes) {
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
		return super.hashCode() ^ Double.hashCode(confidence);
	}

	public static Relation readFromArc(IArc arc, HashMap<INode, ContextIndividual> graphMap, IGraph graph) {
		Relation relation;
		String name = (String) arc.getAttributeValue(RELATION_NAME);
		double confidence = (double) arc.getAttributeValue(CONFIDENCE);
		Entity start = (Entity) graphMap.get(arc.getSourceNode());
		Entity end = (Entity) graphMap.get(arc.getTargetNode());
		relation = new ReferentRelation(name, confidence, start, end);
		start.addRelation(relation);
		end.addRelation(relation);
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
