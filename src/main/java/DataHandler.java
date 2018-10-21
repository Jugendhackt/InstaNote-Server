import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

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
            Log.success("Received the entities id");
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
                "  wd:"+entityId+" wdt:P1448 ?Name." +
                "  OPTIONAL { wd:"+entityId+" wdt:P1082 ?EinwohnerZahl. }" +
                "  OPTIONAL { wd:"+entityId+" wdt:P94 ?Landeswappen. }" +
                "  OPTIONAL { wd:"+entityId+" wdt:P242 ?Karte. }" +
                "  OPTIONAL { wd:"+entityId+" wdt:P18 ?Bild. }" +
                "  OPTIONAL { wd:"+entityId+" wdt:P625 ?Koordinaten. }" +
                "  OPTIONAL { wd:"+entityId+" wdt:P41 ?Flagge. }" +
                "  OPTIONAL { wd:"+entityId+" schema:description ?description." +
                "           FILTER(LANG(?description)=\"de\")}" +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"" + lang + "\". }" +
                "}";
        try {
            //Encode the query to be used in an url
            String queryEncoded = URLEncoder.encode(queryAufruf, "UTF-8");

            //Declare the scanner to later get data from wikidata
            Scanner scanner = new Scanner(new URL("https://query.wikidata.org/sparql?query=" + queryEncoded).openStream());

            //Get all the data to be stored in a StringBuilder
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            scanner.close();
            //return the data as a JSONObject
            String s = stringBuilder.toString();
            return XML.toJSONObject(s);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static JSONObject convertWikiData(JSONObject wikiData) {
        JSONObject result = wikiData.getJSONObject("sparql").getJSONObject("results");
        JSONObject resultObject = result.optJSONObject("result");
        if(resultObject == null){
            resultObject = result.getJSONArray("result").getJSONObject(0);
        }
        JSONArray results = resultObject.getJSONArray("binding");

        JSONObject resultsObject = new JSONObject();

        for (int i = 0; i < results.length(); i++) {
            JSONObject jsonObject = results.getJSONObject(i);

            String key = jsonObject.getString("name");
            String value;

            if(jsonObject.has("literal")) {
                value = String.valueOf(jsonObject.getJSONObject("literal").get("content"));
            } else {
                value = jsonObject.getString("uri");
            }

            resultsObject.put(key, value);
            Log.status("added "+key);
        }

        JSONObject newWikiDataset = new JSONObject();
        newWikiDataset.put("head", wikiData.getJSONObject("sparql").getJSONObject("head").getJSONArray("variable"));
        newWikiDataset.put("results", resultsObject);
				Log.success("formatting succesfull");
        return newWikiDataset;
    }
}
