package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModPack;
import gr.watchful.permchecker.datastructures.UsesPack;
import gr.watchful.permchecker.utils.FileUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;

@SuppressWarnings("serial")
public class ModPacksPanel extends JPanel implements UsesPack, ChangeListener {
	private LabelField nameField;
	private LabelField authorField;
	private LabelField shortNameField;
	private LabelField keyField;
	private HTMLField descriptionField;
	private DropdownSelector minecraftVersionSelector;
	private VersionEditor versionEditor;
	private ForgeEditor forgeEditor;
    private PublicField publicField;
    private LabelField curseField;
    private CheckboxField java8Required;
    private DropdownSelector listedPackTypeSelector;

	private ModPack oldPack;

	private NamedScrollingListPanel<ModPack> modPacksList;
	
	public ModPacksPanel(NamedScrollingListPanel<ModPack> modPacksListIn) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		this.setAlignmentY(JPanel.TOP_ALIGNMENT);

		modPacksList = modPacksListIn;

		nameField = new LabelField("Name", this);
		this.add(nameField);
		authorField = new LabelField("Author", this);
		this.add(authorField);
		shortNameField = new LabelField("ShortName");
		shortNameField.lock("This is autocomputed, changing it breaks lots of stuff");
		this.add(shortNameField);
		keyField = new LabelField("Pack Key", this);
		this.add(keyField);
		descriptionField = new HTMLField("Description", this);
		this.add(descriptionField);
		minecraftVersionSelector = new DropdownSelector("Minecraft", this);
		minecraftVersionSelector.setSelections(Globals.getInstance().preferences.minecraftVersions);
		minecraftVersionSelector.setSelection(Globals.getInstance().preferences.defaultMinecraftVersion);//TODO should this be here?
		this.add(minecraftVersionSelector);
		versionEditor = new VersionEditor("Version", this);
		this.add(versionEditor);

        JPanel horizHolderForgeCurse = new JPanel();
        horizHolderForgeCurse.setAlignmentX(0);
        horizHolderForgeCurse.setLayout(new BoxLayout(horizHolderForgeCurse, BoxLayout.X_AXIS));
		forgeEditor = new ForgeEditor("Forge", this);
        horizHolderForgeCurse.add(forgeEditor);
        horizHolderForgeCurse.add(Box.createRigidArea(new Dimension(120, 1)));
        curseField = new LabelField("Curse Project", this);
        horizHolderForgeCurse.add(curseField);
        this.add(horizHolderForgeCurse);

        JPanel horizHolderPublicJava = new JPanel();
        horizHolderPublicJava.setAlignmentX(0);
        horizHolderPublicJava.setLayout(new BoxLayout(horizHolderPublicJava, BoxLayout.X_AXIS));
        horizHolderPublicJava.add(Box.createRigidArea(new Dimension(90, 1)));
        publicField = new PublicField(this);
        horizHolderPublicJava.add(publicField);
        horizHolderPublicJava.add(Box.createRigidArea(new Dimension(60, 1)));
        java8Required = new CheckboxField("Java 8 required", this);
        horizHolderPublicJava.add(java8Required);
        horizHolderPublicJava.add(Box.createRigidArea(new Dimension(60, 1)));
        listedPackTypeSelector = new DropdownSelector("Listed Type", this);
        listedPackTypeSelector.setSelections(Globals.getInstance().preferences.listedPackTypes);
        horizHolderPublicJava.add(listedPackTypeSelector);
        this.add(horizHolderPublicJava);
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
		pack.minecraftVersion = minecraftVersionSelector.getSelection();
		pack.metaVersions = versionEditor.getVersions();
		pack.recommendedVersion = versionEditor.getRecommendedVersion();
		pack.forgeType = forgeEditor.getForgeType();
		pack.ForgeVersion = forgeEditor.getForgeVersion();
		/*pack.icon = iconSelector.getFile();
		pack.splash = splashSelector.getFile();
		pack.server = serverSelector.getFile();*/

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
		minecraftVersionSelector.setSelection(pack.minecraftVersion);
		versionEditor.setVersions(pack.metaVersions);
		versionEditor.setRecommendedVersion(pack.recommendedVersion);
		forgeEditor.setForgeType(pack.forgeType);
		forgeEditor.setForgeVersion(pack.ForgeVersion);
        publicField.setPublic(pack.isPublic);
        curseField.setText(pack.curseID);
        java8Required.setChecked(pack.java8required);
        listedPackTypeSelector.setSelection(pack.listedPackType);
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
            if(checkValidKey(keyField.getText()) == null) {
                keyField.setText(Globals.getModPack().key);
                return;
            }

