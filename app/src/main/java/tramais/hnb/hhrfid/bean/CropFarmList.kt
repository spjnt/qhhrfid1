package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

class CropFarmList {
    /**
     * data
     */
    @JSONField(name = "Data")
    var data: List<DataDTO>? = null

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

    class DataDTO :Serializable {
        /**
         * companyNumber
         */
        @JSONField(name = "CompanyNumber")
        var companyNumber: String? = null

        /**
         * number
         */
        @JSONField(name = "Number")
        var number: String? = null

        /**
         * name
         */
        @JSONField(name = "Name")
        var name: String? = null

        /**
         * mobile
         */
        @JSONField(name = "Mobile")
        var mobile: String? = null

        /**
         * bankName
         */
        @JSONField(name = "BankName")
        var bankName: String? = null

        /**
         * accountNumber
         */
        @JSONField(name = "AccountNumber")
        var accountNumber: String? = null

        /**
         * bankPicture
         */
        @JSONField(name = "BankPicture")
        var bankPicture: String? = null

        /**
         * signPicture
         */
        @JSONField(name = "SignPicture")
        var signPicture: String? = null

        /**
         * fValidate
         */
        @JSONField(name = "FValidate")
        var fValidate: Any? = null

        /**
         * bankRelatedCode
         */
        @JSONField(name = "BankRelatedCode")
        var bankRelatedCode: String? = null

        /**
         * isEffective
         */
        @JSONField(name = "IsEffective")
        var isEffective: String? = null
    }
}