package gr.watchful.permchecker.datastructures;

import java.io.File;
import java.util.ArrayList;

public class ModFile {
	public File file;
	private String fileName;
	public ArrayList<String[]> names = new ArrayList<String[]>();
	
	public ModFile(File fileIn) {
		file = fileIn;
		fileName = file.getName();
	}
	
	public String fileName() {
		return file.getName();
	}
	
	public void addName(String ID, String name) {
		String[] temp = new String[2];
		temp[0] = ID; temp[1] = name;
		names.add(temp);
	}
	
	public String toString() {
		return fileName;
	}
}
