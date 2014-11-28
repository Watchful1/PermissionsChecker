package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.*;
import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;
import gr.watchful.permchecker.modhandling.ModFinder;
import gr.watchful.permchecker.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

@SuppressWarnings("serial")
public class PermissionsPanel extends JPanel implements NamedScrollingListPanelListener, RebuildsMods, UsesPack {
	private SortedListModel<Mod> goodMods;
	private SortedListModel<Mod> badMods;
	private SortedListModel<ModFile> unknownMods;
	private ArrayList<ModFile> knownModFiles;
	private DisabledPanel disabledPanel;
	private JLayeredPane layeredPanel;
	private NamedScrollingListPanel<Mod> good;
	private NamedScrollingListPanel<Mod> bad;
	private NamedScrollingListPanel<ModFile> unknown;
	private JPanel cards;
	private ModEditor modEditor;
	private ModFileEditor modFileEditor;
	private ModFinder modFinder;
	private JButton parseButton;

	public PermissionsPanel() {
		goodMods = new SortedListModel<>();
		badMods = new SortedListModel<>();
		unknownMods = new SortedListModel<>();
		modFinder = new ModFinder();
		Globals.getInstance().rebuildsMods = this;

		this.setLayout(new BorderLayout());

		layeredPanel = new JLayeredPane();
		layeredPanel.setLayout(new OverlayLayout(layeredPanel));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		mainPanel.setAlignmentX(0f);

		disabledPanel = new DisabledPanel(mainPanel);
		disabledPanel.setOpaque(true);
		layeredPanel.add(disabledPanel, JLayeredPane.DEFAULT_LAYER);

		parseButton = new JButton("Parse Pack");
		parseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parsePack();
			}
		});
		parseButton.setOpaque(true);
		parseButton.setAlignmentX(0.5f);
		parseButton.setAlignmentY(0.5f);
		layeredPanel.add(parseButton, JLayeredPane.DEFAULT_LAYER);

		this.add(layeredPanel, BorderLayout.CENTER);

		good = new NamedScrollingListPanel<>("Good", 100, goodMods);
		good.addListener(this);
		mainPanel.add(good);

		bad = new NamedScrollingListPanel<>("Bad", 100, badMods);
		bad.addListener(this);
		mainPanel.add(bad);

		unknown = new NamedScrollingListPanel<>("Unknown", 100, unknownMods);
		mainPanel.add(unknown);
		unknown.addListener(this);

		cards = new JPanel(new CardLayout());

		modEditor = new ModEditor(new Dimension(500,900));
		cards.add(modEditor,"MODEDITOR");
		modFileEditor = new ModFileEditor(new Dimension(300,300), new ModFile(new File("/")));
		cards.add(modFileEditor,"MODFILEEDITOR");

		CardLayout cardLayout = (CardLayout)(cards.getLayout());
		cardLayout.show(cards, "MODEDITOR");

		mainPanel.add(cards);
	}

	public void selectionChanged(NamedSelectionEvent event) {
		updateEditor(event.getParentName(), event.getSelected());
	}

	private void updateEditor(String list, int selected) {
		if(list.equals("Good")) {
			//System.out.println(good.getSelected().modFile.fileName());
			/*for(String id : good.getSelected().modFile.IDs.getArrayList()) {
				System.out.println(id);
			}*/
			bad.clearSelection();
			unknown.clearSelection();

			CardLayout cardLayout = (CardLayout)(cards.getLayout());
			cardLayout.show(cards, "MODEDITOR");

			modEditor.setMod(Globals.getInstance().nameRegistry.getInfo(good.getSelected(), Globals.getModPack()), good.getSelected().shortName);
		}
		if(list.equals("Bad")) {
			//System.out.println(bad.getSelected().modFile.fileName());
			/*for(String id : bad.getSelected().modFile.IDs.getArrayList()) {
				System.out.println(id);
			}*/
			good.clearSelection();
			unknown.clearSelection();

			CardLayout cardLayout = (CardLayout)(cards.getLayout());
			cardLayout.show(cards, "MODEDITOR");

			modEditor.setMod(Globals.getInstance().nameRegistry.getInfo(bad.getSelected(), Globals.getModPack()), bad.getSelected().shortName);
		}
		if(list.equals("Unknown")) {
			//System.out.println(unknown.getSelected().fileName());
			good.clearSelection();
			bad.clearSelection();

			CardLayout cardLayout = (CardLayout)(cards.getLayout());
			cardLayout.show(cards, "MODFILEEDITOR");

			modFileEditor.setModFile(unknown.getSelected());
		}
	}

	private void setDisabled(boolean isDisabled) {
		if(isDisabled) {
			layeredPanel.moveToFront(parseButton);
			parseButton.setVisible(true);
		} else {
			layeredPanel.moveToFront(disabledPanel);
			parseButton.setVisible(false);
		}
		disabledPanel.setEnabled(!isDisabled);
	}

	public void invalidateContents() {
		setDisabled(true);
	}

	public void updatePack(ModPack modPack) {
		invalidateContents();
	}

	public void parsePack() {
		modEditor.setMod(null, "");
		modFileEditor.setModFile(null);
		System.out.println("Parsing "+Globals.getModPack().name);
		knownModFiles = modFinder.discoverModFiles(new File(
				Globals.getInstance().preferences.workingFolder+File.separator+"minecraft"+File.separator+"mods"));
		System.out.println("Found "+knownModFiles.size()+" files");
		recheckMods();
		setDisabled(false);
	}

	public void recheckMods() {
		goodMods.clear();
		badMods.clear();
		unknownMods.clear();

		ModStorage modStorage = Globals.getInstance().nameRegistry.compileMods(knownModFiles, Globals.getModPack());
		unknownMods.addAll(modStorage.modFiles);
		for(ModFile modFile : modStorage.modFiles) {
			if(modFile.IDs.getSize() > 0) {
				for (String ID : modFile.IDs.getArrayList()) {
					Globals.getInstance().preferences.unknownMods.put(ID, modFile.fileName());
				}
			} else if(modFile.md5 != null && !modFile.md5.equals("")) {
				Globals.getInstance().preferences.unknownMods.put(modFile.md5, modFile.fileName());
			} else {
				String md5 = FileUtils.getMD5(modFile.file);
				if(md5 != null) {
					Globals.getInstance().preferences.unknownMods.put(md5, modFile.fileName());
				}
			}
		}
		Globals.getInstance().savePreferences();
		ModInfo temp;
		for(Mod mod : modStorage.mods.values()) {
			if(mod.shortName.equals("ignore")) continue; // Ignore non-mod files
			temp = Globals.getInstance().nameRegistry.getInfo(mod, Globals.getModPack());
			if(temp == null) {
				unknownMods.addElement(mod.modFile);
			} else {
				if (temp.hasPublic()) {
					mod.permStatus = Mod.PUBLIC;
					goodMods.addElement(mod);
				} else if (temp.hasPrivate()) {
					Globals.getModPack().isPublic = false;
					mod.permStatus = Mod.PRIVATE;
					goodMods.addElement(mod);
				} else {
					badMods.addElement(mod);
				}
			}
		}

		goodMods.sort(new SimpleObjectComparator());
		badMods.sort(new SimpleObjectComparator());
	}

	public boolean promptPermissionsGood() {
		if(badMods.getSize() > 0 || unknownMods.getSize() > 0) {
			StringBuilder bldr = new StringBuilder();
			if(badMods.getSize() > 0) {
				bldr.append(badMods.getSize());
				if(badMods.getSize() == 1) {
					bldr.append(" mod has missing permission\n");
				} else {
					bldr.append(" mods have missing permissions\n");
				}
			}
			if(unknownMods.getSize() > 0) {
				bldr.append(unknownMods.getSize());
				if(unknownMods.getSize() == 1) {
					bldr.append(" file was not identified\n");
				} else {
					bldr.append(" files were not identified\n");
				}
			}

			Object[] options = {"Ignore Missing Permissions", "Cancel"};
			int n = JOptionPane.showOptionDialog(Globals.getInstance().mainFrame,
					bldr.toString(),
					"Missing Permissions",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[1]);
			if(n == 0) return true;
			else return false;
		} else return true;
	}

	public void writeFile() {
		//TODO check for no modpack
		File infoFile = new File(Globals.getInstance().preferences.getWorkingMinecraftFolder()+File.separator+"perms.txt");
		System.out.println("Printing to: "+infoFile.getAbsolutePath());

		StringBuilder bldr = new StringBuilder();
		bldr.append("Permission categories and full licenses for mods marked spreadsheet are available here: http://1drv.ms/1rsQNOJ\n");
		bldr.append("For any problems, please contact Watchful11 on the FTB forums.\n");

		bldr.append("This is a ");
		if(Globals.getModPack().isPublic) bldr.append("public");
		else bldr.append("private");
		bldr.append(" pack\n\n");

		Globals.getModPack().modList.clear();
		SortedListModel<Mod> allMods = new SortedListModel<>();
		allMods.addAll(goodMods.getArrayList());
		allMods.addAll(badMods.getArrayList());
		allMods.sort(new SimpleObjectComparator(true));

		//Hard code Forge in. TODO fix this if we ever need to add support for only loading other tweaks
		Mod forge = new Mod(null, "forge");
		forge.permStatus = Mod.PUBLIC;
		allMods.add(0, forge);

		for(int i=0; i<allMods.getSize(); i++) {
			ModInfo modInfo = Globals.getInstance().nameRegistry.getInfo(allMods.get(i), Globals.getModPack());

			StringBuilder bldr2 = new StringBuilder();
			bldr2.append("<a color=\"aqua\" href=\"");
			bldr2.append(modInfo.modLink);
			bldr2.append("\">");
			bldr2.append(modInfo.modName);
			bldr2.append("</a>");
			bldr2.append(" by ");
			bldr2.append(modInfo.modAuthor);
			Globals.getModPack().modList.add(bldr2.toString());

			bldr.append("(");
			bldr.append(ModInfo.getStringPolicy(modInfo.getPolicy(Globals.getModPack().isPublic)));
			bldr.append(":");
			if(modInfo.officialSpreadsheet) bldr.append("Spreadsheet");
			else bldr.append("Custom");
			bldr.append(") ");
			bldr.append(modInfo.modName); bldr.append(" by ");
			bldr.append(modInfo.modAuthor); bldr.append(" can be found at ");
			bldr.append(modInfo.modLink); bldr.append(".");

			if(!modInfo.officialSpreadsheet) {
				bldr.append(" The license is, ");
				bldr.append(modInfo.getPermImage(Globals.getModPack().isPublic));
				if(modInfo.getPermLink(Globals.getModPack().isPublic).equals("PM")) {
					bldr.append(", which is a private message.");
				} else if(modInfo.licenseLink.equals(modInfo.modLink) || modInfo.licenseLink.equals("")) {
					bldr.append(".");
				} else {
					bldr.append(", and can be found ");
					bldr.append(modInfo.licenseLink);
					bldr.append(".");
				}
			}

			if(modInfo.customLink.equals("") && modInfo.getPolicy(Globals.getModPack().isPublic) != ModInfo.OPEN) {
				bldr.append(" No permission has been listed.");
			} else {
				switch (modInfo.getPolicy(Globals.getModPack().isPublic)) {
					case ModInfo.NOTIFY:
						bldr.append(" The author has been notified, ");
						bldr.append(modInfo.customLink);
						bldr.append(".");
						break;
					case ModInfo.REQUEST:
					case ModInfo.CLOSED:
					case ModInfo.UNKNOWN:
						bldr.append(" Permission has been obtained from the author, ");
						bldr.append(modInfo.customLink);
						bldr.append(".");
						break;
				}
			}

			bldr.append("\n");
		}
		if(unknownMods.getSize() > 0) {
			bldr.append("\n");
			for(int i=0; i < unknownMods.getSize(); i++) {
				bldr.append("The file ");
				bldr.append(unknownMods.get(i).fileName());
				bldr.append(" could not be identified");
			}
		}

		FileUtils.writeFile(bldr.toString(), infoFile);
	}
}
