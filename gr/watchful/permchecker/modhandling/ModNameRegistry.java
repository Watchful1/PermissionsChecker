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
	
	public void loadMappings(ArrayList<ArrayList<String>> infos, ArrayList<ArrayList<String>> mappings, String baseUrl, String extension) {
		imageBaseUrl = baseUrl;
		imageExtension = extension;
		
		for(ArrayList<String> row : infos) {
			if(row.size() > 9 && row.get(2) != null && !row.get(2).equals("")) {
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
				} else if(row.get(7).equals("PM")) {
					info.setPrivatePermLink(imageBaseUrl+"PrivateMessage"+imageExtension);
				} else {
					info.setPrivatePermLink(row.get(7));
				}
				if(row.get(8).equals("")) {//set FTB perm link
					info.setFTBPermLink(info.getPermLink());
				} else if(row.get(8).equals("PM")) {
					info.setPrivatePermLink(imageBaseUrl+"PrivateMessage"+imageExtension);
				} else {
					info.setFTBPermLink(row.get(8));
				}
				
				switch(row.get(3)){//set the public policy
				case "Open":
					info.setPublicPolicy(ModInfo.OPEN);
					break;
				case "Request":
					info.setPublicPolicy(ModInfo.REQUEST);
					break;
				case "Closed":
					info.setPublicPolicy(ModInfo.CLOSED);
					break;
				case "FTB":
					info.setPublicPolicy(ModInfo.FTB);
					break;
				case "Not Available":
					info.setPublicPolicy(ModInfo.UNKNOWN);
					break;
				default:
					System.out.println("Couldn't set the public policy of "+info.getShortName());
					break;
				}
				
				switch(row.get(4)){//set the public policy
				case "Open":
					info.setPrivatePolicy(ModInfo.OPEN);
					break;
				case "Request":
					info.setPrivatePolicy(ModInfo.REQUEST);
					break;
				case "Closed":
					info.setPrivatePolicy(ModInfo.CLOSED);
					break;
				case "FTB":
					info.setPrivatePolicy(ModInfo.FTB);
					break;
				case "Not Available":
					info.setPrivatePolicy(ModInfo.UNKNOWN);
					break;
				default:
					System.out.println("Couldn't set the private policy of "+info.getShortName());
					break;
				}
				
				//set FTB policy
				if(info.getPublicPolicy() == ModInfo.OPEN || info.getPublicPolicy() == ModInfo.FTB || !row.get(8).equals("")) {
					info.setFTBPolicy(ModInfo.FTB_GRANTED);
				} else {
					info.setFTBPolicy(ModInfo.FTB_UNKOWN);
				}
				
				info.setImageLink(imageBaseUrl+info.getShortName()+imageExtension);//set perm image link
				if(row.get(7).equals("")) {//set private perm image link
					info.setPrivateImageLink(info.getImageLink());
				} else {
					info.setPrivateImageLink(imageBaseUrl+info.getShortName()+"private"+imageExtension);
				}
				if(row.get(8).equals("")) {//set FTB perm image link
					info.setFTBImageLink(info.getImageLink());
				} else {
					info.setFTBImageLink(imageBaseUrl+info.getShortName()+"FTB"+imageExtension);
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
