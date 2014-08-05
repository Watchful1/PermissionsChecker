package gr.watchful.permchecker.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gr.watchful.permchecker.datastructures.ForgeType;
import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModPack;
import gr.watchful.permchecker.datastructures.UsesPack;

import javax.swing.*;

@SuppressWarnings("serial")
public class ModPacksPanel extends JPanel implements UsesPack {
    private JPanel mainPanel;

    private JPanel buttonPanel;
    private JButton saveButton;

    private JPanel editorPanel;
    private LabelField nameField;
    private LabelField authorField;
    private LabelField shortNameField;
    private LabelField keyField;
    private HTMLField descriptionField;
	private MinecraftVersionSelecter minecraftVersionSelecter;
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
		buttonPanel = new JPanel();
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

        mainPanel.add(buttonPanel);

        //fields in the middle to edit pack details
        editorPanel = new JPanel();
        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));
        editorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        nameField = new LabelField("Name");
        editorPanel.add(nameField);
        authorField = new LabelField("Author");
        editorPanel.add(authorField);
        shortNameField = new LabelField("ShortName");
        shortNameField.lock("This is autocomputed, changing it breaks lots of stuff");
        editorPanel.add(shortNameField);
        keyField = new LabelField("Pack Key");
        editorPanel.add(keyField);
        descriptionField = new HTMLField("Description");
        editorPanel.add(descriptionField);
		minecraftVersionSelecter = new MinecraftVersionSelecter("Minecraft");
		minecraftVersionSelecter.setVersions(Globals.getInstance().preferences.minecraftVersions);
		minecraftVersionSelecter.setVersion(Globals.getInstance().preferences.defaultMinecraftVersion);
		editorPanel.add(minecraftVersionSelecter);
        versionEditor = new VersionEditor("Version");
        editorPanel.add(versionEditor);
		forgeEditor = new ForgeEditor("Forge");
		editorPanel.add(forgeEditor);
        iconSelector = new FileSelector("Icon", 150, "png");
        editorPanel.add(iconSelector);
        splashSelector = new FileSelector("Splash", 150, "png");
        editorPanel.add(splashSelector);
        serverSelector = new FileSelector("Server", -1, "zip");
        editorPanel.add(serverSelector);

        mainPanel.add(editorPanel);

        this.add(mainPanel);
	}
	
	public void savePack(ModPack packIn) {
        if(packIn == null) return;

		ModPack pack = packIn;

		boolean changed = false;
		if(!pack.name.equals(nameField.getText())) changed = true;
		if(!pack.author.equals(authorField.getText())) changed = true;
		if(!pack.shortName.equals(shortNameField.getText())) changed = true;
		if(!pack.key.equals(keyField.getText())) changed = true;
		if(!pack.description.equals(descriptionField.getText())) changed = true;
		if(!pack.minecraftVersion.equals(minecraftVersionSelecter.getVersion())) changed = true;
		if(!pack.versions.equals(versionEditor.getVersions())) changed = true;
		if(!pack.recommendedVersion.equals(versionEditor.getRecommendedVersion())) changed = true;
		if(!pack.forgeType.equals(forgeEditor.getForgeType())) changed = true;
		if(!(pack.ForgeVersion == forgeEditor.getForgeVersion())) changed = true;
		//if(!pack.icon.equals(iconSelector.getFile())) changed = true;
		//if(!pack.splash.equals(splashSelector.getFile())) changed = true;
		//if(!pack.server.equals(serverSelector.getFile())) changed = true;
		// TODO need to work out how to do icons, splashes and server files
		if(!changed) return;

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
		pack.minecraftVersion = minecraftVersionSelecter.getVersion();
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
		minecraftVersionSelecter.setVersion(pack.minecraftVersion);
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
		if(oldPack != null) savePack(oldPack);
		oldPack = modPack;
		setPack(modPack);
	}
}
