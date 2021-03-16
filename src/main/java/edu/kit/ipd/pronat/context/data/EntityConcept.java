/**
 * 
 */
package edu.kit.ipd.pronat.context.data;

/**
 *
 * @author Sebastian Weigelt
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
	public boolean equalsWithoutRelation(Object obj) {
		if (obj instanceof EntityConcept) {
			return super.equalsWithoutRelation(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
