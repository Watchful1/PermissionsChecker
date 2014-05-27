package gr.watchful.permchecker.datastructures;

import gr.watchful.permchecker.utils.FileUtils;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ModPack {
	public String name;
	public String author;
	public String shortName;
	public String key;
	public String description;
	public String recommendedVersion;
	public String minecraftVersion;
	public ArrayList<String> versions;
	public ArrayList<String> modList;
	public File icon;
	public File splash;
	public File zip;
	public File server;
	
	public Time lastEdited;
	public ArrayList<String> submitters;
	public String submitURL;
	
	public ModpackStorageObject customPerms;
	
	public ModPack() {
        name = "";
        author = "";
        shortName = "";
        key = "";
        description = "";
        recommendedVersion = "";
        minecraftVersion = "";
        versions = new ArrayList<>();
        modList = new ArrayList<>();
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
}
