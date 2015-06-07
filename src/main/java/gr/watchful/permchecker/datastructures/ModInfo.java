package gr.watchful.permchecker.datastructures;

public class ModInfo {
	public static final int OPEN = 0; //1
	public static final int NOTIFY = 1; //2
	public static final int REQUEST = 2; //3
	public static final int CLOSED = 3; //5
	public static final int FTB = 4; //4
	public static final int UNKNOWN = 5; //6
	
	public boolean officialSpreadsheet;
	
	public String shortName;
	
	public String modName;
	public String modVersion;
    public String modAuthor;
    public String[] modAuthors;
	public String modLink;
	
	public String licenseLink;
	public String licenseImage;
	public String privateLicenseLink;
	public String privateLicenseImage;
	
	public String customLink;
	public boolean isPublicPerm;
	
	public int publicPolicy;
	public int privatePolicy;

    public String[] modids;
	
	public ModInfo(String shortName) {
		officialSpreadsheet = false;
		this.shortName = shortName;
		isPublicPerm = false;

        init();
	}

    public void init() {
        if(shortName == null || shortName.equals("")) System.out.println("Trying to init a ModInfo with a null shortname, this is bad");
        if(modName == null || modName.equals("")) modName = "Unknown";
        if(modAuthor == null || modAuthor.equals("")) modAuthor = "Unknown";
        if(modLink == null || modLink.equals("")) modLink = "None";
        if(modVersion == null || modVersion.equals("")) modVersion = "Unknown";
        if(publicPolicy == 0) publicPolicy = UNKNOWN;
        if(privatePolicy == 0) privatePolicy = UNKNOWN;
        if(licenseLink == null) licenseLink = "";
        if(licenseImage == null) licenseImage = "";
        if(privateLicenseLink == null) privateLicenseLink = "";
        if(privateLicenseImage == null) privateLicenseImage = "";
        if(customLink == null) customLink = "";
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
