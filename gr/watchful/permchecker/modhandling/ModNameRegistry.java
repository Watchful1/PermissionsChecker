package gr.watchful.permchecker.modhandling;

import gr.watchful.permchecker.datastructures.ModInfo;
import gr.watchful.permchecker.utils.FileUtils;
import java.util.ArrayList;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Stores and gives access to mappings for modid to shortname as well as shortname to associated modinfo object
 * @author Watchful
 */
public class ModNameRegistry {
	public static String imageBaseUrl;
	public static String imageExtension;
	
	private HashMap<String, String> shortNameMappings;
	private HashMap<String, ModInfo> modInfoMappings;
	
	public ModNameRegistry() {
		shortNameMappings = new HashMap<String, String>();
		modInfoMappings = new HashMap<String, ModInfo>();
	}
	
	public void loadMappings(ArrayList<ArrayList<String>> infos, ArrayList<ArrayList<String>> mappings) {
		for(ArrayList<String> row : infos) {
			if(row.get(2) != null && !row.get(2).equals("")) {
				ModInfo info = new ModInfo(row.get(2));
				info.setModName(row.get(1));
				modInfoMappings.put(row.get(2), info);
			}
		}
		for(ArrayList<String> row : mappings) {
			if(row.get(0) != null && row.get(1) != null && !row.get(0).equals("") && !row.get(1).equals("")) {
				shortNameMappings.put(row.get(0), row.get(1));
			}
		}
	}
	
	public String checkID(String modID) {
		if(shortNameMappings.containsKey(modID)) {
			return shortNameMappings.get(modID);
		} else {
			return null;
		}
	}
	
	public ModInfo getMod(String shortName) {
		if(modInfoMappings.containsKey(shortName)) {
			return modInfoMappings.get(shortName);
		} else {
			return null;
		}
	}
}
