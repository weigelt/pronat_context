package edu.kit.ipd.parse.contextanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ipd.parse.contextanalyzer.data.AbstractConcept;
import edu.kit.ipd.parse.contextanalyzer.data.Action;
import edu.kit.ipd.parse.contextanalyzer.data.ActionConcept;
import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.ContextIndividual;
import edu.kit.ipd.parse.contextanalyzer.data.EntityConcept;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ActionConceptRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.EntityConceptRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.contextanalyzer.util.GraphUtils;
import edu.kit.ipd.parse.graphBuilder.GraphBuilder;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.graph.Pair;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.luna.tools.ConfigManager;
import edu.kit.ipd.parse.luna.tools.StringToHypothesis;
import edu.kit.ipd.parse.ner.NERTagger;
import edu.kit.ipd.parse.ontology_connection.Domain;
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
	private static Properties props;

	@Before
	public void setUp() {
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
	public void conceptBuildingEval() {
		int total = 0;
		int tp = 0;
		int fp = 0;
		int fn = 0;
		List<String> failures = new ArrayList<>();
		for (String id : evalTexts.keySet()) {
			ppd = new PrePipelineData();
			List<Pair<String, String>> text = evalTexts.get(id);
			String input = prepareInputString(text);
			ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
			System.out.println(id);
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
				Pair<List<String>, int[]> checkResult = evaluateConceptBuilding(text, result, indexMap);
				total += checkResult.getRight()[0];
				tp += checkResult.getRight()[1];
				fp += checkResult.getRight()[2];
				fn += checkResult.getRight()[3];
				for (String failure : checkResult.getLeft()) {
					failures.add(id + ": " + failure);
				}
			} catch (MissingDataException e) {
				e.printStackTrace();
			}
		}
		double precision = (double) tp / (double) (tp + fp);
		double recall = (double) tp / (double) (tp + fn);
		double f1 = (2 * precision * recall) / (precision + recall);
		System.out.println("----------------------------------------------------");
		System.out.println("| Correct Relations: " + tp + "/" + total + " | Additionally Detected: " + fp + "|");
		System.out.println("| Precision = " + precision + "   Recall = " + recall + "  F1 = " + f1 + "|");
		System.out.println("----------------------------------------------------");
		for (String string : failures) {
			System.out.println(string);
		}
	}

	@Test
	public void conceptHierarchieBuildingEval() {
		int total = 0;
		int tp = 0;
		int fp = 0;
		int fn = 0;
		List<String> failures = new ArrayList<>();
		for (String id : evalTexts.keySet()) {
			ppd = new PrePipelineData();
			List<Pair<String, String>> text = evalTexts.get(id);
			String input = prepareInputString(text);
			ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
			System.out.println(id);
			executePreviousStages(ppd);
			try {
				Context prev = new Context();
				Context result = new Context();
				IGraph graph = ppd.getGraph();
				wsd.setGraph(ppd.getGraph());
				wsd.exec();
				graph = wsd.getGraph();
				System.out.println(id);
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
				Pair<List<String>, int[]> checkResult = evaluateConceptHierarchieBuilding(text, result, indexMap);
				total += checkResult.getRight()[0];
				tp += checkResult.getRight()[1];
				fp += checkResult.getRight()[2];
				fn += checkResult.getRight()[3];
				for (String failure : checkResult.getLeft()) {
					failures.add(id + ": " + failure);
				}
			} catch (MissingDataException e) {
				e.printStackTrace();
			}
		}
		double precision = (double) tp / (double) (tp + fp);
		double recall = (double) tp / (double) (tp + fn);
		double f1 = (2 * precision * recall) / (precision + recall);
		System.out.println("----------------------------------------------------");
		System.out.println("| Correct Relations: " + tp + "/" + total + " | Additionally Detected: " + fp + "|");
		System.out.println("| Precision = " + precision + "   Recall = " + recall + "  F1 = " + f1 + "|");
		System.out.println("----------------------------------------------------");
		for (String string : failures) {
			System.out.println(string);
		}
	}

	@Test
	public void multiple() {
		ppd = new PrePipelineData();
		List<Pair<String, String>> text = evalTexts.get("s6p02");
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
			Pair<List<String>, int[]> resultEval = evaluateConceptHierarchieBuilding(text, result, indexMap);
			int[] res = resultEval.getRight();
			System.out.println(
					"Total: " + res[0] + ", truePositive: " + res[1] + ", falsePositive: " + res[2] + ", falseNegative: " + res[3]);
			for (String string : resultEval.getLeft()) {
				System.out.println(string);
			}
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

	private Pair<List<String>, int[]> evaluateConceptBuilding(List<Pair<String, String>> text, Context result,
			HashMap<Integer, Integer> indexMap) {
		int tp = 0;
		int fp = 0;
		int fn = 0;
		int total = 0;
		List<String> failures = new ArrayList<>();
		HashSet<Relation> alreadyChecked = new HashSet<>();
		for (int i = 0; i < text.size(); i++) {
			Pair<String, String> pair = text.get(i);
			String word = pair.getLeft();
			if (word != null) {
				String annotation = pair.getRight();
				if (annotation != null && indexMap.containsKey(i)) {
					total++;
					int position = indexMap.get(i);
					ContextIndividual ci = getContainingContextIndividual(position, result);
					if (ci instanceof Entity) {
						EntityConcept concept = ContextUtils.getMostLikelyEntityConcept(ci.getRelations());
						if (concept != null) {
							String conceptName = annotation.substring(1, annotation.length() - 1).trim();
							if (annotation.contains(",")) {
								conceptName = conceptName.split(",")[0];
							}

							if (concept.getName().toLowerCase().equals(conceptName.toLowerCase())) {
								tp++;
							} else {
								String failure = "Word:" + word + " at position " + position + " has related Concept: " + concept.getName()
										+ " but Concept " + conceptName + " was expected";
								failures.add(failure);

								fn++;
								fp++;
							}
							alreadyChecked.addAll(ci.getRelationsOfType(EntityConceptRelation.class));
						} else {

							String failure = "Word:" + word + " at position " + position + " has no related Concept!";
							failures.add(failure);

							fn++;
						}
					} else if (ci instanceof Action) {
						ActionConcept concept = ContextUtils.getMostLikelyActionConcept(ci.getRelations());
						if (concept != null) {
							String conceptName = annotation.substring(1, annotation.length() - 1).trim();
							String[] split = conceptName.split(",");
							conceptName = split[0];
							String synonym = "";
							if (split.length > 1) {
								synonym = split[1];
							}
							if (concept.getName().toLowerCase().equals(conceptName)
									|| concept.getName().toLowerCase().equals(synonym.toLowerCase())) {
								tp++;
							} else {
								String failure = "Word:" + word + " at position " + position + " has related Concept: " + concept.getName()
										+ " but Concept " + conceptName + " was expected";
								failures.add(failure);

								fn++;
								fp++;
							}
							alreadyChecked.addAll(ci.getRelationsOfType(ActionConceptRelation.class));
						} else {
							String failure = "Word:" + word + " at position " + position + " has no related Concept!";
							failures.add(failure);

							fn++;
						}
					} else {
						String failure = "Word:" + word + " at position " + position + " is not connected with any Entity or Action!";
						failures.add(failure);
						fn++;
					}
				}
			}
		}
		for (AbstractConcept abstractConcept : result.getConcepts()) {
			HashSet<Entity> targets = new HashSet<>();
			for (Relation relation : abstractConcept.getRelationsOfType(EntityConceptRelation.class)) {
				if (!alreadyChecked.contains(relation)) {
					Entity entity = ((EntityConceptRelation) relation).getStart();
					if (!targets.contains(entity)) {
						targets.add(entity);
						String failure = "Entity:" + entity.getName() + " at position ["
								+ entity.getReference().get(0).getAttributeValue("position") + "-"
								+ entity.getReference().get(entity.getReference().size() - 1).getAttributeValue("position")
								+ "] has related Concept: " + abstractConcept.getName() + " but no Concept was expected";
						failures.add(failure);

						fp++;
					}
				}
			}
			HashSet<Action> targetsA = new HashSet<>();
			for (Relation relation : abstractConcept.getRelationsOfType(ActionConceptRelation.class)) {
				if (!alreadyChecked.contains(relation)) {
					Action action = ((ActionConceptRelation) relation).getStart();
					if (!targetsA.contains(action)) {
						targetsA.add(action);
						String failure = "Action:" + action.getName() + " at position ["
								+ action.getReference().get(0).getAttributeValue("position") + "-"
								+ action.getReference().get(action.getReference().size() - 1).getAttributeValue("position")
								+ "] has related Concept: " + abstractConcept.getName() + " but no Concept was expected";
						failures.add(failure);

						fp++;
					}
				}
			}
		}
		return new Pair<List<String>, int[]>(failures, new int[] { total, tp, fp, fn });

	}

	private Pair<List<String>, int[]> evaluateConceptHierarchieBuilding(List<Pair<String, String>> text, Context result,
			HashMap<Integer, Integer> indexMap) {
		int tp = 0;
		int fp = 0;
		int fn = 0;
		int total = 0;
		List<String> failures = new ArrayList<>();
		HashSet<String> annotationsToCheck = new HashSet<>();
		HashSet<AbstractConcept> alreadyChecked = new HashSet<>();
		for (int i = 0; i < text.size(); i++) {
			Pair<String, String> pair = text.get(i);
			if (pair.getLeft() != null) {
				String word = pair.getLeft();
				String annotation = pair.getRight();
				if (annotation != null && indexMap.containsKey(i)) {

					int position = indexMap.get(i);
					ContextIndividual ci = getContainingContextIndividual(position, result);
					if (ci instanceof Entity) {
						AbstractConcept concept = ContextUtils.getMostLikelyEntityConcept(ci.getRelations());
						AbstractConcept last = concept;
						AbstractConcept start = concept;

						if (concept != null) {
							if (!alreadyChecked.contains(concept)) {
								String content = annotation.substring(1, annotation.length() - 1).trim();

								String[] annotationContent = content.split(",");
								if (annotationContent.length > 1) {
									total += annotationContent.length - 1;
									if (concept.getName().toLowerCase().equals(annotationContent[0].toLowerCase())) {
										//TODO: Be able to check mutiple super concepts
										for (int j = 1; j < annotationContent.length; j++) {
											String name = annotationContent[j];
											for (AbstractConcept superConcept : concept.getSuperConcepts()) {
												if (superConcept.getName().equalsIgnoreCase(name)) {
													tp++;
													concept = superConcept;

												} else {
													String failure = "Concept: " + last.getName() + " has SuperConcept "
															+ superConcept.getName() + " which was not expected";
													failures.add(failure);
													fp++;
												}
											}
											if (concept.equals(last)) {
												String failure = "Concept: " + concept.getName() + " has no SuperConcept " + name;
												failures.add(failure);

												fn += annotationContent.length - j;
											} else {
												alreadyChecked.add(last);
												last = concept;
											}
										}
									} else {
										String failure = "Word:" + word + " at position " + position + " has related Concept: "
												+ concept.getName() + " but Concept " + annotationContent[0] + " was expected";
										failures.add(failure);

										fn += annotationContent.length - 1;
									}

								} else {
									if (!concept.getName().toLowerCase().equals(annotationContent[0].toLowerCase())) {

										String failure = "Word:" + word + " at position " + position + " has related Concept: "
												+ concept.getName() + " but Concept " + annotationContent[0] + " was expected";
										failures.add(failure);

										fn++;
										fp++;
									} else if (concept.hasSuperConcepts()) {
										for (AbstractConcept superConcept : concept.getSuperConcepts()) {
											String failure = "Concept:" + concept.getName() + " has SuperConcept " + superConcept.getName()
													+ " but no SuperConcept was expected";
											failures.add(failure);

											fp++;
										}
									}
								}

							}
						} else {

							String failure = "Word:" + word + " at position " + position + " has no related Concept!";
							failures.add(failure);

							fn++;
						}
						alreadyChecked.add(start);
					} else if (ci instanceof Action) {

					}
				}
			} else {
				annotationsToCheck.add(pair.getRight());
			}
		}
		for (String annotation : annotationsToCheck) {
			String content = annotation.substring(1, annotation.length() - 1).trim();
			total++;
			String[] annotationContent = content.split(",");
			AbstractConcept concept = result.getConcept(annotationContent[0]);
			AbstractConcept last = concept;
			if (concept != null) {

				for (int j = 1; j < annotationContent.length; j++) {
					if (!alreadyChecked.contains(concept)) {
						String name = annotationContent[j];
						for (AbstractConcept superConcept : concept.getSuperConcepts()) {
							if (superConcept.getName().equalsIgnoreCase(name)) {
								tp++;
								concept = superConcept;

							} else {

								String failure = "Concept: " + last.getName() + " has SuperConcept " + superConcept.getName()
										+ " which was not expected";
								failures.add(failure);
								fp++;
							}
						}
						if (concept.equals(last)) {
							String failure = "Concept: " + concept.getName() + " has no SuperConcept " + name;
							failures.add(failure);

							fn += annotationContent.length - j;
						} else {
							alreadyChecked.add(last);
							last = concept;
						}
					}
				}
				alreadyChecked.add(concept);
			} else {
				String failure = "Concept " + annotationContent[0] + " was expected but was not present";
				failures.add(failure);
				fn += annotationContent.length - 1;
			}
		}

		for (AbstractConcept abstractConcept : result.getConcepts()) {
			if (!alreadyChecked.contains(abstractConcept) && abstractConcept.hasSuperConcepts()) {
				for (AbstractConcept concept : abstractConcept.getSuperConcepts()) {
					String failure = "Concept:" + abstractConcept.getName() + " has SuperConcept " + concept.getName()
							+ " but no SuperConcept was expected";
					failures.add(failure);

					fp++;
					alreadyChecked.add(concept);
				}
			}

		}
		return new Pair<List<String>, int[]>(failures, new int[] { total, tp, fp, fn });

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
			if (pair.getLeft() != null) {
				input += " " + pair.getLeft();
			}
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
