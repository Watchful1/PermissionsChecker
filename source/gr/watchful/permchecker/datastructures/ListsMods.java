package gr.watchful.permchecker.datastructures;

import gr.watchful.permchecker.utils.FileUtils;

public interface ListsMods {
	public boolean codeExists(String code, String currentPack);

	public boolean shortnameExists(String shortname);
}
