/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.relations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import edu.kit.ipd.parse.contextanalyzer.data.Action;
import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.util.GraphUtils;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
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
	 * @param verbNetRole
	 *            the verbNetRole to set
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
	public IArc printToGraph(IGraph graph, HashMap<ContextIndividual, INode> graphNodes) {
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
	public boolean isRepresentedByArc(IArc arc, HashMap<ContextIndividual, INode> graphNodes) {
		boolean result = super.isRepresentedByArc(arc, graphNodes);
		result = result && arc.getAttributeValue(RELATION_TYPE).equals(getCompareType());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SRLArgumentRelation) {
			SRLArgumentRelation other = (SRLArgumentRelation) obj;
			return Objects.equals(propBankRoleDescr, other.propBankRoleDescr) && verbNetRoles.equals(other.verbNetRoles)
					&& frameNetRoles.equals(other.frameNetRoles) && super.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (propBankRoleDescr != null) {
			return super.hashCode() ^ propBankRoleDescr.hashCode();
		}
		return super.hashCode();
	}

	public static Relation readFromArc(IArc arc, HashMap<INode, ContextIndividual> graphMap, IGraph graph) {
		Relation relation;
		String name = (String) arc.getAttributeValue(RELATION_NAME);
		String propBankRoleDescr = (String) arc.getAttributeValue(PB_ROLE_DESCR);
		List<String> vnRoles = GraphUtils.getListFromArrayToString((String) arc.getAttributeValue(VN_ROLES));
		List<String> fnRoles = GraphUtils.getListFromArrayToString((String) arc.getAttributeValue(FN_ROLES));
		Action action = (Action) graphMap.get(arc.getSourceNode());
		Entity entity = (Entity) graphMap.get(arc.getTargetNode());
		relation = new SRLArgumentRelation(name, propBankRoleDescr, vnRoles, fnRoles, action, entity);
		action.addRelation(relation);
		entity.addRelation(relation);
		if (arc.getAttributeValue(VERIFIED_BY_DIALOG_AGENT) != null) {
			relation.setVerifiedByDialogAgent((boolean) arc.getAttributeValue(VERIFIED_BY_DIALOG_AGENT));
		}
		return relation;
	}

	@Override
	public String toString() {
		return "[" + this.getAction().getName() + "[" + this.getAction().getReference().get(0).getAttributeValue("position") + "]" + " --"
				+ this.getName() + ":" + this.getPropBankRoleDescr() + "|" + this.getVerbNetRoles() + "|" + this.getFrameNetRoles() + "-> "
				+ this.getEntity().getName() + "[" + this.getEntity().getReference().get(0).getAttributeValue("position") + "]" + "]";
	}

	@Override
	protected String getCompareType() {
		return TYPE;
	}
}
