package gr.watchful.permchecker.datastructures;

import gr.watchful.permchecker.utils.FileUtils;

import javax.swing.*;
import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ModPack {
	public String name;//simple
	public String author;//simple
	public String shortName;//difficult, need to change all files on server
	public String key;//moderate, delete 1 local file. Leaves old code on server TODO
	public String description;//simple
	public String recommendedVersion;//simple, select from available
    public String minecraftVersion;//simple, select from available
    public String curseID;
	public ArrayList<ModPackVersion> metaVersions;//add, simple. Remove, change rec version if necessary. Move TODO
	public ArrayList<String> versions;//legacy
    public ArrayList<String> mods;
    public ArrayList<String> unknownModIDs;
	public ForgeType forgeType;
	public int ForgeVersion;
    public boolean isPublic;

	public String iconName;
    public String splashName;
    public String squareName;
	public String serverName;
	public String zipName;

	public String warning;
	public String animation;

	transient public File icon;
    transient public File splash;
    transient public File square;
	transient public File server;
	transient public boolean dirty;
	
	public Time lastEdited;
	public ArrayList<String> submitters;
	public String submitURL;
    public File storageLocation;
    public int metaVersion;

	public HashMap<String, String> shortNameMappings;
	public HashMap<String, ModInfo> modInfoMappings;
	
	public ModPack() {
		init();
		ForgeVersion = -1;
		isPublic = true;
	}

	public void init() {
		if(name == null || name.equals("")) name = "Unnamed";
		if(author == null || author.equals("")) author = "none";
        if(shortName == null) shortName = "";
        if(curseID == null) curseID = "";
		if(key == null || key.equals("")) key = generateKey();
		if(description == null) description = "";
		if(metaVersions == null) metaVersions = new ArrayList<>();
		if(metaVersions.size() == 0) metaVersions.add(new ModPackVersion("1.0.0"));
		if(recommendedVersion == null || recommendedVersion.equals(""))
			recommendedVersion = metaVersions.get(0).version;
		if(minecraftVersion == null || minecraftVersion.equals(""))
			minecraftVersion = Globals.getInstance().preferences.defaultMinecraftVersion;
        if(mods == null) mods = new ArrayList<>();
        if(unknownModIDs == null) unknownModIDs = new ArrayList<>();
		if(forgeType == null) forgeType = ForgeType.RECOMMENDED;
		if(shortNameMappings == null) shortNameMappings = new HashMap<>();
		if(modInfoMappings == null) modInfoMappings = new HashMap<>();
		ArrayList<String> badNames = new ArrayList<>();

		for(ModInfo modInfo : modInfoMappings.values()) {
			if(modInfo.officialSpreadsheet) {
				if(modInfo.customLink != null && (modInfo.customLink.equals(modInfo.licenseImage) ||
						modInfo.customLink.equals(modInfo.privateLicenseImage))) {
					badNames.add(modInfo.shortName);
				} else {
					modInfo.officialSpreadsheet = false;
				}
				dirty = true;
			}
		}
		for(String name : badNames) {
			modInfoMappings.remove(name);
			shortNameMappings.remove(name);
		}

        metaVersion = Globals.metaVersion;
	}
	
	public String toString() {
		return name;
	}
	
	public Boolean saveThisObject() {
		if(Globals.getInstance().preferences.saveFolder == null) return false;
		else if(name != null && name.equals("Unnamed")) return false;
		else if(shortName == null || shortName.equals("")) {
			if(createShortName(this)) Globals.getInstance().changeFlag = true;
			else return false;
		}

		saveObject(new File(Globals.getInstance().preferences.saveFolder+File.separator+shortName+".json"), this);
		return true;
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
        if(!FileUtils.getFileExtension(saveFile).equals("json")) return null;
        ModPack temp;
        try {
            temp = (ModPack) FileUtils.readObject(saveFile, new ModPack());
        } catch (Exception e) {
            System.out.println("Failed to read "+saveFile.getName());
            return null;
        }
		if(temp.versions != null) {
			temp.metaVersions = new ArrayList<>();
			for(String version : temp.versions) {
				temp.metaVersions.add(new ModPackVersion(version));
			}
		}
        temp.storageLocation = saveFile;
        if(temp.metaVersion == 0 || temp.metaVersion > Globals.metaVersion) {
            Globals.getInstance().oldVersionsFlag = true;
            return null;
        }
		temp.init();
		temp.versions = null;
		return temp;
	}

    public static String generateShortName(String name) {
        return name.replaceAll("[^0-9A-Za-z]", "");
    }

    public static String generateKey() {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        int len = 10;

        StringBuilder sb = new StringBuilder(len);
        for( int i = 0; i < len; i++ ) sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    public static Boolean isValidKey(String key) {
        return !key.matches(".*[^0-9A-Za-z-].*");
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
		if(iconName == null || iconName.equals("")) iconName = shortName+"Icon"+".png";
		return iconName;
	}

    public String getSplashName() {
        if(splashName == null || splashName.equals("")) splashName = shortName+"Splash"+".png";
        return splashName;
    }

    public String getSquareName() {
        if(squareName == null || squareName.equals("")) squareName = shortName+"Square"+".png";
        return squareName;
    }

	public String getZipName() {
		if(zipName == null || zipName.equals("")) zipName = shortName+".zip";
		return zipName;
	}

	public String getModList() {
		StringBuilder bldr = new StringBuilder();

        for(String shortName : mods) {
            ModInfo modInfo = Globals.getInstance().nameRegistry.getInfo(shortName, Globals.getModPack());

            if(bldr.length() != 0) bldr.append("; ");

            bldr.append("<a color=\"aqua\" href=\"");
            bldr.append(modInfo.modLink);
            bldr.append("\">");
            bldr.append(modInfo.modName);
            bldr.append("</a>");
            bldr.append(" by ");
            bldr.append(modInfo.modAuthor);

            if(modInfo.customLink != null && !modInfo.customLink.equals("")) {
                bldr.append(" - ");
                bldr.append("<a color=\"aqua\" href=\"");
                bldr.append(modInfo.customLink);
                bldr.append("\">");
                bldr.append("Granted");
                bldr.append("</a>");
            }
        }
		return bldr.toString();
	}

	public static boolean createShortName(ModPack pack) {
        String result = checkValidShortName(generateShortName(pack.name));
        if (result == null) {
            System.out.println("Shortname creation canceled");
            return false;
        } else {
            pack.shortName = result;
            return true;
        }
	}

    private static String checkValidShortName(String shortname) {
        String message = "";
        boolean valid = false;
        if(shortname == null || shortname.equals("") || !ModPack.isValidKey(shortname)) {
            message = "Shortname is not valid, pick a new key";
        } else if(Globals.getInstance().listsPacks.shortnameExists(shortname)) {
            message = "Shortname exists, pick a new shortname\nIf you want to overwrite the shortname, press ok without changing the shortname";
            valid = true;
        }
        if(!message.equals("")) {
            String result = (String) JOptionPane.showInputDialog(
                    Globals.getInstance().mainFrame, message,
                    "New shortname", JOptionPane.PLAIN_MESSAGE, null, null, shortname);
            if(result == null) return null;
            if(valid && result.equals(shortname)) {
                int n = JOptionPane.showConfirmDialog(
                        Globals.getInstance().mainFrame,
                        "Are you sure you want to overwrite the shortname \""+shortname+"\"?",
                        "Confirm overwrite",
                        JOptionPane.YES_NO_OPTION);
                if(n == JOptionPane.YES_OPTION) {
                    return shortname;
                } else {
                    return null;
                }
            }
            return checkValidShortName(result);
        }
        return shortname;
    }

	public void addShortName(String shortName, String modID) {
		System.out.println("Saving ID "+modID+" as "+shortName);
		shortNameMappings.put(modID, shortName);
		dirty = true;
	}

	public void addModInfo(String shortName, ModInfo modInfo) {
		System.out.println("Saving ModInfo "+modInfo.modName+" as "+shortName);
		modInfoMappings.put(shortName, modInfo);
		dirty = true;
	}
}
