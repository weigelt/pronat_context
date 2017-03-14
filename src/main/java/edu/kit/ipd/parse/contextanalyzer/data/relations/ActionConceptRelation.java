/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.relations;

import java.util.HashMap;

import edu.kit.ipd.parse.contextanalyzer.data.AbstractConcept;
import edu.kit.ipd.parse.contextanalyzer.data.Action;
import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public class ActionConceptRelation extends Relation {

	protected static final String TYPE = "actionConceptRelation";

	private double confidence = -1.0;

	private Action start;

	private AbstractConcept end;

	public ActionConceptRelation(Action start, AbstractConcept end, double confidence) {
		super("actionConceptRelation");
		this.setStart(start);
		this.setEnd(end);
		this.setConfidence(confidence);
	}

	/**
	 * @return the start
	 */
	public Action getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(Action start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public AbstractConcept getEnd() {
		return end;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(AbstractConcept end) {
		this.end = end;
	}

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

		arc.setAttributeValue(VERIFIED_BY_DIALOG_AGENT, isVerifiedByDialogAgent());
		return arc;
	}

	@Override
	public IArc updateArc(IArc arc) {
		arc.setAttributeValue(RELATION_NAME, getName());
		arc.setAttributeValue(RELATION_TYPE, TYPE);
		arc.setAttributeValue(CONFIDENCE, getConfidence());

		arc.setAttributeValue(VERIFIED_BY_DIALOG_AGENT, isVerifiedByDialogAgent());
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
		if (obj instanceof ActionConceptRelation) {
			ActionConceptRelation other = (ActionConceptRelation) obj;
			return start.equalsWithoutRelation((other.getStart())) && end.equalsWithoutRelation((other.getEnd()))
					&& this.confidence == other.confidence && super.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ start.hashCode() ^ end.hashCode();
	}

	public static Relation readFromArc(IArc arc, HashMap<INode, ContextIndividual> graphMap, IGraph graph) {
		Relation relation;
		double confidence = (double) arc.getAttributeValue(CONFIDENCE);
		Action start = (Action) graphMap.get(arc.getSourceNode());
		AbstractConcept end = (AbstractConcept) graphMap.get(arc.getTargetNode());
		relation = new ActionConceptRelation(start, end, confidence);
		start.addRelation(relation);
		end.addRelation(relation);
		if (arc.getAttributeValue(VERIFIED_BY_DIALOG_AGENT) != null) {
			relation.setVerifiedByDialogAgent((boolean) arc.getAttributeValue(VERIFIED_BY_DIALOG_AGENT));
		}
		return relation;
	}

	@Override
	public String toString() {
		return "[" + this.getStart().getName() + " --" + this.getName() + ":" + this.getConfidence() + "-> " + this.getEnd().getName()
				+ "]";
	}

	@Override
	protected String getCompareType() {
		return TYPE;
	}

}
