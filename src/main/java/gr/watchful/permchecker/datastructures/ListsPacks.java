package gr.watchful.permchecker.datastructures;

public interface ListsPacks {
	public boolean codeExists(String code, String currentPack);

	public boolean shortnameExists(String shortname);

	public void nameChanged();
}
