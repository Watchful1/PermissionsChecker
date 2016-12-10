package gr.watchful.permchecker.panels;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class CheckboxField extends JPanel {
	private JLabel label;
	private JCheckBox checkBox;
	private ChangeListener changeListener;

	public CheckboxField(String name) {
		this(name, null);
	}

	public CheckboxField(String name, ChangeListener changeListener) {
		this.changeListener = changeListener;

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setAlignmentX(0);

		label = new JLabel(name);
		label.setMinimumSize(new Dimension(90, 21));
		label.setMaximumSize(new Dimension(90, 21));
		label.setPreferredSize(new Dimension(90, 21));
		this.add(label);

		checkBox = new JCheckBox();
		checkBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				notifyChanged();
			}
		});
		this.add(checkBox);
	}

	public void setChecked(boolean checked) {
		checkBox.setSelected(checked);
	}

	public boolean isChecked() {
		return checkBox.isSelected();
	}

	public void notifyChanged() {
		if(changeListener == null) return;
		changeListener.stateChanged(new ChangeEvent(this));
	}
}
