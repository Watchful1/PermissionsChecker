package gr.watchful.permchecker.utils;

import gr.watchful.permchecker.datastructures.Mod;
import gr.watchful.permchecker.datastructures.StringComparator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class DatastructureUtils {
    public static void sortDefaultListModel(DefaultListModel model) {
        ArrayList<Object> list = new ArrayList<>();
        for(int i=0; i<model.getSize(); i++) {
            list.add(model.get(i));
        }
        Collections.sort(list, new StringComparator());
        model.clear();
        for(Object object : list) {
            model.addElement(object);
        }
    }
}
