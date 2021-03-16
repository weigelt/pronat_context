/**
 * 
 */
package edu.kit.ipd.pronat.context.data.entities;

import java.util.Set;

import edu.kit.ipd.pronat.context.data.State;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public interface IStateOwner {
	public boolean hasState();

	public Set<State> getStates();
}
