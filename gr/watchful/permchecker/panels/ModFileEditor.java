package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.ModFile;

import java.awt.Dimension;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ModFileEditor extends JPanel {
	private NamedScrollingListPanel<String> names;
	private NamedScrollingListPanel<String> IDs;
	
	public ModFileEditor(Dimension size, ModFile modFile) {
		names = new NamedScrollingListPanel<String>("Names", 100, modFile.names);
		this.add(names);
		IDs = new NamedScrollingListPanel<String>("IDs", 100, modFile.IDs);
		this.add(IDs);
	}
	
	public void setModFile(ModFile modFile) {
		names.setModel(modFile.names);
		IDs.setModel(modFile.IDs);
	}
}
