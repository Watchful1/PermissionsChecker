package gr.watchful.permchecker.datastructures;

import java.util.ArrayList;

public class ModStorage {
	public ArrayList<ModFile> modFiles;
	public ArrayList<Mod> goodMods;
	public ArrayList<Mod> badMods;

	public ModStorage() {
		modFiles = new ArrayList<>();
		goodMods = new ArrayList<>();
		badMods = new ArrayList<>();
	}
}
