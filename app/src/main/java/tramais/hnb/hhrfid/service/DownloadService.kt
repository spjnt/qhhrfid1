package tramais.hnb.hhrfid.service

import android.app.Service
import android.content.Intent
import android.os.AsyncTask
import android.os.IBinder
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.litepal.LitePal
import org.litepal.LitePal.deleteAll
import tramais.hnb.hhrfid.bean.*
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetBankInfo
import tramais.hnb.hhrfid.interfaces.GetCommon
import tramais.hnb.hhrfid.interfaces.GetNature
import tramais.hnb.hhrfid.interfaces.OkResponseInterface
import tramais.hnb.hhrfid.litePalBean.*
import tramais.hnb.hhrfid.net.OkhttpUtil
import tramais.hnb.hhrfid.net.Params
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.util.PreferUtils
import java.util.*


class DownloadService : Service() {
    private var underWrites: List<FarmList>? = null
    private var jsonArray: JSONArray? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        downLoad_num = 0
        initData()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    var downLoad_num = 1
    private fun initData() {

        allData
        indefiyCategory
        animalCategory
        riskReason
        roles
        regions

        getBankInfos()
        getNature()
        getDept()
    }

    fun getAddress() {

        RequestUtil.getInstance(this)!!.address(object : GetCommon<Address> {
            override fun getCommon(t: Address) {

            }
        })


    }

    fun getRegion() {

        RequestUtil.getInstance(this)!!.getRegion(object : GetCommon<Region> {
            override fun getCommon(t: Region) {
//                data = t.data
//                etAreaName!!.text = t.fProvince + t.fCity + t.fCounty
            }
        })


    }

    private fun getDept() {
        RequestUtil.getInstance(this)!!.getCompanyDept(object : GetCommon<Dept> {
            override fun getCommon(t: Dept) {
                t.data.let {
                    for (item in it!!) {
                        var cache = DeptCache()
                        cache.fCompanyDept = item.fCompanyDept
                        cache.fCompanyDeptCode = item.fCompanyDeptCode
                        cache.save()
                    }
                }
            }
        })
    }

    private fun getNature() {
        RequestUtil.getInstance(this)!!.getEnterpriseNature(object : GetNature {
            override fun getNature(nature: Naturean?) {
                nature?.let {
                    for (item in it.dATA!!) {
                        var cache = NatureanCache(item.fCode.toString(), item.fName.toString())
                        cache.save()
                    }
                }
            }
        })
    }

    private fun getBankInfos() {
        RequestUtil.getInstance(this)!!.getBankInfoDetail("", object : GetBankInfo {
            override fun bankInfo(info: BankInfoDetail?) {
                val toJSONString = JSON.toJSONString(info)
                // LogUtils.e("toJSONString  $toJSONString")
                var cache = BankInfoCache()
                cache.jsonString = toJSONString
                cache.save()

                print("getBankInfos")
            }
        })
    }

