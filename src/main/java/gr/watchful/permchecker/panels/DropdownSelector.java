package gr.watchful.permchecker.panels;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class DropdownSelector extends JPanel {
	private JLabel label;
	private JComboBox<String> chooser;
	private DefaultComboBoxModel<String> items;
    private ChangeListener changeListener;
    private String oldVersion;
	private boolean paused;

	public DropdownSelector(String name) {
        this(name, null);
    }

    public DropdownSelector(String name, ChangeListener changeListener) {
        this.changeListener = changeListener;
        oldVersion = "";
		paused = false;

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
                if(oldVersion.equals(getSelection())) return;
                oldVersion = getSelection();

                notifyChanged();
            }
        });
		this.add(chooser);
	}

	public void setSelection(String selection) {
		int index = items.getIndexOf(selection);
		if(index == -1) {
			System.out.println("Couldn't find selection "+selection);
			return;
		}
        oldVersion = selection;
		chooser.setSelectedIndex(index);
	}

	public void setSelections(ArrayList<String> selections) {
		paused = true;
		items.removeAllElements();
		for(String selection : selections) {
			items.addElement(selection);
		}
		paused = false;
	}

	public String getSelection() {
		return (String) chooser.getSelectedItem();
	}

    public void notifyChanged() {
        if(changeListener == null) return;
		if(paused) return;

        changeListener.stateChanged(new ChangeEvent(this));
    }
}
