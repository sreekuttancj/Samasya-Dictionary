package malayalamdictionary.samasya.helper;

public class FavouriteItem {

    String name;
    boolean selected;

    public FavouriteItem(String name, boolean selected) {
        this.name = null;
        this.selected = false;
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
