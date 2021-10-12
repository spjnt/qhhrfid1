package tramais.hnb.hhrfid.net

import android.text.TextUtils
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.apkfuns.logutils.LogUtils
import org.litepal.LitePal
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetOneString
import tramais.hnb.hhrfid.interfaces.GetTwoString
import tramais.hnb.hhrfid.litePalBean.*
import tramais.hnb.hhrfid.util.TimeUtil
import tramais.hnb.hhrfid.util.Utils

enum class SaveAllCache {
    SAVE_ALL_CACHE;


    fun billNum(getOneString: GetOneString) {
        var num = -1
        LitePal.order("creatTime desc").findAsync(BillListListCache::class.java).listen {
            if (it.size > 0) {
                val billNumber = it[0].number
                billNumber?.let {
                    val endFive = it.subSequence(billNumber.length - 5, billNumber.length)
                    num = endFive.toString().toInt() + 1
                }
            } else {
                num = 1
            }
            getOneString.getString(Utils.getTouBaonumber(num))
        }

    }

    fun baoAnNum(getOneString: GetOneString) {
        var num = -1
        LitePal.order("creatTime desc").findAsync(BaoAnListCache::class.java).listen {

            if (it.size > 0) {
                val billNumber = it[it.size - 1].number

                billNumber?.let {
                    val endFive = it.subSequence(billNumber.length - 5, billNumber.length)
                    num = endFive.toString().toInt() + 1
                }
            } else {
                num = 1
            }
            getOneString.getString(Utils.getBillnumber(num))
        }

    }
    fun saveVBillList(Number: String, Date: String, Status: String, CompanyNumber: String, Farmer: String,
                      Category: String, SumAmount: String, Creator: String, farmNumber: String) {
        var listCache = BillListListCache()
        listCache.category = Category
        listCache.date = Date
        listCache.status = Status
        listCache.companyNumber = CompanyNumber
        listCache.farmer = Farmer
        listCache.sumAmount = SumAmount.toDouble()
        listCache.creator = Creator
        listCache.creatTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
        listCache.farmerNumber = farmNumber
        if (TextUtils.isEmpty(Number)) {
            billNum(object : GetOneString {
                override fun getString(str: String?) {
                    listCache.number = str.toString()
                    listCache.save()
                }
            })
        } else {
            listCache.updateAll("Number =?", Number)
        }
    }

    /*
    * 制作投保清单
    * */
    fun saveInsureDeatail(lables:String,billNumber: String, billDate: String, upDateTime: String, status: String, category: String, beginDate: String, endDate: String, riskType: String,
                          address: String, farmName: String, farmNumber: String, zjCategory: String, zjNumber: String, are: String, raise: String, insure: String,
                          EarStartNo: String, EarEndNo: String, lable: MutableList<String?>, unitAmount: String, totalAmount: String, signature: String,sub:String,self:String,public:String,
                          FNationbdwf: String?, FProvincedwbf: String?, FCitydwbf: String?, FCountydwbf: String?,
                          getTwoString: GetTwoString) {

        var bean = AllBillDetailCache()
        bean.FNationbdwf = FNationbdwf
        bean.FProvincedwbf = FProvincedwbf
        bean.FCitydwbf = FCitydwbf
        bean.FCountydwbf = FCountydwbf
        bean.category = category
        bean.beginDate = beginDate
        bean.endDate = endDate
        bean.riskType = riskType
        bean.labelAddress = address
        bean.farmerName = farmName
        bean.farmerNumber = farmNumber
        bean.zJCategory = zjCategory
        bean.sFZNumber = zjNumber
        bean.area = are
        bean.earStartNo = EarStartNo
        bean.earEndNo = EarEndNo
        bean.raiseQty = raise.toInt()
        bean.insureQty = insure.toInt()
        bean.lable_str = lables
        bean.unitAmount = unitAmount.toDouble()
        bean.sumAmount = totalAmount.toDouble()
        bean.signature = signature
        bean.status = status
        bean.isUpLoad = "0"
        bean.fSubsidies = sub.toDouble()
        bean.fOwnAmount = self.toDouble()
        bean.publicDate = public
        var lable_list: MutableList<AllBillDetailCache.LabelsBean> = ArrayList()
        lable_list.clear()
        for (item in lable) {
            val bean = AllBillDetailCache.LabelsBean()
            bean.labelNumber = item
            lable_list.add(bean)
//            var cache1 = AnimalSaveCache()
//            cache1.isMakeDeal = true
//            cache1.updateAll("lableNum =?", item)
        }
        bean.labels = lable_list
        var lable_arr = JSONArray()
        lable_arr.clear()
        lable?.let {
            for (lable in it) {
                var json_obj = JSONObject()
                json_obj.put("labelNumber", lable)
                lable_arr.add(json_obj)
            }
        }
        bean.lable_obj = lable_arr.toJSONString()


        if (TextUtils.isEmpty(billNumber) || billNumber == null) {
            billNum { str -> //  LogUtils.e("str  " + str)
                bean.updateTime = billDate
                bean.billDate = billDate
                bean.billNumber = str
                val save = bean.save()
                getTwoString.getTwo(str, if (save) "保存成功" else "保存失败")
            }
        } else {
            bean.creatTime = upDateTime
            bean.updateTime = upDateTime
            val updateAll = bean.updateAll("BillNumber =?", billNumber)
            getTwoString.getTwo(billNumber, if (updateAll >= 0) "保存成功" else "保存失败")
        }


    }

