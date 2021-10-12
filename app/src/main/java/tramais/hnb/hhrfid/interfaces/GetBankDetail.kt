package tramais.hnb.hhrfid.interfaces

import tramais.hnb.hhrfid.bean.BankInfoDetail

 interface GetBankDetail {
    fun getBankInfo(infoDetail: BankInfoDetail.GetBankResulDataDTO.GetBankResultDetaiDataDTO?)
}