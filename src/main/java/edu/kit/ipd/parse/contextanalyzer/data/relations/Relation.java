/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.relations;

import java.util.HashMap;

import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public abstract class Relation {

	public static final String RELATION_ARC_TYPE = "contextRelation";
	protected static final String RELATION_NAME = "name";
	protected static final String RELATION_TYPE = "typeOfRelation";
	protected static final String PB_ROLE_DESCR = "pbRoleDescr";
	protected static final String VN_ROLES = "vnRoles";
	protected static final String FN_ROLES = "fnRoles";
	protected static final String CONFIDENCE = "confidence";
	protected static final String LOCATION = "location";
	private String name;

	/**
	 * 
	 */
	public Relation(String name) {
		this.setName(name);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the role to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public abstract IArc printToGraph(IGraph graph, HashMap<ContextIndividual, INode> graphNodes);

	protected IArcType createRelationArcType(IGraph graph) {
		IArcType arcType = graph.createArcType(RELATION_ARC_TYPE);
		arcType.addAttributeToType("String", RELATION_NAME);
		arcType.addAttributeToType("String", RELATION_TYPE);
		arcType.addAttributeToType("String", PB_ROLE_DESCR);
		arcType.addAttributeToType("String", VN_ROLES);
		arcType.addAttributeToType("String", FN_ROLES);
		arcType.addAttributeToType("double", CONFIDENCE);
		arcType.addAttributeToType("String", LOCATION);
		return arcType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Relation) {
			Relation other = (Relation) obj;
			return name.equals(other.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public static Relation readFromArc(IArc arc, HashMap<INode, ContextIndividual> graphMap, IGraph graph) {
		switch ((String) arc.getAttributeValue(RELATION_TYPE)) {
		case ActionEntityRelation.TYPE:
			return ActionEntityRelation.readFromArc(arc, graphMap, graph);

		case EntityEntityRelation.TYPE:
			return EntityEntityRelation.readFromArc(arc, graphMap, graph);

		case SRLArgumentRelation.TYPE:
			return SRLArgumentRelation.readFromArc(arc, graphMap, graph);
		case ReferentRelation.TYPE:
			return ReferentRelation.readFromArc(arc, graphMap, graph);
		case ConjunctionRelation.TYPE:
			return ConjunctionRelation.readFromArc(arc, graphMap, graph);
		case LocativeRelation.TYPE:
			return LocativeRelation.readFromArc(arc, graphMap, graph);
		case ActionConceptRelation.TYPE:
			return ActionConceptRelation.readFromArc(arc, graphMap, graph);
		case EntityConceptRelation.TYPE:
			return EntityConceptRelation.readFromArc(arc, graphMap, graph);
		case ConceptConceptRelation.TYPE:
			return ConceptConceptRelation.readFromArc(arc, graphMap, graph);
		case EntityStateRelation.TYPE:
			return EntityStateRelation.readFromArc(arc, graphMap, graph);
		default:
			return null;
		}

	}

	@Override
	public String toString() {
		return "[" + this.name + "]";
	}

	public boolean isRepresentedByArc(IArc arc, HashMap<ContextIndividual, INode> graphNodes) {
		if (arc.getType().getName().equals(RELATION_ARC_TYPE)) {
			if (arc.getAttributeValue(RELATION_NAME).equals(this.getName())) {
				return true;
			}
		}
		return false;
	}

	public abstract IArc updateArc(IArc arc);

	protected abstract String getCompareType();

}
