package gr.watchful.permchecker.datastructures;

import gr.watchful.permchecker.modhandling.ModNameRegistry;
import gr.watchful.permchecker.utils.FileUtils;
import gr.watchful.permchecker.utils.OsTypes;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class Globals {
	private static volatile Globals instance = null;

	public ModNameRegistry nameRegistry;
	public Preferences preferences;
	public File appStore;
	public File jsonFile;
	public File jsonCacheFile;
	public RebuildsMods rebuildsMods;
	public ListsPacks listsPacks;
	public JFrame mainFrame;
	private ModPack modpack;
	private ArrayList<UsesPack> packListeners;
	public boolean changeFlag;
	public boolean oldVersionsFlag;
	public ArrayList<String> filesToReplaceServer;
	public ArrayList<String> filesToDeleteServer;
	public ArrayList<String> filesToReplaceClient;

	public static final String jsonUrl = "https://api.feed-the-beast.com/ss/api/modpermissions/";
	public static final String forgeUrl = "https://api.feed-the-beast.com/ss/api/GetForgePackJSON/";
	public static final String ftbRepoUrl = "https://ftb.forgecdn.net/FTB2/";
	public static final String[] modTypes = {"jar", "zip", "disabled", "litemod", "class"};
	public static final int metaVersion = 1;
	public static final String curseFileName = "curseKeys.json";
	public static final String curseProjectRoot = "https://minecraft.curseforge.com/modpacks/";
	public static final String version = "1.3.7";
	public static final String latestReleaseUrl = "https://api.github.com/repos/Watchful1/PermissionsChecker/releases/latest";
	public static final String serverCommitsUrl = "https://api.github.com/repos/FeedTheBeast/FTBServerBase/commits";
	public static final String serverBaseUrl = "https://github.com/FeedTheBeast/FTBServerBase/archive/master.zip";
	public static final String forgeUniversalUrl = "https://api.feed-the-beast.com/ss/api/GetForgeJar/";
	public static final String[] serverMinecraftVersions = {"1.10.2","1.12","1.12.1","1.12.2"};

	public Globals() {
		nameRegistry = new ModNameRegistry();
		packListeners = new ArrayList<>();

		filesToReplaceServer = new ArrayList<>();
		filesToReplaceServer.add("settings.bat");
		filesToReplaceServer.add("settings.sh");
		filesToReplaceServer.add("version.json");

		filesToDeleteServer = new ArrayList<>();
		filesToDeleteServer.add("animation");
		filesToDeleteServer.add("resources");

		filesToReplaceClient = new ArrayList<>();
		filesToReplaceClient.add("config/Mercurious.cfg");
	}

	public static Globals getInstance() {
		if (instance == null) {
			synchronized (Globals.class) {
				if (instance == null) {
					instance = new Globals();
				}
			}
		}
		return instance;
	}

	public boolean initializeFolders() {
		switch (OsTypes.getOperatingSystemType()) {
			case Windows:
				appStore = new File(System.getenv("APPDATA") + File.separator + "PermissionsChecker");
				break;
			case MacOS:
				appStore = new File(System.getProperty("user.home") + File.separator + "Library/Application Support/PermissionsChecker");
				break;
			case Linux:
				appStore = new File(System.getProperty("user.home") + File.separator + ".permissionsChecker");
				break;
			case Other:
				appStore = new File(".permissionsChecker");
				break;
		}
		if (!appStore.exists()) {
			boolean result = appStore.mkdirs();
			if (!result) {
				System.out.println(Globals.getInstance().appStore.getPath() + " could not be created!");
				return false;
			}
		}
		return true;
	}

	public void savePreferences() {
		if (appStore == null && !initializeFolders()) {
			System.out.println("Can't save prefs as appStore could not be created");
			return;
		}
		System.out.println("Saving prefs");
		FileUtils.saveObject(Globals.getInstance().preferences, new File(appStore +
				File.separator + "preferences.conf"));
	}

	public void loadPreferences() {
		if (appStore == null && !initializeFolders()) {
			System.out.println("Can't load prefs as appStore could not be created");
			return;
		}
		File prefFile = new File(appStore + File.separator + "preferences.conf");
		System.out.println(prefFile.getAbsolutePath());
		if (prefFile.exists()) {
			preferences = (Preferences) FileUtils.readObject(new File(appStore +
					File.separator + "preferences.conf"), new Preferences(appStore));
			preferences.init(appStore);
		} else {
			preferences = new Preferences(appStore);
			savePreferences();
		}
	}

	public boolean updateListings() {
		jsonFile = new File(appStore.getPath() + File.separator + "Permissions.json");
		jsonCacheFile = new File(appStore.getPath() + File.separator + "PermissionsCache.json");
		ModInfo[] modInfos = null;//loadListingsFromSite(jsonFile);
		if (modInfos == null) modInfos = loadListingsFromCache(jsonCacheFile);
		if (modInfos == null) return false;
		nameRegistry.loadMappings(modInfos, "https://dl.dropboxusercontent.com/u/27836116/FTBPermissionsImages/", "png");

		FileUtils.copyFile(jsonFile, jsonCacheFile, true);

		return true;
	}

	private ModInfo[] loadListingsFromSite(File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("Could not create Permissions.json");
				return null;
			}
		}

		try {
			FileUtils.downloadToFile(jsonUrl, file);
		} catch (IOException | URISyntaxException e) {
			System.out.println("Could not download perm file");
			return null;
		}

		ModInfo[] modInfos;
		try {
			modInfos = (ModInfo[]) FileUtils.readObject(file, new ModInfo[1]);
		} catch (Exception e) {
			System.out.println("Unable to parse permissions json");
			return null;
		}
		return modInfos;
	}

	private ModInfo[] loadListingsFromCache(File file) {
		if (!file.exists()) return null;

		ModInfo[] modInfos;
		try {
			modInfos = (ModInfo[]) FileUtils.readObject(file, new ModInfo[1]);
		} catch (Exception e) {
			System.out.println("Unable to parse cached permissions json");
			return null;
		}
		return modInfos;
	}

	public void addListener(UsesPack usesPack) {
		packListeners.add(usesPack);
	}

	public static ModPack getModPack() {
		return getInstance().modpack;
	}

	public static void setModPack(ModPack packIn) {
		saveCurrentPack();
		getInstance().modpack = packIn;
		modPackChanged(null, false);
	}

	public static void modPackChanged(UsesPack source, boolean dirty) {
		getModPack().dirty = dirty;
		for (UsesPack usesPack : getInstance().packListeners) {
			if (source == null || !usesPack.equals(source)) usesPack.updatePack(getModPack());
		}
	}

	public static void saveCurrentPack() {
		if(getModPack() != null && getModPack().dirty) {
			System.out.println("Saving "+getModPack().shortName+" to disk");
			getInstance().changeFlag = false;
			if(!getModPack().saveThisObject()) {
				System.out.println("Save failed");
				return;
			}
			if(getInstance().changeFlag) {
				modPackChanged(null, true);
			}
			getInstance().changeFlag = false;
			getModPack().dirty = false;
		}
	}
}
