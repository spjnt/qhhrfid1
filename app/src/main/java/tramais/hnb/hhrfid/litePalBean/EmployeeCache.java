package tramais.hnb.hhrfid.litePalBean;

import org.litepal.crud.LitePalSupport;

public class EmployeeCache extends LitePalSupport {
    private String EmployeeNo;
    private String EmployeeName;

    public String getEmployeeNo() {
        return EmployeeNo;
    }

    public void setEmployeeNo(String EmployeeNo) {
        this.EmployeeNo = EmployeeNo;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String EmployeeName) {
        this.EmployeeName = EmployeeName;
    }
}
