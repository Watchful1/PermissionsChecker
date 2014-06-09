package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.Globals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class VersionEditor extends JPanel {
    private JLabel label;
    private JList<String> list;
    private DefaultListModel<String> model;
    private RecommendedVersionEditor recommendedVersionEditor;

    public VersionEditor(String name, RecommendedVersionEditor recommendedVersionEditorIn) {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setAlignmentX(0);

        label = new JLabel(name);
        label.setMinimumSize(new Dimension(90, 21));
        label.setMaximumSize(new Dimension(90, 21));
        label.setPreferredSize(new Dimension(90, 21));
        this.add(label);

        recommendedVersionEditor = recommendedVersionEditorIn;

        model = new DefaultListModel<>();
        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
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
                        "", "New Version",
                        JOptionPane.PLAIN_MESSAGE);
                if(result != null) addVersion(result);
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

        this.add(buttonPanel);
    }

    private void addVersion(String newVersion) {
        model.insertElementAt(newVersion, 0);
        recommendedVersionEditor.addVersion(newVersion);
    }

    private void removeSelectedVersion() {
        if(list.getSelectedIndex() >= 0) {
            recommendedVersionEditor.removeVersion(list.getSelectedIndex());
            model.removeElementAt(list.getSelectedIndex());
        }
    }

    public ArrayList<String> getVersions() {
        ArrayList returnList = new ArrayList();
        Collections.addAll(returnList, model.toArray());
        return returnList;
    }

    public void setVersions(ArrayList<String> versions) {
        model.clear();
        for(String version : versions) {
            model.addElement(version);
        }
    }
}
