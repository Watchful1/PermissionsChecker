package gr.watchful.permchecker.datastructures;

public class Mod {
	public String shortName;
	public ModFile modFile;
	
	public Mod(ModFile modFile, String shortName) {
		this.modFile = modFile;
		this.shortName = shortName;
	}
	
	public String toString() {
		return shortName;
	}
}
