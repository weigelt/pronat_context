/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.relations;

import java.util.HashMap;

import edu.kit.ipd.parse.contextanalyzer.data.AbstractConcept;
import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public class ConceptConceptRelation extends Relation {

	protected static final String TYPE = "conceptConceptRelation";

	private AbstractConcept start;

	private AbstractConcept end;

	public ConceptConceptRelation(String name, AbstractConcept start, AbstractConcept end) {
		super(name);
		this.setEnd(end);
		this.setStart(start);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.kit.ipd.parse.contextanalyzer.data.relations.Relation#printToGraph(
	 * edu.kit.ipd.parse.luna.graph.IGraph, java.util.HashMap)
	 */
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
		return arc;
	}

	@Override
	public IArc updateArc(IArc arc) {
		arc.setAttributeValue(RELATION_NAME, getName());
		arc.setAttributeValue(RELATION_TYPE, TYPE);
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

	/**
	 * @return the start
	 */
	public AbstractConcept getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(AbstractConcept start) {
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConceptConceptRelation) {
			ConceptConceptRelation other = (ConceptConceptRelation) obj;
			return start.equalsWithoutRelation((other.getStart())) && end.equalsWithoutRelation((other.getEnd())) && super.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ start.hashCode() ^ end.hashCode();
	}

	public static Relation readFromArc(IArc arc, HashMap<INode, ContextIndividual> graphMap, IGraph graph) {
		Relation relation;
		String name = (String) arc.getAttributeValue(RELATION_NAME);
		AbstractConcept start = (AbstractConcept) graphMap.get(arc.getSourceNode());
		AbstractConcept end = (AbstractConcept) graphMap.get(arc.getTargetNode());
		relation = new ConceptConceptRelation(name, start, end);
		start.addRelation(relation);
		end.addRelation(relation);
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