package gr.watchful.permchecker.datastructures;

public class Mod {
	public String shortName;
	public ModFile modFile;
	public int permStatus;

	public static final int UNKNOWN = 0;
	public static final int PRIVATE = 1;
	public static final int PUBLIC = 2;
	
	public Mod(ModFile modFile, String shortName) {
		this.modFile = modFile;
		this.shortName = shortName;
		permStatus = Mod.UNKNOWN;
	}
	
	public String toString() {
		ModInfo modInfo = Globals.getInstance().nameRegistry.getInfo(this);
		String priv = "";
		if(permStatus == PRIVATE)  priv = "P) ";
		if(modInfo == null || modInfo.modName == "Unknown") {
			return priv+shortName;
		} else {
			return priv+modInfo.modName;
		}
	}
}
