package gr.watchful.permchecker.datastructures;

import java.util.Comparator;

/**
 * Created by Gregory on 5/10/2014.
 */
public class StringComparator implements Comparator<Object> {
    @Override
    public int compare(Object o1, Object o2) {
        return o1.toString().compareTo(o2.toString());
    }
}
