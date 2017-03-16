/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data;

/**
 * @author Tobias Hey
 *
 */
public abstract class EntityConcept extends AbstractConcept {

	protected EntityConcept(String name) {
		super(name);
		changed = false;
	}

	protected static final String TYPE = "entityConcept";

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityConcept) {
			return super.equals(obj);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
