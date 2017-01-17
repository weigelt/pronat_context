/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.graphBuilder.GraphBuilder;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.luna.tools.StringToHypothesis;
import edu.kit.ipd.parse.ner.NERTagger;
import edu.kit.ipd.parse.shallownlp.ShallowNLP;
import edu.kit.ipd.parse.srlabeler.SRLabeler;

/**
 * @author Tobias Hey
 *
 */
public class IntegrationTest {
	ShallowNLP snlp;
	SRLabeler srLabeler;
	NERTagger nerTagger;
	ContextAnalyzer contextAnalyzer;
	GraphBuilder graphBuilder;
	PrePipelineData ppd;
	HashMap<String, String> texts;

	@Before
	public void setUp() {
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
