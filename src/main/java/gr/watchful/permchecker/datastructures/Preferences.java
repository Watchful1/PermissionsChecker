package gr.watchful.permchecker.datastructures;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This will be saved as a JSON to persist user preferences across sessions
 */
public class Preferences {
	transient public File saveFolder;
	transient public File workingFolder;
	transient public File exportFolder;
	transient public File defaultOpenFolder;
	public String saveFolderPath;
	public String workingFolderPath;
	public String exportFolderPath;
	public String defaultOpenFolderPath;
	public ArrayList<String> minecraftVersions;
	public String defaultMinecraftVersion;
	public String parsedPackShortName;
	public boolean copyImportAssets;

	public HashMap<String, String> unknownMods;

    public Preferences(File appStore) {
		init(appStore);
    }

    public void init(File appStore) {
		if(saveFolderPath == null || saveFolderPath.equals("")) saveFolderPath = appStore+File.separator+"packs";
		if(workingFolderPath == null || workingFolderPath.equals("")) workingFolderPath = appStore+File.separator+"working";
		if(exportFolderPath == null || exportFolderPath.equals("")) exportFolderPath = appStore+File.separator+"export";
		if(defaultOpenFolderPath == null || defaultOpenFolderPath.equals("")) defaultOpenFolderPath = System.getProperty("user.home");

        saveFolder = new File(saveFolderPath);
        workingFolder = new File(workingFolderPath);
		exportFolder = new File(exportFolderPath);
		defaultOpenFolder = new File(defaultOpenFolderPath);

		if(minecraftVersions == null) {
			minecraftVersions = new ArrayList<>();
			minecraftVersions.add("1.7.10");
			minecraftVersions.add("1.7.2");
			minecraftVersions.add("1.6.4");
			minecraftVersions.add("1.6.2");
			minecraftVersions.add("1.5.2");
			minecraftVersions.add("1.4.7");
			minecraftVersions.add("1.2.5");
		}
		if(defaultMinecraftVersion == null || defaultMinecraftVersion.equals("")) defaultMinecraftVersion = "1.6.4";
    	if(unknownMods == null) unknownMods = new HashMap<>();
	}

	public File getWorkingMinecraftFolder() {
		File capital = new File(workingFolder+File.separator+"Minecraft");
		File dotCapital = new File(workingFolder+File.separator+".Minecraft");
		File dot = new File(workingFolder+File.separator+".minecraft");
		File normal = new File(workingFolder+File.separator+"minecraft");

		if(capital.exists()) capital.renameTo(normal);
		else if(dotCapital.exists()) dotCapital.renameTo(normal);
		else if(dot.exists()) dot.renameTo(normal);
		return normal;
	}

	public void setDefaultOpenFolder(File folder) {
		defaultOpenFolder = folder;
		defaultOpenFolderPath = folder.getAbsolutePath();
	}
}
