package gr.watchful.permchecker.datastructures;

import java.util.Comparator;

public class SimpleObjectComparator implements Comparator<Object> {
    @Override
    public int compare(Object o1, Object o2) {
		if(o1.getClass().equals(ModPack.class) && o2.getClass().equals(ModPack.class)) {
			ModPack pack1 = (ModPack) o1;
			ModPack pack2 = (ModPack) o2;
			if(pack1.name.equals(pack2.name)) return pack1.shortName.compareTo(pack2.shortName);
			else return pack1.name.compareTo(pack2.name);
		} else return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
    }
}
