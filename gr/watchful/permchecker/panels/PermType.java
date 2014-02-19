package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.ModInfo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class PermType extends JPanel {
	private JTextArea text;
	
	private static Color open = new Color(0, 176, 80);
	private static Color notify = new Color(169, 208, 142);
	private static Color FTB = new Color(47, 117, 181);
	private static Color request = new Color(255, 192, 0);
	private static Color closed = new Color(255, 0, 0);
	private static Color unknown = new Color(192, 192, 192);
	
	private static String openText = "This mod has an open permissions policy for this category. As long as you credit the author and link their page, you can use this mod in your modpack.";
	private static String notifyText = "This mod has an open permissions policy for this category. However, the mod author must be notified with through the method specified in the license that their mod is being used";
	private static String FTBText = "This mod has a FTB specific policy for this group. It is permissible to use this mod in a 3rd party FTB modpack. See the mod's page for details concerning permissions outside the FTB launcher.";
	private static String requestText = "This mod has a restricted permissions policy for this category. You must contact the author and obtain permission for your modpack.";
	private static String closedText = "This mod has a closed modpack policy for this category. You do not have permission to include this mod in your modpack. Please do not contact the author to try to request permission.";
	private static String unknownText = "This spreadsheet does not yet contain this information.";
	
	public PermType() {
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.setAlignmentY(0);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		text = new JTextArea(unknownText);
		text.setBackground(unknown);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setAlignmentX(Component.LEFT_ALIGNMENT);
		text.setAlignmentY(0);
		
		text.setMinimumSize(new Dimension(200, 100));
		text.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		text.setPreferredSize(new Dimension(200, 100));
		
		this.add(text);
	}
	
	public void setType(int type) {
		switch(type) {
		case ModInfo.OPEN:
			text.setText(openText);
			text.setBackground(open);
			break;
		case ModInfo.NOTIFY:
			text.setText(notifyText);
			text.setBackground(notify);
			break;
		case ModInfo.FTB:
			text.setText(FTBText);
			text.setBackground(FTB);
			break;
		case ModInfo.REQUEST:
			text.setText(requestText);
			text.setBackground(request);
			break;
		case ModInfo.CLOSED:
			text.setText(closedText);
			text.setBackground(closed);
			break;
		case ModInfo.UNKNOWN:
			text.setText(unknownText);
			text.setBackground(unknown);
			break;
		}
	}
}
