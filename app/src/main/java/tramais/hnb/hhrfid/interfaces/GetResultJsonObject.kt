package tramais.hnb.hhrfid.interfaces

import com.alibaba.fastjson.JSONObject

interface GetResultJsonObject {
    fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?)
}