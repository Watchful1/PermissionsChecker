package gr.watchful.permchecker.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class JSONUtils {
	public static String getJSON(Object object) {
		Gson gson = new Gson();
		return gson.toJson(object);
	}

	public static Object getObject(String JSON, Object object) {
		Gson gson = new Gson();
		Object tempObject;
		try {
			tempObject = gson.fromJson(JSON, object.getClass());
		} catch (JsonSyntaxException excp) {
			return null;
		}
		return tempObject;
	}

	// bearbear12345
	// Used for checking modified date
	public static String getKey(String key, String url) {
		return new Gson().fromJson(URLToString(url), JsonObject.class).get(key)
				.getAsString();
	}

	public static String URLToString(String url) {
		StringBuilder responseStrBuilder = new StringBuilder();
		String line;
		try {
			BufferedReader streamReader = new BufferedReader(
					new InputStreamReader(new URL(url).openConnection()
							.getInputStream(), "UTF-8"));
			while ((line = streamReader.readLine()) != null) {
				responseStrBuilder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseStrBuilder.toString();
	}
}
