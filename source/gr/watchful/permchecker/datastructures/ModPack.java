package gr.watchful.permchecker.datastructures;

import gr.watchful.permchecker.utils.FileUtils;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ModPack {
	public String name;//simple
	public String author;//simple
	public String shortName;//difficult, need to change all files on server
	public String key;//moderate, delete 1 local file. Leaves old code on server
	public String description;//simple
	public String recommendedVersion;//simple, select from available
	public String minecraftVersion;//simple, select from available
	public ArrayList<String> versions;//add, simple. Remove, change rec version if necessary. Move TODO
	public ArrayList<String> modList;//autocomputed, no interface
	public ForgeType forgeType;
	public int ForgeVersion;
    public boolean isPublic;

	public String iconName;
	public String splashName;
	public String serverName;
	public String zipName;

	transient public File icon;
	transient public File splash;
	transient public File server;
	
	public Time lastEdited;
	public ArrayList<String> submitters;
	public String submitURL;

	public HashMap<String, String> shortNameMappings;
	public HashMap<String, ModInfo> modInfoMappings;
	
	public ModPack() {
        name = "Unnamed";
        author = "";
        shortName = "";
        key = "";
        description = "";
        recommendedVersion = "";
        minecraftVersion = "";
        versions = new ArrayList<>();
        modList = new ArrayList<>();
		forgeType = ForgeType.RECOMMENDED;
		ForgeVersion = 0;
		isPublic = true;
	}
	
	public String toString() {
		return name;
	}
	
	public Boolean saveThisObject() {
		if(Globals.getInstance().preferences.saveFolder == null) return false;
		else {
			saveObject(new File(Globals.getInstance().preferences.saveFolder+File.separator+shortName+".json"), this);
			return true;
		}
	}
	
	public static void saveObject(File saveFile, ModPack pack) {
		if(pack.name == null || pack.name.equals("") || pack.key == null || pack.key.equals("")) {
            System.out.println("Bad Name/Key, can't save");
            return;
        }
		FileUtils.saveObject(pack, saveFile);
	}
	
	public static ModPack loadObject(File saveFile) {
		if(!saveFile.exists()) return null;
		
		return (ModPack) FileUtils.readObject(saveFile, new ModPack());
	}

    public static String generateShortName(String name) {
        return name.replaceAll("[^0-9A-Za-z]", "");
    }

    public static String generateKay() {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        int len = 10;

        StringBuilder sb = new StringBuilder(len);
        for( int i = 0; i < len; i++ ) sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

	public boolean equals(Object object) {
		if(!object.getClass().equals(ModPack.class)) return false;
		return shortName.equals(((ModPack) object).shortName);
	}

	public int compareTo(Object otherObject) {
		if(otherObject.getClass().equals(ModPack.class)) {
			return ((ModPack) otherObject).shortName.compareTo(shortName);
		} else {
			return otherObject.toString().compareTo(toString());
		}
	}

	public String getIconName() {
		return shortName+"Icon"+".png";
	}

	public String getSplashName() {
		return shortName+"Splash"+".png";
	}

	public String getZipName() {
		return shortName+".zip";
	}

	public String getServerName() {
		return shortName+".zip";
	}

	public String getModList() {
		StringBuilder bldr = new StringBuilder();
		Boolean first = true;
		for(String mod : modList) {
			if(first) {
				first = false;
				bldr.append(mod);
				continue;
			}
			bldr.append("; ");
			bldr.append(mod);
		}
		return bldr.toString();
	}

	public String getStringVersions() {
		if(versions.size() == 0) return "";
		StringBuilder bldr = new StringBuilder();
		bldr.append(versions.get(0));
		for(int i=1; i<versions.size(); i++) {
			bldr.append(";");
			bldr.append(versions.get(i));
		}
		return bldr.toString();
	}

	public void addShortName(String shortName, String modID) {
		shortNameMappings.put(modID, shortName);
	}

	public void addModInfo(String shortName, ModInfo modInfo) {
		modInfoMappings.put(shortName, modInfo);
	}
}
