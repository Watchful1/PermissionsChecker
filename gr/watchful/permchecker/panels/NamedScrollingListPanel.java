
package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.listenerevent.NamedScrollingListPanelListener;
import gr.watchful.permchecker.listenerevent.NamedSelectionEvent;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class NamedScrollingListPanel<T> extends JPanel implements ListSelectionListener {
    private JList<T> list;
    private String name = "";
    private ArrayList<NamedScrollingListPanelListener> listeners;

    public NamedScrollingListPanel(String name, Dimension size, DefaultListModel<T> model) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //this.setMinimumSize(size);
        //this.setMaximumSize(size);
        this.setPreferredSize(size);
        if (name != null) {
            this.add(new JLabel(name));
            this.name = name;
        }

        list = new JList<T>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);

        listeners = new ArrayList<NamedScrollingListPanelListener>();

        JScrollPane scrList = new JScrollPane();
        scrList.getViewport().setView(list);
        this.add(scrList);
    }

    public T getSelected() {
        return list.getSelectedValue();
    }

    public void addListener(NamedScrollingListPanelListener listener) {
        listeners.add(listener);
    }

    public void setModel(DefaultListModel<T> model) {
        list.setModel(model);
        for (int i = 0; i < list.getModel().getSize(); i++) {
            System.out.println(list.getModel().getElementAt(i));
        }
    }

    public void clearSelection() {
        list.clearSelection();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && list.getSelectedIndex() != -1) {
            for (NamedScrollingListPanelListener listener : listeners) {
                listener.selectionChanged(new NamedSelectionEvent(name, list.getSelectedIndex()));
            }
        }
    }
}
