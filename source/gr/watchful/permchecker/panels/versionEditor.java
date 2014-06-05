package gr.watchful.permchecker.panels;

import javax.swing.*;
import java.awt.*;

public class versionEditor extends JPanel {
    private JLabel label;
    private JList<String> list;

    public versionEditor(String name) {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setAlignmentX(0);

        label = new JLabel(name);
        label.setMinimumSize(new Dimension(90, 21));
        label.setMaximumSize(new Dimension(90, 21));
        label.setPreferredSize(new Dimension(90, 21));
        this.add(label);

        list = new JList<>();
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        this.add(scrollPane);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        JButton addButton = new JButton("+");
        addButton.setMaximumSize(new Dimension(42, 30));
        buttonPanel.add(addButton);
        JButton removeButton = new JButton("-");
        removeButton.setMaximumSize(new Dimension(42, 30));
        buttonPanel.add(removeButton);

        this.add(buttonPanel);
    }
}
