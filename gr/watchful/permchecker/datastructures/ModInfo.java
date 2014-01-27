package gr.watchful.permchecker.datastructures;

public class ModInfo {
	public static final int OPEN = 0;
	public static final int REQUEST = 1;
	public static final int CLOSED = 2;
	public static final int FTB = 3;
	public static final int UNKNOWN = 4;

	public static final int FTB_GRANTED = 5;
	public static final int FTB_NOT_GRANTED = 6;
	public static final int FTB_UNKOWN = 7;
	
	private String shortName;
	private String modName;
	private String modAuthor;
	private String modUrl;
	private int publicPolicy;
	private int privatePolicy;
	private int FTBPolicy;
	private String permLink;
	private String privatePermLink;
	private String FTBPermLink;
	private String imageLink;
	private String privateImageLink;
	private String FTBImageLink;
	
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
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public void setModName(String modName) {
		this.modName = modName;
	}
	
	public String getModName() {
		return modName;
	}
	
	public void setModAuthor(String modAuthor) {
		this.modAuthor = modAuthor;
	}
	
	public String getModAuthor() {
		return modAuthor;
	}
	
	public void setModUrl(String modUrl) {
		this.modUrl = modUrl;
	}
	
	public String getModUrl() {
		return modUrl;
	}
	
	public void setPublicPolicy(int publicPolicy) {
		this.publicPolicy = publicPolicy;
	}
	
	public int getPublicPolicy() {
		return publicPolicy;
	}
	
	public void setPrivatePolicy(int privatePolicy) {
		this.privatePolicy = privatePolicy;
	}
	
	public int getPrivatePolicy() {
		return privatePolicy;
	}
	
	public void setFTBPolicy(int FTBPolicy) {
		this.FTBPolicy = FTBPolicy;
	}
	
	public int getFTBPolicy() {
		return FTBPolicy;
	}
	
	public void setPermLink(String permLink) {
		this.permLink = permLink;
	}
	
	public String getPermLink() {
		return permLink;
	}
	
	public void setPrivatePermLink(String privatePermLink) {
		this.privatePermLink = privatePermLink;
	}
	
	public String getPrivatePermLink() {
		return privatePermLink;
	}
	
	public void setFTBPermLink(String FTBPermLink) {
		this.FTBPermLink = FTBPermLink;
	}
	
	public String getFTBPermLink() {
		return FTBPermLink;
	}
	
	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}
	
	public String getImageLink() {
		return imageLink;
		//return ModNameRegistry.imageBaseUrl+shortName+ModNameRegistry.imageExtension;
	}
	
	public void setPrivateImageLink(String privateImageLink) {
		this.privateImageLink = privateImageLink;
	}
	
	public String getPrivateImageLink() {
		return privateImageLink;
		/*if(getPrivatePermLink().equals("")) {
			return "";
		} else {
			return ModNameRegistry.imageBaseUrl+shortName+"private"+ModNameRegistry.imageExtension;
		}*/
	}
	
	public void setFTBImageLink(String FTBImageLink) {
		this.FTBImageLink = FTBImageLink;
	}
	
	public String getFTBImageLink() {
		return FTBImageLink;
		/*if(getFTBPermLink().equals("")) {
			return "";
		} else {
			return ModNameRegistry.imageBaseUrl+shortName+"FTB"+ModNameRegistry.imageExtension;
		}*/
	}
}
