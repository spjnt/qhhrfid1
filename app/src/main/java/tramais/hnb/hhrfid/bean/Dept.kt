package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

class Dept {
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

    /**
     * fCompanyNumber
     */
    @JSONField(name = "FCompanyNumber")
    var fCompanyNumber: String? = null

    class DataDTO {
        /**
         * fCompanyDept
         */
        @JSONField(name = "FCompanyDept")
        var fCompanyDept: String? = null

        /**
         * fCompanyDeptCode
         */
        @JSONField(name = "FCompanyDeptCode")
        var fCompanyDeptCode: String? = null
    }
}