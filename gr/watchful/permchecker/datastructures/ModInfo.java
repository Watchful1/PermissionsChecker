package gr.watchful.permchecker.datastructures;

public class ModInfo {
	public static final int OPEN = 0;
	public static final int NOTIFY = 1;
	public static final int REQUEST = 2;
	public static final int CLOSED = 3;
	public static final int FTB = 4;
	public static final int UNKNOWN = 5;

	public static final int FTB_GRANTED = 6;
	public static final int FTB_UNKOWN = 7;
	
	public boolean officialSpreadsheet;
	
	public String shortName;
	
	public String modName;
	public String modAuthor;
	public String modLink;
	
	public String licenseLink;
	public String privateLicenseLink;
	public String FTBLicenseLink;
	public String licenseImageLink;
	public String licensePrivateImageLink;
	public String licenseFTBImageLink;
	
	public String customLink;
	
	public int publicPolicy;
	public int privatePolicy;
	public int FTBPolicy;
	
	public ModInfo(String shortName) {
		officialSpreadsheet = false;
		this.shortName = shortName;
		modName = "Unknown";
		modAuthor = "Unknown";
		modLink = "Unknown";
		publicPolicy = UNKNOWN;
		privatePolicy = UNKNOWN;
		FTBPolicy = FTB_UNKOWN;
		licenseLink = "Unknown";
		privateLicenseLink = "Unknown";
		FTBLicenseLink = "Unknown";
		customLink = "";
	}
	
	public int getCurrentPolicy() {
		switch(Globals.getInstance().packType) {
		case Globals.PUBLIC:
			return publicPolicy;
		case Globals.PRIVATE:
			return privatePolicy;
		default:
			return UNKNOWN;
		}
	}
	
	public void setCurrentPolicy(int policy) {
		switch(Globals.getInstance().packType) {
		case Globals.PUBLIC:
			publicPolicy = policy;
			break;
		case Globals.PRIVATE:
			privatePolicy = policy;
			break;
		}
	}
	
	public String getStringPolicy() {
		switch(getCurrentPolicy()) {
		case ModInfo.OPEN:
			return "Open";
		case ModInfo.NOTIFY:
			return "Notify";
		case ModInfo.REQUEST:
			return "Request";
		case ModInfo.CLOSED:
			return "Closed";
		case ModInfo.FTB:
			return "FTB";
		default:
			return "Unknown";
		}
	}
	
	public String getCurrentPermLink() {
		switch(Globals.getInstance().packType) {
		case Globals.PUBLIC:
			return licenseLink;
		case Globals.PRIVATE:
			return privateLicenseLink;
		default:
			return licenseLink;
		}
	}
	
	public String getCurrentImageLink() {
		switch(Globals.getInstance().packType) {
		case Globals.PUBLIC:
			return licenseImageLink;
		case Globals.PRIVATE:
			return licensePrivateImageLink;
		default:
			return licenseImageLink;
		}
	}
}
