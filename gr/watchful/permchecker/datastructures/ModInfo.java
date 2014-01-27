package gr.watchful.permchecker.datastructures;

public class ModInfo {
	public static final int OPEN = 0;
	public static final int REQUEST = 1;
	public static final int CLOSED = 2;
	public static final int FTB = 3;
	public static final int UNKNOWN = 4;

	public static final int FTB_GRANTED = 5;
	public static final int FTB_UNKOWN = 6;
	
	public String shortName;
	public String modName;
	public String modAuthor;
	public String modUrl;
	public int publicPolicy;
	public int privatePolicy;
	public int FTBPolicy;
	public String permLink;
	public String privatePermLink;
	public String FTBPermLink;
	public String imageLink;
	public String privateImageLink;
	public String FTBImageLink;
	
	public ModInfo(String shortName) {
		this.shortName = shortName;
		modName = "Unknown";
		modAuthor = "Unknown";
		modUrl = "Unknown";
		publicPolicy = UNKNOWN;
		privatePolicy = UNKNOWN;
		FTBPolicy = FTB_UNKOWN;
		permLink = "Unknown";
		privatePermLink = "";
		FTBPermLink = "";
	}
}
