/**
 *
 */
package edu.kit.ipd.pronat.context;

import java.util.HashMap;
import java.util.Properties;

import edu.kit.ipd.parse.ontology_connection.Domain;
import edu.kit.ipd.pronat.graph_builder.GraphBuilder;
import edu.kit.ipd.pronat.ner.NERTagger;
import edu.kit.ipd.pronat.prepipedatamodel.PrePipelineData;
import edu.kit.ipd.pronat.prepipedatamodel.tools.StringToHypothesis;
import edu.kit.ipd.pronat.shallow_nlp.ShallowNLP;
import edu.kit.ipd.pronat.srl.SRLabeler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.ipd.pronat.context.data.Context;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.luna.tools.ConfigManager;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class IntegrationTest {
	private static ShallowNLP snlp;
	private static SRLabeler srLabeler;
	private static NERTagger nerTagger;
	private static ContextAnalyzer contextAnalyzer;
	private static GraphBuilder graphBuilder;
	PrePipelineData ppd;
	private static HashMap<String, String> texts;
	private static Properties props;

	@BeforeClass
	public static void setUp() {
		props = ConfigManager.getConfiguration(Domain.class);
		props.setProperty("ONTOLOGY_PATH", "/ontology.owl");
		props.setProperty("SYSTEM", "System");
		props.setProperty("METHOD", "Method");
		props.setProperty("PARAMETER", "Parameter");
		props.setProperty("DATATYPE", "DataType");
		props.setProperty("VALUE", "Value");
		props.setProperty("STATE", "State");
		props.setProperty("OBJECT", "Object");
		props.setProperty("SYSTEM_HAS_METHOD", "hasMethod");
		props.setProperty("STATE_ASSOCIATED_STATE", "associatedState");
		props.setProperty("STATE_ASSOCIATED_OBJECT", "associatedObject");
		props.setProperty("STATE_CHANGING_METHOD", "changingMethod");
		props.setProperty("METHOD_CHANGES_STATE", "changesStateTo");
		props.setProperty("METHOD_HAS_PARAMETER", "hasParameter");
		props.setProperty("OBJECT_HAS_STATE", "hasState");
		props.setProperty("OBJECT_SUB_OBJECT", "subObject");
		props.setProperty("OBJECT_SUPER_OBJECT", "superObject");
		props.setProperty("PARAMETER_OF_DATA_TYPE", "ofDataType");
		props.setProperty("DATATYPE_HAS_VALUE", "hasValue");
		props.setProperty("PRIMITIVE_TYPES", "String,int,double,float,short,char,boolean,long");
		graphBuilder = new GraphBuilder();
		graphBuilder.init();
		nerTagger = new NERTagger();
		nerTagger.init();
		srLabeler = new SRLabeler();
		srLabeler.init();
		snlp = new ShallowNLP();
		snlp.init();
		texts = CorpusTexts.texts;
		contextAnalyzer = new ContextAnalyzer();
		contextAnalyzer.init();
	}

	@Test
	public void threeTwo() {
		ppd = new PrePipelineData();
		String input = texts.get("3.2");
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			Context contextResult = contextAnalyzer.getContext();
			Assert.assertEquals(contextResult, contextAnalyzer.readContextFromGraph());

		} catch (MissingDataException e) {
			// TODO Auto
			e.printStackTrace();
		}

	}

	@Test
	public void twoOne() {
		ppd = new PrePipelineData();
		String input = texts.get("2.1");
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			Context contextResult = contextAnalyzer.getContext();
			Assert.assertEquals(contextResult, contextAnalyzer.readContextFromGraph());

		} catch (MissingDataException e) {
			// TODO Auto
			e.printStackTrace();
		}

	}

	@Test
	public void thirteenOne() {
		ppd = new PrePipelineData();
		String input = texts.get("13.1");
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			Context contextResult = contextAnalyzer.getContext();
			Assert.assertEquals(contextResult, contextAnalyzer.readContextFromGraph());

		} catch (MissingDataException e) {
			// TODO Auto
			e.printStackTrace();
		}

	}

	@Test
	public void ifFiveSeven() {
		ppd = new PrePipelineData();
		String input = texts.get("if.5.7");
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			Context contextResult = contextAnalyzer.getContext();
			Assert.assertEquals(contextResult, contextAnalyzer.readContextFromGraph());

		} catch (MissingDataException e) {
			// TODO Auto
			e.printStackTrace();
		}

	}

	private void executeSNLPandSRLandNER(PrePipelineData ppd) {
		try {
			snlp.exec(ppd);
		} catch (PipelineStageException e) {
			// TODO Auto
			e.printStackTrace();
		}
		try {
			nerTagger.exec(ppd);
		} catch (PipelineStageException e) {
			// TODO Auto
			e.printStackTrace();
		}
		try {
			srLabeler.exec(ppd);
			graphBuilder.exec(ppd);
		} catch (PipelineStageException e) {
			// TODO Auto
			e.printStackTrace();
		}

	}

}
