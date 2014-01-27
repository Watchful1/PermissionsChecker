package gr.watchful.permchecker.modhandling;

import gr.watchful.permchecker.datastructures.ModInfo;
import java.util.ArrayList;

import java.util.HashMap;

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
				info.setModName(row.get(0));//set name
				info.setModAuthor(row.get(1));//set author
				info.setModUrl(row.get(5));//set url
				
				if(row.get(6).equals("")) {//set perm link
					info.setPermLink(info.getModUrl());
				} else {
					info.setPermLink(row.get(6));
				}
				if(row.get(7).equals("")) {//set private perm link
					info.setPrivatePermLink(info.getPermLink());
				} else {
					info.setPrivatePermLink(row.get(7));
				}
				if(row.get(8).equals("")) {//set FTB perm link
					info.setFTBPermLink(info.getPermLink());
				} else {
					info.setFTBPermLink(row.get(8));
				}
				
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
