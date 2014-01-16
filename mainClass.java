import gr.watchful.permchecker.datastructures.Mod;
import gr.watchful.permchecker.datastructures.ModFile;
import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;
import gr.watchful.permchecker.modhandling.ModFinder;
import gr.watchful.permchecker.panels.ModEditor;
import gr.watchful.permchecker.panels.ModFileEditor;
import gr.watchful.permchecker.panels.NamedScrollingListPanel;
import gr.watchful.permchecker.utils.ExcelUtils;
import gr.watchful.permchecker.utils.FileUtils;
import gr.watchful.permchecker.utils.bearbear12345.OsTypes;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
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
public class mainClass extends JFrame implements NamedScrollingListPanelListener {
    private DefaultListModel<Mod> goodMods;
    private DefaultListModel<Mod> badMods;
    private DefaultListModel<ModFile> unknownMods;
    private NamedScrollingListPanel<Mod> good;
    private NamedScrollingListPanel<Mod> bad;
    private NamedScrollingListPanel<ModFile> unknown;
    private File permFile;
    private File appstore; //Location for the spreadsheet file
    private JPanel cards;
    private ModEditor modEditor;
    private ModFileEditor modFileEditor;

    public mainClass() {
        goodMods = new DefaultListModel<Mod>();
        badMods = new DefaultListModel<Mod>();
        unknownMods = new DefaultListModel<ModFile>();

        /**
         * Check which OS the system is running, and make the appropriate directories if necessary
         * @author bearbear12345
         */
        OsTypes.OSType ostype = OsTypes.getOperatingSystemType();
        System.out.println("Operating System: " + ostype.toString());
        System.out.println("Searching for application storage directory...");
        switch (ostype) {
            case Windows:
                appstore = new File(System.getenv("APPDATA") + "/ftb/PermissionsChecker");
                break;
            case MacOS:
                appstore = new File(System.getProperty("user.home") + "/Library/Application Support/ftb/PermissionsChecker");
                break;
            case Linux:
                appstore = new File(System.getProperty("user.home") + "/.ftb/PermissionsChecker");
                break;
            case Other:
                //TODO ????
                break;
        }
        if (!appstore.exists()) {
            System.out.println("Directory not found! Creating directory: " + appstore.getPath());
            boolean result = appstore.mkdirs();
            if (result) {
                System.out.println(appstore.getPath() + " created!");
            }
        } else {
            System.out.println("Directory exists!");
        }


        // Checks if PermissionsCheckerPermFile.xlsx is existent
        /*
         * Old Code permFile = File.createTempFile("PermissionsCheckerPermFile",
         * ".xlsx");
         */
        permFile = new File(appstore.getPath() + "/PermissionsChecker.data");
        if (!permFile.exists()) {
            try {
                permFile.createNewFile();
            } catch (IOException e) {
                System.out.println("An error occured while creating during setup. Please try again later. If issues persist, contact the author");
            }
        }
        //TODO I guess first run should download? - bearbear12345

        this.setTitle("Permissions Checker"); // Set the window title
        this.setPreferredSize(new Dimension(600, 300)); // and the initial size

        // TODO move this stuff to a seperate method
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

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setAlignmentX(0f);
        this.add(mainPanel);

        good = new NamedScrollingListPanel<Mod>("Good", new Dimension(100, 300), goodMods);
        good.addListener(this);
        mainPanel.add(good);
        bad = new NamedScrollingListPanel<Mod>("Bad", new Dimension(100, 300), badMods);
        bad.addListener(this);
        mainPanel.add(bad);
        unknown = new NamedScrollingListPanel<ModFile>("Unknown", new Dimension(100, 300), unknownMods);
        mainPanel.add(unknown);
        unknown.addListener(this);

        JPanel newWindow = new JPanel();
        JPanel modEditWindow = new JPanel();

        cards = new JPanel(new CardLayout());
        cards.setMinimumSize(new Dimension(300, 300));
        cards.add(newWindow);
        cards.add(modEditWindow);

        modEditor = new ModEditor(new Dimension(300, 300));
        cards.add(modEditor, "MODEDITOR");
        modFileEditor = new ModFileEditor(new Dimension(300, 300));
        cards.add(modFileEditor, "MODFILEEDITOR");

        CardLayout cardLayout = (CardLayout) (cards.getLayout());
        cardLayout.show(cards, "MODEDITOR");

        mainPanel.add(cards);

        JMenuBar menuBar = new JMenuBar(); // create the menu
        JMenu menu = new JMenu("Temp"); // with the submenus
        menuBar.add(menu);

        JMenuItem updatePerms = new JMenuItem("Update Permissions Info");

        // listen to all the menu items and then add them to the menus
        updatePerms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

                // TODO Check last modified - bandwidth
                try {

                    FileUtils.downloadToFile(new URL("https://skydrive.live.com/download?resid=96628E67B4C51B81!105&authkey=!AK7mlmHB0nrxmHg"), permFile);
                    try {
                        ExcelUtils.toArray(permFile, 1);
                    } catch (FileNotFoundException e) {
                        System.out.println("The file has been moved or deleted. Please contact the author immediately about this issue.");
                    } catch (IOException e) {
                        System.out.println("An error occured while saving the file. Please try again later. If issues persist, contact the author");
                    }
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
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
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
        System.out.println(event.getParentName() + " : " + event.getSelected());
        updateEditor(event.getParentName(), event.getSelected());
    }

    private void updateEditor(String list, int selected) {
        if (list.equals("Good")) {
            bad.clearSelection();
            unknown.clearSelection();

            CardLayout cardLayout = (CardLayout) (cards.getLayout());
            cardLayout.show(cards, "MODEDITOR");

            modEditor.setMod(good.getSelected());
        }
        if (list.equals("Bad")) {
            good.clearSelection();
            unknown.clearSelection();

            CardLayout cardLayout = (CardLayout) (cards.getLayout());
            cardLayout.show(cards, "MODEDITOR");

            modEditor.setMod(bad.getSelected());
        }
        if (list.equals("Unknown")) {
            good.clearSelection();
            bad.clearSelection();

            CardLayout cardLayout = (CardLayout) (cards.getLayout());
            cardLayout.show(cards, "MODFILEEDITOR");

            modFileEditor.setModFile(unknown.getSelected());
        }
    }
}
