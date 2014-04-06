package gr.watchful.permchecker.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import gr.watchful.permchecker.datastructures.ModPack;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ModPacksPanel extends JPanel {
	private DefaultListModel<ModPack> modPacksModel;
	private NamedScrollingListPanel<ModPack> modPacksPanel;
	private JPanel fieldPanel;
	private JButton savePackButton;
	
	public ModPacksPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		modPacksModel = new DefaultListModel<ModPack>();
		modPacksPanel = new NamedScrollingListPanel<ModPack>("ModPacks", 100, modPacksModel);
		this.add(modPacksPanel);
		
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
		
		savePackButton = new JButton("Save");
		savePackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveCurrentPack();
			}
		});
		fieldPanel.add(savePackButton);
	}
	
	public void loadPacks(File folder) {
		if(!folder.exists() || !folder.isDirectory()) return;
		for(File pack : folder.listFiles()) {
			ModPack temp = ModPack.loadObject(pack);
			if(temp != null) {
				modPacksModel.addElement(temp);
			}
		}
	}
	
	public void saveCurrentPack() {
		if(!modPacksPanel.getSelected().saveThisObject()) System.out.println("Couldn't save pack");
	}
}
