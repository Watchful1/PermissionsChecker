package gr.watchful.permchecker.panels;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

@SuppressWarnings("serial")
public class LabelField extends JPanel {
	private JLabel label;
	private JTextField textField;
	private ChangeListener changeListener;
	private String oldText;

	public LabelField(String name) {
		this(name, null);
	}

	public LabelField(String name, ChangeListener changeListener) {
		this(name, Integer.MAX_VALUE, changeListener);
	}

	public LabelField(String name, int max, ChangeListener changeListener) {
		this.changeListener = changeListener;
		oldText = "";

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setAlignmentX(0);

		label = new JLabel(name);
		label.setMinimumSize(new Dimension(90, 21));
		label.setMaximumSize(new Dimension(90, 21));
		label.setPreferredSize(new Dimension(90, 21));
		this.add(label);
		
		textField = new JTextField();
		textField.setMaximumSize(new Dimension(max, 21));
		textField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (oldText.equals(getText())) return;
				oldText = getText();
				notifyChanged();
			}
		});
		this.add(textField);
	}
	
	public void setText(String in) {
		oldText = in;
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

	public void notifyChanged() {
		if(changeListener == null) return;
		changeListener.stateChanged(new ChangeEvent(this));
	}
}
