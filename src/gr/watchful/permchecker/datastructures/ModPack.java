package gr.watchful.permchecker.datastructures;

import gr.watchful.permchecker.utils.FileUtils;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

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
		if(pack.name.equals("") || pack.name == null) return;
		FileUtils.saveObject(pack, saveFile);
	}
	
	public static ModPack loadObject(File saveFile) {
		if(!saveFile.exists()) return null;
		
		return (ModPack) FileUtils.readObject(saveFile, new ModPack());
	}
}
