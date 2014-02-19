package gr.watchful.permchecker.panels;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModInfo;

@SuppressWarnings("serial")
public class ModInfoEditor extends JPanel {
	public ModInfo modInfo;

	private LabelField name;
	private LabelField author;
	private LabelField link;
	private LabelField licenseImageLink;
	private LabelField licensePermissionLink;
	private PermType permType;
	
	public ModInfoEditor(Dimension size) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setMinimumSize(new Dimension(200, 100));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		this.setPreferredSize(size);
		this.setAlignmentY(0);
		
		name = new LabelField("Name");
		this.add(name);
		author = new LabelField("Author");
		this.add(author);
		link = new LabelField("Link");
		this.add(link);
		licenseImageLink = new LabelField("Image");
		this.add(licenseImageLink);
		licensePermissionLink = new LabelField("Perm Link");
		this.add(licensePermissionLink);
		
		permType = new PermType();
		this.add(permType);
	}
	
	public void setMod(ModInfo mod, String shortName) {
		if(mod == null) {
			mod = new ModInfo(shortName);
		}
		name.setText(mod.modName);
		author.setText(mod.modAuthor);
		link.setText(mod.modUrl);
		licenseImageLink.setText(mod.licenseImageLink);
		licensePermissionLink.setText(mod.licenseLink);
		if(Globals.getInstance().packType == Globals.PUBLIC) {
			permType.setType(mod.publicPolicy);
		} else {
			permType.setType(mod.privatePolicy);
		}
	}
}
