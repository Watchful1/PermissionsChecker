import gr.watchful.permchecker.datastructures.Mod;
import gr.watchful.permchecker.datastructures.ModFile;
import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;
import gr.watchful.permchecker.modhandling.ModFinder;
import gr.watchful.permchecker.panels.NamedScrollingListPanel;
import gr.watchful.permchecker.utils.FileUtils;

import u.r.a.l.bearbear12345.permchecker.utils.ExcelUtils;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class mainClass extends JFrame implements NamedScrollingListPanelListener {
	DefaultListModel<Mod> goodMods;
	DefaultListModel<Mod> badMods;
	DefaultListModel<ModFile> unknownMods;
	NamedScrollingListPanel<Mod> good;
	NamedScrollingListPanel<Mod> bad;
	NamedScrollingListPanel<ModFile> unknown;
	File permFile;

	public mainClass() {
		goodMods = new DefaultListModel<Mod>();
		badMods = new DefaultListModel<Mod>();
		unknownMods = new DefaultListModel<ModFile>();

		try {
			permFile = File.createTempFile("PermissionsCheckerPermFile",
					".xlsx");
		} catch (IOException e) {
			// TODO Tell user error (No perms?)
		}

		this.setTitle("Permissions Checker"); // Set the window title
		this.setPreferredSize(new Dimension(600, 300)); // and the initial size

		//TODO move this stuff to a seperate method
		File currentDir = new File(System.getProperty("user.dir"));
		if(isMinecraftDir(currentDir)) {
			discoverMods(currentDir);
		} else if(isMinecraftDir(currentDir.getParentFile())) {
			discoverMods(currentDir.getParentFile());
		} else {
			//TODO implement a selection of modpacks, maybe save modpack locations for later use
			//also allow selecting of multiMC instances folder
			
			//debug
			discoverMods(new File("C:\\Users\\Gregory\\Desktop\\MultiMC\\instances\\Hammercraft 4.3.0 Custom\\minecraft"));
		}

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		mainPanel.setAlignmentX(0f);
		this.add(mainPanel);

		good = new NamedScrollingListPanel<Mod>(
				"Good", new Dimension(100, 300), goodMods);
		good.addListener(this);
		mainPanel.add(good);
		bad = new NamedScrollingListPanel<Mod>(
				"Bad", new Dimension(100, 300), badMods);
		bad.addListener(this);
		mainPanel.add(bad);
		unknown = new NamedScrollingListPanel<ModFile>(
				"Unknown", new Dimension(100, 300), unknownMods);
		mainPanel.add(unknown);
		unknown.addListener(this);

		JPanel newWindow = new JPanel();
		JPanel modEditWindow = new JPanel();

		JPanel cards = new JPanel(new CardLayout());
		cards.setMinimumSize(new Dimension(300, 300));
		cards.add(newWindow);
		cards.add(modEditWindow);

		mainPanel.add(cards);

		JMenuBar menuBar = new JMenuBar(); // create the menu
		JMenu menu = new JMenu("Temp"); // with the submenus
		menuBar.add(menu);

		JMenuItem updatePerms = new JMenuItem(
				"Force-update Permissions Listing");

		// listen to all the menu items and then add them to the menus
		updatePerms.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Check last modified - bandwidth
				try {
					FileUtils
							.downloadToFile(
									new URL(
											"https://skydrive.live.com/download?resid=96628E67B4C51B81!105&authkey=!AK7mlmHB0nrxmHg&ithint=file%2c.xlsx"),
									permFile);
					ExcelUtils.toArray(permFile);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		menu.add(updatePerms);
		// TODO Folder select (Pre: Check if current folder is valid)
		JMenuItem chooseModpack = new JMenuItem("Choose Modpack");

		// listen to all the menu items and then add them to the menus
		chooseModpack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser(System
						.getProperty("user.home"));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File result = fileChooser.getSelectedFile();
					discoverMods(result);
				}
			}
		});
		menu.add(chooseModpack);

		this.setJMenuBar(menuBar);

		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private static boolean isMinecraftDir(File file) {
		return file.getName().equals("minecraft") || file.getName().equals(".minecraft");
	}

	public static void main(String[] args) {
		new mainClass();
	}

	private void discoverMods(File minecraftFolder) {
		ModFinder.discoverAllMods(minecraftFolder, unknownMods, badMods);
	}

	public void selectionChanged(NamedSelectionEvent event) {
		System.out.println(event.getParentName()+" : "+event.getSelected());
		clearOthers(event.getParentName());
	}
	
	private void clearOthers(String selected) {
		if(!selected.equals("Good")) {
			good.clearSelection();
		}
		if(!selected.equals("Bad")) {
			bad.clearSelection();
		}
		if(!selected.equals("Unknown")) {
			unknown.clearSelection();
		}
	}
}
