/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
			return null;
		}

	}

	public static final LeastCommonSubsumer getLeastCommonSubsumer(IndexWord current, IndexWord candidate, Set<String> wnSynsets,
			Set<String> wnSynsetsCandidate, Dictionary dictionary) {
		LeastCommonSubsumer result = null;
		double icMax = 0.0;
		List<Synset> currSynsets = new ArrayList<>();
		List<Synset> candSynsets = new ArrayList<>();
		if (!wnSynsets.isEmpty()) {
			for (String synset : wnSynsets) {
				Synset syn = getSynsetForID(synset, dictionary);
				if (syn != null) {
					currSynsets.add(syn);
				}
			}
		} else {
			currSynsets.addAll(current.getSenses());
		}

		if (!wnSynsetsCandidate.isEmpty()) {
			for (String synset : wnSynsetsCandidate) {
				Synset syn = getSynsetForID(synset, dictionary);
				if (syn != null) {
					candSynsets.add(syn);
				}
			}
		} else {
			candSynsets.addAll(candidate.getSenses());
		}
		for (Synset currSynset : currSynsets) {
			for (Synset candSynset : candSynsets) {
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

	public static final Set<LeastCommonSubsumer> getLeastCommonSubsumers(IndexWord current, IndexWord candidate, Set<String> wnSynsets,
			Set<String> wnSynsetsCandidate, Dictionary dictionary) {
		HashSet<LeastCommonSubsumer> result = new HashSet<>();

		List<Synset> currSynsets = new ArrayList<>();
		List<Synset> candSynsets = new ArrayList<>();
		if (!wnSynsets.isEmpty()) {
			for (String synset : wnSynsets) {
				Synset syn = getSynsetForID(synset, dictionary);
				if (syn != null) {
					currSynsets.add(syn);
				}
			}
		} else {
			currSynsets.addAll(current.getSenses());
		}

		if (!wnSynsetsCandidate.isEmpty()) {
			for (String synset : wnSynsetsCandidate) {
				Synset syn = getSynsetForID(synset, dictionary);
				if (syn != null) {
					candSynsets.add(syn);
				}
			}
		} else {
			candSynsets.addAll(candidate.getSenses());
		}
		for (Synset currSynset : currSynsets) {
			for (Synset candSynset : candSynsets) {

				RelationshipList list;
				try {
					list = RelationshipFinder.findRelationships(currSynset, candSynset, PointerType.HYPERNYM);
					for (Relationship relationship : list) {
						if (relationship instanceof AsymmetricRelationship) {
							AsymmetricRelationship asym = (AsymmetricRelationship) relationship;

							result.add(new LeastCommonSubsumer(currSynset, candSynset,
									asym.getNodeList().get(asym.getCommonParentIndex()).getSynset()));

						}
					}
				} catch (CloneNotSupportedException | JWNLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		return result;

	}

	public static final Double calculateWUPSimilarity(Synset start, Synset end, Synset lcs, Dictionary dictionary) {
		int startDepth = getDepth(start, dictionary);
		int endDepth = getDepth(end, dictionary);
		int lcsDepth = getDepth(lcs, dictionary);
		if (startDepth == -1 || endDepth == -1 || lcsDepth == -1) {
			return 0.0;
		} else {
			return (double) 2 * lcsDepth / (double) (startDepth + endDepth);
		}
	}

	private static final int getDepth(Synset source, Dictionary dictionary) {
		Synset target;
		try {
			target = dictionary.getWordBySenseKey("entity%1:03:00::").getSynset();
			RelationshipList rl = RelationshipFinder.findRelationships(source, target, PointerType.HYPERNYM);
			return rl.getDeepest().getDepth();
		} catch (JWNLException | CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}

	public static final boolean isNotSpecificEnough(Synset leastCommonSubsumer, Dictionary dictionary) {
		if (getDepth(leastCommonSubsumer, dictionary) > 6) {
			return false;
			//			Synset artifact;
			//			try {
			//
			//				artifact = dictionary.getWordBySenseKey("artifact%1:03:00::").getSynset();
			//				if (PointerUtils.getHypernymTree(leastCommonSubsumer).findFirst(artifact) != null) {
			//					if (getDepth(leastCommonSubsumer, dictionary) > 6) {
			//						return false;
			//					}
			//				} else {
			//					return false;
			//				}
			//			} catch (JWNLException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}

		}
		return true;
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

	public static final List<String> getDirectHypernyms(String name, POS pos, Dictionary dictionary, String wnSynset) {
		List<String> hypernymResult = new ArrayList<>();
		IndexWord indexWord;
		Synset synset;
		try {
			indexWord = dictionary.lookupIndexWord(pos, name);
			if (indexWord != null) {
				synset = getSynsetForIDWithIW(indexWord, wnSynset);
			} else {
				synset = getSynsetForID(wnSynset, dictionary);
			}

			if (synset != null) {
				PointerTargetNodeList hypernyms = PointerUtils.getDirectHypernyms(synset);
				for (PointerTargetNode pointerTargetNode : hypernyms) {
					for (Word word : pointerTargetNode.getSynset().getWords()) {
						if (!hypernymResult.contains(word.getLemma())) {
							hypernymResult.add(word.getLemma());
						}
					}
				}
			} else {
				return getDirectHypernyms(name, pos, dictionary);
			}
		} catch (JWNLException e) {
			return new ArrayList<String>();
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
			return new ArrayList<String>();
		}

		return hyponymResult;
	}

	public static final List<String> getDirectHyponyms(String name, POS pos, Dictionary dictionary, String wnSynset) {
		List<String> hyponymResult = new ArrayList<>();
		IndexWord indexWord;
		Synset synset;
		try {
			indexWord = dictionary.lookupIndexWord(pos, name);
			if (indexWord != null) {
				synset = getSynsetForIDWithIW(indexWord, wnSynset);
			} else {
				synset = getSynsetForID(wnSynset, dictionary);
			}
			if (synset != null) {
				PointerTargetNodeList hyponyms = PointerUtils.getDirectHyponyms(synset);
				for (PointerTargetNode pointerTargetNode : hyponyms) {
					for (Word word : pointerTargetNode.getSynset().getWords()) {
						if (!hyponymResult.contains(word.getLemma())) {
							hyponymResult.add(word.getLemma());
						}
					}
				}
			} else {
				return getDirectHyponyms(name, pos, dictionary);
			}

		} catch (JWNLException e) {
			return new ArrayList<String>();
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
			return new ArrayList<String>();
		}

		return holonymResult;
	}

	public static final List<String> getHolonyms(String name, POS pos, Dictionary dictionary, String wnSynset) {
		List<String> holonymResult = new ArrayList<>();
		IndexWord indexWord;
		Synset synset;
		try {
			indexWord = dictionary.lookupIndexWord(pos, name);
			if (indexWord != null) {
				synset = getSynsetForIDWithIW(indexWord, wnSynset);
			} else {
				synset = getSynsetForID(wnSynset, dictionary);
			}
			if (synset != null) {
				PointerTargetNodeList holonyms = PointerUtils.getHolonyms(synset);
				for (PointerTargetNode pointerTargetNode : holonyms) {
					for (Word word : pointerTargetNode.getSynset().getWords()) {
						if (!holonymResult.contains(word.getLemma())) {
							holonymResult.add(word.getLemma());
						}
					}
				}
			} else {
				return getHolonyms(name, pos, dictionary);
			}

		} catch (JWNLException e) {
			return new ArrayList<String>();
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
			return new ArrayList<String>();
		}

		return meronymResult;
	}

	public static final List<String> getMeronyms(String name, POS pos, Dictionary dictionary, String wnSynset) {
		List<String> meronymResult = new ArrayList<>();
		IndexWord indexWord;
		Synset synset;
		try {
			indexWord = dictionary.lookupIndexWord(pos, name);
			if (indexWord != null) {
				synset = getSynsetForIDWithIW(indexWord, wnSynset);
			} else {
				synset = getSynsetForID(wnSynset, dictionary);
			}
			if (synset != null) {
				PointerTargetNodeList meronyms = PointerUtils.getMeronyms(synset);
				for (PointerTargetNode pointerTargetNode : meronyms) {
					for (Word word : pointerTargetNode.getSynset().getWords()) {
						if (!meronymResult.contains(word.getLemma())) {
							meronymResult.add(word.getLemma());
						}
					}
				}
			} else {
				return getMeronyms(name, pos, dictionary);
			}

		} catch (JWNLException e) {
			return new ArrayList<String>();
		}

		return meronymResult;
	}

	private static Synset getSynsetForIDWithIW(IndexWord indexWord, String wnSynset) {
		if (wnSynset != null)
			for (Synset synset : indexWord.getSenses()) {
				long wnSet = Long.valueOf(wnSynset.substring(3, wnSynset.length() - 1));
				if (synset.getOffset() == wnSet) {
					return synset;
				}
			}
		return null;
	}

	private static Synset getSynsetForID(String wnSynset, Dictionary dictionary) {
		if (wnSynset != null && !wnSynset.equals("")) {
			long wnSet = Long.valueOf(wnSynset.substring(3, wnSynset.length() - 1));
			POS pos;
			if (wnSynset.endsWith("n")) {
				pos = POS.NOUN;
			} else if (wnSynset.endsWith("a")) {
				pos = POS.ADJECTIVE;
			} else if (wnSynset.endsWith("v")) {
				pos = POS.VERB;
			} else {
				pos = POS.ADVERB;
			}
			try {
				return dictionary.getSynsetAt(pos, wnSet);
			} catch (JWNLException e) {
				return null;
			}

		}

		return null;
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
			return new ArrayList<String>();
		}

		return synonyms;
	}

	public static final List<String> getSynonyms(String name, POS pos, Dictionary dictionary, String wnSynset) {
		List<String> synonyms = new ArrayList<>();
		Synset syn;
		try {
			IndexWord indexWord = dictionary.lookupIndexWord(pos, name);
			if (indexWord != null) {
				syn = getSynsetForIDWithIW(indexWord, wnSynset);
			} else {
				syn = getSynsetForID(wnSynset, dictionary);
			}
			if (syn != null) {
				for (Word word : syn.getWords()) {
					if (!word.getLemma().equalsIgnoreCase(name) && !word.getLemma().equals(indexWord.getLemma())) {
						if (!synonyms.contains(word.getLemma())) {
							synonyms.add(word.getLemma());
						}
					}
				}
			} else {
				return getSynonyms(name, pos, dictionary);
			}

		} catch (JWNLException e) {
			return new ArrayList<String>();
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
			return new ArrayList<String>();
		}

		return antonymResult;
	}

	public static final List<String> getAntonyms(String name, POS pos, Dictionary dictionary, String wnSynset) {
		List<String> antonymResult = new ArrayList<>();
		IndexWord indexWord;
		Synset synset;
		try {
			indexWord = dictionary.lookupIndexWord(pos, name);
			if (indexWord != null) {
				synset = getSynsetForIDWithIW(indexWord, wnSynset);
			} else {
				synset = getSynsetForID(wnSynset, dictionary);
			}
			if (synset != null) {
				PointerTargetNodeList antonyms = PointerUtils.getAntonyms(synset);
				for (PointerTargetNode pointerTargetNode : antonyms) {
					for (Word word : pointerTargetNode.getSynset().getWords()) {
						if (!antonymResult.contains(word.getLemma())) {
							antonymResult.add(word.getLemma());
						}
					}
				}
			} else {
				return getAntonyms(name, pos, dictionary);
			}

		} catch (JWNLException e) {
			return new ArrayList<String>();
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
			return new ArrayList<String>();
		}

		return antonymResult;
	}

}
