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
	private PermType publicPermType;
	
	private ArrayList<SavesMods> saveListeners;
	
	public ModInfoEditor(Dimension size) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setMinimumSize(new Dimension(200, 100));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		this.setPreferredSize(size);
		this.setAlignmentX(JPanel.LEFT_ALIGNMENT);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		save = new JButton("Save Public");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save(true);
			}
		});
		buttonPanel.add(save);

		save = new JButton("Save Private");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save(false);
			}
		});
		buttonPanel.add(save);

		this.add(buttonPanel);
		
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
		shortName.lock("This cannot be changed for mods already in the database");
		
		publicPermType = new PermType();
		this.add(publicPermType);
		
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
		licenseImageLink.setText(modInfo.licenseImage);
		customLink.setText(modInfo.customLink);
		this.shortName.setText(modInfo.shortName);
		publicPermType.setType(modInfo.publicPolicy);
		updateEditableCustom();
	}
	
	public void save(boolean isPublic) {
		if(!name.getText().equals(modInfo.modName) || !author.getText().equals(modInfo.modAuthor) || 
				!link.getText().equals(modInfo.modLink) || !licensePermissionLink.getText().equals(modInfo.licenseLink) ||
				!licenseImageLink.getText().equals(modInfo.licenseImage) || publicPermType.getType() != modInfo.publicPolicy ||
				!shortName.getText().equals(modInfo.shortName)) {
			modInfo.officialSpreadsheet = false;
			modInfo.modName = name.getText();
			modInfo.modAuthor = author.getText();
			modInfo.modLink = link.getText();
			modInfo.licenseLink = licensePermissionLink.getText();
			modInfo.licenseImage = licenseImageLink.getText();
			modInfo.publicPolicy = publicPermType.getType();
			modInfo.shortName = shortName.getText();
		}
		
		modInfo.customLink = customLink.getText();
		modInfo.isPublicPerm = isPublic;
		Globals.getModPack().addModInfo(getShortName(), modInfo);
		
		updateEditableCustom();
		
		notifySaveListeners(modInfo);
		
		Globals.getInstance().rebuildsMods.recheckMods();
	}
	
	public void editShortName(boolean canEdit) {
        if(canEdit) shortName.unLock();
        else shortName.lock("This cannot be changed for mods already in the database");
	}
	
	public String getShortName() {
		return shortName.getText();
	}
	
	private void updateEditableCustom() {
		if(publicPermType.getType() == ModInfo.OPEN || publicPermType.getType() == ModInfo.FTB) {//TODO non-ftb launcher
			customLink.lock("Don't need a link for open permission mods");
		} else {
			customLink.unLock();
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
