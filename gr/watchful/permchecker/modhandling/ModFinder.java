package gr.watchful.permchecker.modhandling;

import gr.watchful.permchecker.datastructures.Mod;
import gr.watchful.permchecker.datastructures.ModFile;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.DefaultListModel;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ModFinder {
	private static ModNameRegistry nameRegistry;
	private static ModFile otherMod;
	private static DefaultListModel<ModFile> modFiles;
	private static DefaultListModel<ModFile> unknownModFiles;
	private static DefaultListModel<Mod> mods;

	public static Mod[] discoverAllMods(File minecraftFolder, DefaultListModel<ModFile> unknownModFilesIn, DefaultListModel<Mod> modsIn, ModNameRegistry nameRegistryIn) {
		modFiles = new DefaultListModel<ModFile>();
		unknownModFiles = unknownModFilesIn;
		mods = modsIn;
		nameRegistry = nameRegistryIn;
		
		getMods(new File(minecraftFolder.getAbsolutePath() + File.separator + "mods"), unknownModFilesIn);
		compileModNames(modFiles);
		for(int i=0; i<mods.getSize(); i++) {
			System.out.println(mods.get(i));
		}
		System.out.println("\nUnknown mods\n");
		for(int i=0; i<unknownModFiles.getSize(); i++) {
			System.out.println("Couldn't identify a mod in "+unknownModFiles.get(i).fileName());
			//System.out.print("     ");
			for(int j=0; j<unknownModFiles.get(i).names.size(); j++) {
				//System.out.print(unknownModFiles.get(i).names.get(j)[0]+", ");
			}
			//System.out.print("\n");
		}
		
		return null;
	}
	
	public static void discoverModFiles(File minecraftFolder, DefaultListModel<ModFile> unknownModFilesIn) {
		getMods(new File(minecraftFolder+"\\mods"), unknownModFilesIn);
		getMods(new File(minecraftFolder+"\\coremods"), unknownModFilesIn);
		getMods(new File(minecraftFolder.getParentFile()+"\\instmods"), unknownModFilesIn);
	}
	
	private static void getMods(File folder, DefaultListModel<ModFile> modFiles) {
		if(!folder.exists()) {
			System.out.println(folder+" doesn't exist");
			return;
		}
		ModFile temp;
		for(File file : folder.listFiles()) {
			System.out.println(file.getName());
			try {
				if(file.isDirectory()) {
					getMods(file, modFiles);
				} else {
					temp = processFile(file);
					if(temp != null) modFiles.addElement(temp);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static class ModAnnotationVisitor extends AnnotationVisitor {
		String name;
		String id;
		
		public ModAnnotationVisitor() {
			super(Opcodes.ASM4);
			name = "";
			id = "";
		}
		
		@Override
		public void visit(String key, Object value) {
			//TODO forgemod, in annotation mod
			if(key.equals("modid")) {
				id = value.toString();
			} else if(key.equals("name")) {
				name = value.toString();
			}
		}
		
		@Override
		public void visitEnum(String name, String desc, String value) {
			
		}
		
		@Override
		public void visitEnd() {
			otherMod.addID(id);
			otherMod.addName(name);
		}
	}
	
	public static class ModMethodVisitor extends MethodVisitor {
		String temp;
		String idStorage;
		String nameStorage;
		
		public ModMethodVisitor() {
			super(Opcodes.ASM4);
			temp = null;
			idStorage = null;
			nameStorage = "";
		}
		
		@Override
		public void visitFieldInsn(int opc, String owner, String name, String desc) {
			//TODO finding name in forge coremod
			if(name.equals("modId")) {
				//System.out.println("   "+owner+" : "+name+" : "+desc);
				idStorage = temp;
			} else if(name.equals("name")) {
				//System.out.println("   "+owner+" : "+name+" : "+desc);
				nameStorage = temp;
			}
		}

		@Override
		public void visitLdcInsn(Object cst) {
			temp = cst.toString();
			//System.out.println("   "+cst);
		}
		
		@Override
		public void visitEnd() {
			if(idStorage != null) {
				//System.out.println("Mod ID is "+idStorage);
				otherMod.addID(idStorage);
				otherMod.addName(nameStorage);
			}
		}
	}

	public static class ModClassVisitor extends ClassVisitor {
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
				//System.out.println(name);
				tmp = true;
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
				return new ModMethodVisitor();
			}
			//System.out.println("    "+name);
			return null;
		}
	}
	
	public static ModFile processFile(File modArchive) throws IOException, ClassNotFoundException {
		otherMod = null;
		otherMod = new ModFile(modArchive);
		ZipFile file = null;
		try {
			file = new ZipFile(modArchive);
			Enumeration<? extends ZipEntry> files = file.entries();
			while (files.hasMoreElements()) {
				ZipEntry item = files.nextElement();
				if (item.isDirectory() || !item.getName().endsWith("class")) {
					continue;
				}
				ClassReader reader = new ClassReader(file.getInputStream(item));
				reader.accept(new ModClassVisitor(), 0);
			}
			file.close();
		} catch(ZipException zipException) {

		}

		if(otherMod.names.isEmpty()) {
			//System.out.println("File "+otherMod.fileName()+" doesn't look like a mod");
		} else {
			//System.out.println(otherMod.fileName()+" has mods");
			//for(String[] name : otherMod.names) {
				//System.out.println("   "+name[0]+"  :  "+name[1]);
			//}
		}
		return otherMod;
	}
	
	private static void compileModNames(DefaultListModel<ModFile> modFiles) {
		for(int i=0; i<modFiles.getSize(); i++) {
			processModFile(modFiles.get(i));
		}
	}
	
	private static void processModFile(ModFile modFile) {
		String result = null;
		HashSet<String> identifiedIDs = new HashSet<String>();
		for(int i=0; i<modFile.IDs.getSize(); i++) {
			result = nameRegistry.checkID(modFile.IDs.get(i));
			if(result != null) {
				identifiedIDs.add(result);
			}
		}
		if(identifiedIDs.isEmpty()) {
			unknownModFiles.addElement(modFile);
		} else {
			for(String ID : identifiedIDs) {
				mods.addElement(new Mod(modFile, ID));
			}
		}
	}
}
