package tramais.hnb.hhrfid.litePalBean

import com.alibaba.fastjson.annotation.JSONField
import org.litepal.crud.LitePalSupport

class BankInfoCache :LitePalSupport(){
    var jsonString: String? = null

    /**
     * getBankResulData
     */
    private var getBankResulData: List<GetBankResulDataDTO?>? = null

    /**
     * code
     */
    @JSONField(name = "Code")
    private var code = 0

    /**
     * msg
     */
    @JSONField(name = "Msg")
    private var msg: String? = null

    fun getGetBankResulData(): List<GetBankResulDataDTO?>? {
        return getBankResulData
    }

    fun setGetBankResulData(getBankResulData: List<GetBankResulDataDTO?>?) {
        this.getBankResulData = getBankResulData
    }

    fun getCode(): Int {
        return code
    }

    fun setCode(code: Int) {
        this.code = code
    }

    fun getMsg(): String? {
        return msg
    }

    fun setMsg(msg: String?) {
        this.msg = msg
    }

    class GetBankResulDataDTO {
        /**
         * getBankResultDetaiData
         */
        var getBankResultDetaiData: List<GetBankResultDetaiDataDTO>? = null


        /**
         * fBackName
         */
        @JSONField(name = "FBackName")
        var fBackName: String? = null

        class GetBankResultDetaiDataDTO {
            /**
             * fBankCode
             */
            @JSONField(name = "FBankCode")
            var fBankCode: String? = null

            /**
             * fBankDetailName
             */
            @JSONField(name = "FBankDetailName")
            var fBankDetailName: String? = null

            /**
             * fDetailCode
             */
            @JSONField(name = "FDetailCode")
            var fDetailCode: String? = null
        }
    }
}