package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.ModInfo;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

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
		//modInfoEditor.setMinimumSize(new Dimension(200, 100));
		//modInfoEditor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		//modInfoEditor.setPreferredSize(size);
		this.add(modInfoEditor);
	}
	
	public void setMod(ModInfo mod, String shortName) {
		modInfoEditor.setMod(mod, shortName);
	}
}