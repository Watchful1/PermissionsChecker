package gr.watchful.permchecker.panels;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ModEditor extends JPanel {
	private JTextField modName;
	
	public ModEditor(Dimension size) {
		modName = new JTextField("TEST");
		this.add(modName);
	}
}
