import gr.watchful.permchecker.datastructures.*;
import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;
import gr.watchful.permchecker.panels.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

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

		this.setTitle("Permissions Checker"); // Set the window title
		this.setPreferredSize(new Dimension(800, 600)); // and the initial size

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        modPacksModel = new SortedListModel<>();
        loadPacks(Globals.getInstance().preferences.saveFolder);

        modPacksList = new NamedScrollingListPanel<>("ModPacks", 200, modPacksModel);
        modPacksList.setAlignmentX(Component.LEFT_ALIGNMENT);

        modPacksPanel = new ModPacksPanel(modPacksList);
		updatePanel = new UpdatePanel();

        modPacksList.addListener(new NamedScrollingListPanelListener() {
            @Override
            public void selectionChanged(NamedSelectionEvent event) {
                if(oldSelection != null && oldSelection.equals(modPacksList.getSelected())) return;
                modPacksPanel.savePack(oldSelection);
				modPacksPanel.setPack(modPacksList.getSelected());
				updatePanel.setPack(modPacksList.getSelected());
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
        permissionsPanel = new PermissionsPanel();
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
        ModPack newPack = new ModPack();
        modPacksModel.addElement(newPack);
        modPacksList.setSelected(0);
        modPacksList.sortKeepSelected();
        modPacksPanel.setPack(newPack);
    }

    public static void main(String[] args) {
        new mainClass();
    }
}
