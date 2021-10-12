package tramais.hnb.hhrfid.bean;

public class SystemListBean {

    /**
     * Item : 11
     * Values : 藏系牦牛
     */

    private String Item;
    private String Values;
    private String Category;

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getItem() {
        return Item;
    }

    public void setItem(String Item) {
        this.Item = Item;
    }

    public String getValues() {
        return Values;
    }

    public void setValues(String Values) {
        this.Values = Values;
    }
}
