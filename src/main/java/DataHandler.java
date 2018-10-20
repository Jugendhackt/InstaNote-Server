import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
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
        try {
            //Declare a Scanner to get data using a sparql query
            Scanner scanner = new Scanner(new URL("https://query.wikidata.org/sparql?query=SELECT%20%3FName%20%3FEinwohnerZahl%20%3FLandeswappen%20%3FKarte%20%3FBild%20%3FKoordinaten%20%3FFlagge%20WHERE%20%7B%0A%20%20wd%3A"+
                    entityId+"%20wdt%3AP1448%20%3FName.%0A%20%20OPTIONAL%20%7Bwd%3A"+
                    entityId+"%20wdt%3AP1082%20%3FEinwohnerZahl.%7D%0A%20%20OPTIONAL%20%7Bwd%3A"+
                    entityId+"%20wdt%3AP94%20%3FLandeswappen.%7D%0A%20%20OPTIONAL%20%7Bwd%3A"+
                    entityId+"%20wdt%3AP242%20%3FKarte.%7D%0A%20%20OPTIONAL%20%7Bwd%3A"+
                    entityId+"%20wdt%3AP18%20%3FBild.%20%7D%20%20%20%20%20%20%20%20%20%20%20%20%20%0A%20%20OPTIONAL%20%7Bwd%3A"+
                    entityId+"%20wdt%3AP625%20%3FKoordinaten.%7D%0A%20%20OPTIONAL%20%7Bwd%3A"+
                    entityId+"%20wdt%3AP41%20%3FFlagge.%7D%0A%0ASERVICE%20wikibase%3Alabel%20%7B%20bd%3AserviceParam%20wikibase%3Alanguage%20%22%5BAUTO_LANGUAGE%5D%2Cen%22.%20%7D%0A%7D%0ALimit%201%0A&format=json").openStream());

            //Get all the data to be stored in a StringBuilder
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            scanner.close();

            //Return the data as a JSONObject
            return new JSONObject(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
