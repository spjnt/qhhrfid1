package tramais.hnb.hhrfid.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Roles implements Parcelable {


    private String FObjSubGroup;
    private String FObjGroup;
    private String FObjectName;
    private String FObjectNO;

    public Roles() {

    }

    protected Roles(Parcel in) {
        FObjSubGroup = in.readString();
        FObjGroup = in.readString();
        FObjectName = in.readString();
        FObjectNO = in.readString();
    }

    public static final Creator<Roles> CREATOR = new Creator<Roles>() {
        @Override
        public Roles createFromParcel(Parcel in) {
            return new Roles(in);
        }

        @Override
        public Roles[] newArray(int size) {
            return new Roles[size];
        }
    };

    public String getFObjSubGroup() {
        return FObjSubGroup;
    }

    public void setFObjSubGroup(String FObjSubGroup) {
        this.FObjSubGroup = FObjSubGroup;
    }

    public String getFObjGroup() {
        return FObjGroup;
    }

    public void setFObjGroup(String FObjGroup) {
        this.FObjGroup = FObjGroup;
    }

    public String getFObjectName() {
        return FObjectName;
    }

    public void setFObjectName(String FObjectName) {
        this.FObjectName = FObjectName;
    }

    public String getFObjectNO() {
        return FObjectNO;
    }

    public void setFObjectNO(String FObjectNO) {
        this.FObjectNO = FObjectNO;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FObjSubGroup);
        dest.writeString(FObjGroup);
        dest.writeString(FObjectName);
        dest.writeString(FObjectNO);
    }
}
