/*
package tramais.hnb.hhrfid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baidu.location.LocationClient;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.base.BaseActivity;
import tramais.hnb.hhrfid.bean.FarmList;
import tramais.hnb.hhrfid.bean.FenPei;
import tramais.hnb.hhrfid.bean.TouBaoBean;
import tramais.hnb.hhrfid.constant.Constants;
import tramais.hnb.hhrfid.interfaces.GetRtnMessage;
import tramais.hnb.hhrfid.listener.MyLocationListener;
import tramais.hnb.hhrfid.litePalBean.BanAnInfo;
import tramais.hnb.hhrfid.litePalBean.RiskReasonCache;
import tramais.hnb.hhrfid.net.RequestUtil;
import tramais.hnb.hhrfid.net.SaveAllCache;
import tramais.hnb.hhrfid.ui.popu.PopuChoice;
import tramais.hnb.hhrfid.util.BDLoactionUtil;
import tramais.hnb.hhrfid.util.NetUtil;
import tramais.hnb.hhrfid.util.PreferUtils;
import tramais.hnb.hhrfid.util.TimeUtil;
import tramais.hnb.hhrfid.util.Utils;

public class ActivityBaoAn extends BaseActivity {
    public LocationClient mLocationClient = null;
    String zjNumber = "";
    List<String> reasons = new ArrayList<>();
    Map<String, String> reason_code = new HashMap<>();
    private TextView mBananNum;
    private TextView mTvBaoanName;
    private EditText mTvName;
    private TextView mTvChoiceName;
    private EditText mEtMobile;
    private TextView mTvBaoanDeal;
    private EditText mBananDanhao;
    private TextView mTvChoiceDeal;
    private EditText mChuxianAddress;
    private TextView mTvChuxianDate;
    private TextView mTvBaoanDate;
    private TextView mTvChuxianReason;
    private EditText mEtChuxianNum;
    private TextView mInsureUnit;
    private EditText mInputInfo;
    private Button mSaveFarm;
    private List<RiskReason> riskReasons;
    private EditText tvZjNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bao_an);
    }

    @Override
    protected void initView() {
        setTitleText("出险报案");

        mBananNum = findViewById(R.id.banan_num);
        mTvBaoanName = findViewById(R.id.tv_baoan_name);
        mTvName = findViewById(R.id.tv_name);
        mTvChoiceName = findViewById(R.id.tv_choice_name);
        mEtMobile = findViewById(R.id.et_mobile);
        mTvBaoanDeal = findViewById(R.id.tv_baoan_deal);
        mBananDanhao = findViewById(R.id.banan_danhao);
        mTvChoiceDeal = findViewById(R.id.tv_choice_deal);
        mChuxianAddress = findViewById(R.id.chuxian_address);
        mTvChuxianDate = findViewById(R.id.tv_chuxian_date);
        mTvBaoanDate = findViewById(R.id.tv_baoan_date);
        mTvChuxianReason = findViewById(R.id.tv_chuxian_reason);

        mEtChuxianNum = findViewById(R.id.et_chuxian_num);
        mInsureUnit = findViewById(R.id.insure_unit);
        mInputInfo = findViewById(R.id.input_info);
        mSaveFarm = findViewById(R.id.save_farm);
        tvZjNumber = findViewById(R.id.et_zjNumber);
        mTvChuxianDate.setText(TimeUtil.getTime(Constants.yyyy_mm_dd));
        mTvBaoanDate.setText(TimeUtil.getTime(Constants.yyyy_mm_dd));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NetUtil.checkNet(this)) {
            mLocationClient = new LocationClient(getApplicationContext());

            BDLoactionUtil.initLoaction(mLocationClient);
            mLocationClient.start();
            //声明LocationClient类
            mLocationClient.registerLocationListener(new MyLocationListener(((lat, log, add) -> {
                if (!TextUtils.isEmpty(add)) {
                    mChuxianAddress.setText(add);
                    mLocationClient.stop();
                }
            })));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null)
            mLocationClient.stop();
    }

    @Override
    protected void initData() {

        getInsureType();
        FenPei fenPei = (FenPei) getIntent().getSerializableExtra("FenPei");
        if (fenPei != null) {
            mBananNum.setText(fenPei.getNumber());
            mTvChoiceName.setText(fenPei.getFarmerName());
            mEtMobile.setText(fenPei.getMobile());
            mBananDanhao.setText(fenPei.getInsureNumber());
            zjNumber = fenPei.getFarmerNumber();
            tvZjNumber.setText(zjNumber);
            if (fenPei.getRiskReason().isEmpty()) {
                if (reasons != null && reasons.size() > 0)
                    mTvChuxianReason.setText(reasons.get(0));
            } else mTvChuxianReason.setText(fenPei.getRiskReason());

            mTvChuxianDate.setText(fenPei.getRiskDate());
            mEtChuxianNum.setText(fenPei.getRiskQty());
            mTvBaoanDate.setText(fenPei.getBaoAnDate());

        }
        FarmList farmList = (FarmList) getIntent().getSerializableExtra("underWrites");
        if (farmList != null) {
            mTvChoiceName.setText(farmList.getName());
            mEtMobile.setText(farmList.getMobile());
            zjNumber = farmList.getZjNumber();
            tvZjNumber.setText(zjNumber);
        }
    }

    @Override
    protected void initListner() {
        mTvChuxianDate.setOnClickListener(v -> {
            TimeUtil.initTimePicker(this, -1, -1, str -> {
                mTvChuxianDate.setText(str);
            });
        });
        mTvBaoanDate.setOnClickListener(v -> {
            TimeUtil.initTimePicker(this, -1, -1, str -> {
                mTvBaoanDate.setText(str);
            });
        });
        mSaveFarm.setOnClickListener(v -> {
            saveBaoAn();
        });
        mTvChuxianReason.setOnClickListener(v -> {
            if (reasons == null || reasons.size() == 0) {
                showStr("暂无出险原因选择");
                return;
            }
            new PopuChoice(ActivityBaoAn.this, mTvChuxianReason, "请选择出险原因", reasons, str -> {
                mTvChuxianReason.setText(str);
            });
        });
        mTvChoiceName.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityFarmList.class);
            intent.putExtra(Constants.MODULE_NAME, "出险报案");
            startActivityForResult(intent, 101);
        });
        mTvChoiceDeal.setOnClickListener(v -> {
            String name = mTvChoiceName.getText().toString();
            if (TextUtils.isEmpty(name)) {
                showStr("请输入报案人姓名");
                return;
            }
            Intent intent = new Intent(this, ActivityTouBaoList.class);
            intent.putExtra(Constants.MODULE_NAME, name);
            intent.putExtra(Constants.MAIN_HEAG_BG, tvZjNumber.getText().toString());
            startActivityForResult(intent, 102);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            FarmList farmList = (FarmList) data.getSerializableExtra("underWrites");
            mTvChoiceName.setText(farmList.getName());
            mEtMobile.setText(farmList.getMobile());
            zjNumber = farmList.getZjNumber();
            tvZjNumber.setText(zjNumber);
        } else if (requestCode == 102 && resultCode == 123 && data != null) {
            TouBaoBean farmList = (TouBaoBean) data.getSerializableExtra("underWrites");
            mBananDanhao.setText(farmList.getNumber());
            LitePal.where("number =?", farmList.getNumber()).findAsync(BanAnInfo.class).listen(list -> {
                if (list != null && list.size() > 0) {
                    BanAnInfo banAnInfo = list.get(0);
                    mBananNum.setText(banAnInfo.getInsureNumber());
                    mChuxianAddress.setText(banAnInfo.getInsureAddress());
                    mTvChuxianReason.setText(banAnInfo.getInsureReason());
                    mEtChuxianNum.setText(banAnInfo.getInsureNum());
                    mInputInfo.setText(banAnInfo.getInsureRemark());
                    mTvChuxianDate.setText(banAnInfo.getInsureTime());
                    mTvBaoanDate.setText(banAnInfo.getBaoAnTime());

                }
            });
        }
    }

    private void getInsureType() {
        if (riskReasons != null) riskReasons.clear();
        if (reasons != null) reasons.clear();
        if (reason_code != null) reason_code.clear();
        if (NetUtil.checkNet(this)) {
           */
