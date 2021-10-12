/*
package tramais.hnb.hhrfid.ui.crop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baidu.location.LocationClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.base.BaseActivity;
import tramais.hnb.hhrfid.bean.ChanKanDetailBean;
import tramais.hnb.hhrfid.bean.EmployeeListBean;
import tramais.hnb.hhrfid.bean.LandInsureBillList;
import tramais.hnb.hhrfid.constant.Constants;
import tramais.hnb.hhrfid.interfaces.GetCropChanDetail;
import tramais.hnb.hhrfid.listener.MyLocationListener;
import tramais.hnb.hhrfid.net.RequestUtil;
import tramais.hnb.hhrfid.ui.popu.PopuChoice;
import tramais.hnb.hhrfid.util.BDLoactionUtil;
import tramais.hnb.hhrfid.util.GsonUtil;
import tramais.hnb.hhrfid.util.NetUtil;
import tramais.hnb.hhrfid.util.TimeUtil;
import tramais.hnb.hhrfid.util.Utils;

public class CropBaoAnActivity extends BaseActivity {

    public LocationClient mLocationClient = null;
    List<String> farmers = new ArrayList<>();
    HashMap<String, String> farmer_id = new HashMap<>();
    List<String> reasons = new ArrayList<>();
    Map<String, String> reason_code = new HashMap<>();
    String fLandCategoryID = "";
    private TextView mBananNum;
    private TextView mTvBaoanName;
    private EditText mTvName;
    private EditText mEtMobile;
    private TextView mTvBaoanDeal;
    private TextView mBananDanhao;
    private EditText mChuxianAddress;
    private TextView mTvInsureType;
    private TextView mTvChuxianDate;
    private TextView mTvBaoanDate;
    private TextView mTvChuxianReason;
    private TextView mTvEmployee;
    private EditText mInputInfo;
    private EditText mDamageQty;
    private Button mAddNewCheck;
    private String fFarmerNumber;
    private String fRiskAddress = "";
    private String f_number = "";
    private String land_category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_baoan);

    }

    @Override
    protected void initView() {
        setTitleText("新增报案");
        mBananNum = findViewById(R.id.banan_num);
        mTvBaoanName = findViewById(R.id.tv_baoan_name);
        mTvName = findViewById(R.id.tv_name);
        mEtMobile = findViewById(R.id.et_mobile);
        mTvBaoanDeal = findViewById(R.id.tv_baoan_deal);
        mBananDanhao = findViewById(R.id.banan_danhao);
        mChuxianAddress = findViewById(R.id.chuxian_address);
        mTvInsureType = findViewById(R.id.tv_insure_type);
        mTvChuxianDate = findViewById(R.id.tv_chuxian_date);
        mTvBaoanDate = findViewById(R.id.tv_baoan_date);
        mTvChuxianReason = findViewById(R.id.tv_chuxian_reason);
        mTvEmployee = findViewById(R.id.tv_employee);
        mInputInfo = findViewById(R.id.input_info);
        mDamageQty = findViewById(R.id.damage_qty);
        mTvChuxianDate.setText(TimeUtil.getTime(Constants.yyyy_mm_dd));
        mTvBaoanDate.setText(TimeUtil.getTime(Constants.yyyy_mm_dd));
        mAddNewCheck = findViewById(R.id.add_new_check);
    }

    private void getDetail(String number) {
       */
/* RequestUtil.getInstance(this).getLandBillDetail(number, new GetCropChengBaoDetail() {
            @Override
            public void getBillDetail(CropCheckChengBaoBean chanKanDetailBean) {
                fRiskAddress = chanKanDetailBean.getFLandAddress();
                mBananNum.setText(chanKanDetailBean.getFNumber());
                mTvName.setText(chanKanDetailBean.getFFarmerName());
                mEtMobile.setText(chanKanDetailBean.getFMobile());
                mBananDanhao.setText(chanKanDetailBean.getFInsureNumber());
                mTvInsureType.setText(chanKanDetailBean.getFLandCategory());
                mChuxianAddress.setText(fRiskAddress);
                mTvChuxianDate.setText(chanKanDetailBean.getFRiskDate());
                mTvBaoanDate.setText(chanKanDetailBean.getFBaoAnDate());
                mTvChuxianReason.setText(chanKanDetailBean.getFRiskReason());
                mDamageQty.setText(chanKanDetailBean.getFDamageQty());
                mTvEmployee.setText(chanKanDetailBean.getFEmployeeName());
                mInputInfo.setText(chanKanDetailBean.getFRiskProcess());
            }
        });*//*

        RequestUtil.getInstance(this).GetLandChaKanDet(number, new GetCropChanDetail() {
            @Override
            public void getBillDetail(ChanKanDetailBean chanKanDetailBean) {
                fRiskAddress = chanKanDetailBean.getFRiskAddress();
                mBananNum.setText(chanKanDetailBean.getFNumber());
                mTvName.setText(chanKanDetailBean.getFFarmerName());
                mEtMobile.setText(chanKanDetailBean.getFMobile());
                mBananDanhao.setText(chanKanDetailBean.getFInsureNumber());
                mTvInsureType.setText(chanKanDetailBean.getFLandCategory());
                mChuxianAddress.setText(fRiskAddress);
                mTvChuxianDate.setText(chanKanDetailBean.getFRiskDate());
                mTvBaoanDate.setText(chanKanDetailBean.getFBaoAnDate());
                mTvChuxianReason.setText(chanKanDetailBean.getFRiskReason());
                mDamageQty.setText(chanKanDetailBean.getFDamageQty());
                mTvEmployee.setText(chanKanDetailBean.getFEmployeeName());
                mInputInfo.setText(chanKanDetailBean.getFRiskProcess());
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            String bean = intent.getStringExtra("bean");
            if (!TextUtils.isEmpty(bean)) {
                getDetail(bean);
            }
        }
        getEmployeList();
        getInsureType();
    }

    private void getEmployeList() {
        RequestUtil.getInstance(this).getEmplyeList(((rtnCode, message, totalNums, datas) -> {
            if (datas != null && datas.size() > 0) {
                List<EmployeeListBean> employeeListBeans = GsonUtil.getInstant().parseCommonUseArr(datas, EmployeeListBean.class);
                for (EmployeeListBean item : employeeListBeans) {
                    String employeeName = item.getEmployeeName();
                    if (!TextUtils.isEmpty(employeeName) && !farmers.contains(employeeName)) {
                        farmer_id.put(employeeName, item.getEmployeeNo());
                        farmers.add(employeeName);
                    }

                }

            }
            if (farmers != null && farmers.size() > 0) mTvEmployee.setText(farmers.get(0));
        }));

    }

    private void getInsureType() {
        if (reasons != null) reasons.clear();
        if (reason_code != null) reason_code.clear();
        if (NetUtil.checkNet(this)) {
          */
