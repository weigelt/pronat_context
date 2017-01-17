/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.relationship.AsymmetricRelationship;
import net.sf.extjwnl.data.relationship.Relationship;
import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;
import net.sf.extjwnl.dictionary.Dictionary;

/**
 * @author Tobias Hey
 *
 */
public final class WordNetUtils {

	public static final IndexWord getIndexWord(String name, POS pos, Dictionary dictionary) {
		try {
			return dictionary.lookupIndexWord(pos, name);
		} catch (JWNLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final LeastCommonSubsumer getLeastCommonSubsumer(IndexWord current, IndexWord candidate) {
		LeastCommonSubsumer result = null;
		double icMax = 0.0;
		for (Synset currSynset : current.getSenses()) {
			for (Synset candSynset : candidate.getSenses()) {
				try {
					RelationshipList list = RelationshipFinder.findRelationships(currSynset, candSynset, PointerType.HYPERNYM);
					for (Relationship relationship : list) {
						if (relationship instanceof AsymmetricRelationship) {
							AsymmetricRelationship asym = (AsymmetricRelationship) relationship;
							Double informationContent = WordNetInformationContent
									.getInformationContent(asym.getNodeList().get(asym.getCommonParentIndex()).getSynset());
							if (informationContent > icMax) {
								result = new LeastCommonSubsumer(currSynset, candSynset,
										asym.getNodeList().get(asym.getCommonParentIndex()).getSynset());
								icMax = informationContent;
							}
						}

					}
				} catch (CloneNotSupportedException | JWNLException e) {
					e.printStackTrace();
				}
			}

		}
		return result;
	}

	public static final Double calculateLinSimilarity(Synset start, Synset end, Synset lcs) {
		Double startIC = WordNetInformationContent.getInformationContent(start);
		Double endIC = WordNetInformationContent.getInformationContent(end);
		Double lcsIC = WordNetInformationContent.getInformationContent(lcs);
		if (startIC != null && endIC != null && lcsIC != null) {
			return (2 * lcsIC) / (startIC + endIC);
		}
		return null;
	}

	public static final List<String> getDirectHypernyms(String name, POS pos, Dictionary dictionary) {
		List<String> hypernymResult = new ArrayList<>();
		IndexWord indexWord;
		try {
			indexWord = dictionary.lookupIndexWord(pos, name);
			if (indexWord != null) {
				int taggedSenses = indexWord.sortSenses();
				if (taggedSenses != 0) {
					for (int i = 0; i < taggedSenses; i++) {
						Synset synset = indexWord.getSenses().get(i);
						PointerTargetNodeList hypernyms = PointerUtils.getDirectHypernyms(synset);
						for (PointerTargetNode pointerTargetNode : hypernyms) {
							for (Word word : pointerTargetNode.getSynset().getWords()) {
								if (!hypernymResult.contains(word.getLemma())) {
									hypernymResult.add(word.getLemma());
								}
							}
						}
					}
				} else {
					for (Synset synset : indexWord.getSenses()) {
						PointerTargetNodeList hypernyms = PointerUtils.getDirectHypernyms(synset);
						for (PointerTargetNode pointerTargetNode : hypernyms) {
							for (Word word : pointerTargetNode.getSynset().getWords()) {
								if (!hypernymResult.contains(word.getLemma())) {
									hypernymResult.add(word.getLemma());
								}
							}
						}
					}
				}
			}
		} catch (JWNLException e) {
			e.printStackTrace();
		}

		return hypernymResult;
	}

	public static final List<String> getDirectHyponyms(String name, POS pos, Dictionary dictionary) {
		List<String> hyponymResult = new ArrayList<>();
		IndexWord indexWord;
		try {
			indexWord = dictionary.lookupIndexWord(pos, name);
			if (indexWord != null) {
				int taggedSenses = indexWord.sortSenses();
				if (taggedSenses != 0) {
					for (int i = 0; i < taggedSenses; i++) {
						Synset synset = indexWord.getSenses().get(i);
						PointerTargetNodeList hyponyms = PointerUtils.getDirectHyponyms(synset);
						for (PointerTargetNode pointerTargetNode : hyponyms) {
							for (Word word : pointerTargetNode.getSynset().getWords()) {
								if (!hyponymResult.contains(word.getLemma())) {
									hyponymResult.add(word.getLemma());
								}
							}
						}
					}
				} else {
					for (Synset synset : indexWord.getSenses()) {
						PointerTargetNodeList hyponyms = PointerUtils.getDirectHyponyms(synset);
						for (PointerTargetNode pointerTargetNode : hyponyms) {
							for (Word word : pointerTargetNode.getSynset().getWords()) {
								if (!hyponymResult.contains(word.getLemma())) {
									hyponymResult.add(word.getLemma());
								}
							}
						}
					}
				}
			}
		} catch (JWNLException e) {
			e.printStackTrace();
		}

		return hyponymResult;
	}

	public static final List<String> getHolonyms(String name, POS pos, Dictionary dictionary) {
		List<String> holonymResult = new ArrayList<>();
		IndexWord indexWord;
		try {
			indexWord = dictionary.lookupIndexWord(pos, name);
			if (indexWord != null) {
				int taggedSenses = indexWord.sortSenses();
				if (taggedSenses != 0) {
					for (int i = 0; i < taggedSenses; i++) {
						Synset synset = indexWord.getSenses().get(i);
						PointerTargetNodeList holonyms = PointerUtils.getHolonyms(synset);
						for (PointerTargetNode pointerTargetNode : holonyms) {
							for (Word word : pointerTargetNode.getSynset().getWords()) {
								if (!holonymResult.contains(word.getLemma())) {
									holonymResult.add(word.getLemma());
								}
							}
						}
					}
				} else {
					for (Synset synset : indexWord.getSenses()) {
						PointerTargetNodeList holonyms = PointerUtils.getHolonyms(synset);
						for (PointerTargetNode pointerTargetNode : holonyms) {
							for (Word word : pointerTargetNode.getSynset().getWords()) {
								if (!holonymResult.contains(word.getLemma())) {
									holonymResult.add(word.getLemma());
								}
							}
						}
					}
				}

			}
		} catch (JWNLException e) {
			e.printStackTrace();
		}

		return holonymResult;
	}

	public static final List<String> getMeronyms(String name, POS pos, Dictionary dictionary) {
		List<String> meronymResult = new ArrayList<>();
		IndexWord indexWord;
		try {
			indexWord = dictionary.lookupIndexWord(pos, name);
			if (indexWord != null) {
				int taggedSenses = indexWord.sortSenses();
				if (taggedSenses != 0) {
					for (int i = 0; i < taggedSenses; i++) {
						Synset synset = indexWord.getSenses().get(i);
						PointerTargetNodeList meronyms = PointerUtils.getMeronyms(synset);
						for (PointerTargetNode pointerTargetNode : meronyms) {
							for (Word word : pointerTargetNode.getSynset().getWords()) {
								if (!meronymResult.contains(word.getLemma())) {
									meronymResult.add(word.getLemma());
								}
							}
						}
					}
				} else {
					for (Synset synset : indexWord.getSenses()) {
						PointerTargetNodeList meronyms = PointerUtils.getMeronyms(synset);
						for (PointerTargetNode pointerTargetNode : meronyms) {
							for (Word word : pointerTargetNode.getSynset().getWords()) {
								if (!meronymResult.contains(word.getLemma())) {
									meronymResult.add(word.getLemma());
								}
							}
						}
					}
				}
			}
		} catch (JWNLException e) {
			e.printStackTrace();
		}

		return meronymResult;
	}

	public static final List<String> getSynonyms(String name, POS pos, Dictionary dictionary) {
		List<String> synonyms = new ArrayList<>();
		try {
			IndexWord indexWord = dictionary.lookupIndexWord(pos, name);
			if (indexWord != null) {
				int taggedSenses = indexWord.sortSenses();
				if (taggedSenses != 0) {
					for (int i = 0; i < taggedSenses; i++) {
						Synset synset = indexWord.getSenses().get(i);
						for (Word word : synset.getWords()) {
							if (!word.getLemma().equalsIgnoreCase(name) && !word.getLemma().equals(indexWord.getLemma())) {
								if (!synonyms.contains(word.getLemma())) {
									synonyms.add(word.getLemma());
								}
							}
						}
					}
				} else {
					for (Synset synset : indexWord.getSenses()) {
						for (Word word : synset.getWords()) {
							if (!word.getLemma().equalsIgnoreCase(name) && !word.getLemma().equals(indexWord.getLemma())) {
								if (!synonyms.contains(word.getLemma())) {
									synonyms.add(word.getLemma());
								}
							}
						}
					}
				}
			}
		} catch (JWNLException e) {
			e.printStackTrace();
		}

		return synonyms;
	}

	public static final List<String> getSynonyms(IndexWord indexWord) {
		List<String> synonyms = new ArrayList<>();

		if (indexWord != null) {
			int taggedSenses = indexWord.sortSenses();
			if (taggedSenses != 0) {
				for (int i = 0; i < taggedSenses; i++) {
					Synset synset = indexWord.getSenses().get(i);
					for (Word word : synset.getWords()) {
						if (!word.getLemma().equals(indexWord.getLemma())) {
							if (!synonyms.contains(word.getLemma())) {
								synonyms.add(word.getLemma());
							}
						}
					}
				}
			} else {
				for (Synset synset : indexWord.getSenses()) {
					for (Word word : synset.getWords()) {
						if (!word.getLemma().equals(indexWord.getLemma())) {
							if (!synonyms.contains(word.getLemma())) {
								synonyms.add(word.getLemma());
							}
						}
					}
				}
			}
		}

		return synonyms;
	}

	public static final List<String> getAntonyms(String name, POS pos, Dictionary dictionary) {
		List<String> antonymResult = new ArrayList<>();
		IndexWord indexWord;
		try {
			indexWord = dictionary.lookupIndexWord(pos, name);
			if (indexWord != null) {
				int taggedSenses = indexWord.sortSenses();
				if (taggedSenses != 0) {
					for (int i = 0; i < taggedSenses; i++) {
						Synset synset = indexWord.getSenses().get(i);
						PointerTargetNodeList antonyms = PointerUtils.getAntonyms(synset);
						for (PointerTargetNode pointerTargetNode : antonyms) {
							for (Word word : pointerTargetNode.getSynset().getWords()) {
								if (!antonymResult.contains(word.getLemma())) {
									antonymResult.add(word.getLemma());
								}
							}
						}
					}
				} else {
					for (Synset synset : indexWord.getSenses()) {
						PointerTargetNodeList antonyms = PointerUtils.getAntonyms(synset);
						for (PointerTargetNode pointerTargetNode : antonyms) {
							for (Word word : pointerTargetNode.getSynset().getWords()) {
								if (!antonymResult.contains(word.getLemma())) {
									antonymResult.add(word.getLemma());
								}
							}
						}
					}
				}
			}
		} catch (JWNLException e) {
			e.printStackTrace();
		}

		return antonymResult;
	}

	public static final List<String> getAntonyms(IndexWord indexWord) {
		List<String> antonymResult = new ArrayList<>();
		try {
			if (indexWord != null) {
				int taggedSenses = indexWord.sortSenses();
				if (taggedSenses != 0) {
					for (int i = 0; i < taggedSenses; i++) {
						Synset synset = indexWord.getSenses().get(i);
						PointerTargetNodeList antonyms = PointerUtils.getAntonyms(synset);
						for (PointerTargetNode pointerTargetNode : antonyms) {
							for (Word word : pointerTargetNode.getSynset().getWords()) {
								if (!antonymResult.contains(word.getLemma())) {
									antonymResult.add(word.getLemma());
								}
							}
						}
					}
				} else {
					for (Synset synset : indexWord.getSenses()) {
						PointerTargetNodeList antonyms = PointerUtils.getAntonyms(synset);
						for (PointerTargetNode pointerTargetNode : antonyms) {
							for (Word word : pointerTargetNode.getSynset().getWords()) {
								if (!antonymResult.contains(word.getLemma())) {
									antonymResult.add(word.getLemma());
								}
							}
						}
					}
				}
			}
		} catch (JWNLException e) {
			e.printStackTrace();
		}

		return antonymResult;
	}

}
