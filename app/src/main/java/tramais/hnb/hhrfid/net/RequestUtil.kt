package tramais.hnb.hhrfid.net

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import tramais.hnb.hhrfid.bean.*
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.*
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.util.PreferUtils
import tramais.hnb.hhrfid.util.TimeUtil
import tramais.hnb.hhrfid.util.Utils
import java.io.File
import java.util.*

class RequestUtil(var context: Context) {
    var titles: MutableList<String> = ArrayList()
    var img_path: MutableList<String?>? = ArrayList()
    var up_size = 0

    /*
     * 获取身份证类型
     * */
    private val list_indefiy: MutableList<String>? = ArrayList()
    private val map_indefiy: MutableMap<String, String>? = HashMap()
    fun getEntry(tag: String, paths: List<String>): String {
        val buffer = StringBuffer()
        var id = 1
        for (item in paths) {
            val i = id++
            buffer.append("$i|")
            buffer.append(titles[i - 1] + "|")
            buffer.append("$tag|")
            buffer.append("0|")
            buffer.append("0|")
            buffer.append("$item~")
        }
        return buffer.toString()
    }

    fun saveTagImgs(BaoAnNumber: String?, tag: String, imgs: List<String>?, getRtnMessage: GetRtnMessage) {
        titles.clear()
        titles.add("养殖户照")
        titles.add("理赔员照")
        titles.add("右前方照")
        titles.add("右后方照")
        titles.add("割耳近照")
        titles.add("耳标近照")
        titles.add("身份证银行卡照")
        if (imgs == null || imgs.size == 0) return
        val params = Params.createParams()
        params.add("BaoAnNumber", BaoAnNumber)
        params.add("Entrys", getEntry(tag, imgs))
        OkhttpUtil.getInstance(context).doPosts(Config.SaveChaKanEarImg, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode, message, totalNums, datas -> getRtnMessage.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }


    fun searchByLable(lable: String?, jsonObject: GetResultJsonObject) {

        val params = Params.createParams()
        params.add("CompanyNumber", companyNum)
        params.add("LblNumber", lable)
        OkhttpUtil.getInstance(context).doPosts(Config.SearchByLabel, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        jsonObject.getResult(rtnCode, message, totalNums, datas)
                    }
                })
            }

            override fun onError(e: Exception) {}
        })
    }

    fun searchByFramNum(input: String?, startTime: String, endTime: String, curentPage: Int, flag: String, getString: GetOneString) {
        val params = Params.createParams()
        val numeric = Utils.isNumeric(input)
        params.add("CompanyNumber", companyNum)
        params.add("FarmerNumber", input)
        params.add("DateFrom", "$startTime 00:00:00")
        params.add("DateTo", "$endTime 23:59:59")
        params.add("Pageindexfrom", (curentPage - 1) * 20 + 1)
        params.add("PageIndexTo", curentPage * 20 + 1)
        var url = if (flag == "养殖险") {
            Config.SearchByFarmer
        } else {
            Config.LandSearchByFarmer
        }
        OkhttpUtil.getInstance(context).doPosts(url, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getString.getString(bean.response)
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getUnderList(input: String?, pageIndex: Int, flag: String?, module_name: String, getResultJsonArarry: GetResultJsonArarry) {
        val numeric = Utils.isNumeric(input)

        val params = Params.createParams()
        params.add("Companynumber", companyNum)
        params.add("FarmerName", input)
        params.add("Mobile", "")
        params.add("SfzNumber", "")
        params.add("PageSize", 20)
        params.add("PageIndex", pageIndex)
        params.add("flag", flag)
        var isIncludeGroup = if (module_name == "养殖户登记") {
            "0"
        } else {
            "1"
        }
        params.add("isIncludeGroup", isIncludeGroup)
        OkhttpUtil.getInstance(context).doPosts(Config.getlist, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, mess: String?, totalNums: Int, datas: JSONArray? -> getResultJsonArarry.getResult(rtnCode, mess, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }
/*
    fun getUnderList(farmer: String?, getResultJsonArarry: GetResultJsonArarry) {

        val params = Params.createParams()
        params.add("Companynumber", companyNum)
        params.add("FarmerName", farmer)
        params.add("Mobile", "")
        params.add("SfzNumber", "")
        params.add("PageSize", 20)
        params.add("PageIndex", 1)
        OkhttpUtil.getInstance(context).doPosts(Config.getlist, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, mess: String?, totalNums: Int, datas: JSONArray? -> getResultJsonArarry.getResult(rtnCode, mess, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }*/

    val companyNum: String
        get() = PreferUtils.getString(context, Constants.companyNumber)
    val employNo: String
        get() = PreferUtils.getString(context, Constants.userNumber)
    val employName: String
        get() = PreferUtils.getString(context, Constants.UserName)
    val fXZCode: String
        get() = PreferUtils.getString(context, Constants.FXZCode)

    fun getIndefiyCategory(cardCategory: GetIdCardCategory) {
        list_indefiy?.clear()
        map_indefiy?.clear()

        val params = Params.createParams()
        OkhttpUtil.getInstance(context).doPosts(Config.getidentifycategory, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, mess: String?, totalNums: Int, datas: JSONArray? ->
                    val identifyCategories: List<IdentifyCategory>? = instant!!.parseCommonUseArr(datas, IdentifyCategory::class.java)
                    for (item in identifyCategories!!) {
                        if (item.identifyName != null && !list_indefiy!!.contains(item.identifyName)) list_indefiy.add(item.identifyName)
                        if (item.identifyName != null && !map_indefiy!!.containsKey(item.identifyName)) map_indefiy[item.identifyName] = item.identifyCode
                    }
                    cardCategory.getIdCard(list_indefiy, map_indefiy)
                }
            }

            override fun onError(e: Exception) {}
        })
    }

    /*
     * 获取身份证信息
     * */
    fun getIdCardsInfo(url: String?, category: String?, picString: String?, jsonObject: GetResultJsonObject) {
        val params = Params.createParams()
        params.add("ZjCategory", category)
        params.add("ZjPicture", picString)
        OkhttpUtil.getInstance(context).doPosts(url, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        jsonObject.getResult(rtnCode, message, totalNums, datas)
                    }
                })
            }

            override fun onError(e: Exception) {}
        })
    }

    fun saveAnimal(epc: String?, id_nums: String?, list: List<String>?, category: String?, date: String?, longitude: Double, latitude: Double, employeeNum: String?, companyNum: String?, getString: GetRtnMessage) {
        val url = Config.BASE_URL + Config.SaveAnimal
        var buffer1 = ""
        var buffer2 = ""
        var buffer3 = ""
        var buffer4 = ""
        if (list != null && list.isNotEmpty()) {
            if (list.size == 1) {
                buffer1 = list[0]
                buffer2 = ""
                buffer3 = ""
                buffer4 = ""
            }
            if (list.size == 2) {
                buffer1 = list[0]
                buffer2 = list[1]
                buffer3 = ""
                buffer4 = ""
            }
            if (list.size == 3) {
                buffer1 = list[0]
                buffer2 = list[1]
                buffer3 = list[2]
                buffer4 = ""
            }
            if (list.size >= 4) {
                buffer1 = list[0]
                buffer2 = list[1]
                buffer3 = list[2]
                buffer4 = list[3]
            }
        }
        val params = Params.createParams()
        params.add("LblNumber", epc)
        val com = if (companyNum.isNullOrEmpty()) {
            PreferUtils.getString(context, Constants.companyNumber)
        } else {
            companyNum
        }

        val em = if (employeeNum.isNullOrEmpty()) {
            PreferUtils.getString(context, Constants.userNumber)
        } else {
            employeeNum
        }
        params.add("CompanyNumber", com)
        params.add("FarmerNumber", id_nums)
        params.add("EmployeeNumber", em)
        params.add("DeviceNo", Build.SERIAL)
        params.add("CheckTime", TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
        params.add("LotNO", UUID.randomUUID().toString())
        params.add("ImgFile1", buffer1)
        params.add("ImgFile2", buffer2)
        params.add("ImgFile3", buffer3)
        params.add("ImgFile4", buffer4)
        params.add("Longitude", longitude)
        params.add("Latitude", latitude)
        var category_ = if (category == "藏系牦牛") {
            "藏系牦牛~IXO~990319~140248"
        } else if (category == "藏系羊") {
            "藏系羊~IXO~990320~140248"
        } else {
            category
        }
        params.add("Category", category_)
        params.add("BornDate", TimeUtil.getPastDate(date))
        OkhttpUtil.getInstance(context).doPosts(Config.SaveAnimal, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        getString.getMess(rtnCode, message)
                    }
                })
            }

            override fun onError(e: Exception) {}
        })
    }

    val userNum: String
        get() = PreferUtils.getString(context, Constants.userNumber)

