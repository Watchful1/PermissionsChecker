package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.Globals;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileSelector extends JPanel {
    private JLabel label;
    private JLabel status;
    private JButton selectButton;
	private JButton cancelButton;
    private File file;
    private int maxKilobytes;
    private String allowedType;
    private ChangeListener changeListener;

    public FileSelector(String name, int maxKilobytesIn, String allowedTypeIn) {
        this(name, maxKilobytesIn, allowedTypeIn, null);
    }

    public FileSelector(String name, int maxKilobytesIn, String allowedTypeIn, ChangeListener changeListener) {
        this.changeListener = changeListener;
        maxKilobytes = maxKilobytesIn;
        allowedType = allowedTypeIn;

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setAlignmentX(0);
        label = new JLabel(name);
        label.setMinimumSize(new Dimension(90, 21));
        label.setMaximumSize(new Dimension(90, 21));
        label.setPreferredSize(new Dimension(90, 21));
        this.add(label);

        this.add(Box.createHorizontalGlue());

        status = new JLabel("No file selected");
        status.setHorizontalAlignment(JLabel.RIGHT);
        this.add(status);

        this.add(Box.createRigidArea(new Dimension(10,0)));

        selectButton = new JButton("Pick");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(Globals.getInstance().preferences.defaultOpenFolder);
                fileChooser.setFileFilter(new FileNameExtensionFilter(allowedType+" files", allowedType));
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File result = fileChooser.getSelectedFile();

					if(result.getParentFile().compareTo(Globals.getInstance().preferences.defaultOpenFolder) != 0) {
						Globals.getInstance().preferences.setDefaultOpenFolder(result.getParentFile());
						Globals.getInstance().savePreferences();
					}

                    setFile(result);
                }
            }
        });
        this.add(selectButton);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setFile(null);
			}
		});
		this.add(cancelButton);
    }

	public void clearSelection() {
		status.setText("No file selected");
		status.setForeground(Color.BLACK);
		file = null;
		cancelButton.setEnabled(false);
		notifyChanged();
	}

    public void setFile(File fileIn) {
        if(fileIn == null || !fileIn.exists()) {
			clearSelection();
			return;
		}
		cancelButton.setEnabled(true);
		file = fileIn;
        int i = file.getName().lastIndexOf('.');
        String ext = "file";
        if(i >= 0) {
            ext = file.getName().substring(i+1);
        }
        if(!ext.equals(allowedType)) {
            status.setText("File is not of type " + allowedType);
            status.setForeground(Color.RED);
        } else {
            status.setText(getSizeName(file.length()) + " " + ext);
            if (maxKilobytes != -1 && file.length() / 1024 > maxKilobytes) {
                status.setText(status.getText() + " is larger than " + maxKilobytes + " kb");
                status.setForeground(Color.RED);
            } else {
                status.setForeground(Color.BLACK);
            }
        }
        notifyChanged();
    }

    public File getFile() {
        return file;
    }

    public void notifyChanged() {
        if(changeListener == null) return;
        changeListener.stateChanged(new ChangeEvent(this));
    }

    public static String getSizeName(long bytes) {
        if(bytes < 1024) return Math.round(bytes) + " bytes";
        bytes /= 1024;
        if(bytes < 1024) return Math.round(bytes) + " kb";
        bytes /= 1024;
        if(bytes < 1024) return Math.round(bytes) + " mb";
        bytes /= 1024;
        if(bytes < 1024) return Math.round(bytes) + " gb";
        bytes /= 1024;
        return Math.round(bytes) + " tb";
    }
}
