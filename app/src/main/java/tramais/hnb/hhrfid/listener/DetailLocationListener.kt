package tramais.hnb.hhrfid.listener

import android.util.Log
import tramais.hnb.hhrfid.interfaces.GetBDLocation
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation

class DetailLocationListener(var getBDLocation: GetBDLocation) : BDAbstractLocationListener() {
    override fun onReceiveLocation(location: BDLocation) {
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
        if (null != location && location.locType != BDLocation.TypeServerError) {
            //获取信息后的操作
            //获取纬度信息
            val latitude = location.latitude
            //获取经度信息
            val longitude = location.longitude
            val radius = location.radius //获取定位精度，默认值为0.0f
            val addr = location.addrStr //获取详细地址信息
            val country = location.country //获取国家
            val province = location.province //获取省份
            val city = location.city //获取城市
            val district = location.district //获取区县
            val street = location.street //获取街道信息
            val adcode = location.adCode //获取adcode
            val town = location.town //获取乡镇信息
            val add = district + town + street
         //  Log.e("地址:", "$latitude:$longitude:$add")
            getBDLocation.getLoaction(latitude, longitude, add)
        }
        //        String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

//        int errorCode = location.getLocType();
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
    }
}