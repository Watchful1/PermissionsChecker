package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.ModFile;
import gr.watchful.permchecker.datastructures.ModInfo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ModEditor extends JPanel {
	private ModInfoEditor modInfoEditor;
	
	public ModEditor(Dimension size) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.setMinimumSize(new Dimension(200, 100));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		this.setPreferredSize(size);
		
		this.setAlignmentY(0);
		modInfoEditor = new ModInfoEditor(size);
		this.add(modInfoEditor);
	}
	
	public void setMod(ModInfo mod, String shortName, ModFile modFile) {
		modInfoEditor.setMod(mod, shortName, modFile);
	}
}