package gr.watchful.permchecker.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModPack;

import javax.swing.*;

@SuppressWarnings("serial")
public class ModPacksPanel extends JPanel {
	private DefaultListModel<ModPack> modPacksModel;
	private NamedScrollingListPanel<ModPack> modPacksPanel;
    private JPanel mainPanel;

    private JPanel buttonPanel;
    private JButton saveButton;
    private JButton addPackButton;
    private JButton removePackButton;

    private JPanel editorPanel;
    private LabelField nameField;
    private LabelField authorField;
    private LabelField shortNameField;
    private LabelField keyField;
    private HTMLField descriptionField;
    private RecommendedVersionEditor recommendedVersionEditor;
    private VersionEditor versionEditor;
    private FileSelecter iconSelector;
    private FileSelecter splashSelector;
    private FileSelecter serverSelector;
	
	public ModPacksPanel() {
		this.setLayout(new BorderLayout());

		modPacksModel = new DefaultListModel<>();
		modPacksPanel = new NamedScrollingListPanel<>("ModPacks", 200, modPacksModel);
		this.add(modPacksPanel, BorderLayout.LINE_START);

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
                saveCurrentPack();
            }
        });
		buttonPanel.add(saveButton);

        addPackButton = new JButton("Add");
        addPackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                addPack();
            }
        });
        buttonPanel.add(addPackButton);

        removePackButton = new JButton("Remove");
        removePackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                removeCurrentPack();
            }
        });
        buttonPanel.add(removePackButton);

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
        recommendedVersionEditor = new RecommendedVersionEditor("Recommended");
        editorPanel.add(recommendedVersionEditor);
        versionEditor = new VersionEditor("Version", recommendedVersionEditor);
        editorPanel.add(versionEditor);
        iconSelector = new FileSelecter("Icon", 150, "png");
        editorPanel.add(iconSelector);
        splashSelector = new FileSelecter("Splash", 150, "png");
        editorPanel.add(splashSelector);
        serverSelector = new FileSelecter("Server", -1, "zip");
        editorPanel.add(serverSelector);

        mainPanel.add(editorPanel);

        this.add(mainPanel);
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
        boolean found = false;

        if(shortNameField.getText().equals("")) {
            shortNameField.setText(ModPack.generateShortName(nameField.getText()));
        }

        if(shortNameField.getText() == null || shortNameField.getText().equals("")) {
            System.out.println("Blank ShortName, can't save");
        }
        if(keyField.getText() == null || keyField.getText().equals("")) {
            System.out.println("Blank Key, can't save");
            return;
        }
        for(int i=0; i<modPacksPanel.getModel().getSize(); i++) {
            ModPack newPack = modPacksPanel.getModel().get(i);
            if(keyField.getText().equals(newPack.key)) {
                if(found) {
                    System.out.println("Key exists. Can't save.");
                    return;
                } else found = true;
            } else if(shortNameField.getText().equals(newPack.shortName)) {
                if(found) {
                    System.out.println("ShortName exists. Can't save.");
                    return;
                } else found = true;
            }
        }

        ModPack pack = modPacksPanel.getSelected();
        pack.name = nameField.getText();
        pack.author = authorField.getText();
        pack.shortName = shortNameField.getText(); // TODO detect changes here
        pack.key = keyField.getText(); // TODO detect changes here
        pack.description = descriptionField.getText();
        pack.versions = versionEditor.getVersions();
        pack.recommendedVersion = recommendedVersionEditor.getRecommendedVersion();
        pack.icon = iconSelector.getFile();
        pack.splash = splashSelector.getFile();
        pack.server = serverSelector.getFile();

        modPacksPanel.sortKeepSelected();



		if(!modPacksPanel.getSelected().saveThisObject()) System.out.println("Couldn't save pack");
	}

    public void addPack() {
        ModPack newPack = new ModPack();
        modPacksModel.addElement(newPack);
        modPacksPanel.setSelected(0);
        modPacksPanel.sortKeepSelected();
        setPack(newPack);
    }

    public void removeCurrentPack() {

    }

    public void setPack(ModPack pack) {
        nameField.setText(pack.name);
        authorField.setText(pack.author);
        shortNameField.setText(pack.shortName);
        keyField.setText(pack.key);
        descriptionField.setText(pack.description);
        versionEditor.setVersions(pack.versions);
        recommendedVersionEditor.setRecommendedVersion(pack.recommendedVersion);
        iconSelector.setFile(pack.icon);
        splashSelector.setFile(pack.splash);
        serverSelector.setFile(pack.server);
    }
}
