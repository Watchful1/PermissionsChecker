package gr.watchful.permchecker.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModInfo;
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
	private LabelField customImageLink;
	private LabelField customPermissionLink;
	private PermType permType;
	
	public ModInfoEditor(Dimension size) {
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
		
		customPermissionLink = new LabelField("Perm Link"); //TODO better handle "PM"
		this.add(customPermissionLink);
		customImageLink = new LabelField("Perm Image");
		this.add(customImageLink);
		
		permType = new PermType();
		this.add(permType);
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
		customPermissionLink.setText(modInfo.customLicenseLink);
		customImageLink.setText(modInfo.customImageLink);
		if(Globals.getInstance().packType == Globals.PUBLIC) {
			permType.setType(modInfo.publicPolicy);
		} else {
			permType.setType(modInfo.privatePolicy);
		}
		if(permType.getType() == ModInfo.OPEN || permType.getType() == ModInfo.FTB) {//TODO non-ftb launcher
			customPermissionLink.setEditable(false);
			customImageLink.setEditable(false);
		} else {
			customPermissionLink.setEditable(true);
			customImageLink.setEditable(true);
		}
	}
	
	public void save() {
		ModNameRegistry nameRegistry = Globals.getInstance().nameRegistry;
		
		if(!name.getText().equals(modInfo.modName) || !author.getText().equals(modInfo.modAuthor) || 
				!link.getText().equals(modInfo.modLink) || !licensePermissionLink.getText().equals(modInfo.licenseLink) ||
				!licenseImageLink.getText().equals(modInfo.licenseImageLink)) {
			modInfo.officialSpreadsheet = false;
			modInfo.modName = name.getText();
			modInfo.modAuthor = author.getText();
			modInfo.modLink = link.getText();
			modInfo.licenseLink = licensePermissionLink.getText();
			modInfo.licenseImageLink = licenseImageLink.getText();
		}
		
		modInfo.customLicenseLink = customPermissionLink.getText();
		modInfo.customImageLink = customImageLink.getText();
		nameRegistry.addModInfo(ModNameRegistry.buildShortName(modInfo.modName), modInfo);
	}
}
