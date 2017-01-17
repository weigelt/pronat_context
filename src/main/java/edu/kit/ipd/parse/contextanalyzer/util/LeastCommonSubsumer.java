/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.util;

import java.util.Comparator;
import java.util.List;

import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;

/**
 * @author Tobias Hey
 *
 */
public class LeastCommonSubsumer {

	private Synset synsetOne;
	private Synset synsetTwo;
	private Synset leastCommonSubsumer;
	private String name = "";

	/**
	 * 
	 */
	public LeastCommonSubsumer(Synset synsetOne, Synset synsetTwo, Synset leastCommonSubsumer) {
		this.synsetOne = synsetOne;
		this.synsetTwo = synsetTwo;
		this.leastCommonSubsumer = leastCommonSubsumer;
		this.name = calcName();
	}

	public Synset getSynsetOne() {
		return synsetOne;
	}

	public void setSynsetOne(Synset synsetOne) {
		this.synsetOne = synsetOne;
	}

	public Synset getSynsetTwo() {
		return synsetTwo;
	}

	public void setSynsetTwo(Synset synsetTwo) {
		this.synsetTwo = synsetTwo;
	}

	public Synset getLeastCommonSubsumer() {
		return leastCommonSubsumer;
	}

	public void setLeastCommonSubsumer(Synset leastCommonSubsumer) {
		this.leastCommonSubsumer = leastCommonSubsumer;
	}

	public String getName() {

		return this.name;

	}

	private String calcName() {
		List<Word> words = leastCommonSubsumer.getWords();
		words.sort(new UseCountComparer());
		return words.get(0).getLemma();
	}

	private class UseCountComparer implements Comparator<Word> {

		@Override
		public int compare(Word o1, Word o2) {

			return Integer.compare(o1.getUseCount(), o2.getUseCount());
		}

	}

	@Override
	public String toString() {

		return getName() + "[" + getSynsetOne().getWords().get(0).getLemma() + "|" + getSynsetTwo().getWords().get(0).getLemma() + "]";
	}

}
