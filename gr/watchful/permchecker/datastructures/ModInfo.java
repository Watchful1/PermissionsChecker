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
	
	public String customImageLink;
	public String customLicenseLink;
	
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
		customImageLink = "None";
		customLicenseLink = "None";
	}
}