/* RequestUtil.getInstance(this).getRiskReason("") (((rtnCode, message, totalNums, datas) -> {
                riskReasons = GsonUtil.getInstant().parseCommonUseArr(datas, RiskReason.class);
                if (riskReasons != null && riskReasons.size() > 0)
                    for (RiskReason item : riskReasons) {
                        String reasonName = item.getReasonName();
                        if (!TextUtils.isEmpty(reasonName) && !reasons.contains(reasonName))
                            reasons.add(reasonName);
                        reason_code.put(reasonName, item.getReasonCode());
                    }

            }));*//*

        } else {
            LitePal.findAllAsync(RiskReasonCache.class).listen(list -> {
                if (list != null && list.size() > 0)
                    for (RiskReasonCache item : list) {
                        String reasonName = item.getReasonName();
                        if (!TextUtils.isEmpty(reasonName) && !reasons.contains(reasonName))
                            reasons.add(reasonName);
                        reason_code.put(reasonName, item.getReasonCode());
                    }

            });
        }

    }

    private void saveBaoAn() {
        String tvName = Utils.getText(mTvChoiceName);
        if (TextUtils.isEmpty(tvName)) {
            showStr("请输入报案人姓名");
            return;
        }
        String mobile = Utils.getEdit(mEtMobile);
        if (TextUtils.isEmpty(mobile)) {
            showStr("请输入报案人电话");
            return;
        }
        String zjNumber_ = Utils.getEdit(tvZjNumber);
        if (TextUtils.isEmpty(zjNumber_)) {
            showStr("请输入报案人证件号");
            return;
        }
        String danHao = Utils.getEdit(mBananDanhao);
        if (TextUtils.isEmpty(danHao)) {
            showStr("请输入投保单号");
            return;
        }
        String address = Utils.getEdit(mChuxianAddress);
        if (TextUtils.isEmpty(address)) {
            showStr("请输入出险地址");
            return;
        }
        //mInsureNum
        String num = Utils.getEdit(mEtChuxianNum);
        if (TextUtils.isEmpty(num)) {
            showStr("请输入出险数量");
            return;
        }
        String userName = PreferUtils.getString(this, Constants.UserName);

        if (NetUtil.checkNet(this)) {
            showAvi();
            String time = TimeUtil.getTime(Constants.yyyy_mm_dd);
            RequestUtil.getInstance(this).SaveBaoAn(Utils.getText(mBananNum), zjNumber_, tvName, mobile, danHao, address, Utils.getText(mTvChuxianDate), Utils.getText(mTvBaoanDate),
                    reason_code.get(Utils.getText(mTvChuxianReason)), Utils.getText(mEtChuxianNum), Utils.getEdit(mInputInfo), getUserNum(), userName, userName, time, time, new GetRtnMessage() {
                        @Override
                        public void getMess(int rtnCode, String message) {
                            hideAvi();
                            if (!message.contains("异常"))
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showStr(message);
                                    }
                                });
                            else showStr(message);
                            mBananNum.setText(message);
                        }
                    });
        } else {

            //报案 ba  投保in
            SaveAllCache.SAVE_ALL_CACHE.saveBaoAn(danHao, zjNumber, tvName, mobile, Utils.getText(mBananNum), address, Utils.getText(mTvChuxianDate), Utils.getText(mTvBaoanDate), reason_code.get(Utils.getText(mTvChuxianReason)), Utils.getText(mEtChuxianNum),
                    Utils.getEdit(mInputInfo), getUserName(), getUserNum(), "已分配", (bill, message) -> {
                        mBananNum.setText(bill);
                        showStr(message);
                    });
            */
/*insureNumber: String?, companyNumber: String?, farmerNumber: String?, farmerName: String?, mobile: String?, number: String?, riskAddress: String?,
                        riskDate: String?, baoAnDate: String?, riskReason: String?, riskQty: String?, riskProcess: String?,  employeeNo: String?, employeeName: String?*//*

            SaveAllCache.SAVE_ALL_CACHE.saveBaoAnList(danHao, getCompanyNum(), zjNumber, tvName, mobile, Utils.getText(mBananNum), address, Utils.getText(mTvChuxianDate), Utils.getText(mTvBaoanDate), reason_code.get(Utils.getText(mTvChuxianReason)), Utils.getText(mEtChuxianNum),
                    Utils.getEdit(mInputInfo), getUserName(), getUserNum());
        }

    }
}
*/
