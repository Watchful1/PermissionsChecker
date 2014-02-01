
package gr.watchful.permchecker;

import gr.watchful.permchecker.datastructures.Mod;
import gr.watchful.permchecker.datastructures.ModFile;
import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;
import gr.watchful.permchecker.modhandling.ModFinder;
import gr.watchful.permchecker.modhandling.ModNameRegistry;
import gr.watchful.permchecker.panels.ModEditor;
import gr.watchful.permchecker.panels.ModFileEditor;
import gr.watchful.permchecker.panels.NamedScrollingListPanel;
import gr.watchful.permchecker.utils.ExcelUtils;
import gr.watchful.permchecker.utils.FileUtils;
import gr.watchful.permchecker.utils.OsTypes;
import gr.watchful.permchecker.utils.SkydriveUtils;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

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
import javax.swing.JToggleButton;

@SuppressWarnings("serial")
public class mainClass extends JFrame implements NamedScrollingListPanelListener {
    private DefaultListModel<Mod> goodMods;
    private DefaultListModel<Mod> badMods;
    private DefaultListModel<ModFile> unknownMods;
    private NamedScrollingListPanel<Mod> good;
    private NamedScrollingListPanel<Mod> bad;
    private NamedScrollingListPanel<ModFile> unknown;
    private String lastdir = System.getProperty("user.home"); // Modpack folder chooser
    private File permFile;
    public static File appstore; //Location for the spreadsheet file
    public static File appdata; //app.data (Store properties)
    public static String updatedtime;
    private JPanel cards;
    private ModEditor modEditor;
    private ModFileEditor modFileEditor;
    private static ModNameRegistry nameRegistry;

