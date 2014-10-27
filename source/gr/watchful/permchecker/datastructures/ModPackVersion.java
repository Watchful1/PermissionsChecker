package gr.watchful.permchecker.datastructures;

public class ModPackVersion {
	public String version;
	public String mcVersion;
	public String forgeVersion;
	public String launcherVersion;

	public ModPackVersion(String version) {
		this.version = version;
	}

	public String toString() {
		return version;
	}
}
