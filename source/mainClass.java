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
	private JTabbedPane tabbedPane;
	private ModPacksPanel modPacksPanel;
    private UpdatePanel updatePanel;
    private PermissionsPanel permissionsPanel;
    public DefaultListModel<ModPack> modPacksModel;
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

        modPacksModel = new DefaultListModel<>();
        loadPacks(Globals.getInstance().preferences.saveFolder);

        modPacksList = new NamedScrollingListPanel<>("ModPacks", 200, modPacksModel);
        modPacksList.setAlignmentX(Component.LEFT_ALIGNMENT);
        modPacksPanel = new ModPacksPanel(modPacksList);
        modPacksList.addListener(new NamedScrollingListPanelListener() {
            @Override
            public void selectionChanged(NamedSelectionEvent event) {
                if(oldSelection == modPacksList.getSelected()) return;
                modPacksPanel.savePack(oldSelection);
                modPacksPanel.setPack(modPacksList.getSelected());
                oldSelection = modPacksList.getSelected();
            }
        });
        leftPanel.add(modPacksList);

        this.add(leftPanel, BorderLayout.LINE_START);
		
		tabbedPane = new JTabbedPane();

        tabbedPane.add("Info", modPacksPanel);
        updatePanel = new UpdatePanel();
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
