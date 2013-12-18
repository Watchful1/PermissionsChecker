import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ModHandler {
	HashMap<String, String> mods;
	File modFile;
	ArrayList<String> list;

	public ModHandler(File modFileIn) {
		mods = new HashMap<String, String>();
		modFile = modFileIn;
		list = new ArrayList<String>();
		loadNames();
	}
	
	public void loadNames() {
		mods.clear();
		String[] list = FileUtils.readFile(modFile).split("\\n");
		for(String mod : list) {
			//System.out.println(mod);
			//System.out.println("Reading " + mod.split("<=>")[0] + " = " + mod.split("<=>")[1]);
			mods.put(mod.split("<=>")[0], mod.split("<=>")[1]);
		}
	}
	
	public void saveNames() {
		StringBuilder bldr = new StringBuilder();
		Iterator<Entry<String, String>> it = mods.entrySet().iterator();
	    while (it.hasNext()) {
	        @SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry)it.next();
	        bldr.append(pairs.getKey() + "<=>" + pairs.getValue() + "\n");
	    }
	    modFile.delete();
	    FileUtils.writeFile(bldr.toString(), modFile);
	}
	
	public String modName(String nameIn) {
		System.out.println("modName "+nameIn);
		if(mods.containsKey(nameIn)) {
			return mods.get(nameIn);
		} else {
			saveMod(nameIn);
			return nameIn;
		}
	}
	
	public String[] modNameList(String[] listIn) {
		print();
		ArrayList<String> list = new ArrayList<String>();
		for(String name : listIn) {
			//System.out.println("Checking mod "+name);
			if(!list.contains(modName(name))) {
				list.add(modName(name));
			}
		}
		saveNames();
		return list.toArray(listIn);
	}
	
	private void saveMod(String modName) {
		//System.out.println("Adding "+modName);
		mods.put(modName, modName);
	}
	
	public void addMod(String name) {
		if(!list.contains(modName(name))) {
			list.add(modName(name));
		}
	}
	
	public void print() {
		StringBuilder bldr = new StringBuilder();
		Iterator<Entry<String, String>> it = mods.entrySet().iterator();
	    while (it.hasNext()) {
	        @SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry)it.next();
	        bldr.append("This (" + pairs.getKey() + ") is the same as this (" + pairs.getValue() + ")\n");
	    }
	    System.out.println(bldr.toString());
	}
}
