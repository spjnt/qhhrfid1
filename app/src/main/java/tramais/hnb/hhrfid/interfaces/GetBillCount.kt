package tramais.hnb.hhrfid.interfaces

import tramais.hnb.hhrfid.bean.BillCount

interface GetBillCount {
    fun  getBillCount(bean:BillCount?)
}