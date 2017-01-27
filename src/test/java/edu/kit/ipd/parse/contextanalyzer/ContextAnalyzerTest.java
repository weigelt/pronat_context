package edu.kit.ipd.parse.contextanalyzer;

import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ipd.parse.contextanalyzer.data.Action;
import edu.kit.ipd.parse.contextanalyzer.data.ActionConcept;
import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.contextanalyzer.data.EntityConcept;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.contextanalyzer.util.GraphUtils;
import edu.kit.ipd.parse.graphBuilder.GraphBuilder;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
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
	HashMap<String, List<Pair<String, String>>> evalTexts;

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
		evalTexts = CorpusTexts.evalTexts;
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
		List<Pair<String, String>> text = evalTexts.get("s6p01");
		String input = prepareInputString(text);
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
			HashMap<Integer, Integer> indexMap = produceIndexMappings(text, graph);
			int[] res = evaluateConceptBuilding(text, result, indexMap);
			System.out.println(
					"Total: " + res[0] + ", truePositive: " + res[1] + ", falsePositive: " + res[2] + ", falseNegative: " + res[3]);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	private HashMap<Integer, Integer> produceIndexMappings(List<Pair<String, String>> text, IGraph graph) {
		HashMap<Integer, Integer> result = new HashMap<>();
		int index = 0;
		try {
			List<INode> nodes = GraphUtils.getNodesOfUtterance(graph);
			for (INode node : nodes) {
				if (node.getType().containsAttribute("position", "int") && node.getType().containsAttribute("value", "String")) {
					for (int i = index; i < text.size(); i++) {
						if (text.get(i).getLeft().equals(node.getAttributeValue("value"))) {
							result.put(i, (Integer) node.getAttributeValue("position"));
							index = i + 1;
							break;
						}
					}
				}
			}
		} catch (MissingDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private int[] evaluateConceptBuilding(List<Pair<String, String>> text, Context result, HashMap<Integer, Integer> indexMap) {
		int tp = 0;
		int fp = 0;
		int fn = 0;
		int total = 0;
		for (int i = 0; i < text.size(); i++) {
			Pair<String, String> pair = text.get(i);
			String word = pair.getLeft();
			String annotation = pair.getRight();
			if (annotation != null && indexMap.containsKey(i)) {
				total++;
				int position = indexMap.get(i);
				ContextIndividual ci = getContainingContextIndividual(position, result);
				if (ci instanceof Entity) {
					EntityConcept concept = ContextUtils.getMostLikelyEntityConcept(ci.getRelations());
					if (concept != null) {
						if (concept.getName().toLowerCase().equals(annotation.replaceAll("[", "").replaceAll("]", "").split(",")[0])) {
							tp++;
						} else {
							System.out.println("Word:" + word + " at position " + position + " has related Concept: " + concept.getName()
									+ " but Concept " + annotation.replaceAll("[", "").replaceAll("]", "").split(",")[0] + " was expected");
							fn++;
							fp++;
						}
					} else {
						System.out.println("Word:" + word + " at position " + position + " has no related Concept!");
						fn++;
					}
				} else if (ci instanceof Action) {
					ActionConcept concept = ContextUtils.getMostLikelyActionConcept(ci.getRelations());
					if (concept != null) {
						if (concept.getName().toLowerCase().equals(annotation.replaceAll("[", "").replaceAll("]", "").split(",")[0])) {
							tp++;
						} else {
							System.out.println("Word:" + word + " at position " + position + " has related Concept: " + concept.getName()
									+ " but Concept " + annotation.replaceAll("[", "").replaceAll("]", "").split(",")[0] + " was expected");
							fn++;
							fp++;
						}
					} else {
						System.out.println("Word:" + word + " at position " + position + " has no related Concept!");
						fn++;
					}
				}
			}
		}
		return new int[] { total, tp, fp, fn };

	}

	private ContextIndividual getContainingContextIndividual(int position, Context result) {
		for (Entity entity : result.getEntities()) {
			if (!(entity instanceof PronounEntity)) {

				for (INode node : entity.getReference()) {
					if ((int) node.getAttributeValue("position") == position) {
						return entity;
					}
				}
			}
		}
		for (Action action : result.getActions()) {
			for (INode node : action.getReference()) {
				if ((int) node.getAttributeValue("position") == position) {
					return action;
				}
			}
		}
		return null;
	}

	private String prepareInputString(List<Pair<String, String>> text) {
		String input = "";
		for (Pair<String, String> pair : text) {
			input += " " + pair.getLeft();
		}
		input = input.trim();
		return input;
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
