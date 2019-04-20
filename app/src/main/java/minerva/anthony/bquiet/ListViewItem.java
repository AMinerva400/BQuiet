package minerva.anthony.bquiet;

public class ListViewItem {
    private boolean checked = false;
    private String itemText = "";
    private User user = null;

    public boolean isChecked() {
        return checked;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }
}
