package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.ForgeType;

import javax.swing.*;
import java.awt.*;

public class ForgeEditor extends JPanel {
	private JLabel label;
	private JComboBox<ForgeType> forgeTypeEditor;
	private JTextField forgeVersionEditor;

	public ForgeEditor(String name) {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setAlignmentX(0);

		label = new JLabel(name);
		label.setMinimumSize(new Dimension(90, 21));
		label.setMaximumSize(new Dimension(90, 21));
		label.setPreferredSize(new Dimension(90, 21));
		this.add(label);

		forgeTypeEditor = new JComboBox<>(ForgeType.values());
		forgeTypeEditor.setMaximumSize(new Dimension(120, 21));
		this.add(forgeTypeEditor);

		this.add(Box.createHorizontalGlue());

		forgeVersionEditor = new JTextField();
		forgeVersionEditor.setMaximumSize(new Dimension(120, 21));
		forgeVersionEditor.setPreferredSize(new Dimension(120, 21));
		this.add(forgeVersionEditor);
	}

	public void setForgeType(ForgeType forgeType) {
		forgeTypeEditor.setSelectedItem(forgeType);
	}

	public void setForgeVersion(int forgeVersion) {
		forgeVersionEditor.setText(Integer.toString(forgeVersion));
	}

	public ForgeType getForgeType() {
		return (ForgeType) forgeTypeEditor.getSelectedItem();
	}

	public int getForgeVersion() {
		return Integer.parseInt(forgeVersionEditor.getText());
	}
}
