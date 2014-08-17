package gr.watchful.permchecker.datastructures;

import java.util.ArrayList;
import java.util.HashMap;

public class ModStorage {
	public HashMap<String, Mod> mods;
	public ArrayList<ModFile> modFiles;

	public ModStorage() {
		modFiles = new ArrayList<>();
		mods = new HashMap<>();
	}
}
