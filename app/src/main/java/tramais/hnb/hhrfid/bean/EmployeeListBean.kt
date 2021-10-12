package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

class EmployeeListBean {
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
     * rowCount
     */
    @JSONField(name = "RowCount")
    var rowCount = 0

    class DataDTO {
        /**
         * employeeNo
         */
        @JSONField(name = "EmployeeNo")
        var employeeNo: String? = null

        /**
         * employeeName
         */
        @JSONField(name = "EmployeeName")
        var employeeName: String? = null
    }
}