    /*
    * 保存报案信息
    * */
    fun saveBaoAn(insureNumber: String, farmNumber: String, farmName: String, farmMobile: String, number: String,
                  insureAddress: String, insureTime: String, baoAnTime: String, insureReason: String,
                  insureNum: String, insureRemark: String, employEeName: String, employEeNum: String, status: String, getTwoString: GetTwoString) {
        val banAnInfo = BanAnInfo()
        banAnInfo.farmName = farmName
        banAnInfo.farmNumber = farmNumber
        banAnInfo.farmMobile = farmMobile
        banAnInfo.insureAddress = insureAddress
        banAnInfo.insureTime = insureTime
        banAnInfo.baoAnTime = baoAnTime
        banAnInfo.insureReason = insureReason
        banAnInfo.insureNum = insureNum
        banAnInfo.insureRemark = insureRemark
        banAnInfo.employEeName = employEeName
        banAnInfo.employEeNum = employEeNum
        banAnInfo.status = status
        banAnInfo.isUpLoad = "0"
        banAnInfo.insureNumber = insureNumber
        banAnInfo.updateTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
        if (TextUtils.isEmpty(number) || number == null) {
            banAnInfo.creatTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
            baoAnNum(object : GetOneString {
                override fun getString(str: String?) {
                    banAnInfo.number = str
                    val save = banAnInfo.save()
                    getTwoString.getTwo(str, if (save) "保存成功" else "保存失败")
                }
            })
        } else {
            val updateAll = banAnInfo.updateAll("number= ?", number)
            getTwoString.getTwo(number, if (updateAll >= 1) "更新缓存成功" else "更新缓存失败")
        }
    }


    fun saveBaoAnList(insureNumber: String?, companyNumber: String?, farmerNumber: String?, farmerName: String?, mobile: String?, number: String?, riskAddress: String?,
                      riskDate: String?, baoAnDate: String?, riskReason: String?, riskQty: String?, riskProcess: String?, employeeNo: String?, employeeName: String?) {
        var listCache = BaoAnListCache()
        listCache.companyNumber = companyNumber
        listCache.farmerNumber = farmerNumber
        listCache.farmerName = farmerName
        listCache.mobile = mobile
        listCache.insureNumber = insureNumber
        listCache.riskAddress = riskAddress
        listCache.riskDate = riskDate
        listCache.baoAnDate = baoAnDate
        listCache.riskReason = riskReason
        listCache.riskQty = riskQty
        listCache.riskProcess = riskProcess
        listCache.employeeNo = employeeNo
        listCache.employeeName = employeeName
        listCache.status = "新建"
        listCache.updateTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
        if (TextUtils.isEmpty(number) || number == null) {
            baoAnNum(object : GetOneString {
                override fun getString(str: String?) {
                    listCache.createTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
                    listCache.number = str
                    listCache.save()
                }
            })
        } else {
            listCache.updateAll("number = ?", number)
        }
    }

    fun saveCheck_info(fenPei: FenPei, checkDate: String, checkAddress: String, sunShidesc: String,
                       sunshiGuJi: String, checkAdvic: String, photoPath: MutableList<String>?, getOneString: GetOneString) {
        LitePal.where("number= ?", fenPei.number).findAsync(ChaKanCache::class.java).listen {
            var cache = ChaKanCache()
            cache.inNumber = fenPei.insureNumber
            cache.farmerNumber = fenPei.farmerNumber
            cache.companyNumber = fenPei.companyNumber
            cache.employeeNumber = fenPei.employeeNo
            cache.chakanDate = checkDate
            cache.chakanAddress = checkAddress
            cache.chakanAdvice = checkAdvic
            cache.chankandesc = sunShidesc
            cache.chankansunshi = sunshiGuJi
            cache.chankanphoto = ""
            cache.isUpLoad_basic = false
            if (it != null && it.size > 0) {
                cache.updateTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
                val updateAll = cache.updateAll("number= ?", fenPei.number)
                getOneString.getString(if (updateAll >= 1) "更新缓存成功" else "更新缓存失败")
            } else {
                cache.number = fenPei.number
                cache.creatTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
                cache.updateTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
                val save = cache.save()
                getOneString.getString(if (save) "保存成功" else "保存失败")
            }
        }
    }

