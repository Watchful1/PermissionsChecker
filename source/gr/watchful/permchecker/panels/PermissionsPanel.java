package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.*;
import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;
import gr.watchful.permchecker.modhandling.ModFinder;
import gr.watchful.permchecker.utils.DatastructureUtils;
import gr.watchful.permchecker.utils.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Eventually I will move everything relevant to the permissions panel from the mainClass constructor to here
 * @author watchful
 */
@SuppressWarnings("serial")
public class PermissionsPanel extends JPanel implements NamedScrollingListPanelListener, RebuildsMods {
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

    public PermissionsPanel() {
        goodMods = new DefaultListModel<>();
        badMods = new DefaultListModel<>();
        unknownMods = new DefaultListModel<>();
        knownMods = new DefaultListModel<>();

        Globals.getInstance().main = this;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(0f);
        buttonPanel.add(new JLabel("Public"));

        packTypeToggle = new JToggleButton();
        packTypeToggle.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                updateToggleIcons();
                updateSettings();
            }
        });

        Dimension temp = new Dimension(92,28);
        packTypeToggle.setMaximumSize(temp);
        packTypeToggle.setMinimumSize(temp);
        packTypeToggle.setPreferredSize(temp);

        try {//try as hard as possible to load the images and set them on the toggle button
            URL leftUrl = getClass().getResource("toggleLeft.png");
            URL rightUrl = getClass().getResource("toggleRight.png");
            if(leftUrl == null || rightUrl == null) {
                ImageIcon leftIcon = new ImageIcon("bin/resources/toggleLeft.png");
                ImageIcon rightIcon = new ImageIcon("bin/resources/toggleRight.png");
                if(leftIcon.getIconWidth() == -1 || rightIcon.getIconWidth() == -1) {
                    packTypeToggle.setText("Switch");
                } else {
                    packTypeToggle.setIcon(leftIcon);
                    packTypeToggle.setSelectedIcon(rightIcon);
                }
            } else {
                Image leftImg = ImageIO.read(leftUrl);
                Image rightImg = ImageIO.read(rightUrl);
                if(leftImg == null || rightImg == null) {
                    packTypeToggle.setText("Switch");
                } else {
                    packTypeToggle.setIcon(new ImageIcon(leftImg));
                    packTypeToggle.setSelectedIcon(new ImageIcon(rightImg));
                }
            }
        } catch (IOException ex) {
            packTypeToggle.setText("Switch");
        }

        if(packTypeToggle.getIcon() != null) {
            Dimension temp2 = new Dimension(packTypeToggle.getIcon().getIconWidth()+2,packTypeToggle.getIcon().getIconHeight()+2);
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

        this.add(buttonPanel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setAlignmentX(0f);
        this.add(mainPanel);

        good = new NamedScrollingListPanel<>(
                "Good", 100, goodMods);
        good.addListener(this);
        mainPanel.add(good);
        bad = new NamedScrollingListPanel<>(
                "Bad", 100, badMods);
        bad.addListener(this);
        mainPanel.add(bad);
        unknown = new NamedScrollingListPanel<>(
                "Unknown", 100, unknownMods);
        mainPanel.add(unknown);
        unknown.addListener(this);

        JPanel newWindow = new JPanel();
        JPanel modEditWindow = new JPanel();

        cards = new JPanel(new CardLayout());
        cards.setMinimumSize(new Dimension(500, 300));
        cards.setMaximumSize(new Dimension(500, 900));
        cards.add(newWindow);
        cards.add(modEditWindow);

        modEditor = new ModEditor(new Dimension(500,900));
        cards.add(modEditor,"MODEDITOR");
        modFileEditor = new ModFileEditor(new Dimension(300,300), new ModFile(new File("/")));
        cards.add(modFileEditor,"MODFILEEDITOR");

        CardLayout cardLayout = (CardLayout)(cards.getLayout());
        cardLayout.show(cards, "MODEDITOR");

        mainPanel.add(cards);
    }

    public void discoverMods(File minecraftFolder) {
        Globals.getInstance().minecraftFolder = minecraftFolder;
        Globals.getInstance().nameRegistry.loadCustomInfos();
        ModFinder.discoverModFiles(minecraftFolder, unknownMods);
        recheckMods();
    }

    public void selectionChanged(NamedSelectionEvent event) {
        System.out.println(event.getParentName()+" : "+event.getSelected());
        updateEditor(event.getParentName(),event.getSelected());
    }

    private void updateEditor(String list, int selected) {
        if(list.equals("Good")) {
            bad.clearSelection();
            unknown.clearSelection();

            CardLayout cardLayout = (CardLayout)(cards.getLayout());
            cardLayout.show(cards, "MODEDITOR");

            modEditor.setMod(Globals.getInstance().nameRegistry.getMod(good.getSelected().shortName), good.getSelected().shortName);
        }
        if(list.equals("Bad")) {
            good.clearSelection();
            unknown.clearSelection();

            CardLayout cardLayout = (CardLayout)(cards.getLayout());
            cardLayout.show(cards, "MODEDITOR");

            modEditor.setMod(Globals.getInstance().nameRegistry.getMod(bad.getSelected().shortName), bad.getSelected().shortName);
        }
        if(list.equals("Unknown")) {
            good.clearSelection();
            bad.clearSelection();

            CardLayout cardLayout = (CardLayout)(cards.getLayout());
            cardLayout.show(cards, "MODFILEEDITOR");

            modFileEditor.setModFile(unknown.getSelected());
        }
    }

    private void updateToggleIcons() {
        if(packTypeToggle.isSelected()) {
            packTypeToggle.setPressedIcon(packTypeToggle.getIcon());
        } else {
            packTypeToggle.setPressedIcon(packTypeToggle.getSelectedIcon());
        }
    }

    public void updateSettings() {
        if(packTypeToggle.isSelected()) {
            Globals.getInstance().packType = Globals.PRIVATE;
        } else {
            Globals.getInstance().packType = Globals.PUBLIC;
        }
        recheckMods();
    }

    public void recheckMods() {
        goodMods.clear();
        badMods.clear();
        for(int i=0; i<knownMods.getSize(); i++) {
            unknownMods.addElement(knownMods.elementAt(i));
        }
        knownMods.clear();

        ArrayList<Mod> mods;
        for(int i=unknownMods.getSize()-1; i>=0; i--) {
            mods = processModFile(unknownMods.elementAt(i));
            if(mods != null) {
                knownMods.addElement(unknownMods.elementAt(i));
                unknownMods.remove(i);
                for(Mod mod : mods) {
                    badMods.addElement(mod);
                }
            }
        }

        for(int i=badMods.getSize()-1; i>=0; i--) {
            ModInfo temp = Globals.getInstance().nameRegistry.getMod(badMods.get(i).shortName);
            if(temp != null) {//TODO FTB
                if((Globals.getInstance().packType == Globals.PUBLIC && (temp.publicPolicy == ModInfo.OPEN || temp.publicPolicy == ModInfo.FTB)) ||
                        (Globals.getInstance().packType == Globals.PRIVATE && (temp.privatePolicy == ModInfo.OPEN || temp.privatePolicy == ModInfo.FTB)) ||
                        (!temp.customLink.equals(""))) {
                    goodMods.addElement(badMods.get(i));
                    badMods.remove(i);
                }
            }
        }
        DatastructureUtils.sortDefaultListModel(goodMods);
        DatastructureUtils.sortDefaultListModel(badMods);
    }

    public void writeFile() {
        //TODO check for no modpack
        File infoFile = new File(Globals.getInstance().minecraftFolder+File.separator+"perms.txt");
        System.out.println("Printing to: "+infoFile.getAbsolutePath());

        StringBuilder bldr = new StringBuilder();
        bldr.append("Permission categories and full licenses for mods marked spreadsheet are available here: http://1drv.ms/1c8mItH\n");
        bldr.append("For any problems, please contact Watchful11 on the FTB forums.\n");

        bldr.append("This is a ");
        bldr.append(Globals.getInstance().getStringType());
        bldr.append(" pack\n\n");

        for(int i=0; i<goodMods.getSize(); i++) {
            ModInfo modInfo = Globals.getInstance().nameRegistry.getMod(goodMods.get(i).shortName);
            bldr.append("(");
            bldr.append(modInfo.getStringPolicy());
            bldr.append(":");
            if(modInfo.officialSpreadsheet) bldr.append("Spreadsheet");
            else bldr.append("Custom");
            bldr.append(") ");
            bldr.append(modInfo.modName); bldr.append(" by ");
            bldr.append(modInfo.modAuthor); bldr.append(" can be found at ");
            bldr.append(modInfo.modLink); bldr.append(".");

            if(!modInfo.officialSpreadsheet) {
                bldr.append(" The license is, ");
                bldr.append(modInfo.getCurrentPermLink());
                if(modInfo.licenseLink.equals("PM")) {
                    bldr.append(", which is a private message.");
                } else if(modInfo.licenseLink.equals(modInfo.modLink) || modInfo.licenseLink.equals("")) {
                    bldr.append(".");
                } else {
                    bldr.append(", and can be found ");
                    bldr.append(modInfo.licenseLink);
                    bldr.append(".");
                }
            }

            switch(modInfo.getCurrentPolicy()) {
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

    private static ArrayList<Mod> processModFile(ModFile modFile) {
        String result;
        HashSet<String> identifiedIDs = new HashSet<>();
        ArrayList<Mod> out = new ArrayList<>();
        for(int i=0; i<modFile.IDs.getSize(); i++) {
            result = Globals.getInstance().nameRegistry.checkID(modFile.IDs.get(i));
            if(result != null) {
                identifiedIDs.add(result);
            }
        }
        if(identifiedIDs.isEmpty()) {
            return null;
        } else {
            for(String ID : identifiedIDs) {
                out.add(new Mod(modFile, ID));
            }
            return out;
        }
    }
}
