package tramais.hnb.hhrfid.listener;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

import tramais.hnb.hhrfid.interfaces.GetBDLocation;

public class MyLocationListener extends BDAbstractLocationListener {
    GetBDLocation getBDLocation;

    public MyLocationListener(GetBDLocation getBDLocation) {
        this.getBDLocation = getBDLocation;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
        if (null != location && location.getLocType() != BDLocation.TypeServerError) {
            //获取信息后的操作
            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            String adcode = location.getAdCode();    //获取adcode

            String town = location.getTown();    //获取乡镇信息
            String add = district + town + street;
//            Log.e("地址:", latitude + ":" + longitude + ":" + add + ":" + addr);
            getBDLocation.getLoaction(latitude, longitude, addr);
        }
//        String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

//        int errorCode = location.getLocType();
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
    }


}