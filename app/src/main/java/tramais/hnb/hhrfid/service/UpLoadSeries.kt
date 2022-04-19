package tramais.hnb.hhrfid.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import tramais.hnb.hhrfid.bean.MessageEvent
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetList
import tramais.hnb.hhrfid.interfaces.GetOneString
import tramais.hnb.hhrfid.litePalBean.AnimalSaveCache
import tramais.hnb.hhrfid.litePalBean.FarmListCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.util.EasyExecutor
import tramais.hnb.hhrfid.util.UpLoadFileUtil.upLoadFile
import java.io.File
import java.util.concurrent.Executors

class UpLoadSeries : Service()/*, CoroutineScope by MainScope()*/ {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        index_ = 0
        load()
        return super.onStartCommand(intent, flags, startId)
    }


    fun load() {
        upLoadFarmer { str_farm ->
            sendBroadCast(str_farm)
            if (str_farm.contains("养殖户信息上传完成")) {
                upLoadLable { str_farm_sign ->
                    sendBroadCast(str_farm_sign)
                }
            }
        }
    }

    var index_ = 0
    private inline fun <T> Iterable<T>.mapMultithreading(nThreads: Int = 5, crossinline funWorker: (T) -> Any?) {
        index_ = 0
        val coroutineDispatcher = Executors.newFixedThreadPool(nThreads).asCoroutineDispatcher()
        runBlocking {
            coroutineDispatcher.use {
                launch {
                    forEachIndexed { index, arg ->
                        async(coroutineDispatcher) {
                            funWorker(arg).let {
                                index_ += 1
                                EventBus.getDefault().post(MessageEvent(index_.toString()))
                            }
                        }
                    }
                }.join()
            }
            coroutineDispatcher.close()
        }
    }

    /*牧户信息*/
    private fun upLoadFarmer(getOneString: GetOneString) {
        j = 0
        LitePal.where("isUpLoad =?", "0").findAsync(FarmListCache::class.java).listen { uploadS ->
            val path: HashMap<String, String?> = HashMap()
            path.clear()
            if (uploadS != null && uploadS.size > 0) {
                executor.onProgressChanged { current, total ->
                  //  EventBus.getDefault().post(MessageEvent("$current"))
                }.execute { notifier ->
                    val total = uploadS.size.toLong() - 1
                    for (item in 0 until uploadS.size) {
                        notifier.progressChanged(total - item.toLong(), total)
                        val animalSaveCache = uploadS[item]
                        upLoadFarmOne(uploadS, animalSaveCache, getOneString)
                    }
                }

               /* uploadS.mapMultithreading {
                    upLoadFarmOne(uploadS, it, getOneString)
                }*/
            } else {
                getOneString.getString("养殖户信息上传完成")
            }
        }
    }

    fun upLoadFarmOne(uploadS: MutableList<FarmListCache>, farmListCache: FarmListCache, getOneString: GetOneString) {
        val path: HashMap<String, String?> = HashMap()
        path.clear()
        //  if (uploadS != null && uploadS.size > 0) {
        //   for (item_ in 0 until uploadS.size) {
        val item = farmListCache
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
                j += 1
                if (j == uploadS.size) {
                    getOneString.getString("养殖户信息上传完成")
                }
            }
        })
        //  }
        /*} else {
            getOneString.getString("养殖户信息上传完成")
        }*/
    }

    var j = 0
    fun upLoadLableOneByOne(uploadS: MutableList<AnimalSaveCache>, animalSaveCache: AnimalSaveCache, getOneString: GetOneString) {
        val img_path: MutableList<String?> = ArrayList()
        val saveCache = animalSaveCache
        img_path.clear()
        val lableNum: String? = saveCache.lableNum
        if (lableNum.isNullOrEmpty()) {
            saveCache.isUpLoad = "1"
            saveCache.updateAll("creatTime =? ", saveCache.creatTime)
            return
        }
        if (saveCache.img1 != null) {
            val file = saveCache.img1
            if (!file.isNullOrEmpty() && File(file).isFile)
                img_path.add(file)
        }

        if (saveCache.img2 != null) {
            val file = saveCache.img2
            if (!file.isNullOrEmpty() && File(file).isFile)
                img_path.add(file)
        }

        if (saveCache.img3 != null) {
            val file = saveCache.img3
            if (!file.isNullOrEmpty() && File(file).isFile)
                img_path.add(file)
        }
        if (saveCache.img4 != null) {
            val file = saveCache.img4
            if (!file.isNullOrEmpty() && File(file).isFile)
                img_path.add(file)
        }
        if (img_path.size == 0) {
            j += 1
            if (j == uploadS.size) {
                getOneString.getString("耳标信息上传完成")
            } else {
                getOneString.getString("农户:${saveCache.farmName},耳标号:${lableNum}照片缺失")
            }
        } else {
            //  Thread {
            upLoadFile(this, "耳标照片", lableNum, img_path, GetList { list_lable ->
                if (list_lable.size == img_path.size)
                    RequestUtil.getInstance(this)!!.saveAnimal(
                            saveCache.lableNum, saveCache.farmID, list_lable, saveCache.animalType, saveCache.ageMonth, saveCache.latitude, saveCache.longitude, saveCache.employeeNumber, saveCache.comPanyNumber,
                    ) { rtnCode: Int, message: String ->

                        if (rtnCode >= 0) {
                            val cache = AnimalSaveCache()
//                            cache.isUpLoad = "1"
                            cache.statu = "在保"
                            getOneString.getString("耳标信息:${lableNum}上传成功")
                            cache.updateAll("LableNum =? ", lableNum)
                        } else {
                            getOneString.getString("农户:${saveCache.farmName},耳标信息:${lableNum}上传失败")
                        }

                        j += 1
                        if (j == uploadS.size) {
                            getOneString.getString("耳标信息上传完成")
                        }
                    }
            })
            //   }.start()

        }

    }


    /*耳标信息*/
    fun upLoadLable(getOneString: GetOneString) {
        j = 0
        /*.where("isUpLoad =?", "0")*/
        LitePal.where("isUpLoad =?", "0").findAsync(AnimalSaveCache::class.java).listen { uploadS ->
            if (uploadS != null && uploadS.size > 0) {
                executor.onProgressChanged { current, total ->
                   // EventBus.getDefault().post(MessageEvent("$current"))
                }.execute { notifier ->
                    val total = uploadS.size.toLong() - 1
                    for (item in 0 until uploadS.size) {
                        notifier.progressChanged(total - item.toLong(), total)
                        val animalSaveCache = uploadS[item]
                        upLoadLableOneByOne(uploadS, animalSaveCache, getOneString)
                    }
                }

                /*   val threadCount: Int = when {
                       uploadS.size <= 200 -> {
                           8
                       }
                       uploadS.size in 201..500 -> {
                           10
                       }
                       uploadS.size > 500 -> {
                           15
                       }
                       else -> {
                           15
                       }
                   }
                   uploadS.mapMultithreading(threadCount) {
                       upLoadLableOneByOne(uploadS, it, getOneString)
                   }*/
            } else {
                getOneString.getString("耳标信息上传完成")
            }
        }

    }


    val executor by lazy {
        return@lazy EasyExecutor.newBuilder(5)
                .setName("Sample Executor")
                .setPriority(Thread.MAX_PRIORITY)
                .build()
    }

    var inten: Intent? = null
    private fun sendBroadCast(string: String) {
        inten = Intent()
        inten!!.putExtra(Constants.upLoad_desc, string)
        inten!!.action = "tramais.hnb.hhrfid.service.UpLoadSercie"
        sendBroadcast(inten)
    }

    override fun onDestroy() {
        super.onDestroy()
        // cancel()
        index_ = 0
        //  GlobalScope.cancel()
    }

}