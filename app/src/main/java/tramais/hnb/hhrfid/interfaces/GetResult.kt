package tramais.hnb.hhrfid.interfaces

import tramais.hnb.hhrfid.bean.BankInfoDetail
import tramais.hnb.hhrfid.bean.ResultBean

interface GetResult {
    fun getResult(result: ResultBean?)
}