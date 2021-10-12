package tramais.hnb.hhrfid.interfaces

import tramais.hnb.hhrfid.bean.BankInfo
import tramais.hnb.hhrfid.bean.BankInfoDetail
import tramais.hnb.hhrfid.bean.CropFarmList
import tramais.hnb.hhrfid.bean.InsureLableBean

interface GetCropFarmList {
    fun cropFarmList(info: CropFarmList?)
}