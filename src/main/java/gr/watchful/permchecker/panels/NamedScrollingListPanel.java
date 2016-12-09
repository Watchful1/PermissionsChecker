package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.datastructures.SimpleObjectComparator;
import gr.watchful.permchecker.datastructures.SortedListModel;
import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class NamedScrollingListPanel<T> extends JPanel implements ListSelectionListener {
	private JList<T> list;
	private SortedListModel<T> model;
	private String name = "";
	private ArrayList<NamedScrollingListPanelListener> listeners;

	public NamedScrollingListPanel(String name, int size, SortedListModel<T> model) {
		this(name, size, model, false);
	}
	
	public NamedScrollingListPanel(String name, int size, final SortedListModel<T> model, boolean enableFiltering) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setMinimumSize(new Dimension(size, 50));
		this.setMaximumSize(new Dimension(size, Integer.MAX_VALUE));
		this.setPreferredSize(new Dimension(size, 500));

		this.model = model;

		if(name != null) {
			JLabel nameLabel = new JLabel(name);
			nameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			this.add(nameLabel);

			this.name = name;
		}

		if(enableFiltering) {
			final JTextField filterField = new JTextField();
			filterField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			filterField.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateFilter(filterField.getText());
				}
			});
			this.add(filterField);
		}
		
		list = new JList<T>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		
		listeners = new ArrayList<>();
		
		JScrollPane scrList = new JScrollPane();
		scrList.getViewport().setView(list);
		this.add(scrList);
	}

	private void updateFilter(String filter) {
		model.updateFilter(filter);
	}
	
	public void addListener(NamedScrollingListPanelListener listener) {
		listeners.add(listener);
	}
	
	public void setModel(SortedListModel<T> model) {
		list.setModel(model);
		/*for(int i=0; i<list.getModel().getSize(); i++) {
			System.out.println(list.getModel().getElementAt(i));
		}*/
	}
	
	public SortedListModel<T> getModel()
	{
		return (SortedListModel<T>) list.getModel();
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
		getModel().sort(new SimpleObjectComparator());
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
