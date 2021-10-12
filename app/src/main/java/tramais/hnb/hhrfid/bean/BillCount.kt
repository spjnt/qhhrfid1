package tramais.hnb.hhrfid.bean


import com.alibaba.fastjson.annotation.JSONField

 class BillCount{
    @JSONField(name = "Code")
    var code: Int = 0

    @JSONField(name = "insurebill_cou")
    var insurebillCou: String?=null
    @JSONField(name = "landbaoan_cou")
    var landbaoanCou: String?=null
    @JSONField(name = "landbill_cou")
    var landbillCou: String?=null
    @JSONField(name = "landchakan_cou")
    var landchakanCou: String?=null
    @JSONField(name = "lipeibaoan_cou")
    var lipeibaoanCou: String?=null
    @JSONField(name = "lipeichakan_cou")
    var lipeichakanCou: String?=null
    @JSONField(name = "Msg")
    var msg: String?=null

    @JSONField(name = "animalsendbill_cou")
    var animalsendbill_cou: String?=null
    @JSONField(name = "landsendbill_cou")
    var landsendbill_cou: String?=null
 }

