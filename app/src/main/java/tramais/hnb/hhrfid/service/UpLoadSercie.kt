package tramais.hnb.hhrfid.service

import android.app.Service
import android.content.Intent
import android.os.AsyncTask
import android.os.IBinder
import android.text.TextUtils
import org.litepal.LitePal
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetList
import tramais.hnb.hhrfid.interfaces.GetOneString
import tramais.hnb.hhrfid.interfaces.GetRtnMessage
import tramais.hnb.hhrfid.litePalBean.*
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.util.UpLoadFileUtil.upLoadFile

class UpLoadSercie : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        PraseAllData().execute()
        return super.onStartCommand(intent, flags, startId)
    }

    private inner class PraseAllData : AsyncTask<Void?, Void?, Any?>() {
        override fun doInBackground(vararg params: Void?): Any? {
            /*上传农户信息 */
            upLoadFarmer { str_farm ->
                sendBroadCast(str_farm)
                if (str_farm.contains("养殖户信息上传完成")) {
                    upLoadLable { str_farm_sign ->
                        sendBroadCast(str_farm_sign)
                        /*   if (str_farm_sign.contains("养殖户签名上传完成")) {
                               *//*耳标信息*//*
                            upLoadLable { str_lable ->
                                sendBroadCast(str_lable)
                                *//* if (str_lable.equals("耳标信息上传完成"))
                                 //投保清单信息
                                     upLoadDeal { str_deal ->
                                         sendBroadCast(str_deal)
                                      *//**//*   if (str_deal.equals("投保清单上传完成"))
                                        // 报案信息
                                            upLoadBaoAn { str_baoan ->
                                                sendBroadCast(str_baoan)
                                                if (str_baoan.equals("报案信息上传完成")) {
                                                    //  查勘信息
                                                    upLoadCheck { str_check ->
                                                        sendBroadCast(str_check)
                                                        if (str_check.equals("查勘基础信息上传完成")) {
                                                            //查勘照片
                                                            upLoadChakanImgs { str_check_img ->
                                                                sendBroadCast(str_check_img)
                                                                if (str_check_img.equals("查勘耳标照片上传完成")) {
                                                                    //查勘签名
                                                                    upCheckSign { str_check_sign ->
                                                                        sendBroadCast(str_check_sign)
                                                                        if (str_check_sign.equals("查勘银行卡及签名信息上传完成")) {
                                                                            // 损失情况
                                                                            upLoadLossInfo { str_loss_info ->
                                                                                sendBroadCast(str_loss_info)
                                                                                if (str_loss_info.equals("损失情况上传完成")) {
                                                                                    // 定损情况
                                                                                    upLoadLossSure { str_loss_sure ->
                                                                                        sendBroadCast(str_loss_sure)
                                                                                        if (str_loss_sure.equals("定损情况上传完成")) {
                                                                                            //  定损签名
                                                                                            upLoadLossSign { str_loss_sign ->
                                                                                                sendBroadCast(str_loss_sign)
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }*//**//*
                                    }*//*
                            }
                        }*/
                    }

                }
            }
            return 0
        }
    }


    /*牧户信息*/
    private fun upLoadFarmer(getOneString: GetOneString) {
        LitePal.where("isUpLoad =?", "0").findAsync(FarmListCache::class.java).listen { uploadS ->
            var path: HashMap<String, String?> = HashMap()
            path.clear()
            if (uploadS != null && uploadS.size > 0) {
                for (item_ in 0 until uploadS.size) {
                    var item = uploadS[item_]
                    val idPath = item.idPath
                    val bankPath = item.bankPath
                    val zjBackPicture = item.zjBackPicture
                    path["身份证反面"] = zjBackPicture
                    path["身份证"] = idPath
                    path["银行卡"] = bankPath
                    path["签名"] = item.singPic
                    upLoadFile(this, item.zjNumber, path, GetList { id_bank_path ->
                        var id_path: String? = null
                        var id_back_path: String? = null
                        var bank_path: String? = null
                        var farmer_sign: String? = null
                        if (id_bank_path.size == path.size)
                            for (item in id_bank_path) {
                                if (!item.isNullOrEmpty())
                                    if (item.contains("sfz") && !item.contains("back")) {
                                        id_path = item
                                    } else if (item.contains("sfzback")) {
                                        id_back_path = item
                                    } else if (item.contains("bank")) {
                                        bank_path = item
                                    } else if (item.contains("sign")) {
                                        farmer_sign = item
                                    }
                            }
                        RequestUtil.getInstance(this)!!.saveFarmer("", item.name.toString(), item.zjCategory, item.number, item.zjNumber, item.sFZAddress, item.bankName,
                                item.accountName, item.accountNumber, item.mobile, item.area, item.raiseAddress, item.category, item.creatTime, id_path, bank_path, item.remark,
                                item.upDateTime, id_back_path, item.isPoor.toString(), item.overdueTime, farmer_sign, item.FBankCode, item.FBankRelatedCode, item.FStartime, item.natureCode, item.natureName) { rtnCode, str_farmer: String ->
                            if (rtnCode >= 0) {
                                getOneString.getString("养殖户信息:${item.name}上传成功")
                                val cache = FarmListCache()
                                cache.isUpLoad = "1"
                                cache.updateAll("ZjNumber =?", item.zjNumber)
                            } else {
                                getOneString.getString("养殖户信息:${item.name}上传失败")
                            }

                            if (item_ + 1 == uploadS.size) {
                                getOneString.getString("养殖户信息上传完成")
                            }
                        }
                    })
                }
            } else {
                getOneString.getString("养殖户信息上传完成")
            }

        }
    }

    private fun upLoadFarmSign(getOneString: GetOneString) {
        LitePal.where("isSingUp =?", "0").findAsync(FarmListCache::class.java).listen { uploadS ->
            //  upLoadFarmSingOneByOne(uploadS, 0, getOneString)
            var path: MutableMap<String, String?> = HashMap()
            path.clear()
            if (uploadS != null && uploadS.size > 0) {
                for (item_ in 0 until uploadS.size) {
                    var item = uploadS[item_]
                    val signPath = item.singPic

                    if (!signPath.isNullOrEmpty()) {
                        path.put("签名", signPath)
                        upLoadFile(this, item.zjNumber, path, GetList { id_bank_path ->
                            RequestUtil.getInstance(this)!!.saveFarmSign(item.zjNumber, id_bank_path[0], item.singTime) { rtnCode, str_farmer: String ->
                                if (rtnCode >= 0) {
                                    getOneString.getString("养殖户签名:${item.name}上传成功")
                                    val cache = FarmListCache()
                                    cache.isSingUp = "1"
                                    cache.updateAll("ZjNumber =?", item.zjNumber)
                                } else {
                                    getOneString.getString("养殖户签名:${item.name}上传失败")
                                }


                                if (item_ + 1 == uploadS.size) {
                                    getOneString.getString("养殖户签名上传完成")
                                }
                            }
                        })
                    } else {

                        if (item_ + 1 == uploadS.size) {
                            getOneString.getString("养殖户签名上传完成")
                        }
                    }

                }
            } else {
                getOneString.getString("养殖户签名上传完成")
            }
        }
    }


    /*耳标信息*/
    private fun upLoadLable(getOneString: GetOneString) {
        LitePal.where("isUpLoad =?", "0").findAsync(AnimalSaveCache::class.java).listen { uploadS ->
            if (uploadS != null && uploadS.size > 0) {
                var img_path: MutableList<String?> = ArrayList()


                for (item_ in 0 until uploadS.size) {
                    img_path.clear()
                    var saveCache = uploadS[item_]
                    val lableNum: String? = saveCache.lableNum
                    if (saveCache.img1 != null)
                        img_path.add(saveCache.img1)
                    if (saveCache.img2 != null)
                        img_path.add(saveCache.img2)
                    if (saveCache.img3 != null)
                        img_path.add(saveCache.img3)
                    if (saveCache.img4 != null)
                        img_path.add(saveCache.img4)
                    upLoadFile(this, "耳标照片", lableNum, img_path, GetList { list_lable ->
                        if (list_lable.size == img_path.size)
                            RequestUtil.getInstance(this)!!.saveAnimal(
                                    saveCache.lableNum, saveCache.farmID, list_lable, saveCache.animalType, saveCache.ageMonth, saveCache.latitude, saveCache.longitude, saveCache.employeeNumber, saveCache.comPanyNumber,
                            ) { rtnCode: Int, message: String ->
                                if (rtnCode >= 0) {
                                    var cache = AnimalSaveCache()
                                    cache.isUpLoad = "1"
                                    cache.statu = "在保"
                                    getOneString.getString("耳标信息:${lableNum}上传成功")
                                    cache.updateAll("LableNum =? ", lableNum)
                                } else {
                                    getOneString.getString("耳标信息:${lableNum}上传失败")
                                }

                                if (item_ + 1 == uploadS.size) {
                                    getOneString.getString("耳标信息上传完成")
                                }

                            }
                    })
                }
            } else {
                getOneString.getString("耳标信息上传完成")
            }

            // upLoadLableOneByOne(uploadS, 0, getOneString)
        }

    }

    /*投保清单*/
    private fun upLoadDeal(getOneString: GetOneString) {
        LitePal.where("isUpLoad =?", "0").findAsync(AllBillDetailCache::class.java).listen { uploadS ->
            if (uploadS.size > 0) {
                var sings: MutableMap<String, String?> = HashMap()
                for (item_ in 0 until uploadS.size) {
                    var item = uploadS[item_]
                    sings.clear()
                    sings["签名"] = item.signature.toString()
                    upLoadFile(this, item.farmerNumber, sings) {
                        RequestUtil.getInstance(this)!!.SaveBill(item.billNumber, item.beginDate, "",
                                item.beginDate, item.endDate, item.farmerNumber, item.raiseQty.toString(), item.insureQty.toString(),
                                item.earStartNo, item.earEndNo, item.unitAmount, item.sumAmount, it[0], "", item.creatTime, item.updateTime, item.lable_str, item.riskType, item.labelAddress, "", item.fSubsidies.toString(), item.fOwnAmount.toString(), item.publicDate, "", "",
                                item.FNationbdwf, item.FProvincedwbf, item.FCitydwbf, item.FCountydwbf, "", "") { rtnCode, str ->
                            if (rtnCode >= 0) {
                                getOneString.getString("投保信息:${item.billNumber}上传成功")
                                var bean = AllBillDetailCache()
                                bean.isUpLoad = "1"
                                bean.billNumber = str
                                bean.updateAll("BillNumber =?", item.billNumber)
                                if (str != null && !TextUtils.isEmpty(str)) {
                                    LitePal.where("number =?", item.billNumber).findAsync(BillListListCache::class.java).listen { list ->
                                        if (list != null && list.size > 0)
                                            list.forEach {
                                                var list_cache = BillListListCache()
                                                list_cache.number = str
                                                list_cache.updateAll("Number =?", item.billNumber)
                                            }
                                    }
                                    LitePal.where("number =?", item.billNumber).findAsync(BanAnInfo::class.java).listen { list ->
                                        if (list != null && list.size > 0)
                                            list.forEach {
                                                var list_cache = BanAnInfo()
                                                list_cache.number = str
                                                list_cache.updateAll("number =?", item.billNumber)
                                            }
                                    }
                                    LitePal.where("BaoAnNumber =?", item.billNumber).findAsync(ChaKanDetailListCache::class.java).listen { list ->
                                        if (list != null && list.size > 0)
                                            list.forEach {
                                                var list_cache = ChaKanDetailListCache()
                                                list_cache.baoAnNumber = str
                                                list_cache.updateAll("BaoAnNumber =?", item.billNumber)
                                            }
                                    }
                                    LitePal.where("baoAnNumber =?", item.billNumber).findAsync(LossListCache::class.java).listen { list ->
                                        if (list != null && list.size > 0)
                                            list.forEach {
                                                var list_cache = LossListCache()
                                                list_cache.baoAnNumber = str
                                                list_cache.updateAll("baoAnNumber =?", item.billNumber)
                                            }
                                    }

                                }
                            } else {
                                getOneString.getString("投保信息:${item.billNumber}上传失败")
                            }

                            if (item_ + 1 == uploadS.size) {
                                getOneString.getString("投保清单上传完成")
                            }
                        }
                    }
                }

            } else {
                getOneString.getString("投保清单上传完成")
            }
        }

    }

    /*报案信息*/
    private fun upLoadBaoAn(getOneString: GetOneString) {
        LitePal.where("isUpLoad =?", "0").findAsync(BanAnInfo::class.java).listen { uploadS ->
            if (uploadS.size > 0) {
                for (item_ in 0 until uploadS.size) {
                    var item = uploadS[item_]
                    if (item != null)
                        RequestUtil.getInstance(this)!!.SaveBaoAn(item.number, item.farmNumber, item.farmName, item.farmMobile, item.insureNumber, item.insureAddress, item.insureTime,
                                item.baoAnTime, item.insureReason, item.insureNum, item.insureRemark, item.employEeNum, item.employEeName, item.employEeName, item.creatTime, item.updateTime, object : GetRtnMessage {
                            override fun getMess(rtnCode: Int, message: String?) {
                                if (rtnCode >= 0) {
                                    getOneString.getString("报案信息:${item.farmName}上传成功")
                                    var info = BanAnInfo()
                                    info.isUpLoad = "1"
                                    info.updateAll("number=?", item.number)
                                } else {
                                    getOneString.getString("报案信息:${item.farmName}上传失败")
                                }

                                if (item_ + 1 == uploadS.size) {
                                    getOneString.getString("报案信息上传完成")
                                }
                            }
                        })
                }
                // upLoadBaoAnOneByOne(uploadS, 0, getOneString)
            } else {
                getOneString.getString("报案信息上传完成")
            }
        }

    }

    /*查勘信息*/
    private fun upLoadCheck(getOneString: GetOneString) {
        LitePal.where("isUpLoad_Basic =?", "0").findAsync(ChaKanDetailListCache::class.java).listen { uploadS ->
            if (uploadS.size > 0) {
                for (item_ in 0 until uploadS.size) {
                    val item = uploadS[item_]
                    if (item != null) {
                        var fenPei = FenPei()
                        fenPei.number = item.baoAnNumber
                        fenPei.insureNumber = item.inNumber
                        fenPei.companyNumber = item.companyNumber
                        fenPei.farmerNumber = item.farmerNumber
                        fenPei.employeeNo = item.employeeNumber
                        RequestUtil.getInstance(this)!!.saveChaKan(fenPei, item.chaKanDate, item.riskAddress, item.chaKanResult, item.lostAssess, item.lostAssess, "", null, item.createTime, item.updateTime) { rtnCode, message ->
                            if (rtnCode >= 0) {
                                getOneString.getString("查勘信息:${item.baoAnNumber}上传成功")
                                var cache = ChaKanDetailListCache()
                                cache.isUpLoad_Basic = "1"
                                cache.updateAll("BaoAnNumber=?", item.baoAnNumber)
                            } else {
                                getOneString.getString("查勘信息:${item.baoAnNumber}上传失败")
                            }

                            if (item_ + 1 == uploadS.size) {
                                getOneString.getString("查勘基础信息上传完成")
                            }
                        }
                    }

                }

            } else {
                getOneString.getString("查勘基础信息上传完成")
            }
        }

    }

    /*查勘照片*/
    private fun upLoadChakanImgs(getOneString: GetOneString) {
        LitePal.where("isUpLoad =?", "0").findAsync(ChanKanEntrysBean::class.java).listen { uploadS ->
            if (uploadS.size > 0) {
                var photos: MutableList<String> = ArrayList()
                for (item_ in 0 until uploadS.size) {
                    photos.clear()
                    var item = uploadS[item_]
                    LitePal.where("isUpLoad =? and lable =?", "0", item.label).findAsync(ChanKanEntrysBean::class.java).listen {
                        for (item in it) {
                            if (!item.picture.isNullOrEmpty())
                                photos.add(item.picture.toString())
                        }
                        upLoadFile(this, "耳标照片", item.label, photos, GetList {
                            if (it.size == photos.size) {
                                RequestUtil.getInstance(this)!!.saveTagImgs(item.baoAnNumber.toString(), item.label.toString(), it) { rtnCode, message ->
                                    if (rtnCode == 0) {
                                        getOneString.getString("查勘耳标照片:${item.label}上传成功")
                                        var cache = ChanKanEntrysBean()
                                        cache.isUpLoad = true
                                        cache.updateAll("label = ?", item.label)
                                    } else {
                                        getOneString.getString("查勘耳标照片:${item.label}上传失败")
                                    }

                                    if (item_ + 1 == uploadS.size) {
                                        getOneString.getString("查勘耳标照片上传完成")
                                    }
                                }
                            }
                        })
                    }
                }
//                upLoadChakanImgOneByOne(uploadS, 0, getOneString)
            } else {
                getOneString.getString("查勘耳标照片上传完成")
            }

        }
    }

    /*查勘签名*/
    private fun upCheckSign(getOneString: GetOneString) {
        LitePal.where("isUpLoad_Sign =?", "0").findAsync(ChaKanDetailListCache::class.java).listen { uploadS ->
            if (uploadS.size > 0) {
                var photo: MutableMap<String, String?> = HashMap()
                for (item_ in 0 until uploadS.size) {
                    photo.clear()
                    var item = uploadS[item_]
                    photo["签名"] = item.employeeSign.toString()
                    photo["银行卡"] = item.bankPicture.toString()
                    upLoadFile(this, item.farmerNumber, photo, GetList { list ->
                        RequestUtil.getInstance(this)!!.saveChakanSign(item.baoAnNumber, list[0], item.bankName,
                                item.bankAccount, item.accountName, list[1], "", "", "") { rtn, srt ->
                            if (rtn >= 0) {
                                getOneString.getString("查勘银行卡及签名信息:${item.baoAnNumber}上传成功")
                                var cache = ChaKanDetailListCache()
                                cache.isUpLoad_Sign = "1"
                                cache.updateAll("BaoAnNumber=?", item.baoAnNumber)
                            } else {
                                getOneString.getString("查勘银行卡及签名信息:${item.baoAnNumber}上传失败")
                            }
                            if (item_ + 1 == uploadS.size) {
                                getOneString.getString("查勘银行卡及签名信息上传完成")
                            }
                        }

                    })

                }
                //upLoadChaKanSignOneByOne(uploadS, 0, getOneString)
            } else {
                getOneString.getString("查勘银行卡及签名信息上传完成")
            }
        }
    }


    /*定损信息*/
    private fun upLoadLossInfo(getOneString: GetOneString) {
        LitePal.where("UpLoad_dingsun =?", "0").findAsync(LossListCache::class.java).listen { uploadS ->
            if (uploadS.size > 0) {
                for (item_ in 0 until uploadS.size) {
                    var item = uploadS[item_]
                    val fenPei = FenPei()
                    fenPei.number = item.baoAnNumber
                    fenPei.insureNumber = item.iNNumber
                    fenPei.companyNumber = item.companyNumber
                    fenPei.farmerNumber = item.farmerNumber
                    fenPei.employeeNo = item.employeeNumber
                    RequestUtil.getInstance(this)!!.saveLose(fenPei, item.sheepSnowQty.toString(), item.sheepIllQty.toString(), item.sheepHarmQty.toString(), item.sheepOtherQty.toString(),
                            item.cowSnowQty.toString(), item.cowIllQty.toString(), item.cowHarmQty.toString(), item.cowOtherQty.toString(), item.caseIsTrue, item.EarLabel, item.WeatherCert,
                            item.IllCert, item.OtherCert, item.employeeName, item.createTime, item.updateTime) { rtnCode, message ->
                        if (rtnCode >= 0) {
                            getOneString.getString("损失情况:${item.farmerName}上传成功")
                            var cache = LossListCache()
                            cache.UpLoad_dingsun = "1"
                            cache.updateAll("baoAnNumber=?", item.baoAnNumber)
                        } else {
                            getOneString.getString("损失情况:${item.farmerName}上传失败")
                        }
                        if (item_ + 1 == uploadS.size) {
                            getOneString.getString("损失情况上传完成")
                        }
                    }
                }
                // upLoadLossInfoOneByOne(uploadS, 0, getOneString)
            } else {
                getOneString.getString("损失情况上传完成")
            }
        }

    }


    /*损失信息*/
    private fun upLoadLossSure(getOneString: GetOneString) {
        LitePal.where("UpLoad_sunshi =?", "0").findAsync(LossListCache::class.java).listen { uploadS ->
            if (uploadS.size > 0) {
                var photo: MutableMap<String, String?> = HashMap()
                for (item_ in 0 until uploadS.size) {
                    var item = uploadS[item_]
                    photo.clear()
                    photo["签名"] = item.checkerSignature.toString()
                    upLoadFile(this, item.baoAnNumber, photo, GetList { list ->
                        RequestUtil.getInstance(this)!!.saveLossValue(item.baoAnNumber, item.sheepReduceAmt.toString(), item.sheepMarketAmt.toString(), item.sheepLPRatio.toString(), item.sheepLPAmt.toString(),
                                item.cowReduceAmt.toString(), item.cowMarketAmt.toString(), item.cowLPRatio.toString(), item.cowLpAmt.toString(), list[0], item.employeeName) { rtnCode, message ->
                            if (rtnCode >= 0) {
                                getOneString.getString("定损情况:${item.farmerName}上传成功")
                                var cache = LossListCache()
                                cache.UpLoad_sunshi = "1"
                                cache.updateAll("baoAnNumber=?", item.baoAnNumber)
                            } else {
                                getOneString.getString("定损情况:${item.farmerName}上传失败")
                            }
                            if (item_ + 1 == uploadS.size) {
                                getOneString.getString("定损情况上传完成")
                            }
                        }
                    })

                }
                //upLossSureOneByOne(uploadS, 0, getOneString)
            } else {
                getOneString.getString("定损情况上传完成")
            }
        }

    }


    /*定损签名*/
    private fun upLoadLossSign(getOneString: GetOneString) {

        LitePal.where("UpLoad_sign =?", "0").findAsync(LossListCache::class.java).listen { uploadS ->
            if (uploadS.size > 0) {
                var photo: MutableMap<String, String?> = HashMap()
                for (item_ in 0 until uploadS.size) {
                    var item = uploadS[item_]
                    photo.clear()
                    photo["签名"] = item.farmerSignature.toString()
                    photo["签名"] = item.employeeSignature.toString()
                    upLoadFile(this, item.baoAnNumber, photo, GetList { list ->
                        RequestUtil.getInstance(this)!!.saveLossSign(item.baoAnNumber, list[0], item.farmerMobile, list[1], item.employeeMobile) { rtnCode, str ->
                            if (rtnCode >= 0) {
                                getOneString.getString("定损签名:${item.farmerName}上传成功")
                                var cache = LossListCache()
                                cache.UpLoad_sign = "1"
                                cache.updateAll("baoAnNumber=?", item.baoAnNumber)
                            } else {
                                getOneString.getString("定损签名:${item.farmerName}上传失败")
                            }

                            if (item_ + 1 == uploadS.size) {
                                getOneString.getString("定损签名上传完成")
                            }
                        }
                    })
                }
                //upLossSingOneByOne(uploadS, 0, getOneString)
            } else {
                getOneString.getString("定损签名上传完成")
            }
        }

    }

    var inten: Intent? = null
    private fun sendBroadCast(string: String) {
        inten = Intent()
        inten!!.putExtra(Constants.upLoad_desc, string)
        inten!!.action = "tramais.hnb.hhrfid.service.UpLoadSercie"
        sendBroadcast(inten)
    }
}