package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModPack;
import gr.watchful.permchecker.datastructures.ModPackVersion;
import gr.watchful.permchecker.datastructures.UsesPack;
import gr.watchful.permchecker.utils.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class UpdatePanel extends JPanel implements ChangeListener, UsesPack {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private LabelField packName;
    private FileSelector selector;
    private FileSelector iconSelector;
    private FileSelector splashSelector;
    private FileSelector serverSelector;
    private JButton serverCreator;
    private ModPack currentPack;
    JComboBox<String> versionSelector;

    public UpdatePanel () {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        packName = new LabelField("Pack Name");
        packName.lock("Currently opened pack");
        this.add(packName);

        selector = new FileSelector("Zip", -1, "zip", this);
        this.add(selector);
        iconSelector = new FileSelector("Icon", 150, "png", this);
        this.add(iconSelector);
        splashSelector = new FileSelector("Splash", 150, "png", this);
        this.add(splashSelector);
        serverSelector = new FileSelector("Server", -1, "zip", this);
        this.add(serverSelector);

        this.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel creatorPanel = new JPanel();
        creatorPanel.setLayout(new BoxLayout(creatorPanel, BoxLayout.X_AXIS));
        creatorPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        creatorPanel.add(Box.createHorizontalGlue());

        serverCreator = new JButton("Create Server");
        serverCreator.setEnabled(false);
        serverCreator.addActionListener(e -> createServer());
        creatorPanel.add(serverCreator);

        creatorPanel.add(Box.createHorizontalGlue());
        this.add(creatorPanel);
        this.add(Box.createRigidArea(new Dimension(0, 10)));

        versionSelector = new JComboBox<>();
        versionSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        versionSelector.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        this.add(versionSelector);

        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e) {
                exportPack();
            }
        });
        this.add(exportButton);
    }

    private void createServer () {
        File serverFolder = new File(Globals.getInstance().appStore, "server");
        FileUtils.copyFolder(Globals.getInstance().preferences.workingFolder, serverFolder, true);

        File minecraftFolder = new File(serverFolder, "minecraft");
        if (!minecraftFolder.exists()) {
            LOGGER.warning("Could not create folder, minecraft folder doesn't exist");
            return;
        }
        FileUtils.moveFolderContents(minecraftFolder, serverFolder);
        minecraftFolder.delete();

        File tempZip = new File(Globals.getInstance().appStore, "baseZip.zip");
        Boolean downloadServer = false;
        String commitSHA = null;

        String commitsJSON = FileUtils.downloadToString(Globals.serverCommitsUrl);
        try {
            JSONArray commits = new JSONArray(commitsJSON);
            commitSHA = ((JSONObject) commits.get(0)).getString("sha");
        } catch (JSONException e) {
            LOGGER.warning("Couldn't parse json for commits, downloading anyway");
            downloadServer = true;
        }
        if (tempZip.exists() && !downloadServer) {
            if (Globals.getInstance().preferences.equals("")) {
                LOGGER.info("Local SHA missing, downloading");
                downloadServer = true;
            } else {
                if (commitSHA != null && !commitSHA.equals("")) {
                    LOGGER.info("Found commit SHA: " + commitSHA);
                    LOGGER.info("Stored commit SHA: " + Globals.getInstance().preferences.serverFilesCommitSHA);
                    if (commitSHA.equals(Globals.getInstance().preferences.serverFilesCommitSHA)) {
                        LOGGER.info("SHA matches, using cache");
                    } else {
                        LOGGER.info("SHA doesn't match, downloading");
                        downloadServer = true;
                    }
                } else {
                    LOGGER.info("Couldn't find SHA, downloading");
                    downloadServer = true;
                }
            }
        } else if (!downloadServer) {
            LOGGER.info("Server cache missing, downloading");
            downloadServer = true;
        }
        if (downloadServer) {
            tempZip.delete();
            try {
                FileUtils.downloadToFile(Globals.serverBaseUrl, tempZip);
                Globals.getInstance().preferences.serverFilesCommitSHA = commitSHA;
                Globals.getInstance().savePreferences();
            } catch (IOException | URISyntaxException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
                LOGGER.severe("Could not download server base");
                return;
            }
        }

        File tempFolder = FileUtils.getEmptyFolder(new File(Globals.getInstance().appStore, "tempFolder"));
        FileUtils.extractZipTo(tempZip, tempFolder);
        File serverBaseFolder = new File(tempFolder + File.separator + "FTBServerBase-master" + File.separator + "Server");
        if (!serverBaseFolder.exists()) {
            LOGGER.warning("Server base files missing from zip");
            return;
        }
        FileUtils.moveFolderContents(serverBaseFolder, serverFolder);
        FileUtils.purgeDirectory(serverBaseFolder);

        File forgeFile = new File(serverFolder, "Forge.jar");
        String finalUrl;
        try {
            String forgeUrl = Globals.forgeUniversalUrl.concat(FileUtils.getForgeUrlSlug(currentPack.ForgeVersion, currentPack.minecraftVersion));
            LOGGER.info("forgeurl " + forgeUrl);
            finalUrl = FileUtils.downloadToFile(forgeUrl, forgeFile);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            LOGGER.severe("Could not download forge");
            return;
        }
        Matcher m = Pattern.compile("(?:/forge/).*?/").matcher(finalUrl);
        if (!m.find()) {
            LOGGER.severe("Could not parse forge url: " + finalUrl);
            return;
        }
        String forgeName = m.group(0);
        forgeName = forgeName.substring("/forge/".length(), forgeName.length() - 1);
        String jarName = "FTBserver-" + forgeName + "-universal.jar";
        LOGGER.info(jarName);
        forgeFile.renameTo(new File(serverFolder, jarName));

        m = Pattern.compile("\\d+$").matcher(forgeName);
        if (!m.find()) {
            LOGGER.severe("Could not parse forge number");
            return;
        }
        String forgeVersion = m.group(0);

        for (String file : Globals.getInstance().filesToReplaceServer) {
            File replaceFile = new File(serverFolder, file);
            if (!replaceFile.exists()) {
                LOGGER.warning("File not found, skipping replacement: " + file);
                continue;
            }
            String content = FileUtils.readFile(replaceFile);
            content = content.replaceAll("\\*FORGEVERSION\\*", forgeVersion);
            content = content.replaceAll("\\*MCVERSION\\*", currentPack.minecraftVersion);
            content = content.replaceAll("\\*PACKVERSION\\*", versionSelector.getSelectedItem().toString());
            content = content.replaceAll("\\*PACKSHORTNAME\\*", currentPack.shortName);
            content = content.replaceAll("\\*SERVERJAR\\*", jarName);
            content = content.replaceAll("\\*XML\\*", "modpacks");
            FileUtils.writeFile(content, replaceFile);
        }

        for (String file : Globals.getInstance().filesToDeleteServer) {
            File replaceFile = new File(serverFolder, file);
            if (!replaceFile.exists()) {
                LOGGER.warning("File not found, skipping deletion: " + file);
                continue;
            }
            FileUtils.delete(replaceFile);
        }

        try {
            Desktop.getDesktop().open(serverFolder);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            LOGGER.severe("Could not open server folder");
        }

        JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
                "Click Ok when done");

        File serverOutputZip = new File(Globals.getInstance().appStore, "Server.zip");
        FileUtils.zipFolderTo(serverFolder, serverOutputZip);
        FileUtils.purgeDirectory(serverFolder);

        serverSelector.setFile(serverOutputZip);
    }

    public void setPack (ModPack pack) {
        currentPack = pack;
        packName.setText(pack.name);
        selector.clearSelection();
        serverCreator.setEnabled(false);
        iconSelector.setFile(pack.icon);
        splashSelector.setFile(pack.splash);
        serverSelector.setFile(pack.server);
        versionSelector.removeAllItems();
        for (ModPackVersion version : pack.metaVersions) {
            versionSelector.addItem(version.version);
        }
    }

    public boolean fileChanged (FileSelector fileSelector) {
        File tempLocation = new File(Globals.getInstance().preferences.exportFolder +
                File.separator + "temp" + File.separator + fileSelector.getFile().getName());
        if (fileSelector.getFile().equals(tempLocation)) {
            return false;
        }
        if (Globals.getInstance().preferences.copyImportAssets) {
            FileUtils.copyFile(fileSelector.getFile(), tempLocation);
        } else {
            FileUtils.moveFile(fileSelector.getFile(), tempLocation);
        }
        fileSelector.setFile(tempLocation);
        return true;
    }

    public void extractPack (File file) {
        LOGGER.info("Extracting pack");
        if (!file.exists()) {
            System.out.println("Can't extract pack, file doesn't exist!");
            return;
        }

        int i = file.getName().lastIndexOf('.');
        String ext = "file";
        if (i >= 0) {
            ext = file.getName().substring(i + 1);
        }
        if (!ext.equals("zip")) {
            System.out.println("Can't extract pack, file isn't a zip");
            return;
        }

        if (FileUtils.purgeDirectory(Globals.getInstance().preferences.workingFolder)) {
            JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
                    "Failed to clear working folder, aborting pack import");
            LOGGER.warning("Failed to clear working folder, aborting pack import");
            selector.clearSelection();
            return;
        }
        boolean temp = FileUtils.extractZipTo(file, Globals.getInstance().preferences.workingFolder);
        if (temp) {
            File working = Globals.getInstance().preferences.workingFolder;

            FileFilter pngFilter = new FileFilter() {
                @Override
                public boolean accept (File pathname) {
                    if (pathname.isDirectory()) {
                        return false;
                    }
                    String ext = FileUtils.getFileExtension(pathname);
                    if (ext == null) {
                        return false;
                    }
                    return ext.equals("png");
                }
            };

            FileFilter dirFilter = new FileFilter() {
                @Override
                public boolean accept (File pathname) {
                    return pathname.isDirectory();
                }
            };

            boolean icon = false;
            boolean splash = false;
            ArrayList<File> extraFiles = new ArrayList<>();
            for (File image : working.listFiles(pngFilter)) {
                String name = image.getName().toLowerCase();
                if ((name.contains("icon") || name.contains("small")) && !icon) {
                    icon = true;
                    System.out.println("Found an icon");
                    iconSelector.setFile(image);
                } else if ((name.contains("splash") || name.contains("big") || name.contains("banner")) && !splash) {
                    splash = true;
                    System.out.println("Found a splash");
                    splashSelector.setFile(image);
                } else {
                    extraFiles.add(image);
                }
            }
            if (!(icon && splash)) {
                for (File dir : working.listFiles(dirFilter)) {
                    for (File image : dir.listFiles(pngFilter)) {
                        String name = image.getName().toLowerCase();
                        if ((name.contains("icon") || name.contains("small")) && !icon) {
                            icon = true;
                            System.out.println("Found an icon");
                            iconSelector.setFile(image);
                        } else if ((name.contains("splash") || name.contains("big") || name.contains("banner")) && !splash) {
                            splash = true;
                            System.out.println("Found a splash");
                            splashSelector.setFile(image);
                        }
                    }
                }
            }

            if (!(icon && splash) && extraFiles.size() > 0) {
                Object[] options = { "Yes", "No" };
                int n = JOptionPane.showOptionDialog(Globals.getInstance().mainFrame,
                        "Found " + extraFiles.size() + " extra png files\nShould we move them to your import folder?",
                        "Extra png's",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);
                if (n == 0) {
                    for (File extra : extraFiles) {
                        FileUtils.moveFile(extra,
                                new File(Globals.getInstance().preferences.defaultOpenFolder, extra.getName()), false);
                    }
                }
            }

            if (getMinecraftFolder(working) == null) {
                boolean found = false;
                for (File tempFolder : working.listFiles()) {
                    if (!tempFolder.isDirectory()) {
                        continue;
                    }
                    if (getMinecraftFolder(tempFolder) != null) {
                        System.out.println("Found minecraft folder in subfolder, moving up");
                        FileUtils.moveFile(new File(tempFolder, "minecraft"), new File(working, "minecraft"));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    if (new File(working, "mods").exists()) {
                        System.out.println("Found mods folder in root, moving down");
                        File minecraftFolder = new File(working, "minecraft");
                        minecraftFolder.mkdirs();
                        for (File tempFile : working.listFiles()) {
                            if (!tempFile.getName().equals("minecraft")) {
                                tempFile.renameTo(new File(minecraftFolder, tempFile.getName()));
                            }
                        }
                    } else {
                        for (File tempFolder : working.listFiles()) {
                            if (tempFolder.isDirectory()) {
                                if (new File(tempFolder, "mods").exists()) {
                                    System.out.println("Found mods in non-minecraft subfolder, renaming parent");
                                    FileUtils.moveFile(tempFolder, new File(working, "minecraft"));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (!Globals.getInstance().preferences.copyImportAssets) {
                file.delete();
            }
        }
    }

    public static File getMinecraftFolder (File parentFolder) {
        File minecraftFolder = new File(parentFolder, "minecraft");
        if (minecraftFolder.exists()) {
            return minecraftFolder;
        }

        File minecraftDotFolder = new File(parentFolder, ".minecraft");
        if (minecraftDotFolder.exists()) {
            minecraftDotFolder.renameTo(minecraftFolder);
            return minecraftFolder;
        }
        return null;
    }

    /**
     * This triggers all the actions necessary to export the pack in the working folder
     * Check permissions and create perm file
     *  - Needs pack folder. From globals
     *  - Needs mod permissions. Pass modpack
     *  * Cancel if incorrect permissions
     * Add libs. This can be just the JSON, or the json and libraries folder
     *  - Needs pack folder. From globals
     *  - Needs forge version. Pass modpack
     * Build xml
     *  - Needs export folder. From globals
     *  - Needs modpack. Pass modpack
     * Zip pack
     *  - Needs pack folder. From globals
     *  - Needs export folder. From globals
     *  - Needs version and shortname. Pass modpack
     * Upload pack and zip
     *  - Needs export folder. From globals
     * Trigger pack json save
     */
    public void exportPack () {
        Globals.saveCurrentPack();
        ModPack pack = Globals.getModPack();

        boolean success = true;
        success = FileUtils.addForge(Globals.getInstance().preferences.getWorkingMinecraftFolder(),
                pack.ForgeVersion);
        if (!success) {
            System.out.println("pack.json add failed");
            return;
        }
        if ((pack.server != null && pack.server.exists()) &&
                (pack.serverName == null || pack.serverName.equals(""))) {
            pack.serverName = pack.shortName + "Server.zip";
        }
        ArrayList<ModPack> temp = new ArrayList<>();
        temp.add(pack);
        if (!FileUtils.writeXML(temp, new File(
                Globals.getInstance().preferences.exportFolder + File.separator + "static" +
                        File.separator + Globals.getModPack().key + ".xml"))) {
            System.out.println("xml export failed");
            return;
        }
        File packExportFolder = new File(Globals.getInstance().preferences.exportFolder + File.separator +
                "modpacks" + File.separator + pack.shortName + File.separator +
                versionSelector.getSelectedItem().toString().replaceAll("\\.", "_"));
        if (!FileUtils.zipFolderTo(Globals.getInstance().preferences.workingFolder,
                new File(packExportFolder + File.separator + pack.getZipName()))) {

        }

        if (pack.icon != null && pack.icon.exists()) {
            FileUtils.moveFile(pack.icon, new File(Globals.getInstance().preferences.exportFolder
                    + File.separator + "static" + File.separator +
                    pack.getIconName()));
            pack.icon = null;
            iconSelector.clearSelection();//kinda hacky
        }
        if (pack.splash != null && pack.splash.exists()) {
            FileUtils.moveFile(pack.splash, new File(Globals.getInstance().preferences.exportFolder
                    + File.separator + "static" + File.separator +
                    pack.getSplashName()));
            pack.splash = null;
            splashSelector.clearSelection();//kinda hacky
        }
        if (pack.server != null && pack.server.exists()) {
            FileUtils.moveFile(pack.server, new File(packExportFolder + File.separator +
                    pack.serverName));
            pack.server = null;
            serverSelector.clearSelection();//kinda hacky
        }

        boolean curseIsBlank = pack.curseID == null || pack.curseID.equals("");
        ArrayList<String> curseKeys = loadCurseKeys();

        if (curseKeys == null && !curseIsBlank) {
            JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
                    "Unable to load curse keys file. Not exporting key");
        } else if (!curseIsBlank) { // TODO ProgWML6 check if this needs to be removed
            boolean exists = false;
            int index = 0;
            for (String key : curseKeys) {
                if (key.equals(pack.key)) {
                    exists = true;
                    break;
                }
                index++;
            }

            boolean save = false;
            if (!exists) {
                curseKeys.add(pack.key);
                save = true;
            } else if (curseIsBlank) {
                curseKeys.remove(index);
                save = true;
            }

            if (save) {
                if (!saveCurseKeys(curseKeys, Globals.getInstance().preferences.saveFolder)) {
                    JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
                            "Couldn't save curse keys file, changes won't sync to other tools. Contact Watchful1");
                }

                if (saveCurseKeys(curseKeys, new File(Globals.getInstance().preferences.exportFolder
                        + File.separator + "static"))) {
                    JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
                            "Exported curse keys file. Please upload static folder as soon as possible.\n" +
                                    "Yes, I know this will get annoying");
                } else {
                    JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
                            "Couldn't export curse keys file, pack won't sync to client. Contact Watchful1");
                }
            }
        }

        Globals.modPackChanged(this, false);

        System.out.println("Deleting working folder");
        FileUtils.purgeDirectory(Globals.getInstance().preferences.workingFolder);
        selector.clearSelection();
        serverCreator.setEnabled(false);
    }

    private ArrayList<String> loadCurseKeys () {
        File curseFile = new File(Globals.getInstance().preferences.saveFolder + File.separator + Globals.curseFileName);
        if (curseFile.exists()) {
            try {
                ArrayList<String> tempArray = (ArrayList<String>) FileUtils.readObject(curseFile, new ArrayList<String>());
                return tempArray;
            } catch (Exception e) {
                System.out.println("Couldn't load curse keys file");
                return null;
            }
        } else {
            System.out.println("Curse keys file doesn't exist");
            return new ArrayList<>();
        }
    }

    private boolean saveCurseKeys (ArrayList<String> curseKeys, File exportLocation) {
        File curseFile = new File(exportLocation + "/" + Globals.curseFileName);
        try {
            FileUtils.saveObject(curseKeys, curseFile);
            return true;
        } catch (Exception e) {
            System.out.println("Couldn't save curse keys file");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void stateChanged (ChangeEvent e) {
        if (e.getSource().equals(selector)) {
            if (selector.getFile() == null) {
                return;
            }
            extractPack(selector.getFile());
            if (Arrays.asList(Globals.serverMinecraftVersions).contains(Globals.getModPack().minecraftVersion)) {
                serverCreator.setEnabled(true);
            }
            return;
        }

        if (Globals.getModPack() == null) {
            return;
        }
        if (e.getSource().equals(iconSelector)) {
            if (iconSelector.getFile() == null || fileChanged(iconSelector)) {
                Globals.getModPack().icon = iconSelector.getFile();
            }
        } else if (e.getSource().equals(splashSelector)) {
            if (splashSelector.getFile() == null || fileChanged(splashSelector)) {
                Globals.getModPack().splash = splashSelector.getFile();
            }
        } else if (e.getSource().equals(serverSelector)) {
            if (serverSelector.getFile() == null || fileChanged(serverSelector)) {
                Globals.getModPack().server = serverSelector.getFile();
            }
        }
    }

    @Override
    public void updatePack (ModPack modPack) {
        setPack(modPack);
    }
}
