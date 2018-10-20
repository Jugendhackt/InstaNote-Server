import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class DataHandler {
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

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            return jsonObject.getJSONArray("search").getJSONObject(0).getString("id");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
