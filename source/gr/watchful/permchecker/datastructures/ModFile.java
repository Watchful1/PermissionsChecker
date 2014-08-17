package gr.watchful.permchecker.datastructures;

import gr.watchful.permchecker.modhandling.MetadataCollection;

import java.io.File;

public class ModFile {
	public File file;
	public MetadataCollection mcmod;

	public SortedListModel<String> IDs = new SortedListModel<String>();
	public SortedListModel<String> names = new SortedListModel<String>();
	public SortedListModel<String> versions = new SortedListModel<String>();
	
	public ModFile(File fileIn) {
		file = fileIn;
	}
	
	public String fileName() {
		return file.getName();
	}
	
	public void addName(String name) {
		names.addElement(name);
	}
	
	public void addID(String ID) {
		IDs.addElement(ID);
	}
	
	public String toString() {
		return file.getName();
	}

    public void addVersion (String version)
    {
        versions.addElement(version);
    }

	public ModInfo getInfo() {
		/*if(mcmod != null) {
			ModInfo temp = new ModInfo("");
			temp.modName = mcmod.
			return temp;
		}*/
		return null;
	}
}
