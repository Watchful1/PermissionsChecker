package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.ModInfo;
import gr.watchful.permchecker.panels.LabelField;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ModEditor extends JPanel {
	private LabelField name;
	private LabelField author;
	private LabelField link;
	private LabelField imageLink;
	private LabelField permissionLink;
	
	public ModEditor(Dimension size) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		name = new LabelField("Name");
		this.add(name);
		author = new LabelField("Author");
		this.add(author);
		link = new LabelField("Link");
		this.add(link);
		imageLink = new LabelField("Image");
		this.add(imageLink);
		permissionLink = new LabelField("Perm Link");
		this.add(permissionLink);
		
	}
	
	public void setMod(ModInfo mod, String shortName) {
		if(mod == null) {
			mod = new ModInfo(shortName);
		}
		name.setText(mod.modName);
		author.setText(mod.modAuthor);
		link.setText(mod.modUrl);
		imageLink.setText(mod.imageLink);
		permissionLink.setText(mod.permLink);
	}
}