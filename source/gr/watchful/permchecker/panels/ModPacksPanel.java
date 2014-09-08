package gr.watchful.permchecker.panels;

import java.awt.*;
import java.io.File;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModPack;
import gr.watchful.permchecker.datastructures.UsesPack;
import gr.watchful.permchecker.utils.FileUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class ModPacksPanel extends JPanel implements UsesPack, ChangeListener {
	private JPanel mainPanel;

	private JPanel buttonPanel;
	private JButton saveButton;

	private JPanel editorPanel;
	private LabelField nameField;
	private LabelField authorField;
	private LabelField shortNameField;
	private LabelField keyField;
	private HTMLField descriptionField;
	private MinecraftVersionSelector minecraftVersionSelector;
	private VersionEditor versionEditor;
	private ForgeEditor forgeEditor;
	private FileSelector iconSelector;
	private FileSelector splashSelector;
	private FileSelector serverSelector;

	private ModPack oldPack;

	private NamedScrollingListPanel<ModPack> modPacksList;
	
	public ModPacksPanel(NamedScrollingListPanel<ModPack> modPacksListIn) {
		this.setLayout(new BorderLayout());

		modPacksList = modPacksListIn;

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		//buttons at the top to manage packs
		/*buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(0f);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				savePack(modPacksList.getSelected());
			}
		});
		buttonPanel.add(saveButton);

		mainPanel.add(buttonPanel);*/

		//fields in the middle to edit pack details
		editorPanel = new JPanel();
		editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));
		editorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		nameField = new LabelField("Name", this);
		editorPanel.add(nameField);
		authorField = new LabelField("Author", this);
		editorPanel.add(authorField);
		shortNameField = new LabelField("ShortName");
		shortNameField.lock("This is autocomputed, changing it breaks lots of stuff");
		editorPanel.add(shortNameField);
		keyField = new LabelField("Pack Key", this);
		editorPanel.add(keyField);
		descriptionField = new HTMLField("Description", this);
		editorPanel.add(descriptionField);
		minecraftVersionSelector = new MinecraftVersionSelector("Minecraft", this);
		minecraftVersionSelector.setVersions(Globals.getInstance().preferences.minecraftVersions);
		minecraftVersionSelector.setVersion(Globals.getInstance().preferences.defaultMinecraftVersion);//TODO should this be here?
		editorPanel.add(minecraftVersionSelector);
		versionEditor = new VersionEditor("Version", this);
		editorPanel.add(versionEditor);
		forgeEditor = new ForgeEditor("Forge", this);
		editorPanel.add(forgeEditor);
		iconSelector = new FileSelector("Icon", 150, "png", this);
		editorPanel.add(iconSelector);
		splashSelector = new FileSelector("Splash", 150, "png", this);
		editorPanel.add(splashSelector);
		serverSelector = new FileSelector("Server", -1, "zip", this);
		editorPanel.add(serverSelector);

		mainPanel.add(editorPanel);

		this.add(mainPanel);
	}
	
	public void savePack(ModPack packIn) {
		if(packIn == null) return;

		ModPack pack = packIn;

		boolean found = false;

		if(shortNameField.getText().equals("")) {
			String shortName = ModPack.generateShortName(nameField.getText());
			for(int i=0; i<modPacksList.getModel().getSize(); i++) {
				if(shortName.equals(modPacksList.getModel().get(i).shortName)) {
					System.out.println("ShortName exists. Create new.");
					String result = (String) JOptionPane.showInputDialog(
						Globals.getInstance().mainFrame, "Shortname exists, pick new shortname",
							"New Shortname", JOptionPane.PLAIN_MESSAGE, null, null, shortName);
					shortName = result;
					i=0;
				}
			}
			shortNameField.setText(shortName);
		}

		if(shortNameField.getText() == null || shortNameField.getText().equals("")) {
			System.out.println("Blank ShortName, can't save");
		}
		if(keyField.getText() == null || keyField.getText().equals("")) {
			System.out.println("Blank Key, can't save");
			return;
		}
		for(int i=0; i<modPacksList.getModel().getSize(); i++) {
			ModPack newPack = modPacksList.getModel().get(i);
			if(keyField.getText().equals(newPack.key)) {
				if(found) {
					System.out.println("Key exists. Can't save.");
					return;
				} else found = true;
			}
		}

		pack.name = nameField.getText();
		pack.author = authorField.getText();
		pack.shortName = shortNameField.getText(); // TODO detect changes here
		pack.key = keyField.getText(); // TODO detect changes here
		pack.description = descriptionField.getText();
		pack.minecraftVersion = minecraftVersionSelector.getVersion();
		pack.versions = versionEditor.getVersions();
		pack.recommendedVersion = versionEditor.getRecommendedVersion();
		pack.forgeType = forgeEditor.getForgeType();
		pack.ForgeVersion = forgeEditor.getForgeVersion();
		pack.icon = iconSelector.getFile();
		pack.splash = splashSelector.getFile();
		pack.server = serverSelector.getFile();

		modPacksList.sortKeepSelected();

		System.out.println("Saving changed pack");
		if(!modPacksList.getSelected().saveThisObject()) System.out.println("Couldn't save pack");
	}

	public void setPack(ModPack pack) {
		nameField.setText(pack.name);
		authorField.setText(pack.author);
		shortNameField.setText(pack.shortName);
		keyField.setText(pack.key);
		descriptionField.setText(pack.description);
		minecraftVersionSelector.setVersion(pack.minecraftVersion);
		versionEditor.setVersions(pack.versions);
		versionEditor.setRecommendedVersion(pack.recommendedVersion);
		forgeEditor.setForgeType(pack.forgeType);
		forgeEditor.setForgeVersion(pack.ForgeVersion);
		iconSelector.setFile(pack.icon);
		splashSelector.setFile(pack.splash);
		serverSelector.setFile(pack.server);
	}

	@Override
	public void updatePack(ModPack modPack) {
		setPack(modPack);
	}

	public boolean fileChanged(FileSelector fileSelector) {
		File tempLocation = new File(Globals.getInstance().preferences.exportFolder +
				File.separator + "temp" + File.separator + fileSelector.getFile().getName());
		if(fileSelector.getFile().equals(tempLocation)) return false;
		if(Globals.getInstance().preferences.copyImportAssets) {
			FileUtils.copyFile(fileSelector.getFile(), tempLocation);
		} else {
			FileUtils.moveFile(fileSelector.getFile(), tempLocation);
		}
		fileSelector.setFile(tempLocation);
		return true;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if(Globals.getModPack() == null) return;

		boolean changed = true;
		if(e.getSource().equals(nameField)) {
			Globals.getModPack().name = nameField.getText();
			Globals.getInstance().listsPacks.nameChanged();
		} else if(e.getSource().equals(authorField)) {
			Globals.getModPack().author = authorField.getText();
		} else if(e.getSource().equals(keyField)) {
			if(keyField.getText() == null || keyField.getText().equals("")) return;
			while(Globals.getInstance().listsPacks.codeExists(keyField.getText(), shortNameField.getText())) {
				String result = (String) JOptionPane.showInputDialog(
						Globals.getInstance().mainFrame, "Key exists, pick new key",
						"New key", JOptionPane.PLAIN_MESSAGE, null, null, keyField.getText());
				if(result == null || result.equals("")) {
					keyField.setText(Globals.getModPack().key);
					return;
				}
				else keyField.setText(result);
			}

			Globals.getModPack().key = keyField.getText();
		} else if(e.getSource().equals(descriptionField)) {
			Globals.getModPack().description = descriptionField.getText();
		} else if(e.getSource().equals(minecraftVersionSelector)) {
			Globals.getModPack().minecraftVersion = minecraftVersionSelector.getVersion();
		} else if(e.getSource().equals(versionEditor)) {
			Globals.getModPack().versions = versionEditor.getVersions();
			Globals.getModPack().recommendedVersion = versionEditor.getRecommendedVersion();
		} else if(e.getSource().equals(forgeEditor)) {
			Globals.getModPack().forgeType = forgeEditor.getForgeType();
			Globals.getModPack().ForgeVersion = forgeEditor.getForgeVersion();
		} else if(e.getSource().equals(iconSelector)) {
			if(fileChanged(iconSelector)) {
				Globals.getModPack().icon = iconSelector.getFile();
			}
		} else if(e.getSource().equals(splashSelector)) {
			if(fileChanged(splashSelector)) {
				Globals.getModPack().splash = splashSelector.getFile();
			}
		} else if(e.getSource().equals(serverSelector)) {
			if(fileChanged(serverSelector)) {
				Globals.getModPack().server = serverSelector.getFile();
			}
		} else {
			changed = false;
		}
		if(changed) {
			Globals.modPackChanged(this, true);
		}

	}
}
