package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;
import gr.watchful.permchecker.utils.DatastructureUtils;
import org.apache.poi.ss.formula.functions.T;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class NamedScrollingListPanel<T extends Comparable> extends JPanel implements ListSelectionListener {
	private JList<T> list;
	private String name = "";
	private ArrayList<NamedScrollingListPanelListener> listeners;
	
	public NamedScrollingListPanel(String name, int size, DefaultListModel<T> model) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setMinimumSize(new Dimension(size, 50));
		this.setMaximumSize(new Dimension(size, Integer.MAX_VALUE));
		this.setPreferredSize(new Dimension(size, 500));
		if(name != null) {
            JLabel nameLabel = new JLabel(name);
            nameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			this.add(nameLabel);

			this.name = name;
		}
		
		list = new JList<T>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		
		listeners = new ArrayList<>();
		
		JScrollPane scrList = new JScrollPane();
        scrList.getViewport().setView(list);
        this.add(scrList);
	}
	
	public void addListener(NamedScrollingListPanelListener listener) {
		listeners.add(listener);
	}
	
	public void setModel(DefaultListModel<T> model) {
		list.setModel(model);
		for(int i=0; i<list.getModel().getSize(); i++) {
			System.out.println(list.getModel().getElementAt(i));
		}
	}
	
	public DefaultListModel<T> getModel() {
		return (DefaultListModel<T>) list.getModel();
	}

    public T getSelected() {
        return list.getSelectedValue();
    }

    public int getNamePos(String name) {
        ListModel model = getModel();
        for(int i=0; i<model.getSize(); i++) {
            if(model.getElementAt(i).toString().equals(name)) return i;
        }
        return -1;
    }

    public void setSelectedName(String name) {
        setSelected(getNamePos(name));
    }

    public void setSelected(int pos) {
        if(pos < 0 || pos > list.getModel().getSize()) System.out.println("Name not found, couldn't select");
        else list.setSelectedIndex(pos);
    }
	
	public void clearSelection() {
		list.clearSelection();
	}

    public void sort() {
        DatastructureUtils.sortDefaultListModel((DefaultListModel<Comparable>) getModel());
    }

    public void sortKeepSelected() {
        String name = getSelected().toString();
        sort();
        setSelectedName(name);
    }

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting() && list.getSelectedIndex() != -1) {
			for(NamedScrollingListPanelListener listener : listeners) {
				listener.selectionChanged(new NamedSelectionEvent(name, list.getSelectedIndex()));
			}
		}
	}
}
