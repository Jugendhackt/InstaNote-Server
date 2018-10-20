import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class DataHandler {
    public static void main(String[] args) {
        System.out.println(queryCall("Hamburg", "en"));
        System.out.println(args);
    }
    static JSONObject wikiData() {
        return null;
    }

    static String queryToEntity(String query, String lang) {
        try {
            Scanner scanner = new Scanner(new URL("https://www.wikidata.org/w/api.php?action=wbsearchentities&format=json&search=" + query + "&language=" + lang).openStream());
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            scanner.close();

            JSONArray blocksArray = new JSONArray();
            blocksArray.put("Fakten");
            blocksArray.put("Bild");
            blocksArray.put("Geschichte");

            JSONObject jsonObject = new JSONObject(stringBuilder.toString()).getJSONArray("search").getJSONObject(0);
            JSONObject object = new JSONObject();
            object.put("name", jsonObject.getString("label"));
            object.put("description", jsonObject.getString("description"));
            object.put("entityId", jsonObject.getString("id"));
            object.put("blocks", blocksArray);

            JSONObject resultobject = new JSONObject();
            resultobject.put("result", object);
            
            
            return object.get("entityId").toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject queryCall(String query, String lang){
        String entityId = queryToEntity(query, lang);
        try {
            Scanner scanner = new Scanner(new URL("https://query.wikidata.org/sparql?query=SELECT%20%3FName%20%3FEinwohnerZahl%20%3FLandeswappen%20%3FKarte%20%3FBild%20%3FKoordinaten%20%3FFlagge%20WHERE%20%7B%0A%20%20wd%3A"+
                    entityId+"%20wdt%3AP1448%20%3FName.%0A%20%20OPTIONAL%20%7Bwd%3A"+
                    entityId+"%20wdt%3AP1082%20%3FEinwohnerZahl.%7D%0A%20%20OPTIONAL%20%7Bwd%3A"+
                    entityId+"%20wdt%3AP94%20%3FLandeswappen.%7D%0A%20%20OPTIONAL%20%7Bwd%3A"+
                    entityId+"%20wdt%3AP242%20%3FKarte.%7D%0A%20%20OPTIONAL%20%7Bwd%3A"+
                    entityId+"%20wdt%3AP18%20%3FBild.%20%7D%20%20%20%20%20%20%20%20%20%20%20%20%20%0A%20%20OPTIONAL%20%7Bwd%3A"+
                    entityId+"%20wdt%3AP625%20%3FKoordinaten.%7D%0A%20%20OPTIONAL%20%7Bwd%3A"+
                    entityId+"%20wdt%3AP41%20%3FFlagge.%7D%0A%0ASERVICE%20wikibase%3Alabel%20%7B%20bd%3AserviceParam%20wikibase%3Alanguage%20%22%5BAUTO_LANGUAGE%5D%2Cen%22.%20%7D%0A%7D%0ALimit%201%0A&format=json").openStream());

            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            scanner.close();
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    return null;
    }

}
