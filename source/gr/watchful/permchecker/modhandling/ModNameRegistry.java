package gr.watchful.permchecker.modhandling;

import gr.watchful.permchecker.datastructures.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	
	public void loadMappings(ArrayList<ArrayList<String>> infos, ArrayList<ArrayList<String>> mappings, String baseUrl, String extension) {
		imageBaseUrl = baseUrl;
		imageExtension = extension;
		
		for(ArrayList<String> row : infos) {
			if(row.size() > 9 && row.get(2) != null && !row.get(2).equals("")) {
				ModInfo info = new ModInfo(row.get(2));
				info.officialSpreadsheet = true;
				info.modName = row.get(0);//set name
				info.modAuthor = row.get(1);//set author
				info.modLink = row.get(5);//set url
				
				if(row.get(6).equals("")) {//set perm link
					info.licenseLink = info.modLink;
				} else {
					info.licenseLink = row.get(6);
				}
				if(row.get(7).equals("")) {//set private perm link
					info.privateLicenseLink = info.licenseLink;
				} else if(row.get(7).equals("PM")) {
					info.privateLicenseLink = imageBaseUrl+"PrivateMessage"+imageExtension;
				} else {
					info.privateLicenseLink = row.get(7);
				}
				
				switch(row.get(3)){//set the public policy
				case "Open":
					info.publicPolicy = ModInfo.OPEN;
					break;
				case "Notify":
					info.publicPolicy = ModInfo.NOTIFY;
					break;
				case "Request":
					info.publicPolicy = ModInfo.REQUEST;
					break;
				case "Closed":
					info.publicPolicy = ModInfo.CLOSED;
					break;
				case "FTB":
					info.publicPolicy = ModInfo.FTB;
					break;
				case "Not Available":
					info.publicPolicy = ModInfo.UNKNOWN;
					break;
				default:
					info.publicPolicy = ModInfo.UNKNOWN;
					break;
				}
				
				switch(row.get(4)){//set the private policy
				case "Open":
					info.privatePolicy = ModInfo.OPEN;
					break;
				case "Notify":
					info.privatePolicy = ModInfo.NOTIFY;
					break;
				case "Request":
					info.privatePolicy = ModInfo.REQUEST;
					break;
				case "Closed":
					info.privatePolicy = ModInfo.CLOSED;
					break;
				case "FTB":
					info.privatePolicy = ModInfo.FTB;
					break;
				case "Not Available":
					info.privatePolicy = ModInfo.UNKNOWN;
					break;
				default:
					info.privatePolicy = ModInfo.UNKNOWN;
					break;
				}
				
				info.licenseImage = imageBaseUrl+info.shortName+imageExtension;//set perm image link
				if(row.get(7).equals("")) {//set private perm image link
					info.privateLicenseImage = info.licenseImage;
				} else {
					info.privateLicenseImage = imageBaseUrl+info.shortName+"private"+imageExtension;
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
		return checkID(modID, null);
	}
	
	public String checkID(String modID, ModPack modPack) {
		if(modPack != null && modPack.shortNameMappings != null
				&& modPack.shortNameMappings.containsKey(modID)) {
			return modPack.shortNameMappings.get(modID);
		} else {
			return shortNameMappings.get(modID);
		}
	}

	public boolean shortnameExists(String shortName) {
		return shortNameMappings.containsValue(shortName);
	}

	public ModInfo getInfo(Mod mod) {
		return getInfo(mod, null);
	}
	
	public ModInfo getInfo(Mod mod, ModPack modPack) {
		if(modPack != null && modPack.modInfoMappings != null
				&& modPack.modInfoMappings.containsKey(mod.shortName)) {
			return modPack.modInfoMappings.get(mod.shortName);
		} else {
			return modInfoMappings.get(mod.shortName);
		}
	}

	public ModStorage compileMods(ArrayList<ModFile> modFiles, ModPack modPack) {
		ModStorage modStorage = new ModStorage();
		ArrayList<Mod> mods;
		for(ModFile modfile : modFiles) {
			mods = processModFile(modfile, modPack);
			if(mods.isEmpty()) modStorage.modFiles.add(modfile);
			else {
				for(Mod mod : mods) {
					if(!modStorage.mods.containsKey(mod.shortName))
						modStorage.mods.put(mod.shortName, mod);
				}
			}
		}
		return modStorage;
	}

	private ArrayList<Mod> processModFile(ModFile modFile, ModPack modPack) {
		ArrayList<Mod> mods = new ArrayList<>();

		String result;
		HashSet<String> identifiedIDs = new HashSet<>();
		for(int i=0; i<modFile.IDs.getSize(); i++) {
			result = checkID(modFile.IDs.get(i), modPack);
			if(result != null) identifiedIDs.add(result);
		}
		for(String ID : identifiedIDs) {
			mods.add(new Mod(modFile, ID));
		}
		return mods;
	}
}
