package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.SortedListModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class VersionEditor extends JPanel {
    private JLabel label;
    private JList<String> list;
    private SortedListModel<String> model;
	private int recommendedIndex;
    private ChangeListener changeListener;

    public VersionEditor(String name) {
        this(name, null);
    }

    public VersionEditor(String name, ChangeListener changeListener) {
        this.changeListener = changeListener;

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setAlignmentX(0);

        label = new JLabel(name);
        label.setMinimumSize(new Dimension(90, 21));
        label.setMaximumSize(new Dimension(90, 21));
        label.setPreferredSize(new Dimension(90, 21));
        this.add(label);

		recommendedIndex = -1;

        model = new SortedListModel<>();
        list = new JList(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        this.add(scrollPane);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        JButton addButton = new JButton("+");
        addButton.setMaximumSize(new Dimension(42, 30));
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
				String tempVersion = "1.0.0";
				if(model.getSize() > 0) tempVersion = model.get(0);
                String result = (String) JOptionPane.showInputDialog(
                        Globals.getInstance().mainFrame,
                        "", "New Version", JOptionPane.PLAIN_MESSAGE, null, null, tempVersion);
                if(result != null) {
                    if(result.matches("\\d(\\.\\d+)*")) {
                        addVersion(result);
                    } else {
                        JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
                                "Version must only include numbers and periods");
                    }
                }
            }
        });
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("-");
        removeButton.setMaximumSize(new Dimension(42, 30));
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedVersion();
            }
        });
        buttonPanel.add(removeButton);

		JButton recommendButton = new JButton("*");
		recommendButton.setMaximumSize(new Dimension(42, 30));
		recommendButton.setToolTipText("Set recommended version");
		recommendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setRecommendedVersion(list.getSelectedValue());
			}
		});
		buttonPanel.add(recommendButton);

        this.add(buttonPanel);
    }

    private void addVersion(String newVersion) {
		for(String version : getVersions()) {
			if(version.equals(newVersion)) {
				System.out.println("Version "+newVersion+" already exists, can't add");
				return;
			}
		}
        model.add(0, newVersion);
		if(recommendedIndex != -1) recommendedIndex++;
		setRecommendedIndex(0, true);
        notifyChanged();
    }

    private void removeSelectedVersion() {
        if(list.getSelectedIndex() > -1) {
			int previousSelected = list.getSelectedIndex();
			model.remove(previousSelected);
			if(model.getSize() == 0) {
				recommendedIndex = -1;
			} else if(recommendedIndex == previousSelected) {
				setRecommendedIndex(recommendedIndex + 1, true);
			} else if(recommendedIndex > previousSelected) {
				recommendedIndex--;
			}
            notifyChanged();
        }
    }

    public ArrayList<String> getVersions() {
		ArrayList<String> temp = model.getArrayList();
		if(recommendedIndex != -1 && model.getSize() != 0) temp.set(recommendedIndex, temp.get(recommendedIndex).replaceAll("\\s\\*",""));
        return temp;
    }

    public void setVersions(ArrayList<String> versions) {
		recommendedIndex = -1;
        model.clear();
        for(String version : versions) {
            model.addElement(version);
        }
    }

	public void setRecommendedVersion(String recommendedVersion) {
		if(recommendedVersion == null) return;
		int index = model.getIndexByString(recommendedVersion);
		if(index == -1) {
			System.out.println("Can't find version "+recommendedVersion);
			return;
		}
		setRecommendedIndex(index, false);
	}

	private void setRecommendedIndex(int index, boolean silent) {
		if(index == recommendedIndex) return;
		if(index < 0) index = 0;
		if(index > model.getSize() - 1) index = model.getSize() - 1;
		boolean first = false;
		if(recommendedIndex > -1 && recommendedIndex < model.getSize()) {
			model.setElement(model.get(recommendedIndex).replaceAll("\\s\\*",""), recommendedIndex);
		} else {
			first = true;
		}
		model.setElement(model.get(index)+" *", index);
		recommendedIndex = index;
		list.repaint();
        if(!first && !silent) notifyChanged();
	}

	public String getRecommendedVersion() {
		if(model.getSize() == 0) return null;
		return model.get(recommendedIndex).replaceAll("\\s\\*","");
	}

    public void notifyChanged() {
        if(changeListener == null) return;
        changeListener.stateChanged(new ChangeEvent(this));
    }
}
