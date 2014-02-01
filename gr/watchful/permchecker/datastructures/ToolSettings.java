package gr.watchful.permchecker.datastructures;

public class ToolSettings {
	public static final int PUBLIC = 0;
	public static final int PRIVATE = 1;
	public static final int FTB = 2;

	public static final int ANYLAUNCHER = 3;
	public static final int FTBLAUNCHER = 4;
	
	public int packType;
	public int launcherType;
	
	public ToolSettings() {
		packType = ToolSettings.PUBLIC;
		launcherType = ToolSettings.FTBLAUNCHER;
	}
}
