package gr.watchful.permchecker.datastructures;

public interface ListsPacks {
	boolean codeExists(String code, String currentPack);

	boolean shortnameExists(String shortname);

	boolean curseIDUsed(String curseID, String currentPack);

	void nameChanged();
}
