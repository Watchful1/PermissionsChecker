package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModFile;
import gr.watchful.permchecker.datastructures.ModInfo;
import gr.watchful.permchecker.datastructures.SavesMods;
import gr.watchful.permchecker.modhandling.ModNameRegistry;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ModFileEditor extends JPanel implements SavesMods {
	private NamedScrollingListPanel<String> names;
	private NamedScrollingListPanel<String> IDs;
	
	private ModInfoEditor modInfoEditor;
	
	public ModFileEditor(Dimension size, ModFile modFile) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setAlignmentX(0);
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
		listPanel.setMinimumSize(new Dimension(300,100));
		listPanel.setMaximumSize(new Dimension(300,100));
		listPanel.setPreferredSize(new Dimension(300,100));
		listPanel.setAlignmentX(0);
		
		names = new NamedScrollingListPanel<String>("Names", 100, modFile.names);
		listPanel.add(names);
		IDs = new NamedScrollingListPanel<String>("IDs", 100, modFile.IDs);
		listPanel.add(IDs);
		
		this.add(listPanel);
		
		modInfoEditor = new ModInfoEditor(new Dimension(500,900));
		modInfoEditor.editShortName(true);
		this.add(modInfoEditor);
		
		modInfoEditor.addSaveListener(this);
	}
	
	public void setModFile(ModFile modFile) {
		names.setModel(modFile.names);
		IDs.setModel(modFile.IDs);
		modInfoEditor.setMod(modFile.tempInfo, "");
	}

	@Override
	public void saveMod(ModInfo modInfo) {
		ModNameRegistry nameRegistry = Globals.getInstance().nameRegistry;
		for(int i=0; i<IDs.getModel().getSize(); i++) {
			System.out.println("ID: "+IDs.getModel().getElementAt(i)+" Shortname: "+modInfo.shortName);
			nameRegistry.addShortName(modInfo.shortName, IDs.getModel().getElementAt(i));
		}
	}
}
