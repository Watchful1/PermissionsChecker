
package gr.watchful.permchecker.panels;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class LabelField extends JPanel {
    private JLabel label;
    private JTextField textField;

    public LabelField(String name) {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setAlignmentX(0);
        label = new JLabel(name);
        label.setMinimumSize(new Dimension(70, 21));
        label.setMaximumSize(new Dimension(70, 21));
        label.setPreferredSize(new Dimension(70, 21));
        this.add(label);

        textField = new JTextField();
        textField.setMinimumSize(new Dimension(300 - label.getMinimumSize().width, 21));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 21));
        this.add(textField);

    }

    public void setText(String in) {
        textField.setText(in);
    }
}
