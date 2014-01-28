/**
 * Uses: https://github.com/douglascrockford/JSON-java
 * @author bearbear12345
 */

package gr.watchful.permchecker.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;

public class SkydriveUtils {
    public static JSONArray getJSON(String url) throws Throwable {
        BufferedReader input = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
        System.out.println(input.readLine());
        JSONArray output = new JSONArray(input.readLine());
        return output;
    }
    
    public static String getAccessToken() throws Throwable {
        JSONArray array = getJSON("https://login.live.com/oauth20_token.srf?client_id=000000004410FE50&redirect_uri=https://login.live.com/oauth20_desktop.srf&grant_type=refresh_token&refresh_token=Chf9!6iNyOsxUtX2uCMG*SKiPuyCsVNuof8bK7avToNEtCbfzYspPLEbuRXdxwjOd8CFO7BpgmyJmVDUnCqZrT6eJgtZ7mCZgkpBUiLRFm8fLHzD2tbYyn!fhJ0I7Da7i!CG05xN8ZfAc*0cOo02bsqkfq!nak!fKtRfOUal1nHjMYdkWPnTQ8a86UxYNm0nJvEvAahJoayNzJ5tvSdsD0Ar8uauOmMyixRiXkoGUvxViQlBfJYeKeifBR1uZkb5f!*JLMA5!zUxNxES9ahzYR!MATG!tnqWtZLzWCYcESEo73YtjVcNAUnf26Ad0SWunHY1C*awrgf7OgwVbiruORR9pyZ*3QcXpK5lpMDCIsAK");
        String token = array.getString(array.length() - 1);
        return token;
    }
}
