package gr.watchful.permchecker.datastructures;

import java.util.ArrayList;

public class ModStorage {
	public ArrayList<ModFile> modFiles;
	public ArrayList<Mod> mods;

	public ModStorage() {
		modFiles = new ArrayList<>();
		mods = new ArrayList<>();
	}
}
