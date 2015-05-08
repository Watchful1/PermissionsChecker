package gr.watchful.permchecker.datastructures;

public class ModInfo {
	public static final int OPEN = 0;
	public static final int NOTIFY = 1;
	public static final int REQUEST = 2;
	public static final int CLOSED = 3;
	public static final int FTB = 4;
	public static final int UNKNOWN = 5;
	
	public boolean officialSpreadsheet;
	
	public String shortName;
	
	public String modName;
	public String modVersion;
	public String modAuthor;
	public String modLink;
	
	public String licenseLink;
	public String licenseImage;
	public String privateLicenseLink;
	public String privateLicenseImage;
	
	public String customLink;
	public boolean isPublicPerm;
	
	public int publicPolicy;
	public int privatePolicy;
	
	public ModInfo(String shortName) {
		officialSpreadsheet = false;
		this.shortName = shortName;
		modName = "Unknown";
		modAuthor = "Unknown";
		modLink = "None";
		modVersion = "Unknown";
		publicPolicy = UNKNOWN;
		privatePolicy = UNKNOWN;
		licenseLink = "";
		licenseImage = "";
		privateLicenseLink = "";
		privateLicenseImage = "";
		customLink = "";
		isPublicPerm = false;
	}

	public boolean hasPublic() {
		if(publicPolicy == OPEN || publicPolicy == FTB) return true;
		else if(!customLink.equals("") && isPublicPerm) return true;
		else return false;
	}

	public boolean hasPrivate() {
		if(privatePolicy == OPEN || privatePolicy == FTB) return true;
		else if(!customLink.equals("")) return true;
		else return false;
	}

	public String getPermLink(boolean isPublic) {
		if(isPublic || privateLicenseLink.equals("")) return licenseLink;
		else return privateLicenseLink;
	}

	public String getPermImage(boolean isPublic) {
		if(isPublic || privateLicenseImage.equals("")) return licenseImage;
		else return privateLicenseImage;
	}

	public int getPolicy(boolean isPublic) {
		if(isPublic) return publicPolicy;
		else return privatePolicy;
	}

	public static String getStringPolicy(int num) {
		switch (num) {
			case OPEN:
				return "Open";
			case FTB:
				return "FTB";
			case NOTIFY:
				return "Notify";
			case REQUEST:
				return "Request";
			case CLOSED:
				return "Closed";
			default:
				return "Unknown";
		}
	}
}
