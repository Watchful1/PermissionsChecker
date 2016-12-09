package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.ForgeType;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ForgeEditor extends JPanel implements ActionListener {
	private JLabel label;
	private JTextField forgeVersionEditor;
	private ChangeListener changeListener;
	private int oldVersion;
	private ForgeType oldType;
	private JRadioButton recommendedButton;
	private JRadioButton latestButton;
	private ButtonGroup buttonGroup;

	public ForgeEditor(String name) {
		this(name, null);
	}

	public ForgeEditor(String name, ChangeListener changeListener) {
		this.changeListener = changeListener;
		oldVersion = -1;
		oldType = ForgeType.RECOMMENDED;

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setAlignmentX(0);

		label = new JLabel(name);
		label.setMinimumSize(new Dimension(90, 21));
		label.setMaximumSize(new Dimension(90, 21));
		label.setPreferredSize(new Dimension(90, 21));
		this.add(label);

		this.add(Box.createHorizontalGlue());

		buttonGroup = new ButtonGroup();
		this.add(new JLabel("Recommended"));
		recommendedButton = new JRadioButton();
		recommendedButton.addActionListener(this);
		buttonGroup.add(recommendedButton);
		this.add(recommendedButton);

		this.add(new JLabel("  Latest"));
		latestButton = new JRadioButton();
		latestButton.addActionListener(this);
		buttonGroup.add(latestButton);
		this.add(latestButton);

		this.add(new JLabel("  Version"));
		forgeVersionEditor = new JTextField();
		forgeVersionEditor.setMaximumSize(new Dimension(120, 21));
		forgeVersionEditor.setPreferredSize(new Dimension(120, 21));
		forgeVersionEditor.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// do nothing
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(oldVersion == getForgeVersion()) return;
				if(getForgeVersion() == -1) setForgeType(ForgeType.RECOMMENDED);
				else setForgeType(ForgeType.VERSION);
				oldVersion = getForgeVersion();
				notifyChanged();
			}
		});
		this.add(forgeVersionEditor);
	}

	public void setForgeType(ForgeType forgeType) {
		oldType = forgeType;
		if(forgeType.equals(ForgeType.RECOMMENDED)) {
			setForgeVersion(-1);
			latestButton.setSelected(false);

			recommendedButton.setSelected(true);
		} else if(forgeType.equals(ForgeType.LATEST)) {
			setForgeVersion(-1);
			recommendedButton.setSelected(false);

			latestButton.setSelected(true);
		} else {
			buttonGroup.clearSelection();
		}
	}

	public void setForgeVersion(int forgeVersion) {
		oldVersion = forgeVersion;
		if(forgeVersion == -1) {
			forgeVersionEditor.setText("");
		} else {
			forgeVersionEditor.setText(Integer.toString(forgeVersion));
		}
	}

	public ForgeType getForgeType() {
		if(recommendedButton.isSelected()) {
			return ForgeType.RECOMMENDED;
		} else if(latestButton.isSelected()) {
			return ForgeType.LATEST;
		} else if(getForgeVersion() != -1) {
			return ForgeType.VERSION;
		}
		return null; //shouldn't happen
	}

	public int getForgeVersion() {
		if(forgeVersionEditor.getText().equals("")) return -1;

		int version;
		try {
			version = Integer.parseInt(forgeVersionEditor.getText());
		} catch (Exception e) {
			return -1;
		}
		return version;
	}

	public void notifyChanged() {
		if(changeListener == null) return;
		changeListener.stateChanged(new ChangeEvent(this));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(oldType.equals(getForgeType())) return;
		oldType = getForgeType();
		setForgeVersion(-1);
		notifyChanged();
	}
}
