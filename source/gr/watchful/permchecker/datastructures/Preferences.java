package gr.watchful.permchecker.datastructures;

import java.io.File;
import java.util.ArrayList;

/**
 * This will be saved as a JSON to persist user preferences across sessions
 */
public class Preferences {
	public File saveFolder;
    public File workingFolder;
	public File exportFolder;
	public ArrayList<String> minecraftVersions;
	public String defaultMinecraftVersion;

    public Preferences() {
    }

    public void initPreferences(File appStore) {
        System.out.println("Initialized new preferences");

        saveFolder = new File(appStore+File.separator+"packs");
        workingFolder = new File(appStore+File.separator+"working");
		exportFolder = new File(appStore+File.separator+"export");

		minecraftVersions = new ArrayList<>();
		minecraftVersions.add("1.7.10");
		minecraftVersions.add("1.7.2");
		minecraftVersions.add("1.6.4");
		minecraftVersions.add("1.6.2");
		minecraftVersions.add("1.5.2");
		minecraftVersions.add("1.4.7");
		minecraftVersions.add("1.2.5");
		defaultMinecraftVersion = "1.6.4";
    }
}
