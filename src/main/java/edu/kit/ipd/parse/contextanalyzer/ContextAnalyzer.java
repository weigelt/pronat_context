/**
 *
 */
package edu.kit.ipd.parse.contextanalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.luna.agent.AbstractAgent;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.tools.ConfigManager;
import edu.kit.ipd.parse.ontology_connection.Domain;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

/**
 * This class represents the {@link AbstractAgent} which analyzes the current
 * context and prints the result into the {@link IGraph}.
 *
 * @author Tobias Hey
 *
 */
@MetaInfServices(AbstractAgent.class)
public class ContextAnalyzer extends AbstractAgent {

	private static final String ID = "contextAnalyzer";
	private Dictionary dictionary;
	private Domain domain;
	private List<IContextAnalyzer> analyzer;
	private Context context;
	private Properties props;
	private String similarityMetric;

	private static final Logger logger = LoggerFactory.getLogger(ContextAnalyzer.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see edu.kit.ipd.parse.luna.agent.LunaObserver#init()
	 */
	@Override
	public void init() {
		setId(ID);
		props = ConfigManager.getConfiguration(getClass());
		similarityMetric = props.getProperty("SIMILARITY");
		try {
			// init extjwnl
			dictionary = Dictionary.getDefaultResourceInstance();
		} catch (JWNLException e) {
			logger.error("WordNet Dictionary not accessible!");
			throw new RuntimeException(e);
		}

		domain = Domain.getInstance();
		analyzer = new ArrayList<IContextAnalyzer>();
		analyzer.add(new EntityRecognizer(dictionary, domain));
		analyzer.add(new ActionRecognizer(dictionary, domain));
		analyzer.add(new LocativeRelationAnalyzer());
		analyzer.add(new KnowledgeConnector(dictionary, domain, similarityMetric));
		analyzer.add(new EntityStateDeterminer(dictionary));

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see edu.kit.ipd.parse.luna.agent.AbstractAgent#exec()
	 */
	@Override
	public void exec() {
		context = Context.readFromGraph(graph);
		for (IContextAnalyzer contextAnalyzer : analyzer) {
			try {
				contextAnalyzer.analyze(graph, context);
			} catch (MissingDataException e) {
				logger.error("Some expected Data is missing. Abort Agent execution.", e);
			}
		}
		context.printToGraph(graph);

	}

	/**
	 * Get the current context from the {@link IGraph}
	 *
	 * @return the current context from the {@link IGraph}
	 */
	Context readContextFromGraph() {
		this.context = Context.readFromGraph(graph);
		return context;
	}

	/**
	 * Returns the current {@link Context} representation
	 *
	 * @return the current {@link Context} representation
	 */
	Context getContext() {
		return context;
	}

}
