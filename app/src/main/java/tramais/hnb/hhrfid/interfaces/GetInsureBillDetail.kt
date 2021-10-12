package tramais.hnb.hhrfid.interfaces

import tramais.hnb.hhrfid.bean.InsureBillDetail

interface GetInsureBillDetail {
    fun getBillDetail(billDetailBean: InsureBillDetail?)
}