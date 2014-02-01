/**
 * Uses: https://github.com/douglascrockford/JSON-java
 * @author bearbear12345
 */

package gr.watchful.permchecker.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;

public class SkydriveUtils {
    public static JSONObject getJSON(String url) throws Throwable {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream(), "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String line;
        while ((line = streamReader.readLine()) != null) {
            responseStrBuilder.append(line);
        }
        JSONObject result = new JSONObject(responseStrBuilder.toString());
        return result;
    }

    public static String getKey(String key, String url) {
        try {
            JSONObject jsonresult = getJSON(url);
            String result = jsonresult.getString(key);
            return result;
        } catch (Throwable e) {
            return e.toString();
        }
    }
}
