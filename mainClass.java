import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFrame;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;


public class mainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Gui();
	}

	@SuppressWarnings("serial")
	public static class Gui extends JFrame {
		public Gui() {
			this.setTitle("Modpack Builder");
            this.setPreferredSize(new Dimension(300,300));
            
            
            
            pack();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
		}
	}
	
	public class ModFileInfo {
		public File file;
		public ArrayList<String> names = new ArrayList<String>();
		
		public ModFileInfo(File file) {
			this.file = file;
		}
		
		public void addName(String name) {
			names.add(name);
		}
	}

	public static class ModInfo {
		 HashMap<String, String> items = new HashMap<String, String>();
		 String _name;
		 
		 public ModInfo(String name) {
			 _name = name;
		 }
		 
		 public void set(String key, String value) {
			 //System.out.println(key);
			 items.put(key, value);
		 }
		 
		 @Override
		public String toString() {
			 return items.get("name");
		}
	}
 
	public static ArrayList<ModInfo> mods = new ArrayList<ModInfo>();
	
	public static class ModAnnotationVisitor extends AnnotationVisitor {
 
		ModInfo _currentMod;
		
		public ModAnnotationVisitor(ModInfo info) {
			super(Opcodes.ASM4);
			_currentMod = info;
		}
		
		@Override
		public void visit(String key, Object value) {
			_currentMod.set(key, value.toString());
		}
		
		@Override
		public void visitEnum(String name, String desc, String value) {
			
		}
		
		@Override
		public void visitEnd() {
			mods.add(_currentMod);
		}
	}
 
	public static class ModClassVisitor extends ClassVisitor {
 
		String _currentName = "";
		
		public ModClassVisitor() {
			super(Opcodes.ASM4);
		}
		
		@Override
		public void visit(int version, int access, String name, String signature,
				String superName, String[] interfaces) {
			_currentName = name;
		}
		
		@Override
		public AnnotationVisitor visitAnnotation(String name, boolean runtime) {
			if (name.equals("Lcpw/mods/fml/common/Mod;")) {
				return new ModAnnotationVisitor(new ModInfo(_currentName));
			} else {
				return new AnnotationVisitor(Opcodes.ASM4) {};
			}
		}
 
	}
	
	public static void processFile(File modArchive) throws IOException, ClassNotFoundException {
		if (modArchive.getName().endsWith("jar") || 
				modArchive.getName().endsWith("zip")) {
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
	
	public static void printAllMods(File folder) {
		discoverAllMods(folder);
		for (ModInfo mod : mods) {
			System.out.println(mod.toString());
		}
	}
	
	public static ArrayList<ModInfo> returnAllMods(File folder) {
		discoverAllMods(folder);
		return mods;
	}
	
	public static String[] returnModNames(File folder) {
		ArrayList<ModInfo> tempmods = returnAllMods(folder);
		String[] out = new String[tempmods.size()];
		int counter = 0;
		for(ModInfo info : tempmods) {
			out[counter] = info.toString();
			counter++;
		}
		return out;
	}
}
