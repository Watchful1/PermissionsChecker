package gr.watchful.permchecker.datastructures;

import java.io.File;
import java.io.FileNotFoundException;
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
    public RebuildsMods main;
    public JFrame mainFrame;

    public static final String permUrl = "https://onedrive.live.com/download?resid=96628E67B4C51B81!161&ithint=" +
            "file%2c.xlsx&app=Excel&authkey=!APQ4QtFrBqa1HwM";
	public static final String forgeUrl = "http://144.118.200.123/ss/api/GetForgePackJSON/";


    // TODO all variables below here should be moved or deleted

	public static final int PUBLIC = 0;
	public static final int PRIVATE = 1;
	public static final int FTB = 2;

	public static final int ANYLAUNCHER = 3;
	public static final int FTBLAUNCHER = 4;
	
	public static final String modpackDataFile = "customPack.json";
	
	public int packType;
	public int launcherType;
	public File minecraftFolder;
	
	public Globals() {
		packType = Globals.PUBLIC;
		launcherType = Globals.FTBLAUNCHER;
		nameRegistry = new ModNameRegistry();
		minecraftFolder = null;
        preferences = new Preferences();
    }
	
	public static Globals getInstance() {
		if(instance == null) {
			synchronized(Globals.class){
				if(instance == null) {
					instance = new Globals();
				}
			}
		}
		return instance;
	}

    public boolean initializeFolders() {
        switch (OsTypes.getOperatingSystemType()) {
            case Windows:
                appStore = new File(System.getenv("APPDATA")+File.separator+"PermissionsChecker");
                break;
            case MacOS:
                appStore = new File(System.getProperty("user.home")+File.separator+"Library/Application Support/PermissionsChecker");
                break;
            case Linux:
                appStore = new File(System.getProperty("user.home")+File.separator+".permissionsChecker");
                break;
            case Other:
                appStore = new File(".permissionsChecker");
                break;
        }
        if (!appStore.exists()) {
            boolean result = appStore.mkdirs();
            if(!result) {
                System.out.println(Globals.getInstance().appStore.getPath() + " could not be created!");
                return false;
            }
        }
        return true;
    }

    public void savePreferences() {
        if(appStore == null && !initializeFolders()) {
            System.out.println("Can't save prefs as appStore could not be created");
            return;
        }
        FileUtils.saveObject(Globals.getInstance().preferences, new File(appStore+
                File.separator+"preferences.conf"));
    }

    public void loadPreferences() {
        if(appStore == null && !initializeFolders()) {
            System.out.println("Can't load prefs as appStore could not be created");
            return;
        }
        File prefFile = new File(appStore+File.separator+"preferences.conf");
        if(prefFile.exists()) {
            preferences = (Preferences) FileUtils.readObject(new File(appStore+
                            File.separator+"preferences.conf"), new Preferences());
        } else {
            preferences = new Preferences();
            preferences.initPreferences(appStore);
            savePreferences();
        }
    }

    public boolean updateListings() {
        permFile = new File(appStore.getPath()+File.separator+"Permissions.xlsx");
        if(!permFile.exists()) {
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
            mappings = ExcelUtils.toArray(permFile,2);
        } catch (IOException e) {
            System.out.println("Could not read perm file");
            return false;
        }
        infos.remove(0);//remove the first row, it contains column titles
        nameRegistry.loadMappings(infos, mappings, infos.get(15).get(14), infos.get(15).get(15));
        return true;
    }

    // TODO should be in modpack
	public String getStringType() {
		switch(packType) {
		case PUBLIC:
			return "public";
		case PRIVATE:
			return "private";
		default:
			return "public";
		}
	}
}
