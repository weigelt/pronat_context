/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.relations;

import java.util.HashMap;

import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PositionType;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public class LocativeRelation extends EntityEntityRelation {

	protected static final String TYPE = "locativeRelation";

	private PositionType location;

	/**
	 * @return the location
	 */
	public PositionType getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(PositionType location) {
		this.location = location;
	}

	public LocativeRelation(String role, PositionType location, Entity start, Entity end) {
		super(role, start, end);
		this.location = location;
	}

	@Override
	public IArc printToGraph(IGraph graph, HashMap<ContextIndividual, INode> graphNodes) {
		IArc arc = super.printToGraph(graph, graphNodes);
		arc.setAttributeValue(RELATION_TYPE, TYPE);
		arc.setAttributeValue(LOCATION, this.location);
		return arc;
	}

	@Override
	public IArc updateArc(IArc arc) {
		super.updateArc(arc);
		arc.setAttributeValue(RELATION_TYPE, TYPE);
		arc.setAttributeValue(LOCATION, this.location);
		return arc;
	}

	@Override
	public boolean isRepresentedByArc(IArc arc, HashMap<ContextIndividual, INode> graphNodes) {
		boolean result = super.isRepresentedByArc(arc, graphNodes);
		result = result && arc.getAttributeValue(RELATION_TYPE).equals(getCompareType());
		if (result) {
			result = result && (PositionType) arc.getAttributeValue(LOCATION) == getLocation();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LocativeRelation) {
			LocativeRelation other = (LocativeRelation) obj;
			return super.equals(other) && this.location == other.location;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ location.hashCode();
	}

	public static Relation readFromArc(IArc arc, HashMap<INode, ContextIndividual> graphMap, IGraph graph) {
		Relation relation;
		String name = (String) arc.getAttributeValue(RELATION_NAME);
		PositionType location = (PositionType) arc.getAttributeValue(LOCATION);
		Entity start = (Entity) graphMap.get(arc.getSourceNode());
		Entity end = (Entity) graphMap.get(arc.getTargetNode());
		relation = new LocativeRelation(name, location, start, end);
		start.addRelation(relation);
		end.addRelation(relation);
		return relation;
	}

	@Override
	public String toString() {
		return "[" + this.getStart().getName() + "[" + this.getStart().getReference().get(0).getAttributeValue("position") + "]" + " --"
				+ this.location + "-> " + this.getEnd().getName() + "[" + this.getEnd().getReference().get(0).getAttributeValue("position")
				+ "]" + "]";
	}

	@Override
	protected String getCompareType() {
		return TYPE;
	}

}