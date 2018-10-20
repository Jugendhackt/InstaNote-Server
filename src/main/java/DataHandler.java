import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Scanner;

public class DataHandler {
    static JSONObject wikiData() {
        return null;
    }

    static String queryToEntity(String query) {
        try {
            Scanner scanner = new Scanner(new URL("https://query.wikidata.org/sparql?query=" + query).openStream());
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            scanner.close();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(stringBuilder.toString())));

            return document.getElementsByTagName("uri").item(0).getTextContent().split("entity/")[1];

        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }
}
