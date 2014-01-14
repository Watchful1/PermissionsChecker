package gr.watchful.permchecker.panels;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class NamedScrollingListPanel<T> extends JPanel {
	private JList<T> list;
	
	public NamedScrollingListPanel(String name, Dimension size, DefaultListModel<T> model) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//this.setMinimumSize(size);
		//this.setMaximumSize(size);
		this.setPreferredSize(size);
		if(name != null) {
			this.add(new JLabel(name));
		}
		
		list = new JList<T>(model);
		
		JScrollPane scrList = new JScrollPane();
        scrList.getViewport().setView(list);
        this.add(scrList);
	}
	
	public void setModel(DefaultListModel<T> model) {
		list.setModel(model);
		for(int i=0; i<list.getModel().getSize(); i++) {
			System.out.println(list.getModel().getElementAt(i));
		}
	}
}
