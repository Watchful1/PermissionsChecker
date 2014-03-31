package gr.watchful.permchecker.datastructures;

import gr.watchful.permchecker.utils.FileUtils;

import java.io.File;
import java.util.HashMap;

public class ModpackStorageObject {
	public HashMap<String, String> customShortNameMappings;
	public HashMap<String, ModInfo> customModInfoMappings;
	
	public ModpackStorageObject() {
		customShortNameMappings = new HashMap<String, String>();
		customModInfoMappings = new HashMap<String, ModInfo>();
	}

	@Deprecated
	public void saveObject(File minecraftFolder) {
		if(customShortNameMappings.isEmpty() && customModInfoMappings.isEmpty()) return;
		FileUtils.saveObject(this, new File(minecraftFolder+File.separator+Globals.modpackDataFile));
	}
	
	@Deprecated
	public Boolean loadObject(File minecraftFolder) {
		if(!new File(minecraftFolder+File.separator+Globals.modpackDataFile).exists()) {
			customShortNameMappings = new HashMap<String, String>();
			customModInfoMappings = new HashMap<String, ModInfo>();
			return false;
		}
		ModpackStorageObject temp;
		temp = (ModpackStorageObject) FileUtils.readObject(new File(minecraftFolder+File.separator+Globals.modpackDataFile), this);
		customShortNameMappings = temp.customShortNameMappings;
		customModInfoMappings = temp.customModInfoMappings;
		return true;
	}
}
