package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

 class SubmitCoreBean {
    /**
     * resCode
     */
    var resCode = 0

    /**
     * msg
     */
    var msg: String? = null

    /**
     * fErrorMsg
     */
    @JSONField(name = "FErrorMsg")
    var fErrorMsg: String? = null

    /**
     * registNo
     */
    var registNo: String? = null

    /**
     * policyNo
     */
    var policyNo: String? = null

    /**
     * fCompensateNo
     */
    @JSONField(name = "FCompensateNo")
    var fCompensateNo: String? = null

    /**
     * fClaimNo
     */
    @JSONField(name = "FClaimNo")
    var fClaimNo: String? = null

    /**
     * fEndorseNo
     */
    @JSONField(name = "FEndorseNo")
    var fEndorseNo: String? = null
}