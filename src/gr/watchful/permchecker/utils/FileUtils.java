package gr.watchful.permchecker.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {
	public static void copyFolder(File sourceFolder, File destinationFolder) {
		copyFolder(sourceFolder, destinationFolder, true);
	}

	public static void copyFolder(File sourceFolder, File destinationFolder,
			boolean overwrite) {
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

	public static void copyFile(File sourceFile, File destinationFile,
			boolean overwrite) {
		try {
			if (sourceFile.exists()) {
				if (!overwrite && destinationFile.exists()) {
					System.out.println("Destination file exists\n"
							+ sourceFile.getAbsolutePath());
					return;
				}
				if (!destinationFile.exists()) {
					destinationFile.createNewFile();
				}
				FileChannel sourceStream = null, destinationStream = null;
				try {
					sourceStream = new FileInputStream(sourceFile).getChannel();
					destinationStream = new FileOutputStream(destinationFile)
							.getChannel();
					destinationStream.transferFrom(sourceStream, 0,
							sourceStream.size());
				} finally {
					if (sourceStream != null) {
						sourceStream.close();
					}
					if (destinationStream != null) {
						destinationStream.close();
					}
				}
			} else {
				System.out.println("Source file does not exist\n"
						+ sourceFile.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public static void extractZipTo(File zipLocation, File outputLocation) {
		ZipInputStream zipInputStream = null;
		try {
			byte[] buf = new byte[1024];
			zipInputStream = new ZipInputStream(
					new FileInputStream(zipLocation));
			ZipEntry zipentry = zipInputStream.getNextEntry();
			while (zipentry != null) {
				String entryName = zipentry.getName();
				System.out.println(entryName);
				int n;
				if (!zipentry.isDirectory()
						&& !entryName.equalsIgnoreCase("minecraft")
						&& !entryName.equalsIgnoreCase(".minecraft")
						&& !entryName.equalsIgnoreCase("instMods")) {
					new File(outputLocation.getAbsolutePath() + File.separator
							+ entryName).getParentFile().mkdirs();
					FileOutputStream fileoutputstream = new FileOutputStream(
							outputLocation.getAbsolutePath() + File.separator
									+ entryName);
					while ((n = zipInputStream.read(buf, 0, 1024)) > -1) {
						fileoutputstream.write(buf, 0, n);
					}
					fileoutputstream.close();
				}
				zipInputStream.closeEntry();
				zipentry = zipInputStream.getNextEntry();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("ping");
		} finally {
			try {
				zipInputStream.close();
			} catch (IOException e) {
			}
		}
		System.out.println("done");
	}

	public static void zipFilesTo(File[] files, File outputLocation) {
		ZipOutputStream zipOutputStream = null;
		try {
			zipOutputStream = new ZipOutputStream(new FileOutputStream(
					outputLocation));
			addDirectoryOrFile(zipOutputStream, files, 0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				zipOutputStream.close();
			} catch (IOException e) {
			}
		}
		System.out.println("done");
	}

	private static void addDirectoryOrFile(ZipOutputStream zipOutputStream,
			File[] files, int depth) throws IOException {
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				System.out.println("Adding directory " + files[i].getName());
				addDirectoryOrFile(zipOutputStream, files[i].listFiles(),
						depth + 1);
				continue;
			}

			byte[] buffer = new byte[1024];

			String fileName = "";
			File temp = files[i];
			for (int j = 0; j < depth; j++) {
				for (int k = depth - j; k > 0; k--) {
					temp = temp.getParentFile();
				}
				fileName = fileName + temp.getName() + "/";
				temp = files[i];
			}
			fileName = fileName + files[i].getName();
			System.out.println("Adding file " + fileName);

			FileInputStream fileInputStream = new FileInputStream(files[i]);
			zipOutputStream.putNextEntry(new ZipEntry(fileName));

			int length = buffer.length;
			while ((length = fileInputStream.read(buffer)) > 0) {
				zipOutputStream.write(buffer, 0, length);
			}

			zipOutputStream.closeEntry();
			fileInputStream.close();
		}
	}

	public static void writeFile(String string, File location) {
		if (!location.exists())
			location.getParentFile().mkdirs();
		try {
			// Create file
			FileWriter fstream = new FileWriter(location);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(string);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static String readFile(File location) {
		if (!location.exists())
			return null;
		BufferedReader br = null;
		StringBuilder bldr = new StringBuilder();
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(location));
			while ((sCurrentLine = br.readLine()) != null) {
				bldr.append(sCurrentLine + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
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

	public static void saveObject(Object object, File file) {
		writeFile(JSONUtils.getJSON(object), file);
	}

	public static Object readObject(File file, Object object) {
		return JSONUtils.getObject(readFile(file), object);
	}
}