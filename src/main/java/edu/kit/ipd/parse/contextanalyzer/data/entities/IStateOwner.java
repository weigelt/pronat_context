/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.data.entities;

import java.util.Set;

import edu.kit.ipd.parse.contextanalyzer.data.State;

/**
 * @author Tobias Hey
 *
 */
public interface IStateOwner {
	public boolean hasState();

	public Set<State> getStates();
}