/*  RequestUtil.getInstance(this).getRiskReason("") (((rtnCode, message, totalNums, datas) -> {
                List<RiskReason> riskReasons = GsonUtil.getInstant().parseCommonUseArr(datas, RiskReason.class);
                if (riskReasons != null && riskReasons.size() > 0)
                    for (RiskReason item : riskReasons) {
                        String reasonName = item.getReasonName();
                        if (!TextUtils.isEmpty(reasonName) && !reasons.contains(reasonName))
                            reasons.add(reasonName);
                        reason_code.put(reasonName, item.getReasonCode());
                    }
                if (reasons != null && reasons.size() > 0) mTvChuxianReason.setText(reasons.get(0));

            }

            ));
*//*

        }

    }

    @Override
    protected void initListner() {
        mBananDanhao.setOnClickListener(v -> {
            Intent intent = new Intent(this, CropBaoAnChoiceActivity.class);
            intent.putExtra(Constants.SO_WHAT, "ForResult");
            startActivityForResult(intent, 111);
        });
        mTvChuxianDate.setOnClickListener(v -> {
            TimeUtil.initTimePicker(this, str -> mTvChuxianDate.setText(str));
        });
        mTvBaoanDate.setOnClickListener(v -> {
            TimeUtil.initTimePicker(this, str -> mTvBaoanDate.setText(str));
        });
        mTvEmployee.setOnClickListener(v -> {
            if (farmers == null || farmers.size() == 0) {
                showStr("暂无员工选择");
                return;
            }
            new PopuChoice(this, mTvEmployee, "请选择员工", farmers, str -> {
                mTvEmployee.setText(str);
            });
        });
        mTvChuxianReason.setOnClickListener(v -> {
            if (reasons == null || reasons.size() == 0) {
                showStr("暂无出险原因选择");
                return;
            }
            new PopuChoice(this, mTvChuxianReason, "请选择出险原因", reasons, str -> {
                mTvChuxianReason.setText(str);
            });
        });
        mAddNewCheck.setOnClickListener(v -> {
            saveLandBaoAn();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK) {
            LandInsureBillList bean = (LandInsureBillList) data.getSerializableExtra("underWrites");
            fLandCategoryID = bean.getFLandCategoryID();
            f_number = bean.getFNumber();
            land_category = bean.getFLandCategory();
            fFarmerNumber = bean.getFFarmerNumber();
            mBananDanhao.setText(f_number);
            mTvInsureType.setText(land_category + "~" + fLandCategoryID);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mLocationClient != null)
            mLocationClient.start();
        if (NetUtil.checkNet(this) && !TextUtils.isEmpty(fRiskAddress)) {
            mLocationClient = new LocationClient(getApplicationContext());
            BDLoactionUtil.initLoaction(mLocationClient);
            //声明LocationClient类
            mLocationClient.registerLocationListener(new MyLocationListener(((lat, log, add) -> {
                if (!TextUtils.isEmpty(add)) {
                    mLocationClient.stop();
                    mChuxianAddress.setText(add);
                }
            })));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationClient != null)
            mLocationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null)
            mLocationClient.stop();
    }

    private void saveLandBaoAn() {
        String farm_name = Utils.getText(mTvName);
        if (TextUtils.isEmpty(farm_name)) {
            showStr("请输入投保人姓名");
            return;
        }
        String mobile = Utils.getText(mEtMobile);
        if (TextUtils.isEmpty(mobile)) {
            showStr("请输入投保人电话");
            return;
        }
        if (TextUtils.isEmpty(f_number)) {
            showStr("请选择保单号");
            return;
        }

        String damage_qty = Utils.getText(mDamageQty);
        if (TextUtils.isEmpty(damage_qty)) {
            showStr("请输入损失估计");
            return;
        }
        Utils.getEdit(mInputInfo);
        showAvi();
        RequestUtil.getInstance(this).saveLandBaoAn(Utils.getText(mBananNum), "", farm_name,
                mobile, f_number, Utils.getEdit(mChuxianAddress), Utils.getText(mTvChuxianDate), Utils.getText(mTvBaoanDate),
                reason_code.get(Utils.getText(mTvChuxianReason)), damage_qty, Utils.getEdit(mInputInfo), farmer_id.get(Utils.getText(mTvEmployee)), Utils.getText(mTvEmployee),
                getUserName(), TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss), getUserName(), TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss), fLandCategoryID, ((rtnCode, message) -> {
                    if (rtnCode >= 0) {
                        mBananNum.setText(message);
                    }

                    showStr(message);
                    hideAvi();
                }));
    }


}
*/
