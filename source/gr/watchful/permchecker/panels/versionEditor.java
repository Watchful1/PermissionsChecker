package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.SortedListModel;

import javax.swing.*;
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

    public VersionEditor(String name) {
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
                String result = (String) JOptionPane.showInputDialog(
                        Globals.getInstance().mainFrame,
                        "", "New Version", JOptionPane.PLAIN_MESSAGE);
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
        model.add(0, newVersion);
		recommendedIndex++;
		setRecommendedVersion(newVersion);
    }

    private void removeSelectedVersion() {
        if(list.getSelectedIndex() >= 0) {
            model.remove(list.getSelectedIndex());
        }
    }

    public ArrayList<String> getVersions() {
		ArrayList<String> temp = model.getArrayList();
		if(recommendedIndex != -1) temp.set(recommendedIndex, temp.get(recommendedIndex).replaceAll("\\s\\*",""));
        return temp;
    }

    public void setVersions(ArrayList<String> versions) {
        model.clear();
        for(String version : versions) {
            model.addElement(version);
        }
    }

	public void setRecommendedVersion(String recommendedVersion) {
		int index = model.getIndexByString(recommendedVersion);
		if(index == -1) {
			System.out.println("Can't find version "+recommendedVersion);
			return;
		}
		if(index == recommendedIndex) return;
		if(recommendedIndex != -1) {
			model.setElement(model.get(recommendedIndex).replaceAll("\\s\\*",""), recommendedIndex);
		}
		model.setElement(model.get(index)+" *", index);
		recommendedIndex = index;
		list.repaint();
	}

	public String getRecommendedVersion() {
		return model.get(recommendedIndex).replaceAll("\\s\\*","");
	}
}
