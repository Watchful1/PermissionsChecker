
package gr.watchful.permchecker.listenerevent;

public class NamedSelectionEvent {
    private String parentName;
    private int selected;

    public NamedSelectionEvent(String parentName, int selected) {
        this.parentName = parentName;
        this.selected = selected;
    }

    public String getParentName() {
        return parentName;
    }

    public int getSelected() {
        return selected;
    }
}
