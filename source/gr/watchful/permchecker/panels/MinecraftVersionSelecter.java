package gr.watchful.permchecker.panels;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MinecraftVersionSelecter extends JPanel {
	private JLabel label;
	private JComboBox<String> chooser;
	private DefaultComboBoxModel<String> items;

	public MinecraftVersionSelecter(String name) {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 21));
		this.setAlignmentX(0);

		label = new JLabel(name);
		label.setMinimumSize(new Dimension(90, 21));
		label.setMaximumSize(new Dimension(90, 21));
		label.setPreferredSize(new Dimension(90, 21));
		this.add(label);

		items = new DefaultComboBoxModel<>();

		chooser = new JComboBox<>(items);
		this.add(chooser);
	}

	public void setVersion(String version) {
		int index = items.getIndexOf(version);
		if(index == -1) {
			System.out.println("Couldn't find minecraft version "+version);
			return;
		}
		chooser.setSelectedIndex(index);
	}

	public void setVersions(ArrayList<String> versions) {
		items.removeAllElements();
		for(String version : versions) {
			items.addElement(version);
		}
	}

	public String getVersion() {
		return (String) chooser.getSelectedItem();
	}
}
