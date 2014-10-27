package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModFile;
import gr.watchful.permchecker.datastructures.ModInfo;
import gr.watchful.permchecker.datastructures.SavesMods;
import gr.watchful.permchecker.modhandling.ModNameRegistry;
import gr.watchful.permchecker.utils.FileUtils;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ModFileEditor extends JPanel implements SavesMods {
	private NamedScrollingListPanel<String> names;
	private NamedScrollingListPanel<String> IDs;
	private ModFile modFile;
	
	private ModInfoEditor modInfoEditor;
	
	public ModFileEditor(Dimension size, ModFile modFile) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentX(0);
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
		listPanel.setMinimumSize(new Dimension(300,100));
		listPanel.setMaximumSize(new Dimension(300,100));
		listPanel.setPreferredSize(new Dimension(300,100));
		listPanel.setAlignmentX(0);
		
		names = new NamedScrollingListPanel<>("Names", 100, modFile.names);
		listPanel.add(names);
		IDs = new NamedScrollingListPanel<>("IDs", 100, modFile.IDs);
		listPanel.add(IDs);
		
		this.add(listPanel);
		
		modInfoEditor = new ModInfoEditor(new Dimension(500,900));
		this.add(modInfoEditor);
		
		modInfoEditor.addSaveListener(this);
	}
	
	public void setModFile(ModFile modFile) {
		if(modFile == null) {
			names.getModel().clear();
			IDs.getModel().clear();
			modInfoEditor.setMod(null, "");
			this.modFile = null;
		} else {
			names.setModel(modFile.names);
			IDs.setModel(modFile.IDs);
			modInfoEditor.setMod(modFile.getInfo(), "");
			this.modFile = modFile;
		}
	}

	@Override
	public void saveMod(ModInfo modInfo) {
		if(IDs.getModel().getSize() > 0) {
			for (int i = 0; i < IDs.getModel().getSize(); i++) {
				//System.out.println("ID: "+IDs.getModel().getElementAt(i)+" Shortname: "+modInfo.shortName);
				Globals.getModPack().addShortName(modInfo.shortName, (String) IDs.getModel().getElementAt(i));
			}
		} else if(modFile != null && modFile.md5 != null && !modFile.md5.equals("")) {
			Globals.getModPack().addShortName(modInfo.shortName, modFile.md5);
		} else if(modFile != null) {
			String md5 = FileUtils.getMD5(modFile.file);
			if(md5 != null) {
				Globals.getModPack().addShortName(modInfo.shortName, md5);
			}
		}
	}
}
