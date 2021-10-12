package tramais.hnb.hhrfid.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {

    public static boolean checkNet(Context context) {


        /**
         * 判断是否为WiFi连接
         */
        boolean isWIFI = isWIFIConnection(context);
        /**
         * 判断是否为移动网络连接
         */
        boolean isMobile = isMobileConnection(context);

        return isMobile || isWIFI;
    }

    private static boolean isMobileConnection(Context context) {
        //获取连接管理器
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取mobile连接
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }

    public static boolean isWIFIConnection(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取mobile连接
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

}
