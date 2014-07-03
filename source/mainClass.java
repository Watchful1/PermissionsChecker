import com.sun.xml.internal.bind.v2.TODO;
import gr.watchful.permchecker.datastructures.*;
import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;
import gr.watchful.permchecker.modhandling.ModFinder;
import gr.watchful.permchecker.modhandling.ModNameRegistry;
import gr.watchful.permchecker.panels.*;
import gr.watchful.permchecker.utils.DatastructureUtils;
import gr.watchful.permchecker.utils.ExcelUtils;
import gr.watchful.permchecker.utils.FileUtils;
import gr.watchful.permchecker.utils.OsTypes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.*;

@SuppressWarnings("serial")
public class mainClass extends JFrame {
	private File permFile;
	private static ModNameRegistry nameRegistry;
	private static Globals globals;
	private JTabbedPane tabbedPane;
	private ModPacksPanel modPacksPanel;
    private PermissionsPanel permissionsPanel;
    public DefaultListModel<ModPack> modPacksModel;
    public NamedScrollingListPanel<ModPack> modPacksList;
    private ModPack oldSelection;

	public mainClass() {
		globals = Globals.getInstance();
        globals.mainFrame = this;
		
		nameRegistry = globals.nameRegistry;

        /**
         * Check which OS the system is running, and make the appropriate directories if necessary
         * @author bearbear12345
         */
        OsTypes.OSType ostype = OsTypes.getOperatingSystemType();
        System.out.println("Operating System: " + ostype.toString());
        System.out.println("Searching for application storage directory...");
        switch (ostype) {
            case Windows:
                Globals.getInstance().appStore = new File(System.getenv("APPDATA") + "/PermissionsChecker");
                break;
            case MacOS:
                Globals.getInstance().appStore = new File(System.getProperty("user.home") + "/Library/Application Support/PermissionsChecker");
                break;
            case Linux:
                Globals.getInstance().appStore = new File(System.getProperty("user.home") + "/.permissionsChecker");
                break;
            case Other:
                //TODO ????
                break;
        }
        if (!Globals.getInstance().appStore.exists()) {
            System.out.println("Directory not found! Creating directory: " + Globals.getInstance().appStore.getPath());
            boolean result = Globals.getInstance().appStore.mkdirs();
            if (result) {
                System.out.println(Globals.getInstance().appStore.getPath() + " created!");
            }
        } else {
            System.out.println("Directory exists!");
        }
        
        loadPreferences();

        permFile = new File(Globals.getInstance().appStore.getPath() + "/PermissionsChecker.xlsx");
        if (!permFile.exists()) {
            try {
                if(!permFile.createNewFile()) throw(new IOException());
            } catch (IOException e) {
                System.out.println("An error occured while creating during setup. Please try again later. If issues persist, contact the author");
            }
        }

		try {
			permFile = File.createTempFile("PermissionsCheckerPermFile",
					".xlsx");
		} catch (IOException e) {
			// TODO Tell user error (No perms?)
		}

		this.setTitle("Permissions Checker"); // Set the window title
		this.setPreferredSize(new Dimension(800, 600)); // and the initial size
		
		updateListings();
        permissionsPanel = new PermissionsPanel();

		//TODO move this stuff to a seperate method
		File currentDir = new File(System.getProperty("user.dir"));
		if(isMinecraftDir(currentDir)) {
            permissionsPanel.discoverMods(currentDir);
		} else if(isMinecraftDir(currentDir.getParentFile())) {
            permissionsPanel.discoverMods(currentDir.getParentFile());
		} //TODO implement a selection of modpacks, maybe save modpack locations for later use

        modPacksPanel = new ModPacksPanel(modPacksList);

        modPacksModel = new DefaultListModel<>();
        loadPacks(Globals.getInstance().preferences.saveFolder);

        modPacksList = new NamedScrollingListPanel<>("ModPacks", 200, modPacksModel);
        modPacksList.addListener(new NamedScrollingListPanelListener() {
            @Override
            public void selectionChanged(NamedSelectionEvent event) {
                if(oldSelection == modPacksList.getSelected()) return;
                modPacksPanel.savePack(oldSelection);
                modPacksPanel.setPack(modPacksList.getSelected());
                oldSelection = modPacksList.getSelected();
            }
        });
        this.add(modPacksList, BorderLayout.LINE_START);
		
		tabbedPane = new JTabbedPane();

        tabbedPane.add("Modpacks", modPacksPanel);
        UpdatePanel updatePanel = new UpdatePanel();
        tabbedPane.add("Update", updatePanel);
		tabbedPane.add("Permissions", permissionsPanel);

		this.add(tabbedPane);

		JMenuBar menuBar = new JMenuBar(); // create the menu
		JMenu menu = new JMenu("Temp"); // with the submenus
		menuBar.add(menu);

		JMenuItem updatePerms = new JMenuItem("Update Permissions");

		// listen to all the menu items and then add them to the menus
		updatePerms.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Check last modified - bandwidth
				updateListings();
			}
		});
		menu.add(updatePerms);
		// TODO Folder select (Pre: Check if current folder is valid)
		JMenuItem chooseModpack = new JMenuItem("Choose Modpack");

		// listen to all the menu items and then add them to the menus
		chooseModpack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//JFileChooser fileChooser = new JFileChooser(new File("C:\\Users\\Gregory\\Desktop\\Private pack staging"));
				JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File result = fileChooser.getSelectedFile();
                    permissionsPanel.discoverMods(result);
				}
			}
		});
		menu.add(chooseModpack);
		
		JMenuItem writeFile = new JMenuItem("Write file");

		// listen to all the menu items and then add them to the menus
		writeFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
                permissionsPanel.writeFile();
			}
		});
		menu.add(writeFile);
		
		JMenuItem setSaveFolder = new JMenuItem("Set Save Folder");

		// listen to all the menu items and then add them to the menus
		setSaveFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File result = fileChooser.getSelectedFile();
					setSavesFolder(result);
				}
			}
		});
		menu.add(setSaveFolder);

		this.setJMenuBar(menuBar);

		pack();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
        System.out.println("DONE");
	}
	
	private static boolean isMinecraftDir(File file) {
		return file.getName().equals("minecraft") || file.getName().equals(".minecraft");
	}

	public static void main(String[] args) {
		new mainClass();
	}

	public void updateListings() {
		try {
			FileUtils
					.downloadToFile(
							new URL("https://skydrive.live.com/download?resid=96628E67B4C51B81!161&ithint=file%2c.xlsx&app=Excel&authkey=!APQ4QtFrBqa1HwM"),
							permFile);
			try {
				ArrayList<ArrayList<String>> infos = ExcelUtils.toArray(permFile,1);
				infos.remove(0);//remove the first row, it contains column titles
				ArrayList<ArrayList<String>> mappings = ExcelUtils.toArray(permFile,2);
				nameRegistry.loadMappings(infos, mappings, infos.get(15).get(14), infos.get(15).get(15));
			} catch (FileNotFoundException e) {
				System.out.println("UHOH");
			} catch (IOException e) {
				System.out.println("uhoh");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setSavesFolder(File savesFolder) {
		Globals.getInstance().preferences.saveFolder = savesFolder;
		savePreferences();
		
		
	}
	
	public void savePreferences() {
		FileUtils.saveObject(Globals.getInstance().preferences, new File(Globals.getInstance().appStore +File.separator+"preferences.conf"));
	}
	
	public void loadPreferences() {
		File prefFile = new File(Globals.getInstance().appStore +File.separator+"preferences.conf");
		if(prefFile.exists()) {
			Globals.getInstance().preferences = (Preferences) FileUtils.readObject(new File(Globals.getInstance().appStore +File.separator+"preferences.conf"), new Preferences());
		} else {
			Globals.getInstance().preferences = new Preferences();
            //TODO THIS IS HARDCODED!!!
            Globals.getInstance().preferences.saveFolder = new File("C:\\Users\\Gregory\\AppData\\Roaming\\PermissionsChecker\\packs");
		}
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

    public void addPack() {
        ModPack newPack = new ModPack();
        modPacksModel.addElement(newPack);
        modPacksList.setSelected(0);
        modPacksList.sortKeepSelected();
        modPacksPanel.setPack(newPack);
    }
}
