package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.ModPack;

import javax.swing.*;
import java.awt.*;

public class UpdatePanel extends JPanel {
	private LabelField packName;
	private ModPack pack;

    public UpdatePanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        packName = new LabelField("Pack Name");
        packName.lock("Currently opened pack");
        this.add(packName);

        FileSelecter zip = new FileSelecter("Zip", -1, "zip");
        this.add(zip);

        LabelField version = new LabelField("Version");
        this.add(version);
    }

	public void setPack(ModPack pack) {
		this.pack = pack;
		packName.setText(pack.name);
	}
}
