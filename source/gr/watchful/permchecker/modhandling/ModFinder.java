package gr.watchful.permchecker.modhandling;

import com.google.gson.JsonObject;
import gr.watchful.permchecker.datastructures.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import gr.watchful.permchecker.utils.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.swing.*;

public class ModFinder {
	// We need a central place to add ID's for when we can't return what we want
	private ModFile otherMod;
	private int rawClasses;

	public ArrayList<ModFile> discoverModFiles(File folder) {
		return discoverModFiles(folder, false);
	}

	public ArrayList<ModFile> discoverModFiles(File folder, boolean silent) {
		rawClasses = 0;
		ArrayList<ModFile> modFiles = new ArrayList<>();

		if(folder == null || !folder.exists()) return modFiles;

		ModFile temp;
		for(File file : folder.listFiles()) {
			if(file.isDirectory())  modFiles.addAll(discoverModFiles(file, true));
			else {
				int i = file.getName().lastIndexOf('.');
				if (i <= 0) continue;
				String extension = file.getName().substring(i+1);
				boolean good = false;
				for(String type : Globals.modTypes) {
					if(type.equals(extension)) good = true;
				}
				if(!good) continue;
				try {
					temp = processFile(file);
					if (temp != null) modFiles.add(temp);
				} catch (IOException | ClassNotFoundException e) {
					modFiles.add(new ModFile(file));
				}
			}
		}
		if(rawClasses > 0 && !silent) JOptionPane.showMessageDialog(Globals.getInstance().mainFrame, "Raw class files are present in the mods folder\n"+
				"This is not supported by this tool and may cause problems in the FTB launcher");
		return modFiles;
	}

	public ModFile processFile(File modArchive) throws IOException, ClassNotFoundException {
		otherMod = new ModFile(modArchive);

		String ext = FileUtils.getFileExtension(modArchive);

		if(ext.equals("jar") || ext.equals("zip") || ext.equals("litemod") || ext.equals("disabled")) {
			ZipFile file;
			try {
				file = new ZipFile(modArchive);
			} catch (Exception e) {
				return null;
			}
			Enumeration<? extends ZipEntry> files = file.entries();
			boolean hasClassFiles = false;
			while(files.hasMoreElements()) {
				ZipEntry item = files.nextElement();
				if(item.getName().equals("mcmod.info")) {
					otherMod.mcmod = MetadataCollection.from(file.getInputStream(item), file.getName());
				}
				if(item.getName().equals("litemod.json")) {
					BufferedReader streamReader = new BufferedReader(new InputStreamReader(file.getInputStream(item), "UTF-8"));
					StringBuilder responseStrBuilder = new StringBuilder();

					String inputStr;
					while ((inputStr = streamReader.readLine()) != null)
						responseStrBuilder.append(inputStr);
					JSONObject litemodJSON;
					try {
						litemodJSON = new JSONObject(responseStrBuilder.toString());
						String name = (String) litemodJSON.get("name");
						otherMod.addID(name);
					} catch (JSONException e) {
						e.printStackTrace();
						continue;
					}
				}
				if(item.getName().equals("META-INF/MANIFEST.MF")) {
					BufferedReader streamReader = new BufferedReader(new InputStreamReader(file.getInputStream(item), "UTF-8"));

					String inputStr;
					while ((inputStr = streamReader.readLine()) != null) {
						if(inputStr.startsWith("TweakClass:")) {
							otherMod.addID(inputStr.split(" ")[1]);
						}
					}
				}
				if(item.isDirectory() || !item.getName().endsWith("class")) continue;
				hasClassFiles = true;

				ClassReader reader;
				try {
					reader = new ClassReader(file.getInputStream(item));
				} catch (Exception e) {
					continue;
				}
				try {
					reader.accept(new ModClassVisitor(), 0);
				} catch (Exception e) {
					continue;
				}
			}
			file.close();
			if(hasClassFiles) return otherMod;
			else return null;
		} else if(ext.equals("class")) {
			rawClasses++;
		}
		return null;
	}

	public class ModClassVisitor extends ClassVisitor {
		boolean tmp;

		public ModClassVisitor() {
			super(Opcodes.ASM4);
			tmp = false;
		}

		@Override
		public void visit(int version, int access, String name, String signature,
						  String superName, String[] interfaces) {
			if(superName.equals("BaseMod")) {
				//TODO modloader mod, add the class name
				otherMod.addID(name);
			} else if(superName.equals("DummyModContainer") || superName.equals("cpw/mods/fml/common/DummyModContainer")) {
				//TODO this is a forge coremod, launch a method visitor to try to find the name
				tmp = true;
				otherMod.addID(name);
			}
		}

		@Override
		public AnnotationVisitor visitAnnotation(String name, boolean runtime) {
			if (name.equals("Lcpw/mods/fml/common/Mod;")) {
				return new ModAnnotationVisitor();
			} else {
				return new AnnotationVisitor(Opcodes.ASM4) {};
			}
		}

		@Override
		public  MethodVisitor visitMethod(int access, String name, String desc,
										  String signature, String[] exceptions) {
			if (tmp) {
				//System.out.println("Visiting "+name);
				return new ModMethodVisitor();
			}
			//System.out.println("    "+name);
			return null;
		}
	}

	public class ModAnnotationVisitor extends AnnotationVisitor {
		String name;
		String id;
		String version;

		public ModAnnotationVisitor() {
			super(Opcodes.ASM4);
			name = "";
			id = "";
			version = "";
		}

		@Override
		public void visit(String key, Object value) {
			//TODO forgemod, in annotation mod
			if(key.equals("modid")) {
				id = value.toString();
			} else if(key.equals("name")) {
				name = value.toString();
			}else if(key.equals("version")) {
				version = value.toString();
			}
		}

		@Override
		public void visitEnum(String name, String desc, String value) {

		}

		@Override
		public void visitEnd() {
			otherMod.addID(id);
			otherMod.addName(name);
			otherMod.addVersion(version);
		}
	}

	public class ModMethodVisitor extends MethodVisitor {
		String temp;
		String idStorage;
		String nameStorage;
		String versionStorage;

		public ModMethodVisitor() {
			super(Opcodes.ASM4);
			temp = null;
			idStorage = null;
			nameStorage = "";
			versionStorage = "";
		}

		@Override
		public void visitFieldInsn(int opc, String owner, String name, String desc) {
			//TODO finding name in forge coremod
			if(name.equals("modId")) {
				idStorage = temp;
			} else if(name.equals("name")) {
				nameStorage = temp;
			} else if(name.equals("version")) {
				versionStorage = temp;
			}
		}

		@Override
		public void visitLdcInsn(Object cst) {
			temp = cst.toString();
		}

		@Override
		public void visitEnd() {
			if(idStorage != null) {
				otherMod.addID(idStorage);
				otherMod.addName(nameStorage);
				otherMod.addVersion(versionStorage);
			}
		}
	}
}
