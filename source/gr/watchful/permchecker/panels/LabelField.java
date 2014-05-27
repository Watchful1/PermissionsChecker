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
    private String lockReason;
	
	public LabelField(String name) {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setAlignmentX(0);
		label = new JLabel(name);
		label.setMinimumSize(new Dimension(90, 21));
		label.setMaximumSize(new Dimension(90, 21));
		label.setPreferredSize(new Dimension(90, 21));
		this.add(label);
		
		textField = new JTextField();
		textField.setMinimumSize(new Dimension(300 - label.getMinimumSize().width, 21));
		textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 21));
		this.add(textField);
	}
	
	public void setText(String in) {
		textField.setText(in);
	}
	
	public String getText() {
		return textField.getText();
	}

    public void lock(String lockReason) {
        textField.setToolTipText(lockReason);
        textField.setEditable(false);
    }

    public void unLock() {
        textField.setToolTipText("");
        textField.setEditable(true);
    }
}
