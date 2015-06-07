package gr.watchful.permchecker.datastructures;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import gr.watchful.permchecker.modhandling.ModNameRegistry;
import gr.watchful.permchecker.utils.ExcelUtils;
import gr.watchful.permchecker.utils.FileUtils;
import gr.watchful.permchecker.utils.OsTypes;

import javax.swing.*;

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

    public static final String permUrl = "https://onedrive.live.com/download?resid=96628E67B4C51B81!161&ithint=" +
            "file%2c.xlsx&app=Excel&authkey=!APQ4QtFrBqa1HwM";
    public static final String jsonUrl = "http://jake-evans.net/_work/_ftb/modperms/api/index.php?key=123&type=all";
	public static final String forgeUrl = "http://api.feed-the-beast.com/ss/api/GetForgePackJSON/";
	public static final String ftbRepoUrl = "http://www.creeperrepo.net/FTB2/";
	public static final String[] modTypes = {"jar", "zip", "disabled", "litemod", "class"};


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
        System.out.println("LENGTH: "+modInfos.length);
        for(ModInfo modInfo : modInfos) {
            System.out.println(modInfo.shortName);
        }
        nameRegistry.loadMappings(modInfos, "https://dl.dropboxusercontent.com/u/27836116/FTBPermissionsImages/", "png");

/*		permFile = new File(appStore.getPath() + File.separator + "Permissions.xlsx");
		if (!permFile.exists()) {
			try {
				permFile.createNewFile();
			} catch (IOException e) {
				System.out.println("Could not create Permissions.xlsx");
				return false;
			}
		}

		try {
			FileUtils.downloadToFile(new URL(permUrl), permFile);
		} catch (IOException e) {
			System.out.println("Could not download perm file");
			return false;
		}

		ArrayList<ArrayList<String>> infos;
		ArrayList<ArrayList<String>> mappings;
		try {
			infos = ExcelUtils.toArray(permFile, 1);
			mappings = ExcelUtils.toArray(permFile, 2);
		} catch (IOException e) {
			System.out.println("Could not read perm json");
			return false;
		}
		infos.remove(0);//remove the first row, it contains column titles
		nameRegistry.loadMappings(infos, mappings, infos.get(15).get(14), infos.get(15).get(15));*/
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
