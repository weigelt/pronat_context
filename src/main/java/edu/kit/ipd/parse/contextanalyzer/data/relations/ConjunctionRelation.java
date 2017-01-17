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
public class ConjunctionRelation extends EntityEntityRelation {

	protected static final String TYPE = "conjunctionRelation";

	public ConjunctionRelation(String role, Entity start, Entity end) {
		super(role, start, end);
	}

	@Override
	public IArc printToGraph(IGraph graph, HashMap<ContextIndividual, INode> graphNodes) {
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
	public boolean isRepresentedByArc(IArc arc, HashMap<ContextIndividual, INode> graphNodes) {
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

	public static Relation readFromArc(IArc arc, HashMap<INode, ContextIndividual> graphMap, IGraph graph) {
		Relation relation;
		String name = (String) arc.getAttributeValue(RELATION_NAME);
		Entity start = (Entity) graphMap.get(arc.getSourceNode());
		Entity end = (Entity) graphMap.get(arc.getTargetNode());
		relation = new ConjunctionRelation(name, start, end);
		start.addRelation(relation);
		end.addRelation(relation);
		return relation;
	}

	@Override
	protected String getCompareType() {
		return TYPE;
	}

}
