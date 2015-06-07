package gr.watchful.permchecker.panels;

import javax.swing.*;
import java.awt.*;

public class RecommendedVersionEditor extends JPanel {
    private JLabel label;
    private JComboBox<String> chooser;
    private DefaultComboBoxModel<String> items;

    public RecommendedVersionEditor(String name) {
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

    public void addVersion(String version) {
        items.insertElementAt(version, 0);
        chooser.setSelectedIndex(0);
    }

    public void removeVersion(int index) {
        System.out.println("Removing version "+index);
        if(chooser.getSelectedIndex() == index) {
            int newIndex = chooser.getSelectedIndex() - 1;
            if(newIndex < 0) newIndex = 0;
            if(newIndex >= chooser.getItemCount()) newIndex = chooser.getItemCount() - 1;
            chooser.setSelectedIndex(newIndex);
        }
        items.removeElementAt(index);
    }

    public String getRecommendedVersion() {
        return (String)chooser.getSelectedItem();
    }

    /**
     * You must set the available metaVersions in the VersionEditor before using this
     */
    public void setRecommendedVersion(String version) {
        items.setSelectedItem(version);
    }
}
