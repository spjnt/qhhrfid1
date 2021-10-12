package tramais.hnb.hhrfid.ui.crop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.base.BaseActivity;
import tramais.hnb.hhrfid.bean.ChanKanDetailBean;
import tramais.hnb.hhrfid.bean.CropChaKanBean;
import tramais.hnb.hhrfid.bean.DingSunResultBean;
import tramais.hnb.hhrfid.constant.Constants;
import tramais.hnb.hhrfid.interfaces.GetCropChanDetail;
import tramais.hnb.hhrfid.lv.CropDingsunAdapter;
import tramais.hnb.hhrfid.net.RequestUtil;
import tramais.hnb.hhrfid.ui.view.ListViewForScrollView;
import tramais.hnb.hhrfid.util.GsonUtil;
import tramais.hnb.hhrfid.util.TimeUtil;
import tramais.hnb.hhrfid.util.Utils;

public class CropClaimAndLossDeter extends BaseActivity {
    private ListViewForScrollView mLv;
    private List<DingSunResultBean> dingSunInfoBeans;
    private Button mOnlySaveFarm;
    private TextView mBananNum;
    private TextView mTvBaoanName;
    private TextView mTvName;
    private TextView mEtMobile;
    private TextView mTvBaoanDeal;
    private TextView mBananDanhao;
    private TextView mChuxianAddress;
    private TextView mTvChuxianDate;
    private TextView mTvBaoanDate;
    private TextView mDingsunDate;
    private TextView mPublicDate, mInsureNumber;
    private Button mSaveFarm;
    private Button mSubmitCoreSystem;
    private TextView mSubmitResult;
    private String fNumber;
    private CropDingsunAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_claim_and_loss);
    }

    @Override
    protected void initView() {
        setTitleText("理赔定损");
        mOnlySaveFarm = findViewById(R.id.only_save_farm);
        mLv = findViewById(R.id.lv);
        mBananNum = findViewById(R.id.banan_num);
        mTvBaoanName = findViewById(R.id.tv_baoan_name);
        mTvName = findViewById(R.id.tv_name);
        mEtMobile = findViewById(R.id.et_mobile);
        mTvBaoanDeal = findViewById(R.id.tv_baoan_deal);
        mBananDanhao = findViewById(R.id.banan_danhao);
        mChuxianAddress = findViewById(R.id.chuxian_address);
        mTvChuxianDate = findViewById(R.id.tv_chuxian_date);
        mTvBaoanDate = findViewById(R.id.tv_baoan_date);
        mLv = findViewById(R.id.lv);
        mDingsunDate = findViewById(R.id.dingsun_date);
        mPublicDate = findViewById(R.id.public_date);
        mOnlySaveFarm = findViewById(R.id.only_save_farm);
        mSaveFarm = findViewById(R.id.save_farm);
        mSubmitCoreSystem = findViewById(R.id.submit_core_system);
        mSubmitResult = findViewById(R.id.submit_result);
        mDingsunDate.setText(TimeUtil.getTime(Constants.yyyy_mm_dd));
        mPublicDate.setText(TimeUtil.getTime(Constants.yyyy_mm_dd));
        mInsureNumber = findViewById(R.id.insure_num);
    }

    @Override
    protected void initData() {

        Intent intent = getIntent();
        if (intent != null) {
            CropChaKanBean cropChaKanBean = (CropChaKanBean) intent.getSerializableExtra("crop");
            fNumber = cropChaKanBean.getFNumber();
            getDingSun(fNumber);
            getDetail(fNumber);
        }


    }

    @Override
    protected void initListner() {
        mOnlySaveFarm.setOnClickListener(v -> {
            if (adapter != null) {
                List<DingSunResultBean> datasFinal = adapter.getDatasFinal();
                if (datasFinal == null || datasFinal.size() == 0) {
                    showStr("暂无定损信息上传");
                    return;
                }
                RequestUtil.getInstance(this).SaveDingSun(fNumber, Utils.getText(mDingsunDate), Utils.getText(mPublicDate), datasFinal, ((rtnCode, message) -> {
                    showStr(message);
                }));
            }

        });
        mDingsunDate.setOnClickListener(v -> {
            TimeUtil.initTimePicker(this, str -> {
                mDingsunDate.setText(str);
            });
        });
        mPublicDate.setOnClickListener(v -> {
            TimeUtil.initTimePicker(this, str -> {
                mPublicDate.setText(str);
            });
        });
    }

    private void getDingSun(String funmber) {
        RequestUtil.getInstance(this).GetDingSunlandResult(funmber, ((rtnCode, message, totalNums, datas) -> {
            if (datas == null || datas.size() == 0) return;
            dingSunInfoBeans = GsonUtil.getInstant().parseCommonUseArr(datas, DingSunResultBean.class);
            setAdapter(dingSunInfoBeans);
        }));
    }

    private void setAdapter(List<DingSunResultBean> dingSunInfoBeans) {
        if (dingSunInfoBeans == null || dingSunInfoBeans.size() == 0) return;
        mLv.setAdapter(adapter = new CropDingsunAdapter(this, dingSunInfoBeans));
    }

    private void getDetail(String number) {
        RequestUtil.getInstance(this).GetLandChaKanDet(number, new GetCropChanDetail() {
            @Override
            public void getBillDetail(ChanKanDetailBean chanKanDetailBean) {

                mBananNum.setText(chanKanDetailBean.getFNumber());
                mTvName.setText(chanKanDetailBean.getFFarmerName());
                mEtMobile.setText(chanKanDetailBean.getFRiskDate());
                mBananDanhao.setText(chanKanDetailBean.getFRiskAddress());
                mInsureNumber.setText(chanKanDetailBean.getFInsureNumber());
                mChuxianAddress.setText(chanKanDetailBean.getFRiskDate());
                mTvChuxianDate.setText(chanKanDetailBean.getFBaoAnDate());

            }
        });
    }

}
