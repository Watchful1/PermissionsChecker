package gr.watchful.permchecker.utils;

import gr.watchful.permchecker.datastructures.Globals;
import org.json.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Updater {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static String checkUpdate(String currentVersion) {
		String latestJSON = FileUtils.downloadToString(Globals.latestReleaseUrl);
		try {
			JSONObject latest = new JSONObject(latestJSON);
			String latestVersion = latest.getString("tag_name");

			if (versionCompare(currentVersion, latestVersion) < 0) {
				JSONArray assets = latest.getJSONArray("assets");
				for(int i=0; i<assets.length(); i++) {
					if (assets.getJSONObject(i).getString("name").equals("PermChecker.jar")) {
						return assets.getJSONObject(i).getString("browser_download_url");
					}
				}
			}
			return null;
		} catch (JSONException e) {
			LOGGER.warning("Couldn't parse json for update");
			return null;
		}
	}

	public static String getChangelog() {
		String latestJSON = FileUtils.downloadToString(Globals.latestReleaseUrl);
		try {
			JSONObject latest = new JSONObject(latestJSON);
			return latest.getString("body");
		} catch (JSONException e) {
			LOGGER.warning("Couldn't parse json for update");
			return "";
		}
	}

	public static Boolean startUpdate(String versionURL) {
		File currentJar = getCurrentJar();
		LOGGER.info("current Jar: "+currentJar.getAbsolutePath());
		File parentFolder = new File(currentJar.getParent());

		File newJar = new File(parentFolder+File.separator+"NewPermChecker.jar");
		LOGGER.info("new Jar: "+newJar.getAbsolutePath());
		try {
			FileUtils.downloadToFile(versionURL, newJar);
		} catch (IOException e) {
			LOGGER.severe("Couldn't download update: "+versionURL);
			return false;
		}

		File updaterJar = new File(parentFolder+File.separator+"UpdaterPermChecker.jar");
		LOGGER.info("updater Jar: "+updaterJar.getAbsolutePath());

		FileUtils.copyFile(newJar, updaterJar);
		LOGGER.info("Copied to updater");

		String[] run = {"java","-jar",updaterJar.getName(),"-u",currentJar.getAbsolutePath(),newJar.getAbsolutePath()};
		try {
			Runtime.getRuntime().exec(run);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
			LOGGER.severe("Could not run new process in start update");
		}
		System.exit(0);
		return true;
	}

	public static Boolean finishUpdate(String targetFileString, String sourceFileString) {
		File targetFile = new File(targetFileString);
		LOGGER.info("Target Jar: "+targetFile.getAbsolutePath());
		if (!targetFile.exists()) {
			LOGGER.warning("Updater, target file does not exist, aborting");
			return false;
		}

		File sourceFile = new File(sourceFileString);
		LOGGER.info("Source Jar: "+sourceFile.getAbsolutePath());
		if (!sourceFile.exists()) {
			LOGGER.warning("Updater, source file does not exist, aborting");
			return false;
		}

		targetFile.delete();
		LOGGER.info("Deleted target");
		sourceFile.renameTo(targetFile);
		LOGGER.info("Copied source");

		File updaterFile = getCurrentJar();
		LOGGER.info("Updater Jar: "+updaterFile.getAbsolutePath());

		String[] run = {"java","-jar",targetFile.getName(),"-c",sourceFile.getAbsolutePath(),updaterFile.getAbsolutePath()};
		try {
			Runtime.getRuntime().exec(run);
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
			LOGGER.severe("Could not run new process in finish update");
		}
		System.exit(0);
		return true;
	}

	public static void cleanup(String fileString1, String fileString2) {
		File file = new File(fileString1);
		if (!file.exists()) {
			LOGGER.info("file 1 does not exist");
		}
		file.delete();
		LOGGER.info("deleted 1");

		file = new File(fileString2);
		if (!file.exists()) {
			LOGGER.info("file 2 does not exist");
		}
		file.delete();
		LOGGER.info("deleted 2");
	}

	public static File getCurrentJar() {
		String path;
		try {
			path = Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			LOGGER.warning("Couldn't get current jar path");
			return null;
		}
		try {
			path = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.warning("Couldn't get current jar path");
			return null;
		}
		return new File(path);
	}

	public static Integer versionCompare(String str1, String str2)
	{
		String[] vals1 = str1.split("\\.");
		String[] vals2 = str2.split("\\.");
		int i = 0;
		while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i]))
		{
			i++;
		}
		if (i < vals1.length && i < vals2.length)
		{
			int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
			return Integer.signum(diff);
		}
		else
		{
			return Integer.signum(vals1.length - vals2.length);
		}
	}
}
