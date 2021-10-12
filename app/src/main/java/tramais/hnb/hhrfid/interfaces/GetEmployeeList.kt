package tramais.hnb.hhrfid.interfaces

import tramais.hnb.hhrfid.bean.BankInfoDetail
import tramais.hnb.hhrfid.bean.EmployeeListBean

interface GetEmployeeList {
    fun getEmployee(infoDetail: EmployeeListBean?)
}