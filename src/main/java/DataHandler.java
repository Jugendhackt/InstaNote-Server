import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class DataHandler {
    public static void main(String[] args) {
        System.out.println(queryToEntity("Hamburg", "en"));
        System.out.println(getEntityID("Hamburg", "de"));
    }
    static JSONObject wikiData() {
        return null;
    }
    
    static String getEntityID(String query, String lang) {
        JSONObject jsonObject = (JSONObject) queryToEntity(query, lang).get("result");
        return jsonObject.get("entityId").toString();
    }

    static JSONObject queryToEntity(String query, String lang) {
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
            return resultobject;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
