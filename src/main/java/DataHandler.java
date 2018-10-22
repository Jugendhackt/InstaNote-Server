import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class DataHandler {
    static String getPresentationData(String keyword, String language) {
        String entityID = queryToEntity(keyword);
        JSONObject data = DataHandler.queryCall(entityID, language);
        
        if (data != null && entityID != null) {
            try {
                return DataHandler.convertWikiData(data).toString();
            } catch (JSONException e) {
                Log.error("Wikidata has no results".toUpperCase());
                return null;
            }
        }
        return null;
    }
    

    /**
     * @param query keyword for the wikidata query
     * @return entityID
     */
    private static String queryToEntity(String query) {
        try {
            //Get the data from the wikidata api using a search word and a language
            Scanner scanner = new Scanner(new URL("https://www.wikidata.org/w/api.php?action=wbsearchentities&format=json&uselang=en&search=" + query + "&language=en").openStream());
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            scanner.close();

            //Convert the data to a JSONObject and return the entities id
            try {
                String id = new JSONObject(stringBuilder.toString()).getJSONArray("search").getJSONObject(0).getString("id");
                Log.success("Received the entities id");
                return id;
            } catch (JSONException e) {
                Log.critical("No ID found".toUpperCase());
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject queryCall(String entityId, String lang){
        //Get the entities id based on a query
        try {
            String queryAufruf = "SELECT ?name ?inhabitants ?codeOfArms ?map ?picture ?coordinates ?flag ?area ?description WHERE {\n" +
                "  OPTIONAL { wd:"+ entityId +" wdt:P1082 ?inhabitants. }" +
                "  OPTIONAL { wd:"+ entityId +" wdt:P94 ?codeOfArms. }" +
                "  OPTIONAL { wd:"+ entityId +" wdt:P242 ?map. }" +
                "  OPTIONAL { wd:"+ entityId +" wdt:P18 ?picture. }" +
                "  OPTIONAL { wd:"+ entityId +" wdt:P625 ?coordinates. }" +
                "  OPTIONAL { wd:"+ entityId +" wdt:P41 ?flag. }" +
                "  OPTIONAL { wd:"+ entityId +" wdt:P2046 ?area }"+
                "  OPTIONAL { wd:"+ entityId +" schema:description ?description." +
                "           FILTER(LANG(?description)=\"" + lang + "\")}" +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"" + lang + "\". }" +
                "  wd:"+entityId+" wdt:P1448 ?name." +
                "}";
            //Encode the query to be used in an url
            String queryEncoded = URLEncoder.encode(queryAufruf, StandardCharsets.UTF_8);

            //Declare the scanner to later get data from wikidata
            Scanner scanner = new Scanner(new URL("https://query.wikidata.org/sparql?query=" + queryEncoded).openStream());

            //Get all the data to be stored in a StringBuilder
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            scanner.close();
            //return the data as a JSONObject
            return XML.toJSONObject(stringBuilder.toString());


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject convertWikiData(JSONObject wikiData) {
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
