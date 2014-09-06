package gr.watchful.permchecker.modhandling;

import gr.watchful.permchecker.datastructures.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ModFinder {
	// We need a central place to add ID's to when we can't return what we want
	private ModFile otherMod;

	public ArrayList<ModFile> discoverModFiles(File folder) {
		ArrayList<ModFile> modFiles = new ArrayList<>();

		if(folder == null || !folder.exists()) return modFiles;

		ModFile temp;
		for(File file : folder.listFiles()) {
			if(file.isDirectory())  modFiles.addAll(discoverModFiles(file));
			else {
				/*int i = file.getName().lastIndexOf('.');
				if (i <= 0) continue;
				String extension = file.getName().substring(i+1);
				boolean good = false;
				for(String type : Globals.modTypes) {
					if(type.equals(extension)) good = true;
				}
				if(!good) continue; *///TODO Clumsy, need to work on
				try {
					temp = processFile(file);
					if (temp != null) modFiles.add(temp);
				} catch (IOException | ClassNotFoundException e) {
					modFiles.add(new ModFile(file));
				}
			}
		}

		return modFiles;
	}

	public ModFile processFile(File modArchive) throws IOException, ClassNotFoundException {
		otherMod = new ModFile(modArchive);

		ZipFile file = new ZipFile(modArchive);
		Enumeration<? extends ZipEntry> files = file.entries();
		while(files.hasMoreElements()) {
			ZipEntry item = files.nextElement();
			if(item.getName().equals("mcmod.info")) {
				otherMod.mcmod = MetadataCollection.from(file.getInputStream(item), file.getName());
			}
			if(item.isDirectory() || !item.getName().endsWith("class")) continue;

			ClassReader reader = new ClassReader(file.getInputStream(item));
			reader.accept(new ModClassVisitor(), 0);
		}
		file.close();
		return otherMod;
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
