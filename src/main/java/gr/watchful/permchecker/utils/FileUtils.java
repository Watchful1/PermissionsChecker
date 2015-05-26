package gr.watchful.permchecker.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import gr.watchful.permchecker.datastructures.ForgeType;
import gr.watchful.permchecker.datastructures.Globals;
import gr.watchful.permchecker.datastructures.ModPack;
import gr.watchful.permchecker.datastructures.ModPackVersion;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class FileUtils {
	public static void copyFolder(File sourceFolder, File destinationFolder) {
		copyFolder(sourceFolder, destinationFolder, true);
	}

	public static void copyFolder(File sourceFolder, File destinationFolder, boolean overwrite) {
		if (sourceFolder.isDirectory()) {
			if (!destinationFolder.exists()) {
				destinationFolder.mkdirs();
			}
			String files[] = sourceFolder.list();
			for (String file : files) {
				File srcFile = new File(sourceFolder, file);
				File destFile = new File(destinationFolder, file);
				copyFolder(srcFile, destFile);
			}
		} else {
			copyFile(sourceFolder, destinationFolder, overwrite);
		}
	}
	
	public static void copyFile(File sourceFile, File destinationFile) {
		copyFile(sourceFile, destinationFile, true);
	}

	public static void copyFile(File sourceFile, File destinationFile, boolean overwrite) {
		try {
			if (sourceFile.exists()) {
				if(!overwrite && destinationFile.exists()) {
					System.out.println("Destination file exists\n"+sourceFile.getAbsolutePath());
					return;
				}
				if(!destinationFile.exists()) {
					destinationFile.createNewFile();
				}
				FileChannel sourceStream = null, destinationStream = null;
				try {
					sourceStream = new FileInputStream(sourceFile).getChannel();
					destinationStream = new FileOutputStream(destinationFile).getChannel();
					destinationStream.transferFrom(sourceStream, 0, sourceStream.size());
				} finally {
					if(sourceStream != null) {
						sourceStream.close();
					}
					if(destinationStream != null) {
						destinationStream.close();
					}
				}
			} else {
				System.out.println("Source file does not exist\n"+sourceFile.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void moveFile(File sourceFile, File destinationFile) {
		moveFile(sourceFile, destinationFile, true);
	}

	public static void moveFile(File sourceFile, File destinationFile, boolean overwrite) {
		if(!sourceFile.exists()) return;
		if(destinationFile.exists() && !overwrite) {
			System.out.println("Destination file exists, aborting");
			return;
		}
		System.out.println("Moving file from: "+sourceFile.getAbsolutePath());
		System.out.println("To: "+destinationFile.getAbsolutePath());
		if(destinationFile.exists()) destinationFile.delete();
		else destinationFile.getParentFile().mkdirs();
		if(!sourceFile.renameTo(destinationFile)) System.out.println("Move failed!");
	}

	public static boolean delete(File resource) {
		if (resource.isDirectory()) {
			File[] childFiles = resource.listFiles();
			for (File child : childFiles) {
				delete(child);
			}
		}
		return resource.delete();
	}

	public static boolean extractZipTo(File zipLocation, File outputLocation) {
		try {
			ZipFile zipFile = new ZipFile(zipLocation);
			zipFile.extractAll(outputLocation.getPath());
			System.out.println("Unzip done");
			return true;
		} catch (Exception e) {
			System.out.println("Unzip failed");
			//e.printStackTrace();

			Object[] options = {"Yes", "No"};
			int n = JOptionPane.showOptionDialog(Globals.getInstance().mainFrame,
					"Unzip failed.\nWould you like to manually unzip?",
					"Unzip failed",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[1]);
			if(n == 0) {
				FileUtils.purgeDirectory(outputLocation);
				try {
					Desktop.getDesktop().open(zipLocation);
					Desktop.getDesktop().open(outputLocation);
					Object[] options2 = {"Done", "Cancel"};
					int n2 = JOptionPane.showOptionDialog(Globals.getInstance().mainFrame,
							"Done with manual unzip?",
							"Waiting",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options2,
							options2[0]);
					if(n2 == 0) return true;
					else return false;
				} catch (IOException e1) {
					System.out.println("Explorer open failed");
					e1.printStackTrace();
				}
			}
			return false;
		}
	}
	
	public static boolean zipFolderTo(File folder, File outputLocation) {
		FileOutputStream fOut = null;
		BufferedOutputStream bOut = null;
		ZipArchiveOutputStream tOut = null;
		try {
			outputLocation.mkdirs();
			outputLocation.delete();

			fOut = new FileOutputStream(outputLocation);
			bOut = new BufferedOutputStream(fOut);
			tOut = new ZipArchiveOutputStream(bOut);

            String[] patterns = {".*\\.DS_Store"};

			for(File file : folder.listFiles()) {
                for(String pattern : patterns) {
                    if(file.getName().matches(pattern)) continue;
                }
				addFileToZip(tOut, file, "");
			}

			/*

			ZipFile zipFile = new ZipFile(outputLocation);

			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			parameters.setIncludeRootFolder(false);

			zipFile.addFolder(folder.getPath(), parameters);*/
		} catch (Exception e) {
			System.out.println("Zip failed");
			e.printStackTrace();

			Object[] options = {"Yes", "No"};
			int n = JOptionPane.showOptionDialog(Globals.getInstance().mainFrame,
					"Zip failed.\nWould you like to manually zip?",
					"Zip failed",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[1]);
			if(n == 0) {
				try {
					Desktop.getDesktop().open(folder);
					JFileChooser fileChooser = new JFileChooser(folder);
					fileChooser.setFileFilter(new FileNameExtensionFilter("zip files", "zip"));
					int returnVal = fileChooser.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File result = fileChooser.getSelectedFile();
						FileUtils.moveFile(result, outputLocation);
						return true;
					}
				} catch (IOException e1) {
					System.out.println("Explorer open failed");
				}
			}
			return false;
		} finally {
			try {
				tOut.finish();
				tOut.close();
				bOut.close();
				fOut.close();
			} catch (IOException e) {
				System.out.println("Closing of resources in zip method failed");
			}
			System.out.println("Zip done");
			return true;
		}
	}

	private static void addFileToZip(ZipArchiveOutputStream zOut, File file, String base) throws IOException {
		String entryName = base + file.getName();
		ZipArchiveEntry zipEntry = new ZipArchiveEntry(file, entryName);
		zOut.putArchiveEntry(zipEntry);
		if (file.isFile()) {
			FileInputStream fInputStream = null;
			try {
				fInputStream = new FileInputStream(file);
				IOUtils.copy(fInputStream, zOut);
				zOut.closeArchiveEntry();
			} finally {
				fInputStream.close();
			}

		} else {
			zOut.closeArchiveEntry();
			File[] children = file.listFiles();
			if (children != null) {
				for (File child : children) {
					addFileToZip(zOut, child, entryName + "/");
				}
			}
		}
	}

	public static boolean writeFile(String string, File location) {
		return writeFile(string, location, false);
	}
	
	public static boolean writeFile(String string, File location, boolean forceASCII) {
		if(!location.exists()) location.getParentFile().mkdirs();
		try{
			if (location.exists()) location.delete();
			location.createNewFile();

			if(!forceASCII) {
				FileWriter fstream = new FileWriter(location);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(string);
				out.close();
			} else {
				Writer out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(location), "ASCII"));
				try {
					out.write(string);
				} catch (Exception e) {
					System.err.println("Error: " + e.getMessage());
				} finally {
					out.close();
				}
			}
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	public static String readFile(File location) {
		if(!location.exists()) return null;
		BufferedReader br = null;
		StringBuilder bldr = new StringBuilder();
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(location));
			while ((sCurrentLine = br.readLine()) != null) {
				bldr.append(sCurrentLine+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return bldr.toString();
	}
	
	public static void downloadToFile(URL url, File file) throws IOException {
        file.getParentFile().mkdirs();
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, 1 << 24);
        fos.close();
    }

	public static String downloadToString(String url)  {
		URL website = null;
		try {
			website = new URL(url);
		} catch (MalformedURLException e) {
			System.out.println("Couldn't connect to "+url);
			return null;
		}
		URLConnection connection = null;
		try {
			connection = website.openConnection();
		} catch (IOException e) {
			System.out.println("Couldn't connect to " + url);
			return null;
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
		} catch (IOException e) {
			System.out.println("Couldn't read " + url);
			return null;
		}

		StringBuilder response = new StringBuilder();
		String inputLine;

		try {
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
		} catch (IOException e) {
			System.out.println("Trouble reading " + url);
			return null;
		}

		try {
			in.close();
		} catch (IOException e) {
			System.out.println("Couldn't close connection to " + url);
		}

		return response.toString();
	}
	
	public static String getJSON(Object object) {
		Gson gson = new Gson();
		return gson.toJson(object);
	}
	
	public static Object getObject(String JSON, Object object) {
		Gson gson = new Gson();
		Object tempObject;
		try {
			tempObject = gson.fromJson(JSON, object.getClass());
		} catch (JsonSyntaxException excp) {
			System.out.println("returning null");
			return null;
		}
		return tempObject;
	}
	
	public static void saveObject(Object object, File file) {
		writeFile(getJSON(object), file);
	}
	
	public static Object readObject(File file, Object object) {
		return getObject(readFile(file), object);
	}

	public static boolean addForge(File minecraftFolder, ForgeType forgeType, String mcVersion) {
		return addForge(minecraftFolder, 0, forgeType, mcVersion);
	}

	public static boolean addForge(File minecraftFolder, int forgeVersion) {
		return addForge(minecraftFolder, forgeVersion, ForgeType.VERSION, "");
	}

	private static boolean addForge(File minecraftFolder, int forgeVersion, ForgeType forgeType, String mcVersion) {
		String forgeUrl = Globals.forgeUrl;
		if(forgeType.equals(ForgeType.RECOMMENDED) || forgeType.equals(ForgeType.LATEST)) {
			if (mcVersion != null && !mcVersion.equals("")) forgeUrl = forgeUrl.concat(mcVersion).concat("-");
			if (forgeType.equals(ForgeType.RECOMMENDED)) forgeUrl = forgeUrl.concat("recommended");
			else forgeUrl = forgeUrl.concat("latest");
		} else forgeUrl = forgeUrl.concat(Integer.toString(forgeVersion));
		try {
			System.out.println("URL: "+forgeUrl);
			downloadToFile(new URL(forgeUrl), new File(minecraftFolder+File.separator+"pack.json"));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(Globals.getInstance().mainFrame,
					"Pack.json add failed");
			return false;
		}
		return true;
	}

	public static String buildXML(ArrayList<ModPack> modPacks) {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Element rootElement = doc.createElement("modpacks");
		doc.appendChild(rootElement);

		for(ModPack modPack : modPacks) {
			Element modpack = doc.createElement("modpack");

			modpack.setAttribute("name", modPack.name);
			modpack.setAttribute("author", modPack.author);
			modpack.setAttribute("version", modPack.recommendedVersion);
			modpack.setAttribute("repoVersion", modPack.recommendedVersion.replace(".", "_"));
			modpack.setAttribute("logo", modPack.getIconName());
			modpack.setAttribute("url", modPack.getZipName());
            modpack.setAttribute("image", modPack.getSplashName());
			modpack.setAttribute("dir", modPack.shortName);
			modpack.setAttribute("mcVersion", modPack.minecraftVersion);
			if (modPack.serverName != null && !modPack.serverName.equals("")) {
				modpack.setAttribute("serverPack", modPack.serverName);
			} else {
				modpack.setAttribute("serverPack", "");
			}
            if (modPack.square != null && !modPack.squareName.equals("")) {
                modpack.setAttribute("squareImage", modPack.squareName);
            }
			modpack.setAttribute("description", modPack.description);
			modpack.setAttribute("mods", modPack.getModList());

			if (modPack.metaVersions.size() == 0) modpack.setAttribute("oldVersions", "");
			else {
				StringBuilder versionListBldr = new StringBuilder();
				StringBuilder mcVersionListBldr = new StringBuilder();
				boolean firstMc = true;

				for (int i = 0; i < modPack.metaVersions.size(); i++) {
					ModPackVersion temp = modPack.metaVersions.get(i);

					if (i != 0) versionListBldr.append(";");
					versionListBldr.append(temp.version);

					if (temp.mcVersion != null && !temp.mcVersion.equals("")) {
						if (firstMc) firstMc = false;
						else mcVersionListBldr.append(";");

						mcVersionListBldr.append(temp.version + "^" + temp.mcVersion);
					}
				}
				modpack.setAttribute("oldVersions", versionListBldr.toString());

				if (!firstMc) modpack.setAttribute("customMCVersions", mcVersionListBldr.toString());
			}

			if (modPack.animation != null && !modPack.animation.equals("")) {
				modpack.setAttribute("animation", modPack.animation);
			}
			if (modPack.warning != null && !modPack.warning.equals("")) {
				modpack.setAttribute("warning", modPack.warning);
			}

			rootElement.appendChild(modpack);
		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			return writer.getBuffer().toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static ArrayList<ModPack> readXML(String string) {
		DocumentBuilder dBuilder;
		Document doc = null;
		try {
			dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(string)));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.out.println("Couldn't parse xml");
			return null;
		}

		doc.getDocumentElement().normalize();

		ArrayList<ModPack> packs = new ArrayList<>();
		for(int i=0; i<doc.getElementsByTagName("modpack").getLength(); i++) {
			Node nNode = doc.getElementsByTagName("modpack").item(0);

			ModPack pack = null;

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				pack = new ModPack();
				pack.name = eElement.getAttribute("name");
				pack.author = eElement.getAttribute("author");
				pack.recommendedVersion = eElement.getAttribute("version");
				pack.minecraftVersion = eElement.getAttribute("mcVersion");
				pack.description = eElement.getAttribute("description");
				pack.iconName = eElement.getAttribute("logo");
                pack.splashName = eElement.getAttribute("image");
				pack.zipName = eElement.getAttribute("url");
				pack.shortName = eElement.getAttribute("dir");
				if (eElement.hasAttribute("warning")) pack.warning = eElement.getAttribute("warning");
				else pack.warning = null;

                if (eElement.hasAttribute("squareImage")) pack.squareName = eElement.getAttribute("squareImage");
                else pack.squareName = null;

				if (eElement.hasAttribute("animation")) pack.animation = eElement.getAttribute("animation");
				else pack.animation = null;

				if (eElement.getAttribute("server").equals("")) pack.serverName = "";
				else pack.serverName = eElement.getAttribute("server");


				HashMap<String, ModPackVersion> versionMap = new HashMap<>();
				ArrayList<ModPackVersion> versions = new ArrayList<>();

				String[] oldVersions = eElement.getAttribute("oldVersions").split(";");
				for (String version : oldVersions) {
					ModPackVersion temp = new ModPackVersion(version);
					versionMap.put(version, temp);
					versions.add(temp);
				}

				if (eElement.hasAttribute("customMCVersions")) {
					String[] customMCVersions = eElement.getAttribute("customMCVersions").split(";");
					for (String version : customMCVersions) {
						ModPackVersion temp = versionMap.get(version.split("\\^")[0]);
						if (temp != null) temp.mcVersion = version.split("\\^")[1];
					}
				}
				pack.metaVersions = versions;
			}
			packs.add(pack);
		}

		return packs;
	}

	public static ArrayList<ModPack> packFromCode(String code) {
		if(code == null || code.length() <= 0) return null;

		String xml = FileUtils.downloadToString(Globals.ftbRepoUrl+"static/"+code+".xml");
		if(xml == null || xml.length() <= 0) return null;

		return FileUtils.readXML(xml);
	}

	public static void purgeDirectory(File dir) {
		if(!dir.exists()) return;
		for (File file: dir.listFiles()) {
			if (file.isDirectory()) purgeDirectory(file);
			file.delete();
		}
	}

	public static boolean remoteFileExists(String url)  {
		try {
			HttpURLConnection.setFollowRedirects(false);
			// note : you may also need
			//        HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con =
					(HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getFileExtension(File file) {
		if(file == null) return null;
		int extPos = file.getAbsolutePath().lastIndexOf(".");
		int sepPos = file.getAbsolutePath().lastIndexOf(File.pathSeparator);
		if(extPos == -1) return null;
		if(sepPos > extPos) return null;
		return file.getAbsolutePath().substring(extPos+1);
	}

	public static String getMD5(File file) {
		try (FileInputStream inputStream = new FileInputStream(file)) {
			MessageDigest digest = MessageDigest.getInstance("MD5");

			byte[] bytesBuffer = new byte[1024];
			int bytesRead = -1;

			while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
				digest.update(bytesBuffer, 0, bytesRead);
			}

			byte[] hashedBytes = digest.digest();

			return convertByteArrayToHexString(hashedBytes);
		} catch (NoSuchAlgorithmException | IOException ex) {
			System.out.println("Couldn't generate hash");
			return null;
		}
	}

	private static String convertByteArrayToHexString(byte[] arrayBytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < arrayBytes.length; i++) {
			stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return stringBuffer.toString();
	}
}