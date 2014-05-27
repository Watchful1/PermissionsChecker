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
	public File icon;//Icon if set and size small enough. Button to select new icon, deletes old
	public File splash;//Icon if set and size small enough. Button to select new icon, deletes old
	public File server;//Icon if set. Button to select new icon, deletes old
	
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