/*    fun saveAnimal(farmID: String?, getString: GetOneString) {
        val list = where("FarmID =? and Statu =?", farmID, "新建").find(AnimalSaveCache::class.java)
        if (list != null && list.size > 0) {
            val finalUp_animal = intArrayOf(0)
            for (saveCache in list) {
                if (img_path != null) img_path!!.clear()
                if (path2Image(saveCache.img1) != null);
                img_path!!.add(saveCache.img1)
                if (path2Image(saveCache.img2) != null);
                img_path!!.add(saveCache.img2)
                if (path2Image(saveCache.img3) != null);
                img_path!!.add(saveCache.img3)
                if (path2Image(saveCache.img4) != null);
                img_path!!.add(saveCache.img4)
                upLoadFile(context, "耳标照片", saveCache.farmID, img_path) { list ->
                    saveAnimal(saveCache.lableNum, saveCache.farmID, list, saveCache.animalType, saveCache.ageMonth, saveCache.latitude, saveCache.longitude,saveCache.employeeNumber,saveCache.comPanyNumber,
                    ) { rtnCode: Int, message: String ->
                        if (rtnCode >= 0) {
                            val cache = AnimalSaveCache()
                            cache.isUpLoad = "1"
                            cache.statu = "在保"
                            cache.updateAll("LableNum =? and FarmID =?", saveCache.lableNum, farmID)
                        }
                        finalUp_animal[0]++
                        val lableNum = saveCache.lableNum
                        var str_show = ""
                        *//*
                                             * 回调畜种信息上传完成信息
                                             * *//*str_show = if (finalUp_animal[0] == list.size) {
                        saveCache.farmName + "畜种信息上传完成"
                    } else {
                        if (!TextUtils.isEmpty(lableNum) && lableNum!!.length >= 4) lableNum.substring(lableNum.length - 4) + message else lableNum + message
                    }
                        getString.getString(str_show)
                    }
                }
            }
        }
    }*/

    fun saveFarmer(
            AreaCode: String?, name: String, zjCategory: String?, Number: String?, ZjNumber: String?, SFZAddress: String?, BankName: String?, AccountName: String?, AccountNumber: String?, Mobile: String?, Area: String?, RaiseAddress: String?, Category: String?,
            CreateTime: String?, ZjPicture: String?, BankPicture: String?, remark: String?, updateTime: String?, backid: String?, isPoor: String,
            OverdueTime: String?, SignPicture: String?,
            FBankCode: String?, FBankRelatedCode: String?, FStartTime: String?, FEnterpriseNature: String?, FEnterpriseNatureName: String?,
            getString: GetRtnMessage,
    ) {
        val params = Params.createParams()
        params.add("Name", name)
        params.add("AreaCode", AreaCode)
        params.add("ZjCategory", zjCategory)
        params.add("Number", Number)
        params.add("ZjNumber", ZjNumber)
        params.add("SFZAddress", SFZAddress)
        params.add("BankName", BankName)
        params.add("AccountName", AccountName)
        params.add("AccountNumber", AccountNumber)
        params.add("Mobile", Mobile)
        params.add("Area", Area)
        params.add("RaiseAddress", RaiseAddress)
        params.add("Category", Category)
        params.add("CreateTime", CreateTime)
        params.add("CompanyNumber", companyNum)
        params.add("ZjPicture", ZjPicture)
        params.add("BankPicture", BankPicture)
        params.add("remark", remark)
        params.add("UpdateTime", updateTime)
        params.add("ZjBackPicture", backid)
        params.add("isPoor", isPoor)
        params.add("FEmployeeNumber", employNo)
        params.add("OverdueTime", OverdueTime)
        params.add("SignPicture", SignPicture)
        params.add("FBankCode", FBankCode)
        params.add("FBankRelatedCode", FBankRelatedCode)
        params.add("FStartTime", FStartTime)
        params.add("FEnterpriseNature", FEnterpriseNature)
        params.add("FEnterpriseNatureName", FEnterpriseNatureName)
        //FEmployeeNumber   OverdueTime
        OkhttpUtil.getInstance(context).doPosts(Config.savefarmer, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        getString.getMess(rtnCode, name + message)
                    }
                })
            }

            override fun onError(e: Exception) {}
        })
    }

    fun saveFarmer2(
            number: String?, name: String, loinaccount: String?, riskcatgory: String?, fupinnumber: String?, areacode: String?, area: String?, zjcategory: String?, zjnumber: String?, zjpicture: String?, zjbackpicture: String?,
            sfzaddress: String?, mobile: String?, ispoor: Int, raiseaddress: String?, bankname: String?,
            accountnumber: String?, accountname: String?, bankpicture: String?, bankbackpicture: String?, remark: String?,
            creattime: String?, updatetime: String?, BegindueTime: String?, OverdueTime: String?, FarmerSignature: String?, FBankCode: String?,
            FBankRelatedCode: String?, FEnterpriseNatureName: String?, FEnterpriseNature: String?,
            getString: GetRtnMessage,
    ) {
        val params = Params.createParams()
        params.add("CompanyNumber", companyNum)
        params.add("FEnterpriseNatureName", FEnterpriseNatureName)
        params.add("FEnterpriseNature", FEnterpriseNature)
        params.add("Number", number)
        params.add("Name", name)
        params.add("LoinAccount", loinaccount)
        params.add("RiskCatgory", riskcatgory)
        params.add("Catgory", "")
        params.add("FuPinNumber", fupinnumber)
        params.add("AreaCode", areacode)
        params.add("Area", area)
        params.add("ZjCategory", zjcategory)
        params.add("ZjNumber", zjnumber)
        params.add("ZjPicture", zjpicture)
        params.add("SFZAddress", sfzaddress)
        params.add("ZjBackPicture", zjbackpicture)
        params.add("Mobile", mobile)
        params.add("IsPoor", ispoor)
        params.add("RaiseAddress", raiseaddress)
        params.add("BankName", bankname)
        params.add("AccountNumber", accountnumber)
        params.add("AccountName", accountname)
        params.add("BankPicture", bankpicture)
        params.add("BankBackPicture", bankbackpicture)
        params.add("Remark", remark)
        params.add("CreatTime", creattime)
        params.add("UpdateTime", updatetime)
        params.add("BegindueTime", OverdueTime)
        params.add("OverdueTime", BegindueTime)
        params.add("FarmerSignature", FarmerSignature)
        params.add("FBankCode", FBankCode)
        params.add("FBankRelatedCode", FBankRelatedCode)
        OkhttpUtil.getInstance(context).doPosts(Config.savefarmer2, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        getString.getMess(rtnCode, name + message)
                    }
                })
            }

            override fun onError(e: Exception) {}
        })
    }

    private fun path2Image(path: String?): Bitmap? {
        if (TextUtils.isEmpty(path)) return null
        val file = File(path)
        return if (!file.exists()) null else BitmapFactory.decodeFile(path)
    }

    /*/*public string OwnAmount { get; set; }    //农户自缴金额
            public string FNationbdwf { get; set; } //中央补贴费用
            public string FProvincedwbf { get; set; } //省级补贴费用
            public string FCitydwbf { get; set; } //市级补贴部分
            public string FCountydwbf { get; set; } //县级补贴费用 */*/
    fun SaveBill(
            number: String?, date: String?, status: String?, begignDate: String?, endDate: String?, farmernumber: String?, raiseQty: String?,
            insureQty: String?, earNoStart: String?, earNoEnd: String?, unitAmout: Double, sumOunt: Double, url: String?,
            loginName: String?, time: String?, updateTime: String?, EarList: String?, RiskType: String?, lableRess: String?, FAddress2: String?, FSubsidies: String?, FOwnAmount: String?,
            FShowTime: String?, FUnitPremium: String?, FRiskRate: String?,
            FNationbdwf: String?, FProvincedwbf: String?, FCitydwbf: String?, FCountydwbf: String?,
            FCompanyDeptCode: String?, FCompanyDept: String?,
            getString: GetRtnMessage,
    ) {
        val params = Params.createParams()
        params.add("Number", number)
        params.add("Date", date)
        params.add("FCompanyDeptCode", FCompanyDeptCode)
        params.add("FCompanyDept", FCompanyDept)

        params.add("Status", status)
        params.add("BeginDate", begignDate)
        params.add("EndDate", endDate)
        params.add("Farmernumber", farmernumber)
        params.add("RaiseQty", raiseQty) //养殖数量
        params.add("InsureQty", insureQty) //承保数量
        params.add("EarNoStart", earNoStart)
        params.add("EarNoEnd", earNoEnd)
        params.add("UnitAmount", unitAmout)
        params.add("SumAmount", sumOunt)
        params.add("CompanyNumber", companyNum)
        params.add("Signature", url)
        params.add("Creator", loginName)
        params.add("CreateTime", time)
        params.add("updator", loginName)
        params.add("updateTime", updateTime)
        params.add("EarList", EarList)
        params.add("RiskType", RiskType)
        params.add("FAddress1", lableRess)
        params.add("FAddress2", FAddress2)
        params.add("FEmployeeNumber", employNo)
        params.add("FSubsidies", FSubsidies)
        params.add("FOwnAmount", FOwnAmount)
        params.add("FShowTime", FShowTime)
        if (ifNmg()) {
            params.add("FRiskRate", FRiskRate)
            params.add("FRiskRowNum", FUnitPremium)
        }
/*public string OwnAmount { get; set; }    //农户自缴金额
        public string FNationbdwf { get; set; } //中央补贴费用
        public string FProvincedwbf { get; set; } //省级补贴费用
        public string FCitydwbf { get; set; } //市级补贴部分
        public string FCountydwbf { get; set; } //县级补贴费用 */
        params.add("FNationbdwf", FNationbdwf)
        params.add("FProvincedwbf", FProvincedwbf)
        params.add("FCitydwbf", FCitydwbf)
        params.add("FCountydwbf", FCountydwbf)

        OkhttpUtil.getInstance(context).doPosts(Config.SaveBill, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        getString.getMess(rtnCode, message)
                    }
                })
            }

            override fun onError(e: Exception) {}
        })
    }

    private fun ifNmg(): Boolean {
        return Config.BASE_URL.contains(Config.nmg)
    }

    //Config.GetBill  //投保清单
    //Config.GetBaoAn// 报案列表
    fun getBill(url: String?, status: String?, farmername: String?, pageSize: Int, pageIndex: Int, FFlag: String, getResultJsonArarry: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("CompanyNumber", companyNum)
        params.add("FSearch", farmername)

        params.add("Pagesize", pageSize)
        params.add("Pageindex", pageIndex)
        params.add("Status", status)
        params.add("FFlag", FFlag)
        OkhttpUtil.getInstance(context).doPosts(url, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode, message, totalNums, datas -> getResultJsonArarry.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun SaveBaoAn(
            number: String?, farmernumber: String?, farmerName: String?, mobile: String?, insureNumber: String?, riskAddress: String?, riskDate: String?,
            baoAnDate: String?, riskReason: String?, riskQty: String?, riskProcess: String?, employeeno: String?, creator: String?, updator: String?, createTime: String?, updateTime: String?, getString: GetRtnMessage,
    ) {
        val params = Params.createParams()
        params.add("Number", number)
        params.add("CompanyNumber", companyNum)
        params.add("Farmernumber", farmernumber)
        params.add("FarmerName", farmerName)
        params.add("Mobile", mobile)
        params.add("InsureNumber", insureNumber)
        params.add("RiskAddress", riskAddress)
        params.add("RiskDate", riskDate)
        params.add("BaoAnDate", baoAnDate)
        params.add("RiskReason", riskReason)
        params.add("RiskQty", riskQty)
        params.add("RiskProcess", riskProcess)
        params.add("EmployeeName", creator)
        params.add("employeeno", employeeno)
        params.add("Creator", creator)
        params.add("Updator", updator)
        params.add("CreateTime", createTime)
        params.add("UpdateTime", updateTime)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveBaoAn, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        getString.getMess(rtnCode, message)
                    }
                })
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getRiskReason(FCategory: String, getCommon: GetCommonWithError<RiskReason>) {
        val params = Params.createParams()
        //        params.add("Ftype", "种植险");
        params.add("Fcategory", FCategory)
        OkhttpUtil.getInstance(context).doPosts(Config.GetRiskReason, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getCommon.getCommon(JSONObject.parseObject(bean.response, RiskReason::class.java))
                /*instant!!.praseAllMessageArray(bean.response)
                { rtnCode, message, totalNums, datas ->
                    getResultJsonArarry.getResult(rtnCode, message, totalNums, datas)
                }*/
            }

            override fun onError(e: Exception) {
                getCommon.getError()
            }
        })
    }


    fun getLandGrowthStage(category: String?, getResultJsonArarry: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("Fcategoryid", category)
        OkhttpUtil.getInstance(context).doPosts(Config.GetLandGrowthStage, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response, "Data") { rtnCode, message, totalNums, datas -> getResultJsonArarry.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun LandChaKanFenPei(BaoAnList: String?, FEmployeeNO: String?, FEmployeeName: String?, getOneString: GetOneString) {
        val params = Params.createParams()
        params.add("BaoAnList", BaoAnList)
        params.add("FEmployeeNO", FEmployeeNO)
        params.add("FEmployeeName", FEmployeeName)
        OkhttpUtil.getInstance(context).doPosts(Config.LandChaKanFenPei, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        getOneString.getString(message)
                    }
                })
            }

            override fun onError(e: Exception) {}
        })
    }

    fun assignBaoAn(number: String?, employeeNum: String?, employeeName: String?, getOneString: GetOneString) {
        val params = Params.createParams()
        params.add("Number", number)
        params.add("EmployeeNo", employeeNum)
        params.add("EmployeeName", employeeName)
        OkhttpUtil.getInstance(context).doPosts(Config.AssignBaoAn, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        getOneString.getString(message)
                    }
                })
            }

            override fun onError(e: Exception) {}
        })
    }

