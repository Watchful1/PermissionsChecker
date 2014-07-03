package gr.watchful.permchecker.datastructures;

import java.io.File;

/**
 * This will be saved as a JSON to persist user preferences across sessions
 */
public class Preferences {
	public File saveFolder;
    public File workingFolder;

    public Preferences() {
    }

    public void initPreferences(File appStore) {
        System.out.println("Initialized new preferences");

        saveFolder = new File(appStore+File.separator+"packs");
        workingFolder = new File(appStore+File.separator+"working");
    }
}
