import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class mainClass {

	public static void main(String[] args) {
		discoverAllMods(new File("C:\\Users\\Gregory\\Desktop\\MultiMC\\instances\\Hammercraft 4.3.0 Custom\\minecraft\\mods"));
	}
	
	public static class ModFileInfo {
		public String fileName;
		public ArrayList<String> names = new ArrayList<String>();
		
		public ModFileInfo(String file) {
			fileName = file;
		}
		
		public void addName(String name) {
			names.add(name);
		}
	}
 
	public static ModFileInfo otherMod;
	
	public static class ModAnnotationVisitor extends AnnotationVisitor {
 
		
		public ModAnnotationVisitor() {
			super(Opcodes.ASM4);
		}
		
		@Override
		public void visit(String key, Object value) {
			if(key.equals("modid")) {
				otherMod.addName(value.toString());
			}
		}
		
		@Override
		public void visitEnum(String name, String desc, String value) {
			
		}
		
		@Override
		public void visitEnd() {
			
		}
	}
	
	public static class ModMethodVisitor extends MethodVisitor {
		String idTemp; //TODO add name storage
		String idStorage;
		
		public ModMethodVisitor() {
			super(Opcodes.ASM4);
			idTemp = null;
			idStorage = null;
		}
		
		@Override
		public void visitFieldInsn(int opc, String owner, String name, String desc) {
			//System.out.println("   "+owner+" : "+name+" : "+desc);
			if(name.equals("modId")) {
				idStorage = idTemp;
			}
		}

		@Override
		public void visitLdcInsn(Object cst) {
			idTemp = cst.toString();
			//System.out.println("   "+cst);
		}
		
		@Override
		public void visitEnd() {
			if(idStorage != null) {
				//System.out.println("Mod ID is "+idStorage);
				otherMod.addName(idStorage);
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
				otherMod.addName(name);
			} else if(superName.equals("DummyModContainer") || superName.equals("cpw/mods/fml/common/DummyModContainer")) {
				System.out.println(name);
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
	
	public static void processFile(File modArchive) throws IOException, ClassNotFoundException {
		if (modArchive.getName().endsWith("jar") || 
				modArchive.getName().endsWith("zip")) {
			otherMod = new ModFileInfo(modArchive.getName());
			ZipFile file = new ZipFile(modArchive);
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
			if(otherMod.names.isEmpty()) {
				System.out.println("File "+otherMod.fileName+" doesn't look like a mod");
			} else {
				System.out.println(otherMod.fileName+" has mods");
				for(String name : otherMod.names) {
					System.out.println("   "+name);
				}
			}
		}
	}
	
	public static void discoverAllMods(File modFolder) {
		for (File file : modFolder.listFiles()) {
			try {
				processFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
