package gr.watchful.permchecker;

import gr.watchful.permchecker.datastructures.*;
import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;
import gr.watchful.permchecker.panels.ModPacksPanel;
import gr.watchful.permchecker.panels.NamedScrollingListPanel;
import gr.watchful.permchecker.panels.PermissionsPanel;
import gr.watchful.permchecker.panels.UpdatePanel;
import gr.watchful.permchecker.utils.FileUtils;
import gr.watchful.permchecker.utils.OsTypes;
import gr.watchful.permchecker.utils.Updater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class mainClass extends JFrame implements ListsPacks {
	private JTabbedPane tabbedPane;
	private ModPacksPanel modPacksPanel;
    private UpdatePanel updatePanel;
    private PermissionsPanel permissionsPanel;
    public SortedListModel<ModPack> modPacksModel;
    public NamedScrollingListPanel<ModPack> modPacksList;
    private ModPack oldSelection;

	public mainClass() {
		long startTime = System.nanoTime();
		//System.out.println("Time 1: " + (System.nanoTime() - startTime) / 1000000);
        Globals.getInstance().mainFrame = this;
		Globals.getInstance().listsPacks = this;
        Globals.getInstance().initializeFolders();
        Globals.getInstance().loadPreferences();
		long tempTime = System.nanoTime();
        Globals.getInstance().updateListings();
		System.out.println("Perm listings update took: " + (System.nanoTime() - tempTime) / 1000000);

		this.setTitle("Permissions Checker v "+Globals.version); // Set the window title
		this.setPreferredSize(new Dimension(1100, 600)); // and the initial size

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        modPacksModel = new SortedListModel<>();
        loadPacks(Globals.getInstance().preferences.saveFolder);

        modPacksList = new NamedScrollingListPanel<>("ModPacks", 200, modPacksModel, true);
        modPacksList.setAlignmentX(Component.LEFT_ALIGNMENT);

        modPacksPanel = new ModPacksPanel(modPacksList);
		Globals.getInstance().addListener(modPacksPanel);
		updatePanel = new UpdatePanel();
		Globals.getInstance().addListener(updatePanel);
		permissionsPanel = new PermissionsPanel();
		Globals.getInstance().addListener(permissionsPanel);
		updatePanel.permPanel = permissionsPanel;

        modPacksList.addListener(new NamedScrollingListPanelListener() {
            @Override
            public void selectionChanged(NamedSelectionEvent event) {
                if(oldSelection != null && oldSelection.equals(modPacksList.getSelected())) return;
				Globals.getInstance().setModPack(modPacksList.getSelected());
                oldSelection = modPacksList.getSelected();
				modPacksList.revalidate();
            }
        });
		modPacksList.setSelected(0);
        leftPanel.add(modPacksList);

        this.add(leftPanel, BorderLayout.LINE_START);
		
		tabbedPane = new JTabbedPane();

        tabbedPane.add("Info", modPacksPanel);
        tabbedPane.add("Update", updatePanel);
		tabbedPane.add("Permissions", permissionsPanel);

		this.add(tabbedPane);


		JMenuBar menuBar = new JMenuBar(); // create the menu
		JMenu menu = new JMenu("Temp"); // with the submenus
		menuBar.add(menu);

        JMenuItem updatePerms = new JMenuItem("Update Permissions");
        updatePerms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Globals.getInstance().updateListings();
				permissionsPanel.invalidateContents();
            }
        });
        menu.add(updatePerms);

		JMenuItem newPack = new JMenuItem("Add pack");
		newPack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addPack();
			}
		});
		menu.add(newPack);

		JMenuItem newPackFromCode = new JMenuItem("Add pack from code");
		newPackFromCode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String code = JOptionPane.showInputDialog(
						Globals.getInstance().mainFrame, "Pack code", "Add pack",
						JOptionPane.PLAIN_MESSAGE);
				if(code == null || code.length() <= 0) return;

				ModPack temp = FileUtils.packFromCode(code).get(0);
				if(temp == null) {
					System.out.println("Couldn't find code");
					return;
				}
				System.out.println("Adding pack "+temp);
				temp.key = code;
				addPack(temp);
			}
		});
		menu.add(newPackFromCode);

        JMenuItem openFolder = new JMenuItem("Open appdata");
        openFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(!OsTypes.getOperatingSystemType().equals(OsTypes.OSType.Windows)) {
                    System.out.println("Os is not windows, can't open explorer");
                    return;
                }
                try {
                    Desktop.getDesktop().open(Globals.getInstance().appStore);
                } catch (IOException e) {
                    System.out.println("Couldn't open explorer");
                    return;
                }
            }
        });
        menu.add(openFolder);

        JMenuItem drastic = new JMenuItem("Drastic click here");
        drastic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
                        "More ram has been downloaded to your system.\nHave a nice day.");
            }
        });
        menu.add(drastic);

		this.setJMenuBar(menuBar);


		pack();
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setVisible(true);

		long endTime = System.nanoTime();
		System.out.println("Startup took: " + (endTime - startTime) / 1000000);
	}
	
	private static boolean isMinecraftDir(File file) {
		return file.getName().equals("minecraft") || file.getName().equals(".minecraft");
	}

    public void loadPacks(File folder) {
        if(!folder.exists() || !folder.isDirectory()) return;
        Globals.getInstance().oldVersionsFlag = false;
        for(File pack : folder.listFiles()) {
            if(pack.getName().equals(Globals.curseFileName)) continue;
			//System.out.println("Loading "+pack.getName());
            ModPack temp = ModPack.loadObject(pack);
            if(temp != null) {
                modPacksModel.addElement(temp);
            }
        }
		modPacksModel.sort(new SimpleObjectComparator());

        if(Globals.getInstance().oldVersionsFlag) {
            JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
                    "Some modpacks were on a newer json format than this version supports and were not loaded.\nGet the new version from Watchful1");
        }
    }

	public void addPack() {
		addPack(new ModPack());
	}

    public void addPack(ModPack pack) {
        modPacksModel.addElement(pack);
        modPacksList.setSelected(modPacksModel.getSize()-1);
        modPacksList.sortKeepSelected();
        Globals.setModPack(pack);
    }

	public boolean codeExists(String code, String currentPack) {
		if(code == null || code.equals("")) return false;
		ArrayList<ModPack> packs = FileUtils.packFromCode(code);

		if(packs != null) {
			ModPack pack = packs.get(0);
			if(pack.shortName != null &&
					!pack.shortName.equals("") && !currentPack.equals(pack.shortName)) return true;
		}

		for(int i=0; i<modPacksList.getModel().getSize(); i++) {
			ModPack newPack = modPacksList.getModel().get(i);
			if(code.equals(newPack.key) && !currentPack.equals(newPack.shortName)) return true;
		}
		return false;
	}

	public boolean shortnameExists(String shortname) {
		for(int i=0; i<modPacksList.getModel().getSize(); i++) {
			ModPack newPack = modPacksList.getModel().get(i);
			if(shortname.equals(newPack.shortName)) {
				System.out.println("Shortname exists in pack with name "+newPack.name);
				return true;
			}
		}

		if(FileUtils.remoteFileExists(Globals.ftbRepoUrl+"static/"+shortname+"Icon.png")) {
			System.out.println("Remote icon exists for "+shortname);
			return true;
		}
		if(FileUtils.remoteFileExists(Globals.ftbRepoUrl+"static/"+shortname+"Splash.png")) {
			System.out.println("Remote splash exists for "+shortname);
			return true;
		}
		if(FileUtils.remoteFileExists(Globals.ftbRepoUrl+"privatepacks/"+shortname)) {
			System.out.println("Remote folder exists for "+shortname);
			return true;
		}
		return false;
	}

    public boolean curseIDUsed(String curseID, String currentPack) {
        for(int i=0; i<modPacksList.getModel().getSize(); i++) {
            ModPack pack = modPacksList.getModel().get(i);
            if(curseID.equals(pack.curseID) && !currentPack.equals(pack.shortName)) {
                System.out.println("CurseID exists in pack with name "+pack.name);
                return true;
            }
        }
        return false;
    }

	@Override
	public void nameChanged() {
		modPacksList.sortKeepSelected();
	}

	public static void main(String[] args) {
        if (args.length >= 3) {
            if (args[0].equals("-u")) {
                Updater.finishUpdate(args[1], args[2]);
                return;
            } else if (args[0].equals("-c")) {
                Updater.cleanup(args[1], args[2]);
            } else {
                System.out.println("Unrecognized argument");
                return;
            }
        }

        String newVersion = Updater.checkUpdate(Globals.version);
        System.out.println(newVersion);
        if(newVersion != null) Updater.startUpdate(newVersion);

        mainClass main = new mainClass();
		main.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				Globals.saveCurrentPack();
				((JFrame)(e.getComponent())).dispose();
			}
		});
    }
}