    /* private fun animalInsureBillList() {
         RequestUtil.getInstance(this)!!.getaAnimalInsureBillList("", "", 1000, 1, GetResultJsonArarry { rtnCode, message, totalNums, datas ->
             if (datas == null) return@GetResultJsonArarry
             val ts: List<AnimalInsureBillList>? = instant!!.parseCommonUseArr(datas, AnimalInsureBillList::class.java)
             if (!(ts == null || ts.isEmpty())) {
                 for (animalInsureBillList in ts) {
                     val animalInsureBillListCache = AnimalInsureBillListCache()
                     animalInsureBillListCache.fFarmerNumber = animalInsureBillList.fFarmerNumber
                     animalInsureBillListCache.fNumber = animalInsureBillList.fNumber
                     animalInsureBillListCache.fCreator = animalInsureBillList.fCreator
                     animalInsureBillListCache.fLabelAddress = animalInsureBillList.fLabelAddress
                     animalInsureBillListCache.fCreateTime = animalInsureBillList.fCreateTime
                     animalInsureBillListCache.fUpdateTime = animalInsureBillList.fUpdateTime
                     animalInsureBillListCache.fStatus = animalInsureBillList.fStatus
                     animalInsureBillListCache.fItemName = animalInsureBillList.fItemName
                     animalInsureBillListCache.fRiskCode = animalInsureBillList.fRiskCode
                     animalInsureBillListCache.fproductCode = animalInsureBillList.fproductCode
                     val fStatus = animalInsureBillList.fStatus
                     if (!(fStatus == null || fStatus.isEmpty())) {
                         if (animalInsureBillList.fStatus == "公示" || animalInsureBillList.fStatus == "同步") {
                             animalInsureBillListCache.statusCode = "5"
                         } else if (animalInsureBillList.fStatus == "缴费" || animalInsureBillList.fStatus == "已缴费") {
                             animalInsureBillListCache.statusCode = "9"
                         }
                     }
                     animalInsureBillListCache.fname = animalInsureBillList.fname
                     animalInsureBillListCache.fShowTime = animalInsureBillList.fShowTime
                     animalInsureBillListCache.isUpLoad = true
                     animalInsureBillListCache.save()
                 }
                 //downLoad_num++
                 print("animalInsureBillList")
             } else {
                 // downLoad_num++
                 print("animalInsureBillList")
             }
         })
     }
 */
    private val regions: Unit
        private get() {
            deleteAll(RegionCache::class.java)
            val params = Params.createParams()
            params.add("fnumber", PreferUtils.getString(this, Constants.FXZCode))
            OkhttpUtil.getInstance(this).doPosts(Config.getRegion, params, object : OkResponseInterface {
                override fun onSuccess(bean: HttpBean, id: Int) {
                    val region = instant!!.getRegion(bean.response)
                    val cache = RegionCache()
                    cache.fCity = region!!.fCity
                    cache.fCounty = region.fCounty
                    cache.code = region.code
                    cache.fNumber = region.fNumber
                    cache.fProvince = region.fProvince
                    cache.msg = region.msg
                    if (JSONObject.parseObject(bean.response).containsKey("Data")) {
                        val data_arr = JSONObject.parseObject(bean.response).getJSONArray("Data").toJSONString()
                        //  LogUtils.e("data_arr$data_arr")
                        cache.data_arr = data_arr
                    }
                    val data = region.data
                    val datas: MutableList<RegionCache.DataBean> = ArrayList()
                    datas.clear()
                    if (data != null && data.isNotEmpty()) {
                        for (item in data) {
                            val dataBean = RegionCache.DataBean()
                            dataBean.fRegionNumber = item.fRegionNumber
                            dataBean.fTown = item.fTown
                            dataBean.fVillage = item.fVillage
                            datas.add(dataBean)
                        }
                        cache.data = datas
                        val save = cache.save()
                    }


                    // downLoad_num++
                    print("getRegion")
                }

                override fun onError(e: Exception) {
                    //  downLoad_num++
                    print("getRegion")
                }
            })
        }
    private val roles: Unit
        private get() {
            deleteAll(RoleCache::class.java)
            RequestUtil.getInstance(this)!!.getRoler(PreferUtils.getString(this, Constants.account)) { rtnCode, message, totalNums, datas ->
                if (datas != null && datas.size > 0) {
                    val cache = RoleCache()
                    cache.json = datas.toJSONString()
                    cache.save()

                    // downLoad_num++
                    print("roles")
                } else {
                    // downLoad_num++
                    print("roles")
                }
            }
        }

