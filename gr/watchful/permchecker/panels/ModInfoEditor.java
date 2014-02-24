package gr.watchful.permchecker.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModInfo;
import gr.watchful.permchecker.datastructures.SavesMods;
import gr.watchful.permchecker.modhandling.ModNameRegistry;

@SuppressWarnings("serial")
public class ModInfoEditor extends JPanel {
	public ModInfo modInfo;
	
	private JButton save;

	private LabelField name;
	private LabelField author;
	private LabelField link;
	private LabelField licenseImageLink;
	private LabelField licensePermissionLink;
	private LabelField customLink;
	private LabelField shortName;
	private PermType permType;
	
	private ArrayList<SavesMods> saveListeners;
	
	public ModInfoEditor(Dimension size) {
		//this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setMinimumSize(new Dimension(200, 100));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		this.setPreferredSize(size);
		this.setAlignmentY(0);
		
		save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		this.add(save);
		
		name = new LabelField("Name");
		this.add(name);
		author = new LabelField("Author");
		this.add(author);
		link = new LabelField("Link");
		this.add(link);
		
		licensePermissionLink = new LabelField("License Link");
		this.add(licensePermissionLink);
		licenseImageLink = new LabelField("License Image");
		this.add(licenseImageLink);
		
		customLink = new LabelField("Permission");//TODO better handle "PM"
		this.add(customLink);
		
		shortName = new LabelField("Short Name");
		this.add(shortName);
		shortName.setEditable(false);
		
		permType = new PermType();
		this.add(permType);
		
		saveListeners = new ArrayList<SavesMods>();
	}
	
	public void setMod(ModInfo mod, String shortName) {
		if(mod == null) {
			modInfo = new ModInfo(shortName);
		} else {
			modInfo = mod;
		}
		name.setText(modInfo.modName);
		author.setText(modInfo.modAuthor);
		link.setText(modInfo.modLink);
		licensePermissionLink.setText(modInfo.licenseLink);
		licenseImageLink.setText(modInfo.licenseImageLink);
		customLink.setText(modInfo.customLink);
		this.shortName.setText(modInfo.shortName);
		permType.setType(modInfo.getCurrentPolicy());
		updateEditableCustom();
	}
	
	public void save() {
		ModNameRegistry nameRegistry = Globals.getInstance().nameRegistry;
		
		if(!name.getText().equals(modInfo.modName) || !author.getText().equals(modInfo.modAuthor) || 
				!link.getText().equals(modInfo.modLink) || !licensePermissionLink.getText().equals(modInfo.licenseLink) ||
				!licenseImageLink.getText().equals(modInfo.licenseImageLink) || permType.getType() != modInfo.getCurrentPolicy() ||
				!shortName.getText().equals(modInfo.shortName)) {
			modInfo.officialSpreadsheet = false;
			modInfo.modName = name.getText();
			modInfo.modAuthor = author.getText();
			modInfo.modLink = link.getText();
			modInfo.licenseLink = licensePermissionLink.getText();
			modInfo.licenseImageLink = licenseImageLink.getText();
			modInfo.setCurrentPolicy(permType.getType());
			modInfo.shortName = shortName.getText();
		}
		
		modInfo.customLink = customLink.getText();
		nameRegistry.addModInfo(getShortName()/*ModNameRegistry.buildShortName(modInfo.modName)*/, modInfo);
		
		updateEditableCustom();
		
		notifySaveListeners(modInfo);
		
		Globals.getInstance().main.recheckMods();
		
		nameRegistry.printCustomInfos();
	}
	
	public void editShortName(boolean canEdit) {
		shortName.setEditable(canEdit);
	}
	
	public String getShortName() {
		return shortName.getText();
	}
	
	private void updateEditableCustom() {
		if(permType.getType() == ModInfo.OPEN || permType.getType() == ModInfo.FTB) {//TODO non-ftb launcher
			customLink.setEditable(false);
		} else {
			customLink.setEditable(true);
		}
	}
	
	public void addSaveListener(SavesMods saveMod) {
		saveListeners.add(saveMod);
	}
	
	private void notifySaveListeners(ModInfo modInfo) {
		for(SavesMods savesMod : saveListeners) {
			savesMod.saveMod(modInfo);
		}
	}
}
