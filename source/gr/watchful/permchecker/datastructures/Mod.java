package gr.watchful.permchecker.datastructures;

public class Mod implements Comparable<Mod> {
	public String shortName;
	public ModFile modFile;
	
	public Mod(ModFile modFile, String shortName) {
		this.modFile = modFile;
		this.shortName = shortName;
	}
	
	public String toString() {
		ModInfo modInfo = Globals.getInstance().nameRegistry.getMod(shortName);
		if(modInfo == null || modInfo.modName == "Unknown") {
			return shortName;
		} else {
			return modInfo.modName;
		}
		
	}

	@Override
	public int compareTo(Mod otherMod) {
		if(otherMod == null) return Integer.MIN_VALUE;
		return shortName.compareTo(otherMod.shortName);
	}
}
