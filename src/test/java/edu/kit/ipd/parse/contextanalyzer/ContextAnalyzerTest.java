package edu.kit.ipd.parse.contextanalyzer;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.graphBuilder.GraphBuilder;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.Pair;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.luna.tools.StringToHypothesis;
import edu.kit.ipd.parse.ner.NERTagger;
import edu.kit.ipd.parse.shallownlp.ShallowNLP;
import edu.kit.ipd.parse.srlabeler.SRLabeler;
import edu.kit.ipd.parse.wsd.Wsd;

public class ContextAnalyzerTest {

	ShallowNLP snlp;
	SRLabeler srLabeler;
	NERTagger nerTagger;
	ContextAnalyzer contextAnalyzer;
	GraphBuilder graphBuilder;
	Wsd wsd;
	PrePipelineData ppd;
	HashMap<String, String> texts;

	@Before
	public void setUp() {
		wsd = new Wsd();
		wsd.init();
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

		executePreviousStages(ppd);
		try {
			wsd.setGraph(ppd.getGraph());
			wsd.exec();
			contextAnalyzer.setGraph(wsd.getGraph());
			contextAnalyzer.exec();
			Context result = contextAnalyzer.getContext();
			for (Entity entity : result.getEntities()) {
				if (entity instanceof ObjectEntity) {
					Pair<String, Double> oE = ((ObjectEntity) entity).getWNSense();
					if (!(oE == null)) {
						System.out.println(oE);

					}
				}
			}
			System.out.println(result.getEntities());
			System.out.println(result.getActions());
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void twoThree() {
		ppd = new PrePipelineData();
		String input = texts.get("2.3");
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePreviousStages(ppd);
		try {
			wsd.setGraph(ppd.getGraph());
			wsd.exec();
			contextAnalyzer.setGraph(wsd.getGraph());
			contextAnalyzer.exec();
			Context result = contextAnalyzer.getContext();
			System.out.println(result.getEntities());
			System.out.println(result.getActions());
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void twelveTwo() {
		ppd = new PrePipelineData();
		String input = texts.get("12.2");
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePreviousStages(ppd);
		try {
			wsd.setGraph(ppd.getGraph());
			wsd.exec();
			contextAnalyzer.setGraph(wsd.getGraph());
			contextAnalyzer.exec();
			Context result = contextAnalyzer.getContext();
			System.out.println(result.getEntities());
			System.out.println(result.getActions());
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void thirteenOne() {
		ppd = new PrePipelineData();
		String input = texts.get("13.1");
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePreviousStages(ppd);
		try {
			wsd.setGraph(ppd.getGraph());
			wsd.exec();
			contextAnalyzer.setGraph(wsd.getGraph());
			contextAnalyzer.exec();
			Context result = contextAnalyzer.getContext();
			System.out.println(result.getEntities());
			System.out.println(result.getActions());
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void iffourTen() {
		ppd = new PrePipelineData();
		String input = texts.get("if.4.10");
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePreviousStages(ppd);
		try {
			wsd.setGraph(ppd.getGraph());
			wsd.exec();
			contextAnalyzer.setGraph(wsd.getGraph());
			contextAnalyzer.exec();
			Context result = contextAnalyzer.getContext();
			System.out.println(result.getEntities());
			System.out.println(result.getActions());
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void variabel() {
		ppd = new PrePipelineData();
		String input = "Armar go to the two white fridges and cupboard and close them both";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePreviousStages(ppd);
		try {
			wsd.setGraph(ppd.getGraph());
			wsd.exec();
			contextAnalyzer.setGraph(wsd.getGraph());
			contextAnalyzer.exec();
			Context result = contextAnalyzer.getContext();
			System.out.println(result.getEntities());
			System.out.println(result.getActions());
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void multiple() {
		ppd = new PrePipelineData();
		String input = "Armar go to the fridge next to the oven and then to the dishwasher and the flatiron which is near the microwave";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePreviousStages(ppd);
		try {
			Context prev = new Context();
			Context result = new Context();
			IGraph graph = ppd.getGraph();
			wsd.setGraph(ppd.getGraph());
			wsd.exec();
			graph = wsd.getGraph();
			do {
				prev = result;
				contextAnalyzer.setGraph(graph);
				contextAnalyzer.exec();
				result = contextAnalyzer.getContext();
				graph = contextAnalyzer.getGraph();
				System.out.println(result.getEntities());
				System.out.println(result.getActions());
				System.out.println(result.getConcepts());
			} while (!prev.equals(result));
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void multipleScenario1() {
		ppd = new PrePipelineData();
		String input = "Armar get the green cup from the table next to the popcorn and go to the fridge then open the fridge and take the water out of it afterwards fill the cup with water and bring it to me then take the red cups out of the dishwasher and put them in the cupboard";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePreviousStages(ppd);
		try {
			Context prev = new Context();
			Context result = new Context();
			IGraph graph = ppd.getGraph();
			wsd.setGraph(ppd.getGraph());
			wsd.exec();
			graph = wsd.getGraph();
			do {
				prev = result;
				contextAnalyzer.setGraph(graph);
				contextAnalyzer.exec();
				result = contextAnalyzer.getContext();
				graph = contextAnalyzer.getGraph();
				System.out.println(result.getEntities());
				System.out.println(result.getActions());
				System.out.println(result.getConcepts());
			} while (!prev.equals(result));
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void multipleScenario2() {
		ppd = new PrePipelineData();
		String input = "Armar take a plate out of the dishwasher and put it on the table then open the fridge and take the instant meal out of it afterwards put the meal on the plate and put it into the microwave when it is finished put the plate on the table";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePreviousStages(ppd);
		try {
			Context prev = new Context();
			Context result = new Context();
			IGraph graph = ppd.getGraph();
			wsd.setGraph(ppd.getGraph());
			wsd.exec();
			graph = wsd.getGraph();
			do {
				prev = result;
				contextAnalyzer.setGraph(graph);
				contextAnalyzer.exec();
				result = contextAnalyzer.getContext();
				graph = contextAnalyzer.getGraph();
				System.out.println(result.getEntities());
				System.out.println(result.getActions());
				System.out.println(result.getConcepts());
			} while (!prev.equals(result));
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	private void executePreviousStages(PrePipelineData ppd) {
		try {
			snlp.exec(ppd);
		} catch (PipelineStageException e) {

			e.printStackTrace();
		}
		try {
			nerTagger.exec(ppd);
		} catch (PipelineStageException e) {
			e.printStackTrace();
		}
		try {
			srLabeler.exec(ppd);

		} catch (PipelineStageException e) {

			e.printStackTrace();
		}

		try {
			graphBuilder.exec(ppd);
		} catch (PipelineStageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
