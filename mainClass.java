import gr.watchful.permchecker.modhandling.Mod;
import gr.watchful.permchecker.modhandling.ModFile;
import gr.watchful.permchecker.modhandling.ModFinder;
import gr.watchful.permchecker.panels.NamedScrollingListPanel;
import gr.watchful.permchecker.utils.FileUtils;

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

@SuppressWarnings("serial")
public class mainClass extends JFrame {
	DefaultListModel<Mod> goodMods;
	DefaultListModel<Mod> badMods;
	DefaultListModel<ModFile> unknownMods;
	File permFile;

	public mainClass() {
		goodMods = new DefaultListModel<Mod>();
		badMods = new DefaultListModel<Mod>();
		unknownMods = new DefaultListModel<ModFile>();
		
		try {
			permFile = File.createTempFile("PermissionsCheckerPermFile", ".xlsx");
		} catch (IOException e) {
			// TODO Tell user error (No perms?)
		}

		this.setTitle("Permissions Checker"); // set the title
		this.setPreferredSize(new Dimension(300, 300)); // and the initial size

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		mainPanel.setAlignmentX(0f);
		this.add(mainPanel);

		NamedScrollingListPanel<Mod> good = new NamedScrollingListPanel<Mod>(
				"Good", new Dimension(100, 300), goodMods);
		mainPanel.add(good);
		NamedScrollingListPanel<Mod> bad = new NamedScrollingListPanel<Mod>(
				"Bad", new Dimension(100, 300), badMods);
		mainPanel.add(bad);
		NamedScrollingListPanel<ModFile> unknown = new NamedScrollingListPanel<ModFile>(
				"Unknown", new Dimension(100, 300), unknownMods);
		mainPanel.add(unknown);

		JPanel newWindow = new JPanel();
		JPanel modEditWindow = new JPanel();

		JPanel cards = new JPanel(new CardLayout());
		cards.setMinimumSize(new Dimension(300, 300));
		cards.add(newWindow);
		cards.add(modEditWindow);

		// mainPanel.add(cards);

		JMenuBar menuBar = new JMenuBar(); // create the menu
		JMenu menu = new JMenu("Temp"); // with the submenus
		menuBar.add(menu);

		JMenuItem updatePerms = new JMenuItem("Force-update Permissions Listing");

		// listen to all the menu items and then add them to the menus
		updatePerms.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//TODO Check last modified - bandwidth
				try {
					FileUtils.downloadToFile(new 
							URL("https://skydrive.live.com/download?resid=96628E67B4C51B81!105&authkey=!AK7mlmHB0nrxmHg&ithint=file%2c.xlsx"), permFile);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		menu.add(updatePerms);
		//TODO Folder select (Pre: Check if current folder is valid)
		JMenuItem chooseModpack = new JMenuItem("Choose Modpack");

		// listen to all the menu items and then add them to the menus
		chooseModpack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Insert folder chooser (Check if current directory?)
				JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fileChooser.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
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

	public static void main(String[] args) {
		new mainClass();
	}

	private void discoverMods(File minecraftFolder) {
		ModFinder.discoverAllMods(minecraftFolder, unknownMods, badMods);
	}
}
