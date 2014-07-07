package gr.watchful.permchecker.utils;

import gr.watchful.permchecker.datastructures.Mod;
import gr.watchful.permchecker.datastructures.SimpleObjectComparator;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.Collections;

public class DatastructureUtils {
    public static void sortDefaultListModel(DefaultListModel modelIn) {
		DefaultListModel<Comparable> model = (DefaultListModel<Comparable>) modelIn;
        ArrayList<Comparable> list = new ArrayList<>();
        for(int i=0; i<model.getSize(); i++) {
            list.add(model.get(i));
        }
        Collections.sort(list, new SimpleObjectComparator());

		ListDataListener[] listeners = model.getListDataListeners();
		for(ListDataListener listDataListener : listeners) {
			model.removeListDataListener(listDataListener);
		}

        model.clear();
        for(Comparable object : list) {
            model.addElement(object);
        }

		for(ListDataListener listDataListener : listeners) {
			model.addListDataListener(listDataListener);
		}
    }
}
