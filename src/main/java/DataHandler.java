import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

class DataHandler {

    /**
     * @param query keyword for the wikidata query
     * @param lang  language for wikidata dump
     * @return entityID
     */
    private static String queryToEntity(String query, String lang) {
        try {
            //Get the data from the wikidata api using a search word and a language
            Scanner scanner = new Scanner(new URL("https://www.wikidata.org/w/api.php?action=wbsearchentities&format=json&uselang=" + lang + "&search=" + query + "&language=" + lang).openStream());
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            scanner.close();

            //Convert the data to a JSONObject and return the entities id
            JSONObject jsonObject = new JSONObject(stringBuilder.toString()).getJSONArray("search").getJSONObject(0);
            return jsonObject.getString("id");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static JSONObject queryCall(String query, String lang){
        //Get the entities id based on a query
        String entityId = queryToEntity(query, lang);
        String queryAufruf = "SELECT ?Name ?EinwohnerZahl ?Landeswappen ?Karte ?Bild ?Koordinaten ?Flagge ?description WHERE {\n" +
                "  wd:"+entityId+" wdt:P1448 ?Name.\n" +
                "  OPTIONAL { wd:"+entityId+" wdt:P1082 ?EinwohnerZahl. }\n" +
                "  OPTIONAL { wd:"+entityId+" wdt:P94 ?Landeswappen. }\n" +
                "  OPTIONAL { wd:"+entityId+" wdt:P242 ?Karte. }\n" +
                "  OPTIONAL { wd:"+entityId+" wdt:P18 ?Bild. }\n" +
                "  OPTIONAL { wd:"+entityId+" wdt:P625 ?Koordinaten. }\n" +
                "  OPTIONAL { wd:"+entityId+" wdt:P41 ?Flagge. }\n" +
                "  OPTIONAL { wd:"+entityId+" schema:description ?description.\n" +
                "           FILTER(LANG(?description)=\"de\")}\n" +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\". }\n" +
                "}";
        try {
            String queryAufrufEncoded = URLEncoder.encode(queryAufruf, "UTF-8");

            Scanner scanner = new Scanner(new URL(queryAufrufEncoded).openStream());

            //Get all the data to be stored in a StringBuilder
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            scanner.close();
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            //Return the data as a JSONObject
            return new JSONObject(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
