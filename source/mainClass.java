import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModPack;
import gr.watchful.permchecker.datastructures.SimpleObjectComparator;
import gr.watchful.permchecker.datastructures.SortedListModel;
import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;
import gr.watchful.permchecker.panels.ModPacksPanel;
import gr.watchful.permchecker.panels.NamedScrollingListPanel;
import gr.watchful.permchecker.panels.PermissionsPanel;
import gr.watchful.permchecker.panels.UpdatePanel;
import gr.watchful.permchecker.utils.FileUtils;
import gr.watchful.permchecker.utils.OsTypes;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class mainClass extends JFrame {
	private JTabbedPane tabbedPane;
	private ModPacksPanel modPacksPanel;
    private UpdatePanel updatePanel;
    private PermissionsPanel permissionsPanel;
    public SortedListModel<ModPack> modPacksModel;
    public NamedScrollingListPanel<ModPack> modPacksList;
    private ModPack oldSelection;

	public mainClass() {
        Globals.getInstance().mainFrame = this;
        Globals.getInstance().initializeFolders();
        Globals.getInstance().loadPreferences();
        Globals.getInstance().updateListings();

		this.setTitle("Permissions Checker v 1.0.5"); // Set the window title
		this.setPreferredSize(new Dimension(1000, 600)); // and the initial size

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        modPacksModel = new SortedListModel<>();
        loadPacks(Globals.getInstance().preferences.saveFolder);

        modPacksList = new NamedScrollingListPanel<>("ModPacks", 200, modPacksModel);
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
				Globals.getInstance().setModPack(modPacksList.getSelected(), null);
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

		tabbedPane.addChangeListener(new ChangeListener() {
			private int previousIndex = 0;
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				if(previousIndex == 0) {
					modPacksPanel.savePack(Globals.getModPack());
					Globals.setModPack(Globals.getModPack(), modPacksPanel);
				}
				previousIndex = index;
			}
		});

		this.add(tabbedPane);


		JMenuBar menuBar = new JMenuBar(); // create the menu
		JMenu menu = new JMenu("Temp"); // with the submenus
		menuBar.add(menu);

        JMenuItem updatePerms = new JMenuItem("Update Permissions");
        updatePerms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Globals.getInstance().updateListings();
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
				String xml = FileUtils.downloadToString(Globals.ftbRepoUrl+code+".xml");
				if(xml == null || xml.length() <= 0) {
					System.out.println("Couldn't find code");
					return;
				}
				ModPack temp = FileUtils.readXML(xml);
				if(temp != null) System.out.println("Adding pack "+temp);
				temp.key = code;
				addPack(temp);
			}
		});
		menu.add(newPackFromCode);

		JMenuItem checkPack = new JMenuItem("Check pack");
		checkPack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				permissionsPanel.parsePack();
			}
		});
		menu.add(checkPack);

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

		JMenuItem exportUnknowns = new JMenuItem("Export and clear unknown mods");
		exportUnknowns.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				StringBuilder bldr = new StringBuilder();
				for (Map.Entry<String, String> entry : Globals.getInstance().preferences.unknownMods.entrySet()) {
					bldr.append(entry.getKey());
					bldr.append(" : ");
					bldr.append(entry.getValue());
					bldr.append("\n");
				}
				FileUtils.writeFile(bldr.toString(), new File(Globals.getInstance().appStore +
					File.separator+"UnknownMods.txt"));
				Globals.getInstance().preferences.unknownMods = new HashMap<>();
				Globals.getInstance().savePreferences();
			}
		});
		menu.add(exportUnknowns);

		this.setJMenuBar(menuBar);


		pack();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private static boolean isMinecraftDir(File file) {
		return file.getName().equals("minecraft") || file.getName().equals(".minecraft");
	}

    public void loadPacks(File folder) {
        if(!folder.exists() || !folder.isDirectory()) return;
        for(File pack : folder.listFiles()) {
            ModPack temp = ModPack.loadObject(pack);
            if(temp != null) {
                modPacksModel.addElement(temp);
            }
        }
		modPacksModel.sort(new SimpleObjectComparator());
    }

	public void addPack() {
		addPack(new ModPack());
	}

    public void addPack(ModPack pack) {
        modPacksModel.addElement(pack);
        modPacksList.setSelected(modPacksModel.getSize()-1);
        modPacksList.sortKeepSelected();
        modPacksPanel.setPack(pack);
    }

    public static void main(String[] args) {
        new mainClass();
    }
}
