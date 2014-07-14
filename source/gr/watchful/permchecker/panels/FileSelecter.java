package gr.watchful.permchecker.panels;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

//yes I know this is spelled wrong
public class FileSelecter extends JPanel {
    private JLabel label;
    private JLabel status;
    private JButton selectButton;
    private File file;
    private int maxKilobytes;
    private String allowedType;

	private ArrayList<ActionListener> listeners;

    public FileSelecter(String name, int maxKilobytesIn, String allowedTypeIn) {
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
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home")); //TODO change this to the working folder
                //fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setFileFilter(new FileNameExtensionFilter(allowedType+" files", allowedType));
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File result = fileChooser.getSelectedFile();
                    setFile(result);
                }
            }
        });
        this.add(selectButton);

		listeners = new ArrayList<>();
    }

    public void setFile(File fileIn) {
        file = fileIn;
        if(file == null || !file.exists()) return;
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
		notifyListeners();
    }

    public File getFile() {
        return file;
    }

	public void addListener(ActionListener listener) {
		listeners.add(listener);
	}

	private void notifyListeners() {
		for(ActionListener listener : listeners) {
			listener.actionPerformed(new ActionEvent(this, 0, ""));
		}
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
