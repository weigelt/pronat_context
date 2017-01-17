/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import net.sf.extjwnl.data.Synset;

/**
 * @author Tobias Hey
 *
 */
public final class WordNetInformationContent {

	private static double nounRootCount;

	private static final HashMap<Long, Double> informationContents;

	static {
		BufferedReader reader;
		informationContents = new HashMap<Long, Double>();

		try {
			reader = new BufferedReader(
					new InputStreamReader(WordNetInformationContent.class.getResourceAsStream("/ic-semcorraw-add1.dat"), "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] lineContent = line.split(" ");
				if (lineContent[0].endsWith("n")) {
					if (lineContent.length == 3) {
						nounRootCount = Double.parseDouble(lineContent[1]);
					}
					informationContents.put(Long.parseLong(lineContent[0].substring(0, lineContent[0].length() - 1)),
							Double.parseDouble(lineContent[1]));
				}

			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static final Double getRootCount() {
		return nounRootCount;
	}

	public static final Double getInformationContent(Synset synset) {
		Double result = null;
		result = informationContents.getOrDefault(synset.getOffset(), null);
		if (result != null) {
			result = result / nounRootCount;
			result = -Math.log(result);
		}
		return result;
	}

}
