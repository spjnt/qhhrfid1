package tramais.hnb.hhrfid.interfaces

import tramais.hnb.hhrfid.bean.BankInfo
import tramais.hnb.hhrfid.bean.BankInfoDetail

interface GetBankInfo {
    fun bankInfo(info: BankInfoDetail?)
}