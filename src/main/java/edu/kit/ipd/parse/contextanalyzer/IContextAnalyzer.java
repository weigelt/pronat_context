package edu.kit.ipd.parse.contextanalyzer;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;

/**
 * This interface represents analyzer working on the {@link IGraph} and
 * {@link Context}.
 * 
 * @author Tobias Hey
 *
 */
public interface IContextAnalyzer {

	/**
	 * Analyzes the current {@link IGraph} and adjust the {@link Context}
	 * respectively.
	 * 
	 * @param graph
	 *            The current {@link IGraph}
	 * @param context
	 *            The current {@link Context}
	 * @throws MissingDataException
	 */
	public void analyze(IGraph graph, Context context) throws MissingDataException;

}
