package gr.watchful.permchecker.panels;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MinecraftVersionSelector extends JPanel {
	private JLabel label;
	private JComboBox<String> chooser;
	private DefaultComboBoxModel<String> items;
    private ChangeListener changeListener;
    private String oldVersion;

	public MinecraftVersionSelector(String name) {
        this(name, null);
    }

    public MinecraftVersionSelector(String name, ChangeListener changeListener) {
        this.changeListener = changeListener;
        oldVersion = "";

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
        chooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(oldVersion.equals(getVersion())) return;
                oldVersion = getVersion();

                notifyChanged();
            }
        });
		this.add(chooser);
	}

	public void setVersion(String version) {
		int index = items.getIndexOf(version);
		if(index == -1) {
			System.out.println("Couldn't find minecraft version "+version);
			return;
		}
        oldVersion = version;
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

    public void notifyChanged() {
        if(changeListener == null) return;

        changeListener.stateChanged(new ChangeEvent(this));
    }
}
