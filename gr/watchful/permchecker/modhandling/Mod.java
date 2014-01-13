package gr.watchful.permchecker.modhandling;

import java.io.File;

public class Mod {
	public String ID;
	public String name;
	public File file;
	
	public Mod(File fileIn, String IDIn, String nameIn) {
		file = fileIn;
		ID = IDIn;
		name = nameIn;
	}
	
	public String toString() {
		StringBuilder bldr = new StringBuilder();
		if(name.equals("")) {
			bldr.append("ModID ");
		} else {
			bldr.append("Mod ");
			bldr.append(name);
			bldr.append(" has ID ");
		}
		bldr.append(ID);
		bldr.append(" is in file ");
		bldr.append(file.getName());
		return bldr.toString();
	}
}
