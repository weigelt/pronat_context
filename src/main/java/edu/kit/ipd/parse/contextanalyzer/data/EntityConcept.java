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
	}

	protected static final String TYPE = "entityConcept";

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

}
