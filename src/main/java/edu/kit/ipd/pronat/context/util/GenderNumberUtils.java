package edu.kit.ipd.pronat.context.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class GenderNumberUtils {

	/**
	 * map containing names and their respective gender as MALE, FEMALE, NEUTRAL or
	 * UNKNOWN
	 */
	public static final Map<List<String>, Object> genderNumber = new HashMap<List<String>, Object>();

	/**
	 * Use serialized genderMap of StanfordCoreNLP-models version 3.9.2
	 *
	 */
	static {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new GZIPInputStream(GenderNumberUtils.class.getResourceAsStream("/gender/gender.map.ser.gz")));
			Map<List<String>, Object> temp = (Map<List<String>, Object>) ois.readObject();
			genderNumber.putAll(temp);
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
