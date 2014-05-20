package gr.watchful.permchecker.datastructures;

import java.io.File;

import javax.swing.DefaultListModel;

public class ModFile {
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
}