/*
    fun getEmplyeList(getResultJsonArarry: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("CompanyNumber", companyNum)
        params.add("Role", "全部")
        OkhttpUtil.getInstance(context).doPosts(Config.GetList, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode, message, totalNums, datas -> getResultJsonArarry.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }
*/

    /*
    * role  核保
    * */
    fun getEmplyeListnNew(SearchFilter: String?, Role: String?, getEmployeeList: GetEmployeeList) {
        val params = Params.createParams()
        params.add("CompanyNumber", companyNum)
        params.add("Role", Role)
        params.add("SearchFilter", SearchFilter)
        OkhttpUtil.getInstance(context).doPosts(Config.GetList, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getEmployeeList.getEmployee(JSONObject.parseObject(bean.response, EmployeeListBean::class.java))
                //   instant!!.praseAllMessageArray(bean.response) { rtnCode, message, totalNums, datas -> getResultJsonArarry.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getEntry(paths: List<String>): String {
        val buffer = StringBuffer()
        var id = 1
        for (item in paths) {
            /* val i = id++
             buffer.append("$i|")
             buffer.append("|")
             buffer.append("|")
             buffer.append("|")
             buffer.append("|")*/
            buffer.append("$item~")
        }
        return buffer.toString()
    }

    fun saveChaKan(fenPei: FenPei, date: String?, address: String?, lostRemark: String?, lostAsses: String?, checkResult: String?, creator: String?, paths: List<String>?, createTme: String?, updateTime: String?, getOneString: GetRtnMessage) {
        val params = Params.createParams()
        params.add("BaoAnNumber", fenPei.number.toString())
        params.add("INNumber", fenPei.insureNumber)
        params.add("CompanyNumber", fenPei.companyNumber)
        params.add("FarmerNumber", fenPei.farmerNumber)
        params.add("EmployeeNumber", employNo)
        params.add("ChaKanDate", date)
        params.add("RiskAddress", address)
        params.add("LostRemark", lostRemark)
        params.add("LostAssess", lostAsses)
        params.add("ChaKanResult", checkResult)
        params.add("EmployeeSign", "")
        params.add("Creator", creator)
        params.add("Updator", employNo)
        params.add("CreateTime", createTme)
        params.add("UpdateTime", updateTime)
        params.add("Entrys", "")
//        if (paths != null && paths.isNotEmpty()) params.add("Entrys", getEntry(paths))
        OkhttpUtil.getInstance(context).doPosts(Config.SaveChaKan, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode, message, totalNums, datas -> getOneString.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun saveLose(
            fenPei: FenPei, SheepSnowQty: String?, SheepIllQty: String?, SheepHarmQty: String?, SheepOtherQty: String?,
            CowSnowQty: String?, CowIllQty: String?, CowHarmQty: String?, CowOtherQty: String?, isCauseTrue: Int,
            isLableTag: Int, IsWeatherCert: Int, IsIllCert: Int, IsOtherCert: Int, creater: String?,
            createrTime: String?, updateTime: String?, getOneString: GetRtnMessage,
    ) {
        val params = Params.createParams()
        params.add("BaoAnNumber", fenPei.number)
        params.add("INNumber", fenPei.insureNumber)
        params.add("CompanyNumber", fenPei.companyNumber)
        params.add("FarmerNumber", fenPei.farmerNumber)
        params.add("EmployeeNumber", fenPei.employeeNo)
        params.add("SheepSnowQty", SheepSnowQty)
        params.add("SheepIllQty", SheepIllQty)
        params.add("SheepHarmQty", SheepHarmQty)
        params.add("SheepOtherQty", SheepOtherQty)
        params.add("CowSnowQty", CowSnowQty)
        params.add("CowIllQty", CowIllQty)
        params.add("CowHarmQty", CowHarmQty)
        params.add("CowOtherQty", CowOtherQty)
        params.add("CaseIsTrue", isCauseTrue)
        params.add("IsEarLabel", isLableTag)
        params.add("IsWeatherCert", IsWeatherCert)
        params.add("IsIllCert", IsIllCert)
        params.add("IsOtherCert", IsOtherCert)
        params.add("Creator", creater)
        params.add("CreateTime", createrTime)
        params.add("Updator", creater)
        params.add("UpdateTime", updateTime)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveLoss, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode, message, totalNums, datas -> getOneString.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun saveLossValue(
            BaoAnNumber: String?, SheepReduceAmt: String?, SheepMarketAmt: String?, SheepLPRatio: String?, SheepLPAmt: String?,
            CowReduceAmt: String?, CowMarketAmt: String?, CowLPRatio: String?, CowLpAmt: String?, CheckerSignature: String?, Updator: String?, getOneString: GetRtnMessage,
    ) {
        val params = Params.createParams()
        params.add("BaoAnNumber", BaoAnNumber)
        params.add("SheepReduceAmt", SheepReduceAmt)
        params.add("SheepMarketAmt", SheepMarketAmt)
        params.add("SheepLPRatio", SheepLPRatio)
        params.add("SheepLPAmt", SheepLPAmt)
        params.add("CowReduceAmt", CowReduceAmt)
        params.add("CowMarketAmt", CowMarketAmt)
        params.add("CowLPRatio", CowLPRatio)
        params.add("CowLpAmt", CowLpAmt)
        params.add("CheckerSignature", CheckerSignature)
        params.add("Updator", Updator)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveLossValue, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode, message, totalNums, datas -> getOneString.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getBillDetail(getResultJsonArarry: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("Company", companyNum)
        OkhttpUtil.getInstance(context).doPosts(Config.GetAllBillDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getResultJsonArarry.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getBillDetail(number: String?, getBillDetail: GetBillDetail) {
        val params = Params.createParams()
        params.add("number", number)
        OkhttpUtil.getInstance(context).doPosts(Config.GetBillDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                //  getBillDetail.getBillDetail(instant!!.praseBillDetail(bean.response))
                getBillDetail.getBillDetail(JSONObject.parseObject(bean.response, BillDetailBean::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }
/*
* GetPiccInsureBillDetail
* */

    fun getPiccInsureBillDetail(bussinessNo: String?, insuredName: String?, getBillDetail: GetInsureBillDetail) {
        val params = Params.createParams()
        params.add("bussinessNo", bussinessNo)
        params.add("insuredName", insuredName)
        OkhttpUtil.getInstance(context).doPosts(Config.GetPiccInsureBillDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getBillDetail.getBillDetail(JSONObject.parseObject(bean.response, InsureBillDetail::class.java))
                // getBillDetail.getBillDetail(instant!!.praseInsureBillDetail(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun saveLossSign(number: String?, farmerSign: String?, farmMobile: String?, employeeSign: String?, employeeMobile: String?, getOneString: GetRtnMessage) {
        val params = Params.createParams()
        params.add("BaoAnNumber", number)
        params.add("FarmerSignature", farmerSign)
        params.add("FarmerMobile", farmMobile)
        params.add("EmployeeSignature", employeeSign)
        params.add("EmployeeMobile", employeeMobile)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveLossSign, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode, message, totalNums, datas -> getOneString.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    /*
    *
    * public string FBankCode { get; set; } //如  银行英文缩写，如ICBC
            public string FBankRelatedCode { get; set; } // 银行行联号，每个支行唯一，如105338003002*/
    fun saveChakanSign(
            number: String?, sign: String?, BankName: String?, BankAccount: String?, AccountName: String?, BankPicture: String?,
            FBankCode: String?, FBankRelatedCode: String?, FarmerNumber: String?, getOneString: GetRtnMessage,
    ) {
        val params = Params.createParams()
        params.add("BaoAnNumber", number)
        params.add("EmployeeSign", sign)
        params.add("BankName", BankName)
        params.add("BankAccount", AccountName)
        params.add("AccountName", BankAccount)
        params.add("BankPicture", BankPicture)
        params.add("FBankCode", FBankCode)
        params.add("FBankRelatedCode", FBankRelatedCode)
        params.add("FarmerNumber", FarmerNumber)
        params.add("CompanyNumber", companyNum)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveChaKanSign, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode, message, totalNums, datas -> getOneString.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getChaKanDetail(number: String?, detailInf: GetCheckDetailInf) {
        val params = Params.createParams()
        params.add("BaoAnNumber", number)
        params.add("CompanyNumber", companyNum)
        OkhttpUtil.getInstance(context).doPosts(Config.GetChaKanDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                //detailInf.getDetail(instant!!.praseCheckDetail(bean.response))
                detailInf.getDetail(JSONObject.parseObject(bean.response, CheckDetail::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getLossDetail(number: String?, getLossDetailInf: GetLossDetailInf) {
        val params = Params.createParams()
        params.add("BaoAnNumber", number)
        params.add("CompanyNumber", companyNum)
        OkhttpUtil.getInstance(context).doPosts(Config.LossDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getLossDetailInf.getDetail(JSONObject.parseObject(bean.response, SunDetailBean::class.java))
                //  getLossDetailInf.getDetail(instant!!.praselossDetail(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getCompanyList(getResultJsonArarry: GetResultJsonArarry) {
        val params = Params.createParams()
        OkhttpUtil.getInstance(context).doPosts(Config.GetCompanyList, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getResultJsonArarry.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getSystemList(category: String?, getResultJsonArarry: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("category", category)
        OkhttpUtil.getInstance(context).doPosts(Config.System_GetList, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getResultJsonArarry.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }


    fun getGYArea(location: String?, filterCode: String?, getResultJsonArarry: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("CompanyNumber", companyNum)
        params.add("FilterWords", filterCode)
        params.add("Location", location)
        OkhttpUtil.getInstance(context).doPosts(Config.GetGYArea, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getResultJsonArarry.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getGuoYuan(key: String?, getOneString: GetOneString) {
        OkhttpUtil.getInstance(context).doPost1(Config.GUOYUAN, key) { str -> getOneString.getString(str) }
    }

    fun saveLand(
            farmernumber: String?, number: String?,
            name: String?, square: String?,
            cirlength: String?, cropcode: String?,
            cropname: String?, checksquare: String?,
            ownerno: String?, ownerpicture: String?,
            gis: String?, gispicture: String?,
            livepicture: String?, farmersignature: String?,
            checksignature: String?, createtime: String?,
            updatetime: String?,
            getRtnMessage: GetRtnMessage,
    ) {
        val params = Params.createParams()
        params.add("CompanyNumber", companyNum)
        params.add("FarmerNumber", farmernumber)
        params.add("Number", number)
        params.add("Name", name)
        params.add("Square", square)
        params.add("CirLength", cirlength)
        params.add("CropCode", cropcode)
        params.add("CropName", cropname)
        params.add("CheckSquare", checksquare)
        params.add("OwnerNo", ownerno)
        params.add("OwnerPicture", ownerpicture)
        params.add("GIS", gis)
        params.add("GISPicture", gispicture)
        params.add("LivePicture", livepicture)
        params.add("FarmerSignature", farmersignature)
        params.add("CheckSignature", checksignature)
        params.add("CreateTime", createtime)
        params.add("UpdateTime", updatetime)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveLand, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun saveLand(farmernumber: JSONArray?, getRtnMessage: GetRtnMessage) {
        val params = Params.createParams()
        params.add("Lands", farmernumber)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveLand2, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun saveAnimalDingSun(
            FNumber: String?, ReasonCode: String?, ReasonName: String?, FRiskQty: String?,
            FRiskPre: String?, FRiskAmount: String?, FIsTrue: String?, FIsLabel: String?,
            FCreator: String?, FFarmerSign: String?, LiPeiAnimalData: JSONArray, jsonObject: GetJsonObject,
    ) {
        val params = Params.createParams()
        params.add("FNumber", FNumber)
        params.add("ReasonCode", ReasonCode)
        params.add("ReasonName", ReasonName)
        params.add("FRiskQty", FRiskQty)
        params.add("FRiskPre", FRiskPre)
        params.add("FRiskAmount", FRiskAmount)
        params.add("FIsTrue", FIsTrue)
        params.add("FIsLabel", FIsLabel)
        params.add("FCreator", FCreator)
        params.add("FFarmerSign", FFarmerSign)
        params.add("LiPeiAnimalData", LiPeiAnimalData)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveAnimalDingSun, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                jsonObject.getJsonObject(JSON.parseObject(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    private fun landDetail(landList: List<LandDetail>?, crop_code: String): String {
        if (landList == null || landList.size == 0) return ""
        val buffer = StringBuffer()
        for (item in landList) {
            buffer.append(item.fLandNumber + "|")
            buffer.append(item.fsfzNumber + "|")
            buffer.append(item.fCompanyNumber + "|")
            buffer.append("$crop_code~")
        }
        return buffer.toString()
    }

    fun saveLandBill(
            number: String?, date: String?, farmernumber: String?, beginDate: String?,
            landAddress: String?, FAddress2: String?, endDate: String?, raiseQty: String?, insureQty: String?,
            unitAmount: String?, sumAmount: String?, landCategory: String?, landList: List<LandDetail>?, cropCode: String,
            signature: String?, Subsidies: String?, OwnAmount: String?, SealPicture: String?, creator: String?, updator: String?, createtime: String?,
            updatetime: String?, FLandCategoryId: String?, fproductCode: String?, FShowTime: String?, InsureType: String?,
            FNationbdwf: String?, FProvincedwbf: String?, FCitydwbf: String?, FCountydwbf: String?,
            FCompanyDeptCode: String?, FCompanyDept: String?,
            getRtnMessage: GetRtnMessage,
    ) {
        val params = Params.createParams()
        params.add("Number", number)
        params.add("Date", date)
        params.add("FCompanyDeptCode", FCompanyDeptCode)
        params.add("FCompanyDept", FCompanyDept)

        params.add("CompanyNumber", companyNum)
        params.add("FarmerNumber", farmernumber)
        params.add("BeginDate", beginDate)
        params.add("FAddress1", landAddress)
        params.add("FAddress2", FAddress2)
        params.add("RiskType", "种植险")
        params.add("EndDate", endDate)
        params.add("RaiseQty", raiseQty)
        params.add("InsureQty", insureQty)
        params.add("UnitAmount", unitAmount)
        params.add("SumAmount", sumAmount)
        params.add("LandCategory", landCategory)
        params.add("LandList", landDetail(landList, cropCode))
        params.add("Signature", signature)
        params.add("Subsidies", Subsidies) //财政补贴金额
        params.add("OwnAmount", OwnAmount) //农户自费金额
        params.add("SealPicture", SealPicture) //集体投保盖章确认
        params.add("InsureType", InsureType) //集体投保盖章确认
        params.add("Creator", creator)
        params.add("Updator", updator)
        params.add("CreateTime", createtime)
        params.add("UpdateTime", updatetime)
        params.add("FShowTime", FShowTime)
        params.add("FLandCategoryId", FLandCategoryId)
        params.add("fproductCode", fproductCode)
        params.add("FEmployeenumber", employNo)

        params.add("FNationbdwf", FNationbdwf)
        params.add("FProvincedwbf", FProvincedwbf)
        params.add("FCitydwbf", FCitydwbf)
        params.add("FCountydwbf", FCountydwbf)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveLandBill, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getAllLandDetail(farmNumber: String?, code: String?, CropName: String?, InsureType: String?, getRtnMessage: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("SFZNumber", farmNumber)
        params.add("CropCode", code)
        params.add("CropName", CropName)
        params.add("InsureType", InsureType)
        OkhttpUtil.getInstance(context).doPosts(Config.GetAllLandDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response, "GetAllLandList") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getLandBillDetail(number: String?, getRtnMessage: GetCropChengBaoDetail) {
        val params = Params.createParams()
        params.add("BillNo", number) //投保单号
        OkhttpUtil.getInstance(context).doPosts(Config.GetLandBillDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getRtnMessage.getBillDetail(JSONObject.parseObject(bean.response, CropCheckChengBaoBean::class.java))
                //  getRtnMessage.getBillDetail(instant!!.praseCropChengBaoDetail(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getCrop(type: String?, getRtnMessage: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("Ftype", type)
        params.add("Fcategory", "")
        OkhttpUtil.getInstance(context).doPosts(Config.GetCrop, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response, "GetCropList") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getLandInsureBillList(status: String?, search: String?, pagesize: Int, pageIndex: Int, getRtnMessage: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("FCompanyNumber", companyNum)
        params.add("FStatus", status) ///只能为1,3,5（1：新建，3：已审核，5：已公示）
        params.add("FSearch", search)
        params.add("PageSize", pagesize)
        params.add("PageIndex", pageIndex)
        OkhttpUtil.getInstance(context).doPosts(Config.GetLandInsureBillList, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response, "LandInsureBillList") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getaAnimalInsureBillList(status: String?, search: String?, pagesize: Int, pageIndex: Int, getRtnMessage: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("FCompanyNumber", companyNum)
        params.add("FStatus", status) ///只能为1,3,5（1：新建，3：已审核，5：已公示）
        params.add("FSearch", search)
        params.add("PageSize", pagesize)
        params.add("PageIndex", pageIndex)
        OkhttpUtil.getInstance(context).doPosts(Config.GetAnimalInsureBillList, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response, "AnimalInsureBillList") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun SetAnimalBillStatus(number: String?, status: String?, getRtnMessage: GetRtnMessage) {
        val params = Params.createParams()
        params.add("FNumber", number) //投保单号
        params.add("FStatus", status) //状态值（1表示新建，3表示已审核，5表示已公式）
        OkhttpUtil.getInstance(context).doPosts(Config.SetAnimalBillStatus, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        getRtnMessage.getMess(rtnCode, message)
                    }
                })
            }

            override fun onError(e: Exception) {}
        })
    }

    fun UpdateAnimalBaoAnStatus(number: String?, status: String?, getRtnMessage: GetRtnMessage) {
        val params = Params.createParams()
        params.add("FNumber", number) //投保单号
        params.add("FStatus", status) //状态值（1表示新建，3表示已审核，5表示已公式）
        OkhttpUtil.getInstance(context).doPosts(Config.UpdateAnimalBaoAnStatus, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        getRtnMessage.getMess(rtnCode, message)
                    }
                })
            }

            override fun onError(e: Exception) {}
        })
    }

    fun deleteImgs(BaoAnNumber: String?, LabelNumber: String?, getRtnMessage: GetRtnMessage) {
        val params = Params.createParams()
        params.add("BaoAnNumber", BaoAnNumber)
        params.add("LabelNumber", LabelNumber)
        OkhttpUtil.getInstance(context).doPosts(Config.DeleteChaKanEarImg, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode, message, totalNums, datas -> getRtnMessage.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun setBillStatus(number: String?, status: String?, getRtnMessage: GetRtnMessage) {
        val params = Params.createParams()
        params.add("FNumber", number) //投保单号
        params.add("FStatus", status) //状态值（1表示新建，3表示已审核，5表示已公式）
        OkhttpUtil.getInstance(context).doPosts(Config.SetBillStatus, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        getRtnMessage.getMess(rtnCode, message)
                    }
                })


            }

            override fun onError(e: Exception) {}
        })
    }

    fun saveLandBaoAn(
            number: String?, FarmerNumber: String?, FarmerName: String?, Mobile: String?, InsureNumber: String?, RiskAddress: String?,
            RiskDate: String?, BaoAnDate: String?, RiskReason: String?, FDamageQty: String?, RiskProcess: String?, EmployeeNo: String?,
            EmployeeName: String?, Creator: String?, CreateTime: String?, Updator: String?, UpdateTime: String?, FLandCategoryID: String?,
            getRtnMessage: GetRtnMessage,
    ) {
        val params = Params.createParams()
        params.add("Number", number) //保单号
        params.add("CompanyNumber", companyNum) //
        params.add("FarmerNumber", FarmerNumber) //
        params.add("FarmerName", FarmerName) //
        params.add("Mobile", Mobile) //
        params.add("InsureNumber", InsureNumber) //
        params.add("RiskAddress", RiskAddress) //
        params.add("RiskDate", RiskDate) //
        params.add("BaoAnDate", BaoAnDate) //
        params.add("RiskReason", RiskReason) //
        params.add("FLandDetail", "") //
        params.add("FDamageQty", FDamageQty) //
        params.add("RiskProcess", RiskProcess) //
        params.add("EmployeeNo", EmployeeNo) //
        params.add("EmployeeName", EmployeeName) //
        params.add("Creator", Creator) //投保单号
        params.add("CreateTime", CreateTime) //投保单号
        params.add("Updator", Updator) //投保单号
        params.add("UpdateTime", UpdateTime) //投保单号
        params.add("FLandCategoryID", FLandCategoryID) //投保单号
        OkhttpUtil.getInstance(context).doPosts(Config.SaveLandBaoAn, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun GetLandChaKanList(search: String?, pagesize: Int, pageIndex: Int, status: String, getRtnMessage: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("FCompanyNumber", companyNum)
        params.add("FSearch", search)
        params.add("PageSize", pagesize)
        params.add("PageIndex", pageIndex)
        if (status == "新建") {
            params.add("status", "未分配")
        } else if (status == "全部") {
            params.add("status", "")
        } else if (status == "已分配") {
            params.add("status", "已分配")
        }
        OkhttpUtil.getInstance(context).doPosts(Config.GetLandChaKanList, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response, "LandChaKanList") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun GetLandChaKanDet(BaoAnNumber: String?, getCropChanDetail: GetCropChanDetail) {
        val params = Params.createParams()
        params.add("CompanyNumber", companyNum)
        params.add("BaoAnNumber", BaoAnNumber)
        OkhttpUtil.getInstance(context).doPosts(Config.GetLandChaKanDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getCropChanDetail.getBillDetail(JSONObject.parseObject(bean.response, ChanKanDetailBean::class.java))
                // getCropChanDetail.getBillDetail(instant!!.praseCropChanKanDetail(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getLandChaKanDetailList(BaoAnNumber: String?, list: GetCropCheckChanKanList) {
        val params = Params.createParams()
        params.add("BaoAnNumber", BaoAnNumber)
        OkhttpUtil.getInstance(context).doPosts(Config.GetLandChaKanDetailList, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                list.getList(JSONObject.parseObject(bean.response, CropCheckChanKanList::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun SaveLandChaKan(
            FBaoAnNumber: String?, Fid: String?, FChaKanDate: String?,
            FCreator: String?, FAddress: String?,
            FRiskReason: String?, FShouZaiqty: String?, FLossRate: String?,
            FGrowthStage: String?, FLossqty: String?, FRiskProcess: String?,
            FOpintion: String?, FEmployeeSign: String?,
            FPicture: String?, maxFid: String?, FSunShiqty: String?, getRtnMessage: GetResult,
    ) {
        val params = Params.createParams()
        params.add("FBaoAnNumber", FBaoAnNumber) //报案👌
        params.add("Fid", Fid) //系号
        params.add("FChaKanDate", FChaKanDate)
        params.add("FCreator", FCreator)
        params.add("FAddress", FAddress)

        params.add("FRiskReason", FRiskReason)
        params.add("FShouZaiqty", FShouZaiqty)
        params.add("Data", FLossRate) //损失程度
        params.add("FLossRate", FLossRate) //损失程度
        params.add("FGrowthStage", FGrowthStage) //成长阶段
        params.add("FLossqty", FLossqty) //损失金额
        params.add("FRiskProcess", FRiskProcess) //出险经过描述
        params.add("FOpintion", FOpintion) //查勘意见
//FSunShiqty
        params.add("FSunShiqty", FSunShiqty)
        params.add("FEmployeeSign", FEmployeeSign) //查勘员签名
        params.add("FPicture", FPicture) //照片链接
        params.add("maxFid", maxFid) //照片链接
        OkhttpUtil.getInstance(context).doPosts(Config.SaveLandChaKan, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getRtnMessage.getResult(JSONObject.parseObject(bean.response, ResultBean::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getNewsList(getRtnMessage: GetResultJsonArarry) {
        val params = Params.createParams()
        OkhttpUtil.getInstance(context).doPosts(Config.GetNewsList, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response, "Data") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun saveFarmSign(zJnumber: String?, SignPicture: String?, time: String?, getRtnMessage: GetRtnMessage) {
        val params = Params.createParams()
        params.add("ZjNumber", zJnumber)
        params.add("SignPicture", SignPicture)
        params.add("SignTime", time)
        params.add("CompanyNumber", companyNum)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveFarmerSign, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getLandChaKanFidDetail(FBanAnNumber: String?, Fid: String?, getFidDetail: GetCommon<FidDetail>) {
        val params = Params.createParams()
        params.add("FBaoAnNumber", FBanAnNumber)
        params.add("Fid", Fid)
        OkhttpUtil.getInstance(context).doPosts(Config.GetLandChaKanFidDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getFidDetail.getCommon(JSONObject.parseObject(bean.response, FidDetail::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }


    fun SaveChouYang(
            FNumber: String?, FSampleDate: String?, FAreaQty: String?, FRiskQty: String?, FSampleResult: String?,
            FAddr: String?, FSampleType: String?, FRiskDetail: String?, FLandSet: String?,
            getRtnMessage: GetRtnMessage,
    ) {
        val params = Params.createParams()
        params.add("FNumber", FNumber)
        params.add("FSampleDate", FSampleDate)
        params.add("FAreaQty", FAreaQty)
        params.add("FRiskQty", FRiskQty)
        params.add("FAddr", FAddr)

        params.add("FSampleType", FSampleType)
        params.add("FRiskDetail", FRiskDetail)//灾害特征描述
        params.add("FSampleResult", FSampleResult)//结论
        params.add("FLandSet", FLandSet)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveChouYang, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getitem(FCategory: String?, getitemBean: GetitemBean) {
        val params = Params.createParams()
        //  params.add("FType", FType);
        params.add("Fcategoryid", FCategory)
        OkhttpUtil.getInstance(context).doPosts(Config.getItem, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getitemBean.getItemBean(JSONObject.parseObject(bean.response, ItemBean::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getDingSun(fnumber: String?, getRtnMessage: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("fnumber", fnumber)
        OkhttpUtil.getInstance(context).doPosts(Config.getDingSun, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response, "Data") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun GetDingSunlandResult(fnumber: String?, getRtnMessage: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("BaoAnNumber", fnumber)
        OkhttpUtil.getInstance(context).doPosts(Config.GetDingSunlandResult, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response, "Data") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    private fun setLand(dingSunInfoBeans: List<DingSunResultBean>): String {
        val buffer = StringBuffer()
        for (item in dingSunInfoBeans) {
            buffer.append(item.fFarmerName + "|")
            buffer.append(item.fFarmerNumber + "|")
            buffer.append(item.fInsureQty + "|")
            buffer.append(item.fRiskQty + "|")
            buffer.append(item.fLossQty + "|~")
        }
        return buffer.toString()
    }

    fun SaveDingSun(fnumber: String?, FDingSunDate: String?, FGongShiDate: String?, dingSunInfoBeans: List<DingSunResultBean>, getRtnMessage: GetRtnMessage) {
        val params = Params.createParams()
        params.add("fnumber", fnumber)
        params.add("FDingSunDate", FDingSunDate)
        params.add("FGongShiDate", FGongShiDate)
        params.add("FCreator", employName)
        params.add("FLandList", setLand(dingSunInfoBeans))
        OkhttpUtil.getInstance(context).doPosts(Config.SaveDingSun, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getMess(rtnCode, message) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getChouYang(BaoAnNumber: String?, getChouYangBean: GetCommon<ChouYangBean>) {
        val params = Params.createParams()
        params.add("BaoAnNumber", BaoAnNumber)
        OkhttpUtil.getInstance(context).doPosts(Config.getChouYang, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getChouYangBean.getCommon(JSONObject.parseObject(bean.response, ChouYangBean::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getLandBillPDFDetail(BillNo: String?, getPdfDetail: GetCommon<PDFDetailBean>) {
        val params = Params.createParams()
        params.add("BillNo", BillNo)
        OkhttpUtil.getInstance(context).doPosts(Config.getLandBillPDFDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getPdfDetail.getCommon(JSONObject.parseObject(bean.response, PDFDetailBean::class.java))
                // getPdfDetail.getPdfDetail(instant!!.prasepdfDetail(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun GetDingSunlandDetailResult(FBaoAnNumber: String?, FFarmerNumber: String?, Fid: String?, getRtnMessage: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("FBaoAnNumber", FBaoAnNumber)
        params.add("FFarmerNumber", FFarmerNumber)
        params.add("Fid", Fid)
        OkhttpUtil.getInstance(context).doPosts(Config.GetDingSunlandDetailResult, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response, "Data") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getRegion(getRegion: GetCommon<Region>) {
        val params = Params.createParams()
        params.add("FNumber", fXZCode)
        OkhttpUtil.getInstance(context).doPosts(Config.getRegion, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getRegion.getCommon(JSONObject.parseObject(bean.response, Region::class.java))
                //   getRegion.getRegion(instant!!.getRegion(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getRegionWithCache(getRegion: GetCommonWithError<Region>) {
        val params = Params.createParams()
        params.add("FNumber", fXZCode)
        OkhttpUtil.getInstance(context).doPosts(Config.getRegion, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getRegion.getCommon(JSONObject.parseObject(bean.response, Region::class.java))
                //   getRegion.getRegion(instant!!.getRegion(bean.response))
            }

            override fun onError(e: Exception) {
                getRegion.getError()
            }
        })
    }

    fun GetGongShiPDFDetail(billno: String?, gongShiPdfBeanIn: GongShiPdfBeanIn) {
        val params = Params.createParams()
        params.add("billno", billno)
        OkhttpUtil.getInstance(context).doPosts(Config.GetGongShiPDFDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                //gongShiPdfBeanIn.getGongshiPdf(instant!!.getGongShiPdfBean(bean.response))
                gongShiPdfBeanIn.getGongshiPdf(JSONObject.parseObject(bean.response, GongShiPdfBean::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun LandChaKanSunShi(FBaoAnNumber: String?, fid: String?, getRtnMessage: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("FBaoAnNumber", FBaoAnNumber)
        params.add("fid", fid)
        OkhttpUtil.getInstance(context).doPosts(Config.LandChaKanSunShi, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessageArray(bean.response, "Data") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    private fun getData(datasFinal: MutableList<ChaKanLandsBean.Data1DTO?>?): String {
        val buffer = StringBuffer()
        for (item in datasFinal!!) {
            for (item_dto in item!!.data2!!) {
                if (item_dto.fIsChecked) {
                    buffer.append(item!!.fNumber + "|")
                    buffer.append(item!!.fName + "|")
                    buffer.append(item_dto.fLandNumber + "|")
                    buffer.append(item_dto.fLandName + "|")
                    buffer.append(item_dto.fSquare + "|")
                    buffer.append(item_dto.fRiskQty + "|")
                    buffer.append(item_dto.fLossQty + "|~")
                }
            }

        }
        return buffer.toString()
    }

    fun SaveChanKanLand(FBaoAnNumber: String?, maxFid: String?, fid: String?, datasFinal: String?, getRtnMessage: GetResult) {

        val params = Params.createParams()
        params.add("FBaoAnNumber", FBaoAnNumber)
        params.add("maxFid", maxFid)
        params.add("fid", maxFid)
        params.add("data", datasFinal)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveLandChaKan, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getRtnMessage.getResult(JSONObject.parseObject(bean.response, ResultBean::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun GetAnimalInsureGongShiPDF(billno: String?, animalPdfBeanIn: GetCommon<AnimalPublicBean>) {
        val params = Params.createParams()
        params.add("billno", billno)
        var url = ""
        url = if (ifNmg()) Config.GetNMGAnimalInsureGongShiPDF else Config.GetAnimalInsureGongShiPDF
        OkhttpUtil.getInstance(context).doPosts(url, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                //  animalPdfBeanIn.getGongshiPdf(instant!!.getBean(bean.response))
                animalPdfBeanIn.getCommon(JSONObject.parseObject(bean.response, AnimalPublicBean::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    //
    fun GetAnimalLipeiPDF(FNumber: String?, pdfBeanIn: GetCommon<AnimalLiPeiPdfBean>) {
        val params = Params.createParams()
        params.add("FNumber", FNumber)
        OkhttpUtil.getInstance(context).doPosts(Config.GetAnimalLipeiPDF, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                // pdfBeanIn.getLiPeiPdf(instant!!.getLiPeiBean(bean.response))
                pdfBeanIn.getCommon(JSONObject.parseObject(bean.response, AnimalLiPeiPdfBean::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getRoler(fnumber: String?, getRtnMessage: GetResultJsonArarry) {
        val params = Params.createParams()
        params.add("fnumber", fnumber)
        OkhttpUtil.getInstance(context).doPosts(Config.getRole, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {

                instant!!.praseAllMessageArray(bean.response, "Data") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? -> getRtnMessage.getResult(rtnCode, message, totalNums, datas) }
            }

            override fun onError(e: Exception) {}
        })
    }

    /*
     * 获取银行卡信息
     * */
    fun getBankInfo(pathImgBank: String?, jsonObject: GetResultJsonObject) {
        //     val url = Config.BASE_URL + Config.bankocr
        val params = Params.createParams()
        params.add("bankPicture", pathImgBank)
        OkhttpUtil.getInstance(context).doPosts(Config.bankocr, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                instant!!.praseAllMessage(bean.response, object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        jsonObject.getResult(rtnCode, message, totalNums, datas)
                    }
                })
            }

            override fun onError(e: Exception) {}
        })
    }

    fun WapGsIListImportServlet(type: String, insurenumber: String?, jsonObject: GetJsonObject) {
        val params = Params.createParams()
        params.add("insurenumber", insurenumber)
        var url = ""
        url = if (type == "养殖险") Config.WapGsIListImportServlet else Config.CropWapGsHListImportServlet
        OkhttpUtil.getInstance(context).doPosts(url, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                jsonObject.getJsonObject(JSON.parseObject(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun PayBill(type: String?, insurenumber: String?, PayType: String?, jsonObject: GetJsonObject) {
        val params = Params.createParams()
        params.add("insurenumber", insurenumber)
        params.add("PayType", PayType)
        var url = ""
        url = if (type == "养殖险") Config.PayBill else Config.CropPayBill
        OkhttpUtil.getInstance(context).doPosts(url, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                jsonObject.getJsonObject(JSON.parseObject(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun PayBillStatus(type: String?, exchangeno: String?, jsonObject: GetJsonObject) {
        val params = Params.createParams()
        params.add("insurenumber", exchangeno)
        var url = ""
        url = if (type == "养殖险") Config.PayBillStatus else Config.CropPayBillStatus
        OkhttpUtil.getInstance(context).doPosts(url, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                jsonObject.getJsonObject(JSON.parseObject(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun PayBillCancel(type: String?, exchangeno: String?, jsonObject: GetJsonObject) {
        val params = Params.createParams()
        params.add("insurenumber", exchangeno)
        var url = ""
        url = if (type == "养殖险") Config.PayBillCancel else Config.CropPayBillCancel
        OkhttpUtil.getInstance(context).doPosts(url, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                jsonObject.getJsonObject(JSON.parseObject(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getBillCount(billCount: GetBillCount) {
        val params = Params.createParams()
        params.add("FNumber", companyNum)
        OkhttpUtil.getInstance(context).doPosts(Config.getBillCount, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                billCount.getBillCount(JSONObject.parseObject(bean.response, BillCount::class.java))
                //  val parseObject = JSONObject.parseObject(bean.response, Test::class.java)
                //jsonObject.getJsonObject(JSON.parseObject(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getVerCode(PhoneNumber: String, jsonObject: GetJsonObject) {
        val params = Params.createParams()
        params.add("PhoneNumber", PhoneNumber)
        OkhttpUtil.getInstance(context).doPosts(Config.GetVerificationCode, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                jsonObject.getJsonObject(JSON.parseObject(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun judgeVerCode(PhoneNumber: String, VerificationCode: String, jsonObject: GetJsonObject) {
        val params = Params.createParams()
        params.add("PhoneNumber", PhoneNumber)
        params.add("VerificationCode", VerificationCode)
        OkhttpUtil.getInstance(context).doPosts(Config.JudgeVerificationCode, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                jsonObject.getJsonObject(JSON.parseObject(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun resetPsw(PhoneNumber: String, NewPswd: String, jsonObject: GetJsonObject) {
        val params = Params.createParams()
        params.add("PhoneNumber", PhoneNumber)
        params.add("NewPswd", NewPswd)
        OkhttpUtil.getInstance(context).doPosts(Config.ResetPswd, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                jsonObject.getJsonObject(JSON.parseObject(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }


    fun getProductDetail(FCode: String?, FProductCode: String?, pdbbean: ProductDetailBeanIn) {
        val params = Params.createParams()
        params.add("FCode", FCode)
        params.add("FProductCode", FProductCode)
        OkhttpUtil.getInstance(context).doPosts(Config.GetProductDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                // pdbbean.getPdb(instant!!.getProductDetailBean(bean.response))
                pdbbean.getPdb(JSONObject.parseObject(bean.response, ProductDetailBean::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    /* fun getBankInfo_(getBankInfo: GetBankInfo) {
         val params = Params.createParams()
         //    params.add("BillNo", number) //投保单号
         OkhttpUtil.getInstance(context).doPosts(Config.getBankInfo, params, object : OkResponseInterface {
             override fun onSuccess(bean: HttpBean, id: Int) {
                 getBankInfo.bankInfo(JSONObject.parseObject(bean.response, BankInfo::class.java))
                 //  getRtnMessage.getBillDetail(instant!!.praseCropChengBaoDetail(bean.response))
             }

             override fun onError(e: Exception) {}
         })
     }*/

    fun getBankInfoDetail(FBackCode: String?, getBankInfo: GetBankInfo) {
        val params = Params.createParams()
        params.add("FCompanyNumber", companyNum) //投保单号
        //    params.add("FBackCode", FBackCode) //投保单号
        OkhttpUtil.getInstance(context).doPosts(Config.getBankInfoDetail, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getBankInfo.bankInfo(JSONObject.parseObject(bean.response, BankInfoDetail::class.java))
                //  getRtnMessage.getBillDetail(instant!!.praseCropChengBaoDetail(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun setLiPeiStatus(FNumber: String?, FStatus: String, getResult: GetResult) {
        val params = Params.createParams()
        params.add("FNumber", FNumber) //投保单号
        params.add("FStatus", FStatus) //投保单号
        OkhttpUtil.getInstance(context).doPosts(Config.SetLiPeiStatus, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getResult.getResult(JSONObject.parseObject(bean.response, ResultBean::class.java))
                //  getRtnMessage.getBillDetail(instant!!.praseCropChengBaoDetail(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun GetInsureLabels(FPiccPolicyNo: String?, FLabelNumbers: String, getInsure: GetInsureLable) {
        val params = Params.createParams()
        params.add("FPiccPolicyNo", FPiccPolicyNo) //投保单号
        params.add("FLabelNumbers", FLabelNumbers) //投保单号
        OkhttpUtil.getInstance(context).doPosts(Config.GetInsureLabels, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getInsure.insureLable(JSONObject.parseObject(bean.response, InsureLableBean::class.java))
                //  getRtnMessage.getBillDetail(instant!!.praseCropChengBaoDetail(bean.response))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun GetInsureFarmer(Fnumber: String?, getFarmList: GetCropFarmList) {
        val params = Params.createParams()
        params.add("Fnumber", Fnumber) //投保单号
        params.add("Fcompanynumber", companyNum) //投保单号
        OkhttpUtil.getInstance(context).doPosts(Config.GetInsureFarmer, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getFarmList.cropFarmList(JSONObject.parseObject(bean.response, CropFarmList::class.java))

            }

            override fun onError(e: Exception) {}
        })
    }

    fun saveCropBankInfo(
            Number: String?, Name: String?, Mobile: String?, BankName: String?, AccountNumber: String?,
            BankPicture: String?, SignPicture: String?, BankRelatedCode: String?, IsEffective: String?,
            FValidate: String?, getResult: GetResult,
    ) {

        val params = Params.createParams()
        params.add("CompanyNumber", companyNum)
        params.add("Number", Number)

        params.add("Name", Name)
        params.add("Mobile", Mobile)
        params.add("BankName", BankName)
        params.add("AccountNumber", AccountNumber)
        params.add("BankPicture", BankPicture)

        params.add("FValidate", FValidate)

        params.add("SignPicture", SignPicture)
        params.add("BankRelatedCode", BankRelatedCode)
        params.add("BankName", BankName)
        params.add("IsEffective", IsEffective)
        OkhttpUtil.getInstance(context).doPosts(Config.SaveFarmer3, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getResult.getResult(JSONObject.parseObject(bean.response, ResultBean::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getChaKanLands(Fnumber: String?, fid: String?, getChaKanLandsBean: GetChaKanLandsBean) {
        val params = Params.createParams()
        params.add("Fnumber", Fnumber) //投保单号
        params.add("fid", fid) //投保单号
        params.add("Fcompanynumber", companyNum)
        OkhttpUtil.getInstance(context).doPosts(Config.GetChaKanLands, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getChaKanLandsBean.getLands(JSONObject.parseObject(bean.response, ChaKanLandsBean::class.java))

            }

            override fun onError(e: Exception) {}
        })
    }

    /*
    * fnumber  //报案号
      hebaoEmployee  //核保人
    * */
    fun getNXLPAppServlet(Fnumber: String?, hebaoEmployee: String?, getCore: GetCore) {
        val params = Params.createParams()
        params.add("registNo", Fnumber) //投保单号
        params.add("hpEmployeeNo", hebaoEmployee)
        OkhttpUtil.getInstance(context).doPosts(Config.NXLPAppServlet, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getCore.getCore(JSONObject.parseObject(bean.response, SubmitCoreBean::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    /*public string filename { get; set; }
            public string longitude { get; set; }
            public string latitude { get; set; }
            public string address { get; set; }
        }*/
    fun ai(FarmerNumber: String?, filename: String?, address: String?, latitude: Double, longitude: Double, getShuShu: GetShuShu) {
        val params = Params.createParams()
        params.add("CompanyNumber", companyNum) //投保单号
        params.add("EmployeeName", employName)
        params.add("FarmerNumber", FarmerNumber) //投保单号
        params.add("filename", filename) //投保单号
        params.add("address", address)
        params.add("latitude", latitude) //投保单号
        params.add("longitude", longitude)
        OkhttpUtil.getInstance(context).doPosts(Config.AIShuoShuo, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getShuShu.getShu(JSONObject.parseObject(bean.response, ShuShu::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getLable(FInsureNumber: String?, FAreaCode: String?, FFarmerNumber: String?, FProductCode: String?, FRiskCode: String?, getLable: GetLbaleBean) {
        val params = Params.createParams()

        params.add("FCompanyNumber", companyNum) //投保单号
        params.add("FAreaCode", FAreaCode)
        params.add("FFarmerNumber", FFarmerNumber) //投保单号
        params.add("FProductCode", FProductCode) //投保单号
        params.add("FRiskCode", FRiskCode)
        params.add("FInsureNumber", FInsureNumber)
//FInsureNumber
        OkhttpUtil.getInstance(context).doPosts(Config.GetLabels, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getLable.getLanleBean(JSONObject.parseObject(bean.response, LableBean::class.java))

            }

            override fun onError(e: Exception) {}
        })
    }

    fun DeleteBill(number: String?, getResult: GetResult) {
        val params = Params.createParams()

        params.add("number", number) //投保单号

        OkhttpUtil.getInstance(context).doPosts(Config.DeleteAnimalBill, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getResult.getResult(JSONObject.parseObject(bean.response, ResultBean::class.java))

            }

            override fun onError(e: Exception) {}
        })
    }

    fun getEnterpriseNature(getNature: GetNature) {
        val params = Params.createParams()
        OkhttpUtil.getInstance(context).doPosts(Config.getEnterpriseNature, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getNature.getNature(JSONObject.parseObject(bean.response, Naturean::class.java))
            }

            override fun onError(e: Exception) {}
        })
    }

    fun getCompanyDept(getCommon: GetCommon<Dept>) {
        val params = Params.createParams()
        params.add("companynumber", companyNum)
        OkhttpUtil.getInstance(context).doPosts(Config.GetCompanyDept, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getCommon.getCommon(JSONObject.parseObject(bean.response, Dept::class.java))
            }

            override fun onError(e: Exception) {}
        })

    }

    fun address(getCommon: GetCommon<Address>) {
        val params = Params.createParams()
        params.add("FCompanyNumber", fXZCode)
        OkhttpUtil.getInstance(context).doPosts(Config.address, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                getCommon.getCommon(JSONObject.parseObject(bean.response, Address::class.java))
            }

            override fun onError(e: Exception) {}
        })

    }

    companion object {
        private var requestUtil: RequestUtil? = null

        @JvmStatic
        fun getInstance(context: Context?): RequestUtil? {
            if (requestUtil == null) {
                synchronized(OkhttpUtil::class.java) {
                    if (requestUtil == null && context != null) {
                        requestUtil = RequestUtil(context)
                    }
                }
            }
            return requestUtil
        }
    }
}