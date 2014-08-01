package gr.watchful.permchecker.datastructures;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortedListModel<T> extends AbstractListModel {
	private ArrayList<T> list;

	public SortedListModel() {
		list = new ArrayList<>();
	}

	public void add(int index, T element) {
		list.add(index, element);
		fireIntervalAdded(this, index, index);
	}

	public void addElement(T element) {
		list.add(element);
		fireIntervalAdded(this, list.size() - 1, list.size() - 1);
	}

	public void addAll(ArrayList<T> array) {
		for(T element : array) {
			addElement(element);
		}
	}

	public void setElement(T element, int index) {
		list.set(index, element);
	}

	public void remove(int index) {
		list.remove(index);
		fireIntervalRemoved(this, index, index);
	}

	public void clear() {
		int temp = list.size() - 1;
		list.clear();
		fireIntervalRemoved(this, 0, (temp < 0) ? 0 : temp);
	}

	@Override
	public int getSize() {
		return list.size();
	}

	public boolean isEmpty() {
		return getSize() == 0;
	}

	@Override
	public Object getElementAt(int index) {
		return list.get(index);
	}

	public T get(int index) {
		return (T) getElementAt(index);
	}

	public int getIndexByString(T object) {
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).toString().equals(object.toString())) return i;
		}
		return -1;
	}

	public ArrayList<T> getArrayList() {
		return (ArrayList<T>) list.clone();
	}

	public void sort(Comparator comparator) {
		Collections.sort(list, comparator);
		fireContentsChanged(this, 0, list.size());
	}
}
