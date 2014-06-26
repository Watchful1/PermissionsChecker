package gr.watchful.permchecker.panels;

import javax.swing.*;
import java.awt.*;

public class UpdatePanel extends JPanel {


    public UpdatePanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        LabelField packName = new LabelField("Pack Name");
        packName.lock("");
        packName.setAlignmentX(CENTER_ALIGNMENT);
        this.add(packName);

        FileSelecter zip = new FileSelecter("Zip", -1, "zip");
        zip.setAlignmentX(CENTER_ALIGNMENT);
        this.add(zip);

        LabelField version = new LabelField("Version");
        version.setAlignmentX(CENTER_ALIGNMENT);
        this.add(version);
    }
}
