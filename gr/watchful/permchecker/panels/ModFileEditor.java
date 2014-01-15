package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.ModFile;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ModFileEditor extends JPanel {
	private JTextField modName;
	
	public ModFileEditor(Dimension size) {
		modName = new JTextField("NOTTEST");
		this.add(modName);
	}
	
	public void setModFile(ModFile modFile) {
		
	}
}
