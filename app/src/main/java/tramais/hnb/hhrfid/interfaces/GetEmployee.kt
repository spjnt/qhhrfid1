package tramais.hnb.hhrfid.interfaces

import tramais.hnb.hhrfid.bean.EmployeeListBean

interface GetEmployee {
    fun getString(str: EmployeeListBean.DataDTO?)
}