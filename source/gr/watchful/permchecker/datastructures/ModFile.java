package gr.watchful.permchecker.datastructures;

import java.io.File;

import javax.swing.DefaultListModel;

public class ModFile implements Comparable {
	public File file;
	private String fileName;
	public DefaultListModel<String> IDs = new DefaultListModel<String>();
	public DefaultListModel<String> names = new DefaultListModel<String>();
	public DefaultListModel<String> versions = new DefaultListModel<String>();

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
