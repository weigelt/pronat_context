/**
 * 
 */
package edu.kit.ipd.pronat.context.data.relations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import edu.kit.ipd.pronat.context.data.Action;
import edu.kit.ipd.pronat.context.data.ContextIndividual;
import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.util.GraphUtils;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class SRLArgumentRelation extends ActionEntityRelation {

	protected static final String TYPE = "srlArgumentRelation";

	private String propBankRoleDescr;

	private List<String> verbNetRoles;

	private List<String> frameNetRoles;

	public SRLArgumentRelation(String role, String propBankRoleDescr, List<String> verbNetRoles, List<String> frameNetRoles, Action action,
			Entity entity) {
		super(role, action, entity);
		setPropBankRoleDescr(propBankRoleDescr);
		setVerbNetRoles(verbNetRoles);
		setFrameNetRoles(frameNetRoles);
	}

	/**
	 * @return the propBankRoleDescr
	 */
	public String getPropBankRoleDescr() {
		return propBankRoleDescr;
	}

	/**
	 * @param propBankRoleDescr
	 *            the propBankRoleDescr to set
	 */
	public void setPropBankRoleDescr(String propBankRoleDescr) {
		this.propBankRoleDescr = propBankRoleDescr;
	}

	/**
	 * @return the verbNetRole
	 */
	public List<String> getVerbNetRoles() {
		return verbNetRoles;
	}

	/**
	 * @param verbNetRoles
	 *            the verbNetRoles to set
	 */
	public void setVerbNetRoles(List<String> verbNetRoles) {
		this.verbNetRoles = verbNetRoles;
	}

	/**
	 * @return the frameNetRoles
	 */
	public List<String> getFrameNetRoles() {
		return frameNetRoles;
	}

	/**
	 * @param frameNetRoles
	 *            the frameNetRoles to set
	 */
	public void setFrameNetRoles(List<String> frameNetRoles) {
		this.frameNetRoles = frameNetRoles;
	}

	@Override
	public IArc printToGraph(IGraph graph, HashMap<Long, INode> graphNodes) {
		IArc arc = super.printToGraph(graph, graphNodes);
		arc.setAttributeValue(PB_ROLE_DESCR, getPropBankRoleDescr());
		arc.setAttributeValue(VN_ROLES, Arrays.toString(getVerbNetRoles().toArray()));
		arc.setAttributeValue(FN_ROLES, Arrays.toString(getFrameNetRoles().toArray()));
		arc.setAttributeValue(RELATION_TYPE, TYPE);
		return arc;
	}

	@Override
	public IArc updateArc(IArc arc) {
		super.updateArc(arc);
		arc.setAttributeValue(RELATION_TYPE, TYPE);
		arc.setAttributeValue(PB_ROLE_DESCR, getPropBankRoleDescr());
		arc.setAttributeValue(VN_ROLES, Arrays.toString(getVerbNetRoles().toArray()));
		arc.setAttributeValue(FN_ROLES, Arrays.toString(getFrameNetRoles().toArray()));
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
		if (obj instanceof SRLArgumentRelation) {
			SRLArgumentRelation other = (SRLArgumentRelation) obj;
			return Objects.equals(propBankRoleDescr, other.propBankRoleDescr) && Objects.equals(verbNetRoles, other.verbNetRoles)
					&& Objects.equals(frameNetRoles, other.frameNetRoles) && super.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = propBankRoleDescr == null ? hash : 31 * hash + propBankRoleDescr.hashCode();
		hash = verbNetRoles == null ? hash : 31 * hash + verbNetRoles.hashCode();
		hash = frameNetRoles == null ? hash : 31 * hash + frameNetRoles.hashCode();
		return hash;
	}

	public static Relation readFromArc(IArc arc, ContextIndividual[] graphMap, IGraph graph) {
		Relation relation;
		String name = (String) arc.getAttributeValue(RELATION_NAME);
		String propBankRoleDescr = (String) arc.getAttributeValue(PB_ROLE_DESCR);
		List<String> vnRoles = GraphUtils.getListFromArrayToString((String) arc.getAttributeValue(VN_ROLES));
		List<String> fnRoles = GraphUtils.getListFromArrayToString((String) arc.getAttributeValue(FN_ROLES));
		Action action;
		Entity entity;
		if (graphMap[graph.getNodes().indexOf(arc.getSourceNode())] instanceof Action) {
			action = (Action) graphMap[graph.getNodes().indexOf(arc.getSourceNode())];
		} else {
			throw new IllegalArgumentException("the mapping between node and contextIndividual is defect");
		}
		if (graphMap[graph.getNodes().indexOf(arc.getTargetNode())] instanceof Entity) {
			entity = (Entity) graphMap[graph.getNodes().indexOf(arc.getTargetNode())];
		} else {
			throw new IllegalArgumentException("the mapping between node and contextIndividual is defect");
		}

		relation = new SRLArgumentRelation(name, propBankRoleDescr, vnRoles, fnRoles, action, entity);
		action.addRelationWithoutSettingChanged(relation);
		entity.addRelationWithoutSettingChanged(relation);
		if (arc.getAttributeValue(CONFIDENCE_VERIFIED) != null) {
			relation.setConfidenceVerified((boolean) arc.getAttributeValue(CONFIDENCE_VERIFIED));
		}
		return relation;
	}

	@Override
	public String toString() {
		return "[" + getAction().getName() + "[" + getAction().getReference().get(0).getAttributeValue("position") + "]" + " --"
				+ getName() + ":" + getPropBankRoleDescr() + "|" + getVerbNetRoles() + "|" + getFrameNetRoles() + "-> "
				+ getEntity().getName() + "[" + getEntity().getReference().get(0).getAttributeValue("position") + "]" + "]";
	}

	@Override
	protected String getCompareType() {
		return TYPE;
	}
}
