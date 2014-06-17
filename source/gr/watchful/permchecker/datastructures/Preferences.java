package gr.watchful.permchecker.datastructures;

import java.io.File;

/**
 * This will be saved as a JSON to persist user preferences across sessions
 */
public class Preferences {
	public File saveFolder;

    public Preferences() {
        System.out.println("New Prefs");
    }

    public void setSaveFolder(File folder) {
        System.out.println("Setting save folder");
        saveFolder = folder;
    }
}
