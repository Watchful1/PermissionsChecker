package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.*;
import gr.watchful.permchecker.utils.FileUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class UpdatePanel extends JPanel implements ChangeListener, UsesPack {
	private LabelField packName;
	private FileSelector selector;
	private FileSelector iconSelector;
    private FileSelector splashSelector;
    private FileSelector squareSelector;
	private FileSelector serverSelector;
	JComboBox<String> versionSelector;
	public PermissionsPanel permPanel;//TODO really should be a better way to do this


    public UpdatePanel() {
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
        squareSelector = new FileSelector("Square", 150, "png", this);
        this.add(squareSelector);
		serverSelector = new FileSelector("Server", -1, "zip", this);
		this.add(serverSelector);

        versionSelector = new JComboBox<>();
		versionSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		versionSelector.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		this.add(versionSelector);

		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportPack();
			}
		});
		this.add(exportButton);
    }

	public void setPack(ModPack pack) {
		packName.setText(pack.name);
		selector.clearSelection();
		iconSelector.setFile(pack.icon);
        splashSelector.setFile(pack.splash);
        squareSelector.setFile(pack.square);
		serverSelector.setFile(pack.server);
		versionSelector.removeAllItems();
		for(ModPackVersion version : pack.metaVersions) {
			versionSelector.addItem(version.version);
		}
		versionSelector.setSelectedItem(pack.recommendedVersion);
	}

	public boolean fileChanged(FileSelector fileSelector) {
		File tempLocation = new File(Globals.getInstance().preferences.exportFolder +
				File.separator + "temp" + File.separator + fileSelector.getFile().getName());
		if(fileSelector.getFile().equals(tempLocation)) return false;
		if(Globals.getInstance().preferences.copyImportAssets) {
			FileUtils.copyFile(fileSelector.getFile(), tempLocation);
		} else {
			FileUtils.moveFile(fileSelector.getFile(), tempLocation);
		}
		fileSelector.setFile(tempLocation);
		return true;
	}

	public void extractPack(File file) {
		if(!file.exists()) {
			System.out.println("Can't extract pack, file doesn't exist!");
			return;
		}

		int i = file.getName().lastIndexOf('.');
		String ext = "file";
		if(i >= 0) {
			ext = file.getName().substring(i+1);
		}
		if(!ext.equals("zip")) {
			System.out.println("Can't extract pack, file isn't a zip");
			return;
		}

		FileUtils.purgeDirectory(Globals.getInstance().preferences.workingFolder);
		boolean temp = FileUtils.extractZipTo(file, Globals.getInstance().preferences.workingFolder);
		if(temp) {
			File working = Globals.getInstance().preferences.workingFolder;

			FileFilter pngFilter = new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if(pathname.isDirectory()) return false;
					String ext = FileUtils.getFileExtension(pathname);
					if (ext == null) return false;
					return ext.equals("png");
				}
			};

			FileFilter dirFilter = new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			};

			boolean icon = false;
			boolean splash = false;
			ArrayList<File> extraFiles = new ArrayList<>();
			for(File image : working.listFiles(pngFilter)) {
				String name = image.getName().toLowerCase();
				if((name.contains("icon") || name.contains("small")) && !icon) {
					icon = true;
					System.out.println("Found an icon");
					iconSelector.setFile(image);
				} else if((name.contains("splash") || name.contains("big") || name.contains("banner")) && !splash) {
					splash = true;
					System.out.println("Found a splash");
					splashSelector.setFile(image);
				} else {
					extraFiles.add(image);
				}
			}
			if(!(icon && splash)) {
				for(File dir : working.listFiles(dirFilter)) {
					for(File image : dir.listFiles(pngFilter)) {
						String name = image.getName().toLowerCase();
						if((name.contains("icon") || name.contains("small")) && !icon) {
							icon = true;
							System.out.println("Found an icon");
							iconSelector.setFile(image);
						} else if((name.contains("splash") || name.contains("big") || name.contains("banner")) && !splash) {
							splash = true;
							System.out.println("Found a splash");
							splashSelector.setFile(image);
						}
					}
				}
			}

			if(!(icon && splash) && extraFiles.size() > 0) {
				Object[] options = {"Yes", "No"};
				int n = JOptionPane.showOptionDialog(Globals.getInstance().mainFrame,
						"Found " + extraFiles.size() + " extra png files\nShould we move them to your import folder?",
						"Extra png's",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[1]);
				if(n == 0) {
					for(File extra : extraFiles) {
						FileUtils.moveFile(extra,
								new File(Globals.getInstance().preferences.defaultOpenFolder, extra.getName()), false);
					}
				}
			}

			if(getMinecraftFolder(working) == null) {
				boolean found = false;
				for(File tempFolder : working.listFiles()) {
					if(!tempFolder.isDirectory()) continue;
					if(getMinecraftFolder(tempFolder) != null) {
						System.out.println("Found minecraft folder in subfolder, moving up");
						FileUtils.moveFile(new File(tempFolder, "minecraft"), new File(working, "minecraft"));
						found = true;
						break;
					}
				}
				if(!found) {
					if(new File(working, "mods").exists()) {
						System.out.println("Found mods folder in root, moving down");
						File minecraftFolder = new File(working, "minecraft");
						minecraftFolder.mkdirs();
						for(File tempFile : working.listFiles()) {
							if(!tempFile.getName().equals("minecraft")) {
								tempFile.renameTo(new File(minecraftFolder, tempFile.getName()));
							}
						}
					} else {
						for(File tempFolder : working.listFiles()) {
							if(tempFolder.isDirectory()) {
								if(new File(tempFolder, "mods").exists()) {
									System.out.println("Found mods in non-minecraft subfolder, renaming parent");
									FileUtils.moveFile(tempFolder, new File(working, "minecraft"));
									break;
								}
							}
						}
					}
				}
			}
			if (!Globals.getInstance().preferences.copyImportAssets) file.delete();
			permPanel.invalidateContents();
		}
	}

	public static File getMinecraftFolder(File parentFolder) {
		File minecraftFolder = new File(parentFolder, "minecraft");
		if(minecraftFolder.exists()) return minecraftFolder;

		File minecraftDotFolder = new File(parentFolder, ".minecraft");
		if(minecraftDotFolder.exists()) {
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
	public void exportPack() {
		Globals.saveCurrentPack();
		permPanel.parsePack();
		if(!permPanel.promptPermissionsGood()) {
			System.out.println("Export canceled");
			return;
		}
		permPanel.writeFile();

		boolean success = true;
		if(Globals.getModPack().forgeType.equals(ForgeType.VERSION)) {
			success = FileUtils.addForge(Globals.getInstance().preferences.getWorkingMinecraftFolder(),
					Globals.getModPack().ForgeVersion);
		} else {
			success = FileUtils.addForge(Globals.getInstance().preferences.getWorkingMinecraftFolder(),
					Globals.getModPack().forgeType, Globals.getModPack().minecraftVersion);
		}
		if(!success) {
			System.out.println("pack.json add failed");
			return;
		}
		if((Globals.getModPack().server != null && Globals.getModPack().server.exists()) &&
				(Globals.getModPack().serverName == null || Globals.getModPack().serverName.equals(""))) {
			Globals.getModPack().serverName = Globals.getModPack().shortName + "Server.zip";
		}
		ArrayList<ModPack> temp = new ArrayList<>();
		temp.add(Globals.getModPack());
		String xml = FileUtils.buildXML(temp);
		if(!FileUtils.writeFile(xml, new File(
				Globals.getInstance().preferences.exportFolder+File.separator+"static"+
				File.separator+Globals.getModPack().key+".xml"), false)) {
			System.out.println("xml export failed");
			return;
		}
		File packExportFolder = new File(Globals.getInstance().preferences.exportFolder + File.separator +
				"privatepacks" + File.separator + Globals.getModPack().shortName + File.separator +
				versionSelector.getSelectedItem().toString().replaceAll("\\.","_"));
		if(!FileUtils.zipFolderTo(Globals.getInstance().preferences.workingFolder,
				new File(packExportFolder + File.separator + Globals.getModPack().getZipName()))) {

		}

		if(Globals.getModPack().icon != null && Globals.getModPack().icon.exists()) {
			FileUtils.moveFile(Globals.getModPack().icon, new File(Globals.getInstance().preferences.exportFolder
					+ File.separator + "static" + File.separator +
					Globals.getModPack().getIconName()));
			Globals.getModPack().icon = null;
			iconSelector.clearSelection();//kinda hacky
		}
        if(Globals.getModPack().splash != null && Globals.getModPack().splash.exists()) {
            FileUtils.moveFile(Globals.getModPack().splash, new File(Globals.getInstance().preferences.exportFolder
                    + File.separator + "static" + File.separator +
                    Globals.getModPack().getSplashName()));
            Globals.getModPack().splash = null;
            splashSelector.clearSelection();//kinda hacky
        }
        if(Globals.getModPack().square != null && Globals.getModPack().square.exists()) {
            FileUtils.moveFile(Globals.getModPack().square, new File(Globals.getInstance().preferences.exportFolder
                    + File.separator + "static" + File.separator +
                    Globals.getModPack().getSquareName()));
            Globals.getModPack().square = null;
            squareSelector.clearSelection();//kinda hacky
        }
		if(Globals.getModPack().server != null && Globals.getModPack().server.exists()) {
			FileUtils.moveFile(Globals.getModPack().server, new File(packExportFolder + File.separator +
					Globals.getModPack().serverName));
			Globals.getModPack().server = null;
			serverSelector.clearSelection();//kinda hacky
		}

        boolean curseIsBlank = Globals.getModPack().curseID != null || !Globals.getModPack().curseID.equals("");
        ArrayList<String> curseKeys = loadCurseKeys();

        if(curseKeys == null && !curseIsBlank) {
            JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
                    "Unable to load curse keys file. Not exporting key");
        } else if(Globals.getModPack().curseID != null && !Globals.getModPack().curseID.equals("")) {
            boolean exists = false;
            int index = 0;
            for(String key : curseKeys) {
                if(key.equals(Globals.getModPack().key)) {
                    exists = true;
                    break;
                }
                index++;
            }

            boolean save = false;
            if(!exists) {
                curseKeys.add(Globals.getModPack().key);
                save = true;
            } else if(curseIsBlank) {
                curseKeys.remove(index);
                save = true;
            }

            if(save) {
                if(!saveCurseKeys(curseKeys, Globals.getInstance().preferences.saveFolder)) {
                    JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
                            "Couldn't save curse keys file, changes won't sync to other tools. Contact Watchful1");
                }

                if(saveCurseKeys(curseKeys, new File(Globals.getInstance().preferences.exportFolder
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
	}

    private ArrayList<String> loadCurseKeys() {
        File curseFile = new File(Globals.getInstance().preferences.saveFolder+File.separator+Globals.curseFileName);
        if(curseFile.exists()) {
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

    private boolean saveCurseKeys(ArrayList<String> curseKeys, File exportLocation) {
        File curseFile = new File(exportLocation+"/"+Globals.curseFileName);
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
	public void stateChanged(ChangeEvent e) {
		if(e.getSource().equals(selector)) {
			if(selector.getFile() == null) return;
			extractPack(selector.getFile());
			return;
		}

		if(Globals.getModPack() == null) return;
		if(e.getSource().equals(iconSelector)) {
			if(iconSelector.getFile() == null || fileChanged(iconSelector)) {
				Globals.getModPack().icon = iconSelector.getFile();
			}
        } else if(e.getSource().equals(splashSelector)) {
            if(splashSelector.getFile() == null || fileChanged(splashSelector)) {
                Globals.getModPack().splash = splashSelector.getFile();
            }
        } else if(e.getSource().equals(squareSelector)) {
            if(squareSelector.getFile() == null || fileChanged(squareSelector)) {
                Globals.getModPack().square = squareSelector.getFile();
            }
		} else if(e.getSource().equals(serverSelector)) {
			if(serverSelector.getFile() == null || fileChanged(serverSelector)) {
				Globals.getModPack().server = serverSelector.getFile();
			}
		}
	}

	@Override
	public void updatePack(ModPack modPack) {
		setPack(modPack);
	}
}
