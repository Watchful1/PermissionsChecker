package gr.watchful.permchecker.datastructures;

public class Mod implements Comparable {
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

	public int compareTo(Object otherObject) {
		if(otherObject.getClass().equals(Mod.class)) {
			return ((Mod) otherObject).shortName.compareTo(shortName);
		} else {
			return otherObject.toString().compareTo(toString());
		}
	}
}
