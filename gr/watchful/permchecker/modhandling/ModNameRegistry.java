package gr.watchful.permchecker.modhandling;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModInfo;
import gr.watchful.permchecker.datastructures.ModpackStorageObject;

import java.util.ArrayList;

import java.util.HashMap;

import com.google.gson.Gson;

/**
 * Stores and gives access to mappings for modid to shortname as well as shortname to associated modinfo object
 * @author Watchful
 */
public class ModNameRegistry {
	public static String imageBaseUrl;
	public static String imageExtension;
	
	private HashMap<String, String> shortNameMappings;
	private HashMap<String, ModInfo> modInfoMappings;
	private ModpackStorageObject customMappings;
	
	public ModNameRegistry() {
		customMappings = new ModpackStorageObject();
		shortNameMappings = new HashMap<String, String>();
		customMappings.customShortNameMappings = new HashMap<String, String>();
		modInfoMappings = new HashMap<String, ModInfo>();
		customMappings.customModInfoMappings = new HashMap<String, ModInfo>();
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
				if(row.get(8).equals("")) {//set FTB perm link
					info.FTBLicenseLink = info.licenseLink;
				} else if(row.get(8).equals("PM")) {
					info.FTBLicenseLink = imageBaseUrl+"PrivateMessage"+imageExtension;
				} else {
					info.FTBLicenseLink = row.get(8);
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
				
				//set FTB policy
				if(info.publicPolicy == ModInfo.OPEN || info.publicPolicy == ModInfo.FTB || !row.get(8).equals("")) {
					info.FTBPolicy = ModInfo.FTB_GRANTED;
				} else {
					info.FTBPolicy = ModInfo.FTB_UNKOWN;
				}
				
				info.licenseImageLink = imageBaseUrl+info.shortName+imageExtension;//set perm image link
				if(row.get(7).equals("")) {//set private perm image link
					info.licensePrivateImageLink = info.licenseImageLink;
				} else {
					info.licensePrivateImageLink = imageBaseUrl+info.shortName+"private"+imageExtension;
				}
				if(row.get(8).equals("")) {//set FTB perm image link
					info.licenseFTBImageLink = info.licenseImageLink;
				} else {
					info.licenseFTBImageLink = imageBaseUrl+info.shortName+"FTB"+imageExtension;
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
	
	public void addShortName(String shortName, String modID) {
		customMappings.customShortNameMappings.put(modID, shortName);
		saveCustomInfos();
	}
	
	public void addModInfo(String shortName, ModInfo modInfo) {
		customMappings.customModInfoMappings.put(shortName, modInfo);
		saveCustomInfos();
	}
	
	public HashMap<String, String> getCustomMappings() {
		return customMappings.customShortNameMappings;
	}
	
	public HashMap<String, ModInfo> getCustomInfos() {
		return customMappings.customModInfoMappings;
	}
	
	public String checkID(String modID) {
		if(customMappings.customShortNameMappings.containsKey(modID)) {
			return customMappings.customShortNameMappings.get(modID);
		} else {
			return shortNameMappings.get(modID);
		}
	}
	
	public ModInfo getMod(String shortName) {
		if(customMappings.customModInfoMappings.containsKey(shortName)) {
			return customMappings.customModInfoMappings.get(shortName);
		} else {
			return modInfoMappings.get(shortName);
		}
	}
	
	public static String buildShortName(String name) {
		return name.toLowerCase().replaceAll("[^A-Za-z]","");
	}
	
	public void printCustomInfos() {
		Gson gson = new Gson();
		System.out.println(gson.toJson(customMappings.customShortNameMappings));
		System.out.println(gson.toJson(customMappings.customModInfoMappings));
	}
	
	public void saveCustomInfos() {
		customMappings.saveObject(Globals.getInstance().minecraftFolder);
	}
	
	public void loadCustomInfos() {
		customMappings.loadObject(Globals.getInstance().minecraftFolder);
	}
}
