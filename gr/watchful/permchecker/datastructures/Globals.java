package gr.watchful.permchecker.datastructures;

import java.io.File;

import gr.watchful.permchecker.modhandling.ModNameRegistry;

public class Globals {
	private static volatile Globals instance = null;
	
	public static final int PUBLIC = 0;
	public static final int PRIVATE = 1;
	public static final int FTB = 2;

	public static final int ANYLAUNCHER = 3;
	public static final int FTBLAUNCHER = 4;
	
	public static final String modpackDataFile = "customPack.json";
	
	public int packType;
	public int launcherType;
	public ModNameRegistry nameRegistry;
	public RebuildsMods main;
	public File minecraftFolder;
	
	public Globals() {
		packType = Globals.PUBLIC;
		launcherType = Globals.FTBLAUNCHER;
		nameRegistry = new ModNameRegistry();
		minecraftFolder = null;
	}
	
	public static Globals getInstance() {
		if(instance == null) {
			synchronized(Globals.class){
				if(instance == null) {
					instance = new Globals();
				}
			}
		}
		return instance;
	}
	
	public String getStringType() {
		switch(packType) {
		case PUBLIC:
			return "public";
		case PRIVATE:
			return "private";
		default:
			return "public";
		}
	}
}