    /* private val cropType: Unit
         private get() {
             deleteAll(CropTypeCache::class.java)
             RequestUtil.getInstance(this)!!.getCrop("种植险") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
                 if (datas != null && datas.size > 0) {
                     val cropTypeLists: List<CropTypeList>? = instant!!.parseCommonUseArr(datas, CropTypeList::class.java)
                     for (item in cropTypeLists!!) {
                         val cache = CropTypeCache()
                         cache.bxfl = item.bxfl
                         cache.dwbe = item.dwbe
                         cache.dwbf = item.dwbf
                         cache.fCropCode = item.fCropCode
                         cache.fCropName = item.fCropName
                         cache.fproductCode = item.fproductCode
                         cache.fClauseCode = item.fClauseCode
                         cache.grdwbf = item.grdwbf
                         cache.nationbdwf = item.nationbdwf
                         cache.provincedwbf = item.provincedwbf
                         cache.citydwbf = item.citydwbf
                         cache.countydwbf = item.countydwbf
                         cache.save()
                     }
                     // downLoad_num++
                     print("cropType")
                 } else {
                     //  downLoad_num++
                     print("cropType")
                 }
             }
         }*/
    /*  private val billDetail: Unit
          private get() {
              RequestUtil.getInstance(this)!!.getBillDetail { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
                  if (datas != null && datas.size > 0) {
                      val allBillDetals: List<AllBillDetal>? = instant!!.parseCommonUseArr(datas, AllBillDetal::class.java)
                      for (item in allBillDetals!!) {
                          where("BillNumber=?", item.billNumber).findAsync(AllBillDetailCache::class.java).listen { list: List<AllBillDetailCache?>? ->
                              if (list == null || list.isEmpty()) {
                                  val cache = AllBillDetailCache()
                                  cache.area = item.area

                                  val labels = item.labels
                                  var lable_arr = JSONArray()
                                  labels?.let {
                                      for (lable in it) {
                                          var json_obj = JSONObject()
                                          json_obj.put("labelNumber", lable.labelNumber)
                                          lable_arr.add(json_obj)
                                      }
                                  }
                                  cache.lable_obj = lable_arr.toJSONString()
                                  val farmer = item.farmer
                                  var json_arr = JSONArray()
                                  farmer?.let {
                                      for (item in it) {
                                          var json_obj = JSONObject()
                                          json_obj.put("Fname", item.fname)
                                          json_obj.put("FSfzNumber", item.fSfzNumber)
                                          json_arr.add(json_obj)
                                      }
                                  }
                                  cache.fRiskRate = item.fRiskRate
                                  cache.FNationbdwf = item.FNationbdwf
                                  cache.FProvincedwbf = item.FProvincedwbf
                                  cache.FCitydwbf = item.FCitydwbf
                                  cache.FCountydwbf = item.FCountydwbf
                                  cache.farmer_obj = json_arr.toJSONString()
                                  // cache.labels = labels_cache
                                  cache.beginDate = item.beginDate
                                  cache.endDate = item.endDate
                                  cache.billNumber = item.billNumber
                                  cache.billDate = item.billDate
                                  cache.status = item.status
                                  cache.category = item.category
                                  cache.labelAddress = item.labelAddress
                                  cache.riskType = item.riskType
                                  cache.farmerNumber = item.farmerNumber
                                  cache.farmerName = item.farmerName
                                  cache.zJCategory = item.zJCategory
                                  cache.sFZNumber = item.sFZNumber
                                  cache.raiseQty = item.raiseQty
                                  cache.insureQty = item.insureQty
                                  cache.earStartNo = item.earStartNo
                                  cache.earEndNo = item.earEndNo
                                  cache.unitAmount = item.unitAmount
                                  cache.sumAmount = item.sumAmount
                                  cache.signature = item.signature
                                  cache.fSubsidies = item.fSubsidies
                                  cache.fOwnAmount = item.fOwnAmount
                                  cache.isUpLoad = "1"
                                  cache.save()
                              }


                          }
                      }
                      //downLoad_num++
                      print("billDetail")
                  } else {
                      //  downLoad_num++
                      print("billDetail")
                  }
              }
          }
  */
    /*  private val baoAnList: Unit
          private get() {
              RequestUtil.getInstance(this)!!.getBill(Config.GetBaoAn, "", "", 3000, 1, "") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
                  if (datas != null && datas.size > 0) {
                      val touBaoBeans: List<FenPei>? = instant!!.parseCommonUseArr(datas, FenPei::class.java)
                      if (touBaoBeans!!.isNotEmpty())
                          for (item in touBaoBeans) {
                              where("number =?", item.number).findAsync(BaoAnListCache::class.java).listen { list: List<BaoAnListCache?>? ->
                                  if (list == null || list.size == 0) {
                                      val cache = BaoAnListCache()
                                      cache.number = item.number
                                      cache.companyNumber = item.companyNumber
                                      cache.farmerName = item.farmerName
                                      cache.farmerNumber = item.farmerNumber
                                      cache.mobile = item.mobile
                                      cache.insureNumber = item.insureNumber
                                      cache.riskAddress = item.riskAddress
                                      cache.riskDate = item.riskDate
                                      cache.riskProcess = item.riskProcess
                                      cache.riskQty = item.riskQty
                                      cache.riskReason = item.riskReason
                                      cache.status = item.status
                                      cache.employeeName = item.employeeName
                                      cache.employeeNo = item.employeeNo
                                      cache.baoAnDate = item.baoAnDate
                                      cache.save()
                                  }
                              }
                          }
                      // downLoad_num++
                      print("GetBaoAn")
                  } else {
                      // downLoad_num++
                      print("GetBaoAn")
                  }
              }
          }*/
    /*  private val billList: Unit
          private get() {
              deleteAll(BillListListCache::class.java)
              RequestUtil.getInstance(this)!!.getBill(Config.GetBill, "1", "", 3000, 1, "") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
                  if (datas != null && datas.size > 0) {
                      val touBaoBeans: List<TouBaoBean>? = instant!!.parseCommonUseArr(datas, TouBaoBean::class.java)
                      if (touBaoBeans!!.isNotEmpty()) for (item in touBaoBeans) {
                          val cache = BillListListCache()
                          cache.category = item.category
                          cache.companyNumber = item.companyNumber
                          cache.creator = item.creator
                          cache.date = item.date
                          cache.farmer = item.farmer
                          cache.number = item.number
                          cache.status = item.status
                          cache.sumAmount = item.sumAmount
                          cache.farmerNumber = item.farmerNumber
                          cache.area = item.area
                          cache.save()

                      }
                      // downLoad_num++
                      print("GetBill")
                  } else {
                      //  downLoad_num++
                      print("GetBill")
                  }
              }
          }*/
    private val allData: Unit
        private get() {
            val params = Params.createParams()
            params.add("Companynumber", companyNum)
            params.add("FarmerName", "")
            params.add("Mobile", "")
            params.add("SfzNumber", "")
            params.add("PageSize", 3000)
            params.add("PageIndex", 1)
            params.add("isIncludeGroup", "0")
            OkhttpUtil.getInstance(this).doPosts(Config.getlist, params, object : OkResponseInterface {
                override fun onSuccess(bean: HttpBean, id: Int) {
                    instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, mess: String?, totalNums: Int, datas: JSONArray? ->
                        jsonArray = datas
                        PraseAllData().execute()
                    }
                }

                override fun onError(e: Exception) {
                    //   downLoad_num++
                    print("saveCache")
                }
            })
        }

    private fun saveCache(underWrites: List<FarmList>?) {
        LitePal.deleteAll(FarmListCache::class.java)
        if (underWrites != null && underWrites.isNotEmpty()) {
            for (item in underWrites) {
                //   where("ZjNumber =?", item.zjNumber).findAsync(FarmListCache::class.java).listen { list: List<FarmListCache?>? ->
                //  if (list == null || list.isEmpty()) {
                val cache = FarmListCache()
                cache.accountName = item.accountName
                cache.accountNumber = item.accountNumber
                cache.area = item.area
                cache.bankName = item.bankName
                cache.category = item.category
                cache.mobile = item.mobile
                cache.name = item.name
                cache.number = item.number
                cache.raiseAddress = item.raiseAddress
                cache.sFZAddress = item.sfzAddress
                cache.zjCategory = item.zjCategory
                cache.zjNumber = item.zjNumber
                cache.creatTime = item.createTime
                cache.isUpLoad = "1"
                cache.isPoor = item.isPoor.toString()
                cache.singPic = item.signPicture
                cache.overdueTime = item.fValidate
                cache.save()
                //   }
                //  }
            }
            // downLoad_num++
            print("saveCache")
        } else {
            //  downLoad_num++
            print("saveCache")
        }
    }

    private val indefiyCategory: Unit
        get() {
            deleteAll(IdCategoryCache::class.java)

            val params = Params.createParams()
            OkhttpUtil.getInstance(this).doPosts(Config.getidentifycategory, params, object : OkResponseInterface {
                override fun onSuccess(bean: HttpBean, id: Int) {
                    instant!!.praseAllMessageArray(bean.response) { rtnCode: Int, mess: String?, totalNums: Int, datas: JSONArray? ->
                        if (datas != null && datas.size > 0) {
                            val identifyCategories: List<IdentifyCategory>? = instant!!.parseCommonUseArr(datas, IdentifyCategory::class.java)
                            for (item in identifyCategories!!) {
                                val idCategory = IdCategoryCache()
                                idCategory.identifyCode = item.identifyCode
                                idCategory.identifyName = item.identifyName
                                idCategory.save()
                            }
                            //   downLoad_num++
                            print("indefiyCategory")
                        }
                    }
                }

                override fun onError(e: Exception) {
                    //  downLoad_num++
                    print("indefiyCategory")
                }
            })
        }
    val companyNum: String
        get() = PreferUtils.getString(this, Constants.companyNumber)
    val animalCategory: Unit
        get() {
            deleteAll(AnimalCateGoryCache::class.java)
            RequestUtil.getInstance(this)!!.getCrop("养殖险") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
                if (datas != null && datas.size > 0) {
                    val cropTypeLists: List<CropTypeList>? = instant!!.parseCommonUseArr(datas, CropTypeList::class.java)
                    if (cropTypeLists != null && cropTypeLists.isNotEmpty()) {
                        for (item in cropTypeLists) {
                            val animalCategory = AnimalCateGoryCache()
                            animalCategory.bxfl = item.bxfl
                            animalCategory.dwbe = item.dwbe
                            animalCategory.dwbf = item.dwbf
                            animalCategory.fClauseCode = item.fClauseCode
                            animalCategory.fCropCode = item.fCropCode
                            animalCategory.fCropName = item.fCropName
                            animalCategory.fproductCode = item.fproductCode
                            animalCategory.grdwbf = item.grdwbf
                            animalCategory.save()
                        }
                        //  downLoad_num++
                        print("getCrop")
                    }

                } else {
                    // downLoad_num++
                    print("getCrop")
                }
            }
        }

    private val riskReason: Unit
        private get() {
            deleteAll(RiskReasonCache::class.java)
            RequestUtil.getInstance(this)!!.getRiskReason("", object : GetCommon<RiskReason> {
                override fun getCommon(t: RiskReason) {
                    val data = t.data

                    if (data != null && data.isNotEmpty()) {
                        for (item in data) {
                            val cache = RiskReasonCache()
                            val reasonName = item.reasonName
                            cache.reasonName = reasonName
                            cache.reasonCode = item.reasonCode
                            cache.fcategory = item.fCategory
                            cache.save()
                        }
                        print("getRiskReason")
                    } else {
                        print("getRiskReason")
                    }

                }

            })
        }

    private inner class PraseAllData : AsyncTask<Void?, Void?, Any?>() {
        override fun doInBackground(vararg params: Void?): Any? {
            if (jsonArray != null && jsonArray!!.size > 0) {
                underWrites = instant!!.parseCommonUseArr(jsonArray, FarmList::class.java)
                saveCache(underWrites)
            }
            return null
        }

    }

    var inten: Intent? = null
    fun print(url: String) {
        downLoad_num++
//        LogUtils.e("downLoad_num  $downLoad_num  $url")
        inten = Intent()
        inten!!.putExtra(Constants.DownLoad_desc, downLoad_num)
        inten!!.action = "tramais.hnb.hhrfid.service.DownLoadService"
        sendBroadcast(inten)
    }
}