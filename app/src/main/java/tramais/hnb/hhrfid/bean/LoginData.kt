package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

internal class LoginData {
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
     * data
     */
    @JSONField(name = "Data")
    var data: DataDTO? = null

    class DataDTO {
        /**
         * companyNumber
         */
        @JSONField(name = "CompanyNumber")
        var companyNumber: String? = null

        /**
         * companName
         */
        @JSONField(name = "CompanName")
        var companName: String? = null

        /**
         * userNumber
         */
        @JSONField(name = "UserNumber")
        var userNumber: String? = null

        /**
         * userName
         */
        @JSONField(name = "UserName")
        var userName: String? = null

        /**
         * userRole
         */
        @JSONField(name = "UserRole")
        var userRole: String? = null

        /**
         * province
         */
        @JSONField(name = "Province")
        var province: String? = null

        /**
         * city
         */
        @JSONField(name = "City")
        var city: String? = null

        /**
         * country
         */
        @JSONField(name = "Country")
        var country: String? = null

        /**
         * xzcode
         */
        @JSONField(name = "Xzcode")
        var xzcode: String? = null

        /**
         * mobile
         */
        @JSONField(name = "Mobile")
        var mobile: String? = null

        /**
         * email
         */
        @JSONField(name = "Email")
        var email: String? = null

        /**
         * fXZCode
         */
        @JSONField(name = "FXZCode")
        var fXZCode: String? = null

        /**
         * fEmployeeNumber
         */
        @JSONField(name = "FEmployeeNumber")
        var fEmployeeNumber: String? = null
    }
}