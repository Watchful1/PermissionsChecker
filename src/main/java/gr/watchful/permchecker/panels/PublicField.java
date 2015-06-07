package gr.watchful.permchecker.panels;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PublicField extends JPanel {
	private JToggleButton toggleButton;
	private ChangeListener changeListener;

	public PublicField(ChangeListener changeListener) {
		this.changeListener = changeListener;
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setAlignmentX(0);

		JLabel left = new JLabel("Private");
		this.add(left);

		this.add(Box.createRigidArea(new Dimension(10, 1)));

		toggleButton = new JToggleButton();

		try {
            System.out.println(ClassLoader.getSystemResource("images/toggleLeft.png"));
			Image left1 = ImageIO.read(ClassLoader.getSystemResource("images/toggleLeft.png"));
			Image right1 = ImageIO.read(ClassLoader.getSystemResource("images/toggleRight.png"));
			toggleButton.setIcon(new ImageIcon(left1));
			toggleButton.setSelectedIcon(new ImageIcon(right1));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(toggleButton.getIcon() != null) {
			Dimension temp2 = new Dimension(toggleButton.getIcon().getIconWidth()+2, toggleButton.getIcon().getIconHeight()+2);
			toggleButton.setMaximumSize(temp2);
			toggleButton.setMinimumSize(temp2);
			toggleButton.setPreferredSize(temp2);

			toggleButton.setBorderPainted(false);
			toggleButton.setContentAreaFilled(false);
			toggleButton.setOpaque(false);
			toggleButton.setFocusPainted(false);
		}
		toggleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stateChanged();
			}
		});
		this.add(toggleButton);

		this.add(Box.createRigidArea(new Dimension(10, 1)));

		JLabel right = new JLabel("Public");
		this.add(right);
	}

	private void updateImages() {
		if(toggleButton.isSelected()) {
			toggleButton.setPressedIcon(toggleButton.getIcon());
		} else {
			toggleButton.setPressedIcon(toggleButton.getSelectedIcon());
		}
	}

	public boolean isPublic() {
		return toggleButton.isSelected();
	}

	public void setPublic(boolean isPublic) {
		toggleButton.setSelected(isPublic);
		updateImages();
	}

	private void stateChanged() {
		updateImages();
		changeListener.stateChanged(new ChangeEvent(this));
	}
}