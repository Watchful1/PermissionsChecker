package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.ModInfo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
	
	private JRadioButton openButton;
	private JRadioButton notifyButton;
	private JRadioButton FTBButton;
	private JRadioButton requestButton;
	private JRadioButton closedButton;
	private JRadioButton unknownButton;
	
	private int type;
	
	public PermType() {
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.setAlignmentY(0);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		type = ModInfo.UNKNOWN;
		
		text = new JTextArea(unknownText);
		text.setBackground(unknown);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		
		text.setMinimumSize(new Dimension(200, 100));
		text.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		
		this.add(text);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
		openButton = new JRadioButton();
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setType(ModInfo.OPEN);
			}
		});
		buttonPanel.add(new JLabel("Open"));
		buttonPanel.add(openButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		notifyButton = new JRadioButton();
		notifyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setType(ModInfo.NOTIFY);
			}
		});
		buttonPanel.add(new JLabel("Notify"));
		buttonPanel.add(notifyButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));

		FTBButton = new JRadioButton();
		FTBButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setType(ModInfo.FTB);
			}
		});
		buttonPanel.add(new JLabel("FTB"));
		buttonPanel.add(FTBButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		requestButton = new JRadioButton();
		requestButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setType(ModInfo.REQUEST);
			}
		});
		buttonPanel.add(new JLabel("Request"));
		buttonPanel.add(requestButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		closedButton = new JRadioButton();
		closedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setType(ModInfo.CLOSED);
			}
		});
		buttonPanel.add(new JLabel("Closed"));
		buttonPanel.add(closedButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		unknownButton = new JRadioButton();
		unknownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setType(ModInfo.UNKNOWN);
			}
		});
		buttonPanel.add(new JLabel("Unknown"));
		buttonPanel.add(unknownButton);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(openButton);
		buttonGroup.add(notifyButton);
		buttonGroup.add(FTBButton);
		buttonGroup.add(requestButton);
		buttonGroup.add(closedButton);
		buttonGroup.add(unknownButton);
		
		this.add(buttonPanel);
	}
	
	public void setType(int type) {
		this.type = type;
		switch(type) {
		case ModInfo.OPEN:
			text.setText(openText);
			text.setBackground(open);
			openButton.setSelected(true);
			break;
		case ModInfo.NOTIFY:
			text.setText(notifyText);
			text.setBackground(notify);
			notifyButton.setSelected(true);
			break;
		case ModInfo.FTB:
			text.setText(FTBText);
			text.setBackground(FTB);
			FTBButton.setSelected(true);
			break;
		case ModInfo.REQUEST:
			text.setText(requestText);
			text.setBackground(request);
			requestButton.setSelected(true);
			break;
		case ModInfo.CLOSED:
			text.setText(closedText);
			text.setBackground(closed);
			closedButton.setSelected(true);
			break;
		case ModInfo.UNKNOWN:
			text.setText(unknownText);
			text.setBackground(unknown);
			unknownButton.setSelected(true);
			break;
		}
	}
	
	public int getType() {
		return type;
	}
}