			Globals.getModPack().key = keyField.getText();
		} else if(e.getSource().equals(descriptionField)) {
			Globals.getModPack().description = descriptionField.getText();
		} else if(e.getSource().equals(minecraftVersionSelector)) {
			Globals.getModPack().minecraftVersion = minecraftVersionSelector.getSelection();
		} else if(e.getSource().equals(versionEditor)) {
			Globals.getModPack().metaVersions = versionEditor.getVersions();
			Globals.getModPack().recommendedVersion = versionEditor.getRecommendedVersion();
		} else if(e.getSource().equals(forgeEditor)) {
			Globals.getModPack().forgeType = forgeEditor.getForgeType();
			Globals.getModPack().ForgeVersion = forgeEditor.getForgeVersion();
        } else if(e.getSource().equals(curseField)) {
            if(curseField.getText() != null && !curseField.getText().equals("")) {
                String result = checkValidProject(curseField.getText());
                if(result == null) {
                    curseField.setText(Globals.getModPack().curseID);
                    return;
                }
                curseField.setText(result);
            }
            Globals.getModPack().curseID = curseField.getText();
        } else if(e.getSource().equals(publicField)) {
            Globals.getModPack().isPublic = publicField.isPublic();
        } else if(e.getSource().equals(java8Required)) {
            Globals.getModPack().java8required = java8Required.isChecked();
        } else if(e.getSource().equals(listedPackTypeSelector)) {
            Globals.getModPack().listedPackType = listedPackTypeSelector.getSelection();
		} else {
			changed = false;
		}
		if(changed) {
			Globals.modPackChanged(this, true);
		}

	}

    private String checkValidKey(String key) {
        String message = "";
        boolean valid = false;
        if(key == null || key.equals("") || !ModPack.isValidKey(key)) {
            message = "Key is not valid, pick a new key";
        } else if(Globals.getInstance().listsPacks.codeExists(key, shortNameField.getText())) {
            message = "Key exists, pick a new key\nIf you want to overwrite the key, press ok without changing the key";
            valid = true;
        }
        if(!message.equals("")) {
            String result = (String) JOptionPane.showInputDialog(
                    Globals.getInstance().mainFrame, message,
                    "New key", JOptionPane.PLAIN_MESSAGE, null, null, key);
            if(result == null) return null;
            if(valid && result.equals(key)) {
                int n = JOptionPane.showConfirmDialog(
                        Globals.getInstance().mainFrame,
                        "Are you sure you want to overwrite the key \""+key+"\"?",
                        "Confirm overwrite",
                        JOptionPane.YES_NO_OPTION);
                if(n == JOptionPane.YES_OPTION) {
                    return key;
                } else {
                    return null;
                }
            }
            return checkValidKey(result);
        }
        keyField.setText(key);
        return key;
    }

    private String checkValidProject(String projectID) {
        String message = "";
        if(projectID == null || projectID.equals("")) {
            message = "Curse project ID is not valid, pick a new project ID";
        } else if(Globals.getInstance().listsPacks.curseIDUsed(projectID, shortNameField.getText())) {
            message = "Curse project ID is taken by another pack, pick a new project ID";
        }
        if(!message.equals("")) {
            String result = (String) JOptionPane.showInputDialog(
                    Globals.getInstance().mainFrame, message,
                    "New curse ID", JOptionPane.PLAIN_MESSAGE, null, null, projectID);
            if(result == null) return null;
            return checkValidProject(result);
        } else {
            JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
                    "Project ID changed. Click Ok to open project page and verify that it exists and the author is correct");
            FileUtils.openWebpage(Globals.curseProjectRoot+projectID);
        }
        return projectID;
    }
}
