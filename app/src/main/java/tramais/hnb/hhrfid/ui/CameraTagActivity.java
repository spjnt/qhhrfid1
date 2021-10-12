package tramais.hnb.hhrfid.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.LocationClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.base.BaseActivity;
import tramais.hnb.hhrfid.bean.FenPei;
import tramais.hnb.hhrfid.constant.Constants;
import tramais.hnb.hhrfid.listener.MyLocationListener;
import tramais.hnb.hhrfid.ui.view.CustomCameraView;
import tramais.hnb.hhrfid.util.BDLoactionUtil;
import tramais.hnb.hhrfid.util.FileUtil;
import tramais.hnb.hhrfid.util.ImageUtils;
import tramais.hnb.hhrfid.util.NetUtil;
import tramais.hnb.hhrfid.util.TimeUtil;

import static tramais.hnb.hhrfid.constant.Constants.sdk_first_path;


public class CameraTagActivity extends BaseActivity {

    public LocationClient mLocationClient = null;
    List<String> tips = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    List<Integer> maps = new ArrayList<>();
    int i = 1;
    int j = 0;
    private CustomCameraView cameraView;
    private ImageView imv_pic;
    private String mFilePath = null;
    private final String lblnumber = "";
    private double latitude;
    private double longitude;
    private Bitmap bitmap;
    private ImageButton btn_showcamera;
    private RelativeLayout bottom;
    private final Context context = CameraTagActivity.this;
    private TextView mImvGallery;
    private TextView mScanTotal;
    private TextView ivTips;
    private ImageView range;
    private TextView imv_gallery;
    private final ArrayList<String> bitmaps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_tag);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationClient != null)
            mLocationClient.stop();
        if (bitmaps != null) bitmaps.clear();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null)
            mLocationClient.stop();
        if (bitmaps != null) bitmaps.clear();
    }

    @Override
    protected void initView() {
        titles.add("养殖户照");
        titles.add("理赔员照");
        titles.add("右前方照");
        titles.add("右后方照");
        titles.add("割耳近照");
        titles.add("耳标近照");
        titles.add("身份证银行卡照");
        tips.add("养殖户站在动物身后");
        tips.add("理赔员站在动物后面");
        tips.add("人站在标的右前面一米左右距离");
        tips.add("人站在标的右后面一米左右距离");
        tips.add("割下耳朵放旁边,正面拍照");
        tips.add("露出耳标,拍清楚耳标上文字");
        tips.add("身份证和银行卡平放整齐");
        maps.add(R.mipmap.one);
        maps.add(R.mipmap.two);
        maps.add(R.mipmap.three);
        maps.add(R.mipmap.four);
        maps.add(R.mipmap.five);
        maps.add(R.mipmap.six);
        maps.add(R.mipmap.seven);

        btn_showcamera = findViewById(R.id.btn_showcamera);
        cameraView = findViewById(R.id.cc_camera);
        imv_pic = findViewById(R.id.imv_pic);
        mImvGallery = findViewById(R.id.imv_gallery);
        mScanTotal = findViewById(R.id.scan_total);
        ivTips = (TextView) findViewById(R.id.iv_tips);
        bottom = findViewById(R.id.bottom);
        bottom.bringToFront();
        range = (ImageView) findViewById(R.id.range);
        imv_gallery = findViewById(R.id.imv_gallery);
        upDateTips(0);
        ivTips.bringToFront();
    }

    @Override
    protected void initData() {

        if (NetUtil.checkNet(this)) {
            mLocationClient = new LocationClient(getApplicationContext());
            BDLoactionUtil.initLoaction(mLocationClient);
            //声明LocationClient类
            mLocationClient.registerLocationListener(new MyLocationListener(((lat, log, add) -> {
                latitude = lat;
                longitude = log;
            })));
        }
        Intent intent = getIntent();
        FenPei fenpei = (FenPei) intent.getSerializableExtra("fenpei");
        String earTag = intent.getStringExtra("earTag");
        setTitleText("投保人 " + fenpei.getFarmerName());
      //  CustomCameraView.PHOTO_FILE_NAME = FileUtil.getSDPath() + sdk_first_path;
      //  CustomCameraView.cameramode = "newfarmer";
        cameraView.setOnTakePictureInfo((isSuccess, file) -> {
            if (isSuccess) {
                mFilePath = file.getAbsolutePath();
                String address = "";
                if (fenpei != null) {

                    String riskAddress = fenpei.getRiskAddress();
                    if (!TextUtils.isEmpty(riskAddress)) {
                        address = riskAddress;
                    } else {
                        address = "经度:" + longitude + " 纬度:" + latitude;
                    }
                } else {
                    address = "经度:" + longitude + " 纬度:" + latitude;
                }
                bitmap = ImageUtils.drawTextToRightBottom(CameraTagActivity.this, mFilePath, "耳标号:" + earTag, "拍照类别:" + titles.get(j++), "地点:" + address);
                String path = ImageUtils.saveBitmap(CameraTagActivity.this, bitmap, FileUtil.getSDPath() + Constants.sdk_middle_animal + "/", TimeUtil.getTime(Constants.yyyyMMddHHmmss) + "_" + bitmaps.size() + ".jpg");
                imv_pic.setImageBitmap(bitmap);
                imv_pic.setVisibility(View.VISIBLE);
                if (!bitmaps.contains(path))
                    bitmaps.add(path);

                mScanTotal.bringToFront();

                if (bitmaps.size() == 7) {
                    goBack();
                } else {
                    upDateTips(i++);
                }
            }
        });


        if (mLocationClient != null)
            mLocationClient.start();

    }


    @Override
    protected void initListner() {
        btn_showcamera.setOnClickListener(view -> {
            takePhoto();
        });

        mImvGallery.setOnClickListener(v -> goBack());
        imv_gallery.setOnClickListener(v -> {
            if (bitmaps.size() > 0) {
                bitmaps.remove(bitmaps.size() - 1);
                upDateTips(i--);

            }
        });
    }

    private void goBack() {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("imgs", bitmaps);
        //设置返回数据
        setResult(RESULT_OK, intent);
        finish();

    }

    /**
     * 调用拍照功能
     */
    private void takePhoto() {
       // CustomCameraView.lblphotofilename = lblnumber;
        cameraView.takePicture();
        playSound(R.raw.camera_click);
//        SoundUtil.playSound(3);/*playSound(3);*/
    }


    /**
     * 发送广播，图库更新照片
     *
     * @param file 新增的图片
     */
    private void updatePhotos(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        sendBroadcast(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            takePhoto();

        }
        return super.onKeyDown(keyCode, event);
    }

    private void upDateTips(int i) {

        range.setBackgroundResource(maps.get(i));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mScanTotal.setText("当前第 " + (i + 1) + "/7" + " 张");
                setTitleText(titles.get(i));
                ivTips.setText(tips.get(i));
            }
        });


        /**/
    }
}
