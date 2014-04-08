package gr.watchful.permchecker;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.Mod;
import gr.watchful.permchecker.datastructures.ModFile;
import gr.watchful.permchecker.datastructures.ModInfo;
import gr.watchful.permchecker.datastructures.Preferences;
import gr.watchful.permchecker.datastructures.RebuildsMods;
import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;
import gr.watchful.permchecker.modhandling.ModFinder;
import gr.watchful.permchecker.modhandling.ModNameRegistry;
import gr.watchful.permchecker.panels.ModEditor;
import gr.watchful.permchecker.panels.ModFileEditor;
import gr.watchful.permchecker.panels.ModPacksPanel;
import gr.watchful.permchecker.panels.NamedScrollingListPanel;
import gr.watchful.permchecker.utils.ExcelUtils;
import gr.watchful.permchecker.utils.FileUtils;
import gr.watchful.permchecker.utils.JSONUtils;
import gr.watchful.permchecker.utils.OsTypes;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;

@SuppressWarnings("serial")
public class mainClass extends JFrame implements
		NamedScrollingListPanelListener, RebuildsMods {
	private DefaultListModel<Mod> goodMods;
	private DefaultListModel<Mod> badMods;
	private DefaultListModel<ModFile> unknownMods;
	private DefaultListModel<ModFile> knownMods;
	private NamedScrollingListPanel<Mod> good;
	private NamedScrollingListPanel<Mod> bad;
	private NamedScrollingListPanel<ModFile> unknown;
	private JToggleButton packTypeToggle;
	private JPanel cards;
	private ModEditor modEditor;
	private ModFileEditor modFileEditor;
	private static ModNameRegistry nameRegistry;
	private static Globals globals;
	private JTabbedPane tabbedPane;
	private ModPacksPanel modPacksPanel;
	/*
	 * bearbear12345's section
	 */
	private File appstore; // Location for the application data
	private File permFile; // Permission spreadsheet
	private Properties session;
	private File sessionstore;

	private String updatedtime = null;
	private String newupdatedtime = null;
	private String lastselectedpath = System.getProperty("user.home");

	/*
     *
     */

	public mainClass() {
		goodMods = new DefaultListModel<Mod>();
		badMods = new DefaultListModel<Mod>();
		unknownMods = new DefaultListModel<ModFile>();
		knownMods = new DefaultListModel<ModFile>();

		globals = Globals.getInstance();
		globals.main = this;

		nameRegistry = globals.nameRegistry;

		/**
		 * Check which OS the system is running, and make the appropriate
		 * directories if necessary
		 * 
		 * @author bearbear12345
		 */
		OsTypes.OSType ostype = OsTypes.getOperatingSystemType();
		System.out.println("Operating System: " + ostype.toString());
		System.out.println("Searching for application storage directory...");
		switch (ostype) {
		case Windows:
			appstore = new File(System.getenv("APPDATA")
					+ "/PermissionsChecker");
			break;
		case MacOS:
			appstore = new File(System.getProperty("user.home")
					+ "/Library/Application Support/PermissionsChecker");
			break;
		case Linux:
			appstore = new File(System.getProperty("user.home")
					+ "/.PermissionsChecker");
			break;
		case Other:
			// I'm not sure what will fall under this category, but I'll play it
			// safe and do the same for linux
			appstore = new File(System.getProperty("user.home")
					+ "/.PermissionsChecker");
			break;
		}
		if (!appstore.exists()) {
			System.out.println("Directory not found! Creating directory: "
					+ appstore.getAbsolutePath());
			if (appstore.mkdirs()) {
				System.out.println(appstore.getPath() + " created!");
			} else {
				System.out.println("Could not create directory: "
						+ appstore.getAbsolutePath());
			}
		}

		permFile = new File(appstore.getPath() + "/permissionslist.data");
		if (!permFile.exists()) {
			try {
				permFile.createNewFile();
			} catch (IOException e) {
				System.out
						.println("An error occured while creating during setup. Please try again later. If issues persist, contact the author");
			}
		}

		sessionstore = new File(appstore.getPath() + "/session.data");
		if (!sessionstore.exists()) {
			try {
				sessionstore.createNewFile();
			} catch (IOException e) {
				System.out
						.println("An error occured while creating during setup. Please try again later. If issues persist, contact the author");
			}
		}
		session = new Properties();
		InputStream in;
		try {
			in = new FileInputStream(sessionstore);
			session.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		updatedtime = session.getProperty("updatedtime",
				"0000-00-00T00:00:00+0000");

		loadPreferences();

		this.setTitle("Permissions Checker"); // Set the window title
		this.setPreferredSize(new Dimension(800, 600)); // and the initial size

		updateListings();
		File currentDir = new File(System.getProperty("user.dir"));
		if (isMinecraftDir(currentDir)) {
			discoverMods(currentDir);
		} else if (isMinecraftDir(currentDir.getParentFile())) {
			discoverMods(currentDir.getParentFile());
		} else {
			// TODO implement a selection of modpacks, maybe save modpack
			// locations for later use
			// also allow selecting of multiMC instances folder

			// debug
			// discoverMods(new
			// File("C:\\Users\\Gregory\\Desktop\\MultiMC\\instances\\Hammercraft 4.3.0 Custom\\minecraft"));
		}

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(0f);
		buttonPanel.add(new JLabel("Public"));

		packTypeToggle = new JToggleButton();
		packTypeToggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateToggleIcons();
				updateSettings();
			}
		});

		Dimension temp = new Dimension(92, 28);
		packTypeToggle.setMaximumSize(temp);
		packTypeToggle.setMinimumSize(temp);
		packTypeToggle.setPreferredSize(temp);

		try { // try as hard as possible to load the images and set them on the
				// toggle button
			URL leftUrl = getClass().getResource("resources/toggleLeft.png");
			URL rightUrl = getClass().getResource("resources/toggleRight.png");
			if (leftUrl == null || rightUrl == null) {
				ImageIcon leftIcon = new ImageIcon(
						"bin/resources/toggleLeft.png");
				ImageIcon rightIcon = new ImageIcon(
						"bin/resources/toggleRight.png");
				if (leftIcon.getIconWidth() == -1
						|| rightIcon.getIconWidth() == -1) {
					packTypeToggle.setText("Switch");
				} else {
					packTypeToggle.setIcon(leftIcon);
					packTypeToggle.setSelectedIcon(rightIcon);
				}
			} else {
				Image leftImg = ImageIO.read(leftUrl);
				Image rightImg = ImageIO.read(rightUrl);
				if (leftImg == null || rightImg == null) {
					packTypeToggle.setText("Switch");
				} else {
					packTypeToggle.setIcon(new ImageIcon(leftImg));
					packTypeToggle.setSelectedIcon(new ImageIcon(rightImg));
				}
			}
		} catch (IOException ex) {
			packTypeToggle.setText("Switch");
		}

		if (packTypeToggle.getIcon() != null) {
			Dimension temp2 = new Dimension(packTypeToggle.getIcon()
					.getIconWidth() + 2, packTypeToggle.getIcon()
					.getIconHeight() + 2);
			packTypeToggle.setMaximumSize(temp2);
			packTypeToggle.setMinimumSize(temp2);
			packTypeToggle.setPreferredSize(temp2);

			packTypeToggle.setBorderPainted(false);
			packTypeToggle.setContentAreaFilled(false);
			packTypeToggle.setOpaque(false);
			packTypeToggle.setFocusPainted(false);
		}

		buttonPanel.add(packTypeToggle);

		buttonPanel.add(new JLabel("Private"));

		topPanel.add(buttonPanel);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		mainPanel.setAlignmentX(0f);
		topPanel.add(mainPanel);

		modPacksPanel = new ModPacksPanel();

		tabbedPane = new JTabbedPane();

		tabbedPane.add("Permissions", topPanel);
		tabbedPane.add("Modpacks", modPacksPanel);
		// this.add(topPanel);

		this.add(tabbedPane);

		good = new NamedScrollingListPanel<Mod>("Good", 100, goodMods);
		good.addListener(this);
		mainPanel.add(good);
		bad = new NamedScrollingListPanel<Mod>("Bad", 100, badMods);
		bad.addListener(this);
		mainPanel.add(bad);
		unknown = new NamedScrollingListPanel<ModFile>("Unknown", 100,
				unknownMods);
		mainPanel.add(unknown);
		unknown.addListener(this);

		JPanel newWindow = new JPanel();
		JPanel modEditWindow = new JPanel();

		cards = new JPanel(new CardLayout());
		cards.setMinimumSize(new Dimension(500, 300));
		cards.setMaximumSize(new Dimension(500, 900));
		cards.add(newWindow);
		cards.add(modEditWindow);

		modEditor = new ModEditor(new Dimension(500, 900));
		cards.add(modEditor, "MODEDITOR");
		modFileEditor = new ModFileEditor(new Dimension(300, 300), new ModFile(
				new File("/")));
		cards.add(modFileEditor, "MODFILEEDITOR");

		CardLayout cardLayout = (CardLayout) (cards.getLayout());
		cardLayout.show(cards, "MODEDITOR");

		mainPanel.add(cards);

		JMenuBar menuBar = new JMenuBar(); // create the menu
		JMenu menu = new JMenu("Menu"); // with the submenus
		menuBar.add(menu);

		JMenuItem updatePerms = new JMenuItem("Update Permissions");

		// listen to all the menu items and then add them to the menus
		updatePerms.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateListings();
			}
		});
		menu.add(updatePerms);
		JMenuItem chooseModpack = new JMenuItem("Choose Modpack");

		// listen to all the menu items and then add them to the menus
		chooseModpack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser(new File(
						"C:\\Users\\Gregory\\Desktop\\Private pack staging"));
				// JFileChooser fileChooser = new
				// JFileChooser(lastselectedpath); TODO Remove above line when
				// finished
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File result = fileChooser.getSelectedFile();
					lastselectedpath = result.getAbsolutePath();
					discoverMods(result);
				}
			}
		});
		menu.add(chooseModpack);

		JMenuItem writeFile = new JMenuItem("Write file");

		// listen to all the menu items and then add them to the menus
		writeFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				writeFile();
			}
		});
		menu.add(writeFile);

		JMenuItem setSaveFolder = new JMenuItem("Set Save Folder");

		// listen to all the menu items and then add them to the menus
		setSaveFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// JFileChooser fileChooser = new JFileChooser(new
				// File("C:\\Users\\Gregory\\Desktop\\Private pack staging"));
				JFileChooser fileChooser = new JFileChooser(lastselectedpath);
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File result = fileChooser.getSelectedFile();
					lastselectedpath = result.getAbsolutePath();
					setSavesFolder(result);
				}
			}
		});
		menu.add(setSaveFolder);

		this.setJMenuBar(menuBar);

		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private static boolean isMinecraftDir(File file) {
		return file.getName().equals("minecraft")
				|| file.getName().equals(".minecraft");
	}

	public static void main(String[] args) {
		new mainClass();
	}

	private void discoverMods(File minecraftFolder) {
		globals.minecraftFolder = minecraftFolder;
		nameRegistry.loadCustomInfos();
		ModFinder.discoverModFiles(minecraftFolder, unknownMods);
		recheckMods();
	}

	public void selectionChanged(NamedSelectionEvent event) {
		System.out.println(event.getParentName() + " : " + event.getSelected());
		updateEditor(event.getParentName(), event.getSelected());
	}

	private void updateEditor(String list, int selected) {
		if (list.equals("Good")) {
			bad.clearSelection();
			unknown.clearSelection();

			CardLayout cardLayout = (CardLayout) (cards.getLayout());
			cardLayout.show(cards, "MODEDITOR");

			modEditor.setMod(nameRegistry.getMod(good.getSelected().shortName),
					good.getSelected().shortName);
		}
		if (list.equals("Bad")) {
			good.clearSelection();
			unknown.clearSelection();

			CardLayout cardLayout = (CardLayout) (cards.getLayout());
			cardLayout.show(cards, "MODEDITOR");

			modEditor.setMod(nameRegistry.getMod(bad.getSelected().shortName),
					bad.getSelected().shortName);
		}
		if (list.equals("Unknown")) {
			good.clearSelection();
			bad.clearSelection();

			CardLayout cardLayout = (CardLayout) (cards.getLayout());
			cardLayout.show(cards, "MODFILEEDITOR");

			modFileEditor.setModFile(unknown.getSelected());
		}
	}

	/*
	 * I split up the functions for future integration - bearbear12345
	 */
	public boolean checknewListings() {
		System.out.println("Retrieving Access Token");
		// TODO Change access token to Watchful1's file - Currently mine
		// (bearbear12345's)
		String accesstoken = JSONUtils
				.getKey("access_token",
						"https://login.live.com/oauth20_token.srf?client_id=000000004410FE50&redirect_uri=https://login.live.com/oauth20_desktop.srf&grant_type=refresh_token&refresh_token=Chf9!6iNyOsxUtX2uCMG*SKiPuyCsVNuof8bK7avToNEtCbfzYspPLEbuRXdxwjOd8CFO7BpgmyJmVDUnCqZrT6eJgtZ7mCZgkpBUiLRFm8fLHzD2tbYyn!fhJ0I7Da7i!CG05xN8ZfAc*0cOo02bsqkfq!nak!fKtRfOUal1nHjMYdkWPnTQ8a86UxYNm0nJvEvAahJoayNzJ5tvSdsD0Ar8uauOmMyixRiXkoGUvxViQlBfJYeKeifBR1uZkb5f!*JLMA5!zUxNxES9ahzYR!MATG!tnqWtZLzWCYcESEo73YtjVcNAUnf26Ad0SWunHY1C*awrgf7OgwVbiruORR9pyZ*3QcXpK5lpMDCIsAK");
		System.out.println("Access token retrieved!");
		// TODO Change access token to Watchful1's file - Currently mine
		// (bearbear12345's)
		newupdatedtime = JSONUtils
				.getKey("updated_time",
						"https://apis.live.net/v5.0/file.a4423f3123801749.A4423F3123801749!418?access_token="
								+ accesstoken);
		System.out.println("Current: " + updatedtime);
		System.out.println("Server:  " + newupdatedtime);
		if (!updatedtime.equals(newupdatedtime)) {
			return true;
		}
		return false;
	}

	public void downloadListings() {
		try {
			FileUtils
					.downloadToFile(
							new URL(
									"https://skydrive.live.com/download?resid=96628E67B4C51B81!105&authkey=!AK7mlmHB0nrxmHg&ithint=file%2c.xlsx"),
							permFile);
			updatedtime = newupdatedtime;
			session.setProperty("updatedtime", newupdatedtime);
			session.store(new FileOutputStream(sessionstore),
					"Permissions checker file data - bearbear12345");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Spreadsheet updated!");
	}

	public void parseListings() {
		try {

			// Excel parsing
			ArrayList<ArrayList<String>> infos = ExcelUtils
					.toArray(permFile, 1);
			infos.remove(0); // remove the first row, it contains column
			// titles
			ArrayList<ArrayList<String>> mappings = ExcelUtils.toArray(
					permFile, 2);
			nameRegistry.loadMappings(infos, mappings, infos.get(15).get(14),
					infos.get(15).get(15));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateListings() {
		if (checknewListings()) {
			System.out
					.println("A spreadsheet update has been found! Dowloading...");
			downloadListings();
			parseListings();
		} else {
			System.out.println("The spreadsheet is up to date!");
		}
	}

	private void updateToggleIcons() {
		if (packTypeToggle.isSelected()) {
			packTypeToggle.setPressedIcon(packTypeToggle.getIcon());
		} else {
			packTypeToggle.setPressedIcon(packTypeToggle.getSelectedIcon());
		}
	}

	public void updateSettings() {
		if (packTypeToggle.isSelected()) {
			globals.packType = Globals.PRIVATE;
		} else {
			globals.packType = Globals.PUBLIC;
		}
		recheckMods();
	}

	public void recheckMods() {
		goodMods.clear();
		badMods.clear();
		for (int i = 0; i < knownMods.getSize(); i++) {
			unknownMods.addElement(knownMods.elementAt(i));
		}
		knownMods.clear();

		ArrayList<Mod> mods;
		for (int i = unknownMods.getSize() - 1; i >= 0; i--) {
			mods = processModFile(unknownMods.elementAt(i));
			if (mods != null) {
				knownMods.addElement(unknownMods.elementAt(i));
				unknownMods.remove(i);
				for (Mod mod : mods) {
					badMods.addElement(mod);
				}
			}
			mods = null;
		}

		for (int i = badMods.getSize() - 1; i >= 0; i--) {
			ModInfo temp = nameRegistry.getMod(badMods.get(i).shortName);
			if (temp != null) { // TODO FTB
				if ((globals.packType == Globals.PUBLIC && (temp.publicPolicy == ModInfo.OPEN || temp.publicPolicy == ModInfo.FTB))
						|| (globals.packType == Globals.PRIVATE && (temp.privatePolicy == ModInfo.OPEN || temp.privatePolicy == ModInfo.FTB))
						|| (!temp.customLink.equals(""))) {
					goodMods.addElement(badMods.get(i));
					badMods.remove(i);
				}

				// System.out.println(temp.modName+" is good");
			}
		}
		sortDefaultListModel(goodMods);
		sortDefaultListModel(badMods);
	}

	public static void sortDefaultListModel(DefaultListModel<Mod> model) {
		ArrayList<Mod> list = new ArrayList<Mod>();
		for (int i = 0; i < model.getSize(); i++) {
			list.add(model.get(i));
		}
		Collections.sort(list);
		model.clear();
		for (Mod mod : list) {
			model.addElement(mod);
		}
	}

	private static ArrayList<Mod> processModFile(ModFile modFile) {
		String result = null;
		HashSet<String> identifiedIDs = new HashSet<String>();
		ArrayList<Mod> out = new ArrayList<Mod>();
		for (int i = 0; i < modFile.IDs.getSize(); i++) {
			result = nameRegistry.checkID(modFile.IDs.get(i));
			if (result != null) {
				identifiedIDs.add(result);
			}
		}
		if (identifiedIDs.isEmpty()) {
			return null;
		} else {
			for (String ID : identifiedIDs) {
				out.add(new Mod(modFile, ID));
			}
			return out;
		}
	}

	private void writeFile() {
		// TODO check for no modpack
		File infoFile = new File(globals.minecraftFolder + "/perms.txt");
		System.out.println("Printing to: " + infoFile.getAbsolutePath());

		StringBuilder bldr = new StringBuilder();
		bldr.append("Permission categories and full licenses for mods marked spreadsheet are available here: http://1drv.ms/1c8mItH\n");
		bldr.append("For any problems, please contact Watchful11 on the FTB forums.\n");

		bldr.append("This is a ");
		bldr.append(globals.getStringType());
		bldr.append(" pack\n\n");

		for (int i = 0; i < goodMods.getSize(); i++) {
			ModInfo modInfo = nameRegistry.getMod(goodMods.get(i).shortName);
			bldr.append("(");
			bldr.append(modInfo.getStringPolicy());
			bldr.append(":");
			if (modInfo.officialSpreadsheet)
				bldr.append("Spreadsheet");
			else
				bldr.append("Custom");
			bldr.append(") ");
			bldr.append(modInfo.modName);
			bldr.append(" by ");
			bldr.append(modInfo.modAuthor);
			bldr.append(" can be found at ");
			bldr.append(modInfo.modLink);
			bldr.append(".");

			if (!modInfo.officialSpreadsheet) {
				bldr.append(" The license is, ");
				bldr.append(modInfo.getCurrentPermLink());
				if (modInfo.licenseLink.equals("PM")) {
					bldr.append(", which is a private message.");
				} else if (modInfo.licenseLink.equals(modInfo.modLink)
						|| modInfo.licenseLink.equals("")) {
					bldr.append(".");
				} else {
					bldr.append(", and can be found ");
					bldr.append(modInfo.licenseLink);
					bldr.append(".");
				}
			}

			switch (modInfo.getCurrentPolicy()) {
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

	public void setSavesFolder(File savesFolder) {
		Globals.getInstance().preferences.saveFolder = savesFolder;
		savePreferences();

	}

	public void savePreferences() {
		FileUtils.saveObject(Globals.getInstance().preferences, new File(
				appstore + "/preferences.conf"));
	}

	public void loadPreferences() {
		File prefFile = new File(appstore + "/preferences.conf");
		if (prefFile.exists()) {
			Globals.getInstance().preferences = (Preferences) FileUtils
					.readObject(new File(appstore + "/preferences.conf"),
							new Preferences());
		} else {
			Globals.getInstance().preferences = new Preferences();
		}
	}
}