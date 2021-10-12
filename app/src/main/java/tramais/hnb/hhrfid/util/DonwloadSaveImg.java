package tramais.hnb.hhrfid.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import tramais.hnb.hhrfid.constant.Constants;

import static tramais.hnb.hhrfid.constant.Constants.MAIN_HEAG_BANNER;

/**
 * Created by YuShuangPing on 2018/12/12.
 */
public class DonwloadSaveImg {
    private final static String TAG = "PictureActivity";
    private static Context context;
    private static String filePath;
    private static String bannerName;
    private static Bitmap mBitmap;
    private static String mSaveMessage = "失败";
    //    private static ProgressDialog mSaveDialog = null;
    private static final Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };
    private static final Runnable saveFileRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (!TextUtils.isEmpty(filePath)) { //网络图片
                    // 对资源链接
                    URL url = new URL(filePath);
                    //打开输入流
                    InputStream inputStream = url.openStream();
                    //对网上资源进行下载转换位图图片
                    mBitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                }
                ImageUtils.saveBitmap(context, mBitmap, FileUtil.getSDPath() + Constants.sdk_qr, "qr" + ".jpg");
                mSaveMessage = "图片保存成功！";
            } catch (IOException e) {
                mSaveMessage = "图片保存失败！";
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageHandler.sendMessage(messageHandler.obtainMessage());
        }
    };

    public static void donwloadImg(Context contexts, String filePaths, String bannername) {
        context = contexts;
        filePath = filePaths;
        bannerName = bannername;
        new Thread(saveFileRunnable).start();
    }

}
