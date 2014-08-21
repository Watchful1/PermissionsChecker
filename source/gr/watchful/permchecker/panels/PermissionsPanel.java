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

/**
 * Eventually I will move everything relevant to the permissions panel from the mainClass constructor to here
 * @author watchful
 */
@SuppressWarnings("serial")
public class PermissionsPanel extends JPanel implements NamedScrollingListPanelListener, RebuildsMods, UsesPack {
	private SortedListModel<Mod> goodMods;
	private SortedListModel<Mod> badMods;
	private SortedListModel<ModFile> unknownMods;
	private ArrayList<ModFile> knownModFiles;
	private NamedScrollingListPanel<Mod> good;
	private NamedScrollingListPanel<Mod> bad;
	private NamedScrollingListPanel<ModFile> unknown;
	private JPanel cards;
	private ModEditor modEditor;
	private ModFileEditor modFileEditor;
	private ModFinder modFinder;

	public PermissionsPanel() {
		goodMods = new SortedListModel<>();
		badMods = new SortedListModel<>();
		unknownMods = new SortedListModel<>();
		modFinder = new ModFinder();
		Globals.getInstance().rebuildsMods = this;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		mainPanel.setAlignmentX(0f);
		this.add(mainPanel);

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
		updateEditor(event.getParentName(),event.getSelected());
	}

	private void updateEditor(String list, int selected) {
		if(list.equals("Good")) {
			bad.clearSelection();
			unknown.clearSelection();

			CardLayout cardLayout = (CardLayout)(cards.getLayout());
			cardLayout.show(cards, "MODEDITOR");

			modEditor.setMod(Globals.getInstance().nameRegistry.getInfo(good.getSelected()), good.getSelected().shortName);
		}
		if(list.equals("Bad")) {
			good.clearSelection();
			unknown.clearSelection();

			CardLayout cardLayout = (CardLayout)(cards.getLayout());
			cardLayout.show(cards, "MODEDITOR");

			modEditor.setMod(Globals.getInstance().nameRegistry.getInfo(bad.getSelected()), bad.getSelected().shortName);
		}
		if(list.equals("Unknown")) {
			good.clearSelection();
			bad.clearSelection();

			CardLayout cardLayout = (CardLayout)(cards.getLayout());
			cardLayout.show(cards, "MODFILEEDITOR");

			modFileEditor.setModFile(unknown.getSelected());
		}
	}

	public void updatePack(ModPack modPack) {
		//TODO
	}

	public void parsePack() {
		System.out.println("Parsing");
		knownModFiles = modFinder.discoverModFiles(new File(
				Globals.getInstance().preferences.workingFolder+File.separator+"minecraft"+File.separator+"mods"));
		System.out.println("Found "+knownModFiles.size()+" files");
		recheckMods();
	}

	public void recheckMods() {
		goodMods.clear();
		badMods.clear();
		unknownMods.clear();

		ModStorage modStorage = Globals.getInstance().nameRegistry.compileMods(knownModFiles, Globals.getModPack());
		unknownMods.addAll(modStorage.modFiles);
		for(ModFile modFile : modStorage.modFiles) {
			for(String ID : modFile.IDs.getArrayList()) {
				Globals.getInstance().preferences.unknownMods.put(ID, modFile.fileName());
			}
		}
		Globals.getInstance().savePreferences();
		ModInfo temp;
		for(Mod mod : modStorage.mods.values()) {
			temp = Globals.getInstance().nameRegistry.getInfo(mod, Globals.getModPack());
			if(temp == null) continue;
			if(temp.hasPublic()) {
				mod.permStatus = Mod.PUBLIC;
				goodMods.addElement(mod);
			} else if(temp.hasPrivate()) {
				Globals.getModPack().isPublic = false;
				mod.permStatus = Mod.PRIVATE;
				goodMods.addElement(mod);
			} else {
				badMods.addElement(mod);
			}
		}

		goodMods.sort(new SimpleObjectComparator());
		badMods.sort(new SimpleObjectComparator());
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
		for(int i=0; i<goodMods.getSize(); i++) {
			ModInfo modInfo = Globals.getInstance().nameRegistry.getInfo(goodMods.get(i), Globals.getModPack());

			StringBuilder bldr2 = new StringBuilder();
			bldr2.append("<a href=\"");
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

			switch(modInfo.getPolicy(Globals.getModPack().isPublic)) {
				case ModInfo.NOTIFY:
					bldr.append(" The author has been notified, ");
					bldr.append(modInfo.customLink);
					bldr.append(".");
					break;
				case ModInfo.REQUEST:
				case ModInfo.CLOSED:
					bldr.append(" Permission has been obtained from the author, ");
					bldr.append(modInfo.customLink);
					bldr.append(".");
					break;
			}

			bldr.append("\n");
		}

		FileUtils.writeFile(bldr.toString(), infoFile);
	}
}
