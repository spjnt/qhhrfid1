package tramais.hnb.hhrfid.bean;

public class SearchByFarm {

    /**
     * ID : 0
     * LabelNumber : 100000008751
     * CheckTime : 2020-3-24 16:52:19
     * InsureTime :
     * Status : 新建
     */
    private String Category;
    private int ID;
    private String LabelNumber;
    private String CheckTime;
    private String InsureTime;
    private String Status;
    private String path;

    //SFZNumber  Name
    private String SFZNumber;
    private String Name;

    public String getSFZNumber() {
        return SFZNumber;
    }

    public void setSFZNumber(String SFZNumber) {
        this.SFZNumber = SFZNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLabelNumber() {
        return LabelNumber;
    }

    public void setLabelNumber(String LabelNumber) {
        this.LabelNumber = LabelNumber;
    }

    public String getCheckTime() {
        return CheckTime;
    }

    public void setCheckTime(String CheckTime) {
        this.CheckTime = CheckTime;
    }

    public String getInsureTime() {
        return InsureTime;
    }

    public void setInsureTime(String InsureTime) {
        this.InsureTime = InsureTime;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }
}
