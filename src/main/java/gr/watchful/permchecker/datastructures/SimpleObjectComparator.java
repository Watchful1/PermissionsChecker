package gr.watchful.permchecker.datastructures;

import java.util.Comparator;

public class SimpleObjectComparator implements Comparator<Object> {

	@Override
	public int compare(Object o1, Object o2) {
		if(o1.getClass().equals(ModPack.class) && o2.getClass().equals(ModPack.class)) {
			ModPack pack1 = (ModPack) o1;
			ModPack pack2 = (ModPack) o2;
			if (pack1.name.toLowerCase().equals(pack2.name.toLowerCase()))
				return pack1.shortName.toLowerCase().compareTo(pack2.shortName.toLowerCase());
			else return pack1.name.toLowerCase().compareTo(pack2.name.toLowerCase());
		} else if(o1.getClass().equals(Mod.class) && o2.getClass().equals(Mod.class)) {
			Mod mod1 = (Mod) o1;
			Mod mod2 = (Mod) o2;
			return mod1.toString().compareToIgnoreCase(mod2.toString());
		} else return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
	}
}
