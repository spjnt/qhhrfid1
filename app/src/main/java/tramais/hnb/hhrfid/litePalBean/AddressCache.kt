package tramais.hnb.hhrfid.litePalBean

import com.alibaba.fastjson.annotation.JSONField
import org.litepal.crud.LitePalSupport

class AddressCache:LitePalSupport() {
    /**
     * code
     */
    @JSONField(name = "Code")
    var code = 0

    /**
     * msg
     */
    @JSONField(name = "Msg")
    var msg: String? = null

    /**
     * companyAddress
     */
    var companyAddress: String? = null
}