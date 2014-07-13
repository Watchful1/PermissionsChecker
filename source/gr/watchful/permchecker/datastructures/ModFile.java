package gr.watchful.permchecker.datastructures;

import java.io.File;

public class ModFile implements Comparable {
	public File file;
	private String fileName;
	public SortedListModel<String> IDs = new SortedListModel<String>();
	public SortedListModel<String> names = new SortedListModel<String>();
	public SortedListModel<String> versions = new SortedListModel<String>();

	public ModInfo tempInfo;
	
	public ModFile(File fileIn) {
		file = fileIn;
		fileName = file.getName();
	}
	
	public String fileName() {
		return file.getName();
	}
	
	public void addName(String name) {
		names.addElement(name);
	}
	
	public void addID(String ID) {
		System.out.println("----- Adding ID "+ID+" -----");
		IDs.addElement(ID);
	}
	
	public String toString() {
		return fileName;
	}

    public void addVersion (String version)
    {
        versions.addElement(version);
    }

	public int compareTo(Object otherObject) {
		if(otherObject.getClass().equals(ModFile.class)) {
			return ((ModFile) otherObject).fileName.compareTo(fileName);
		} else {
			return otherObject.toString().compareTo(toString());
		}
	}
}