    /*  private String BankName, BankAccount, AccountName, BankPicture;*/
    //开户行 账户  卡号
    fun saveCheck_sign(fenPei: FenPei, signPath: String, BankName: String, BankAccount: String, AccountName: String, BankPicture: String, getOneString: GetOneString) {
        LitePal.where("BaoAnNumber= ?", fenPei.number).findAsync(ChaKanDetailListCache::class.java).listen {
            var cache = ChaKanDetailListCache()
            cache.farmName = fenPei.farmerName
            cache.employeeSign = signPath
            cache.isUpLoad_Sign = "0"
            cache.bankAccount = BankAccount
            cache.bankName = BankName
            cache.accountName = AccountName
            cache.bankPicture = BankPicture
            if (it != null && it.size > 0) {
                val updateAll = cache.updateAll("BaoAnNumber= ?", fenPei.number)
                getOneString.getString(if (updateAll >= 1) "保存成功" else "保存失败")
            } else {
                cache.baoAnNumber = fenPei.number
                val save = cache.save()
                getOneString.getString(if (save) "保存成功" else "保存失败")
            }
        }
    }

    /* 定损---损失情况*/
    fun saveDingSun_info(fenPei: FenPei, sheep_snow: String, sheep_ill: String, sheep_harm: String, sheep_other: String,
                         cow_snow: String, cow_ill: String, cow_harm: String, cow_other: String,
                         isTruthTrue: Int, isTagTruth: Int, isWeather: Int, isIll: Int, isOther: Int, getOneString: GetOneString) {
        LitePal.where("number= ?", fenPei.number).findAsync(DingSunCache::class.java).listen {
            var cache = DingSunCache()
            cache.INNumber = fenPei.insureNumber
            cache.FarmerNumber = fenPei.farmerNumber
            cache.CompanyNumber = fenPei.companyNumber
            cache.EmployeeNumber = fenPei.employeeNo
            cache.sheep_snow = sheep_snow
            cache.sheep_ill = sheep_ill
            cache.sheep_harm = sheep_harm
            cache.sheep_other = sheep_other
            cache.cow_snow = cow_snow
            cache.cow_ill = cow_ill
            cache.cow_harm = cow_harm
            cache.cow_other = cow_other
            cache.sheep_harm = sheep_harm
            cache.isup_load_sunshi = false
            cache.isTruthTrue = isTruthTrue
            cache.isTagTruth = isTagTruth
            cache.isWeather = isWeather
            cache.isIll = isIll
            cache.isOther = isOther
            if (it != null && it.size > 0) {
                cache.updateTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
                val updateAll = cache.updateAll("number= ?", fenPei.number)
                getOneString.getString(if (updateAll >= 1) "更新缓存成功" else "更新缓存失败")
            } else {
                cache.creatTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
                cache.updateTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
                cache.number = fenPei.number
                val save = cache.save()
                getOneString.getString(if (save) "保存成功" else "保存失败")
            }
        }
    }

    /* 定损---定损情况
    * */
    fun saveDingSun_sure(fenPei: FenPei, sheep_slavge: String, sheep_market: String, sheep_ratio: String, sheep_lpr: String,
                         cow_slavge: String, cow_market: String, cow_ratio: String, cow_lpr: String, repet_sign: String, getOneString: GetOneString) {
        LitePal.where("number= ?", fenPei.number).findAsync(DingSunCache::class.java).listen {
            var cache = DingSunCache()
            cache.INNumber = fenPei.insureNumber
            cache.FarmerNumber = fenPei.farmerNumber
            cache.CompanyNumber = fenPei.companyNumber
            cache.EmployeeNumber = fenPei.employeeNo
            cache.EmployeeName = fenPei.employeeName
            cache.sheep_slavge = sheep_slavge
            cache.sheep_market = sheep_market
            cache.sheep_ratio = sheep_ratio
            cache.sheep_lpr = sheep_lpr
            cache.cow_slavge = cow_slavge
            cache.cow_market = cow_market
            cache.cow_lpr = cow_lpr
            cache.cow_ratio = cow_ratio
            cache.repet_sign = repet_sign
            cache.isup_load_dingsun = false
            if (it != null && it.size > 0) {
                val updateAll = cache.updateAll("number= ?", fenPei.number)
                getOneString.getString(if (updateAll >= 1) "更新缓存成功" else "更新缓存失败")
            } else {
                cache.number = fenPei.number
                val save = cache.save()
                getOneString.getString(if (save) "保存成功" else "保存失败")
            }
        }

    }

    /*
    * 定损---签名
    *  var farmer_sign: String? = null
        var farmer_mobile: String? = null
        var check_sign: String? = null
        var check_mobile: String? = null*/
    fun saveDingSun_sign(number: String, farmer_sign: String, farmer_mobile: String, check_sign: String, check_mobile: String, getOneString: GetOneString) {
        LitePal.where("number= ?", number).findAsync(DingSunCache::class.java).listen {
            var cache = DingSunCache()
            cache.farmer_sign = farmer_sign
            cache.farmer_mobile = farmer_mobile
            cache.check_sign = check_sign
            cache.check_mobile = check_mobile
            cache.isup_load_sign = false
            if (it != null && it.size > 0) {
                val updateAll = cache.updateAll("number= ?", number)
                getOneString.getString(if (updateAll >= 1) "更新缓存成功" else "更新缓存失败")
            } else {
                cache.number = number
                val save = cache.save()
                getOneString.getString(if (save) "保存成功" else "保存失败")
            }
        }

    }
}