    public mainClass() {
        goodMods = new DefaultListModel<Mod>();
        badMods = new DefaultListModel<Mod>();
        unknownMods = new DefaultListModel<ModFile>();

        nameRegistry = new ModNameRegistry();

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
                appstore = new File(System.getenv("APPDATA") + "/PermissionsChecker");
                break;
            case MacOS:
                appstore = new File(System.getProperty("user.home") + "/Library/Application Support/PermissionsChecker");
                break;
            case Linux:
                appstore = new File(System.getProperty("user.home") + "/.permissionsChecker");
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

        appdata = new File(appstore.getPath() + "/app.data");
        if (!appdata.exists()) {
            try {
                appdata.createNewFile();
                updatedtime = "0000-00-00T00:00:00+0000";
                FileUtils.WriteSettings();
            } catch (IOException e) {
                System.out.println("Cannot create application data! Do you have permission to access the folder: " + appstore.getPath() + "?");
            }
        }

        permFile = new File(appstore.getPath() + "/PermissionsChecker.xlsx");
        if (!permFile.exists()) {
            try {
                permFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Cannot create application data! Do you have permission to access the folder: " + appstore.getPath() + "?");
            }
        }
        this.setTitle("Permissions Checker"); // Set the window title
        this.setPreferredSize(new Dimension(600, 300)); // and the initial size

        //updateListings();

        //TODO move this stuff to a seperate method
        File currentDir = new File(System.getProperty("user.dir"));
        if (isMinecraftDir(currentDir)) {
            discoverMods(currentDir);
        } else if (isMinecraftDir(currentDir.getParentFile())) {
            discoverMods(currentDir.getParentFile());
        } else {
            //TODO implement a selection of modpacks, maybe save modpack locations for later use
            //also allow selecting of multiMC instances folder

            //debug
            //discoverMods(new File("C:\\Users\\Gregory\\Desktop\\MultiMC\\instances\\Hammercraft 4.3.0 Custom\\minecraft"));
        }

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(0f);
        buttonPanel.add(new JLabel("Public"));
        JToggleButton packTypeToggle = new JToggleButton();
        packTypeToggle.setBorderPainted(false);
        packTypeToggle.setContentAreaFilled(false);
        //try {
        //Image img = ImageIO.read(getClass().getResource("src/resources/toggleOff.png"));
        packTypeToggle.setIcon(new ImageIcon("src/resources/toggleOff.png"));
        packTypeToggle.setSelectedIcon(new ImageIcon("src/resources/toggleOn.png"));
        //} catch (IOException ex) {

        //}

        buttonPanel.add(packTypeToggle);
        buttonPanel.add(new JLabel("Private"));

        topPanel.add(buttonPanel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setAlignmentX(0f);
        topPanel.add(mainPanel);

        this.add(topPanel);

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

        JMenuItem updatePerms = new JMenuItem("Update Permission List");

        // listen to all the menu items and then add them to the menus
        updatePerms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                updateListings();
            }
        });
        menu.add(updatePerms);
        // TODO Folder select (Pre: Check if current folder is valid)
        // TODO Last selected directory
        JMenuItem chooseModpack = new JMenuItem("Choose Modpack");

        // listen to all the menu items and then add them to the menus
        chooseModpack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser fileChooser = new JFileChooser(lastdir);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File result = fileChooser.getSelectedFile();
                    lastdir = result.getPath();
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
        ModFinder.discoverAllMods(minecraftFolder, unknownMods, badMods, nameRegistry);
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

            modEditor.setMod(nameRegistry.getMod(good.getSelected().shortName));
        }
        if (list.equals("Bad")) {
            good.clearSelection();
            unknown.clearSelection();

            CardLayout cardLayout = (CardLayout) (cards.getLayout());
            cardLayout.show(cards, "MODEDITOR");

            modEditor.setMod(nameRegistry.getMod(bad.getSelected().shortName));
        }
        if (list.equals("Unknown")) {
            good.clearSelection();
            bad.clearSelection();

            CardLayout cardLayout = (CardLayout) (cards.getLayout());
            cardLayout.show(cards, "MODFILEEDITOR");

            modFileEditor.setModFile(unknown.getSelected());
        }
    }

    public void updateListings() {
        System.out.println("Updating permission listings!");
        String newupdatedtime = null;
        String accesstoken = null;

        // Read setting (Not settingS)
        System.out.println("Reading Settings");
        FileUtils.ReadSettings();

        // Gets Access Token from Microsoft's API
        System.out.println("Retrieving Access Token");
        accesstoken = SkydriveUtils.getKey("access_token", "https://login.live.com/oauth20_token.srf?client_id=000000004410FE50&redirect_uri=https://login.live.com/oauth20_desktop.srf&grant_type=refresh_token&refresh_token=Chf9!6iNyOsxUtX2uCMG*SKiPuyCsVNuof8bK7avToNEtCbfzYspPLEbuRXdxwjOd8CFO7BpgmyJmVDUnCqZrT6eJgtZ7mCZgkpBUiLRFm8fLHzD2tbYyn!fhJ0I7Da7i!CG05xN8ZfAc*0cOo02bsqkfq!nak!fKtRfOUal1nHjMYdkWPnTQ8a86UxYNm0nJvEvAahJoayNzJ5tvSdsD0Ar8uauOmMyixRiXkoGUvxViQlBfJYeKeifBR1uZkb5f!*JLMA5!zUxNxES9ahzYR!MATG!tnqWtZLzWCYcESEo73YtjVcNAUnf26Ad0SWunHY1C*awrgf7OgwVbiruORR9pyZ*3QcXpK5lpMDCIsAK");
        newupdatedtime = SkydriveUtils.getKey("updated_time", "https://apis.live.net/v5.0/file.a4423f3123801749.A4423F3123801749!418?access_token=" + accesstoken);

        System.out.println("Current: " + updatedtime);
        System.out.println("Server:  " + newupdatedtime);

        if (!(updatedtime.equals(newupdatedtime))) {
            System.out.println("A spreadsheet update has been found! Dowloading...");
            FileUtils.downloadToFile("https://skydrive.live.com/download?resid=96628E67B4C51B81!105&authkey=!AK7mlmHB0nrxmHg&ithint=file%2c.xlsx", permFile);
            updatedtime = newupdatedtime;
            FileUtils.WriteSettings();
        } else {
            System.out.println("The local spreadsheet is the same as the cloud version! Not downloading!");
        }
        try {
            ArrayList<ArrayList<String>> infos = ExcelUtils.toArray(permFile, 1);
            infos.remove(0);//remove the first row, it contains column titles
            ArrayList<ArrayList<String>> mappings = ExcelUtils.toArray(permFile, 2);
            nameRegistry.loadMappings(infos, mappings, infos.get(16).get(14), infos.get(16).get(15));
        } catch (FileNotFoundException e) {
            System.out.println("UHOH");
        } catch (IOException e) {
            System.out.println("UHOH");
        }
    }
}
