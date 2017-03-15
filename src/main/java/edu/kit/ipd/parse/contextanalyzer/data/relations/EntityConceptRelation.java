/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.relations;

import java.util.HashMap;

import edu.kit.ipd.parse.contextanalyzer.data.AbstractConcept;
import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public class EntityConceptRelation extends Relation {

	protected static final String TYPE = "entityConceptRelation";

	private double confidence = -1.0;

	private Entity start;

	private AbstractConcept end;

	public EntityConceptRelation(Entity start, AbstractConcept end, double confidence) {
		super("entityConceptRelation");
		this.setStart(start);
		this.setEnd(end);
		this.setConfidence(confidence);
	}

	protected EntityConceptRelation(Entity start, AbstractConcept end, double confidence, String name) {
		super(name);
		this.setStart(start);
		this.setEnd(end);
		this.setConfidence(confidence);
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
		if (obj instanceof EntityConceptRelation) {
			EntityConceptRelation other = (EntityConceptRelation) obj;
			return start.equalsWithoutRelation((other.getStart())) && end.equalsWithoutRelation((other.getEnd()))
					&& this.confidence == other.confidence && super.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = this.start == null ? hash : 31 * hash + this.start.hashCode();
		hash = this.end == null ? hash : 31 * hash + this.end.hashCode();
		hash = 31 * hash + Double.hashCode(confidence);
		return hash;
	}

	public static Relation readFromArc(IArc arc, ContextIndividual[] graphMap, IGraph graph) {
		Relation relation;
		double confidence = (double) arc.getAttributeValue(CONFIDENCE);
		Entity start;
		AbstractConcept end;
		if (graphMap[graph.getNodes().indexOf(arc.getSourceNode())] instanceof Entity) {
			start = (Entity) graphMap[graph.getNodes().indexOf(arc.getSourceNode())];
		} else {
			throw new IllegalArgumentException("the mapping between node and contextIndividual is defect");
		}
		if (graphMap[graph.getNodes().indexOf(arc.getTargetNode())] instanceof AbstractConcept) {
			end = (AbstractConcept) graphMap[graph.getNodes().indexOf(arc.getTargetNode())];
		} else {
			throw new IllegalArgumentException("the mapping between node and contextIndividual is defect");
		}
		relation = new EntityConceptRelation(start, end, confidence);
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
