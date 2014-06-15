package gr.watchful.permchecker.panels;

import javax.swing.*;
import java.awt.*;

public class FileSelecter extends JPanel {
    private JLabel label;
    private JLabel status;
    private JButton selectButton;

    public FileSelecter(String name) {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setAlignmentX(0);
        label = new JLabel(name);
        label.setMinimumSize(new Dimension(90, 21));
        label.setMaximumSize(new Dimension(90, 21));
        label.setPreferredSize(new Dimension(90, 21));
        this.add(label);

        this.add(Box.createHorizontalGlue());

        status = new JLabel("test");
        status.setMinimumSize(new Dimension(90, 21));
        status.setMaximumSize(new Dimension(90, 21));
        status.setPreferredSize(new Dimension(90, 21));
        status.setHorizontalAlignment(JLabel.RIGHT);
        this.add(status);
        this.add(Box.createRigidArea(new Dimension(10,0)));

        selectButton = new JButton("Pick");
        this.add(selectButton);
    }
}
