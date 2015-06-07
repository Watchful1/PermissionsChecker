package gr.watchful.permchecker.modhandling;

import gr.watchful.permchecker.datastructures.*;
import gr.watchful.permchecker.utils.FileUtils;

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
		shortNameMappings = new HashMap<>();
		modInfoMappings = new HashMap<>();
	}

    public void loadMappings(ModInfo[] modInfos, String baseUrl, String extension) {
        imageBaseUrl = baseUrl;
        imageExtension = extension;

        for(ModInfo modInfo : modInfos) {
            if(modInfo.shortName == null) {
                System.out.println("Skipping, no shortname");
                continue;
            }
            if(modInfo.modids == null || modInfo.modids.length == 0) {
                System.out.println("Skipping, no id's: "+modInfo.shortName);
                continue;
            }

            for(String modid : modInfo.modids) {
                shortNameMappings.put(modid, modInfo.shortName);
            }

            if(modInfo.modAuthors == null || modInfo.modAuthors.length == 0) {
                System.out.println("No authors for: "+modInfo.shortName);
            } else {
                StringBuilder bldr = new StringBuilder();
                for (String author : modInfo.modAuthors) {
                    bldr.append(author);
                    bldr.append(", ");
                }
                bldr.delete(bldr.length()-2, bldr.length());
                modInfo.modAuthor = bldr.toString();
            }
            modInfoMappings.put(modInfo.shortName, modInfo);
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
		return getInfo(mod.shortName, modPack);
	}

    public ModInfo getInfo(String shortName, ModPack modPack) {
        if(modPack != null && modPack.modInfoMappings != null
                && modPack.modInfoMappings.containsKey(shortName)) {
            return modPack.modInfoMappings.get(shortName);
        } else {
            return modInfoMappings.get(shortName);
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
		if(modFile.IDs.getSize() > 0) {
			for(int i=0; i<modFile.IDs.getSize(); i++) {
				result = checkID(modFile.IDs.get(i), modPack);
				if(result != null) identifiedIDs.add(result);
			}
		} else {
			String md5 = FileUtils.getMD5(modFile.file);
			if(md5 != null) {
				modFile.md5 = md5;
				result = checkID(md5, modPack);
				if(result != null) identifiedIDs.add(result);
			}
		}

		for(String ID : identifiedIDs) {
			mods.add(new Mod(modFile, ID));
		}
		return mods;
	}
}
