package gr.watchful.permchecker.panels;

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
	private ButtonGroup buttonGroup;

	public ForgeEditor(String name) {
		this(name, null);
	}

	public ForgeEditor(String name, ChangeListener changeListener) {
		this.changeListener = changeListener;
		oldVersion = -1;

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setAlignmentX(0);

		label = new JLabel(name);
		label.setMinimumSize(new Dimension(90, 21));
		label.setMaximumSize(new Dimension(90, 21));
		label.setPreferredSize(new Dimension(90, 21));
		this.add(label);

		this.add(Box.createHorizontalGlue());

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
				oldVersion = getForgeVersion();
				notifyChanged();
			}
		});
		this.add(forgeVersionEditor);
	}


	public void setForgeVersion(int forgeVersion) {
		oldVersion = forgeVersion;
		if(forgeVersion == -1) {
			forgeVersionEditor.setText("");
		} else {
			forgeVersionEditor.setText(Integer.toString(forgeVersion));
		}
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
		notifyChanged();
	}
}
