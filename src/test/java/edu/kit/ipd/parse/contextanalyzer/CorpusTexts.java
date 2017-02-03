/**
 * 
 */
package edu.kit.ipd.parse.contextanalyzer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.kit.ipd.parse.luna.graph.Pair;

/**
 * @author Tobias Hey
 *
 */
public final class CorpusTexts {

	public static HashMap<String, String> texts;

	public static HashMap<String, List<Pair<String, String>>> evalTexts;

	static {
		texts = new HashMap<String, String>();
		evalTexts = new HashMap<>();
		try {
			File file = new File(CorpusTexts.class.getResource("/korpus.xml").toURI());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			NodeList nl = doc.getElementsByTagName("text");
			for (int i = 0; i < nl.getLength(); i++) {
				Element node = (Element) nl.item(i);
				String name = node.getAttribute("name");
				String text = node.getTextContent();
				texts.put(name, text);
			}

			file = new File(CorpusTexts.class.getResource("/korpusEval.xml").toURI());
			doc = dBuilder.parse(file);
			nl = doc.getElementsByTagName("text");
			for (int i = 0; i < nl.getLength(); i++) {
				Element node = (Element) nl.item(i);
				String name = node.getAttribute("name");
				String text = node.getTextContent();
				text = text.trim();
				List<Pair<String, String>> textList = new ArrayList<>();
				String[] tokens = text.split(" ");
				String last = "";
				for (int j = 0; j < tokens.length; j++) {
					if (tokens[j].startsWith("[")) {
						textList.add(new Pair<String, String>(last, tokens[j]));
					} else if (j + 1 < tokens.length) {
						if (!tokens[j + 1].startsWith("[")) {
							textList.add(new Pair<String, String>(tokens[j], null));
						}
					} else {
						textList.add(new Pair<String, String>(tokens[j], null));
					}

					last = tokens[j];
				}
				NodeList otherConceptsList = node.getElementsByTagName("otherConcepts");
				for (int j = 0; j < otherConceptsList.getLength(); j++) {
					Element otherConcepts = (Element) otherConceptsList.item(j);
					String string = otherConcepts.getAttribute("annotation");
					textList.add(new Pair<String, String>(null, string));
				}

				evalTexts.put(name, textList);
			}
		} catch (URISyntaxException e) {

			e.printStackTrace();
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		} catch (SAXException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
