package edu.kit.ipd.parse.contextanalyzer.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity.Gender;

public class GenderNumberUtils {

	public static final Map<List<String>, Gender> genderNumber = new HashMap<List<String>, Gender>();

	/**
	 * Use serialized genderMap of StanfordCoreNLP-models version 3.9.2
	 *
	 */
	static {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new GZIPInputStream(GenderNumberUtils.class.getResourceAsStream("/gender/gender.map.ser.gz")));
			Map<List<String>, Gender> temp = (Map<List<String>, Gender>) ois.readObject();
			genderNumber.putAll(temp);
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
