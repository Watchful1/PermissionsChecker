package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModPack;
import gr.watchful.permchecker.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class UpdatePanel extends JPanel implements ActionListener {
	private LabelField packName;
	private ModPack pack;
	private FileSelecter selecter;

    public UpdatePanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        packName = new LabelField("Pack Name");
        packName.lock("Currently opened pack");
        this.add(packName);

        selecter = new FileSelecter("Zip", -1, "zip");
		selecter.addListener(this);
        this.add(selecter);

        LabelField version = new LabelField("Version");
        this.add(version);
    }

	public void setPack(ModPack pack) {
		this.pack = pack;
		packName.setText(pack.name);
	}

	public void extractPack(File file) {
		if(!file.exists()) {
			System.out.println("Can't extract pack, file doesn't exist!");
			return;
		}

		int i = file.getName().lastIndexOf('.');
		String ext = "file";
		if(i >= 0) {
			ext = file.getName().substring(i+1);
		}
		if(!ext.equals("zip")) {
			System.out.println("Can't extract pack, file isn't a zip");
			return;
		}

		FileUtils.extractZipTo(file, Globals.getInstance().preferences.workingFolder);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		extractPack(selecter.getFile());
	}
}
