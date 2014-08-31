package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.ForgeType;
import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModPack;
import gr.watchful.permchecker.datastructures.UsesPack;
import gr.watchful.permchecker.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class UpdatePanel extends JPanel implements ActionListener, UsesPack {
	private LabelField packName;
	private FileSelector selector;
	JComboBox<String> versionSelector;
	public PermissionsPanel permPanel;//TODO really should be a better way to do this


    public UpdatePanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        packName = new LabelField("Pack Name");
        packName.lock("Currently opened pack");
        this.add(packName);

        selector = new FileSelector("Zip", -1, "zip");
		selector.addListener(this);
        this.add(selector);

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
		versionSelector.removeAllItems();
		for(String version : pack.versions) {
			versionSelector.addItem(version);
		}
		versionSelector.setSelectedItem(pack.recommendedVersion);
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
		if(temp && !Globals.getInstance().preferences.copyImportAssets) file.delete();
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
		permPanel.parsePack();
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
		String xml = FileUtils.buildXML(Globals.getModPack());
		if(!FileUtils.writeFile(xml, new File(
				Globals.getInstance().preferences.exportFolder+File.separator+"static"+
				File.separator+Globals.getModPack().key+".xml"), true)) {
			System.out.println("xml export failed");
			return;
		}
		File packExportFolder = new File(Globals.getInstance().preferences.exportFolder + File.separator +
				"privatepacks" + File.separator + Globals.getModPack().shortName + File.separator +
				versionSelector.getSelectedItem().toString().replaceAll("\\.","_"));
		FileUtils.zipFolderTo(Globals.getInstance().preferences.workingFolder,
				new File(packExportFolder + File.separator + Globals.getModPack().getZipName()));

		if(Globals.getModPack().icon != null && Globals.getModPack().icon.exists()) {
			FileUtils.moveFile(Globals.getModPack().icon, new File(Globals.getInstance().preferences.exportFolder
					+ File.separator + "static" + File.separator +
					Globals.getModPack().getIconName()));
		}
		if(Globals.getModPack().splash != null && Globals.getModPack().splash.exists()) {
			FileUtils.moveFile(Globals.getModPack().splash, new File(Globals.getInstance().preferences.exportFolder
					+ File.separator + "static" + File.separator +
					Globals.getModPack().getSplashName()));
		}
		if(Globals.getModPack().server == null) System.out.println("Server is null in pack");
		if(Globals.getModPack().server != null && Globals.getModPack().server.exists()) {
			FileUtils.moveFile(Globals.getModPack().server, new File(packExportFolder + File.separator +
					Globals.getModPack().serverName));
		}

		System.out.println("Deleting working folder");
		for(File file : Globals.getInstance().preferences.workingFolder.listFiles()) {
			FileUtils.delete(file);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		extractPack(selector.getFile());
	}

	@Override
	public void updatePack(ModPack modPack) {
		setPack(modPack);
	}
}
