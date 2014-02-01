
package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.ModFile;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ModFileEditor extends JPanel {
    private JTextField name;

    public ModFileEditor(Dimension size) {
        name = new JTextField("NOTTEST");
        this.add(name);
    }

    public void setModFile(ModFile modFile) {

    }
}
