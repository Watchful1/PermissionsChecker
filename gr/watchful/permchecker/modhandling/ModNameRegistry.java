package gr.watchful.permchecker.modhandling;

import gr.watchful.permchecker.utils.FileUtils;
import java.util.ArrayList;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ModNameRegistry {
	private HashMap<String, String> mappings;
	private HashMap<String, String> names;
	private HashMap<String, String> fileNames;
	private File mappingsFile;
	
	public ModNameRegistry(File modNameFile) {
		mappingsFile = modNameFile;
		mappings = new HashMap<String, String>();
		names = new HashMap<String, String>();
		fileNames = new HashMap<String, String>();
		loadMappings();
	}
	
	private void loadMappings() {
		String[] mappingsStrings = FileUtils.readFile(mappingsFile).split("\\n");
		for(String mappingString : mappingsStrings) {
			//System.out.println(mappingString);
			//System.out.println(mappingString.split("<=>")[0]);
			//System.out.println(mappingString.split("<=>")[1]);
			mappings.put(mappingString.split("<=>")[0], mappingString.split("<=>")[1]);
		}
	}
	
	private void saveMappings() {
		StringBuilder bldr = new StringBuilder();
		Iterator<Entry<String, String>> it = mappings.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry)it.next();
			bldr.append(pairs.getKey() + "<=>" + pairs.getValue() + "\n");
		}
		mappingsFile.delete();
		FileUtils.writeFile(bldr.toString(), mappingsFile);
	}
	
	public String checkMod(String modID) {
		if(mappings.containsKey(modID)) {
			return mappings.get(modID);
		} else {
			return null;
		}
	}
	
	public String getName(String modID) {
		if(names.containsKey(modID)) {
			return names.get(modID);
		} else {
			return "";
		}
	}
}
