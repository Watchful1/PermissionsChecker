package gr.watchful.permchecker.datastructures;

import gr.watchful.permchecker.modhandling.ModNameRegistry;
import gr.watchful.permchecker.utils.FileUtils;
import gr.watchful.permchecker.utils.OsTypes;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Globals {
	private static volatile Globals instance = null;

	public ModNameRegistry nameRegistry;
	public Preferences preferences;
	public File appStore;
    public File permFile;
    public File jsonFile;
	public RebuildsMods rebuildsMods;
	public ListsPacks listsPacks;
	public JFrame mainFrame;
	private ModPack modpack;
	private ArrayList<UsesPack> packListeners;
	public boolean changeFlag;
    public boolean oldVersionsFlag;

    public static final String permUrl = "https://onedrive.live.com/download?resid=96628E67B4C51B81!161&ithint=" +
            "file%2c.xlsx&app=Excel&authkey=!APQ4QtFrBqa1HwM";
    public static final String jsonUrl = "http://www.feed-the-beast.com/mods/json";
	public static final String forgeUrl = "http://api.feed-the-beast.com/ss/api/GetForgePackJSON/";
    public static final String ftbRepoUrl = "http://ftb.cursecdn.com/FTB2/";
	public static final String[] modTypes = {"jar", "zip", "disabled", "litemod", "class"};
    public static final int metaVersion = 1;
    public static final String curseFileName = "curseKeys.json";
    public static final String curseProjectRoot = "http://minecraft.curseforge.com/modpacks/";


	public Globals() {
		nameRegistry = new ModNameRegistry();
		packListeners = new ArrayList<>();
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
        if (!jsonFile.exists()) {
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Could not create Permissions.json");
                return false;
            }
        }

        try {
            FileUtils.downloadToFile(new URL(jsonUrl), jsonFile);
        } catch (IOException e) {
            System.out.println("Could not download perm file");
            return false;
        }

        ModInfo[] modInfos;
        try {
            modInfos = (ModInfo[]) FileUtils.readObject(jsonFile, new ModInfo[1]);
        } catch (Exception e) {
            System.out.println("Unable to parse permissions json");
            return false;
        }
        nameRegistry.loadMappings(modInfos, "https://dl.dropboxusercontent.com/u/27836116/FTBPermissionsImages/", "png");

		return true;
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
