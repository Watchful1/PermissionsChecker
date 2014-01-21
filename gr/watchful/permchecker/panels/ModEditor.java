package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.Mod;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ModEditor extends JPanel {
	private JTextField name;
	private JTextField author;
	private JTextField link;
	private JTextField imageLink;
	private JTextField permissionLink;
	
	public ModEditor(Dimension size) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		name = new JTextField("TEST");
		this.add(name);
		author = new JTextField("TEST");
		this.add(author);
		link = new JTextField("TEST");
		this.add(link);
		imageLink = new JTextField("TEST");
		this.add(imageLink);
		permissionLink = new JTextField("TEST");
		this.add(permissionLink);
	}
	
	public void setMod(Mod mod) {
		
	}
}
