package gr.watchful.permchecker.datastructures;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortedListModel<T> extends AbstractListModel {
	private ArrayList<T> fullList;
	private ArrayList<T> filteredlist;
	private String filter;
	public boolean blockUpdates;

	public SortedListModel() {
		fullList = new ArrayList<>();
		filteredlist = new ArrayList<>();
		filter = "";
		blockUpdates = false;
	}

	public boolean isFiltered() {
		return !filter.equals("");
	}

	public void updateFilter() {
		updateFilter(filter);
	}

	public void updateFilter(String newFilter) {
		filter = newFilter;

		filteredlist.clear();

		if(filter.equals("")) {
			filteredlist.addAll(fullList);
			if(!blockUpdates) fireContentsChanged(this, 0, filteredlist.size());
		} else {
			for(T item : fullList) {
				if(matchesFilter(item)) filteredlist.add(item);
			}
			if(!blockUpdates) fireContentsChanged(this, 0, fullList.size());
		}
	}

	public boolean matchesFilter(T item) {
		return item.toString().toLowerCase().contains(filter.toLowerCase());
	}

	public void add(int index, T element) {
		fullList.add(index, element);
		if(isFiltered() && matchesFilter(element)) updateFilter();
		else if(!blockUpdates) fireIntervalAdded(this, index, index);
	}

	public void addElement(T element) {
		fullList.add(element);
		if(isFiltered() && matchesFilter(element)) updateFilter();
		else if(!blockUpdates) fireIntervalAdded(this, fullList.size() - 1, fullList.size() - 1);
	}

	public void addAll(ArrayList<T> array) {
		for(T element : array) {
			fullList.add(element);
		}
		if(isFiltered()) updateFilter();
		else if(!blockUpdates) fireIntervalAdded(this, 0, fullList.size() - 1);
	}

	public void setElement(T element, int index) {
		fullList.set(index, element);
		if(isFiltered() && matchesFilter(element)) updateFilter();
		else if(!blockUpdates) fireContentsChanged(this, index, index);
	}

	public void remove(int index) {
		if(isFiltered()) {
			T item = filteredlist.get(index);
			filteredlist.remove(index);
			fullList.remove(item);
		} else {
			fullList.remove(index);
		}
		if(!blockUpdates) fireIntervalRemoved(this, index, index);
	}

	public void clear() {
		int temp = fullList.size() - 1;
		fullList.clear();
		filteredlist.clear();
		if(!blockUpdates) fireIntervalRemoved(this, 0, (temp < 0) ? 0 : temp);
	}

	@Override
	public int getSize() {
		if(isFiltered()) return filteredlist.size();
		else return fullList.size();
	}

	public boolean isEmpty() {
		return getSize() == 0;
	}

	@Override
	public Object getElementAt(int index) {
		if(isFiltered()) return filteredlist.get(index);
		else return fullList.get(index);
	}

	public T get(int index) {
		return (T) getElementAt(index);
	}

	public int getIndexByString(T object) {
		if(isFiltered()) {
			for(int i=0; i<filteredlist.size(); i++) {
				if(filteredlist.get(i).toString().equals(object.toString())) return i;
			}
		} else {
			for(int i=0; i<fullList.size(); i++) {
				if(fullList.get(i).toString().equals(object.toString())) return i;
			}
		}
		return -1;
	}

	/**
	 * Always returns the non-filtered list
	 * @return
	 */
	public ArrayList<T> getArrayList() {
		return (ArrayList<T>) fullList.clone();
	}

	public void sort(Comparator comparator) {
		Collections.sort(fullList, comparator);

		boolean cache = blockUpdates;
		blockUpdates = true;
		if(isFiltered()) updateFilter();
		blockUpdates = cache;

		if(!blockUpdates) {
			if(isFiltered()) fireContentsChanged(this, 0, filteredlist.size());
			else fireContentsChanged(this, 0, fullList.size());
		}
	}
}
