import gr.watchful.permchecker.modhandling.Mod;
import gr.watchful.permchecker.modhandling.ModFile;
import gr.watchful.permchecker.modhandling.ModFinder;
import gr.watchful.permchecker.panels.NamedScrollingListPanel;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

@SuppressWarnings("serial")
public class mainClass extends JFrame {
	DefaultListModel<Mod> goodMods;
	DefaultListModel<Mod> badMods;
	DefaultListModel<ModFile> unknownMods;
	
	public mainClass() {
		goodMods = new DefaultListModel<Mod>();
		badMods = new DefaultListModel<Mod>();
		unknownMods = new DefaultListModel<ModFile>();
		
		this.setTitle("Permissions Checker"); // set the title
        this.setPreferredSize(new Dimension(600,300)); // and the initial size
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setAlignmentX(0f);
        this.add(mainPanel);
        
        NamedScrollingListPanel<Mod> good = new NamedScrollingListPanel<Mod>("Good", new Dimension(100,300), goodMods);
        mainPanel.add(good);
        NamedScrollingListPanel<Mod> bad = new NamedScrollingListPanel<Mod>("Bad", new Dimension(100,300), badMods);
        mainPanel.add(bad);
        NamedScrollingListPanel<ModFile> unknown = new NamedScrollingListPanel<ModFile>("Unknown", new Dimension(100,300), unknownMods);
        mainPanel.add(unknown);
        
        JPanel newWindow = new JPanel();
        JPanel modEditWindow = new JPanel();
        
        JPanel cards = new JPanel(new CardLayout());
        cards.setMinimumSize(new Dimension(300, 300));
        cards.add(newWindow);
        cards.add(modEditWindow);
        
        mainPanel.add(cards);
        
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        discoverMods(new File("C:\\Users\\Gregory\\Desktop\\MultiMC\\instances\\Hammercraft 4.3.0 Custom\\minecraft"));
	}

	public static void main(String[] args) {
		new mainClass();
		//discoverMods(new File("C:\\Users\\Gregory\\Desktop\\MultiMC\\instances\\Hammercraft 4.3.0 Custom\\minecraft"));
	}
	
	private void discoverMods(File minecraftFolder) {
		ModFinder.discoverAllMods(minecraftFolder, unknownMods, goodMods);
	}
}
